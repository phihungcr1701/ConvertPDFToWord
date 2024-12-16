package model.DAO;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import config.Config;

public class ConnectionDB {
	
	public static Connection getConnection() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			return DriverManager.getConnection(Config.DB_URL, Config.DB_USERNAME, Config.DB_PASSWORD);
		} catch (Exception e) {
			System.out.print("Kết nối thất bại");
			e.printStackTrace();
			return null;
		}
	}
}
