package br.com.ibracon.idr.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.complab.R;

public class ListaUfActivity extends MainActivity {

	private ListView listaExibicao;
	public static final String UF = "br.com.ibracon.idr.activity.UF";

	String[] titulos = {"AC","AL","AM","AP","BA","CE","DF","ES","GO","MA","MG","MS","MT","PA","PB","PE","PI","PR","RJ","RN","RO","RR","RS","SC","SE","SP","TO"};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_lista_select);

		((TextView) findViewById(R.id.titulo)).setText("Escolha a UF");

		int layout = android.R.layout.simple_expandable_list_item_1;
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, layout,
				titulos);

		listaExibicao = (ListView) findViewById(R.id.listaExibicao);
		listaExibicao.setAdapter(adapter);

		listaExibicao.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> adapter, View view,
					int posicao, long id) {
				// REDIRECIONANDO PARA O DETALHAMENTO DO LIVRO
				Intent intent = new Intent(ListaUfActivity.this,
						RegistroNaoAssociadoActivity.class);
				intent.putExtra(UF, titulos[posicao]);
				setResult(1,intent);
				finish();
			}
		});
	}
}
