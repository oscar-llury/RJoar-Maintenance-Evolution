package jroar.web.controllers;

import jroar.web.services.InfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import jroar.code.com.jcraft.jroar.Drop;
import jroar.code.com.jcraft.jroar.Shout;
import jroar.code.com.jcraft.jroar.Mount;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ControlController {

	@Autowired
	InfoService iService;

	@RequestMapping("/control")
	public String control(Model model) {
		iService.addGlobalVariables(model);
		return "panelControl";
	}
	
	@RequestMapping("/control/mount")
	public String controlMount(Model model, @RequestParam String mountPoint, @RequestParam String source, @RequestParam String type,
			@RequestParam(required = false) String limit) {
		int limitInt = Integer.parseInt(limit);
		boolean livestream = type.equalsIgnoreCase("livestream");
		Mount.newKick(mountPoint, source, livestream, limitInt);
		return "redirect:/home";
	}
	
	@RequestMapping("/control/drop")
	public String controlDrop(Model model, @RequestParam String playList) {
		Drop.deleteMountPoint(playList);
		return "redirect:/home";
	}

	@RequestMapping("/control/dropall")
	public String controlDropAll(Model model) {
		Drop.deleteAll();
		return "redirect:/home";
	}
	
	@RequestMapping("/control/shout")
	public String controlShout(Model model, @RequestParam String playList, @RequestParam String ice, 
			@RequestParam String icePass) {
		Shout.newKick(playList, ice, icePass);
		return "redirect:/home";
	}

}