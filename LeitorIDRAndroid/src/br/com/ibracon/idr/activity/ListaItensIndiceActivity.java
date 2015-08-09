package br.com.ibracon.idr.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import br.com.ibracon.idr.indice.Item;

import com.complab.R;
import com.radaee.reader.PDFReaderAct;

public class ListaItensIndiceActivity extends MainActivity {

	private ListView listaExibicao;

	ArrayList<String> titulos = new ArrayList<String>();
	
	public ArrayList<Item> listaItens;
	
	private static final String LISTA_ITENS_INDICE = "br.com.ibracon.idr.activity.LISTA_ITENS_INDICE";

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent itent = getIntent();
		Bundle params = itent.getExtras();

		listaItens = (ArrayList<Item>)params.get(EscolherListaActivity.LISTA_ITENS_INDICE);
		
		if(listaItens != null && listaItens.size()>0){
			for(Item item: listaItens){
				String lbl = item.toString();
				if(item.getItens()!=null && item.getItens().size()> 0){
					lbl = lbl.concat(" -->");
				}
				titulos.add(lbl.concat("                                                        " +
									   "                                                        "));
			}
		}
		
		setContentView(R.layout.activity_indice);

		int layout = android.R.layout.simple_expandable_list_item_1;
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, layout,
				titulos);

		listaExibicao = (ListView) findViewById(R.id.listaExibicao);
		listaExibicao.setAdapter(adapter);

		listaExibicao.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> adapter, View view,
					int posicao, long id) {

				Item item = listaItens.get(posicao);
				if(item.getItens()== null || item.getItens().size()<1){
					Intent data = new Intent();
					data.putExtra(PDFReaderAct.NUMERO_PAGINA, Integer.valueOf(item.getPaginareal()));
					setResult(1, data);
					finish();
				}else{
					// REDIRECIONANDO PARA O DETALHAMENTO DO LIVRO
					Intent intent = new Intent(ListaItensIndiceActivity.this,
							ListaItensIndiceActivity.class);
					intent.putExtra(LISTA_ITENS_INDICE, item.getItens());
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
