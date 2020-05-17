package jroar.web.controllers;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jroar.code.com.jcraft.jroar.JRoar;

@Controller
public class InstallerController {
	
	@RequestMapping("/installer")
	public String installerHome(Model model) {
		return "emitir.html";
	}
	
	@RequestMapping("/install")
	public String install(Model model,
			@RequestParam(required = false) Integer isPort, @RequestParam(required = false) String inputPort,
			@RequestParam(required = false) Integer isAddress,
			@RequestParam(required = false) Integer isRelay, @RequestParam(required = false) String inputRelayMountPoint, @RequestParam(required = false) String inputSource,
			@RequestParam(required = false) Integer isPlaylist, @RequestParam(required = false) String inputPlaylistMountPoint, @RequestParam(required = false) String inputPlaylistFilename,
			@RequestParam(required = false) Integer isPage,
			@RequestParam(required = false) Integer isPass,  @RequestParam(required = false) String inputPass,
			@RequestParam(required = false) Integer isIcepass,
			@RequestParam(required = false) Integer isListener,
			@RequestParam(required = false) Integer isShout) {
		
			HashMap<String, List<String>> infoParams = new HashMap<>();
			
			if(isPort!=null&&isPort==1) {
				List<String> laux = new LinkedList<String>();
				laux.add(inputPort);
				infoParams.put("Port",laux);
			}
			
			if(isPlaylist!=null&&isPlaylist==1) {
				List<String> laux = new LinkedList<String>();
				laux.add(inputPlaylistMountPoint);
				laux.add(inputPlaylistFilename);
				infoParams.put("PlayList",laux);
			}
			
			if(isRelay!=null&&isRelay==1) {
				List<String> laux = new LinkedList<String>();
				laux.add(inputRelayMountPoint);
				laux.add(inputSource);
				infoParams.put("Relay",laux);
			}
			
			if(isPass!=null&&isPass==1) {
				List<String> laux = new LinkedList<String>();
				laux.add(inputPass);
				infoParams.put("Pass",laux);
			}
			
			
			JRoar.install(infoParams);
			
		return "emitir.html";
	}

}
