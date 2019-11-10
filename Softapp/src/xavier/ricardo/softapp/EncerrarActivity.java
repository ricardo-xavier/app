package xavier.ricardo.softapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import xavier.ricardo.softapp.tasks.EncerramentoTask;

public class EncerrarActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_encerrar);
		
		Intent intent = getIntent();
		String chave = intent.getStringExtra("chave");
		System.out.println(chave);
		String[] partes = chave.split(";");
		String observacao = partes.length > 2 ? partes[2] : "";

		EditText etObservacao = (EditText) findViewById(R.id.etObservacao);
		etObservacao.setText(observacao);
	}
	
	public void confirma(View v) {
		
		Intent intent = getIntent();
		String chave = intent.getStringExtra("chave");
		System.out.println(chave);
		String[] partes = chave.split(";");
		String usuario = partes[0];
		String data = partes[1];
		
		EditText etObservacao = (EditText) findViewById(R.id.etObservacao);
		String observacao = etObservacao.getText().toString();
		
		new EncerramentoTask(this, usuario, data, observacao).execute();
	}	
	
	public void cancela(View v) {
		finish();
	}

	public void limpa(View v) {
		DrawView assinatura = (DrawView) findViewById(R.id.view1);
		assinatura.limpa();
	}

	public void resultado(String result) {
		
		if (result.equals("ok")) {
			Toast.makeText(this, "Agendamento encerrado com sucesso", Toast.LENGTH_LONG).show();
			Intent intent = getIntent();
			
			String chave = intent.getStringExtra("chave");
			System.out.println(chave);
			String[] partes = chave.split(";");
			String usuario = partes[0];
			String data = partes[1];
			
			EditText etObservacao = (EditText) findViewById(R.id.etObservacao);
			String observacao = etObservacao.getText().toString();
			chave = usuario + ";" + data + ";" + observacao;
			
			Intent resultIntent = new Intent();
			resultIntent.putExtra("chave", chave);
 			
			setResult(RESULT_OK, resultIntent);
			
		} else {
			Toast.makeText(this, "ERRO:" + result, Toast.LENGTH_LONG).show();
			setResult(RESULT_CANCELED);
		}
		
		finish();		
	}	
	

}
