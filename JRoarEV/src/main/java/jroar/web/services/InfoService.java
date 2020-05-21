package jroar.web.services;

import jroar.code.com.jcraft.jroar.HttpServer;
import jroar.code.com.jcraft.jroar.JRoar;
import jroar.web.model.InfoSource;
import jroar.web.model.User;
import jroar.web.repositories.MountRatingRepository;
import jroar.web.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import jroar.code.com.jcraft.jroar.HomePage;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

@Service
public class InfoService {

	@Autowired
	private MountRatingRepository mountRatingRepository;
	
	@Autowired
	private UserRepository userRepository;
	
    public void addGlobalVariables(Model model,HttpServletRequest request) {
        model.addAttribute("jVersion", JRoar.version);
        model.addAttribute("URL", HttpServer.myURL);
        List<InfoSource> iSource = HomePage.newKick();
        for(InfoSource i:iSource) {
        	i.setLikes(mountRatingRepository.countLikes(i.getMountpoint()));
        	i.setDislikes(mountRatingRepository.countDisLikes(i.getMountpoint()));
        	i.setCanRate(request.isUserInRole("USER"));
        }
        model.addAttribute("sources", iSource);
    }

}
