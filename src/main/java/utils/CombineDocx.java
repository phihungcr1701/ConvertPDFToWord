package utils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import com.spire.doc.Document;
import com.spire.doc.FileFormat;
import com.spire.doc.Section;

public class CombineDocx {

	public static void combineFiles(ArrayList<String> docFilePaths, String output) {
		Thread combineDocxThread = new Thread(() -> {
			Document firstDocument = new Document();
			firstDocument.loadFromFile(docFilePaths.get(0), FileFormat.Docx);

			for (int i = 1; i < docFilePaths.size(); i++) {
				Document documentMerge = new Document();
				documentMerge.loadFromFile(docFilePaths.get(i), FileFormat.Docx);

				// Merge files
				for (Object sectionObj : documentMerge.getSections()) {
					Section section = (Section) sectionObj;
					firstDocument.getSections().add(section.deepClone());
				}
			}

			firstDocument.saveToFile(output, FileFormat.Docx);
			System.out.println("Combine done");
		});

		combineDocxThread.start();
		try {
			// Đảm bảo các luồng convert file pdf qua docx, comvbine file docx đã hoàn thành
			// mới thực hện đi xoá các file tạo ra trong quá trình làm
			combineDocxThread.join();
			deleteTemporalFiles(docFilePaths);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static void deleteTemporalFiles(ArrayList<String> temporalFiles) {
		for (String filePath : temporalFiles) {
			deleteFile(filePath);
			deleteFile(filePath.replace(".docx", ".pdf"));
		}
	}
	
	private static void deleteFile(String filePath) {
		try {
			Path path = Paths.get(filePath);
			Files.deleteIfExists(path);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
}
