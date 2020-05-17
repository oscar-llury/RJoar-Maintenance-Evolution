package jroar.web.controllers;

import jroar.web.services.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

	@Autowired
	HomeService hService;

	@RequestMapping("/home")
	public String home(Model model) {
		hService.addGlobalVariables(model);
		return "home";
	}

}
