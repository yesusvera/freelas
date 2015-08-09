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
import br.com.ibracon.idr.util.InternetUtil;
import br.com.ibracon.idr.util.Mask;
import br.com.ibracon.idr.util.Validacoes;
import br.com.ibracon.idr.webservice.ConnectionWS;
import br.com.ibracon.idr.webservice.ResponseWS;
import br.com.ibracon.idr.webservice.registrar.ConnectionRegistrar;
import br.com.ibracon.idr.webservice.registrar.RequestRegistrar;
import br.com.ibracon.idr.webservice.registrar.ResponseRegistrar;

import com.complab.R;

public class RegistroAssociadoActivity extends MainActivity implements
		OnItemSelectedListener {

	RequestRegistrar registro = new RequestRegistrar();
	EditText editTextCPF ;
	EditText editTextCNPJ ;
	int tipoDocumento = 0; //O CPF - 1 CNPJ



	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registro_associado);

		/** Tratamento do Edit Text IP **/
		EditText editTextIp = (EditText) findViewById(R.id.ip);
		WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
		registro.setIp(Formatter.formatIpAddress(wm.getConnectionInfo()
				.getIpAddress()));
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

	public void registrarAssociado(View v) {

		registro.setAssociado("S");

		if(tipoDocumento==0){
			registro.setDocumento(editTextCPF.getText().toString());
		}else if(tipoDocumento ==1){
			registro.setDocumento(editTextCNPJ.getText().toString());
		}

		EditText editTextRegistroNacional = (EditText) findViewById(R.id.registroNacional);
		registro.setRegistro(editTextRegistroNacional.getText().toString());

		EditText editTextSenha = (EditText) findViewById(R.id.senha);
		registro.setSenha(editTextSenha.getText().toString());

		/**
		 * Parametros setados para evitar null pointer na criação da url de
		 * conexao
		 **/
		registro.setBairro("");
		registro.setCep("");
		registro.setCidade("");
		registro.setCliente("");
		registro.setComplemento("");
		registro.setEmail("");
		registro.setEndereco("");
		registro.setNumero("");
		registro.setTelefone("");
		registro.setUf("");

//		if (registro.getSenha() == null || registro.getSenha().isEmpty()) {
//			Toast.makeText(RegistroAssociadoActivity.this,
//					"O campo Senha é de preenchimento obrigatório.",
//					Toast.LENGTH_LONG).show();
//			return;
//		}
		if (registro.getDocumento() == null
				|| registro.getDocumento().isEmpty()) {
			Toast.makeText(RegistroAssociadoActivity.this,
					"O campo Documento é de preenchimento obrigatório.",
					Toast.LENGTH_LONG).show();
			return;
		}else{
			if(tipoDocumento==0){
				if(!Validacoes.cpfEValido(Mask.unmask(registro.getDocumento()))){
					Toast.makeText(RegistroAssociadoActivity.this,
							"CPF inválido.",
							Toast.LENGTH_LONG).show();
					return;
				}
			}else if(tipoDocumento ==1){
				if(!Validacoes.cnpjEValido(Mask.unmask(registro.getDocumento()))){
					Toast.makeText(RegistroAssociadoActivity.this,
							"CNPJ inválido.",
							Toast.LENGTH_LONG).show();
					return;
				}
			}
		}

		/** FIM **/

		if (InternetUtil.possuiConexaoInternet(getApplicationContext())) {
			new RegistroAssociadoTask().execute(registro);
		} else {
			Toast.makeText(
					RegistroAssociadoActivity.this,
					"Erro ao conectar. Seu dispositivo está sem acesso a internet.",
					Toast.LENGTH_LONG).show();
		}
	}

	private class RegistroAssociadoTask extends
			AsyncTask<RequestRegistrar, Void, Void> {

		ProgressDialog progressDialog;
		ResponseWS resp;

		@Override
		protected void onPreExecute() {
			progressDialog = new ProgressDialog(RegistroAssociadoActivity.this);
			progressDialog.setTitle("Registrando Associado");
			progressDialog
					.setMessage("Registrando o dispositivo, por favor aguarde.");
			progressDialog.show();
		}

		@Override
		protected void onPostExecute(Void result) {

			progressDialog.dismiss();

			if (resp != null && !resp.getErro().equals("0")) {
				Log.i("Mensagem de Erro", resp.getMsgErro());
				Toast.makeText(RegistroAssociadoActivity.this,
						resp.getMsgErro(), Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(RegistroAssociadoActivity.this,
						"Registo Efetuado com Sucesso!", Toast.LENGTH_LONG)
						.show();
				Intent intent = new Intent(RegistroAssociadoActivity.this,
						ListaEstantesActivity.class);
				startActivity(intent);
			}
		}

		@Override
		protected Void doInBackground(RequestRegistrar... params) {
			RequestRegistrar requestRegistrar = params[0];

			ConnectionRegistrar connectionRegistrar = new ConnectionRegistrar();
			try {
				resp = connectionRegistrar.serviceConnect(requestRegistrar,
						ConnectionWS.WS_REGISTRAR);
				ResponseRegistrar respostaRegistrar = (ResponseRegistrar) resp;
				System.out.println(respostaRegistrar);

				RespostaDAO respostaDAO = new RespostaDAO(
						RegistroAssociadoActivity.this);
				respostaDAO.excluirTudo();
				respostaDAO.salvar(respostaRegistrar);
				respostaDAO.close();

				RegistroDAO registroDao = new RegistroDAO(
						RegistroAssociadoActivity.this);
				registroDao.excluirTudo();
				registroDao.salvar(registro);
				registroDao.close();
			} catch (Exception e) {
				Log.i("info", e.getMessage());
			}
			return null;
		}

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
