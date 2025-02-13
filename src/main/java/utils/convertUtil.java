package utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;

import org.apache.pdfbox.pdmodel.PDDocument;

import com.spire.pdf.FileFormat;
import com.spire.pdf.PdfDocument;

public class convertUtil {
	private static final int MAX_PAGES_PER_FILE = 10;

	public static void ConvertPDFToWord(String pdfPath, String WordPath) {
		ArrayList<String> pathOfChunkFiles = splitPdf(pdfPath);
		ArrayList<String> fileDocxPaths = convertChunkPdfToDocx(pathOfChunkFiles);
		Collections.sort(fileDocxPaths);
		CombineDocx.combineFiles(fileDocxPaths, WordPath);
	}

	private static ArrayList<String> splitPdf(String filePath) {
		ArrayList<String> pathOfChunkFiles = new ArrayList<>();
		try {
			String fileNameDontHaveExtension = filePath.replace(".pdf", "").replaceAll(" ", "");
			PDDocument document = PDDocument.load(new File(filePath));
			int totalPages = document.getNumberOfPages();
			int fileIndex = 1;

			for (int start = 0; start < totalPages; start += MAX_PAGES_PER_FILE) {
				int end = Math.min(fileIndex * MAX_PAGES_PER_FILE, totalPages);

				PDDocument chunkDocument = new PDDocument();
				for (int page = start; page < end; page++) {
					chunkDocument.addPage(document.getPage(page));
				}

				String outputPdf = fileNameDontHaveExtension + "_part_" + fileIndex + ".pdf";
				pathOfChunkFiles.add(outputPdf);
				chunkDocument.save(outputPdf);
				chunkDocument.close();
				fileIndex++;
			}
			document.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return pathOfChunkFiles;
	}

	private static ArrayList<String> convertChunkPdfToDocx(ArrayList<String> chunkFiles) {
		CountDownLatch latch = new CountDownLatch(chunkFiles.size());
		ArrayList<String> docFilePaths = new ArrayList<>();
		try {
			ArrayList<ConvertDocxThread> threads = new ArrayList<>();
			for (String filePath : chunkFiles) {
				ConvertDocxThread thread = new ConvertDocxThread(docFilePaths, filePath, latch);
				threads.add(thread);
				thread.start();
			}

			latch.await();
			System.out.println("Đã chuyển đổi xong, đang chờ gộp...");
		} catch (Exception e) {
		}
		return docFilePaths;
	}
}

class ConvertDocxThread extends Thread {
	private final CountDownLatch latch;
	private ArrayList<String> docFilePaths;
	private String filePath;

	public ConvertDocxThread(ArrayList<String> docFilePaths, String filePath, CountDownLatch latch) {
		this.filePath = filePath;
		this.docFilePaths = docFilePaths;
		this.latch = latch;
	}

	private void convert(String filePath) {
		try {
			System.out.println(filePath + " start");
			PdfDocument document = new PdfDocument();
			document.loadFromFile(filePath);
			String docFilePath = filePath.replace(".pdf", ".docx");
			document.saveToFile(docFilePath, FileFormat.DOCX);
			document.close();
			this.docFilePaths.add(docFilePath);
			System.out.println(filePath + " end");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			latch.countDown();
		}
	}

	@Override
	public void run() {
		this.convert(filePath);
	}
}
