package br.com.ibracon.idr.activity;

import static br.com.ibracon.idr.util.ConditionsUtil.isNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import br.com.ibracon.idr.model.Livro;
import br.com.ibracon.idr.persistence.LivrosBaixadosDAO;
import br.com.ibracon.idr.persistence.RegistroDAO;
import br.com.ibracon.idr.persistence.RespostaDAO;
import br.com.ibracon.idr.util.InternetUtil;
import br.com.ibracon.idr.webservice.ConnectionWS;
import br.com.ibracon.idr.webservice.ResponseWS;
import br.com.ibracon.idr.webservice.estante.ConnectionEstante;
import br.com.ibracon.idr.webservice.estante.RequestEstante;
import br.com.ibracon.idr.webservice.estante.ResponseEstante;

import com.complab.R;
import com.example.leitoridr.AutenticacaoDirDeUsoActivity;

public class ListaEstantesActivity extends MainActivity implements OnClickListener {

	ResponseWS responseWS;

	ConnectionEstante connectionEstante = new ConnectionEstante();

	RequestEstante estante = new RequestEstante();

	public static ResponseEstante responseEstante = new ResponseEstante();
	
	public static RequestEstante requestEstante;

	public static List<Livro> listaTodosLivros = new ArrayList<Livro>();
	List<Livro> listaTodosDisponiveis = new ArrayList<Livro>();
	List<Livro> listaTodosDeDireito = new ArrayList<Livro>();
	List<Livro> listaTodosBaixados = new ArrayList<Livro>();
	
	private Boolean conectarDireitoDeUso = false;
	private String palavraChave;
	private String senha;

	private ListView listaBiblioteca;

	private AlertDialog dialogConfirm;
	
	private boolean conectouEstantes;
	
	@Override
	protected void onResume() {
		super.onResume();
		
		atualizar();
		
		conectouEstantes = false;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_lista_estante);

		dialogConfirm = criarConfirmacao();
		
		/**SOMENTE PARA O DIREITO DE USO**/
		Bundle bundle = ListaEstantesActivity.this.getIntent().getExtras();
		if(bundle!=null && bundle.containsKey("conectarDireitoDeUso")){
			conectarDireitoDeUso = bundle.getBoolean("conectarDireitoDeUso");
			palavraChave = bundle.getString("palavraChave");
			senha = bundle.getString("senha");
		}
		/*************************************/
		listaBiblioteca = (ListView) findViewById(R.id.listaEstante);
		
		listaBiblioteca.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> adapter, View view,
					int posicao, long id) {
				if(posicao==2){
					startActivity(new Intent(ListaEstantesActivity.this, AutenticacaoDirDeUsoActivity.class));
				}else{
					exibirListaLivros(posicao);
				}
			}
		});
		
		if(InternetUtil.possuiConexaoInternet(this)){
			zerarListas();
			conectouEstantes = true;
			new ListaEstantesTask().execute(estante);
		}else{
			Toast.makeText(ListaEstantesActivity.this, "Erro ao conectar - Parece que seu dispositivo está sem acesso a internet.", Toast.LENGTH_LONG).show();
		}

	}

	private void zerarListas() {
		listaTodosLivros = new ArrayList<Livro>();
		listaTodosDisponiveis = new ArrayList<Livro>();
		listaTodosDeDireito = new ArrayList<Livro>();
	}

	public void atualizar(){
		if(InternetUtil.possuiConexaoInternet(this)){
			zerarListas();
			conectouEstantes = true;
			new ListaEstantesTask().execute(estante);
		}else{
			carregaEstantes();
			Toast.makeText(ListaEstantesActivity.this, "Erro ao conectar - Parece que seu dispositivo está sem acesso a internet.", Toast.LENGTH_LONG).show();
			configurarListView();	
			conectouEstantes = false;
		}
	}
	
	public void atualizar(View view){
		atualizar();
	}
	
	@Override
	public void onBackPressed() {
		dialogConfirm.show();
	}
	
	private AlertDialog criarConfirmacao() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setMessage("Deseja encerrar o Leitor Ibracon?");
		builder.setPositiveButton("Sim", this);
		builder.setNegativeButton("Não", this);

		return builder.create();
	}
	
	public void exibirListaLivros(int posicao) {
		
		Boolean abrirLivro = false;

		Intent intent = new Intent(ListaEstantesActivity.this, ListaLivrosActivity.class);
		Bundle parametros = new Bundle();
		
		switch (posicao) {
		case 0:
//			for (Livro livroTemp : listaTodosLivros) {
//				parametros.putSerializable(livroTemp.getCodigolivro(), livroTemp);
//				parametros.putSerializable("tituloEstante", "Estante - Visão Geral");
//			}
			adicionaParametrosListaDeDireito(parametros);
			adicionaParametrosListaDisponiveis(parametros);
			adicionaParametrosListaBaixados(parametros);
			break;
		case 1:
			adicionaParametrosListaDisponiveis(parametros);
			break;
		case 2:
			adicionaParametrosListaDeDireito(parametros);
			break;
		case 3:
			adicionaParametrosListaBaixados(parametros);
			abrirLivro = true;
			break;
		}
		parametros.putSerializable("abrirLivro", abrirLivro);
		intent.putExtras(parametros);
		startActivity(intent);
	}

	private void adicionaParametrosListaBaixados(Bundle parametros) {
		for (Livro livroTemp : listaTodosBaixados) {
			parametros.putSerializable(livroTemp.getCodigolivro(), livroTemp);
			parametros.putSerializable("tituloEstante", "Estante - Minha Biblioteca");
		}
	}

	private void adicionaParametrosListaDisponiveis(Bundle parametros) {
		for (Livro livroTemp : listaTodosDisponiveis) {
			parametros.putSerializable(livroTemp.getCodigolivro(), livroTemp);
			parametros.putSerializable("tituloEstante", "Estante - Disponíveis");
		}
	}

	private void adicionaParametrosListaDeDireito(Bundle parametros) {
		for (Livro livroTemp : listaTodosDeDireito) {
			parametros.putSerializable(livroTemp.getCodigolivro(), livroTemp);
			parametros.putSerializable("tituloEstante", "Estante - Direito de uso");
		}
	}

	public void carregaEstantes() {
		zerarListas();
		
		listarTodosBaixados();
		listarTodosDeDireito();
		listarTodosParaBaixar();
	}
	
	private int qteLivrosBaixadosSemRepeticao(){
		HashMap<String, Livro> listaLivrosBaixadosNãoRepetidos = new HashMap<String, Livro>();
		for (Livro livroTemp : listaTodosBaixados) {
			listaLivrosBaixadosNãoRepetidos.put(livroTemp.getCodigolivro(), livroTemp);
		}
		
		return listaLivrosBaixadosNãoRepetidos.size();
	}
	
	private void configurarListView() {
		
		
		String[] estantes = { "Visão Geral (" + (listaTodosDisponiveis.size()+qteLivrosBaixadosSemRepeticao())+ " livros)", "Disponíveis (" +listaTodosDisponiveis.size()+" livros)", "Direito de uso",
		"Minha Biblioteca (" + qteLivrosBaixadosSemRepeticao() + " livros)" };
		
		int layout = android.R.layout.simple_list_item_1;
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(ListaEstantesActivity.this, layout, estantes);
		
		listaBiblioteca.setAdapter(adapter);
	}

	public List<Livro> listarTodosDeDireito() {
		if (!isNull(responseEstante) && !isNull(responseEstante.dedireito)) {
			for (Livro livro : responseEstante.dedireito) {
				listaTodosDeDireito.add(livro);
				listaTodosLivros.add(livro);
			}
			return listaTodosDeDireito;
		} else {
			return null;
		}
	}

	public List<Livro> listarTodosParaBaixar() {
		if (!isNull(responseEstante) && !isNull(responseEstante.parabaixar)) {
			for (Livro livro : responseEstante.parabaixar) {
				listaTodosDisponiveis.add(livro);
				listaTodosLivros.add(livro);
			}
			return listaTodosDisponiveis;
		} else {
			return null;
		}
	}

	public List<Livro> listarTodosBaixados() {
		LivrosBaixadosDAO livrosBaixadosDAO = new LivrosBaixadosDAO(getApplicationContext());
		//PARA A LISTA DE BAIXADOS DO ANDROID EU LISTO SOMENTE OS QUE EU GRAVEI NO BANCO.
		listaTodosBaixados =  livrosBaixadosDAO.listaLivrosBaixados();
		livrosBaixadosDAO.close();
		
		if(listaTodosBaixados!=null){
			for(Livro lvTmp : listaTodosBaixados){
				listaTodosLivros.add(lvTmp);
			}
		}
		
		return listaTodosBaixados;
	}

	private class ListaEstantesTask extends
			AsyncTask<RequestEstante, Void, Void> {

		ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
			progressDialog = new ProgressDialog(ListaEstantesActivity.this);
			progressDialog.setTitle("Estantes");
			progressDialog.setMessage("Conectando ao serviço das estantes...");
			progressDialog.show();
		}

		@Override
		protected void onPostExecute(Void result) {
			progressDialog.dismiss();
			
			carregaEstantes();
			configurarListView();
			
			if(conectarDireitoDeUso){
				exibirListaLivros(2);
			}
		}

		

		@Override
		protected Void doInBackground(RequestEstante... params) {
			requestEstante = params[0];

			RespostaDAO resposta = new RespostaDAO(ListaEstantesActivity.this);
			RegistroDAO registro = new RegistroDAO(ListaEstantesActivity.this);

			if (resposta.listarRespostasRegistro() != null
					&& resposta.listarRespostasRegistro().get(0) != null
					&& registro.listarRequisicaoRegistro() != null
					&& registro.listarRequisicaoRegistro().get(0) != null) {
				requestEstante.setCliente(resposta.listarRespostasRegistro().get(0)
						.getCodCliente());
				requestEstante.setDocumento(registro.listarRequisicaoRegistro().get(0)
						.getDocumento());
				requestEstante.setDispositivo(resposta.listarRespostasRegistro()
						.get(0).getCodDispositivo());
				
				
				if(conectarDireitoDeUso){
					requestEstante.setKeyword(palavraChave);
					requestEstante.setSenha(senha);
				}
				
			}

			try {
				responseWS = connectionEstante.serviceConnect(requestEstante,
						ConnectionWS.WS_ESTANTES);
				responseEstante = (ResponseEstante) responseWS;
				removerLivrosRevogados();
				
				Log.i("Resposta", responseEstante.toString());
			} catch (Exception e) {
				conectouEstantes = false;
				Log.i("info", e.getMessage());
			}
			return null;
		}
	}

	public void removerLivrosRevogados(){
		LivrosBaixadosDAO livrosBaixadosDAO = new LivrosBaixadosDAO(getApplicationContext());
		listaTodosBaixados =  livrosBaixadosDAO.listaLivrosBaixados();
		for(Livro lvrTmp: listaTodosBaixados){
			if(livroEstaRevogado(lvrTmp)){
				livrosBaixadosDAO.excluirPorId(lvrTmp.getCodigolivro());
			}
		}
	}
	
	//Comparando se o livro local ainda está na prateleira de baixados online.
	public boolean livroEstaRevogado(Livro livro){
		if(responseEstante==null){
			return false;
		}
		boolean flagRevogado = true;
		
		List<Livro> livrosBaixadosOnline = responseEstante.baixados;
		for(Livro lvrTmp: livrosBaixadosOnline){
			if(lvrTmp!=null &&
				lvrTmp.getCodigolivro().equals(livro.getCodigolivro())){
				flagRevogado = false;
			}
				
		}
		
		return flagRevogado;
	}
	
	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
		case DialogInterface.BUTTON_POSITIVE:
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			break;
		default:
			break;
		}
		dialogConfirm.dismiss();
	}
}
