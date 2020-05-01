package xavier.ricardo.softws;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import javax.naming.NamingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;

import xavier.ricardo.softws.dao.AgendaDao;
import xavier.ricardo.softws.dao.AnexoDao;
import xavier.ricardo.softws.dao.PedidoDao;
import xavier.ricardo.softws.dao.UsuarioDao;
import xavier.ricardo.softws.tipos.Agenda;
import xavier.ricardo.softws.tipos.AgendaMes;
import xavier.ricardo.softws.tipos.Anexo;
import xavier.ricardo.softws.tipos.Compromisso;
import xavier.ricardo.softws.tipos.Encerramento;
import xavier.ricardo.softws.tipos.Imagem;
import xavier.ricardo.softws.tipos.Pdf;
import xavier.ricardo.softws.tipos.Pedido;
import xavier.ricardo.softws.tipos.Usuarios;
import xavier.ricardo.softws.utils.PdfEncerramento;

public class SoftService {

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String version() {
		return "soft v2.6.2(01/05/2020)";
	}

	@GET
	@Path("/login/{usuario}/{senha}/{versao}")
	@Produces(MediaType.TEXT_PLAIN)
	public String login(@PathParam("usuario") String usuario, @PathParam("senha") String senha,
			@PathParam("versao") String versao) {

		System.out.println(new Date() + " softws login: " + usuario + " " + versao);
		try {
			return UsuarioDao.login(usuario.toLowerCase(), senha);

		} catch (NamingException | SQLException | NoSuchAlgorithmException | UnsupportedEncodingException e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}

	@GET
	@Path("/lista/{usuario}/{data}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAgenda(@PathParam("usuario") String usuario, @PathParam("data") String data) {
		
		System.out.println(new Date() + " softws agenda: " + usuario + " " + data);
		try {
			Agenda agenda = new AgendaDao().lista(usuario, data);
			Gson gson = new Gson();
			return gson.toJson(agenda);

		} catch (NamingException | SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@GET
	@Path("/listames/{usuario}/{data}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAgendaMes(@PathParam("usuario") String usuario, @PathParam("data") String data) {
		
		System.out.println(new Date() + " softws agendames: " + usuario + " " + data);
		try {
			AgendaMes agenda = new AgendaDao().listaMes(usuario, data);
			Gson gson = new Gson();
			return gson.toJson(agenda);

		} catch (NamingException | SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@GET
	@Path("/listaNF/{nf}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAgendaNF(@PathParam("nf") String nf) {
		
		System.out.println(new Date() + " softws agendaNF: " + nf);
		try {
			Agenda agenda = new AgendaDao().listaNF(nf);
			Gson gson = new Gson();
			return gson.toJson(agenda);

		} catch (NamingException | SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@GET
	@Path("/usuarios")
	@Produces(MediaType.APPLICATION_JSON)
	public String getUsuarios() {
		
		System.out.println(new Date() + " softws usuarios");
		try {
			Usuarios usuarios = new UsuarioDao().lista();
			Gson gson = new Gson();
			return gson.toJson(usuarios);

		} catch (NamingException | SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@POST
	@Path("/salva/{nome}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String adicionarLista(@PathParam("nome") String nome, String json) {
		
		System.out.println(new Date() + " softws salva: " + nome);
		Gson gson = new Gson();
		Pdf pdf = gson.fromJson(json, Pdf.class);
		byte[] data = Base64.getDecoder().decode(pdf.getConteudo());
		try {
			OutputStream stream = new FileOutputStream("/usr/local/tomcat/webapps/ROOT/soft/pedidos/" + nome);
		    stream.write(data);
		    stream.close();
		} catch (IOException e) {
			e.printStackTrace();
			return e.getMessage();
		}		
		return "ok";
	}
	
	@POST
	@Path("/encerra")
	@Consumes(MediaType.APPLICATION_JSON)
	public String encerra(String json) {
		
		System.out.println(new Date() + " softws agenda encerra");
		System.out.println(json);
		try {
			Gson gson = new Gson();
			Encerramento encerramento = gson.fromJson(json, Encerramento.class);					
			AgendaDao.encerra(encerramento);
			PdfEncerramento pdf = new PdfEncerramento();
			pdf.gera(json);
		} catch (SQLException | NamingException | NoSuchAlgorithmException | IOException e) {
			e.printStackTrace();
			return e.getMessage();
		}
		return "ok";
	}
	
	@POST
	@Path("/foto")
	@Consumes(MediaType.APPLICATION_JSON)
	public String foto(String json) {
		
		try {
			Gson gson = new Gson();
			Imagem imagem = gson.fromJson(json, Imagem.class);
			System.out.println(new Date() + " softws foto " + imagem.getFornecedor()
					+ " " + imagem.getData() + " " + imagem.getOrcamento() + " " + imagem.getId());	
			AnexoDao.anexa(imagem);
		} catch (NamingException | SQLException e) {
			e.printStackTrace();
			return e.getMessage();
		}
		return "ok";
	}
	
	@GET
	@Path("/pedido/{fornecedor}/{data}/{orcamento}/{pedido}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getPedido(@PathParam("fornecedor") String fornecedor, 
			@PathParam("data") String data,
			@PathParam("orcamento") int codOrcamento,
			@PathParam("pedido") int codPedido) {
		
		System.out.println(new Date() + " softws pedido: " + fornecedor + " " + data + " " + codOrcamento + " " + codPedido);
		try {
			Pedido pedido = new PedidoDao().getPedido(fornecedor, data, codOrcamento, codPedido);
			Gson gson = new Gson();
			return gson.toJson(pedido);

		} catch (NamingException | SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@GET
	@Path("/anexos/{fornecedor}/{data}/{orcamento}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAnexos(@PathParam("fornecedor") String fornecedor, 
			@PathParam("data") String data,
			@PathParam("orcamento") int codOrcamento) {
		
		System.out.println(new Date() + " softws getAnexos: " + fornecedor + " " + data + " " + codOrcamento);
		try {
			List<Anexo> anexos = AnexoDao.getAnexos(fornecedor, data, codOrcamento);
			Compromisso compromisso = new Compromisso();
			compromisso.setAnexos(anexos);
			Gson gson = new Gson();
			return gson.toJson(compromisso);

		} catch (NamingException | SQLException | ParseException | IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}

// 01/05/2020 - 2.6.2 - só estava mostrando o primeiro item do pedido