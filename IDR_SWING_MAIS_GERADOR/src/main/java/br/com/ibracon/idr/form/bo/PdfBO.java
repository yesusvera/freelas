package br.com.ibracon.idr.form.bo;

import java.io.File;

import org.apache.log4j.Logger;

public class PdfBO {

	static Logger logger = Logger.getLogger(PdfBO.class);
	
	InstalacaoBO instalacaoBO = new InstalacaoBO();

	public File getDiretorioNotas(String serialPDF) {
		String dirNotas = instalacaoBO.getPathInstalacao()
				+ File.separator + serialPDF;
		File dirPDF = new File(dirNotas);
		if (!dirPDF.exists()) {
			dirPDF.mkdir();
			logger.info("Diretório "+dirNotas+" criado.");
		}
		return dirPDF;
	}
}
