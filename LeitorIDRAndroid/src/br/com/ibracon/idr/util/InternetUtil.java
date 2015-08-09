package br.com.ibracon.idr.util;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * 
 * @author yesus
 *
 * Classe útil para verificar status de rede.
 */
public class InternetUtil {

	public static boolean possuiConexaoInternet(Context context){
		ConnectivityManager connectivityManager = 
					(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		return  connectivityManager.getActiveNetworkInfo()!=null	&&
				connectivityManager.getActiveNetworkInfo().isAvailable() &&
				connectivityManager.getActiveNetworkInfo().isConnected();
	}
}
