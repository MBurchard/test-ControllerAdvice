package de.mbur.acme

import mu.KotlinLogging
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

private val log = KotlinLogging.logger {}

@Component
@ControllerAdvice
class CustomHandlerExceptionResolver {

  fun getFullURl(req: HttpServletRequest) =
    "${req.requestURI}${if (req.queryString.orEmpty() != "") "?${req.queryString}" else ""}"

  @ExceptionHandler(BadCredentialsException::class)
  fun handleBadCredentialsException(
    ex: BadCredentialsException,
    req: HttpServletRequest,
    resp: HttpServletResponse
  ): ResponseEntity<*> {
    log.info {
      "Request: ${req.method} ${getFullURl(req)}\n" +
        "\t\tUseragent: '${req.getHeader(HttpHeaders.USER_AGENT).orEmpty()}'\n" +
        "\t\tReferer: ${req.getHeader(HttpHeaders.REFERER).orEmpty()}\n" +
        "\t\tReason: ${ex.message}"
    }
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.message)
  }
}
