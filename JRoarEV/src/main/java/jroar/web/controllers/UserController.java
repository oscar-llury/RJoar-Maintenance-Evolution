package jroar.web.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import jroar.web.model.User;
import jroar.web.repositories.UserRepository;
import jroar.web.services.LoginService;
import jroar.web.services.SessionService;

@Controller
public class UserController {
	
	@Autowired
	private UserRepository userRepository; 
	
	@Autowired
	private SessionService sesion;
	
	@Autowired
	private LoginService loginService;

	@GetMapping("/login")
	public String login(Model model, HttpServletRequest request) {
		model.addAttribute("isError",false);
		sesion.userLoader(model,request);
		return "login";
	}
	
	@GetMapping("/loginerror")
	public String loginError(Model model) {
		model.addAttribute("isError",true);
		return "login";
	}
	
	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/home";
	}
	
	@GetMapping("/register")
	public String registerPage(Model model, HttpServletRequest request) {
		model.addAttribute("emailExist", false);
		sesion.userLoader(model,request);
		return "register";
	}
	
	@PostMapping("/register")
	public String register(Model model, User user, HttpServletRequest request, HttpSession session) {

		User registeredUser = userRepository.findByEmail(user.getEmail());

		if (registeredUser != null) {
			model.addAttribute("emailExist", true);
			return "register";
		}

		user.getRoles().add("ROLE_USER");
		user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));

		userRepository.save(user);

		loginService.autoLogin(user, request);

		return "redirect:/home";
	}


}
