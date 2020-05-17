package jroar.web.services;

import jroar.code.com.jcraft.jroar.HttpServer;
import jroar.code.com.jcraft.jroar.JRoar;
import jroar.web.model.InfoSource;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import jroar.code.com.jcraft.jroar.HomePage;
import java.util.List;

@Service
public class HomeService {

    public void addGlobalVariables(Model model) {
        model.addAttribute("jVersion", JRoar.version);
        model.addAttribute("URL", HttpServer.myURL);
        List<InfoSource> iSource = HomePage.newKick();
        model.addAttribute("sources", iSource);
    }

}
