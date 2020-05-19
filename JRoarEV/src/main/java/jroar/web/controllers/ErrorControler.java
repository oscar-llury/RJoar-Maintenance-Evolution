package jroar.web.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jroar.web.services.SessionService;

@Controller
public class ErrorControler implements ErrorController{
	
	@Autowired
	private SessionService sesion;

	@GetMapping("/error")
	public String handle(Model model, HttpServletRequest request) {
		int httpErrorCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
		sesion.userLoader(model,request);
		selectError(model, httpErrorCode);
		return "error";
	}

	private void selectError(Model model, int httpErrorCode) {

		switch (httpErrorCode) {
			case 404: {
				addErrorToModel(model, httpErrorCode, "No se puede encontrar la página solicitada en el servidor", "Page not found");
				break;
			}
			case 500: {
				addErrorToModel(model, httpErrorCode, "Error interno en el servidor de JRoar",
					"Internal server error");
				break;
			}
			case 405: {
				addErrorToModel(model, httpErrorCode, "La página no puede mostrarse porque se ha empleado un método no permitido.",
					"Invalid method error");
				break;
			}
			case 403: {
				addErrorToModel(model, httpErrorCode, "No tiene permiso para acceder a esta página.", "Forbidden");
				break;
			}
			default: {
				addErrorToModel(model, httpErrorCode, "Se ha producido un error desconocido.", "Unknown error");
			}
		}
	}

	private void addErrorToModel(Model model, int httpErrorCode, String msg, String errorTitle) {
		model.addAttribute("title", "Error " + httpErrorCode + ": " + errorTitle);
		model.addAttribute("msg", msg);
	}

	@Override
	public String getErrorPath() {
		return "/error";
	}

}
