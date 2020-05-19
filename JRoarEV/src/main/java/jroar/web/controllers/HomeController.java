package jroar.web.controllers;

import jroar.web.model.InstallerInfo;
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
	private InfoService hService;

	@Autowired
	private SessionService sesion;
	
	@Autowired
	private InstallerInfo installerInfo;
	
	@RequestMapping(value= {"home","index","/"})
	public String home(Model model, HttpServletRequest request) {
		if(!installerInfo.isInstall()) {
			return "emitir";
		}else {
			hService.addGlobalVariables(model);
			sesion.userLoader(model,request);
			return "index";
		}
	}
	
}
