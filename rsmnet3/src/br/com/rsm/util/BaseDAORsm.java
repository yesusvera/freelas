package br.com.rsm.util;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.google.appengine.api.utils.SystemProperty;

public class BaseDAORsm implements Serializable {
	
	private static final long serialVersionUID = -5200623124991551113L;

	private static Connection connection = getConnection();
	
	protected static Connection getConnection(){
		try {
			if(connection==null || connection.isClosed()){
					conecta();
			}
		} catch (ClassNotFoundException | SQLException e) {
			try {
				conecta();
			} catch (ClassNotFoundException | SQLException e1) {
				e1.printStackTrace();
			}
		}
		return connection;
	}
	
	private static void conecta() throws SQLException, ClassNotFoundException{
		String url = null;

		try {
			if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production) {
				// Connecting from App Engine.
				// Load the class that provides the "jdbc:google:mysql://"
				// prefix.
//				Class.forName("com.mysql.jdbc.GoogleDriver");
//				url =
//				"jdbc:google:mysql://rsm-net:rsmdb1/rsmdb?user=root";
//				conn = DriverManager.getConnection(url);
				Class.forName("com.mysql.jdbc.Driver");
				url = "jdbc:mysql://108.167.168.26:3306/speed046_rsmdbDEV?user=speed046_rsmDEV";
				connection = DriverManager.getConnection(url, "speed046_rsmDEV", "WoK(W*{l6n(~");
			} else {
					 // Connecting from an external network.
					Class.forName("com.mysql.jdbc.Driver");
					url = "jdbc:mysql://108.167.168.26:3306/speed046_rsmdbDEV?user=speed046_rsmDEV";
					connection = DriverManager.getConnection(url, "speed046_rsmDEV", "WoK(W*{l6n(~");
//					url = "jdbc:mysql://173.194.248.195:3306/rsmdb?user=root";
//					conn = DriverManager.getConnection(url, "root", "1qaz2wsx");
//					Class.forName("com.mysql.jdbc.Driver");
//					url = "jdbc:mysql://localhost:3306/rsmdb";
//					conn = DriverManager.getConnection(url, "root", "1qaz2wsx");
			}
			
			connection.setAutoCommit(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}		
}