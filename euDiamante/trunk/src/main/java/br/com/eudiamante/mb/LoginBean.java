package br.com.eudiamante.mb;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import br.com.eudiamante.dao.UsuarioDAO;
import br.com.eudiamante.model.Convite;
import br.com.eudiamante.model.Usuario;
import br.com.eudiamante.util.UtilSession;

@SessionScoped
@ManagedBean(name="loginBean")
public class LoginBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Usuario usuario = new Usuario() ;
	
	
	public Usuario getUsuario() {
		return usuario;
	}


	public String efetuaLogin(){
		UsuarioDAO dao = new UsuarioDAO();
		boolean loginValido = dao.existe(this.usuario);
		System.out.println("O login era valido? "+loginValido);
		
		if(loginValido) {
			Convite convite = UtilSession.getConviteSessao();
			
			if(convite!=null){
				if(convite.getPaginaAtual()!=null && !convite.getPaginaAtual().isEmpty()){
					return convite.getPaginaAtual().concat("?faces-redirect=true"); 
				}
			}
			
			return "convite?faces-redirect=true"; 
		} else {
			return "index";
		}
	}
	
	public boolean isLogado(){
		return this.usuario.getLogin() != null; 
	}
	
	public String logout(){
		this.usuario = new Usuario();
		return "index?faces-redirect=true";
	}
	
}
