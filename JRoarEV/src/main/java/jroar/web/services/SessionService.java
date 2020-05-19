package jroar.web.services;

import jroar.web.model.User;
import jroar.web.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import javax.servlet.http.HttpServletRequest;

@Service
public class SessionService {

	@Autowired
	private UserRepository userRepository;
	
	public Model userLoader(Model model, HttpServletRequest request) {
		
		if(request.isUserInRole("USER")) {
			User user = userRepository.findByEmail(request.getUserPrincipal().getName());
		
			model.addAttribute("logged",true);
			model.addAttribute("user",user);
		}else {
			model.addAttribute("logged",false);
		}
		
		if(request.isUserInRole("ADMIN")) {
			model.addAttribute("isAdmin",true);
		}else {
			model.addAttribute("isAdmin",false);
		}
		
		return model;
	}
	
}
