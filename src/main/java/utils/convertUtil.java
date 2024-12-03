package utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

public class convertUtil {

    public static void ConvertPDFToWord(String pdfPath, String wordPath) {
        try (PDDocument pdfDocument = PDDocument.load(new File(pdfPath))) {
            // Trích xuất văn bản từ PDF
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String text = pdfStripper.getText(pdfDocument);
            
            // Tạo tài liệu Word mới
            XWPFDocument wordDocument = new XWPFDocument();
            XWPFParagraph paragraph = wordDocument.createParagraph();
            paragraph.createRun().setText(text); // Thêm văn bản vào Word
            
            // Lưu tài liệu Word vào file
            try (OutputStream outputStream = new FileOutputStream(new File(wordPath))) {
                wordDocument.write(outputStream);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
