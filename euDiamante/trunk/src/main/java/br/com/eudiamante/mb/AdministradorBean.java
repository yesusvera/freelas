package br.com.eudiamante.mb;

import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import br.com.eudiamante.dao.ConviteDAO;
import br.com.eudiamante.dao.UsuarioDAO;
import br.com.eudiamante.model.Convite;
import br.com.eudiamante.model.Navegacao;
import br.com.eudiamante.model.Usuario;
import br.com.eudiamante.util.SendMail;
import br.com.eudiamante.util.Sh1Util;

@SessionScoped
@ManagedBean(name = "administradorBean")
public class AdministradorBean {

	ConviteDAO conviteDao = new ConviteDAO();
	UsuarioDAO usuarioDao = new UsuarioDAO();

	Usuario usuarioConvidador = new Usuario();

	private String de = "demo@eudiamante.com";
	private String para;
	private String nomeDestinatario;
	private String assunto = "f:nomeDestinatario voce precisa ver isto!";
	private String mensagem;
	

	private Convite convite = new Convite();

	public AdministradorBean() {
		mensagem = "<html><body>Olá f:nomeDestinatario, <br/><br/>"
				+ "Estou muito empolgado com essa oportunidade que acabei de conhecer!<br/> Gostaria da sua opinião pois acho que poderemos trabalhar juntos "
				+ " e ter ótimos resultados com esse negócio."
				+ "<br/>Clique no link abaixo para ver um apresentação rápida e entender melhor porque estou tão animado.<br/><br/>"
				+ "f:link" + "<br/><br/>" + "Falo com você em breve!"
				+ "<br/><br/>"
				+ "Att,<br/> <br/> f:nomeRemetente</body></html>";
	}

	public Convite getConvite() {
		return convite;
	}

	public void setConvite(Convite convite) {
		this.convite = convite;
	}

	public void atualizarConviteBanco() {
		getConvite().setPaginaAtual(Navegacao.CADASTRO);
		convite = conviteDao.gravar(convite);
	}

	public String getPara() {
		return para;
	}

	public void setPara(String para) {
		this.para = para;
	}

	public String getAssunto() {
		return assunto;
	}

	public void setAssunto(String assunto) {
		this.assunto = assunto;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	public String enviaMensagem() {
		usuarioConvidador = usuarioDao.buscarPorId(1L);

		if (usuarioConvidador != null) {
			Convite novoConvite = new Convite();
			novoConvite.setDataEnvioConvite(new Date());
			novoConvite.setUsuarioConvidador(usuarioConvidador);
			novoConvite.setNomeRazao(nomeDestinatario);
			novoConvite.setEmail(para);
			novoConvite.setAtivo(true);
			novoConvite.setHashValidacao(gerarHashValidacao(usuarioConvidador));
			novoConvite.setHashAcesso(gerarHashValidacao(usuarioConvidador));
			conviteDao.gravar(novoConvite);

			enviaEmailComLinkConfirmacao(novoConvite);
			FacesContext.getCurrentInstance()
					.addMessage(
							"mensagemServlet",
							new FacesMessage(
									"Convite enviado com sucesso para o email "
											+ para));
		} else {
			FacesContext.getCurrentInstance().addMessage("mensagemServlet",
					new FacesMessage("Aconteceu um erro ao enviar o convite"));
		}
		return "retornoMensagem";
	}
	
	public String funcaoNaoDisponivel(){
		FacesContext.getCurrentInstance()
		.addMessage(
				"mensagemServlet",
				new FacesMessage("Função não disponível na demo!"));
		
		return "retornoMensagem";
	}

	private String gerarHashValidacao(Usuario usuario) {
		StringBuffer hashConfirmacao = new StringBuffer();
		hashConfirmacao.append(usuario.getId());
		hashConfirmacao.append(getConvite().getEmailEnvioConvite());
		hashConfirmacao.append(new Date().getTime());
		hashConfirmacao.append(Math.random());

		return Sh1Util.criptografaSHA1(hashConfirmacao.toString());
	}

	private void enviaEmailComLinkConfirmacao(Convite novoConvite) {
		SendMail sendMail = new SendMail();
		String link = "http://";
		link += FacesContext.getCurrentInstance().getExternalContext().getRequestServerName();
		if(FacesContext.getCurrentInstance().getExternalContext().getRequestServerPort()!=80){
			link+=":"+FacesContext.getCurrentInstance().getExternalContext().getRequestServerPort();
		}
		link += "/euDiamante/convite/"
					+ novoConvite.getHashAcesso();
		
		mensagem = mensagem.replaceAll("f:nomeDestinatario",
				getNomeDestinatario());
		mensagem = mensagem.replaceAll("f:link", link);
		mensagem = mensagem.replaceAll("f:nomeRemetente",
				usuarioConvidador.getNomeRazao());
		sendMail.sendMail(
				de,
				para,
				assunto.replaceAll("f:nomeDestinatario", getNomeDestinatario()),
				mensagem.toString());
	}

	public String getDe() {
		return de;
	}

	public void setDe(String de) {
		this.de = de;
	}

	public String getNomeDestinatario() {
		return nomeDestinatario;
	}

	public void setNomeDestinatario(String nomeDestinatario) {
		this.nomeDestinatario = nomeDestinatario;
	}

}
