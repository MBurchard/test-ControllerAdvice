# Spring Boot Kotlin Starter

## The Beginning
The Tag `v1` is more or less what https://start.spring.io/ generates for you.

I have chosen some dependencies that are quite useful for each project.

- **Spring Boot DevTools**: Provides fast application restarts, LiveReload, and configurations for enhanced development experience.
- **Spring Configuration Processor**: Generate metadata for developers to offer contextual help and "code completion" when working with custom configuration keys (ex.application.properties/.yml files).
- **Spring Web**: Build web, including RESTful, applications using Spring MVC. Uses Apache Tomcat as the default embedded container.
- **Spring Security**: Highly customizable authentication and access-control framework for Spring applications.
- **Spring Boot Actuator**: Supports built in (or custom) endpoints that let you monitor and manage your application - such as application health, metrics, sessions, etc.

Due to the use of `org.springframework.boot:spring-boot-starter-security` the application will show a login  page when
opening it in the browser.  
After inserting **user** as username and the generated security password from the console the application show the
**Whitelabel Error Page** because no controller has been created so fare.

## Prepare the Project
The Tag `v2` shows some files and settings that I use basically for every project.

### Editorconfig
This file is recognized by lots of IDEs and editors and leads to unified layout for every team member.

### application.yml
That is a question of taste. I don't like the properties files and prefer to use YAML.

It contains default settings for Spring Actuator. Now you can try the following URLs with and without authorisation:

- http://localhost:8080/actuator/health
- http://localhost:8080/actuator/health/readiness
- http://localhost:8080/actuator/health/liveness

The `application.yml` also contains typical settings for the web server, like compression, HTTP2 and port.

At least I prefer to have `test` as the default profile when running everything.

### gradle.properties
This file will contain some configuration later. For now, it just enables parallel builds.

## Dependencies, or how to stay up to date
Projects live long, often much longer than you think.

The `v3` changes are required in order to easily detect whether dependencies have new versions during this time and
then easily adapt them if necessary.

### Gradle Versions Plugin
This [plugin][versin-plugin] from Ben Manes is very useful and in my projects for a long time now.

The lines 45 - 65 in the `build.gradle.kts` show my default configuration for the plugin.

The usage of the Gradle task `dependencyUpdates` is straight forward.

[versin-plugin]: https://github.com/ben-manes/gradle-versions-plugin

### build.gradle.kts
Other changes in that file are also simple. The `javaVersion` variable is included to avoid having two positions where it
needs to be changed in case.

The Gradle wrapper task is helpful to easily change the used gradle wrapper. This can also be done via command line, but
I prefer it the clicky way.

### settings.gradle.kts
The additions in this file are responsible for the plugin versions used in the whole project. This is not only helpful
when using the defined version from the `gradle.properties` but also in a multi module project.

## v4: Who is at the door?
It's often interesting to know who's knocking at the door and where you've just arrived.  
For this purpose, I use the method `whoIsKnocking` in the `DefaultController` and the Mustache template `knock.mustache`
in my applications.  
But first things first...

### Logging
Most developers want to know what their application is doing. Kotlin logging is recommended for that.

So you need the dependency in the `build.gradle.kts` line 13 and 41 and the version in `gradle.properties` line 9.

The logger will be imported above the class definition as on can see in `AcmeApp.kt` line 9.

Logging itself will be done using a lambda expression.

```kotlin
log.info { "Version: ${buildProperties.version}" }
```

The good thing is, you don't need to worry about the computing costs of that logging. Because of its lambda nature it is
not calculated if the loglevel isn't info. So you don't need to deal with placeholders like known from SLF4J, simply use
the variables where you want them.

For debugging you need to set the loglevel in the `application.yml`.
```yaml
logging:
  level:
    de.mbur.acme: debug
```

### Spring Boot Build Info
One can provide information from the build at runtime.  
Add the following lines to your `build.gradle.kts`:

```kotlin
springBoot {
  buildInfo()
}
```

And use this like every other Spring bean like shown in the `AcmeApp.kt`.

### A bit of security configuration
Since it is needed anyway, we introduce the `WebSecurityConfiguration` here, which inherits from
`WebSecurityConfigurerAdapter`.  
The annotations used are typical. The Spring documentation reveals more about this.

The function `configure(http: HttpSecurity)` has to be overridden.  
To be able to use the handy `HttpSecurityDsl` you need this import:

```kotlin
import org.springframework.security.config.web.servlet.invoke
```

```kotlin
  override fun configure(http: HttpSecurity) {
    http {
      authorizeRequests {
        authorize("/knock", permitAll)
        authorize(anyRequest, authenticated)
      }
      exceptionHandling { authenticationEntryPoint = HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED) }
    }
  }
```

For simplicity, we allow access to the `/knock` path. Everything else only gets authenticated access.

Spring responds with status 403 if one is not authenticated, so we add the line with `exceptionHandling`, now Spring
responds with status 401 which has quite a different meaning.
