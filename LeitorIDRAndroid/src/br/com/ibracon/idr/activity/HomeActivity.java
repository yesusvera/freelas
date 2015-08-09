package br.com.ibracon.idr.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import br.com.ibracon.idr.persistence.RegistroDAO;
import br.com.ibracon.idr.util.ConstantesIDR;
import br.com.ibracon.idr.util.FileUtil;

import com.complab.R;


public class HomeActivity extends MainActivity {
	
	RegistroDAO registroDAO = new RegistroDAO(this);

	@Override
	public void onCreate(Bundle savedInstanceState) {		
 		super.onCreate(savedInstanceState);
 		
 		//CONFIGURANDO PASTA QUE O APLICATIVO IRÁ UTILIZAR PARA ARMAZENAR OS DADOS.
 		FileUtil.criarPasta(ConstantesIDR.PATH_IBRACON);
 		
 		int i = registroDAO.listarRequisicaoRegistro().size();
 		
		if(i == 0){
			setContentView(R.layout.activity_home);
		}else{
			Intent intent = new Intent(this, ListaEstantesActivity.class);
			startActivity(intent);
		}
		
	}

	public void solicitarAssociado(View v){
		Intent intent = new Intent(this, RegistroAssociadoActivity.class);
		startActivity(intent);
	}
	
	public void solicitarNaoAssociado(View v){
		Intent intent = new Intent(this, RegistroNaoAssociadoActivity.class);
		startActivity(intent);
	}

}
