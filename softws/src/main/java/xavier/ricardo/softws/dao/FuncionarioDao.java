package xavier.ricardo.softws.dao;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.NamingException;

import xavier.ricardo.softws.tipos.Funcionario;

public class FuncionarioDao {

	public static Funcionario get(String usuario, Connection bd) throws NamingException, SQLException, NoSuchAlgorithmException, UnsupportedEncodingException {
		
		Funcionario func = null;
		boolean fechar = false;
		if (bd == null) {
			bd = BancoDados.conecta();
			fechar = true;
		}
		
		String sql = "select NOM_FUNCIONARIO,NRO_FONE3 from FUNCIONARIOS where COD_FUNCIONARIO='" + usuario + "' and IDT_ATIVO='S'";
		
		Statement cmd = bd.createStatement();
		ResultSet cursor = cmd.executeQuery(sql);
		
		if (cursor.next()) {
			func = new Funcionario();
			String nome = cursor.getString("NOM_FUNCIONARIO");
			String fone = cursor.getString("NRO_FONE3");
			func.setNome(nome);
			func.setFone(fone);
		}
		
		cursor.close();
		cmd.close();		
		
		if (fechar) {
			bd.close();
		}
		return func;
	}

}
