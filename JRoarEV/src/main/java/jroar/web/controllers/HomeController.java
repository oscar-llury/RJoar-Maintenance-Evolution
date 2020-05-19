package jroar.web.controllers;

import jroar.web.services.InfoService;
import jroar.web.services.SessionService;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

	@Autowired
	InfoService hService;

	@Autowired
	private SessionService sesion;
	
	@RequestMapping(value= {"home","index","/"})
	public String home(Model model, HttpServletRequest request) {
		hService.addGlobalVariables(model);//quitar
		sesion.userLoader(model,request);//quitar
		/*
		if(no hay emision) {
			return "emitir";
		}else {
			hService.addGlobalVariables(model);
			sesion.userLoader(model,request);
			return "index";
		}
		*/
		
		return "index";//quitar
	}
	
	//quitar todo
	@RequestMapping("/installer")
	public String installerHome(Model model, HttpServletRequest request) {
		sesion.userLoader(model,request);//quitar
		return "emitir";
	}
	
	@GetMapping("/error")
	public String login(Model model, HttpServletRequest request) {
		sesion.userLoader(model,request);
		return "error";
	}
}
