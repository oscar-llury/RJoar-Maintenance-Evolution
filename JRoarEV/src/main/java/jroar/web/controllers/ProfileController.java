package jroar.web.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jroar.web.model.User;
import jroar.web.repositories.UserRepository;
import jroar.web.services.InfoService;
import jroar.web.services.SessionService;

@Controller
public class ProfileController {

	@Autowired
	private InfoService iService;
	
	@Autowired
	private SessionService sesion;
	
	@Autowired
	private UserRepository userRepository;
	
	@RequestMapping("/profile")
	public String profile(Model model, HttpServletRequest request) {

		sesion.userLoader(model,request);
		model.addAttribute("isAdmin", request.isUserInRole("ADMIN"));
		iService.addGlobalVariables(model,request);
		return "profile";
		
	}
	@RequestMapping("/profileEdit")
	public String profileEdit(Model model, 
			@RequestParam(required = false) String inputFirstName,
			@RequestParam(required = false) String inputLastName,
			HttpServletRequest request) {

		User userUpdate = userRepository.findByEmail(request.getUserPrincipal().getName());
		
		if(!inputFirstName.isEmpty()) {
			
			userUpdate.setFirstName(inputFirstName);
		}
		
		
		if(!inputLastName.isEmpty()) {

			userUpdate.setLastName(inputLastName);
		}
		
		if(!inputFirstName.isEmpty() || !inputLastName.isEmpty()) {
			userRepository.save(userUpdate);
		}
		
		sesion.userLoader(model,request);
		iService.addGlobalVariables(model,request);
		return "profile";
		
	}
}
