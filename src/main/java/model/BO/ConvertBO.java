package model.BO;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.servlet.http.Part;
import controllers.WebSocket;
import model.Bean.FileEntity;
import model.DAO.ConvertDAO;
import utils.convertUtil;

public class ConvertBO {
	
	private ConvertDAO convertDAO = new ConvertDAO();
	
	private String getUniqueFileName(String uploadDir, String fileName) {
        File file = new File(uploadDir + File.separator + fileName);
        if (!file.exists()) {
            return fileName; 
        }

        int dotIndex = fileName.lastIndexOf('.');
        String baseName = (dotIndex == -1) ? fileName : fileName.substring(0, dotIndex);
        String extension = (dotIndex == -1) ? "" : fileName.substring(dotIndex);

        int counter = 1;
        String newFileName = baseName + " (" + counter + ")" + extension;

        while (new File(uploadDir + File.separator + newFileName).exists()) {
            counter++;
            newFileName = baseName + " (" + counter + ")" + extension;
        }

        return newFileName;
    }
	
	
	public List<FileEntity> getAllFile() {
		return convertDAO.getAllFiles();
	}
	
	public void processPendingFiles() {
        List<FileEntity> pendingFiles = convertDAO.getFilePending();

        for (FileEntity f : pendingFiles) {
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
	        FileEntity fileToDelete = convertDAO.getFileById(fileId);

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
	
	public void uploadFile(Collection<Part> parts, String uploadDir) {
		try {
			File uploadDirectory = new File(uploadDir);
            if (!uploadDirectory.exists()) {
                uploadDirectory.mkdirs();
            }

            for (Part part : parts) {
                String fileName = part.getSubmittedFileName();
                if (fileName != null && !fileName.isEmpty()) {
                    String uniqueFileName = this.getUniqueFileName(uploadDir, fileName);
                    String filePath = uploadDir + File.separator + uniqueFileName;
                    part.write(filePath);
                    FileEntity uploadFile = new FileEntity(uniqueFileName, filePath, "pending", null);
                    convertDAO.saveFile(uploadFile);
                }
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<File> getFilesByIds(String[] fileIds) throws SQLException {
        List<File> filesToDownload = new ArrayList<>();
        for (String fileIdStr : fileIds) {
            int fileId = Integer.parseInt(fileIdStr);
            FileEntity f = convertDAO.getFileById(fileId);
            if (f != null && "COMPLETED".equals(f.getStatus())) {
                File file = new File(f.getConvertedPath());
                if (file.exists()) {
                    filesToDownload.add(file);
                }
            }
        }
        return filesToDownload;
    }
	
	public File createZipFile(List<File> files) throws IOException {
    	File zipFile = new File("files.zip");

        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
            byte[] buffer = new byte[1024];

            for (File file : files) {
                String originalFileName = file.getName(); // Use original file name

                try (FileInputStream fis = new FileInputStream(file)) {
                    zos.putNextEntry(new ZipEntry(originalFileName)); // No suffix added, using original name

                    int length;
                    while ((length = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, length);
                    }

                    zos.closeEntry();
                }
            }
        }

        return zipFile;
    }

}
