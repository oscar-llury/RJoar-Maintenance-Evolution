package jroar.web.controllers;

import jroar.web.services.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

@Controller
public class HomeController {

	@Autowired
	HomeService hService;

	public String home(Model model) {
		hService.addGlobalVariables(model);
		return "home.html";
	}

}
