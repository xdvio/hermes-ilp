package org.interledger.spsp.server.controllers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import javax.servlet.http.HttpServletRequest;

public abstract class AbstractController {

  @Autowired
  private HttpServletRequest request;
  private Logger logger = LoggerFactory.getLogger(this.getClass());

  public String getAuthorization() {
    return request.getHeader("Authorization");
  }

  public DecodedJWT getJwt() {
    try {
      String bearerToken = getAuthorization();
      DecodedJWT jwt = JWT.decode(bearerToken.substring(bearerToken.indexOf(" ") + 1));
      if (jwt.getExpiresAt().before(new Date())) {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "JWT is expired");
      }
      return jwt;
    } catch (Exception e) {
      if (ResponseStatusException.class.isAssignableFrom(e.getClass())) {
        throw e;
      }

      logger.info("Could not decode bearer token as JWT, assuming it is SIMPLE token");
    }
    return null;
  }
}
