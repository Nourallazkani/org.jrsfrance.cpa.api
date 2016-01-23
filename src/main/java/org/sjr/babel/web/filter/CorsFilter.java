package org.sjr.babel.web.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

@WebFilter(urlPatterns = "/*", asyncSupported = true)
public class CorsFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		response.addHeader("Access-Control-Allow-Origin", "*");
		if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
			response.addHeader("Access-Control-Allow-Methods", "POST, PUT, DELETE, GET");
			response.addHeader("Access-Control-Allow-Headers", "Content-type, XSRF-TOKEN");
		} else {
			filterChain.doFilter(request, response);
		}
	}
}
