package xavier.ricardo.softws.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
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

import xavier.ricardo.softws.dao.AgendaDao;
import xavier.ricardo.softws.dao.BancoDados;
import xavier.ricardo.softws.dao.ClienteDao;
import xavier.ricardo.softws.dao.FilialDao;
import xavier.ricardo.softws.dao.FuncionarioDao;
import xavier.ricardo.softws.tipos.Cliente;
import xavier.ricardo.softws.tipos.Contato;
import xavier.ricardo.softws.tipos.Encerramento;
import xavier.ricardo.softws.tipos.Endereco;
import xavier.ricardo.softws.tipos.Funcionario;
import xavier.ricardo.softws.tipos.Ponto;

public class Pdf {

	public void gera(String arq, Encerramento encerramento, Funcionario func, Endereco filial, Cliente cliente, List<Contato> contatos) throws IOException {
		
	      PdfWriter writer = new PdfWriter(arq);           
	      
	      PdfDocument pdfDoc = new PdfDocument(writer);                   
	      
	      Document doc = new Document(pdfDoc);                
	      
	      PdfPage pdfPage = pdfDoc.addNewPage(); 
	      Rectangle size = pdfPage.getMediaBox();
	      
	      PdfCanvas canvas = new PdfCanvas(pdfPage);        
	      
	      canvas.setFontAndSize(PdfFontFactory.createFont(FontConstants.HELVETICA), 12);

	      // separador
	      
	      canvas.moveTo(200, 0);              
	      canvas.lineTo(200, size.getHeight());           
	      canvas.closePathStroke();
	      
	      // imagem

	      String imageFile = "/tmp/logo1.jpg"; 
	      ImageData data = ImageDataFactory.create(imageFile); 
	      Image img = new Image(data);
	      img.setFixedPosition(10, size.getHeight() - 180);
	      doc.add(img);
	      
	      // colaborador
	      
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
	    	  if (func.getFone() != null) {
	    		  canvas.beginText();
	    		  String fone = formataFone(func.getFone().trim());
	    		  canvas.moveText(10, size.getHeight() - 340).showText(fone);
	    		  canvas.endText();
	    	  }
	      }
	      
	      // filial

	      if (filial.getRua() != null) {
	    	  canvas.beginText();
	    	  canvas.moveText(10, size.getHeight() - 550).showText(filial.getRua().trim() + ", " + filial.getNumero().trim());
	    	  canvas.endText();
	      }
	      if (filial.getBairro() != null) {
	    	  canvas.beginText();
	    	  canvas.moveText(10, size.getHeight() - 565).showText(filial.getBairro().trim());
	    	  canvas.endText();
	      }
	      if (filial.getCidade() != null) {
	    	  canvas.beginText();
	    	  canvas.moveText(10, size.getHeight() - 580).showText(filial.getCidade().trim());
	    	  canvas.endText();
	      }
	      if (filial.getCep() != null) {
	    	  canvas.beginText();
	    	  canvas.moveText(10, size.getHeight() - 595).showText("CEP: " + formataCep(filial.getCep().trim()));
	    	  canvas.endText();
	      }
	      
	      canvas.setFontAndSize(PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD), 12);
	      canvas.beginText();
	      canvas.moveText(210, size.getHeight() - 70).showText("ENCERRAMENTO AGENDAMENTO ");
	      canvas.endText();
	      canvas.setFontAndSize(PdfFontFactory.createFont(FontConstants.HELVETICA), 12);

	      // cliente
	      
	      canvas.setFontAndSize(PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD), 12);
	      canvas.beginText();
	      canvas.moveText(210, size.getHeight() - 100).showText("CLIENTE");
	      canvas.endText();
	      canvas.setFontAndSize(PdfFontFactory.createFont(FontConstants.HELVETICA), 12);
	      if (cliente != null) {
		      if (cliente.getNome() != null) {
		    	  canvas.beginText();
		    	  canvas.moveText(220, size.getHeight() - 120).showText(cliente.getNome().trim());
		    	  canvas.endText();
		      }
		      if (encerramento.getNome() != null) {
		    	  canvas.beginText();
		    	  canvas.moveText(220, size.getHeight() - 135).showText(encerramento.getNome().trim());
		    	  canvas.endText();
		      }	    	  		      		      
		      if (cliente.getEndereco() != null) {
		    	  canvas.beginText();
		    	  canvas.moveText(220, size.getHeight() - 135).showText(cliente.getEndereco().trim());
		    	  canvas.endText();
		      }	    	  		      
	      }

	      // objetivo
	      
	      canvas.setFontAndSize(PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD), 12);
	      canvas.beginText();
	      canvas.moveText(210, size.getHeight() - 200).showText("OBJETIVO");
	      canvas.endText();
	      canvas.setFontAndSize(PdfFontFactory.createFont(FontConstants.HELVETICA), 12);

	      // observações
	      
	      canvas.setFontAndSize(PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD), 12);
	      canvas.beginText();
	      canvas.moveText(210, size.getHeight() - 300).showText("OBSERVAÇÕES");
	      canvas.endText();
	      canvas.setFontAndSize(PdfFontFactory.createFont(FontConstants.HELVETICA), 12);
	      
	      if (encerramento.getObservacao() != null) {
	    	  String[] linhas = encerramento.getObservacao().split("\\n");
	    	  double y = size.getHeight() - 320;
	    	  for (String linha : linhas) {
	    	      canvas.beginText();
	    	      canvas.moveText(220, y).showText(linha);
	    	      canvas.endText();	    		  
	    	      y -= 18;
	    	  }
	      }
	      
	      // assinatura
	      
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
		
		Funcionario func = FuncionarioDao.get(encerramento.getUsuario());
		Endereco filial = FilialDao.get("BHZ");
		
		Connection bd = BancoDados.conecta();
		String codCliente = AgendaDao.getCliente(bd, encerramento.getUsuario(), encerramento.getData());
		Cliente cliente = null;
		List<Contato> contatos = null;
		if (codCliente != null) {
			cliente = ClienteDao.get(codCliente);
			if (cliente != null) {
				contatos = ClienteDao.getContatos(bd, codCliente);
			}
		}
		bd.close();
		/*
		Funcionario func = new Funcionario();
		func.setNome("FABIANA FRANCO FERRARI");
		func.setFone("31991038581");
		*/
		
		new Pdf().gera("teste.pdf", encerramento, func, filial, cliente, contatos);
	}
	
	private String formataFone(String fone) {
  	  switch (fone.length()) {
  	  case 9:
  		  // 012345678
  		  // 988749526
  		  return fone.substring(0, 5) + "-" + fone.substring(5);
  	  case 11:
  		  // 01234567890
  		  // 31988749526
  		  return "(" + fone.substring(0, 3) + ")"
  				  + fone.substring(2, 7) + "-" + fone.substring(7);
  	  }
  	  return fone;
	}
	
	private String formataCep(String cep) {
  	  switch (cep.length()) {
  	  case 8:
  		  // 01234567
  		  // 30493165
  		  return cep.substring(0, 5) + "-" + cep.substring(5);
  	  }
  	  return cep;
	}
	
}
