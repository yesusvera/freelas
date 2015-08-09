package br.com.eudiamante.mb;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import br.com.eudiamante.framework.persistencia.BaseDAO;
import br.com.eudiamante.model.Usuario;

@RequestScoped
@ManagedBean
public class UsuarioBean {

	private Usuario usuario = new Usuario();
	private List<Usuario> usuarios;

	public Usuario getUsuario() {
		return this.usuario;
	}

	public void gravar() {
		BaseDAO<Usuario> dao = new BaseDAO<Usuario>(Usuario.class);
		dao.gravar(usuario);
		this.usuario = new Usuario();
		this.usuarios = dao.listar();
	}

	public List<Usuario> getUsuarios() {
		if (usuarios == null) {
			BaseDAO<Usuario> dao = new BaseDAO<Usuario>(Usuario.class);
			this.usuarios = dao.listar();
		}
		return usuarios;
	}
}