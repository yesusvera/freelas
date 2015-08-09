package br.com.ibracon.idr.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import br.com.ibracon.idr.model.Livro;
import br.com.ibracon.idr.task.DownloadImageTask;

import com.complab.R;

public class ListaLivrosActivity extends MainActivity {

	private ListView listaExibicao;

	byte[] livroByteArray;

	ArrayList<Livro> listaLivros = new ArrayList<Livro>();
	ArrayList<String> titulos = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent itent = getIntent();
		Bundle params = itent.getExtras();

		setContentView(R.layout.activity_lista_livros);

		((TextView) findViewById(R.id.tituloEstante)).setText(params
				.getString("tituloEstante"));

		Set<String> setKeys = params.keySet();

		for (String key : setKeys) {
			try {
				Object obj = params.get(key);
				if (obj instanceof Livro) {
					Livro livroTemp = (Livro) obj;
					listaLivros.add(livroTemp);
					titulos.add(livroTemp.getTitulo()
								.concat("                                                        " +
										"                                                        "));
				}
			} catch (ClassCastException ce) {
				ce.printStackTrace();
			}
		}

		if (listaLivros.size() == 0) {
			Toast.makeText(ListaLivrosActivity.this,
					"Nenhum livro encontrado nesta estante.", Toast.LENGTH_LONG)
					.show();
		} else {
			 final Boolean abrirLivro = params.getBoolean("abrirLivro");

			 int layout = android.R.layout.simple_expandable_list_item_1;
			 ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
			 layout,
			 titulos);
			 // TODO novo adaptar, ainda implementar.
//			String de[] = { "imagem", "descricao" };
//			int para[] = {R.id.imagem, R.id.descricao};
//
//			SimpleAdapter adapter = new SimpleAdapter(this, listarLivros(),
//					R.layout.list_item_livros, de, para);

			listaExibicao = (ListView) findViewById(R.id.listaExibicao);
			listaExibicao.setAdapter(adapter);

			listaExibicao.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> adapter, View view,
						int posicao, long id) {
					Livro livro = listaLivros.get(posicao);

					// REDIRECIONANDO PARA O DETALHAMENTO DO LIVRO
					Intent intent = new Intent(ListaLivrosActivity.this,
							DetalharLivroActivity.class);
					Bundle parametros = new Bundle();
					parametros.putSerializable(livro.getCodigolivro(), livro);
					parametros.putSerializable("abrirLivro", abrirLivro);
					intent.putExtras(parametros);
					startActivity(intent);
				}
			});

		}
	}
	
	
	//UTILIZADO NO ADAPTAR PERSONALIZADO PARA A LISTVIEW
	public List<Map<String, Object>> listarLivros(){
		 List<Map<String, Object>> lista = new ArrayList<Map<String,Object>>();
		 
		 for(Livro livro: listaLivros){
			 Map<String, Object> item = new HashMap<String, Object>();
			 
			 if (livro.getFoto()!=null){
				ImageView imgView = (ImageView) findViewById(R.id.imagem);
				new DownloadImageTask(imgView).execute(livro.getFoto());
			 }
			 
			 item.put("imagem", livro.getFoto());
			 item.put("descricao", livro.getTitulo());
			 lista.add(item);
		 }
		 
		 return lista;
	}

}
