package br.com.eudiamante.util;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import br.com.eudiamante.model.Convite;

public class UtilSession {

	public static final String CONVITE = "convite";
	public static final String USUARIO_CONVIDADOR = "usuario_convidador";
	
	
	public static Convite getConviteSessao() {
		return (Convite) getSession().getAttribute(CONVITE);
	}

	public static HttpSession getSession() {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(false);
		return session;
	}
}
