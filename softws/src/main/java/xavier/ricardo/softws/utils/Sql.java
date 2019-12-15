package xavier.ricardo.softws.utils;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Sql {
	
	public static String getString(ResultSet cursor, String nome) throws SQLException {
		String s = cursor.getString(nome);
		return s != null ? s : "";
	}
	

}
