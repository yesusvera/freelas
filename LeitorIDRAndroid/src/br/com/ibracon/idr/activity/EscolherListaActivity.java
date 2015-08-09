package br.com.ibracon.idr.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import br.com.ibracon.idr.indice.ResponseIndice;
import br.com.ibracon.idr.model.Livro;

import com.complab.R;
import com.radaee.reader.PDFReaderAct;

public class EscolherListaActivity extends MainActivity {

	private ListView listaExibicao;

	private final String LISTA_INDICE = "Índice...";
	private final String LISTA_ANOTACOES = "Anotações...";
	
	public static final String LISTA_ITENS_INDICE = "br.com.ibracon.idr.activity.LISTA_ITENS_INDICE";

	String titulos[] = { LISTA_INDICE, LISTA_ANOTACOES };

	private ResponseIndice responseIndice = null;
	private Livro livro;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent itent = getIntent();
		final Bundle extras = itent.getExtras();

		responseIndice = (ResponseIndice) extras
				.get(PDFReaderAct.RESPONSE_INDICE_OBJ);
		livro = (Livro)extras.get(PDFReaderAct.LIVRO);

		setContentView(R.layout.activity_lista_select);
		
		int layout = android.R.layout.simple_expandable_list_item_1;
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, layout,
				titulos);

		listaExibicao = (ListView) findViewById(R.id.listaExibicao);
		listaExibicao.setAdapter(adapter);

		listaExibicao.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> adapter, View view,
					int posicao, long id) {

				if (titulos[posicao].equals(LISTA_INDICE)) {
					// REDIRECIONANDO PARA O DETALHAMENTO DO LIVRO
					Intent intent = new Intent(EscolherListaActivity.this,
							ListaItensIndiceActivity.class);
					intent.putExtra(LISTA_ITENS_INDICE, responseIndice.getLivro().getListaItens());
					startActivityForResult(intent,1);
				}else if(titulos[posicao].equals(LISTA_ANOTACOES)) {
					Intent intent = new Intent(EscolherListaActivity.this,
							ListaNotasActivity.class);
					intent.putExtra(PDFReaderAct.LIVRO,livro);
					startActivityForResult(intent, 1);
				}
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		setResult(resultCode, data);
		finish();
	}

}
