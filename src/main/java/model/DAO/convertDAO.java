package model.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Bean.file;

public class convertDAO {
	
	public List<file> getAllFiles() {
	    List<file> fileList = new ArrayList<>();
	    String sqlString = "SELECT * FROM file";
	    try (Connection connection = connectionDB.getConnection();
		         PreparedStatement statement = connection.prepareStatement(sqlString)) {
	        ResultSet rs = statement.executeQuery();
	        while (rs.next()) {
	            file f = new file(
            		rs.getInt("id"),
	                rs.getString("fileName"),
	                rs.getString("filePath"),
	                rs.getString("status"),
	                rs.getString("convertedPath")
	            );
	            fileList.add(f);
	        }
	    } catch (Exception e) {
	    	e.printStackTrace();
		}
	    return fileList;
	}
	
	public void saveFile(file file) {
	    String sqlString = "INSERT INTO file (fileName, filePath, status, convertedPath) VALUES (?, ?, ?, ?)";
	    
	    try (Connection connection = connectionDB.getConnection();
	         PreparedStatement statement = connection.prepareStatement(sqlString)) {
	         
	        statement.setString(1, file.getFileName());
	        statement.setString(2, file.getFilePath());
	        statement.setString(3, file.getStatus());
	        statement.setString(4, file.getConvertedPath());
	        
	        statement.executeUpdate();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	
	public List<file> getFilePending() {
		List<file> resultFiles = new ArrayList<file>();
		String sqlString = "SELECT * FROM file WHERE status = 'pending'";
		
		try (Connection connection = connectionDB.getConnection();
				PreparedStatement statement = connection.prepareStatement(sqlString)) {
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				int id = resultSet.getInt("id");
				String fileName = resultSet.getString("fileName");
				String filePath = resultSet.getString("filePath");
				String status = resultSet.getString("status");
				String convertedPath = resultSet.getString("convertedPath");
				file file = new file(id, fileName, filePath, status, convertedPath);
				resultFiles.add(file);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultFiles;
	}
	
	public void updateConvertedFile(int fileId, String status, String convertedPath) {
        String sql = "UPDATE file SET status = ?, convertedPath = ? WHERE id = ?";

        try (Connection connection = connectionDB.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

        	statement.setString(1, status);
            statement.setString(2, convertedPath);
            statement.setInt(3, fileId);

            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	public file getFileById(int fileId) throws SQLException {
	    String query = "SELECT * FROM file WHERE id = ?";
	    try (Connection connection = connectionDB.getConnection();
	    	PreparedStatement stmt = connection.prepareStatement(query)) {
	        stmt.setInt(1, fileId);
	        ResultSet rs = stmt.executeQuery();
	        if (rs.next()) {
	            return new file(
	                rs.getInt("id"),
	                rs.getString("fileName"),
	                rs.getString("filePath"),
	                rs.getString("status"),
	                rs.getString("convertedPath")
	            );
	        }
	    }
	    return null;
	}

	public void deleteFile(int fileId) throws SQLException {
	    String query = "DELETE FROM file WHERE id = ?";
	    try (Connection connection = connectionDB.getConnection();
	    	PreparedStatement stmt = connection.prepareStatement(query)) {
	        stmt.setInt(1, fileId);
	        stmt.executeUpdate();
	    }
	}


}
