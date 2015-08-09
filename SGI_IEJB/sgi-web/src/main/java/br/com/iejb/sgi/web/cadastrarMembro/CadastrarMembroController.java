package br.com.iejb.sgi.web.cadastrarMembro;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.Conversation;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import br.com.iejb.sgi.domain.Filho;
import br.com.iejb.sgi.domain.Membro;
import br.com.iejb.sgi.domain.Usuario;
import br.com.iejb.sgi.service.TabelasDominioServiceRemote;
import br.com.iejb.sgi.service.cadastrarMembro.CadastrarMembroServiceRemote;
import br.com.iejb.sgi.web.autenticacao.Identity;
import br.com.iejb.sgi.web.util.BaseAction;
import br.com.iejb.sgi.web.util.ComboUtil;


@ManagedBean
@ViewScoped
public class CadastrarMembroController extends BaseAction {

	private static final long serialVersionUID = 1L;

	@Inject Conversation conversation;
	
	@Inject private CadastrarMembroView view;
	
	@EJB private CadastrarMembroServiceRemote cadastrarMembroService;
	
	@EJB private TabelasDominioServiceRemote tabelasDominioService;
	
	@Inject Identity identity;
	
	@PostConstruct
	public void init(){
		view.setListaUF(ComboUtil.converterListaObjParaSelectItem(tabelasDominioService.listarUFs()));
		view.getMembro().setUsuario(new Usuario(false));
		view.getMembro().setAtivo(false);
		view.getMembro().setFilhos(new ArrayList<Filho>());
		if(view.getFilho()==null){
			view.setFilho(new Filho());
		}
		
		if(identity!=null && identity.getUsuario()!=null && identity.getUsuario().getMembro()!=null){
			view.setMembro(identity.getUsuario().getMembro());
		}
	}
	
	public String salvarMembro(){
		if(!view.getMembro().getUsuario().getSenha().equals(view.getSenhaConfirmacao())){
			addMensagemErro("As senhas não conferem!");
			return "";
		}else{
			cadastrarMembroService.salvarMembro(view.getMembro());
			return "/pages/publico/cadastroMembro/confirmacaoCadastro";	
		}
	}

	public void carregarCidades(){
		view.setListaCidade(ComboUtil.converterListaObjParaSelectItem(view.getUf().getCidades()));
	}
	
	public List<Membro> listarMembros(){
		view.setListaMembros(cadastrarMembroService.listarMembros());
		return view.getListaMembros();
	}
	
	public List<Filho> listarFilhos(){
		return view.getMembro().getFilhos();
	}
	
	public void adicionarFilho(){
		view.getMembro().getFilhos().add(view.getFilho());
	}
	
	public CadastrarMembroView getView() {
		return view;
	}

	public void setView(CadastrarMembroView view) {
		this.view = view;
	}
}
