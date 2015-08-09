package br.com.rsm.dao;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.com.rsm.enumeradores.EnumUFId;
import br.com.rsm.model.City;
import br.com.rsm.model.State;
import br.com.rsm.util.BaseDAORsm;

public class DirectionDao extends BaseDAORsm implements Serializable {


	private static final long serialVersionUID = 1L;

	public List<City> findCitiesByState(State state) throws SQLException{
		
		List<City> cities = new ArrayList<City>();
		City city;
		EnumUFId.valueOf(state.getAcronym());
		
		
//		cities.add(city);
		String sql = "select * from tb_city c where c.stateId = ?";
		try(PreparedStatement stmt = getConnection().prepareStatement(sql)){
			stmt.setLong(1, EnumUFId.valueOf(state.getAcronym()).getV());
			stmt.execute();
			ResultSet resultSet = stmt.getResultSet();
			while(resultSet.next()){
				city = new City(resultSet.getInt(1), resultSet.getString(2), resultSet.getInt(3));
				cities.add(city);
			}
		} 
		
		//cities.add("Acrelândia");
		return cities;
	}
	

	public void save(City city) throws SQLException {
		//Account result = new Account();
		String sql = "insert into tb_city(name, stateId) values (?, ?)";
		try (PreparedStatement stmt = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			
			stmt.setString(1, city.getName());
			stmt.setInt(2,city.getStateId());
		
			stmt.execute();
//			try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
//				while (generatedKeys.next()) {
//					long id = generatedKeys.getLong(1);
//					result = new Account(id);
//				}
//			}
			getConnection().commit(); 
			stmt.close();
		} catch (Exception e) {
			getConnection().rollback();
			e.printStackTrace();
		}
		//return result;
	}
}
