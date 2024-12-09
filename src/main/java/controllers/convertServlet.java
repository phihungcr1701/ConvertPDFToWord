package controllers;

import java.io.*;
import java.sql.SQLException;
import java.util.*;
import java.util.zip.*;

import javax.servlet.*;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import model.BO.convertBO;
import model.Bean.file;

@WebServlet("/convertServlet")
@MultipartConfig
public class convertServlet extends HttpServlet {

    private convertBO convertBO = new convertBO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    	List<file> files = convertBO.getAllFile();
        req.setAttribute("files", files);
        String path = "/views/convertView.jsp";
        RequestDispatcher rd = getServletContext().getRequestDispatcher(path);
        rd.forward(req, res);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String action = req.getParameter("action");
        System.out.println("hi" + action);
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

            File uploadDirectory = new File(uploadDir);
            if (!uploadDirectory.exists()) {
                uploadDirectory.mkdirs();
            }

            for (Part part : parts) {
                String fileName = part.getSubmittedFileName();
                if (fileName != null && !fileName.isEmpty()) {
                    String uniqueFileName = getUniqueFileName(uploadDir, fileName);
                    String filePath = uploadDir + File.separator + uniqueFileName;
                    
                    part.write(filePath);
                    
                    file uploadFile = new file(uniqueFileName, filePath, "pending", null);
                    convertBO.saveFile(uploadFile);
                }
            }

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
            List<File> filesToDownload = new ArrayList<>();

            for (String fileIdStr : fileIds) {
                int fileId = Integer.parseInt(fileIdStr);
                file f = convertBO.getFilebyID(fileId);
                if (f != null && "COMPLETED".equals(f.getStatus())) {
                    File file = new File(f.getConvertedPath());
                    if (file.exists()) {
                        filesToDownload.add(file);
                    }
                }
            }

            if (!filesToDownload.isEmpty()) {
                // Create a ZIP file containing the selected files
                File zipFile = createZipFile(filesToDownload);

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

    private File createZipFile(List<File> files) throws IOException {
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

}
