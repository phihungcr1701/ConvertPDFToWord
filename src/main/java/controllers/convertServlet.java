package controllers;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import model.BO.convertBO;
import model.Bean.file;

@WebServlet("/convertServlet")
@MultipartConfig

public class convertServlet extends HttpServlet {
	
	private convertBO convertBO = new convertBO();
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
       
    }
	
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		
		String action = req.getParameter("action");
		if (action.equals("convert")) {
			this.handleConvert(req, res);
		} else if (action.equals("delete")) {
			this.handleDelete(req, res);
		}
	}
	
	private void handleConvert (HttpServletRequest req, HttpServletResponse res) {
		try {
			Collection<Part> parts = req.getParts();
			String uploadDir = getServletContext().getRealPath("/") + "uploads";
			
            // Tạo thư mục uploads nếu chưa tồn tại
            File uploadDirectory = new File(uploadDir);
            if (!uploadDirectory.exists()) {
                uploadDirectory.mkdirs(); 
            }
            
			for (Part part : parts) {
				String fileName = part.getSubmittedFileName();
				if (fileName != null && !fileName.isEmpty()) {
					 String filePath = uploadDir + File.separator + fileName;
	                 // luu file vao thu muc uploadDir
					 part.write(filePath);
					 file uploadFile = new file(fileName, filePath, "pending", null);
					 convertBO.saveFile(uploadFile);
                }
			}
			
			new Thread(() -> {
				convertBO.processPendingFiles();
			}).start();
			
			List<file> files = convertBO.getAllFile();
            req.setAttribute("files", files);
            String path = "/views/convertView.jsp";
            RequestDispatcher rd = getServletContext().getRequestDispatcher(path);
            rd.forward(req, res);
            
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void handleDelete(HttpServletRequest req, HttpServletResponse res) {
		try {
			
			String[] fileIds = req.getParameterValues("fileIds");
	        if (fileIds != null && fileIds.length > 0) {
	            for (String fileId : fileIds) {
	                convertBO.deleteFile(Integer.parseInt(fileId));
	            }
	        }
			
			List<file> files = convertBO.getAllFile();
			req.setAttribute("files", files);
			String path = "/views/convertView.jsp";
			RequestDispatcher rd = getServletContext().getRequestDispatcher(path);
			rd.forward(req, res);			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
