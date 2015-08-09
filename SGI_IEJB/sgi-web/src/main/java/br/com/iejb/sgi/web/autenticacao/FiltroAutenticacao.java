package br.com.iejb.sgi.web.autenticacao;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * 
 * @author yesus
 * Responsável por validar a autenticação para todas as páginas que estão em admin
 *
 */
@WebFilter(urlPatterns="/pages/admin/*")
@Named
public class FiltroAutenticacao implements Filter {
	@Inject Identity identity;
	
	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		
		if(identity==null || identity.getUsuario()==null){
			((HttpServletResponse)response).sendRedirect(request.getServletContext().getContextPath().concat("/pages/login.xhtml"));
		}else{
			chain.doFilter(request, response);
		}
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}

}
