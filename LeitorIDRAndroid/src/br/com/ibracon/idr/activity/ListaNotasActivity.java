package br.com.ibracon.idr.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import br.com.ibracon.idr.model.Livro;
import br.com.ibracon.idr.model.Nota;
import br.com.ibracon.idr.persistence.NotasDAO;

import com.complab.R;
import com.radaee.reader.PDFReaderAct;

public class ListaNotasActivity extends MainActivity {

	private ListView listaExibicao;
	private Livro livro;
	private ArrayList<String> titulos = new ArrayList<String>();
	List<Nota> listaNotas = new ArrayList<Nota>();
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent itent = getIntent();
		Bundle extras = itent.getExtras();

		livro = (Livro) extras.get(PDFReaderAct.LIVRO);

		setContentView(R.layout.activity_lista_select);
		int layout = android.R.layout.simple_expandable_list_item_1;
		
		((TextView)findViewById(R.id.titulo)).setText("Lista de Anotações");

		NotasDAO notasDAO = new NotasDAO(this);
		listaNotas= notasDAO.listaNotasPorCodigoLivro(livro.getCodigolivro());
		
		for(Nota nota: listaNotas){
			titulos.add("(Pág. ".concat(nota.getPagina()).concat(") - ").concat(cortarString(nota.getTitulo(), 10)));
		}
				
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, layout,
				titulos);

		listaExibicao = (ListView) findViewById(R.id.listaExibicao);
		listaExibicao.setAdapter(adapter);

		listaExibicao.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> adapter, View view,
					int posicao, long id) {
				Intent intent = new Intent(ListaNotasActivity.this,
						AnotacoesActivity.class);
				intent.putExtra(AnotacoesActivity.NOTA, listaNotas.get(posicao));
				startActivityForResult(intent, 1);
			}
		});
	}

	
	public String cortarString(String texto, int tamanho){
		if(texto!=null){
			if(texto.length()>tamanho){
				return texto.substring(0, tamanho-1) + "...";
			}
		}
		return texto;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		setResult(resultCode, data);
		finish();
	}

}
