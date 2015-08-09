package br.com.ibracon.idr.form.bo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.log4j.Logger;

import br.com.ibracon.idr.form.FormPrincipal;
import br.com.ibracon.idr.form.model.Nota;
import br.com.ibracon.idr.form.util.FileUtil;

public class NotaBO {

	static Logger logger = Logger.getLogger(NotaBO.class);
	
	
	InstalacaoBO instalacaoBO = new InstalacaoBO();
	PdfBO pdfBO = new PdfBO();

	public void salvarNota(Nota nota, String serialPDF) {
		logger.info("Salvando notas");
		File dirPdf = pdfBO.getDiretorioNotas(serialPDF);
		File arquivoNota = getArquivoNota(nota.getPagina(), dirPdf.getPath());
		Properties properties = new Properties();
		properties.setProperty("titulo", nota.getTitulo());
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		properties
				.setProperty("dataCriacao", sdf.format(nota.getDataCriacao()));
		properties.setProperty("dataModificacao",
				sdf.format(nota.getDataModificacao()));
		properties.setProperty("texto", nota.getTexto());
		properties.setProperty("pagina", String.valueOf(nota.getPagina()));
		try {
			properties
					.store(new FileOutputStream(arquivoNota), "Salvando nota");
		} catch (FileNotFoundException e) {
			logger.error(e);
			e.printStackTrace();
		} catch (IOException e) {
			logger.error(e);
			e.printStackTrace();
		}
	}
	
	public void excluirNota(int pagina, String serialPDF){
		logger.info("Excluindo nota página " + pagina + " do livro " + serialPDF);
		File dirPdf = pdfBO.getDiretorioNotas(serialPDF);
		File arquivoNota = getArquivoNota(pagina, dirPdf.getPath());
		
		if(arquivoNota.exists()){
			logger.info("Nota excluída com sucesso");
			arquivoNota.delete();
		}
	}
	
	public boolean existeNotaSalva(int pagina, String serialPDF){
		String nomeArquivo = String.valueOf(pagina) + ".properties";
		
		File dirPdf = pdfBO.getDiretorioNotas(serialPDF);

		String pathArquivo = dirPdf.getPath()
				+ File.separator + nomeArquivo;
		File arquivoNota = new File(pathArquivo);
		
		return arquivoNota.exists();
	}

	public File getArquivoNota(int pagina, String diretorioArquivoNotas) {
		String nomeArquivo = String.valueOf(pagina) + ".properties";

		String pathArquivo = diretorioArquivoNotas
				+ File.separator + nomeArquivo;
		File arquivoNota = new File(pathArquivo);

		logger.info("Capturando arquivo notas: "  + arquivoNota );
		if (!arquivoNota.exists()) {
			FileUtil.copiar( FormPrincipal.class
					.getResourceAsStream("configuracoes/nota.properties"), pathArquivo);
		}
		return arquivoNota;
	}

	public Properties pesquisarNota(int pagina, String serialPDF) {
		File dirPdf = pdfBO.getDiretorioNotas(serialPDF);
		File arquivoNota = getArquivoNota(pagina, dirPdf.getPath());
		FileInputStream input;
		try {
			input = new FileInputStream(arquivoNota.getPath());
			Properties prop = new Properties();
			prop.load(input);
			return prop;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new Properties();
	}

	public ArrayList<Nota> listaNotasGravadas(String serialPDF){
		File dirPdf = pdfBO.getDiretorioNotas(serialPDF);

		ArrayList<Nota> listaNotas = new ArrayList<Nota>();
		if (dirPdf.exists() && dirPdf.isDirectory()) {
			for (File arquivo : dirPdf.listFiles()) {
			
				try {
					if(arquivo.isDirectory()){
						continue;
					}
					
					FileInputStream input = new FileInputStream(arquivo);
					Properties prop = new Properties();
					prop.load(input);
					Nota nota = new Nota();
					nota.setTitulo(prop.getProperty("titulo"));
					nota.setTexto(prop.getProperty("texto"));
					try{
					nota.setPagina(Integer.valueOf(prop.getProperty("pagina")));
					}catch(NumberFormatException nfe){
					}
					if(nota.getPagina()>0){
						listaNotas.add(nota);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return listaNotas;
		}
		return null;
	}
}
