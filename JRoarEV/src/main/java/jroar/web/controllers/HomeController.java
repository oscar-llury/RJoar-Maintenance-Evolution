package jroar.web.controllers;

import jroar.web.model.Comment;
import jroar.web.model.InstallerInfo;
import jroar.web.model.MountRating;
import jroar.web.model.StatInfo;
import jroar.web.model.User;
import jroar.web.repositories.CommentRepository;
import jroar.web.repositories.MountRatingRepository;
import jroar.web.repositories.StatsRepository;
import jroar.web.repositories.UserRepository;
import jroar.web.services.InfoService;
import jroar.web.services.SessionService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

	@Autowired
	private InfoService iService;

	@Autowired
	private SessionService sesion;
	
	@Autowired
	private InstallerInfo installerInfo;
	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private StatsRepository statsRepository;

	@Autowired
	private MountRatingRepository mountRatingRepository;
	
	@Autowired
	private CommentRepository commentRepository;
	
	@RequestMapping(value= {"home","index","/"})
	public String home(Model model, HttpServletRequest request) {
		if(!installerInfo.isInstall()) {
			iService.addGlobalVariables(model,request);
			sesion.userLoader(model,request);
			return "emitir";
		}else {
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
			LocalDateTime now = LocalDateTime.now();
			String s = dtf.format(now);
			int contVisitas = 1;
			StatInfo stat = statsRepository.findByDate(s);
			if (stat != null) {
				int cont = stat.getVisits();
				contVisitas = cont+1;
				stat.setVisits(contVisitas);
				statsRepository.save(stat);
			} else {
				StatInfo nuevaStat = new StatInfo(s, 1);
				statsRepository.save(nuevaStat);
			}
			model.addAttribute("visitasHoy", contVisitas);
			model.addAttribute("visitasTotales", iService.visitasTotales());
			
			iService.addGlobalVariables(model,request);
			sesion.userLoader(model,request);
			return "index";
		}
	}
	
	@RequestMapping("/rate")
	public String rate(Model model, HttpServletRequest request, @RequestParam String name, @RequestParam String isLike) {
		if(request.isUserInRole("USER")) {
			User user = userRepository.findByEmail(request.getUserPrincipal().getName());
			List<MountRating> lr =mountRatingRepository.findByNameAndUserEmail(name,user.getEmail());
			MountRating r = null;
			if(lr.isEmpty()) {
				r = new MountRating(name,isLike.equals("like"),user);
			}else {
				r = lr.get(0);
				r.setIslike(isLike.equals("like"));
			}
			mountRatingRepository.save(r);
			
		}	
		return "redirect:/";
	}
	
	@RequestMapping("/comment")
	public String comment(Model model, HttpServletRequest request, @RequestParam(required = false) String text, @RequestParam String mountPoint) {
		if(request.isUserInRole("USER")&&text!=null&&!text.equals("")) {
			User user = userRepository.findByEmail(request.getUserPrincipal().getName());
			Comment c =  new Comment(mountPoint,text,user,user.getFirstName());
			commentRepository.save(c);
		}	
		return "redirect:/";
	}
	
}
