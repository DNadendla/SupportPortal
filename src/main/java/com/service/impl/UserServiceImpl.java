package com.service.impl;

import java.util.Date;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.repository.UserRepository;
import com.service.UserService;
import com.sps.model.User;
import com.sps.model.UserPrincipal;

@Service
@Transactional 
@Qualifier("userDetailsService")
public class UserServiceImpl implements UserService, UserDetailsService {

	private static Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findUserByUserName(username);

		if (user == null) {
			LOGGER.error("User not found by username: " + username);
			throw new UsernameNotFoundException("User not found by username: " + username);
		} else {
			user.setLastLoginDateDisplay(user.getLastLoginDate());
			user.setLastLoginDateDisplay(new Date());
			userRepository.save(user);

			UserPrincipal userPrincipal = new UserPrincipal(user);
			LOGGER.info("Returning user found by username: " + username);
			return userPrincipal;
		}
	}

}
