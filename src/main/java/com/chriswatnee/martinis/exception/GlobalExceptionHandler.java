package com.chriswatnee.martinis.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public Object handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        logger.warn("Resource not found: {} [URI: {}]", ex.getMessage(), request.getRequestURI());

        if (isAjaxRequest(request)) {
            return new ResponseEntity<>("The requested resource was not found", HttpStatus.NOT_FOUND);
        }

        ModelAndView mav = new ModelAndView("error/404");
        mav.setStatus(HttpStatus.NOT_FOUND);
        return mav;
    }

    @ExceptionHandler(Exception.class)
    public Object handleGenericException(Exception ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        if (ex instanceof ErrorResponse errorResponse) {
            status = HttpStatus.valueOf(errorResponse.getStatusCode().value());
            logger.warn("Request error [URI: {}]: {}", request.getRequestURI(), ex.getMessage());
        } else {
            logger.error("Unexpected error processing request [URI: {}]", request.getRequestURI(), ex);
        }

        if (isAjaxRequest(request)) {
            return new ResponseEntity<>("An error occurred", status);
        }

        String view = status.is4xxClientError() ? "error/404" : "error/500";
        ModelAndView mav = new ModelAndView(view);
        mav.setStatus(status);
        return mav;
    }

    private boolean isAjaxRequest(HttpServletRequest request) {
        String requestedWith = request.getHeader("X-Requested-With");
        String accept = request.getHeader("Accept");
        String hxRequest = request.getHeader("HX-Request");
        return "XMLHttpRequest".equals(requestedWith)
                || (accept != null && accept.contains("application/json"))
                || "true".equals(hxRequest);
    }
}
