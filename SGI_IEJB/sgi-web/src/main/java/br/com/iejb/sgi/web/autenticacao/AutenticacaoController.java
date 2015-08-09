package br.com.iejb.sgi.web.autenticacao;

import javax.ejb.EJB;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.bean.ManagedBean;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import br.com.iejb.sgi.domain.Usuario;
import br.com.iejb.sgi.service.autenticacao.AutenticacaoServiceRemote;
import br.com.iejb.sgi.web.util.BaseAction;

@ManagedBean
@ConversationScoped
public class AutenticacaoController extends BaseAction {

	private static final long serialVersionUID = 1L;

	@Inject Conversation conversation;
	
	@Inject private AutenticacaoView view;
	
	@Inject private Identity identity;
	
	@EJB private AutenticacaoServiceRemote autenticacaoServiceRemote;
	
	
	public String autentica(){
		Usuario usr = autenticacaoServiceRemote.verificarLogin(view.getUsuario());
		
		if(usr==null){
			addMensagemInformativa("Erro ao efetuar o login. Por favor verifique o usuário e senha digitados.");
			identity.setUsuario(null);
			return "";
		}else{
			identity.setUsuario(usr);
			return "/pages/admin/index.xhtml?faces-redirect=true";
		}
		
	}
	
	public String sair(){
		HttpServletRequest request = (HttpServletRequest) getContext().getCurrentInstance().getExternalContext().getRequest();
		request.getSession().invalidate();
		return "/pages/login.xhtml?faces-redirect=true";
	}
	
	public AutenticacaoView getView() {
		return view;
	}

	public void setView(AutenticacaoView view) {
		this.view = view;
	}
}
