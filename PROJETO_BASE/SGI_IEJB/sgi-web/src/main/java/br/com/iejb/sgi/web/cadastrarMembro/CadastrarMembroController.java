package br.com.iejb.sgi.web.cadastrarMembro;

import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.iejb.sgi.domain.Membro;
import br.com.iejb.sgi.service.cadastrarMembro.CadastrarMembroServiceRemote;
import br.com.iejb.sgi.web.util.BaseAction;

@Named
@ConversationScoped
public class CadastrarMembroController extends BaseAction {

	private static final long serialVersionUID = 1L;

	@Inject Conversation conversation;
	
	@Inject private CadastrarMembroView view;
	
	@EJB private CadastrarMembroServiceRemote cadastrarMembroService;
	
	
	public void salvarMembro(){
		cadastrarMembroService.salvarMembro(view.getMembro());
		addMensagemInformativa("Membro salvo com sucesso");
	}
	
	public List<Membro> listarMembros(){
		view.setListaMembros(cadastrarMembroService.listarMembros());
		return view.getListaMembros();
	}
	
	public CadastrarMembroView getView() {
		return view;
	}

	public void setView(CadastrarMembroView view) {
		this.view = view;
	}
}
