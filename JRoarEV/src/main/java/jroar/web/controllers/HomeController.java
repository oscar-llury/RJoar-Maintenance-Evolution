package jroar.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

@Controller
public class HomeController {
	
	public String home(Model model) {
		return "index.html";
	}

}
