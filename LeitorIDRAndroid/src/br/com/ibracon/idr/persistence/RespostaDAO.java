package br.com.ibracon.idr.persistence;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import br.com.ibracon.idr.webservice.registrar.ResponseRegistrar;

public class RespostaDAO extends DatabaseHelper{
	
	public RespostaDAO(Context context) {
		super(context);
	}

	/**MÉTODO UTILIZADO PARA CADASTRAR A RESPOSTA NO BANCO DE DADOS**/
	public void salvar(ResponseRegistrar resposta){
		
		ContentValues values = new ContentValues();
		
		values.put("codCliente", resposta.getCodCliente());
		values.put("codDispositivo", resposta.getCodDispositivo());
		values.put("status", resposta.getStatus());
		
		getWritableDatabase().insert("Resposta", null, values);
		
	}
	
	public void excluirTudo(){
		getWritableDatabase().delete("Resposta", null, null);
	}
	
	
	public ResponseRegistrar getRegistro(){
		List<ResponseRegistrar> lista = listarRespostasRegistro();
		if(lista!=null && lista.size()>0){
			return lista.get(0);
		}
		return null;
	}
	
	/**TODO UTILIZADO PARA CONSULTA DA RESPOSTA NO BANCO DE DADOS
	 * E VALIDAÇÃO DOS DADOS A CADA VEZ QUE O USUÁRIO
	 * ACESSA A APLICAÇÃO**/
	public List<ResponseRegistrar> listarRespostasRegistro(){
        
        String[] colunas = {"codCliente", "codDispositivo", "status"};

        Cursor cursor = getWritableDatabase().query("Resposta ", colunas, null, null, null, null, null);

        List<ResponseRegistrar> respostas = new ArrayList<ResponseRegistrar>();
        
        while(cursor.moveToNext()) {
        	
        	ResponseRegistrar resposta = new ResponseRegistrar();

	        resposta.setCodCliente(cursor.getString(0));
	        resposta.setCodDispositivo(cursor.getString(1));
	        resposta.setStatus(cursor.getString(2));
	        respostas.add(resposta);
        }

        cursor.close();
        
		return respostas;
	}
}
