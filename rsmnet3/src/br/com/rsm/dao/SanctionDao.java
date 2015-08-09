package br.com.rsm.dao;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;

import br.com.rsm.model.Sanction;
import br.com.rsm.util.BaseDAORsm;

public class SanctionDao extends BaseDAORsm implements Serializable {

	private static final long serialVersionUID = 1L;

	public Sanction save(Sanction sanction) {
		String sql = "insert into tb_sanction(userId, description, date, operator, operation) values (?, ?, ?, ?, ?)";
		try (PreparedStatement stmt = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			stmt.setString(1, sanction.getUserId());
			stmt.setString(2, sanction.getDescription());
			stmt.setTimestamp(3, new Timestamp(new Date().getTime()));
			stmt.setLong(4, sanction.getOperator());
			stmt.setString(5, sanction.getOperation());
			stmt.execute();
			
			try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
				while (generatedKeys.next()) {
					long id = generatedKeys.getLong(1);
					sanction.setId(id);
				}
			}

			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sanction;
	}
}
