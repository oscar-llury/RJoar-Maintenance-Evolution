package jroar.web.controllers;

import jroar.web.model.StatInfo;
import jroar.web.repositories.StatsRepository;
import jroar.web.services.InfoService;
import jroar.web.services.SessionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

@Controller
public class StatsController {

	@Autowired
	private InfoService iService;
	
	@Autowired
	private SessionService sesion;
	
    @Autowired
    private StatsRepository statsRepository;

    @GetMapping("/stats")
    public String getStats(Model model, HttpServletRequest request) {
    	
        List<StatInfo> lStat = statsRepository.findAll();
        model.addAttribute("listaStat", lStat);
        sesion.userLoader(model,request);
        iService.addGlobalVariables(model,request);
        return "stats";
    }

}
