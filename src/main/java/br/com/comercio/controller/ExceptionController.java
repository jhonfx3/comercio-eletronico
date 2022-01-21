package br.com.comercio.controller;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class ExceptionController {

//	@ExceptionHandler(Exception.class)
	public ModelAndView resolveGlobalException(HttpServletRequest request) {
		System.out.println("resolveGlobalException sendo chamada...");
		Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
		ModelAndView view = new ModelAndView("error");
		//view.addObject("status", Integer.valueOf(status.toString()));
		return view;
	}

	/*
	 * // implements HandlerExceptionResolver
	 * 
	 * @Override public ModelAndView resolveException(HttpServletRequest request,
	 * HttpServletResponse response, Object handler, Exception ex) {
	 * System.out.println("ENTROU AQUI"); return new ModelAndView("home"); }
	 * 
	 */

}
