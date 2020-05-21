package jroar.web.controllers;

import jroar.web.model.StatInfo;
import jroar.web.repositories.StatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class StatsController {

    @Autowired
    private StatsRepository statsRepository;

    @GetMapping("/stats")
    public String getStats(Model model) {
        List<StatInfo> lStat = statsRepository.findAll();
        model.addAttribute("listaStat", lStat);
        return "stats";
    }

}
