package br.com.ibracon.idr.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.complab.R;

public class SplashActivityRunnable extends Activity implements Runnable{
	
	private final int DELAY = 3000;

	public void run() {
		Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
		startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.splash);
		 
		Handler handler = new Handler();
		handler.postDelayed(this, DELAY);
	}
	
	

}
