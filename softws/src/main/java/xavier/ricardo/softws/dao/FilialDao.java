package xavier.ricardo.softws.dao;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.NamingException;

import xavier.ricardo.softws.tipos.Endereco;

public class FilialDao {

	public static Endereco get(String codigo) throws NamingException, SQLException, NoSuchAlgorithmException, UnsupportedEncodingException {
		
		Endereco ender = null;
		Connection bd = BancoDados.conecta();
		
		String sql = "select DES_LOGRADOURO, NRO_ENDERECO, NOM_BAIRRO, NOM_CIDADE, NRO_CEP, NRO_FONE1 " 
				+ "from FILIAIS where COD_FILIAL='" + codigo + "'";
		
		Statement cmd = bd.createStatement();
		ResultSet cursor = cmd.executeQuery(sql);
		
		if (cursor.next()) {
			ender = new Endereco();
			String rua = cursor.getString("DES_LOGRADOURO");
			String numero = cursor.getString("NRO_ENDERECO");
			String bairro = cursor.getString("NOM_BAIRRO");
			String cidade = cursor.getString("NOM_CIDADE");
			String cep = cursor.getString("NRO_CEP");
			String fone = cursor.getString("NRO_FONE1");
			ender.setRua(rua);
			ender.setNumero(numero);
			ender.setBairro(bairro);
			ender.setCidade(cidade);
			ender.setCep(cep);
			ender.setFone(fone);
		}
		
		cursor.close();
		cmd.close();		
		
		bd.close();
		return ender;
	}

}
