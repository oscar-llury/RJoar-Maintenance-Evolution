package jroar.web.controllers;

import jroar.web.model.InstallerInfo;
import jroar.web.model.MountRating;
import jroar.web.model.User;
import jroar.web.repositories.MountRatingRepository;
import jroar.web.repositories.UserRepository;
import jroar.web.services.InfoService;
import jroar.web.services.SessionService;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

	@Autowired
	private InfoService hService;

	@Autowired
	private SessionService sesion;
	
	@Autowired
	private InstallerInfo installerInfo;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private MountRatingRepository mountRatingRepository;
	
	@RequestMapping(value= {"home","index","/"})
	public String home(Model model, HttpServletRequest request) {
		if(!installerInfo.isInstall()) {
			return "emitir";
		}else {
			hService.addGlobalVariables(model,request);
			sesion.userLoader(model,request);
			return "index";
		}
	}
	
	@RequestMapping("/rate")
	public String rate(Model model, HttpServletRequest request, @RequestParam String name, @RequestParam String isLike) {
		if(request.isUserInRole("USER")) {
			User user = userRepository.findByEmail(request.getUserPrincipal().getName());
			List<MountRating> lr =mountRatingRepository.findByNameAndUserEmail(name,user.getEmail());
			MountRating r = null;
			if(lr.isEmpty()) {
				r = new MountRating(name,isLike.equals("like"),user);
			}else {
				r = lr.get(0);
				r.setIslike(isLike.equals("like"));
			}
			mountRatingRepository.save(r);
			
		}	
		return "redirect:/";
	}
	
}
