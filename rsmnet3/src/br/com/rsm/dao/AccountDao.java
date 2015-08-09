package br.com.rsm.dao;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;

import br.com.rsm.enumeradores.EnumUFId;
import br.com.rsm.model.Account;
import br.com.rsm.util.BaseDAORsm;

public class AccountDao extends BaseDAORsm implements Serializable {

	private static final long serialVersionUID = -8850967157107875610L;

	public Account save(Account account) throws SQLException {
		Account result = new Account();
		String sql = "insert into tb_account(fullname, email, birthdate, cpf, rg, address, addressNumber, complement, cep, state, city, phone, mobile, carrier, status, created, parentId) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement stmt = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			                                
			java.sql.Date sqlDateBirthday = new java.sql.Date(account.getBirthdate().getTime());
			account.setBirthdate(sqlDateBirthday);
			
			
			stmt.setString(1, account.getFullname());
			stmt.setString(2, account.getEmail());
			stmt.setDate(3, sqlDateBirthday);
			stmt.setString(4, account.getCpf());
			stmt.setString(5, account.getRg());
			stmt.setString(6, account.getAddress());
			stmt.setString(7, account.getAddressNumber());
			stmt.setString(8, account.getComplement());
			stmt.setString(9, account.getCep());
			stmt.setLong(10, EnumUFId.valueOf(account.getState()).getV());
			stmt.setLong(11, account.getCity().getId());
			stmt.setString(12, account.getPhone());
			stmt.setString(13, account.getMobile());
			stmt.setString(14, account.getCarrier());
			stmt.setString(15, account.getStatus());
			stmt.setTimestamp(16, new Timestamp(new Date().getTime()));
			stmt.setString(17, account.getParentId());
			stmt.execute();
			try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
				while (generatedKeys.next()) {
					long id = generatedKeys.getLong(1);
					result = new Account(id);
				}
			}
			stmt.close();
			//getConnection().commit();
		} catch (Exception e) {
			//getConnection().rollback();
			e.printStackTrace();
		}
		return result;
	}
	
	
	public Account saveUserId(Account account) throws SQLException {
		
		Account result = new Account();
		String sql = "update tb_account set userId=? where id=?";
		try (PreparedStatement stmt = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			
			stmt.setString(1, account.getUserId());
			stmt.setLong(2, account.getId());
			stmt.execute();
			try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
				while (generatedKeys.next()) {
					long id = generatedKeys.getLong(1);
					result = new Account(id);
				}
			}
			stmt.close();
		//	getConnection().commit();
		} catch (Exception e) {
			//getConnection().rollback();
			e.printStackTrace();
		}
		return result;
	}
	
	
	
	public Account update(Account account) throws SQLException {
		Account result = new Account();
		//String sql = "update tb_account set fullname=?, email=?, cpf=?, rg=?, address=?, addressNumber=?, complement=?, cep=?, city=?, phone=?, mobile=?, status=? where id=?";
		String sql = "update tb_account set fullname=? where id=?";
		try (PreparedStatement stmt = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			
			stmt.setString(1, account.getFullname());
//			stmt.setString(2, account.getEmail());
//			stmt.setString(3, account.getCpf());
//			stmt.setString(4, account.getRg());
//			stmt.setString(5, account.getAddress());
//			stmt.setString(6, account.getAddressNumber());
//			stmt.setString(7, account.getComplement());
//			stmt.setString(8, account.getCep());
//			stmt.setString(9, account.getCity());
//			stmt.setString(10, account.getPhone());
//			stmt.setString(11, account.getMobile());
//			stmt.setString(12, account.getStatus());
			stmt.setLong(2, account.getId());
			stmt.execute();
			try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
				while (generatedKeys.next()) {
					long id = generatedKeys.getLong(1);
					result = new Account(id);
				}
			}
			stmt.close();
			//getConnection().commit();
		} catch (Exception e) {
			//getConnection().rollback();
			e.printStackTrace();
		}
		return result;
	}

	public Account findById(Long id) throws SQLException{
		Account acc = new Account();	
		
		String sql = "select * from tb_account a where a.id = ?";
		try(PreparedStatement stmt = getConnection().prepareStatement(sql)){
			stmt.setLong(1, id);
			stmt.execute();
			ResultSet resultSet = stmt.getResultSet();
			if(resultSet.next()){
				criarAccount(acc, resultSet);
			}
		} 
		
		return acc;
	}


	private void criarAccount(Account acc, ResultSet resultSet)
			throws SQLException {
		acc.setId(resultSet.getLong(1));
		acc.setUserId(resultSet.getString(2));
		acc.setFullname(resultSet.getString(3));
		acc.setEmail(resultSet.getString(4));
		acc.setBirthdate(resultSet.getDate(5));
		acc.setCpf(resultSet.getString(6));
		acc.setRg(resultSet.getString(7));
		acc.setAddress(resultSet.getString(8));
		acc.setAddressNumber(resultSet.getString(9));
		acc.setComplement(resultSet.getString(10));
		acc.setCep(resultSet.getString(11));
		acc.setState(resultSet.getString(12));
		//acc.setCity(resultSet.getLong(13));
		acc.setPhone(resultSet.getString(14));
		acc.setMobile(resultSet.getString(15));
		acc.setCarrier(resultSet.getString(16));
		acc.setStatus(resultSet.getString(17));
		if(resultSet.getLong(18) != 0){
			acc.setLeftSide(new Account(resultSet.getLong(18)));
		} else {
			acc.setLeftSide(null);
		}
		if(resultSet.getLong(19) != 0){
			acc.setRightSide(new Account(resultSet.getLong(19)));
		} else {
			acc.setRightSide(null);
		}
		acc.setCreated(resultSet.getTimestamp(20));
		acc.setParentId(resultSet.getString(21));
		acc.setNetworkSide(resultSet.getString(22));
		acc.setTickets(resultSet.getLong(23));
		acc.setActivated(resultSet.getTimestamp(24));
	} 
	
	public Account findByParentId(String parentId) throws SQLException {
		Account acc = null;	
		String sql = "select * from tb_account a where a.userId = ?";
		try(PreparedStatement stmt = getConnection().prepareStatement(sql)){
			stmt.setString(1, parentId);
			stmt.execute();
			ResultSet resultSet = stmt.getResultSet();
			acc = new Account();
			if(resultSet.next()){
				criarAccount(acc, resultSet);
			}
		} 
		return acc;
	}

	public Account updateAccountWithUserId(Account account) throws SQLException {
		Account result = new Account();
		String sql = "update tb_account set userId=? where id=?";
		try (PreparedStatement stmt = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			
			stmt.setString(1, account.getUserId());
			stmt.setLong(2, account.getId());
			stmt.execute();
			ResultSet resultSet = stmt.getResultSet();
			//try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
				if (resultSet.next()) {
					long id = resultSet.getLong(1);
					result = new Account(id);
			//	}
				}
			stmt.close();
			//getConnection().commit();
		} catch (Exception e) {
			//getConnection().rollback();
			e.printStackTrace();
		}
		return result;
	}


	public Account findUpAccountByCurrentId(Long currentId) throws NumberFormatException, SQLException {
		Account acc = null;	
		
		String sql = "select * from tb_account a where a.leftSide = ? or a.rightSide = ? ";
		try(PreparedStatement stmt = getConnection().prepareStatement(sql)){
			stmt.setLong(1, currentId);
			stmt.setLong(2, currentId);
			
			stmt.execute();
			ResultSet resultSet = stmt.getResultSet();
			acc = new Account();
			if(resultSet.next()){
				criarAccount(acc, resultSet);
			}
		} 
		
		return acc;
	}


	/**
	 * @author yesus
	 * @param userId
	 * @return
	 */
	public int getTotalIndicadosDiretos(String userId) {
		
		String sql = "select count(*) from tb_account a where a.parentId = ?";
		try(PreparedStatement stmt = getConnection().prepareStatement(sql)){
			stmt.setString(1, userId);
			
			stmt.execute();
			ResultSet resultSet = stmt.getResultSet();
			if(resultSet.next()){
				return resultSet.getInt(1);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return 0;
	}


	public Account findByUserId(String id) throws SQLException {
		Account acc = new Account();	
		
		String sql = "select * from tb_account a where a.userId = ?";
		try(PreparedStatement stmt = getConnection().prepareStatement(sql)){
			stmt.setString(1, id);
			stmt.execute();
			ResultSet resultSet = stmt.getResultSet();
			if(resultSet.next()){
				criarAccount(acc, resultSet);
			}
		} 
		
		return acc;
	}


	/**
	 * @author yesus
	 * @param id
	 * @param selectConfigNetworkValue
	 */
	public void saveNetworkSide(Long id, String selectConfigNetworkValue) {
		String sql = "update tb_account set networkSide=? where id=?";
		try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
			
			stmt.setString(1, selectConfigNetworkValue);
			stmt.setLong(2, id);
			stmt.execute();
//			try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
//				while (generatedKeys.next()) {
//					long id = generatedKeys.getLong(1);
//					result = new Account(id);
//				}
//			}
			stmt.close();
//			getConnection().commit();
		} catch (Exception e) {
//			try {
//				//getConnection().rollback();
//			} catch (SQLException e1) {
//				e1.printStackTrace();
//			}
			e.printStackTrace();
		}
//		return result;
	}


	public Account isActivated(String userIdAtivar) {
		Account acc = null;	
		
		String sql = "select * from tb_account a where a.userId = ? and a.status = 'A'";
		try (PreparedStatement stmt = getConnection().prepareStatement(sql)){
			stmt.setString(1, userIdAtivar);
			stmt.execute();
			ResultSet resultSet = stmt.getResultSet();
			if(resultSet.next()){
				acc = new Account();	
				criarAccount(acc, resultSet);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return acc;
	}


	public void activatedAccount(String userIdAtivar) {
		String sql = "update tb_account set activated=now(), status = ? where userId=? ";
		try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
			
			stmt.setString(1, "A");
			stmt.setString(2, userIdAtivar);
			stmt.execute();
			stmt.close();
//			getConnection().commit();
		} catch (Exception e) {
//			try {
//				//getConnection().rollback();
//			} catch (SQLException e1) {
//				e1.printStackTrace();
//			}
			e.printStackTrace();
		}
	}


	public Account saveChangePersonalInfo(Account account) {
		Account result = new Account(account.getId());
		String sql = "update tb_account set fullname=?, birthdate=?, cpf=?, rg=? where id=?";
		
		
		java.sql.Date sqlDateBirthday = new java.sql.Date(account.getBirthdate().getTime());
		account.setBirthdate(sqlDateBirthday);
		
		try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
			
			stmt.setString(1, account.getFullname());
			stmt.setDate(2, new java.sql.Date(account.getBirthdate().getTime()));
			stmt.setString(3, account.getCpf());
			stmt.setString(4, account.getRg());
			stmt.setLong(5, account.getId());
			stmt.execute();
//			stmt.close();
			
//			ResultSet resultSet = stmt.getResultSet();
//			try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
//				if (resultSet.next()) {
//					long id = resultSet.getLong(1);
//					System.out.println("Personal Info Updated - id: " + id);
//					result = new Account(id);
//				}
//			}
			stmt.close();
			
			
//			getConnection().commit();
		} catch (Exception e) {
//			try {
//				//getConnection().rollback();
//			} catch (SQLException e1) {
//				e1.printStackTrace();
//			}
			e.printStackTrace();
		}
		
		return result;
	}


	public void useTicket(Account account) {
		String sql = "update tb_account set tickets = tickets - 1 where id=?";
		try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
			stmt.setLong(1, account.getId());
			stmt.execute();
			stmt.close();
//			getConnection().commit();
		} catch (Exception e) {
//			try {
//				//getConnection().rollback();
//			} catch (SQLException e1) {
//				e1.printStackTrace();
//			}
			e.printStackTrace();
		}
	}


	/**
	 * @author yesus
	 */
	public void suspendID(String userIdSuspend) {
		String sql = "update tb_account set status = ? where userId=? ";
		try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
			
			stmt.setString(1, "S");
			stmt.setString(2, userIdSuspend);
			stmt.execute();
			stmt.close();
//			getConnection().commit();
		} catch (Exception e) {
//			try {
//				//getConnection().rollback();
//			} catch (SQLException e1) {
//				e1.printStackTrace();
//			}
			e.printStackTrace();
		}
	}


	/**
	 * @author yesus
	 */
	public void reactivateSuspendedID(String userIdSuspend) {
		String sql = "update tb_account set status = ? where userId=? ";
		try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
			
			stmt.setString(1, "A");
			stmt.setString(2, userIdSuspend);
			stmt.execute();
			stmt.close();
//			getConnection().commit();
		} catch (Exception e) {
//			try {
//				//getConnection().rollback();
//			} catch (SQLException e1) {
//				e1.printStackTrace();
//			}
			e.printStackTrace();
		}
	}


	public Account saveChangeContactInfo(Account account) {
		Account result = new Account(account.getId());
		String sql = "update tb_account set address=?, addressNumber=?, complement=?, cep=?, state=?, city=?, phone=?, mobile=?, carrier=?, email=? where id=?";
		
		
		java.sql.Date sqlDateBirthday = new java.sql.Date(account.getBirthdate().getTime());
		account.setBirthdate(sqlDateBirthday);
		
		try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
			
			stmt.setString(1, account.getAddress());
			stmt.setString(2, account.getAddressNumber());
			stmt.setString(3, account.getComplement());
			stmt.setString(4, account.getCep());
			stmt.setString(5, account.getState());
			stmt.setLong(6, account.getCity().getId());
			stmt.setString(7, account.getPhone());
			stmt.setString(8, account.getMobile());
			stmt.setString(9, account.getCarrier());
			stmt.setString(10, account.getEmail());
			stmt.setLong(11, account.getId());
			stmt.execute();
//			stmt.close();
			
//			ResultSet resultSet = stmt.getResultSet();
//			try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
//				if (resultSet.next()) {
//					long id = resultSet.getLong(1);
//					System.out.println("Personal Info Updated - id: " + id);
//					result = new Account(id);
//				}
//			}
			stmt.close();
			
			
//			getConnection().commit();
		} catch (Exception e) {
//			try {
//				//getConnection().rollback();
//			} catch (SQLException e1) {
//				e1.printStackTrace();
//			}
			e.printStackTrace();
		}
		
		return result;

	}
	
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
