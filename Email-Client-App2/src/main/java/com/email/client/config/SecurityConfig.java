package com.email.client.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	private JwtFilter jwtFilter;
	
	private PasswordEncoder passwordEncoder;
	
	private UserDetailsService userDetailsService;
	
	private JwtAuthEntryPoint jwtAuthEntryPoint;
	
	
	@Autowired
	public SecurityConfig(JwtFilter jwtFilter, PasswordEncoder passwordEncoder, UserDetailsService userDetailsService,JwtAuthEntryPoint jwtAuthEntryPoint) {
		this.jwtFilter = jwtFilter;
		this.passwordEncoder = passwordEncoder;
		this.userDetailsService = userDetailsService;
		this.jwtAuthEntryPoint = jwtAuthEntryPoint;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		
		//disable csrf protection
		http
		.cors(Customizer.withDefaults())
		.csrf().disable()
		.exceptionHandling(ex -> 
				ex.authenticationEntryPoint(jwtAuthEntryPoint))
		
		.authorizeHttpRequests(auth -> auth.requestMatchers("/auth/**").permitAll()
				.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
										
				//ROLE Based endpoints.
				.requestMatchers("/email/**").hasAuthority("ROLE_USER")
										
				//ALL other endpoints needs authentication.
				.anyRequest().authenticated())
		
		//Setting the SessionManagement as STATELESS
		.sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
		
		//Set the Dao Authentication Provider.
		.authenticationProvider(authenticationProvider())
		
		//add jwt filter before UsernamePasswordAuthenticationFIlter
		.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
		
		return http.build();
	}

	@Bean
	public AuthenticationProvider authenticationProvider() {
		// TODO Auto-generated method stub
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(userDetailsService);
		authenticationProvider.setPasswordEncoder(passwordEncoder);
		return authenticationProvider;
	}
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}
	
}
