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
			@RequestParam(required = false) Integer isAddress, @RequestParam(required = false) String inputAddress,
			@RequestParam(required = false) Integer isRelay, @RequestParam(required = false) String inputRelayMountPoint, @RequestParam(required = false) String inputSource,
			@RequestParam(required = false) Integer isPlaylist, @RequestParam(required = false) String inputPlaylistMountPoint, @RequestParam(required = false) String inputPlaylistFilename,
			@RequestParam(required = false) Integer isPage, @RequestParam(required = false) String inputPageName, @RequestParam(required = false) String inputClassName,
			@RequestParam(required = false) Integer isStore, @RequestParam(required = false) String inputPageNameStore, @RequestParam(required = false) String inputURL,
			@RequestParam(required = false) Integer isPass,  @RequestParam(required = false) String inputPass,
			@RequestParam(required = false) Integer isIcepass, @RequestParam(required = false) String inputIcepass,
			@RequestParam(required = false) Integer isListener, @RequestParam(required = false) String inputListClass,
			@RequestParam(required = false) Integer isShout, @RequestParam(required = false) String inputSrcMount, @RequestParam(required = false) String inputDstIP,
			@RequestParam(required = false) String inputDstPort, @RequestParam(required = false) String inputDstPass, @RequestParam(required = false) String inputDstMount
			) {
		
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
			
			if(isAddress!=null&&isAddress==1) {
				List<String> laux = new LinkedList<String>();
				laux.add(inputAddress);
				infoParams.put("Address",laux);
			}
			
			if(isPage!=null&&isPage==1) {
				List<String> laux = new LinkedList<String>();
				laux.add(inputPageName);
				laux.add(inputClassName);
				infoParams.put("Page",laux);
			}
			
			if(isStore!=null&&isStore==1) {
				List<String> laux = new LinkedList<String>();
				laux.add(inputPageNameStore);
				laux.add(inputURL);
				infoParams.put("Store",laux);
			}
			
			if(isIcepass!=null&&isIcepass==1) {
				List<String> laux = new LinkedList<String>();
				laux.add(inputIcepass);
				infoParams.put("Icepass",laux);
			}
			
			if(isListener!=null&&isListener==1) {
				List<String> laux = new LinkedList<String>();
				laux.add(inputListClass);
				infoParams.put("Listener",laux);
			}
			
			if(isShout!=null&&isShout==1) {
				List<String> laux = new LinkedList<String>();
				laux.add(inputSrcMount);
				laux.add(inputDstIP);
				laux.add(inputDstPort);
				laux.add(inputDstPass);
				laux.add(inputDstMount);
				infoParams.put("Shout",laux);
			}
			
			
			
			
			JRoar.install(infoParams);
			
		return "redirect:/home";
	}

}
