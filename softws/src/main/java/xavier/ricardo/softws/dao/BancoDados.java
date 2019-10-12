package xavier.ricardo.softws.dao;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;

public class BancoDados {
    public static Connection conecta() throws NamingException, SQLException {
        InitialContext ctx = new InitialContext();
        BasicDataSource ds = (BasicDataSource) ctx.lookup("java:comp/env/jdbc/softplace");
        Connection bd =  ds.getConnection();
        bd.setAutoCommit(true);
        return bd;
    }
}
	