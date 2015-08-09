package br.com.eudiamante.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import br.com.eudiamante.framework.persistencia.BaseDAO;
import br.com.eudiamante.framework.persistencia.JPAUtil;
import br.com.eudiamante.model.Usuario;

public class UsuarioDAO extends BaseDAO<Usuario> {

	public UsuarioDAO(){
		super(Usuario.class);
	}
	
	public boolean existe(Usuario usuario){
		EntityManager em = JPAUtil.getEntityManager();
		em.getTransaction().begin();
		Query query = em.createQuery("from Usuario u where u.login = :pLogin and u.senha = :pSenha");
		query.setParameter("pLogin", usuario.getLogin());
		query.setParameter("pSenha", usuario.getSenha());
		
		boolean encontrado = !query.getResultList().isEmpty();
		em.getTransaction().commit();
		em.close();
		
		return encontrado;
	}
	
	@SuppressWarnings("unchecked")
	public Usuario getUsuarioPorLogin(String login) {
		EntityManager em = JPAUtil.getEntityManager();
		em.getTransaction().begin();
		Query query = em.createQuery("from Usuario u where u.login = :pLogin");
		query.setParameter("pLogin", login);
		
		List<Usuario> usuarios = (List<Usuario>)(query.getResultList());
		em.getTransaction().commit();
		em.close();
		
		if(usuarios!=null && usuarios.size()>0){
			return usuarios.get(0);
		}else{
			return null;
		}
	}
	
}
