package br.com.iejb.sgi.web.autenticacao;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import br.com.iejb.sgi.domain.Usuario;
import java.io.Serializable;

@Named
@SessionScoped
public class Identity implements Serializable {

	private static final long serialVersionUID = 6913256741129920208L;
	
	private Usuario usuario;

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	
}
