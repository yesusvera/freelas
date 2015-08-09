package br.com.eudiamante.mb;

import java.util.LinkedList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import br.com.eudiamante.model.Usuario;
import br.com.eudiamante.util.GeradorNomeUtil;

@RequestScoped
@ManagedBean(name = "geradorUsuariosBean")
public class GeradorUsuariosBean {

	Usuario usuario = new Usuario();
	private static LinkedList<Usuario> usuarios = new LinkedList<Usuario>();

	public GeradorUsuariosBean() {

	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public List<Usuario> getUsuarios() {
		return usuarios;
	}

	public void acrescentarUsuario() {
		Usuario u = new Usuario();
		u.setLogin("DEMO->"+GeradorNomeUtil.getNomeRandomico());
		u.setUf(GeradorNomeUtil.getUFRandomico());
		long x = Math.round(Math.random() * 5);
		if (x == 2) {
			if (usuarios.size() > 10) {
				usuarios.removeFirst();
			}
			usuarios.add(u);
		}

	}
}
