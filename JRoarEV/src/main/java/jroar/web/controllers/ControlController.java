package jroar.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ControlController {
	
	@RequestMapping("/control")
	public String control(Model model) {
		return "panelControl.html";
	}
	
	@RequestMapping("/control/mount")
	public String controlMount(Model model, @RequestParam String mountPoint, @RequestParam String source, @RequestParam String type,
			@RequestParam(required = false) String limit, @RequestParam String pass) {
			
		
		return "redirect:/home";
	}
	
	@RequestMapping("/control/drop")
	public String controlDrop(Model model, @RequestParam String playList, @RequestParam String pass) {
		
		return "redirect:/home";
	}
	
	@RequestMapping("/control/shout")
	public String controlShout(Model model, @RequestParam String playList, @RequestParam String ice, 
			@RequestParam String icePass, @RequestParam String pass) {
		
		return "redirect:/home";
	}

}
