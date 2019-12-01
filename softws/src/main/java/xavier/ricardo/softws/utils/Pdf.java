package xavier.ricardo.softws.utils;

import java.io.IOException;

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;

public class Pdf {

	public void gera(String arq) throws IOException {
		
	      PdfWriter writer = new PdfWriter(arq);           
	      
	      // Creating a PdfDocument object       
	      PdfDocument pdfDoc = new PdfDocument(writer);                   
	      
	      // Creating a Document object       
	      Document doc = new Document(pdfDoc);                
	      
	      // Creating a new page       
	      PdfPage pdfPage = pdfDoc.addNewPage();       
	      
	      // Creating a PdfCanvas object       
	      PdfCanvas canvas = new PdfCanvas(pdfPage);        
	      
	      canvas.setFontAndSize(PdfFontFactory.createFont(FontConstants.HELVETICA), 20);
	      
	      canvas.beginText();
	      canvas.moveText(10, 100).showText("titulo");
	      canvas.endText();
	      
	      canvas.moveTo(100, 300);              
	      canvas.lineTo(500, 300);           
	      canvas.closePathStroke();

	      canvas.beginText();
	      canvas.moveText(200, 400).showText("nome");
	      canvas.endText();

	      canvas.moveTo(100, 500);              
	      canvas.lineTo(500, 500);           
	      canvas.closePathStroke();
	      
	      // Closing the document       
	      doc.close();  		
	}
	
	public static void main(String[] args) throws IOException {
		new Pdf().gera("teste.pdf");
	}
	
}
