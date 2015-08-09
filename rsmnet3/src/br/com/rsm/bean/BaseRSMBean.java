package br.com.rsm.bean;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;

/**
 * Base de Bean do sistema com funcionalidades comuns a todos os componentes.
 * @author yesus
 *
 */
public class BaseRSMBean implements Serializable{

	private static final long serialVersionUID = 1L;

	public void message(String message){
		message("", message, FacesMessage.SEVERITY_INFO);
	}
	
	public void message(String message, Severity severity){
		message("", message, severity);
	}
	
	public void message(String title, String message){
		message(title, message, FacesMessage.SEVERITY_INFO);
	}
	
	public void message(String title, String message, Severity severity){
		 FacesContext.getCurrentInstance().addMessage(title, 
			        new FacesMessage(severity, message, null));
		
	}
}
