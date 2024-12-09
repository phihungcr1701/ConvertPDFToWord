package controllers;

import java.io.*;
import java.sql.SQLException;
import java.util.*;

import javax.servlet.*;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import model.BO.ConvertBO;
import model.Bean.FileEntity;

@WebServlet("/convertServlet")
@MultipartConfig
public class ConvertServlet extends HttpServlet {

    private ConvertBO convertBO = new ConvertBO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    	List<FileEntity> files = convertBO.getAllFile();
        req.setAttribute("files", files);
        String path = "/views/convertView.jsp";
        RequestDispatcher rd = getServletContext().getRequestDispatcher(path);
        rd.forward(req, res);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String action = req.getParameter("action");
        if ("convert".equals(action)) {
            this.handleConvert(req, res);
        } else if ("delete".equals(action)) {
            this.handleDelete(req, res);
        } else if ("downloadAll".equals(action)) {
            try {
                this.handleDownloadAll(req, res);
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
        }
        res.sendRedirect(req.getContextPath() + "/convertServlet");
    }

    private void handleConvert(HttpServletRequest req, HttpServletResponse res) {
        try {
            Collection<Part> parts = req.getParts();
            String uploadDir = getServletContext().getRealPath("/") + "uploads";
            convertBO.uploadFile(parts, uploadDir);
            new Thread(() -> convertBO.processPendingFiles()).start();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleDownloadAll(HttpServletRequest req, HttpServletResponse res) throws IOException, SQLException {
        String[] fileIds = req.getParameterValues("fileIds");

        if (fileIds != null && fileIds.length > 0) {
            List<File> filesToDownload = convertBO.getFilesByIds(fileIds);

            if (!filesToDownload.isEmpty()) {
                // Create a ZIP file containing the selected files
                File zipFile = convertBO.createZipFile(filesToDownload);

                // Send the ZIP file to the client
                res.setContentType("application/zip");
                res.setHeader("Content-Disposition", "attachment; filename=files.zip");
                res.setContentLength((int) zipFile.length());

                try (FileInputStream is = new FileInputStream(zipFile);
                     ServletOutputStream os = res.getOutputStream()) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = is.read(buffer)) != -1) {
                        os.write(buffer, 0, bytesRead);
                    }
                }

                zipFile.delete();
            } else {
                res.getWriter().write("No files found to download.");
            }
        } else {
            res.getWriter().write("No files selected.");
        }
    }

}
