package de.mbur.acme

import mu.KotlinLogging
import org.springframework.boot.info.BuildProperties
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import java.net.InetAddress
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

private val log = KotlinLogging.logger {}

@Controller
class DefaultController(
  private val buildProperties: BuildProperties,
) {

  @GetMapping("/knock")
  fun whoIsKnocking(model: Model, req: HttpServletRequest, resp: HttpServletResponse): String {
    log.debug { "Someone is at the door" }
    model.addAttribute("version", buildProperties.version)
    model.addAttribute("ip", req.remoteAddr.orEmpty())
    model.addAttribute("service-ip", InetAddress.getLocalHost().hostAddress.orEmpty())
    model.addAttribute("method", req.method)
    model.addAttribute("path", req.requestURI.orEmpty())
    model.addAttribute("query", req.queryString.orEmpty())
    val headers = mutableListOf<AbstractMap.SimpleEntry<String, String>>()
    req.headerNames.iterator().forEach {
      headers.add(AbstractMap.SimpleEntry(it, req.getHeader(it).orEmpty()))
    }
    model.addAttribute("headers", headers)
    return "knock"
  }

  @GetMapping("/test")
  fun testControllerAdvice() {
    throw BadCredentialsException("just for test")
  }
}
