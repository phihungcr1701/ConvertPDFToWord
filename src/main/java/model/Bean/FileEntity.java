package model.Bean;

public class FileEntity {
    private int id;
    private String fileName;
    private String filePath;
    private String status; 
    private String convertedPath;
    
    public FileEntity(int id, String fileName, String filePath, String status, String convertedPath) {
    	this.id = id;
    	this.fileName = fileName;
    	this.filePath = filePath;
    	this.status = status;		
    	this.convertedPath = convertedPath;
    }
    
    public FileEntity(String fileName, String filePath, String status, String convertedPath) {
    	this.fileName = fileName;
    	this.filePath = filePath;
    	this.status = status;
    	this.convertedPath = convertedPath;
    }
    
	public int getId() {
		return id;
	} 	
	public void setId(int id) {
		this.id = id;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getConvertedPath() {
		return convertedPath;
	}
	public void setConvertedPath(String convertedPath) {
		this.convertedPath = convertedPath;
	}
	
}
