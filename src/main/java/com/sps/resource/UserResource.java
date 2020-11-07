package com.sps.resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sps.exception.ExceptionHandling;
import com.sps.exception.domain.EmailExistException;

@RestController
@RequestMapping({"/user", "/"})
public class UserResource extends ExceptionHandling {

	@GetMapping("/home")
	public String showUser() throws EmailExistException {
		//return "Dattu";
		throw new EmailExistException("this email already in use!!!");
	}
}
 