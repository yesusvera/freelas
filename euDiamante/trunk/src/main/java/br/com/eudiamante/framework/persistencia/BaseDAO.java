package br.com.eudiamante.framework.persistencia;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;


public class BaseDAO <T>{
	
	private final Class<T> classe;
	
	public BaseDAO(Class<T> classe){
		this.classe = classe;
	}
	
	public T gravar(T t){
		EntityManager em = JPAUtil.getEntityManager();
		em.getTransaction().begin();
		t = em.merge(t);
		em.getTransaction().commit();
		em.close();
		
		return t;
	}
	
	public void remover(T t){
		EntityManager em = JPAUtil.getEntityManager();
		em.getTransaction().begin();
		em.remove(em.merge(t));
		em.getTransaction().commit();
		em.close();
	}
	
	@SuppressWarnings("unchecked")
	public List<T> listar(){
		EntityManager em = JPAUtil.getEntityManager();
		
		Query query = em.createQuery("select t from "+classe.getSimpleName() + " t");
		List <T> result = query.getResultList();
		em.close();
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public List<T> listar(String hql){
		EntityManager em = JPAUtil.getEntityManager();
		
		Query query = em.createQuery(hql);
		List <T> result = query.getResultList();
		em.close();
		
		return result;
	}
	
	public T buscarPorId (Object id){
		EntityManager em = JPAUtil.getEntityManager();
		return (T)em.find(classe, id);
	}

}
