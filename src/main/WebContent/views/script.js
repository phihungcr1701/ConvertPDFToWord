
const socket = new WebSocket("ws://localhost:8085/ConvertPDFToWord/socket");

socket.onopen = function() {
	console.log("WebSocket connection established.");
};

socket.onmessage = function(event) {
	const messageData = JSON.parse(event.data);
	const fileId = messageData.id;
	const fileName = messageData.fileName;
	
	// Cập nhật trạng thái của file trong bảng
	const statusCell = document.getElementById("status-" + fileId);
	if (statusCell) {
		statusCell.textContent = "COMPLETED";
	}

	const downloadLink = document.createElement("a");
	downloadLink.href = contextPath + "/uploads/" + fileName.replace("pdf", "docx"); 
	downloadLink.textContent = "Tải xuống";
	
	const actionCell = document.getElementById("file-" + fileId).cells[3];
	actionCell.innerHTML = ""; // Xóa phần "Đang xử lý"
	actionCell.appendChild(downloadLink);
};

socket.onerror = function(error) {
	console.error("WebSocket error: ", error);
};

socket.onclose = function() {
	console.log("WebSocket connection closed.");
};



//checkbox
document.addEventListener("DOMContentLoaded", function () {
	const selectAllCheckbox = document.getElementById("selectAll");
	const fileCheckboxes = document.querySelectorAll(".fileCheckbox");
	
	// Xử lý nút "Chọn tất cả"
	selectAllCheckbox.addEventListener("change", function () {
	    fileCheckboxes.forEach((checkbox) => {
	        checkbox.checked = selectAllCheckbox.checked;
	    });
	});
	
	// Kiểm tra nếu tất cả checkbox được chọn, cập nhật trạng thái nút "Chọn tất cả"
	fileCheckboxes.forEach((checkbox) => {
	    checkbox.addEventListener("change", function () {
	        selectAllCheckbox.checked = Array.from(fileCheckboxes).every(
	            (box) => box.checked
	        );
	    });
	});
});

		
		