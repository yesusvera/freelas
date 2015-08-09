package br.com.rsm.dao;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.com.rsm.model.Notification;
import br.com.rsm.util.BaseDAORsm;

public class NotificationDao extends BaseDAORsm implements Serializable {
	
	private static final long serialVersionUID = 2180407804934461174L;

	//
	public Notification save(Notification notification) throws SQLException {
		Notification result = null;
		String sql;
		sql = "insert into tb_notification (title, body, accountOrig, accountDest) values (?, ?, ?, ?)";
		
		try (PreparedStatement stmt = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			stmt.setString(1, notification.getTitle());
			stmt.setString(2, notification.getBody());
			stmt.setLong(3, 46L);
			stmt.setLong(4, notification.getAccountId());
			
			stmt.execute(); 
			result = new Notification(notification.getId());
//			getConnection().commit(); 
			stmt.close();
			
		} catch (Exception e) {
//			getConnection().rollback();
			e.printStackTrace();
		}
		return result;
	}
	
	
	public List<Notification> paginate(Long id, int qtd, Long accId) throws SQLException {
	
		List<Notification> notifications = null;
		Notification notification = null;
		String sql = "SELECT * FROM tb_notification n WHERE n.accountDest = ? ORDER BY n.id DESC LIMIT ?, ?";
		try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
			stmt.setLong(1, accId);
			stmt.setLong(2, id);
			stmt.setLong(3, qtd);
			stmt.execute();
			ResultSet resultSet = stmt.getResultSet();
//			result = new User();
			notification = new Notification();
			notifications = new ArrayList<Notification>();
			while(resultSet.next()) {
				notification.setId(resultSet.getLong(1));
				notification.setTitle((resultSet.getString(2)));
				notification.setBody((resultSet.getString(3)));
				//notification.setAccId(resultSet.getLong(4));
				//notification.setCreated(resultSet.getDate);
//				result.setPass(resultSet.getString(3));
//				result.setAccount(new Account(resultSet.getLong(4)));
				notifications.add(notification);
//			
			}
	    	return notifications;
		}
	}
		
		

//	public Notification findByLoginAndPass(Notification userAuth) throws SQLException {
//		Notification result = null;
//		String sql = "select * from tb_user u inner join tb_account a where (a.email = ? or a.userId= ?) and u.pass = ?;";
//		try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
//			stmt.setString(1, userAuth.getLogin());
//			stmt.setString(2, userAuth.getLogin());
//			stmt.setString(3, userAuth.getPass());
//			stmt.execute();
//			ResultSet resultSet = stmt.getResultSet();
//			result = new Notification();
//			while(resultSet.next()) {
//				result.setId(resultSet.getLong(1));
//				result.setAccount(new Account(resultSet.getLong(4)));
//				result.getAccount().setFullname(resultSet.getString(7));
//				result.getAccount().setEmail(resultSet.getString(8));
//				result.getAccount().setCpf(resultSet.getString(10));
//				result.getAccount().setNotificationId(resultSet.getString(6));
//				result.getAccount().setStatus(resultSet.getString(19)); //status
//				//result.setParentId(parentId);
//				
//			}
//		}
//		return result;
//	}
//	
//	public Notification findByUser(User user) throws SQLException {
//		User result = null;
//		String sql = "select * from tb_user u where u.id = ?;";
//		try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
//			stmt.setLong(1, user.getId());
//			stmt.execute();
//			ResultSet resultSet = stmt.getResultSet();
//			result = new User();
//			while(resultSet.next()) {
//				result.setId(resultSet.getLong(1));
//				result.setParentId(resultSet.getLong(2));
//				result.setPass(resultSet.getString(3));
//				result.setAccount(new Account(resultSet.getLong(4)));
//			}
//		}
//		return result;
//	}



}
