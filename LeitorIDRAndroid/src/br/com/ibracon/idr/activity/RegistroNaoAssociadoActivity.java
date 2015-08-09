package br.com.ibracon.idr.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextWatcher;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import br.com.ibracon.idr.persistence.RegistroDAO;
import br.com.ibracon.idr.persistence.RespostaDAO;
import br.com.ibracon.idr.util.Mask;
import br.com.ibracon.idr.util.Validacoes;
import br.com.ibracon.idr.webservice.ConnectionWS;
import br.com.ibracon.idr.webservice.ResponseWS;
import br.com.ibracon.idr.webservice.registrar.ConnectionRegistrar;
import br.com.ibracon.idr.webservice.registrar.RequestRegistrar;
import br.com.ibracon.idr.webservice.registrar.ResponseRegistrar;

import com.complab.R;


public class RegistroNaoAssociadoActivity extends MainActivity implements OnItemSelectedListener{
	
	RequestRegistrar registro = new RequestRegistrar();
	 private TextWatcher cpfMask;
	 private TextWatcher cnpjMask;
		EditText editTextCPF ;
		EditText editTextCNPJ ;
		int tipoDocumento = 0; //O CPF - 1 CNPJ
	ResponseWS resp;
	
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registro_nao_associado);

		/** Tratamento do Edit Text IP **/
		EditText editTextIp = (EditText) findViewById(R.id.ip);
		WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
		registro.setIp(Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress()));
		editTextIp.setText(registro.getIp());

		/** Tratamento do Edit Text MacAdress **/
		EditText editTextMacAdress = (EditText) findViewById(R.id.macAdress);
		registro.setMacadress(wm.getConnectionInfo().getMacAddress());
		editTextMacAdress.setText(registro.getMacadress());

		/** Tratamento do Edit Text Dispositivo **/
		EditText editTextDispositivo = (EditText) findViewById(R.id.dispositivo);
		registro.setDispositivo(Build.USER);
		editTextDispositivo.setText(registro.getDispositivo());

		/** Tratamento do Edit Text Serial **/
		EditText editTextSerial = (EditText) findViewById(R.id.serial);
		registro.setSerial(Build.SERIAL);
		editTextSerial.setText(registro.getSerial());

		((Spinner)findViewById(R.id.spinnerDocumento)).setOnItemSelectedListener(this);
		
		//MASCARAS DOCUMENTO
		editTextCPF = (EditText)findViewById(R.id.cpfEditText);
		editTextCPF.addTextChangedListener(Mask.insert("###.###.###-##", editTextCPF));
		
		editTextCNPJ = (EditText)findViewById(R.id.cnpjEditText);
		editTextCNPJ.addTextChangedListener(Mask.insert("##.###.###/####-##", editTextCNPJ));
	}
	
	public void adicionarUF(View v){
		Intent intent = new Intent(RegistroNaoAssociadoActivity.this, ListaUfActivity.class);
		startActivityForResult(intent, 1);
	}
	
	public void registrarNaoAssociado(View v){

		registro.setAssociado("N");
		EditText editTextCliente = (EditText) findViewById(R.id.cliente);
		registro.setCliente(editTextCliente.getText().toString());
		
		EditText editTextSenha = (EditText) findViewById(R.id.senha);
		registro.setSenha(editTextSenha.getText().toString());
		
		if(tipoDocumento==0){
			registro.setDocumento(editTextCPF.getText().toString());
		}else if(tipoDocumento ==1){
			registro.setDocumento(editTextCNPJ.getText().toString());
		}
		
		EditText editTextTelefone = (EditText) findViewById(R.id.telefone);
		registro.setTelefone(editTextTelefone.getText().toString());
		
		EditText editTextEndereco = (EditText) findViewById(R.id.endereco);
		registro.setEndereco(editTextEndereco.getText().toString());
		
		EditText editTextNumero = (EditText) findViewById(R.id.numero);
		registro.setNumero(editTextNumero.getText().toString());

		
		EditText editTextComplemento = (EditText) findViewById(R.id.complemento);
		registro.setComplemento(editTextComplemento.getText().toString());

		EditText editTextBairro = (EditText) findViewById(R.id.bairro);
		registro.setBairro(editTextBairro.getText().toString());
		
		EditText editTextCidade = (EditText) findViewById(R.id.cidade);
		registro.setCidade(editTextCidade.getText().toString());
		
		EditText editTextUF = (EditText) findViewById(R.id.uf);
		registro.setUf(editTextUF.getText().toString());
		
		EditText editTextCep = (EditText) findViewById(R.id.cep);
		registro.setCep(editTextCep.getText().toString());
		
		EditText editTextEmail = (EditText) findViewById(R.id.email);
		registro.setEmail(editTextEmail.getText().toString());

		
		if(registro.getCliente()==null || registro.getCliente().isEmpty()){
			Toast.makeText(RegistroNaoAssociadoActivity.this, "O campo Nome/Razão Social é de preenchimento obrigatório.", Toast.LENGTH_LONG).show();
			return;
		}
//		if(registro.getSenha()==null || registro.getSenha().isEmpty()){
//			Toast.makeText(RegistroNaoAssociadoActivity.this, "O campo Senha é de preenchimento obrigatório.", Toast.LENGTH_LONG).show();
//			return;
//		}
		if(registro.getDocumento()==null || registro.getDocumento().isEmpty()){
			Toast.makeText(RegistroNaoAssociadoActivity.this, "O campo Documento é de preenchimento obrigatório.", Toast.LENGTH_LONG).show();
			return;
		}else{
			if(tipoDocumento==0){
				if(!Validacoes.cpfEValido(Mask.unmask(registro.getDocumento()))){
					Toast.makeText(RegistroNaoAssociadoActivity.this,
							"CPF inválido.",
							Toast.LENGTH_LONG).show();
					return;
				}
			}else if(tipoDocumento ==1){
				if(!Validacoes.cnpjEValido(Mask.unmask(registro.getDocumento()))){
					Toast.makeText(RegistroNaoAssociadoActivity.this,
							"CNPJ inválido.",
							Toast.LENGTH_LONG).show();
					return;
				}
			}
		}
		
		new RegistroNaoAssociadoTask().execute(registro);

	}
	
	private class RegistroNaoAssociadoTask extends AsyncTask<RequestRegistrar, Void, Void> {

		ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() {
			progressDialog = new ProgressDialog(RegistroNaoAssociadoActivity.this);
			progressDialog.show();
		}
		
		@Override
		protected void onPostExecute(Void result) {
			
			progressDialog.dismiss();
			
			if (resp !=  null && !resp.getErro().equals("0")){
				Log.i("Mensagem de Erro", resp.getMsgErro());
				Toast.makeText(RegistroNaoAssociadoActivity.this, resp.getMsgErro(), Toast.LENGTH_LONG).show();
			}else {
				Toast.makeText(RegistroNaoAssociadoActivity.this, "Registo Efetuado com Sucesso!", Toast.LENGTH_LONG).show();
				Intent intent = new Intent(RegistroNaoAssociadoActivity.this, ListaEstantesActivity.class);
				startActivity(intent);
			}
		}
		
		
		@Override
		protected Void doInBackground(RequestRegistrar... params) {
			
			RequestRegistrar requestRegistrar = params[0];
			ResponseRegistrar responseRegistrar =  new ResponseRegistrar();

			ConnectionRegistrar connectionRegistrar = new ConnectionRegistrar();
			try {
				resp = connectionRegistrar.serviceConnect(requestRegistrar, ConnectionWS.WS_REGISTRAR);
				responseRegistrar = (ResponseRegistrar) resp;
				
				Log.i("Resposta", responseRegistrar.toString());
			
			} catch (Exception e) {
				Log.i("info", e.getMessage());
			}
			
			RespostaDAO respostaDAO = new RespostaDAO(RegistroNaoAssociadoActivity.this);
			respostaDAO.excluirTudo();
			respostaDAO.salvar(responseRegistrar);
			respostaDAO.close();
			
			RegistroDAO registroDao = new RegistroDAO(RegistroNaoAssociadoActivity.this);
			registroDao.excluirTudo();
			registroDao.salvar(registro);
			registroDao.close();
			
			return null;
		}
	}

	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		EditText editTextUF = (EditText) findViewById(R.id.uf);
		editTextUF.setText(data.getExtras().getString(ListaUfActivity.UF));
	}
	
	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		tipoDocumento = pos;
		if(pos==0){
			editTextCPF.setVisibility(EditText.VISIBLE);
			editTextCNPJ.setVisibility(EditText.INVISIBLE);
		}else{
			editTextCPF.setVisibility(EditText.INVISIBLE);
			editTextCNPJ.setVisibility(EditText.VISIBLE);
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
	}
}
