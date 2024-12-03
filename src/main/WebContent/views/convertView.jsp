<%@page import="model.Bean.file"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Upload file</title>
    <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/views/Style.css">
</head>
<body>
    <div class="container">
        <div class="logo">
            <span class="pdf">PDF</span>
            <span class="to">to</span>
            <span class="doc">DOC</span>
        </div>

        <form id="uploadForm" action="<%= request.getContextPath() %>/convertServlet" enctype="multipart/form-data" method="POST" class="form">
            <div class="upload-section">
                <input class="btn upload" id="fileInput" type="file" name="files" multiple accept="application/pdf" />
            </div>

            <div class="file-list">
                <table id="fileTable">
                    <thead>
                        <tr>
                            <th><input type="checkbox" id="selectAll" /> </th> 
                            <th>Tên file</th>
                            <th>Trạng thái</th>
                            <th>Hành động</th>
                        </tr>
                    </thead>
                    <tbody>
                        <%
                       	List<file> files = (List<file>) request.getAttribute("files");
                        if (files == null) {
                            files = (List<file>) application.getAttribute("files");
                        }
                        if (files != null && !files.isEmpty()) {
                            for (file f : files) {
                        %>
                        <tr id="file-<%= f.getId() %>">
                            <td><input type="checkbox" class="fileCheckbox" data-id="<%= f.getId() %>" name="fileIds" value="<%= f.getId() %>" /></td> 
                            <td><%= f.getFileName() %></td>
                            <td id="status-<%= f.getId() %>"><%= f.getStatus() %></td>
                            <td>
                                <%
                                if ("COMPLETED".equals(f.getStatus())) {
                                %>
                                	<a 
                                		href="<%= request.getContextPath() + "/uploads/" + f.getFileName().replace("pdf", "docx") %>" 
                                		target="_blank"
                                	>
                                		Tải xuống
                                	</a>
                                <%
                                } else {
                                %>
                                    <span>Đang xử lý</span>
                                <%
                               	}
                                %>
                            </td>
                        </tr>
                        <%
                        	}
                        } else {
                        %>
	                        <tr>
	                            <td colspan="4">Không có file nào được tìm thấy.</td>
	                        </tr>
                        <%
                        }
                        %>
                    </tbody>
                </table>
            </div>

            <div class="action-buttons">
                <button class="btn action" id="convertBtn" type="submit" name="action" value="convert">Chuyển</button>
                <button class="btn action" id="downloadAllBtn" type="button" onclick="() => handleDowloadAllClick()">Tải tất cả</button>
                <button class="btn action delete" id="deleteBtn" type="submit" name="action" value="delete">Xóa</button>
            </div>
        </form>
    </div>

    <script type="text/javascript">
        var contextPath = "<%= request.getContextPath() %>";
    </script>
    <script type="text/javascript" src="<%= request.getContextPath() %>/views/script.js"></script>
</body>
</html>
