package br.com.ibracon.idr.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import br.com.ibracon.idr.model.Nota;
import br.com.ibracon.idr.persistence.NotasDAO;

import com.complab.R;
import com.radaee.reader.PDFReaderAct;

public class AnotacoesActivity extends MainActivity implements OnClickListener {

	public final static String NOTA = "br.com.ibracon.idr.activity.AnotacoesActivity.NOTA";
	public final static byte SALVAR = 1; 
	public final static byte CANCELAR = 2; 
	
	private Nota nota = new Nota();
	
	private EditText txtTituloNota;
	private EditText txtConteudoNota;
	private TextView tituloAnotacao;
	
	private NotasDAO notasDAO = new NotasDAO(this);
	
	private AlertDialog dialogConfirm;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_anotacoes);
		
		dialogConfirm = criarConfirmacao();
		
		Bundle extras = getIntent().getExtras();
		nota = (Nota) extras.get(NOTA);
		
		Nota notaTemp = notasDAO.getNota(nota.getPagina(), nota.getCodigoLivro());
		if(notaTemp!=null){
			nota =notaTemp;
		}
		
		txtTituloNota = (EditText)findViewById(R.id.txtTituloNota);
		txtConteudoNota = (EditText)findViewById(R.id.txtConteudoNota);
		tituloAnotacao = (TextView)findViewById(R.id.tituloAnotacao);
		
		tituloAnotacao.setText("Anotação Página (".concat(nota.getPagina()).concat(")"));
		txtTituloNota.setText(nota.getTitulo());
		txtConteudoNota.setText(nota.getNota());
	}
	
	private AlertDialog criarConfirmacao() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setMessage("Confirma a gravação da nota?");
		builder.setPositiveButton("Sim", this);
		builder.setNegativeButton("Não", this);

		return builder.create();
	}
	
	public void confirmarSalvar(View view){
		if(txtConteudoNota.getText().toString().isEmpty()){
			Toast.makeText(this, "Por favor, insira um conteúdo para a nota", Toast.LENGTH_SHORT).show();
			return;
		}
		dialogConfirm.show();
	}
	
	public void cancelar(View view){
		devolverResultado();
	}

	private void devolverResultado() {
		Intent data = new Intent();
		data.putExtra(PDFReaderAct.NUMERO_PAGINA, Integer.valueOf(nota.getPagina()));
		setResult(2,data);
		finish();
	}
	
	private void salvarAnotacao(){
		nota.setTitulo(txtTituloNota.getText().toString());
		nota.setNota(txtConteudoNota.getText().toString());
		
		notasDAO.salvarOuAtualizar(nota);
		
		Toast.makeText(this, "Nota salva com sucesso.", Toast.LENGTH_SHORT).show();
		
		devolverResultado();
	}

	@Override
	public void onClick(DialogInterface arg0, int arg1) {
		switch (arg1) {
		case DialogInterface.BUTTON_POSITIVE:
			salvarAnotacao();
			break;
		default:
			break;
		}
		dialogConfirm.dismiss();
	}
}
