package com.quartz.scheduler.exceptionhandler;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.quartz.scheduler.dto.ServerResponse;
import com.quartz.scheduler.util.SchedulerUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import com.fasterxml.jackson.databind.node.ObjectNode;

@ControllerAdvice(basePackages = { "com.quartz.scheduler" })
@Slf4j
public class GlobalExceptionHandler extends ExceptionHandlerExceptionResolver {

	@ExceptionHandler({ javax.validation.ConstraintViolationException.class,
			javax.validation.ValidationException.class,
			org.hibernate.exception.ConstraintViolationException.class,
			org.springframework.dao.DataIntegrityViolationException.class })
	public ResponseEntity<?> constraintViolationException(HttpServletResponse response) throws IOException {
		//response.sendError(HttpStatus.CONFLICT.value());
		return new ResponseEntity<>(ServerResponse.builder()
											.data(false)
											.statusCode(HttpStatus.CONFLICT.value())
											.build(), HttpStatus.CONFLICT);
	}

	@ExceptionHandler({ org.springframework.web.HttpMediaTypeNotSupportedException.class,
			HttpMessageNotReadableException.class })
	public ResponseEntity<?> handleControllerException(HttpMediaTypeNotSupportedException ex, WebRequest req) {

		String message = ExceptionUtils.getRootCauseMessage(ex);
		ObjectNode errorJsonNode = SchedulerUtil.createErrorJsonNode(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
																	 ((ServletWebRequest) req).getRequest().getRequestURI(), message);
		log.error(ExceptionUtils.getStackTrace(ex));
		return new ResponseEntity<>(ServerResponse.builder()
									.data(errorJsonNode)
									.statusCode(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())
									.build()
				, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
	}
	
	/**
     * Handles MissingServletRequestParameterExceptions from the rest controller.
     * 
     * @param ex MissingServletRequestParameterException
     * @return error response POJO
     */
    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    public ResponseEntity<?> handleMissingServletRequestParameterException( HttpServletRequest request, 
                                                                        MissingServletRequestParameterException ex) {
    	String message = ExceptionUtils.getRootCauseMessage(ex);
		ObjectNode errorJsonNode = SchedulerUtil.createErrorJsonNode(HttpStatus.BAD_REQUEST,
																	 request.getRequestURI(), message);

		log.error(ExceptionUtils.getStackTrace(ex));

		return new ResponseEntity<>(ServerResponse.builder()
											.data(errorJsonNode)
											.statusCode(HttpStatus.BAD_REQUEST.value())
											.build(), HttpStatus.BAD_REQUEST);
    }
    
}