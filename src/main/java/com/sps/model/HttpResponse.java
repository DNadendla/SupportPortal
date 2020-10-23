package com.sps.model;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public @Data class HttpResponse {
	
	private int httpStatusCode; //200, 201, 400, 404, 500
	private HttpStatus httpStatus;
	private String response;
	private String message;
	
}
