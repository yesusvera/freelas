package br.com.eudiamante.servlets;

import java.io.IOException;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.eudiamante.dao.UsuarioDAO;
import br.com.eudiamante.model.Usuario;
import br.com.eudiamante.util.UtilSession;

public class GerarConviteServlet extends HttpServlet {

	private static final long serialVersionUID = 120789158431318451L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String login = request.getPathInfo();
		
		System.out.println("usuarioGeraConvite -->" + login);
		if(login!=null){
			login = login.replace("/", "");
			Usuario usuarioConvidador = new UsuarioDAO().getUsuarioPorLogin(login);
			if(usuarioConvidador!=null){	
				request.getSession().setAttribute(UtilSession.USUARIO_CONVIDADOR, usuarioConvidador);
				System.out.println(usuarioConvidador);
				response.sendRedirect(request.getContextPath() + "/formEnviaConvite.xhtml");
			}else{
				FacesContext.getCurrentInstance().addMessage("mensagemServlet",
						new FacesMessage("Aconteceu um erro ao enviar o convite."));
				response.sendRedirect(request.getContextPath()
						+ "/retornoMensagem.xhtml");
			}
		}
	}		
}