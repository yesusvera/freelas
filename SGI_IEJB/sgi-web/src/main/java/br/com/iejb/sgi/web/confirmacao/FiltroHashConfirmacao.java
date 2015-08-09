package br.com.iejb.sgi.web.confirmacao;

import java.io.IOException;

import javax.ejb.EJB;
import javax.enterprise.context.BusyConversationException;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.iejb.sgi.domain.Membro;
import br.com.iejb.sgi.repository.MembroRepository;
import br.com.iejb.sgi.service.cadastrarMembro.CadastrarMembroServiceRemote;

/**
 * 
 * @author yesus
 * Responsável por validar a autenticação para todas as páginas que estão em admin
 *
 */
@WebFilter(urlPatterns="/validacaoCadastro/*")
@Named
public class FiltroHashConfirmacao implements Filter {
	
	@EJB private CadastrarMembroServiceRemote cadastrarMembroService;
	
	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		
		HttpServletResponse resp = (HttpServletResponse)response;
		
		HttpServletRequest req = (HttpServletRequest)request;
		
		String spl []= req.getRequestURI().split("/");
		String hashConfirmacao = spl[spl.length-1];
		
		Membro membro = cadastrarMembroService.buscarMembroPorHashConfirmacao(hashConfirmacao);
		
		if(membro!=null && membro.getId()!=null){
			membro.setAtivo(true);
			membro.getUsuario().setAtivo(true);
			
			cadastrarMembroService.salvarMembro(membro);
			
			resp.sendRedirect(req.getContextPath() + "/pages/publico/cadastroMembro/cadastroValidado");
		}else{
			resp.sendRedirect(req.getContextPath() + "/pages/publico/cadastroMembro/erroCadastroValidacao.xhtml");
		}
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}

}
