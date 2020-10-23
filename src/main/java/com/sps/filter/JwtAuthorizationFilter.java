package com.sps.filter;

import static com.sps.constant.SecurityConstant.OPTIONS_HTTP_METHOD;
import static com.sps.constant.SecurityConstant.TOKEN_PREFIX;

import java.io.IOException;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.sps.utility.JWTTokenProvider;

//To check whether request is coming with valid token or not
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

	//@Autowired  
	private JWTTokenProvider jwtTokenProvider; 

	public JwtAuthorizationFilter(JWTTokenProvider jwtTokenProvider) {
		this.jwtTokenProvider = jwtTokenProvider;
	} 

	// Checks is the token/user are valid
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {   

		if (request.getMethod().equals(OPTIONS_HTTP_METHOD)) {
			// Options request is send before every request to make sure/gather info
			// regarding server
			response.setStatus(HttpStatus.OK.value());
		} else {
			String authizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
			if (authizationHeader == null || !authizationHeader.startsWith(TOKEN_PREFIX)) {
				filterChain.doFilter(request, response);
				return;
			}

			String token = authizationHeader.substring(TOKEN_PREFIX.length());
			String username = jwtTokenProvider.getSubject(token);
			boolean isTokenValid = jwtTokenProvider.isTokenValid(username, token);

			if (isTokenValid && SecurityContextHolder.getContext().getAuthentication() == null) {
				List<GrantedAuthority> authorities = jwtTokenProvider.getAuthorities(token);
				Authentication authentication = jwtTokenProvider.getAuthentication(username, authorities, request);
				SecurityContextHolder.getContext().setAuthentication(authentication);
			} else {
				SecurityContextHolder.clearContext();
			}

			filterChain.doFilter(request, response);
		}
	}

}
