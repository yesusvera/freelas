package br.com.iejb.sgi.service.autenticacao;

import javax.ejb.Remote;

import br.com.iejb.sgi.domain.Usuario;

@Remote
public interface AutenticacaoServiceRemote {
	
	public Usuario verificarLogin(Usuario usuario);
}
