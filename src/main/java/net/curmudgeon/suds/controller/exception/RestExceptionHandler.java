package net.curmudgeon.suds.controller.exception;

import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import net.curmudgeon.suds.controller.response.ErrorResponse;

/*
 * Copyright (C) 2022 Jay Rustine
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 * 
 */
@RestControllerAdvice
public class RestExceptionHandler {
	private static final Logger log = LogManager.getLogger(RestExceptionHandler.class);

	/**
	 * Handle missing record exceptions.
	 */
	@ExceptionHandler(MissingRecordException.class)
	public ResponseEntity<ErrorResponse> handleMissingRecordException(HttpServletRequest request, MissingRecordException e) {
		log.error("record with id [" + e.getId() + "] not found");
		return ResponseEntity
				.status(HttpStatus.NOT_FOUND)
				.body(new ErrorResponse(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(), "Missing record for " + e.getId(), request.getRequestURI()));
	}

	/**
	 * Handle all other exceptions.
	 */
	@ExceptionHandler(value = {Exception.class, RuntimeException.class})
	public ResponseEntity<ErrorResponse> handleDefaultExceptions(HttpServletRequest request, Exception e) {
		log.error("unhandled exception " + e.getMessage(), e);
		return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ErrorResponse(LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error", request.getRequestURI()));
	}

}
