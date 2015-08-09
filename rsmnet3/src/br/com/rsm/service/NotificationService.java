package br.com.rsm.service;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

import br.com.rsm.dao.NotificationDao;
import br.com.rsm.model.Notification;


public class NotificationService implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3401354213363357024L;
	private transient NotificationDao notificationDao;
	
	public NotificationService() {
		
	}

	public void save(Notification notification) throws SQLException {
		this.notificationDao = new NotificationDao();
		notificationDao.save(notification);
	}

	public List<Notification> paginate(Long id, int qtd, Long accountId) throws SQLException {
		List<Notification> retorno = null;
		this.notificationDao = new NotificationDao();
		retorno = notificationDao.paginate(id, qtd, accountId);
		return retorno;
	}
	
//	public User findByLoginAndPass(User userAuth) throws SQLException {
//		User result = new User();
//		try (Connection connection = new ConnectionFactory().getConnection()) {
//			connection.setAutoCommit(false);
//			this.userDao = new UserDao(connection);
//			result = userDao.findByLoginAndPass(userAuth);
//		} catch(Exception e){
//			e.printStackTrace();
//		}
//		return result;
//	}
	
//	public User findByUser(User user) throws SQLException {
//		User result = new User();
//		try (Connection connection = new ConnectionFactory().getConnection()) {
//			connection.setAutoCommit(false);
//			this.userDao = new UserDao(connection);
//			result = userDao.findByUser(user);
//		} catch(Exception e){
//			e.printStackTrace();
//		}
//		return result;
//	}
	

	
}
