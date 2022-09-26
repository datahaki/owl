package ch.alpine.ubongo;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.itextpdf.commons.exceptions.ITextException;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import ch.alpine.tensor.ext.HomeDirectory;

public enum PdfDemo {
  ;
  public static void main(String[] args) throws IOException {
    String simplePdf = HomeDirectory.file("dzone-simple-text.pdf").toString();
    WriterProperties wp = new WriterProperties();
    wp.setPdfVersion(PdfVersion.PDF_2_0);
    try (PdfWriter writer = new PdfWriter(simplePdf, wp); //
        PdfDocument pdfDocument = new PdfDocument(writer); //
        Document document = new Document(pdfDocument)) {
      document.add(new Paragraph("asld fkjahsd fljasdhflkj hasldkfjh lkjh"));
    } catch (FileNotFoundException e) {
      throw new ITextException(e.getMessage());
    }
  }
}
