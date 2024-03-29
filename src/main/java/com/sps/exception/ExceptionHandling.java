package com.sps.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.sps.exception.domain.EmailExistException;
import com.sps.exception.domain.EmailNotFoundException;
import com.sps.exception.domain.UserNotFoundException;
import com.sps.exception.domain.UsernameExistException;
import com.sps.model.HttpResponse;

@RestControllerAdvice
public class ExceptionHandling implements ErrorController {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final Logger LOGGER2 = LoggerFactory.getLogger(getClass());
    private final Logger LOGGER4 = LoggerFactory.getLogger(getClass());

	private static final String ACCOUNT_LOCKED		      = "Your account has been locked, Please contact Administrator";
	
	private static final String METHOD_IS_NOT_ALLOWED 	  = "This request method is not allowed on this endpoint."
															+ " Please send a '%s' request";
    
	private static final String INTERNAL_SERVER_ERROR_MSG = "An error occurred while processing the request";
    
	private static final String INCORRECT_CREDENTIALS     = "Username / password incorrect. Please try again";
    
	private static final String ACCOUNT_DISABLED		  = "Your account has been disabled. "
															+ "If this is an error, please contact administration";
    
	private static final String ERROR_PROCESSING_FILE 	  = "Error occurred while processing file";
	
    private static final String NOT_ENOUGH_PERMISSION 	  = "You do not have enough permission";
    
    private static final String URL_MAPPING_NOT_AVAILABLE = "There is no mapping for this URL";
    
    public static final String ERROR_PATH 				  = "/error";
    
    @ExceptionHandler(DisabledException.class)
	public ResponseEntity<HttpResponse> accountDisabledException() {
		return createHttpResponse(BAD_REQUEST, ACCOUNT_DISABLED);
	}
 

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<HttpResponse> badCredentialsException() {
        return createHttpResponse(BAD_REQUEST, INCORRECT_CREDENTIALS);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<HttpResponse> accessDeniedException() {
        return createHttpResponse(FORBIDDEN, NOT_ENOUGH_PERMISSION);
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<HttpResponse> lockedException() {
        return createHttpResponse(UNAUTHORIZED, ACCOUNT_LOCKED);
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<HttpResponse> tokenExpiredException(TokenExpiredException exception) {
        return createHttpResponse(UNAUTHORIZED, exception.getMessage());
    }

    @ExceptionHandler(EmailExistException.class)
    public ResponseEntity<HttpResponse> emailExistException(EmailExistException exception) {
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(UsernameExistException.class)
    public ResponseEntity<HttpResponse> usernameExistException(UsernameExistException exception) {
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(EmailNotFoundException.class)
    public ResponseEntity<HttpResponse> emailNotFoundException(EmailNotFoundException exception) {
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<HttpResponse> userNotFoundException(UserNotFoundException exception) {
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }
    
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<HttpResponse> methodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
    	HttpMethod supportedMethod = Objects.requireNonNull(exception.getSupportedHttpMethods()).iterator().next();
        return createHttpResponse(METHOD_NOT_ALLOWED, String.format(METHOD_IS_NOT_ALLOWED, supportedMethod));
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<HttpResponse> internalServerErrorException(Exception exception) {
    	LOGGER.error(exception.getMessage());
    	return createHttpResponse(INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_MSG);
    }
 
	/* @ExceptionHandler(NoHandlerFoundException.class)
	public ResponseEntity<HttpResponse> noHandlerFoundException(NoHandlerFoundException e) {
		return createHttpResponse(BAD_REQUEST, "There is no mapping for this URL");
	} */
    
	private ResponseEntity<HttpResponse> createHttpResponse(HttpStatus httpStatus, String message) {
		HttpResponse httpResponse = new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase().toUpperCase(), message);
		return new ResponseEntity<>(httpResponse, httpStatus);
	}

	@RequestMapping(ERROR_PATH)
	private ResponseEntity<HttpResponse> notFound404() {
		return createHttpResponse(NOT_FOUND, URL_MAPPING_NOT_AVAILABLE);
	}

	@Override
	public String getErrorPath() {
		//return ERROR_PATH;
		return null;
	}

}
