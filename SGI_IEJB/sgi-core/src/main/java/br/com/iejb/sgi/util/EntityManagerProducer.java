package br.com.iejb.sgi.util;

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Produz uma instância de EntityManager e possiblita usar @Inject para injeção
 * (necessário para o Spring Data JPA)
 * 
 */
public class EntityManagerProducer {

	@PersistenceContext(name = "sgiPU")
	@Produces
	private EntityManager entityManager;

}
