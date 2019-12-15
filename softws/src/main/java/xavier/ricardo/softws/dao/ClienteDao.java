package xavier.ricardo.softws.dao;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import xavier.ricardo.softws.tipos.Cliente;
import xavier.ricardo.softws.tipos.Contato;
import xavier.ricardo.softws.utils.Sql;

public class ClienteDao {

	public static Cliente get(Connection bd, String codigo) throws SQLException {

		Cliente cliente = null;
		
		String sql = String.format("select NOM_PARCEIRO, NRO_CPF_CNPJ, NRO_INSCRICAO_ESTADUAL, NRO_INSCRICAO_MUNICIPAL, "
			+ "DES_LOGRADOURO, NRO_ENDERECO, DES_COMPLEMENTO, NOM_BAIRRO, NOM_CIDADE, COD_ESTADO, NRO_CEP, "
			+ "DES_LOGRADOURO_ENTREGA, NRO_ENDERECO_ENTREGA, DES_COMPLEMENTO_ENTREGA, NOM_BAIRRO_ENTREGA, NOM_CIDADE_ENTREGA, COD_ESTADO_ENTREGA, NRO_CEP_ENTREGA, "
			+ "NRO_FONE1, NRO_FONE2, NRO_CELULAR, DES_EMAIL "
			+ "from PARCEIROS "
			+ "where COD_PARCEIRO='%s'",
			codigo);

		PreparedStatement cmd = bd.prepareStatement(sql);

		ResultSet cursor = cmd.executeQuery();
	
		if (cursor.next()) {
			
			cliente = new Cliente();
	
			String nomParceiro = Sql.getString(cursor, "NOM_PARCEIRO");
			String cpfCnpj = Sql.getString(cursor, "NRO_CPF_CNPJ");
			String ie = Sql.getString(cursor, "NRO_INSCRICAO_ESTADUAL");
			String im = Sql.getString(cursor, "NRO_INSCRICAO_MUNICIPAL");
			String rua = Sql.getString(cursor, "DES_LOGRADOURO");
			String numero = Sql.getString(cursor, "NRO_ENDERECO");
			String complemento = Sql.getString(cursor, "DES_COMPLEMENTO");
			String bairro = Sql.getString(cursor, "NOM_BAIRRO");
			String cidade = Sql.getString(cursor, "NOM_CIDADE");
			String estado = Sql.getString(cursor, "COD_ESTADO");
			String cep = Sql.getString(cursor, "NRO_CEP");
			String ruaEntrega = Sql.getString(cursor, "DES_LOGRADOURO_ENTREGA");
			String numeroEntrega = Sql.getString(cursor, "NRO_ENDERECO_ENTREGA");
			String complementoEntrega = Sql.getString(cursor, "DES_COMPLEMENTO_ENTREGA");
			String bairroEntrega = Sql.getString(cursor, "NOM_BAIRRO_ENTREGA");
			String cidadeEntrega = Sql.getString(cursor, "NOM_CIDADE_ENTREGA");
			String estadoEntrega = Sql.getString(cursor, "COD_ESTADO_ENTREGA");
			String cepEntrega = Sql.getString(cursor, "NRO_CEP_ENTREGA");
			String fone1 = Sql.getString(cursor, "NRO_FONE1");
			String fone2 = Sql.getString(cursor, "NRO_FONE2");
			String celular = Sql.getString(cursor, "NRO_CELULAR");
			String email = Sql.getString(cursor, "DES_EMAIL");
			
			cliente.setNome(nomParceiro);
			cliente.setCpfCnpj(cpfCnpj);
			cliente.setIe(ie);
			cliente.setIm(im);
			cliente.setEndereco(rua + " - " + numero + " - " + complemento 
					+ " - " + bairro + " - " + cidade + " - " + estado + " - " + cep);
			cliente.setEnderecoEntrega(ruaEntrega + " - " + numeroEntrega + " - " + complementoEntrega 
					+ " - " + bairroEntrega + " - " + cidadeEntrega + " - " + estadoEntrega + " - " + cepEntrega);
			cliente.setFone(fone1 + " - " + fone2 + " - " + celular);
			cliente.setEmail(email);
		}
		
		cursor.close();
		cmd.close();		
		
		return cliente;
		
	}
	
	public static Cliente get(String codigo) throws NamingException, SQLException, NoSuchAlgorithmException, UnsupportedEncodingException {
		Connection bd = BancoDados.conecta();
		Cliente cliente = get(bd, codigo);
		bd.close();
		return cliente;
	}
	
	public static List<Contato> getContatos(Connection bd, String codigo) throws SQLException {
		
		List<Contato> contatos = new ArrayList<Contato>(); 
	
		String sql = String.format("select NOM_CONTATO, DES_PAPEL, "
				+ "NRO_FONE1, NRO_FONE2, NRO_CELULAR, DES_EMAIL "
				+ "from CONTATOS "
				+ "where COD_PARCEIRO='%s'",
				codigo);

		PreparedStatement cmd = bd.prepareStatement(sql);

		ResultSet cursor = cmd.executeQuery();

		while (cursor.next()) {
		
			Contato contato = new Contato();

			String nome = Sql.getString(cursor, "NOM_CONTATO");
			String papel = Sql.getString(cursor, "DES_PAPEL");
			String fone1 = Sql.getString(cursor, "NRO_FONE1");
			String fone2 = Sql.getString(cursor, "NRO_FONE2");
			String celular = Sql.getString(cursor, "NRO_CELULAR");
			String email = Sql.getString(cursor, "DES_EMAIL");
		
			contato.setNome(nome);
			contato.setPapel(papel);
			contato.setFone(fone1 + " - " + fone2 + " - " + celular);
			contato.setEmail(email);
		
			contatos.add(contato);
	
		}
	
		cursor.close();
		cmd.close();
		return contatos;
	}
}
