package model.DAO;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionDB {
	
	public static Connection getConnection() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			return DriverManager.getConnection("jdbc:mysql://localhost:3306/laptrinhmang", "root", "");
		} catch (Exception e) {
			System.out.print("Kết nối thất bại");
			e.printStackTrace();
			return null;
		}
	}
}
