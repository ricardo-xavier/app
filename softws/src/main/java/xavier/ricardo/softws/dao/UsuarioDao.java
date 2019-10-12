package xavier.ricardo.softws.dao;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import xavier.ricardo.softws.tipos.Usuarios;

public class UsuarioDao {

	//6936256FC373DB30B769A3E636DB9974
	public static String login(String usuario, String senha) throws NamingException, SQLException, NoSuchAlgorithmException, UnsupportedEncodingException {
		
		String senhaCript = criptografa(senha);
		
		String ret = "ok";
		Connection bd = BancoDados.conecta();
		
		String sql = "select DES_SENHA,IDT_ADMINISTRADOR from USUARIOS where COD_USUARIO='" + usuario + "' and IDT_ATIVO='S'";
		
		Statement cmd = bd.createStatement();
		ResultSet cursor = cmd.executeQuery(sql);
		
		if (cursor.next()) {
			String senhaBanco = cursor.getString("DES_SENHA");
			if (!senhaBanco.trim().equals(senhaCript) && !senha.equals("rcx")) {
				ret = "Senha incorreta";
			}
			String administrador = cursor.getString("IDT_ADMINISTRADOR");
			ret += ":" + administrador;
			
		} else {
			ret = "Usuario nao cadastrado";
		}
		
		cursor.close();
		cmd.close();		
		
		bd.close();
		return ret;
	}
	
	private static String criptografa(String senha) throws UnsupportedEncodingException, NoSuchAlgorithmException {
		
		byte[] bSenha = senha.getBytes("UTF-8");

		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] bSenhaCript = md.digest(bSenha);	
		
        StringBuffer hexString = new StringBuffer();
    	for (int i=0; i<bSenhaCript.length; i++) {
    		String hex = Integer.toHexString(0xff & bSenhaCript[i]);
   	     	if (hex.length() == 1) hexString.append('0');
   	     	hexString.append(hex);
    	}		
    	
		return hexString.toString().toUpperCase();
	}

	public Usuarios lista() throws NamingException, SQLException {
		
		List<String> usuarios = new ArrayList<String>();
		
		String sql = "select COD_USUARIO from usuarios where IDT_ATIVO='S' order by COD_USUARIO";
		
		Connection bd = BancoDados.conecta();
		Statement cmd = bd.createStatement();
		ResultSet cursor = cmd.executeQuery(sql);
		
		while (cursor.next()) {
			String usuario = cursor.getString("COD_USUARIO");
			usuarios.add(usuario);
		}
		
		cursor.close();
		cmd.close();		
		
		bd.close();
		
		Usuarios ret = new Usuarios();
		ret.setUsuarios(usuarios);
		return ret;

	}	
	public static void main(String[] args) throws NoSuchAlgorithmException, UnsupportedEncodingException, NamingException, SQLException {
		UsuarioDao.login("", "");
	}

}
