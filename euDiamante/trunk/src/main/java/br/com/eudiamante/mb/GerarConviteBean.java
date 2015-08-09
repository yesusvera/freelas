package br.com.eudiamante.mb;

import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import br.com.eudiamante.dao.ConviteDAO;
import br.com.eudiamante.model.Convite;
import br.com.eudiamante.model.Usuario;
import br.com.eudiamante.util.SendMail;
import br.com.eudiamante.util.Sh1Util;
import br.com.eudiamante.util.UtilSession;

@RequestScoped
@ManagedBean(name = "gerarConviteBean")
public class GerarConviteBean {

	private Convite convite = new Convite();
	private ConviteDAO conviteDao = new ConviteDAO();

	private Usuario usuarioConvidador = (Usuario) UtilSession.getSession()
			.getAttribute(UtilSession.USUARIO_CONVIDADOR);

	public String enviaConvite() {

		if (usuarioConvidador != null) {
			convite.setDataEnvioConvite(new Date());
			convite.setUsuarioConvidador(usuarioConvidador);
			convite.setHashValidacao(gerarHashValidacao());
			convite.setAtivo(false);
			conviteDao.gravar(convite);

			enviaEmailComLinkConfirmacao();

			FacesContext.getCurrentInstance().addMessage("mensagemServlet",
					new FacesMessage("Um convite foi enviado para seu email, por favor clique no link de confirmação. Obrigado"));
		} else {
			FacesContext.getCurrentInstance().addMessage("mensagemServlet",
					new FacesMessage("Aconteceu um erro ao enviar o convite"));
		}
		
		return "retornoMensagem";
	}

	private void enviaEmailComLinkConfirmacao() {
		SendMail sendMail = new SendMail();
		String link = "";
		if (FacesContext.getCurrentInstance().getExternalContext()
				.getRequestServerName().contains("localhost")) {
			link = "http://localhost:8080/euDiamante/validarConvite/"
					+ convite.getHashValidacao();
		}
		StringBuffer mensagem = new StringBuffer();
		mensagem.append("<html><body>");
		mensagem.append("<img src='imagemEuDiamante'/><br>");
		mensagem.append("Prezado, você gerou um convite para participar do euDiamante.com");
		mensagem.append("<br> Por favor,");
		mensagem.append("<a href='").append(link).append("'>clique aqui</a>");
		mensagem.append(" para poder ativar seu convite. </body></html>");

		sendMail.sendMail("eudiamante2013@gmail.com",
				convite.getEmailEnvioConvite(), "Convite do euDiamante.com", mensagem.toString());
	}

	private String gerarHashValidacao() {
		StringBuffer hashConfirmacao = new StringBuffer();
		hashConfirmacao.append(usuarioConvidador.getId());
		hashConfirmacao.append(convite.getEmailEnvioConvite());
		hashConfirmacao.append(new Date().getTime());
		hashConfirmacao.append(Math.random());

		return Sh1Util.criptografaSHA1(hashConfirmacao.toString());
	}

	public Convite getConvite() {
		return convite;
	}

	public void setConvite(Convite convite) {
		this.convite = convite;
	}

	public Usuario getUsuarioConvidador() {
		return usuarioConvidador;
	}

	public void setUsuarioConvidador(Usuario usuarioConvidador) {
		this.usuarioConvidador = usuarioConvidador;
	}

}
