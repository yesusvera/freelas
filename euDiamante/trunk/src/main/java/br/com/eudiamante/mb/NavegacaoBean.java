package br.com.eudiamante.mb;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import br.com.eudiamante.dao.ConviteDAO;
import br.com.eudiamante.model.Convite;
import br.com.eudiamante.model.Navegacao;
import br.com.eudiamante.util.UtilSession;

@SessionScoped
@ManagedBean(name = "navegacaoBean")
public class NavegacaoBean {

	public void gravarPaginaConvite(String paginaAtual) {
		try {
			ConviteDAO conviteDAO = new ConviteDAO();
			Convite convite = UtilSession.getConviteSessao();
			if (convite != null) {
				convite.setPaginaAtual(paginaAtual);
				conviteDAO.gravar(convite);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String irIdentificacao() {
		gravarPaginaConvite(Navegacao.IDENTIFICACAO);
		return Navegacao.IDENTIFICACAO;
	}

	public String irSala() {
		gravarPaginaConvite(Navegacao.SALA);
		return Navegacao.SALA;
	}

	public String irHome() {
		gravarPaginaConvite(Navegacao.HOME);
		return Navegacao.HOME;
	}

	public String irPagamento() {
		gravarPaginaConvite(Navegacao.PAGAMENTO);
		return Navegacao.PAGAMENTO;
	}

	public String irCadastro() {
		gravarPaginaConvite(Navegacao.CADASTRO);
		return Navegacao.CADASTRO;
	}
}
