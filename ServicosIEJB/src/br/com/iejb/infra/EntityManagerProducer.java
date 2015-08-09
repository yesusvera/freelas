package br.com.iejb.infra;

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class EntityManagerProducer {

	@PersistenceContext(name = "iejbMembroPU")
	@Produces
	private EntityManager entityManager;

}
