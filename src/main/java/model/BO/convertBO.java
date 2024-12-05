package model.BO;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

import controllers.WebSocket;
import model.Bean.file;
import model.DAO.convertDAO;
import utils.convertUtil;

public class convertBO {
	
	private convertDAO convertDAO = new convertDAO();
	
	public void saveFile(file file) {
		convertDAO.saveFile(file);
	}
	
	public List<file> getAllFile() {
		return convertDAO.getAllFiles();
	}
	
	public void updateConvertedFile(int fileId, String status, String convertedPath) {
		convertDAO.updateConvertedFile(fileId, status, convertedPath);
	}
	
	public void processPendingFiles() {
        List<file> pendingFiles = convertDAO.getFilePending();

        for (file f : pendingFiles) {
            try {
                String filePath = f.getFilePath();
                String convertedPath = filePath.replace(".pdf", ".docx");
                convertUtil.ConvertPDFToWord(filePath, convertedPath);

                convertDAO.updateConvertedFile(f.getId(), "completed", convertedPath);
                WebSocket.sendMessageToAll(f);
                System.out.println("Đã xử lý xong file: " + f.getFileName());

            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Lỗi khi xử lý file: " + f.getFileName());
            }
        }
    }
	
	public void deleteFile(int fileId) {
	    try {
	        file fileToDelete = convertDAO.getFileById(fileId);

	        if (fileToDelete != null) {
	            File filePdf = new File(fileToDelete.getFilePath());
	            File fileWord = new File(fileToDelete.getConvertedPath());
	            if (filePdf.exists() && fileWord.exists()) {
	                filePdf.delete();
	                fileWord.delete();
	            }
	            convertDAO.deleteFile(fileId);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	public file getFilebyID(int fileId) throws SQLException {
		return convertDAO.getFileById(fileId);
	}

}
