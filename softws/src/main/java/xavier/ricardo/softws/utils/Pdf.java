package xavier.ricardo.softws.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.List;

import javax.naming.NamingException;

import com.google.gson.Gson;
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;

import xavier.ricardo.softws.tipos.Encerramento;
import xavier.ricardo.softws.tipos.Funcionario;
import xavier.ricardo.softws.tipos.Ponto;

public class Pdf {

	public void gera(String arq, Encerramento encerramento, Funcionario func) throws IOException {
		
	      PdfWriter writer = new PdfWriter(arq);           
	      
	      PdfDocument pdfDoc = new PdfDocument(writer);                   
	      
	      Document doc = new Document(pdfDoc);                
	      
	      // Creating a new page       
	      PdfPage pdfPage = pdfDoc.addNewPage(); 
	      Rectangle size = pdfPage.getMediaBox();
	      //System.out.println(size.getWidth());
	      //System.out.println(size.getHeight());
	      
	      PdfCanvas canvas = new PdfCanvas(pdfPage);        
	      
	      canvas.setFontAndSize(PdfFontFactory.createFont(FontConstants.HELVETICA), 12);

	      String imageFile = "/tmp/logo1.jpg"; 
	      ImageData data = ImageDataFactory.create(imageFile); 
	      Image img = new Image(data);
	      img.setFixedPosition(10, size.getHeight() - 180);
	      doc.add(img);
	      
	      canvas.moveTo(200, 0);              
	      canvas.lineTo(200, size.getHeight());           
	      canvas.closePathStroke();
	      
	      canvas.beginText();
	      canvas.moveText(10, size.getHeight() - 300).showText("Colaborador: ");
	      canvas.endText();
	      if (func == null) {
	    	  canvas.beginText();
	    	  canvas.moveText(10, size.getHeight() - 320).showText(encerramento.getUsuario());
	    	  canvas.endText();
	      } else {
	    	  canvas.beginText();
	    	  canvas.moveText(10, size.getHeight() - 320).showText(func.getNome());
	    	  canvas.endText();
	    	  canvas.beginText();
	    	  String fone = formataFone(func.getFone().trim());
	    	  canvas.moveText(10, size.getHeight() - 340).showText(fone);
	    	  canvas.endText();
	      }

	      canvas.setFontAndSize(PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD), 12);
	      canvas.beginText();
	      canvas.moveText(210, size.getHeight() - 70).showText("ENCERRAMENTO AGENDAMENTO ");
	      canvas.endText();
	      canvas.setFontAndSize(PdfFontFactory.createFont(FontConstants.HELVETICA), 12);

	      canvas.setFontAndSize(PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD), 12);
	      canvas.beginText();
	      canvas.moveText(210, size.getHeight() - 100).showText("CLIENTE");
	      canvas.endText();
	      canvas.setFontAndSize(PdfFontFactory.createFont(FontConstants.HELVETICA), 12);

	      canvas.setFontAndSize(PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD), 12);
	      canvas.beginText();
	      canvas.moveText(210, size.getHeight() - 200).showText("OBJETIVO");
	      canvas.endText();
	      canvas.setFontAndSize(PdfFontFactory.createFont(FontConstants.HELVETICA), 12);

	      canvas.setFontAndSize(PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD), 12);
	      canvas.beginText();
	      canvas.moveText(210, size.getHeight() - 300).showText("OBSERVAÇÕES");
	      canvas.endText();
	      canvas.setFontAndSize(PdfFontFactory.createFont(FontConstants.HELVETICA), 12);
	      
	      double xAssinatura = 220;
	      double yAssinatura = 550;
	      double yMax = 0;
	      double k = 2.0;
	      
	      if ((encerramento.getAssinatura() != null) && (encerramento.getAssinatura().getPartes() != null)) {
	    	  for (List<Ponto> pontos : encerramento.getAssinatura().getPartes()) {
	    		  boolean primeiro = true;
	    		  for (Ponto ponto : pontos) {
	    			  double x = (double) ponto.getX() / k + xAssinatura;
	    			  if (ponto.getY() > yMax) {
	    				  yMax = ponto.getY();
	    			  }
	    			  double y = size.getHeight() - (double) ponto.getY() / k - yAssinatura;
	    			  if (primeiro) {
	    				  canvas.moveTo(x, y);
	    				  primeiro = false;
	    			  } else {
	    				  canvas.lineTo(x, y);
	    			  }
	    		  }
	    	  }
	      }
	      
	      canvas.moveTo(xAssinatura, size.getHeight() - yAssinatura - yMax / k - 10);              
	      canvas.lineTo(xAssinatura + 340, size.getHeight() - yAssinatura - yMax / k - 10);           
	      canvas.closePathStroke();
	      
	      canvas.beginText();
	      canvas.moveText(xAssinatura, size.getHeight() - yAssinatura - yMax / k - 30).showText(encerramento.getNome() + " - " + encerramento.getDocumento());
	      canvas.endText();
	      
	      doc.close();  		
	}
	
	public static void main(String[] args) throws IOException, NoSuchAlgorithmException, NamingException, SQLException {
		
		BufferedReader reader = new BufferedReader(new FileReader("teste.txt"));
		StringBuffer json = new StringBuffer();
		String linha;
		while ((linha = reader.readLine()) != null) {
			json.append(linha + "\n");
		}
		reader.close();
		Gson gson = new Gson();
		Encerramento encerramento = gson.fromJson(json.toString(), Encerramento.class);
		
		//Funcionario func = FuncionarioDao.get(encerramento.getUsuario());
		Funcionario func = new Funcionario();
		func.setNome("FABIANA FRANCO FERRARI");
		func.setFone("31991038581");
		
		new Pdf().gera("teste.pdf", encerramento, func);
	}
	
	private String formataFone(String fone) {
  	  switch (fone.length()) {
  	  case 9:
  		  // 012345678
  		  // 988749526
  		  return fone.substring(0, 5) + "-" + fone.substring(5, 9);
  	  case 11:
  		  // 01234567890
  		  // 31988749526
  		  return "(" + fone.substring(0, 3) + ")"
  				  + fone.substring(2, 7) + "-" + fone.substring(7, 11);
  	  }
  	  return fone;
	}
	
}
