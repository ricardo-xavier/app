package xavier.ricardo.bancohoras;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TimePicker;

@SuppressWarnings("deprecation")
public class MarcacaoActivity extends ActionBarActivity {

	private TimePicker mTpHora;
	private int mIndice;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_marcacao);

		setResult(RESULT_CANCELED);

		Intent intent = getIntent();
		int hora = intent.getIntExtra("hora", 0);
		int minuto = intent.getIntExtra("minuto", 0);
		mIndice = intent.getIntExtra("indice", 0);

		mTpHora = (TimePicker) findViewById(R.id.tpHora);
		mTpHora.setCurrentHour(hora);
		mTpHora.setCurrentMinute(minuto);

	}

	public void gravar(View v) {
		Intent retorno = new Intent();
		retorno.putExtra("hora", mTpHora.getCurrentHour());
		retorno.putExtra("minuto", mTpHora.getCurrentMinute());
		retorno.putExtra("operacao", "u");
		retorno.putExtra("indice", mIndice);
		setResult(RESULT_OK, retorno);
		finish();
	}

	public void excluir(View v) {
		new AlertDialog.Builder(this).setTitle("Confirmação").setMessage("Excluir a marcação?")
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						Intent retorno = new Intent();
						retorno.putExtra("operacao", "d");
						retorno.putExtra("indice", mIndice);
						setResult(RESULT_OK, retorno);
						finish();
					}
				}).setNegativeButton(android.R.string.no, null).show();
	}
}
