package br.com.eudiamante.framework.persistencia;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JPAUtil {

	private static EntityManagerFactory entityManagerFactory;
	
	static {
		entityManagerFactory = Persistence.createEntityManagerFactory("diamante_persistence");
	}
	
	public static EntityManager getEntityManager() {
		return entityManagerFactory.createEntityManager();
	}
	
//	public void tiraDoCache(Class<?> entityClass, Serializable id){
//		Cache cache = entityManagerFactory.getCache();
//		cache.evict(entityClass, id);
//	}
}