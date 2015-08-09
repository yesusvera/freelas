package br.com.eudiamante.mb;

import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import br.com.eudiamante.dao.ConviteDAO;
import br.com.eudiamante.dao.UsuarioDAO;
import br.com.eudiamante.model.Convite;
import br.com.eudiamante.model.Navegacao;
import br.com.eudiamante.model.Usuario;
import br.com.eudiamante.util.UtilSession;

@RequestScoped
@ManagedBean(name = "cadastroBean")
public class CadastroBean {

	ConviteDAO conviteDao = new ConviteDAO();
	UsuarioDAO usuarioDao = new UsuarioDAO();

	Usuario usuarioConvidador = new Usuario();

	private Convite convite = new Convite();

	public CadastroBean() {
		if(UtilSession.getConviteSessao()!=null){
			convite = conviteDao.buscarPorId(UtilSession.getConviteSessao().getId());
		}
	}

	public Convite getConvite() {
		return convite;
	}

	public void setConvite(Convite convite) {
		UtilSession.getSession().setAttribute("convite", convite);
	}

	public void atualizarConviteBanco() {
		getConvite().setPaginaAtual(Navegacao.CADASTRO);
		convite = conviteDao.gravar(convite);
	}
	
	public void atualizaConvite(){
		convite = conviteDao.buscarPorId(UtilSession.getConviteSessao().getId());
	}
	
	public Date getMinDataNascimento(){
		return new Date(1930, 1,1);
	}
	
	public Date getMaxDataNascimento(){
		return new Date();
	}



}
