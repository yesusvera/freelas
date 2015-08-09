package br.com.ibracon.idr.persistence;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import br.com.ibracon.idr.webservice.registrar.RequestRegistrar;

public class RegistroDAO extends DatabaseHelper{
	
	public RegistroDAO(Context context) {
		super(context);
	}

	/**MÉTODO UTILIZADO PARA CADASTRAR O REGISTRO NO BANCO DE DADOS**/
	public void salvar(RequestRegistrar registro){
		
		ContentValues values = new ContentValues();
		
		values.put("cliente", registro.getCliente());
		values.put("endereco", registro.getEndereco());
		values.put("numero", registro.getNumero());
		values.put("complemento", registro.getComplemento());
		values.put("bairro", registro.getBairro());
		values.put("cidade", registro.getCidade());
		values.put("uf", registro.getUf());
		values.put("cep", registro.getCep());
		values.put("email", registro.getEmail());
		values.put("senha", registro.getSenha());
		values.put("serial", registro.getSerial());
		values.put("macadress", registro.getMacadress());
		values.put("ip", registro.getIp());
		values.put("documento", registro.getDocumento());
		values.put("telefone", registro.getTelefone());
		values.put("dispositivo", registro.getDispositivo());
		values.put("associado", registro.getAssociado());
		
		getWritableDatabase().insert("Registro", null, values);
	}
	
	public void excluirTudo(){
		getWritableDatabase().delete("Registro", null, null);
	}
	
	
	public RequestRegistrar getRequisicaoRegistro(){
		List<RequestRegistrar> lista = listarRequisicaoRegistro();
	
		if(lista!=null && lista.size()>0){
			return lista.get(0);
		}
		
		return null;
	}
	
	/**MÉTODO UTILIZADO PARA CONSULTA DO REGISTRO NO BANCO DE DADOS
	 * E VALIDAÇÃO DOS DADOS A CADA VEZ QUE O USUÁRIO
	 * ACESSA A APLICAÇÃO**/
	public List<RequestRegistrar> listarRequisicaoRegistro(){
        
        String[] colunas = {
        		"cliente", "endereco", "numero", "complemento", "bairro", "cidade",
        		"uf","cep", "email", "senha", "serial", "macadress","ip", "documento",
        		"telefone", "dispositivo", "associado"};

        Cursor cursor = getWritableDatabase().query("Registro ", colunas, null, null, null, null, null);
        
        List<RequestRegistrar> registros = new ArrayList<RequestRegistrar>();
        
        while(cursor.moveToNext()) {
        	
        	RequestRegistrar registro = new RequestRegistrar();

        	registro.setCliente(cursor.getString(0));
        	registro.setEndereco(cursor.getString(1));
        	registro.setNumero(cursor.getString(2));
        	registro.setComplemento(cursor.getString(3));
        	registro.setBairro(cursor.getString(4));
        	registro.setCidade(cursor.getString(5));
        	registro.setUf(cursor.getString(6));
        	registro.setCep(cursor.getString(7));
        	registro.setEmail(cursor.getString(8));
        	registro.setSenha(cursor.getString(9));
        	registro.setSerial(cursor.getString(10));
        	registro.setMacadress(cursor.getString(11));
        	registro.setIp(cursor.getString(12));
        	registro.setDocumento(cursor.getString(13));
        	registro.setTelefone(cursor.getString(14));
        	registro.setDispositivo(cursor.getString(15));
        	registro.setAssociado(cursor.getString(16));
        	
        	registros.add(registro);
        }

        cursor.close();
        
		return registros;
	}
}
