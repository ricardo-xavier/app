package xavier.ricardo.softws.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;

public class BancoDados {
	
    public static Connection conecta() throws NamingException, SQLException {
    	Connection bd = null;
    	if (System.getenv("ECLIPSE") == null) {
    		InitialContext ctx = new InitialContext();
    		BasicDataSource ds = (BasicDataSource) ctx.lookup("java:comp/env/jdbc/softplace");
    		bd =  ds.getConnection();
    	} else {
            String driver = "org.firebirdsql.jdbc.FBDriver";
            try {
				Class.forName(driver);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
            String url = "jdbc:firebirdsql://softplacemoveis.dyndns.org:3050/softplace/SISTEMA/producao/softplace_bh.fdb?encoding=ISO8859_1";
            bd = DriverManager.getConnection(url, "sysdba", "masterkey");
    		
    	}
    	bd.setAutoCommit(true);
        return bd;
    }
    
}
	