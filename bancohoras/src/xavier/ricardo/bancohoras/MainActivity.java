package xavier.ricardo.bancohoras;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class MainActivity extends ActionBarActivity {

	private DBHelper mDbHelper;
	private Date mData;
	private List<Marcacao> mMarcacoes;
	private int mPositivoDia;
	private int mNegativoDia;
	private boolean mNotificacao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		setTitle("Banco de Horas v1.13");
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Intent intent = getIntent();
		mNotificacao = intent.getBooleanExtra("notificacao", false);
		Log.i("BANCOHORAS", String.valueOf(mNotificacao));

		mMarcacoes = new ArrayList<Marcacao>();
		mDbHelper = new DBHelper(this);
		mData = new Date();
		
		atualizaData();
		carregaMarcacoes();
		atualizaResumo();

		MobileAds.initialize(this, "ca-app-pub-0381609228541841~7271883578");
		AdView mAdView = (AdView) findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		mAdView.loadAd(adRequest);
		
	}
	
	private void disparaNotificacao(Date hora) {
		Log.i("BANCOHORAS", "disparaNotificacao:" + hora);
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		Intent notificationIntent = new Intent(getApplicationContext(), NotificationReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, 0);
		Calendar cal = Calendar.getInstance();
		cal.setTime(hora);
		cal.add(Calendar.MINUTE, -5);
		alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
		System.out.println(hora.toString());
	}
	
	private void cancelaNotificacao() {
		Log.i("BANCOHORAS", "cancelaNotificacao");
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		Intent notificationIntent = new Intent(getApplicationContext(), NotificationReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, 0);
		alarmManager.cancel(pendingIntent);
	}

	private void atualizaData() {

		TextView lblData = (TextView) findViewById(R.id.lblData);
		DateFormat df = new SimpleDateFormat("E dd/MM/yyyy");
		lblData.setText(df.format(mData));

	}

	private void atualizaResumo() {

		int mes = mData.getMonth() + 1;
		int ano = mData.getYear() + 1900;
		
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		MarcacaoDao.recalculaMes(db, ano, mes);
		db.close();

		List<String> grid = new ArrayList<String>();

		grid.add("");
		grid.add("Posit");
		grid.add("Negat");
		grid.add("Saldo");

		grid.add("Dia");
		int saldoDia = mPositivoDia - mNegativoDia;
		grid.add(String.format("%02d:%02d", mPositivoDia / 60, mPositivoDia % 60));
		grid.add(String.format("%02d:%02d", mNegativoDia / 60, mNegativoDia % 60));
		if (saldoDia >= 0) {
			grid.add(String.format("%02d:%02d", saldoDia / 60, saldoDia % 60));
		} else {
			int saldo = saldoDia * -1;
			grid.add(String.format("-%02d:%02d", saldo / 60, saldo % 60));
		}

		grid.add("M�s");
		db = mDbHelper.getReadableDatabase();
		Saldo saldoMes = MarcacaoDao.saldoMes(db, ano, mes);
		db.close();
		grid.add(String.format("%02d:%02d", saldoMes.getPositivo() / 60, saldoMes.getPositivo() % 60));
		grid.add(String.format("%02d:%02d", saldoMes.getNegativo() / 60, saldoMes.getNegativo() % 60));
		int saldo = saldoMes.getPositivo() - saldoMes.getNegativo();
		if (saldo >= 0) {
			grid.add(String.format("%02d:%02d", saldo / 60, saldo % 60));
		} else {
			saldo = saldo * -1;
			grid.add(String.format("-%02d:%02d", saldo / 60, saldo % 60));
		}

		grid.add("Quad.");
		db = mDbHelper.getReadableDatabase();
		Saldo saldoQ = MarcacaoDao.saldoQuadrimestre(db, ano, mes);
		db.close();
		grid.add(String.format("%02d:%02d", saldoQ.getPositivo() / 60, saldoQ.getPositivo() % 60));
		grid.add(String.format("%02d:%02d", saldoQ.getNegativo() / 60, saldoQ.getNegativo() % 60));
		saldo = saldoQ.getPositivo() - saldoQ.getNegativo();
		if (saldo >= 0) {
			grid.add(String.format("%02d:%02d", saldo / 60, saldo % 60));
		} else {
			saldo = saldo * -1;
			grid.add(String.format("-%02d:%02d", saldo / 60, saldo % 60));
		}

		GridView gvResumo = (GridView) findViewById(R.id.gvResumo);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, grid);
		gvResumo.setAdapter(adapter);

	}

	private void carregaMarcacoes() {

		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		mMarcacoes = MarcacaoDao.lista(db, mData.getYear() + 1900, mData.getMonth() + 1, mData.getDate());
		db.close();

		Button btnEntrada = (Button) findViewById(R.id.btnEntrada);
		Button btnSaida = (Button) findViewById(R.id.btnSaida);
		TextView lblSair = (TextView) findViewById(R.id.lblSair);
		CheckBox chkFalta = (CheckBox) findViewById(R.id.chkFalta);

		mPositivoDia = 0;
		mNegativoDia = 0;
		btnEntrada.setVisibility(View.GONE);
		btnSaida.setVisibility(View.GONE);
		lblSair.setVisibility(View.GONE);
		chkFalta.setVisibility(View.GONE);

		if (mMarcacoes.size() == 0) {
			chkFalta.setVisibility(View.VISIBLE);
			chkFalta.setChecked(false);
			return;
		}

		if (!mNotificacao) {
			cancelaNotificacao();
		}
		
		if (mMarcacoes.get(0).isFalta()) {
			chkFalta.setVisibility(View.VISIBLE);
			chkFalta.setChecked(true);
			mPositivoDia = 0;
			mNegativoDia = 480;
			return;
		}
		
		// mostra a marca��o de entrada
		btnEntrada.setVisibility(View.VISIBLE);
		btnEntrada.setText(
				"Entrada " + String.format("%02d:%02d", mMarcacoes.get(0).getHora(), mMarcacoes.get(0).getMinuto()));

		int minutosEntrada = mMarcacoes.get(0).getHora() * 60 + mMarcacoes.get(0).getMinuto();
		int minutosTrabalhados = 0;

		if (mMarcacoes.size() > 1) {
			// mostra a marca��o de sa�da
			btnSaida.setVisibility(View.VISIBLE);
			btnSaida.setText(
					"Sa�da " + String.format("%02d:%02d", mMarcacoes.get(1).getHora(), mMarcacoes.get(1).getMinuto()));

			// calcula o tempo trabalhado
			int minutosSaida = mMarcacoes.get(1).getHora() * 60 + mMarcacoes.get(1).getMinuto();
			minutosTrabalhados = minutosSaida - minutosEntrada;
			
		} else {

			// mostra o hor�rio previsto para sa�da
			int minutosSair = minutosEntrada + 540;
			lblSair.setVisibility(View.VISIBLE);
			lblSair.setText(
					"Sair " + String.format("%02d:%02d", minutosSair/60, minutosSair%60));
			
			Date agora = new Date();
			
			// dispara o alarme de sa�da
			Date sair = new Date();
			sair.setHours(minutosSair / 60);
			sair.setMinutes(minutosSair % 60);
			if (!mNotificacao && (sair.getTime() > agora.getTime())) {
				disparaNotificacao(sair);
			}
			
			// calcula o tempo trabalhado at� o momento
			int hora = agora.getHours();
			int minuto = agora.getMinutes();
			int minutosAgora = hora * 60 + minuto;
			minutosTrabalhados = minutosAgora - minutosEntrada;
		}

		// desconta o tempo de almo�o se trabalhou mais de 6 horas
		if (minutosTrabalhados > 360) {
			minutosTrabalhados -= 60;
		}
		
		if (diaUtil(mData)) {
			if (minutosTrabalhados >= 480) {
				mPositivoDia = minutosTrabalhados - 480;
			} else {
				mNegativoDia = 480 - minutosTrabalhados;
			}
		} else {
			mPositivoDia = minutosTrabalhados;
		}
	}

	private boolean diaUtil(Date data) {
		return data.getDay() > 0 && data.getDay() < 6;
	}

	public void marca(View v) {

		if (mMarcacoes.size() == 2) {
			Toast.makeText(this, "J� foram registradas duas marca��es no dia.", Toast.LENGTH_LONG).show();
			return;
		}
		
		Date agora = new Date();

		Marcacao marcacao = new Marcacao();
		marcacao.setAno(mData.getYear() + 1900);
		marcacao.setMes(mData.getMonth() + 1);
		marcacao.setDia(mData.getDate());
		marcacao.setHora(agora.getHours());
		marcacao.setMinuto(agora.getMinutes());
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		MarcacaoDao.inclui(db, marcacao);
		db.close();

		carregaMarcacoes();
		atualizaResumo();
	}

	public void falta(View v) {
		
		boolean checked = ((CheckBox) v).isChecked();
		
		Marcacao marcacao = new Marcacao();
		marcacao.setAno(mData.getYear() + 1900);
		marcacao.setMes(mData.getMonth() + 1);
		marcacao.setDia(mData.getDate());
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		if (checked) {
			MarcacaoDao.incluiFalta(db, marcacao);
		} else {
			MarcacaoDao.excluiFalta(db, marcacao);
		}
		db.close();

		carregaMarcacoes();
		atualizaResumo();
		
	}
	
	public void alteraEntrada(View v) {

		Marcacao marcacao = mMarcacoes.get(0);
		Intent intent = new Intent(this, MarcacaoActivity.class);
		intent.putExtra("hora", marcacao.getHora());
		intent.putExtra("minuto", marcacao.getMinuto());
		intent.putExtra("indice", 0);
		startActivityForResult(intent, 1);
	}

	public void alteraSaida(View v) {

		Marcacao marcacao = mMarcacoes.get(1);
		Intent intent = new Intent(this, MarcacaoActivity.class);
		intent.putExtra("hora", marcacao.getHora());
		intent.putExtra("minuto", marcacao.getMinuto());
		intent.putExtra("indice", 1);
		startActivityForResult(intent, 1);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	
		if (resultCode != RESULT_OK) {
			return;
		}

		String operacao = data.getStringExtra("operacao");
		int indice = data.getIntExtra("indice", 0);

		if (operacao.equals("u")) {
			
            int hora = data.getIntExtra("hora", 0);
            int minuto = data.getIntExtra("minuto", 0);
            Marcacao marcacao = mMarcacoes.get(indice);
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            MarcacaoDao.altera(db, marcacao, hora, minuto);
            db.close();
    		carregaMarcacoes();
    		atualizaResumo();
            return;
			
		}
		
        Marcacao marcacao = mMarcacoes.get(indice);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        MarcacaoDao.exclui(db, marcacao);
        db.close();
		carregaMarcacoes();
		atualizaResumo();
		
	}

    public void ontem(View v) throws ParseException {
        Calendar c = Calendar.getInstance();
        c.setTime(mData);
        c.add(Calendar.DATE, -1);
        mData = c.getTime();
        atualizaData();
        carregaMarcacoes();
        atualizaResumo();
    }

    public void amanha(View v) throws ParseException {
        Calendar c = Calendar.getInstance();
        c.setTime(mData);
        c.add(Calendar.DATE, 1);
        mData = c.getTime();
        atualizaData();
        carregaMarcacoes();
        atualizaResumo();
    }	
}
