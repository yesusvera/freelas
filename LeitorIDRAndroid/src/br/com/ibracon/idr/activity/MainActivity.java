package br.com.ibracon.idr.activity;

import android.app.Activity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.complab.R;

public class MainActivity extends Activity{
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.layout.menu_lista_livros, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

//		if (item.getItemId() == 0) {
//			AlertDialog.Builder versao = new AlertDialog.Builder(MainActivity.this);
//			versao.setTitle("Sobre");
//			versao.setMessage("Versão 1.0.0");
//			versao.setNeutralButton("OK", null);
//			versao.show();
//
//		}
		return super.onOptionsItemSelected(item);
	}
	
	protected Activity getInstance(){
		return this;
	}
}
