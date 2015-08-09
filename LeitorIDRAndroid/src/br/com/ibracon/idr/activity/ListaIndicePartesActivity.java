package br.com.ibracon.idr.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import br.com.ibracon.idr.indice.ResponseIndice;

import com.complab.R;
import com.radaee.reader.PDFReaderAct;

@Deprecated
public class ListaIndicePartesActivity extends MainActivity {

	private ListView listaExibicao;


	private final String PARTE_A = "Parte A";
	private final String PARTE_B = "Parte B";

	String titulos[] = { PARTE_A, PARTE_B };
	
	private ResponseIndice responseIndice = null;
	
	public static final String LISTA_ITENS_INDICE = "br.com.ibracon.idr.activity.LISTA_ITENS_INDICE";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent itent = getIntent();
		Bundle extras = itent.getExtras();

		responseIndice = (ResponseIndice)extras.get(PDFReaderAct.RESPONSE_INDICE_OBJ);
		
		setContentView(R.layout.activity_indice);

		int layout = android.R.layout.simple_expandable_list_item_1;
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, layout,
				titulos);

		listaExibicao = (ListView) findViewById(R.id.listaExibicao);
		listaExibicao.setAdapter(adapter);

		listaExibicao.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> adapter, View view,
					int posicao, long id) {

				// REDIRECIONANDO PARA O DETALHAMENTO DO LIVRO
				Intent intent = new Intent(ListaIndicePartesActivity.this,
						ListaItensIndiceActivity.class);
				
				if(titulos[posicao].equals(PARTE_A)){
					//intent.putExtra(LISTA_ITENS_INDICE, responseIndice.getLivro().getIndice().ParteA);
				}else{
					//intent.putExtra(LISTA_ITENS_INDICE, responseIndice.getLivro().getIndice().ParteB);
				}
				startActivityForResult(intent,1);
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		setResult(1, data);
		finish();
	}

}
