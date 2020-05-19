package jroar.web.services;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;

import jroar.web.model.User;
import jroar.web.repositories.UserRepository;

@Service
public class LoginService {

	@Autowired
	private UserRepository userRepository;
	
	public void registerAdmin(User user, HttpServletRequest request) {
		
		user.getRoles().add("ROLE_USER");
		user.getRoles().add("ROLE_ADMIN");
		user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));

		userRepository.save(user);

		autoLogin(user, request);
		
	}
	
	public void autoLogin(User user, HttpServletRequest request) {

		List<GrantedAuthority> roles = new ArrayList<>();
		for (String role : user.getRoles()) {
			roles.add(new SimpleGrantedAuthority(role));
		}

		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
				user.getEmail(), user.getPassword(), roles);

		SecurityContextHolder.getContext().setAuthentication(authenticationToken);

		request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
				SecurityContextHolder.getContext());

	}
	
}
