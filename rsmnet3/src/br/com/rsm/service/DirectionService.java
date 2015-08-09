package br.com.rsm.service;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.rsm.dao.DirectionDao;
import br.com.rsm.model.City;
import br.com.rsm.model.State;

public class DirectionService implements Serializable {

	private static final long serialVersionUID = 1L;
	private DirectionDao directionDao;
	
	
	public List<City> findCitiesByState(State state) throws SQLException{
		List<City> cityList = new ArrayList<City>();
		this.directionDao = new DirectionDao();
		cityList = directionDao.findCitiesByState(state);
			
		
		return cityList;
	} 
	
	
	public DirectionDao getDirectionDao() {
		return directionDao;
	}
	public void setDirectionDao(DirectionDao directionDao) {
		this.directionDao = directionDao;
	}
}
