package com.quartz.scheduler.exceptionhandler;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;

import com.quartz.scheduler.dto.ServerResponse;
import com.quartz.scheduler.util.SchedulerUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fasterxml.jackson.databind.node.ObjectNode;

@ControllerAdvice(basePackages = { "com.quartz.scheduler" })
@Slf4j
public class GlobalResponseEntityExceptionHandler extends ResponseEntityExceptionHandler{
	
	// error handle for @Valid
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status, WebRequest request) {

		log.error(ExceptionUtils.getStackTrace(ex));

        Map<String, ObjectNode> messageMap = new LinkedHashMap<>();
        //Get all errors
        ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .forEach(x->messageMap.put(x.getField(), createObjNode(x.getField(), x.getDefaultMessage()) ) );
        
        ObjectNode errorJsonNode = SchedulerUtil.createErrorJsonNode(status
        		, ((ServletWebRequest)request).getRequest().getRequestURI()
        		, messageMap);
        
        return new ResponseEntity<>(ServerResponse.builder()
											.data(errorJsonNode)
											.statusCode(status.value())
											.build(), headers, status);

    }
    
	@ExceptionHandler(EntityNotFoundException.class)
	protected ResponseEntity<Object> handleEntityNotFound(HttpServletRequest request, EntityNotFoundException ex) {

		String message = ex.getMessage();
		ObjectNode errorJsonNode = SchedulerUtil.createErrorJsonNode(HttpStatus.NOT_FOUND,
																	 request.getRequestURI(), message);

		return new ResponseEntity<>(ServerResponse.builder()
											.data(errorJsonNode)
											.statusCode(HttpStatus.NOT_FOUND.value())
											.build(), HttpStatus.NOT_FOUND);
	}

    private static ObjectNode createObjNode(String filed, String message) {
    	ObjectNode jsonNode = SchedulerUtil.createJsonNode();
    	jsonNode.put("field", filed);
    	jsonNode.put("message", message);
    	return jsonNode;
    }
}