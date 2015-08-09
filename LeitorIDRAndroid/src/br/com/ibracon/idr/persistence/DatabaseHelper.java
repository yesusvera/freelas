package br.com.ibracon.idr.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public abstract class DatabaseHelper extends SQLiteOpenHelper{
	
	private static final String DATA_BASE = "Ibracon_IDR";
	
	/**
	 * Tabelas
	 */
	protected static final String TBL_NOTA = "Nota";
	
	private static int VERSAO = 1;
	
	public DatabaseHelper(Context context) {
		super(context, DATA_BASE, null, VERSAO);
	}

	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase) {
		String ddl1 = 
				"CREATE TABLE Registro "+
				"(cliente TEXT," +
				" endereco TEXT," +
				" numero TEXT," +
				" complemento TEXT," +
				" bairro TEXT," +
				" cidade TEXT," +
				" uf TEXT," +
				" cep TEXT," +
				" email TEXT," +
				" senha TEXT," +
				" serial TEXT," +
				" macadress TEXT," +
				" ip TEXT," +
				" documento TEXT," +
				" telefone TEXT," +
				" dispositivo TEXT," +
				" associado TEXT);";
		
		sqLiteDatabase.execSQL(ddl1);
		
		String ddl2 = 
				" CREATE TABLE Resposta "+
				" (codCliente TEXT,"+
				" codDispositivo TEXT,"+
				" status TEXT);";
		
		sqLiteDatabase.execSQL(ddl2);


		String ddl3 = 
				" CREATE TABLE LivrosBaixados "+
				" (codigoLivro TEXT,"+
				" titulo TEXT,"+
				" versao TEXT,"+
				" codigoLoja TEXT,"+
				" idrPath TEXT," +
				" foto TEXT," +
				" arquivoMobile TEXT," +
				" indiceXML TEXT);";
		
		sqLiteDatabase.execSQL(ddl3);

		String ddl4 = 
				" CREATE TABLE "+TBL_NOTA +
				" (pagina INT,"+
				" titulo TEXT," +
				" codigoLivro TEXT,"+
				" nota TEXT);";
		
		sqLiteDatabase.execSQL(ddl4);
		
		
	}

	/**METODO RECEBE COMO PARAMETROS
	 *1) O BANCO DE DADOS
	 *2) A VERSÃO ANTERIOR DA APLICAÇÃO
	 *3) A NOVA VERSÃO DA APLICAÇÃO
	 ***/
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		/**IMPLEMENTAR METODO CASO HAJA NOVA VERSÃO DA APLICAÇÃO 
		 * QUE INCLUA UM ALTERAÇÃO NA TABLE**/
		
	}

}
