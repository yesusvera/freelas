package br.com.eudiamante.servlets;

import java.io.IOException;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.eudiamante.dao.ConviteDAO;
import br.com.eudiamante.model.Convite;
import br.com.eudiamante.util.UtilSession;

public class ConviteServlet extends HttpServlet {

	private static final long serialVersionUID = 120789158431318451L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String hashAcesso = request.getPathInfo();
		
		if(hashAcesso!=null){
			//PEGANDO O CONVITE DO BANCO E JOGANDO NA SESSAO
			request.getSession().setAttribute(UtilSession.CONVITE, null);
			hashAcesso = hashAcesso.replace("/", "");
			Convite conv = new ConviteDAO().getConvitePorHashAcesso(hashAcesso);
			
			//REDIRECIONAR PARA A PAGINA DE ONDE O CONVITE PAROU
			if(conv!=null && conv.getAtivo()){	
				request.getSession().setAttribute(UtilSession.CONVITE, conv);
				if(conv.getPaginaAtual()==null || conv.getPaginaAtual().equals("")){
					response.sendRedirect(request.getContextPath() + "/convite.xhtml");
				}else{
					response.sendRedirect(request.getContextPath() + "/" + conv.getPaginaAtual() + ".xhtml");
				}
			}else{
				FacesContext.getCurrentInstance().addMessage("mensagemServlet",
						new FacesMessage("Este convite está em formato inválido ou expirou, verifique se você acessou exatamente url que lhe foi enviada."));
				response.sendRedirect(request.getContextPath()
						+ "/retornoMensagem.xhtml");
			}
		}
	}		
}