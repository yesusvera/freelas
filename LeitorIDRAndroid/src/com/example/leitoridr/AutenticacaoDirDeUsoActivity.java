package com.example.leitoridr;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import br.com.ibracon.idr.activity.ListaEstantesActivity;
import br.com.ibracon.idr.activity.MainActivity;

import com.complab.R;

public class AutenticacaoDirDeUsoActivity extends MainActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_autenticacao_dir_de_uso);
		setTitle("IBRACON - PALAVRA-CHAVE");
	}

	
	public void autenticar(View view){
		EditText palavraChaveText = (EditText) findViewById(R.id.palavraChaveText);
		EditText senhaText = (EditText) findViewById(R.id.senhaText);
		
		if(palavraChaveText.getText().toString().isEmpty()){
			Toast.makeText(getApplicationContext(), "Digite a palavra-chave para prosseguir", Toast.LENGTH_SHORT).show();
			return;
		}

		if(senhaText.getText().toString().isEmpty()){
			Toast.makeText(getApplicationContext(), "Digite a senha para prosseguir", Toast.LENGTH_SHORT).show();
			return;
		}
		
		Intent intent = new Intent(AutenticacaoDirDeUsoActivity.this, ListaEstantesActivity.class);
		Bundle parametros = new Bundle();
	
		parametros.putSerializable("conectarDireitoDeUso", true);
		parametros.putSerializable("palavraChave", palavraChaveText.getText().toString());
		parametros.putSerializable("senha", senhaText.getText().toString());
		
		intent.putExtras(parametros);
		
		startActivity(intent);
	}
	
}
