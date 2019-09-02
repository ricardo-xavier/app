package xavier.ricardo.bancohoras;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context contexto) {
        super(contexto, "bancohoras", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    	
        db.execSQL("create table MARCACOES (ANO integer not null, "
        		+ "MES integer not null, "
        		+ "DIA integer not null, "
        		+ "HORA integer not null, "
        		+ "MINUTO integer not null, "
                + "primary key (ANO, MES, DIA, HORA, MINUTO))");

        db.execSQL("create table MENSAL (ANO integer not null, "
                + "MES integer not null, "
                + "POSITIVO integer not null, "
                + "NEGATIVO integer not null, "
                + "primary key (ANO, MES))");
        
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

    }
}
