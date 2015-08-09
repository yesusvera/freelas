package br.com.rsm.dao;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import br.com.rsm.model.Account;
import br.com.rsm.model.User;
import br.com.rsm.util.BaseDAORsm;


public class UserDao extends BaseDAORsm implements Serializable {

	
	private static final long serialVersionUID = 7917213313175273341L;

	public User save(User user) throws SQLException {
		User result = new User(user.getId());
		String sql;
		if(user.getId() != null){ //update
			sql = "update tb_user set pass = ? where id = ?";
		} else {
			sql = "insert into tb_user (pass, accountId) values (?, ?)";
		}
		//String sql = "insert into tb_user (login, pass, account) values (?, ?, ?)";
		
		try (PreparedStatement stmt = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
		//try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
			
			if(user.getId() != null){ //update
				stmt.setString(1, user.getPass());
				stmt.setLong(2, user.getId());
				
			} else {
				stmt.setString(1, user.getPass());
				stmt.setLong(2, user.getAccount().getId());
			}
			
			stmt.execute();
//			stmt.close();
			//getConnection().commit();
			
//			result = new User(user.getId());
			//TODO update (pass change) nao funfa, verificar se funfa pra insert (new user)
			try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
				while (generatedKeys.next()) {
					long id = generatedKeys.getLong(1);
					result = new User(id);
				}
			}
			stmt.close();
		} catch (Exception e) {
			//getConnection().rollback();
			e.printStackTrace();
		}
		return result;
	}
	
	
	
	public User saveChangePersonalInfo(User user) throws SQLException {
		User result = new User(user.getId());
		String sql;
		if(user.getId() != null){ //update
			sql = "update tb_user set pass = ? where id = ?";
		} else {
			sql = "insert into tb_user (pass, accountId) values (?, ?)";
		}
		//String sql = "insert into tb_user (login, pass, account) values (?, ?, ?)";
		
		try (PreparedStatement stmt = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
		//try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
			stmt.setString(1, user.getPass());
			stmt.setLong(2, user.getId());
			
			stmt.execute();
			//stmt.close();
			//getConnection().commit();
			
//
			
//			result = new User(user.getId());
			//TODO update (pass change) nao funfa, verificar se funfa pra insert (new user)
			try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
				while (generatedKeys.next()) {
					long id = generatedKeys.getLong(1);
					result = new User(id);
				}
			}
			stmt.close();
		} catch (Exception e) {
			//getConnection().rollback();
			e.printStackTrace();
		}
		return result;
	}
	
	
	public User saveParentId(User user) throws SQLException {
		User result = new User();
		String sql = "update tb_user set parentId=? where id=?";
		try (PreparedStatement stmt = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			
			stmt.setLong(1, user.getParentId());
			stmt.setLong(2, user.getId());
			stmt.execute();
		//	getConnection().commit();
			try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
				while (generatedKeys.next()) {
					long id = generatedKeys.getLong(1);
					result = new User(id);
				}
			}
			stmt.close();
		} catch (Exception e) {
			//getConnection().rollback();
			e.printStackTrace();
		}
		return result;
	}
	

	public User findByLoginAndPass(User userAuth) throws SQLException {
		User result = null;
		String sql = "select * from tb_user u inner join tb_account a where u.accountId = a.id and u.pass = ? and (a.email = ? or a.userId = ?);";
		try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
			stmt.setString(1, userAuth.getPass());
			stmt.setString(2, userAuth.getLogin());
			stmt.setString(3, userAuth.getLogin());
			
			stmt.execute();
			ResultSet resultSet = stmt.getResultSet();
			result = new User();
			while(resultSet.next()) {
				result.setId(resultSet.getLong(1));
				
				result.getAccount().setId(resultSet.getLong(5));
				result.getAccount().setUserId(resultSet.getString("userId"));
				result.getAccount().setFullname(resultSet.getString("fullname"));
				result.getAccount().setBirthdate(resultSet.getDate("birthdate"));
				result.getAccount().setEmail(resultSet.getString("email"));
				result.getAccount().setCpf(resultSet.getString("cpf"));
				result.getAccount().setRg(resultSet.getString("rg"));
				result.getAccount().setAddress(resultSet.getString("address"));
				result.getAccount().setAddressNumber(resultSet.getString("addressNumber"));
				result.getAccount().setComplement(resultSet.getString("complement"));
				result.getAccount().setCep(resultSet.getString("cep"));
				result.getAccount().setState(resultSet.getString("state"));
				//result.getAccount().setCity(resultSet.getLong(13));
				result.getAccount().setPhone(resultSet.getString("phone"));
				result.getAccount().setMobile(resultSet.getString("mobile"));
				result.getAccount().setCarrier(resultSet.getString("carrier"));
				result.getAccount().setStatus(resultSet.getString("status"));
				if(resultSet.getLong("leftSide") != 0){
					result.getAccount().setLeftSide(new Account(resultSet.getLong("leftSide")));
				} 
				
				if(resultSet.getLong("rightSide") != 0){
					result.getAccount().setRightSide(new Account(resultSet.getLong("rightSide")));
				} 
				result.getAccount().setCreated(resultSet.getTimestamp("created"));
				result.getAccount().setParentId(resultSet.getString("parentId"));
				result.getAccount().setNetworkSide(resultSet.getString("networkSide"));
				result.getAccount().setTickets(resultSet.getLong("tickets"));
				result.getAccount().setActivated(resultSet.getTimestamp("activated"));
				
//				result.setAccount(new Account(resultSet.getLong(4)));
//				result.getAccount().setFullname(resultSet.getString(7));
//				result.getAccount().setEmail(resultSet.getString(8));
//				result.getAccount().setCpf(resultSet.getString(10));
//				result.getAccount().setUserId(resultSet.getString(6));
//				result.getAccount().setStatus(resultSet.getString(21)); //status
//				result.setParentId(resultSet.getLong(2));
				
			}
		}
		return result;
	}
	
	public User findByUser(User user) throws SQLException {
		User result = null;
		
		String sql = "select * from tb_user u where u.id = ?;";
		try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
			stmt.setLong(1, user.getId());
			stmt.execute();
			ResultSet resultSet = stmt.getResultSet();
			result = new User();
			while(resultSet.next()) {
				result.setId(resultSet.getLong(1));
				result.setParentId(resultSet.getLong(2));
				result.setPass(resultSet.getString(3));
				result.setAccount(new Account(resultSet.getLong(4)));
			}
		}
		return result;
	}

	public User findParentByParentId(String parentId) throws SQLException {
		User result = null;
		
		String sql = "select * from tb_account tba inner join tb_user tbu where tba.id = tbu.accountId and tba.userId = ?;";
		try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
			stmt.setString(1, parentId);
			stmt.execute();
			ResultSet resultSet = stmt.getResultSet();
			result = new User();
			while(resultSet.next()) {
				result.setId(resultSet.getLong(21));
//				result.setParentId(resultSet.getLong(2));
//				result.setPass(resultSet.getString(3));
//				result.setAccount(new Account(resultSet.getLong(4)));
			}
		}
		return result;
	}



//	public User findByParentId(User user) throws SQLException{
//		User result = null;	
//		
//		String sql = "select * from tb_user a where a.parentId = ?";
//		try(PreparedStatement stmt = getConnection().prepareStatement(sql)){
//			stmt.setLong(1, user.getParentId());
//			stmt.execute();
//			ResultSet resultSet = stmt.getResultSet();
//			result = new User();
//			if(resultSet.next()){
//				result.setId(resultSet.getLong(1));
//				result.setParentId(resultSet.getLong(2));
//				result.setPass(resultSet.getString(3));
//				result.setAccount(new Account(resultSet.getLong(4)));
//			}
//		} 
//		
//		return result;
//	}

	
	
//	public List<Account> listAll() throws SQLException {
//		String sql = "select * from Account";
//		try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
//			stmt.execute();
//			ResultSet resultSet = stmt.getResultSet();
//			ArrayList<Account> accounts = new ArrayList<>();
//			while(resultSet.next()) {
////				String nome = resultSet.getString("nome");
////				String descricao = resultSet.getString("descricao");
//				Account acc = new Account();
//				acc.setPass(resultSet.getString("pass"));
//				acc.setParentId(resultSet.getString("parentId"));
//				acc.setFullname(resultSet.getString("fullname"));
//				acc.setParentId(resultSet.getString("email"));
//				//acc.setPass(resultSet.getString("birthdate"));
//				acc.setParentId(resultSet.getString("cpf"));
//				acc.setParentId(resultSet.getString("rg"));
//				acc.setParentId(resultSet.getString("address"));
//				acc.setParentId(resultSet.getString("addressNumber"));
//				acc.setParentId(resultSet.getString("complement"));
//				acc.setParentId(resultSet.getString("cep"));
//				//state
//				acc.setParentId(resultSet.getString("city"));
//				acc.setParentId(resultSet.getString("phone"));
//				acc.setParentId(resultSet.getString("mobile"));
//				//carrier
//				//agreement
//				acc.setParentId(resultSet.getString("status"));
//                //created
//				
//				
//				
//				Long id = resultSet.getLong("id");
//				//Account acc = new Account(pass, passConfirmation, parentId, fullname, email, birthdate, cpf, rg, address, addressNumber, complement, cep, state, city, phone, mobile, carrier, agreement, status, created)
//				acc.setId(id);
//				accounts.add(acc);
//			}
//			return accounts;
//		}
//	}

}
