package br.com.iejb.sgi.web.autenticacao;

import java.io.Serializable;

import javax.inject.Named;

import br.com.iejb.sgi.domain.Usuario;

@Named
public class AutenticacaoView implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Usuario usuario = new Usuario(true);

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	
}
