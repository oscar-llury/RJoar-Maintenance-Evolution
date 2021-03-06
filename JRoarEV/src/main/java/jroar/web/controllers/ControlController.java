package jroar.web.controllers;

import jroar.code.com.jcraft.jroar.Ctrl;
import jroar.web.model.Comment;
import jroar.web.model.MountRating;
import jroar.web.repositories.CommentRepository;
import jroar.web.repositories.MountRatingRepository;
import jroar.web.services.InfoService;
import jroar.web.services.SessionService;

import javax.servlet.http.HttpServletRequest;

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
	private InfoService iService;

	@Autowired
	private SessionService sesion;
	
	@Autowired
	private MountRatingRepository mountRatingRepository;
	
	@Autowired
	private CommentRepository commentRepository;
	
	@RequestMapping("/control")
	public String control(Model model, HttpServletRequest request) {
		
		
		sesion.userLoader(model,request);
		model.addAttribute("clientList", Ctrl.getClients());
		model.addAttribute("isAdmin", request.isUserInRole("ADMIN"));
		iService.addGlobalVariables(model,request);
		return "panelControl";
	}
	
	@RequestMapping("/control/mount")
	public String controlMount(Model model,@RequestParam String inputMountEmissionType, @RequestParam String mountPoint, @RequestParam String source, @RequestParam String type,
			@RequestParam(required = false) String limit) {
		int limitInt = 0;
		if(limit!=null&&!limit.equals(""))
			limitInt = Integer.parseInt(limit);
		boolean livestream = type.equalsIgnoreCase("livestream");
		Mount.newKick(mountPoint, source, livestream, limitInt,inputMountEmissionType.equals("video"));
		return "redirect:/home";
	}
	
	@RequestMapping("/control/drop")
	public String controlDrop(Model model, @RequestParam String playList) {
		Drop.deleteMountPoint(playList);
		//Borrar los ratings asociados al punto de montura que se va a eliminar
		for(MountRating r : mountRatingRepository.findByName(playList)) {
			mountRatingRepository.delete(r);
		}
		//Borrar los comentarios asociados al punto de montura que se va a eliminar
		for(Comment c : commentRepository.findByMountPoint(playList)) {
			commentRepository.delete(c);
		}
		return "redirect:/home";
	}

	@RequestMapping("/control/dropall")
	public String controlDropAll(Model model) {
		Drop.deleteAll();
		//Borrar todos los ratings al borrarse todos los puntos de montura
		for(MountRating r : mountRatingRepository.findAll()) {
			mountRatingRepository.delete(r);
		}
		//Borrar todos los comentarios al borrarse todos los puntos de montura
		for(Comment c : commentRepository.findAll()) {
			commentRepository.delete(c);
		}
		return "redirect:/home";
	}
	
	@RequestMapping("/control/shout")
	public String controlShout(Model model, @RequestParam String playList, @RequestParam String ice, 
			@RequestParam String icePass) {
		Shout.newKick(playList, ice, icePass);
		return "redirect:/home";
	}

}
