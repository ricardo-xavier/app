package xavier.ricardo.bancohoras;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;

public class NotificationReceiver extends BroadcastReceiver {

	@SuppressWarnings("deprecation")
	@Override
	public void onReceive(Context context, Intent intent) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(3000);
        Intent main = new Intent(context, MainActivity.class);
        main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        main.putExtra("notificacao", true);
        context.startActivity(main);
	}

}
