package br.com.rsm.service;

import java.io.Serializable;

import br.com.rsm.dao.SanctionDao;
import br.com.rsm.model.Sanction;

/**
 * 
 * @author yesus
 *
 */
public class SanctionService implements Serializable {

	private static final long serialVersionUID = -8719863215748444670L;

	private SanctionDao sanctionDao = new SanctionDao();

	public Sanction save(Sanction sanction){
		return sanctionDao.save(sanction);
	}
	
}
