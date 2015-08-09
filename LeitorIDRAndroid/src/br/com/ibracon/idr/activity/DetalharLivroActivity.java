package br.com.ibracon.idr.activity;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.Set;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import br.com.ibracon.idr.indice.ResponseIndice;
import br.com.ibracon.idr.model.Livro;
import br.com.ibracon.idr.persistence.LivrosBaixadosDAO;
import br.com.ibracon.idr.persistence.RegistroDAO;
import br.com.ibracon.idr.persistence.RespostaDAO;
import br.com.ibracon.idr.task.DownloadImageTask;
import br.com.ibracon.idr.util.ConstantesIDR;
import br.com.ibracon.idr.util.FileUtil;
import br.com.ibracon.idr.util.InternetUtil;
import br.com.ibracon.idr.webservice.ConnectionWS;
import br.com.ibracon.idr.webservice.registrar.RequestRegistrar;
import br.com.ibracon.idr.webservice.registrar.ResponseRegistrar;
import br.com.ibracon.idr.webservice.registrarLivro.ConnectionRegistrarLivro;
import br.com.ibracon.idr.webservice.registrarLivro.RequestRegistrarLivro;

import com.complab.R;
import com.radaee.reader.PDFReaderAct;

/**
 * 
 * @author yesus
 * 
 */
public class DetalharLivroActivity extends MainActivity implements
		OnClickListener {

	private Livro livro, livroTmpNovaVersao;
	private ProgressDialog progressDialog;
	private Button botaoBaixar;

	private Boolean abrirLivro = false,flagNovaVersao = false;

	private AlertDialog dialogConfirm,dialogNovaVersao;

	private String idrPATH = "";
	private String xmlPATH = "";
	private String fotoPath = "";
	
	private DownloadArquivo downloadLivro;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detalhar_livro);

		Intent itent = getIntent();
		Bundle params = itent.getExtras();

		abrirLivro = params.getBoolean("abrirLivro");

		botaoBaixar = (Button) findViewById(R.id.btnDownload);
		botaoBaixar.setText(abrirLivro ? "Abrir livro" : "Baixar Livro");

		dialogConfirm = criarConfirmacao();
		pegaLivro();
		configuraImagemLivro();
		configuraInformacoesLivro();
	}

	private void configuraInformacoesLivro() {
		TextView tituloLivro = (TextView) findViewById(R.id.titulo);
		TextView versao = (TextView) findViewById(R.id.versaoLivro);
		TextView codigoLoja = (TextView) findViewById(R.id.codigo);

		tituloLivro.setText(livro.getTitulo());
		versao.setText("Versão: ".concat(livro.getVersao()));
		codigoLoja.setText("Código loja: ".concat(livro.getCodigoloja()));

	}

	private void configuraImagemLivro() {
		if (livro.getFoto() == null)
			return;
		ImageView imgView = (ImageView) findViewById(R.id.imageView1);
		if(abrirLivro){
			try {
				imgView.setImageBitmap(BitmapFactory.decodeStream(new FileInputStream(new File(livro.getFoto()))));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}else{
			new DownloadImageTask(imgView).execute(livro.getFoto());
		}
	}

	private void pegaLivro() {
		Intent itent = getIntent();
		Bundle params = itent.getExtras();

		Set<String> setKeys = params.keySet();

		for (String key : setKeys) {
			Object obj = params.get(key);
			if (obj instanceof Livro) {
				livro = (Livro) obj;
				break;
			}
		}
	}
	
	public void baixarLivro(View view) {
		dialogConfirm.show();
	}

	private AlertDialog criarConfirmacao() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setMessage("Deseja realmente ".concat(
				(abrirLivro ? "abrir " : "baixar")).concat(" o livro?"));
		builder.setPositiveButton("Sim", this);
		builder.setNegativeButton("Não", this);

		return builder.create();
	}

	private class RegistrarlivroTask extends AsyncTask<Void, String, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {

			RespostaDAO respostaDAO = new RespostaDAO(
					DetalharLivroActivity.this);
			RegistroDAO registroDAO = new RegistroDAO(
					DetalharLivroActivity.this);

			ResponseRegistrar respReg = respostaDAO.getRegistro();
			RequestRegistrar reqReg = registroDAO.getRequisicaoRegistro();

			RequestRegistrarLivro requestRegistrarLivro = new RequestRegistrarLivro();
			requestRegistrarLivro.setCliente(respReg.getCodCliente());
			requestRegistrarLivro.setDocumento(reqReg.getDocumento());
			requestRegistrarLivro.setDispositivo(respReg.getCodDispositivo());
			requestRegistrarLivro.setKeyworkd(ListaEstantesActivity.requestEstante!=null?ListaEstantesActivity.requestEstante.getKeyword():"");
			requestRegistrarLivro.setProduto(livro.getCodigolivro());
			requestRegistrarLivro.setSenha(ListaEstantesActivity.requestEstante!=null?ListaEstantesActivity.requestEstante.getSenha():"");

			ConnectionRegistrarLivro connectionRegistrar = new ConnectionRegistrarLivro();
			try {
				//ResponseWS resp = 
						connectionRegistrar.serviceConnect(
						requestRegistrarLivro, ConnectionWS.WS_REGISTRAR_LIVRO);
				// ResponseRegistrarLivro responseRegistrar =
				// (ResponseRegistrarLivro) resp;

				// if (responseRegistrar.getErro() == null
				// || responseRegistrar.getErro().isEmpty()
				// || responseRegistrar.getErro().equals("0")) {
				// SALVA Na tabela de baixados (DISPONIVEIS) - Vou
				// mostrar apenas os livros que foram baixados.
				LivrosBaixadosDAO livrosBaixadosDAO = new LivrosBaixadosDAO(
						getApplicationContext());
				livro.setArquivomobile(idrPATH);
				livro.setIndiceXML(xmlPATH);
				livro.setFoto(fotoPath);
				livrosBaixadosDAO.salvar(livro);
				livrosBaixadosDAO.close();
				// }

			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			progressDialog.dismiss();

			Toast.makeText(DetalharLivroActivity.this,
					"Download do livro finalizado com sucesso",
					Toast.LENGTH_LONG).show();
			startActivity(new Intent(DetalharLivroActivity.this,
					ListaEstantesActivity.class));
		}

	}

	private class DownloadArquivo extends AsyncTask<Void, String, Void> {
		int progresso = 0;
		String mensagemProgresso;
		boolean resultadoDownload = true;

		@Override
		protected Void doInBackground(Void... params) {

			try {
				String tmp = ConstantesIDR.PATH_IBRACON
						.concat(livro.getCodigoloja())
						.concat(livro.getCodigolivro())
						.concat(""+new Date().getTime())
						.concat(".");
				idrPATH = tmp.concat("idr");
				xmlPATH = tmp.concat("xml");
				fotoPath = tmp.concat("png");
				
				downloadArquivo(livro.getArquivomobile(), "Efetuando o download do livro", idrPATH);
				downloadArquivo(livro.getIndiceXML(), "Efetuando o download do índice", xmlPATH);
				downloadArquivo(livro.getFoto(), "Efetuando o download da foto", fotoPath);
			} catch (Exception e) {
				resultadoDownload = false;
			}

			return null;
		}

		private void downloadArquivo(String urlPDF, String mensagem, String path) throws MalformedURLException, IOException,
				FileNotFoundException {
			int count;
			if(urlPDF==null || urlPDF.isEmpty()){
				return;
			}
			
			URL url = new URL(urlPDF.replace(" ", "%20"));
			URLConnection conexion = url.openConnection();
			conexion.connect();
			conexion.setConnectTimeout(150000);

			int lenghtOfFile = conexion.getContentLength();
			Log.d("ANDRO_ASYNC", "Lenght of file: " + lenghtOfFile);

			InputStream input = new BufferedInputStream(url.openStream());

			FileUtil.criarPasta(ConstantesIDR.PATH_IBRACON);

			
			OutputStream output = new FileOutputStream(path);	
			
			//GRAVANDO NO ARMAZENAMENTO INTERNO DO ANDROID
			//FileOutputStream output = openFileOutput(urlPDF.substring(urlPDF.indexOf("/")), Context.MODE_PRIVATE);

			byte data[] = new byte[5024];

			long total = 0;

			while ((count = input.read(data)) != -1) {
				total += count;
				progresso = (int) ((total * 100) / lenghtOfFile);

				mensagemProgresso = mensagem + "(" + progresso + "%)";

				publishProgress(mensagemProgresso);
				output.write(data, 0, count);
			}

			output.flush();
			output.close();
			input.close();
		}

		@Override
		protected void onProgressUpdate(String... values) {
			progressDialog.setMessage(mensagemProgresso + "");
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			botaoBaixar.setEnabled(false);
			progressDialog = ProgressDialog.show(DetalharLivroActivity.this,
					"Aguarde por favor", "Efetuando o download...", false);

			getInstance().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
		}

		@Override
		protected void onPostExecute(Void result) {
			botaoBaixar.setEnabled(true);
			if (resultadoDownload) {
				if(flagNovaVersao){
					flagNovaVersao = false;
					LivrosBaixadosDAO livrosBaixadosDAO = new LivrosBaixadosDAO(
							getApplicationContext());
					livro.setArquivomobile(idrPATH);
					livro.setIndiceXML(xmlPATH);
					livro.setFoto(fotoPath);
					livrosBaixadosDAO.atualizar(livro);
					livrosBaixadosDAO.close();
					
					progressDialog
						.setMessage("Download da nova versão do livro finalizada.");
					
					progressDialog.dismiss();
					
					configuraImagemLivro();
					configuraInformacoesLivro();
					
					Toast.makeText(DetalharLivroActivity.this,
							"Download da nova versão do livro finalizada.",
							Toast.LENGTH_LONG).show();
				}else{
					progressDialog
						.setMessage("Download Finalizado. Efetuando o registro do livro.");
					new RegistrarlivroTask().execute();
				}
			}
			flagNovaVersao = false;
		}
	}

	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
		case DialogInterface.BUTTON_POSITIVE:
			if (abrirLivro && !flagNovaVersao) {
				//VERIFICANDO NOVA VERSÃO DO LIVRO.
				if(ListaEstantesActivity.responseEstante!=null &&
						ListaEstantesActivity.responseEstante.baixados!=null &&
								ListaEstantesActivity.responseEstante.baixados.size() > 0){
					for(Livro lvTmp : ListaEstantesActivity.responseEstante.baixados){
						if(lvTmp.getCodigolivro().equals(livro.getCodigolivro()) && 
							lvTmp.getCodigoloja().equals(livro.getCodigoloja())){
							if(!lvTmp.getVersao().equals(livro.getVersao())){
								
								flagNovaVersao = true;
								livroTmpNovaVersao = lvTmp;
								
								dialogConfirm.dismiss();
								
								AlertDialog.Builder builder = new AlertDialog.Builder(this);

								builder.setMessage("Existe uma nova versão do livro, deseja baixá-la agora?");
								builder.setPositiveButton("Sim", this);
								builder.setNegativeButton("Não", this);

								dialogNovaVersao = builder.create();
								dialogNovaVersao.show();
								
								return;
							}
						}
					}
				}
				
				abrirRedirecionarPDF();
			} else {
				if(flagNovaVersao){
					livro = livroTmpNovaVersao;
				}
				
				if(InternetUtil.possuiConexaoInternet(this)){
					downloadLivro = new DownloadArquivo();
					downloadLivro.execute();
				}else{
					Toast.makeText(DetalharLivroActivity.this, "Erro ao conectar - Parece que seu dispositivo está sem acesso a internet.", Toast.LENGTH_LONG).show();
				}
			}
			break;
		case DialogInterface.BUTTON_NEGATIVE:
			if(flagNovaVersao){
				abrirRedirecionarPDF();
				flagNovaVersao = false;
			}
			break;
		default:
			break;
		}
		if(dialogConfirm!=null){
			dialogConfirm.dismiss();
		}
		if(dialogNovaVersao!=null){
			dialogNovaVersao.dismiss();
		}
	}

	private void abrirRedirecionarPDF() {
		/**
		 * ABRIR COM PDFREADER
		 */
		Intent intent = new Intent(this,PDFReaderAct.class);

		intent.putExtra(PDFReaderAct.LIVRO, livro);
		intent.putExtra(PDFReaderAct.EXTRA_PDF_SENHA, "ibracon%2014");
		ResponseIndice respInd = ResponseIndice.montaResposta(livro.getIndiceXML());
		intent.putExtra(PDFReaderAct.RESPONSE_INDICE_OBJ, respInd);
		
		startActivity(intent);
	}
}