package xavier.ricardo.bancohoras;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MarcacaoDao {
	
	public static List<Marcacao> lista(SQLiteDatabase db, int ano, int mes, int dia) {
		
		List<Marcacao> marcacoes = new ArrayList<Marcacao>();

		Cursor res = db.query("MARCACOES", 
				new String[] { "HORA", "MINUTO" }, 
				"ANO = ? and MES = ? and DIA = ?", 
				new String[] { String.valueOf(ano), String.valueOf(mes), String.valueOf(dia) }, 
				null,
				null, 
				"HORA");
		res.moveToFirst();

		while (!res.isAfterLast()) {
			Marcacao marcacao = new Marcacao();
			int hora = res.getInt(res.getColumnIndex("HORA"));
			int minuto = res.getInt(res.getColumnIndex("MINUTO"));
			marcacao.setAno(ano);
			marcacao.setMes(mes);
			marcacao.setDia(dia);
			marcacao.setHora(hora);
			marcacao.setMinuto(minuto);
			marcacoes.add(marcacao);
			res.moveToNext();
		}
		res.close();

		return marcacoes;
	}
	
	@SuppressWarnings("deprecation")
	public static Saldo recalculaMes(SQLiteDatabase db, int ano, int mes) {
		
		int positivo = 0;
		int negativo = 0;
		
		Cursor res = db.query("MARCACOES", 
				new String[] { "DIA", "HORA", "MINUTO" }, 
				"ANO = ? and MES = ?", 
				new String[] { String.valueOf(ano), String.valueOf(mes) }, 
				null,
				null, 
				"DIA, HORA, MINUTO" );
		res.moveToFirst();
		
		Marcacao marcacaoAnterior = null;

		while (!res.isAfterLast()) {
			Marcacao marcacao = new Marcacao();
			int dia = res.getInt(res.getColumnIndex("DIA"));
			int hora = res.getInt(res.getColumnIndex("HORA"));
			int minuto = res.getInt(res.getColumnIndex("MINUTO"));
			marcacao.setAno(ano);
			marcacao.setMes(mes);
			marcacao.setDia(dia);
			marcacao.setHora(hora);
			marcacao.setMinuto(minuto);
			
			if (marcacaoAnterior != null) {
				if ((marcacao.getAno() == marcacaoAnterior.getAno())
						&& (marcacao.getMes() == marcacaoAnterior.getMes())
						&& (marcacao.getDia() == marcacaoAnterior.getDia())) {
					int minutosEntrada = marcacaoAnterior.getHora() * 60 + marcacaoAnterior.getMinuto();
					int minutosSaida = marcacao.getHora() * 60 + marcacao.getMinuto();
					int minutosTrabalhados = minutosSaida - minutosEntrada;
					if (diaUtil(marcacao)) {
						if (minutosTrabalhados >= 540) {
							positivo += (minutosTrabalhados - 540);
						} else {
							if (minutosTrabalhados > 360) {
								// só desconta almoço se tiver mais de 6 horas trabalhadas
								negativo += (540 - minutosTrabalhados);
							} else {
								negativo += (480 - minutosTrabalhados);
							}
						}						
					} else {
						positivo += minutosTrabalhados;
					}
					marcacaoAnterior = null;
					res.moveToNext();
					continue;
				} else {
					
					// o dia anterior nao tem saida
					negativo += 480;
					marcacaoAnterior = marcacao;
					res.moveToNext();
					continue;
					
				}
			}
			
			marcacaoAnterior = new Marcacao();
			marcacaoAnterior.setAno(ano);
			marcacaoAnterior.setMes(mes);
			marcacaoAnterior.setDia(dia);
			marcacaoAnterior.setHora(hora);
			marcacaoAnterior.setMinuto(minuto);
			
			res.moveToNext();
		}
		res.close();
		
		if (marcacaoAnterior != null) {
			// o ultimo dia nao tem saida
			Date agora = new Date();
			int hora = agora.getHours();
			int minuto = agora.getMinutes();
			int minutosAgora = hora * 60 + minuto;
			int minutosEntrada = marcacaoAnterior.getHora() * 60 + marcacaoAnterior.getMinuto();
			int minutosTrabalhados = minutosAgora - minutosEntrada;
			negativo += (480 - minutosTrabalhados);
			
		}

		Saldo saldo = new Saldo();
		saldo.setPositivo(positivo);
		saldo.setNegativo(negativo);
		
		excluiMes(db, ano, mes);
		incluiMes(db, ano, mes, saldo);
		
		return saldo;
	}
	
	@SuppressWarnings("deprecation")
	private static boolean diaUtil(Marcacao marcacao) {
		Date data = new Date(marcacao.getAno()-1900, marcacao.getMes()-1, marcacao.getDia());
		return data.getDay() > 0 && data.getDay() < 6;
	}

	public static Saldo saldoMes(SQLiteDatabase db, int ano, int mes) {
		
		int positivo = 0;
		int negativo = 0;

		Cursor res = db.query("MENSAL", 
				new String[] { "POSITIVO", "NEGATIVO" }, 
				"ANO = ? and MES = ?", 
				new String[] { String.valueOf(ano), String.valueOf(mes) }, 
				null,
				null, 
				null);
		res.moveToFirst();

		if (!res.isAfterLast()) {
			positivo = res.getInt(res.getColumnIndex("POSITIVO"));
			negativo = res.getInt(res.getColumnIndex("NEGATIVO"));
		}
		res.close();

		Saldo saldo = new Saldo();
		saldo.setPositivo(positivo);
		saldo.setNegativo(negativo);
		return saldo;
	}

	@SuppressWarnings("deprecation")
	public static Saldo saldoQuadrimestre(SQLiteDatabase db, int ano, int mes) {
		
		int positivo = 0;
		int negativo = 0;
		
		int q = (mes - 1) / 4;
		int mesIni = q * 4 + 1;
		
		Date hoje = new Date();
		int anoAtual = hoje.getYear() + 1900;
		int mesAtual = hoje.getMonth() + 1;
		
		for (int m = mesIni; m < mesIni + 4; m++) {
			Saldo saldoMes = saldoMes(db, ano, m);
			if ((ano == anoAtual) && (m == mesAtual)) {
				positivo += saldoMes.getPositivo();
			} else {
				positivo += saldoMes.getPositivo() * 1.25;
			}
			negativo += saldoMes.getNegativo();
		}
		
		Saldo saldo = new Saldo();
		saldo.setPositivo(positivo);
		saldo.setNegativo(negativo);
		return saldo;
		
	}
	
	public static void inclui(SQLiteDatabase db, Marcacao marcacao) {
		
		ContentValues contentValues = new ContentValues();
		contentValues.put("ANO", marcacao.getAno());
		contentValues.put("MES", marcacao.getMes());
		contentValues.put("DIA", marcacao.getDia());
		contentValues.put("HORA", marcacao.getHora());
		contentValues.put("MINUTO", marcacao.getMinuto());
		db.insert("MARCACOES", null, contentValues);
		
		recalculaMes(db, marcacao.getAno(), marcacao.getMes());
		
	}
	
	public static void incluiMes(SQLiteDatabase db, int ano, int mes, Saldo saldo) {
		
		ContentValues contentValues = new ContentValues();
		contentValues.put("ANO", ano);
		contentValues.put("MES", mes);
		contentValues.put("POSITIVO", saldo.getPositivo());
		contentValues.put("NEGATIVO", saldo.getNegativo());
		db.insert("MENSAL", null, contentValues);
		
	}

	public static void altera(SQLiteDatabase db, Marcacao marcacao,
			int hora, int minuto) {
		
		ContentValues contentValues = new ContentValues();
		
		contentValues.put("HORA", hora);
		contentValues.put("MINUTO", minuto);
		
		db.update("MARCACOES", 
				contentValues, 
				"ANO = ? and MES = ? and DIA = ? and HORA = ? and MINUTO = ?",
				new String[] { 
						String.valueOf(marcacao.getAno()),
						String.valueOf(marcacao.getMes()),
						String.valueOf(marcacao.getDia()),
						String.valueOf(marcacao.getHora()),
						String.valueOf(marcacao.getMinuto())
						});
		
		recalculaMes(db, marcacao.getAno(), marcacao.getMes());
	}

	public static void exclui(SQLiteDatabase db, Marcacao marcacao) {
		
		db.delete("MARCACOES", 
				"ANO = ? and MES = ? and DIA = ? and HORA = ? and MINUTO = ?", 
				new String[] { 
						String.valueOf(marcacao.getAno()),
						String.valueOf(marcacao.getMes()),
						String.valueOf(marcacao.getDia()),
						String.valueOf(marcacao.getHora()),
						String.valueOf(marcacao.getMinuto())
						});
		recalculaMes(db, marcacao.getAno(), marcacao.getMes());
	}

	public static void excluiMes(SQLiteDatabase db, int ano, int mes) {
		
		db.delete("MENSAL", 
				"ANO = ? and MES = ?", 
				new String[] { 
						String.valueOf(ano),
						String.valueOf(mes)
						});
		
	}
	
}
