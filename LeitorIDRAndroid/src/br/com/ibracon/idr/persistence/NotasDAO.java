package br.com.ibracon.idr.persistence;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import br.com.ibracon.idr.model.Nota;

/**
 * 
 * @author yesus
 *
 */
public class NotasDAO extends DatabaseHelper{
	
	public NotasDAO(Context context) {
		super(context);
	}

	public void salvarOuAtualizar(Nota nota){
		
		if(nota==null){
			return;
		}
		
		if(getNota(nota.getPagina(), nota.getCodigoLivro())!=null){
			alterar(nota);
		}else{
			ContentValues values = new ContentValues();
			
			values.put("pagina", Integer.valueOf(nota.getPagina()));
			values.put("titulo", nota.getTitulo());
			values.put("codigoLivro", nota.getCodigoLivro());
			values.put("nota", nota.getNota());
			
			getWritableDatabase().insert(TBL_NOTA, null, values);
		}
	}
	
	public Nota getNota(String pagina, String codigoLivro){
		Nota nota = null;
		Cursor cursor = getReadableDatabase().rawQuery("select pagina,titulo,codigoLivro,nota from "+TBL_NOTA+" where pagina=? and codigoLivro=? order by pagina asc", new String[]{pagina, codigoLivro}); 
		while(cursor.moveToNext()) {
			nota = new Nota();
        	nota.setCodigoLivro(cursor.getString(cursor.getColumnIndex("codigoLivro")));
        	nota.setNota(cursor.getString(cursor.getColumnIndex("nota")));
        	nota.setPagina(""+cursor.getInt(cursor.getColumnIndex("pagina")));
        	nota.setTitulo(cursor.getString(cursor.getColumnIndex("titulo")));
        	
        	break;
        }
		cursor.close();
		
		return nota;
	}
	
	public void alterar(Nota nota){
		ContentValues dataUpdate = new ContentValues();
		
		dataUpdate.put("pagina", Integer.valueOf(nota.getPagina()));
		dataUpdate.put("titulo", nota.getTitulo());
		dataUpdate.put("codigoLivro", nota.getCodigoLivro());
		dataUpdate.put("nota", nota.getNota());
		
		String where = "codigoLivro=? and pagina=?";
		String[] whereArgs = new String[] {nota.getCodigoLivro(), nota.getPagina()};

		getWritableDatabase().update(TBL_NOTA, dataUpdate, where, whereArgs);
	}
	

	public ArrayList<Nota> listaNotasPorCodigoLivro(String codigoLivro){
		ArrayList<Nota> listaNotas = new ArrayList<Nota>();
		Cursor cursor = getReadableDatabase().rawQuery("select pagina,titulo,codigoLivro,nota from "+TBL_NOTA+" where codigoLivro=? order by pagina asc", new String[]{codigoLivro}); 
		while(cursor.moveToNext()) {
			Nota nota = new Nota();
        	nota.setCodigoLivro(cursor.getString(cursor.getColumnIndex("codigoLivro")));
        	nota.setNota(cursor.getString(cursor.getColumnIndex("nota")));
        	nota.setPagina(""+cursor.getInt(cursor.getColumnIndex("pagina")));
        	nota.setTitulo(cursor.getString(cursor.getColumnIndex("titulo")));
        	listaNotas.add(nota);
        }
		cursor.close();
		
		return listaNotas;
	}
}