package br.com.ibracon.idr.form.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

public class FileUtil {
	
	static Logger logger = Logger.getLogger(FileUtil.class);
	
	public static void copiar(String sourceFile, String destinationFile) {
		logger.debug("De: " + sourceFile);
		logger.debug("Para: " + destinationFile);

		try {
			File inFile = new File(sourceFile);
			File outFile = new File(destinationFile);

			FileInputStream in = new FileInputStream(inFile);
			FileOutputStream out = new FileOutputStream(outFile);

			// CONFIGURACOES DE TRANSFERENCIA
			final int n = 8192;// 4096;
			final byte[] b = new byte[n];

			for (int r = -1; (r = in.read(b, 0, n)) != -1; out.write(b, 0, r)) {
			}
			out.flush();

			in.close();
			out.close();
		} catch (IOException e) {
			System.err.println("Erro ao copiar o arquivo.");
		}
	}

	public static void copiar(InputStream in, String destinationFile) {
		try {
			File outFile = new File(destinationFile);

			FileOutputStream out = new FileOutputStream(outFile);


			// CONFIGURACOES DE TRANSFERENCIA
			final int n = 8192;// 4096;
			final byte[] b = new byte[n];

			for (int r = -1; (r = in.read(b, 0, n)) != -1; out.write(b, 0, r)) {
			}
			out.flush();

			in.close();
			out.close();
		} catch (IOException e) {
			System.err.println("Erro ao copiar o arquivo.");
		}
	}

	public static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		// Agora o diretório está vazio, restando apenas deletá-lo.
		return dir.delete();
	}
}