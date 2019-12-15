package xavier.ricardo.softws.dao;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.naming.NamingException;

import com.google.gson.Gson;

import xavier.ricardo.softws.tipos.Agenda;
import xavier.ricardo.softws.tipos.AgendaMes;
import xavier.ricardo.softws.tipos.Anexo;
import xavier.ricardo.softws.tipos.Assinatura;
import xavier.ricardo.softws.tipos.Compromisso;
import xavier.ricardo.softws.tipos.Encerramento;

public class AgendaDao {

	//http://ricardoxavier.no-ip.org/soft-ws3/softws/lista/fabiana.ferrari/2019-10-16
	@SuppressWarnings("deprecation")
	public Agenda lista(String responsavel, String data) throws NamingException, SQLException {
		
		List<Compromisso> compromissos = new ArrayList<Compromisso>();
		
		String sql = String.format("select a.COD_USUARIO, a.DAT_AGENDAMENTO, a.DAT_PREVISAO, a.COD_NATUREZA, a.COD_PARCEIRO, a.DES_PENDENCIA, "
				+ "a.DES_ENCERRAMENTO, a.DES_NOME, a.DES_DOCUMENTO, a.DES_EMAIL, a.DES_JSON, "
				+ "a.COD_CONTATO, c.DES_PAPEL, c.NRO_FONE1, c.NRO_FONE2, c.NRO_CELULAR, p.DES_LOGRADOURO, p.NRO_ENDERECO, "
				+ "p.DES_COMPLEMENTO, p.NOM_BAIRRO, p.NOM_CIDADE "
				+ "from AGENDA a "
				+ "left outer join PARCEIROS p on p.COD_PARCEIRO = a.COD_PARCEIRO "
				+ "left outer join CONTATOS c on c.COD_PARCEIRO = a.COD_PARCEIRO and c.COD_CONTATO = a.COD_CONTATO "
				+ "where a.COD_RESPONSAVEL='%s' "
				+ "and a.DAT_PREVISAO between ? and ? and a.DAT_SOLUCAO is null order by a.DAT_PREVISAO",
				responsavel);

		int ano = Integer.parseInt(data.substring(0, 4));
		int mes = Integer.parseInt(data.substring(5, 7));
		int dia = Integer.parseInt(data.substring(8, 10));
		Date datai = new Date(ano-1900, mes-1, dia, 0, 0, 0);
		Date dataf = new Date(ano-1900, mes-1, dia, 23, 59, 59);
		
		Connection bd = BancoDados.conecta();
		PreparedStatement cmd = bd.prepareStatement(sql);
		cmd.setDate(1, new java.sql.Date(datai.getTime()));
		cmd.setDate(2, new java.sql.Date(dataf.getTime()));
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		ResultSet cursor = cmd.executeQuery();
		while (cursor.next()) {
			
			Compromisso compromisso = new Compromisso();
			String usuario = cursor.getString("COD_USUARIO");
			Date datAgendamento = new Date(cursor.getDate("DAT_AGENDAMENTO").getTime());
			Date datPrevisao = new Date(cursor.getDate("DAT_PREVISAO").getTime());
			String hora = String.format("%02d:%02d", datPrevisao.getHours(), datPrevisao.getMinutes());
			String natureza = cursor.getString("COD_NATUREZA");
			String parceiro = cursor.getString("COD_PARCEIRO");
			String pendencia = cursor.getString("DES_PENDENCIA");
			String contato = cursor.getString("COD_CONTATO");
			String papel = cursor.getString("DES_PAPEL");
			String fone1 = cursor.getString("NRO_FONE1");
			String fone2 = cursor.getString("NRO_FONE2");
			String celular = cursor.getString("NRO_CELULAR");
			String rua = cursor.getString("DES_LOGRADOURO");
			String nro = cursor.getString("NRO_ENDERECO");
			String complemento = cursor.getString("DES_COMPLEMENTO");
			String bairro = cursor.getString("NOM_BAIRRO");
			String cidade = cursor.getString("NOM_CIDADE");
			String encerramento = cursor.getString("DES_ENCERRAMENTO");
			String nome = cursor.getString("DES_NOME");
			String documento = cursor.getString("DES_DOCUMENTO");
			String email = cursor.getString("DES_EMAIL");
			String json = cursor.getString("DES_JSON");
			compromisso.setUsuario(usuario);
			compromisso.setData(df.format(datAgendamento));
			compromisso.setHora(hora);
			compromisso.setNatureza(natureza);
			compromisso.setParceiro(parceiro);
			compromisso.setPendencia(pendencia);
			compromisso.setContato(contato);
			compromisso.setPapel(papel);
			compromisso.setFone1(fone1);
			compromisso.setFone2(fone2);
			compromisso.setCelular(celular);
			compromisso.setRua(rua);
			compromisso.setNro(nro);
			compromisso.setComplemento(complemento);
			compromisso.setBairro(bairro);
			compromisso.setCidade(cidade);
			compromisso.setEncerramento(encerramento);
			compromisso.setNome(nome);
			compromisso.setDocumento(documento);
			compromisso.setEmail(email);
			compromisso.setJson(json);
			/*
			System.out.println(hora + " " + natureza + " " + parceiro
					+ " " + fone1 + " " + fone2 + " " + celular);
			System.out.println(pendencia);
			*/
			if (pendencia.startsWith("Montagem Pedido: ")) {
				String[] partes = pendencia.replace("\r", " ").replace("\n", " ").split(" ");
				String fornecedor = partes[2];
				String dt = partes[3];
				String orc = partes[4];
				String[] partesData = dt.split("/");
				String a = partesData[2];
				String m = partesData[1];
				String pdf = "/usr/local/tomcat/webapps/ROOT/soft/pedidos/" + fornecedor + a + m + orc + "_1.pdf";
				//System.out.println(pdf);
				
				try {
					compromisso.setCodFornecedor(fornecedor);
					compromisso.setDatOrcamento(dt);
					compromisso.setCodOrcamento(Integer.parseInt(orc));
					compromisso.setNroPedido(Integer.parseInt(partes[5]));
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (new File(pdf).exists()) {
					compromisso.setPedido(pdf);
				} else {
					pdf = "/usr/local/tomcat/webapps/ROOT/soft/pedidos/" + fornecedor + a + m + orc + ".pdf";
					//System.out.println(pdf);
					if (new File(pdf).exists()) {
						compromisso.setPedido(pdf);
					} else {
						// verifica reagendamento
						int r = pendencia.toLowerCase().indexOf("reagendado para ");
						if (r >= 0) {
							dt  = pendencia.substring(r+16, r+16+10);
							partesData = dt.split("/");
							a = partesData[2];
							m = partesData[1];
							if ((m.length() == 2) && (m.charAt(0) == '0')) {
								m = m.substring(1);
							}
							pdf = "/usr/local/tomcat/webapps/ROOT/soft/pedidos/" + fornecedor + a + m + orc + "_1.pdf";
							//System.out.println(pdf);
							if (new File(pdf).exists()) {
								compromisso.setPedido(pdf);
							} else {
								pdf = "/usr/local/tomcat/webapps/ROOT/soft/pedidos/" + fornecedor + a + m + orc + ".pdf";
								//System.out.println(pdf);
								if (new File(pdf).exists()) {
									compromisso.setPedido(pdf);
								}
							}
						}
					}
				}
			}
			//System.out.println(compromisso.getPedido());
			compromissos.add(compromisso);
			
		}
		
		cursor.close();
		cmd.close();		
		
		try {
			carregaAnexos(bd, compromissos);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		bd.close();
		
		Agenda agenda = new Agenda();
		agenda.setCompromissos(compromissos);
		return agenda;

	}

	private void carregaAnexos(Connection bd, List<Compromisso> compromissos) throws SQLException, ParseException {
		
		String sql = "select COD_ANEXO, DES_ARQ_ANEXO, DES_CONTEUDO "
				+ "from ANEXOS_ORCAMENTO "
				+ "where COD_FORNECEDOR=? and DAT_ORCAMENTO=? and COD_ORCAMENTO=? ";

		PreparedStatement cmd = bd.prepareStatement(sql);
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		
		for (Compromisso compromisso : compromissos) {
			
			compromisso.setAnexos(new ArrayList<Anexo>());
			
			String data = compromisso.getDatOrcamento();
			if (data == null) {
				continue;
			}
			cmd.setString(1, compromisso.getCodFornecedor());
			cmd.setDate(2, new java.sql.Date(df.parse(data).getTime()));
			cmd.setInt(3, compromisso.getCodOrcamento());

			ResultSet cursor = cmd.executeQuery();
			
			while (cursor.next()) {
			
				String codigo = cursor.getString("COD_ANEXO");
				String descricao = cursor.getString("DES_ARQ_ANEXO");
				//String conteudo = cursor.getString("DES_CONTEUDO");
				if (descricao.toLowerCase().trim().endsWith(".pdf")) {
					Anexo anexo = new Anexo();
					anexo.setCodigo(codigo);
					compromisso.getAnexos().add(anexo);
				}
				
			}
			
			cursor.close();
		}
		
		cmd.close();
	}

	@SuppressWarnings("deprecation")
	public AgendaMes listaMes(String usuario, String data) throws NamingException, SQLException {
		
		List<Integer> dias = new ArrayList<Integer>();
		
		String sql = String.format("select a.DAT_PREVISAO "
				+ "from AGENDA a "
				+ "where a.COD_RESPONSAVEL='%s' "
				+ "and a.DAT_PREVISAO between ? and ? and a.DAT_SOLUCAO is null order by a.DAT_PREVISAO",
				usuario);
		
		int ano = Integer.parseInt(data.substring(0, 4));
		int mes = Integer.parseInt(data.substring(5, 7));
		int dia = 1;
		Date datai = new Date(ano-1900, mes-1, dia, 0, 0, 0);
		if (mes < 12) {
			mes++;
		} else {
			mes = 1;
			ano++;
		}
		Date dataf = new Date(ano-1900, mes-1, dia, 0, 0, 0);
		
		Connection bd = BancoDados.conecta();
		PreparedStatement cmd = bd.prepareStatement(sql);
		cmd.setDate(1, new java.sql.Date(datai.getTime()));
		cmd.setDate(2, new java.sql.Date(dataf.getTime()));
		ResultSet cursor = cmd.executeQuery();
		
		while (cursor.next()) {
			
			Date datPrevisao = new Date(cursor.getDate("DAT_PREVISAO").getTime());
			dia = datPrevisao.getDate();
			if (!dias.contains(dia)) {
				dias.add(dia);
			}
			
		}
		
		cursor.close();
		cmd.close();		
		
		bd.close();
		
		AgendaMes agenda = new AgendaMes();
		agenda.setDias(dias);
		return agenda;
	}
	

	public static String getCliente(Connection bd, String usuario, String data) throws SQLException, NamingException {
		String cliente = null;
		String sql = String.format("select COD_PARCEIRO from AGENDA "
				+ "where COD_USUARIO='%s' "
				+ "and DAT_AGENDAMENTO='%s'",
				usuario, data);

		PreparedStatement cmd = bd.prepareStatement(sql);
		ResultSet cursor = cmd.executeQuery();
		if (cursor.next()) {
			cliente = cursor.getString("COD_PARCEIRO");
		}
		cursor.close();
		cmd.close();
		return cliente;
	}	

	public static void encerra(Encerramento encerramento) throws SQLException, NamingException {
		
		String sql = String.format("update AGENDA set "
				+ " DAT_APP = ? "
				+ ",DES_ENCERRAMENTO = ? "
				+ ",DES_NOME = ? "
				+ ",DES_DOCUMENTO = ? "
				+ ",DES_EMAIL = ? "
				+ ",DES_JSON = ? "
				+ "where COD_USUARIO='%s' "
				+ "and DAT_AGENDAMENTO='%s'",
				encerramento.getUsuario(), encerramento.getData());
		Gson gson = new Gson();
		String json = null;
		if (encerramento.getAssinatura() != null) {
			json = gson.toJson(encerramento.getAssinatura(), Assinatura.class);
		}
		
		Connection bd = BancoDados.conecta();
		PreparedStatement cmd = bd.prepareStatement(sql);
		cmd.setDate(1, new java.sql.Date(new Date().getTime()));
		cmd.setString(2, encerramento.getObservacao());
		cmd.setString(3, encerramento.getNome());
		cmd.setString(4, encerramento.getDocumento());
		cmd.setString(5, encerramento.getEmail());
		cmd.setString(6, json);
		cmd.executeUpdate();
		cmd.close();
		
		bd.close();
	}

}
