package jroar.web.controllers;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jroar.code.com.jcraft.jroar.JRoar;
import jroar.web.model.InstallerInfo;
import jroar.web.model.User;
import jroar.web.services.LoginService;

@Controller
public class InstallerController {

	@Autowired
	private InstallerInfo installerInfo;
	
	@Autowired
	private LoginService loginService;
	
	@RequestMapping("/install")
	public String install(Model model, User user, HttpServletRequest request,
			@RequestParam(required = false) Integer isPort, @RequestParam(required = false) String inputPort,
			@RequestParam(required = false) Integer isAddress, @RequestParam(required = false) String inputAddress,
			@RequestParam(required = false) Integer isRelay, @RequestParam(required = false) String inputRelayMountPoint, 
			@RequestParam(required = false) String inputSource, @RequestParam(required = false) String inputLimitRelay,
			@RequestParam(required = false) Integer isPlaylist, @RequestParam(required = false) String inputPlaylistMountPoint,
			@RequestParam(required = false) String inputPlaylistFilename,@RequestParam(required = false) String inputLimitPlay,
			@RequestParam(required = false) Integer isPass,  @RequestParam(required = false) String inputPass,
			@RequestParam(required = false) Integer isIcepass, @RequestParam(required = false) String inputIcepass,
			@RequestParam(required = false) Integer isShout, @RequestParam(required = false) String inputSrcMount, @RequestParam(required = false) String inputDstIP,
			@RequestParam(required = false) String inputDstPort, @RequestParam(required = false) String inputDstPass, @RequestParam(required = false) String inputDstMount
			) {
		
		//Se impide que se vuelva a instalar JRoar una vez instalado
		if(!installerInfo.isInstall()) {
			
			//Se registra un usuario admin y se inicia sesión con esa cuenta
			loginService.registerAdmin(user, request);
			
			HashMap<String, List<String>> infoParams = new HashMap<>();
			
			if(isPort!=null&&isPort==1&&!inputPort.equals("")) {
				List<String> laux = new LinkedList<String>();
				laux.add(inputPort);
				infoParams.put("Port",laux);
			}
			
			if(isPlaylist!=null&&isPlaylist==1&&!inputPlaylistMountPoint.equals("")&&!inputPlaylistFilename.equals("")) {
				List<String> laux = new LinkedList<String>();
				laux.add(inputPlaylistMountPoint);
				laux.add(inputPlaylistFilename);
				if(inputLimitPlay!=null&&!inputLimitPlay.equals("")) {
					laux.add(inputLimitPlay);
				}
				infoParams.put("PlayList",laux);
			}
			
			if(isRelay!=null&&isRelay==1&&!inputRelayMountPoint.equals("")&&!inputSource.equals("")) {
				List<String> laux = new LinkedList<String>();
				laux.add(inputRelayMountPoint);
				laux.add(inputSource);
				if(inputLimitRelay!=null&&!inputLimitRelay.equals("")) {
					laux.add(inputLimitRelay);
				}
				infoParams.put("Relay",laux);
			}
			
			if(isPass!=null&&isPass==1&&!inputPass.equals("")) {
				List<String> laux = new LinkedList<String>();
				laux.add(inputPass);
				infoParams.put("Pass",laux);
			}
			
			if(isAddress!=null&&isAddress==1&&!inputAddress.equals("")) {
				List<String> laux = new LinkedList<String>();
				laux.add(inputAddress);
				infoParams.put("Address",laux);
			}
			
			if(isIcepass!=null&&isIcepass==1&&!inputIcepass.equals("")) {
				List<String> laux = new LinkedList<String>();
				laux.add(inputIcepass);
				infoParams.put("Icepass",laux);
			}
			
			
			if(isShout!=null&&isShout==1&&!inputSrcMount.equals("")&&!inputDstIP.equals("")
					&&!inputDstPort.equals("")&&!inputDstPass.equals("")&&!inputDstMount.equals("")) {
				List<String> laux = new LinkedList<String>();
				laux.add(inputSrcMount);
				laux.add(inputDstIP);
				laux.add(inputDstPort);
				laux.add(inputDstPass);
				laux.add(inputDstMount);
				infoParams.put("Shout",laux);
			}
			
			JRoar.install(infoParams);
			
			//Se impide volver al instalador
			installerInfo.setInstall(true);
			
		}
		return "redirect:/home";
	}

}
