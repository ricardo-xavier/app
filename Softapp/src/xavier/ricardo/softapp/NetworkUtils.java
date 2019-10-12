package xavier.ricardo.softapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtils {
	
	public static boolean verificaConexao(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		boolean conectado = false;
		try {
			conectado = activeNetworkInfo != null && activeNetworkInfo.isConnected();
		} catch (Exception e) {
		}
		return conectado;
	}

}
