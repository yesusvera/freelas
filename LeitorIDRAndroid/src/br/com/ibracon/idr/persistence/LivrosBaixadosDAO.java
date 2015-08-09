package br.com.ibracon.idr.persistence;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import br.com.ibracon.idr.model.Livro;

/**
 * 
 * @author yesus
 *
 */
public class LivrosBaixadosDAO extends DatabaseHelper{
	
	public LivrosBaixadosDAO(Context context) {
		super(context);
	}

	/**MÉTODO UTILIZADO PARA CADASTRAR A RESPOSTA NO BANCO DE DADOS**/
	public void salvar(Livro livro){
		
		ContentValues values = new ContentValues();
		
		values.put("codigoLivro", livro.getCodigolivro());
		values.put("titulo", livro.getTitulo());
		values.put("versao", livro.getVersao());
		values.put("codigoLoja", livro.getCodigoloja());
		values.put("idrPath", livro.getArquivo());
		values.put("foto", livro.getFoto());
		values.put("arquivoMobile", livro.getArquivomobile());
		values.put("indiceXML", livro.getIndiceXML());
		
		getWritableDatabase().insert("LivrosBaixados", null, values);
	}
	
	public void atualizar(Livro livro){
		ContentValues values = new ContentValues();
		
		values.put("codigoLivro", livro.getCodigolivro());
		values.put("titulo", livro.getTitulo());
		values.put("versao", livro.getVersao());
		values.put("codigoLoja", livro.getCodigoloja());
		values.put("idrPath", livro.getArquivo());
		values.put("foto", livro.getFoto());
		values.put("arquivoMobile", livro.getArquivomobile());
		values.put("indiceXML", livro.getIndiceXML());
		
		String filtro = "codigoLivro=".concat(livro.getCodigolivro());
		
		getWritableDatabase().update("LivrosBaixados",values,filtro, null);
	}

	public void excluirPorId(String codLivro){
		getWritableDatabase().delete("LivrosBaixados", "codigoLivro=".concat(codLivro), null);
	}
	public void excluirTudo(){
		getWritableDatabase().delete("LivrosBaixados", null, null);
	}
	
	public ArrayList<Livro> listaLivrosBaixados(){
        String[] colunas = {"codigoLivro", "titulo", "versao", "codigoLoja", "idrPath", "foto", "arquivoMobile", "indiceXML"};
        Cursor cursor = getWritableDatabase().query("LivrosBaixados ", colunas, null, null, null, null, null);

        ArrayList<Livro> listaLivros = new ArrayList<Livro>();
        
        while(cursor.moveToNext()) {
        	Livro livro = new Livro();
        	byte i = 0;
        	livro.setCodigolivro(cursor.getString(i++));
        	livro.setTitulo(cursor.getString(i++));
        	livro.setVersao(cursor.getString(i++));
        	livro.setCodigoloja(cursor.getString(i++));
        	livro.setArquivo(cursor.getString(i++));
        	livro.setFoto(cursor.getString(i++));
        	livro.setArquivomobile(cursor.getString(i++));
        	livro.setIndiceXML(cursor.getString(i++));
        	listaLivros.add(livro);
        }
        cursor.close();
		return listaLivros;
	}
}