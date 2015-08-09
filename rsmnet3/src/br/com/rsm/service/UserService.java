package br.com.rsm.service;

import java.io.Serializable;
import java.sql.SQLException;

import br.com.rsm.dao.UserDao;
import br.com.rsm.model.User;

public class UserService implements Serializable {

	private static final long serialVersionUID = -3401354213363357024L;
	private UserDao userDao;
	
	public UserService() {
	}

	public void save(User user) throws SQLException {
			this.userDao = new UserDao();
			userDao.save(user);
	}
	
	public User findByLoginAndPass(User userAuth) throws SQLException {
		User result = new User();
			this.userDao = new UserDao();
			result = userDao.findByLoginAndPass(userAuth);
		return result;
	}
	
	public User findByUser(User user) throws SQLException {
		User result = new User();
		this.userDao = new UserDao();
		result = userDao.findByUser(user);
		return result;
	}
}
