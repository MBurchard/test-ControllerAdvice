# Test @ControllerAdvice

This is a small demo project showing that using @ControllerAdvice leads to additional warning logging of
`ExceptionHandlerExceptionResolver`.

```log
2022-02-14 11:33:58.395  INFO 4136 --- [nio-8080-exec-1] d.m.acme.CustomHandlerExceptionResolver  : Request: GET /test
		Useragent: 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:97.0) Gecko/20100101 Firefox/97.0'
		Referer: 
		Reason: just for test
2022-02-14 11:33:58.406  WARN 4136 --- [nio-8080-exec-1] .m.m.a.ExceptionHandlerExceptionResolver : Resolved [org.springframework.security.authentication.BadCredentialsException: just for test]

```
