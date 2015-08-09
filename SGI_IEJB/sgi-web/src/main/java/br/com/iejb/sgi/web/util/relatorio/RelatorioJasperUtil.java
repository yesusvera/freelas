package br.com.iejb.sgi.web.util.relatorio;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.oasis.JROdtExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import br.com.iejb.sgi.exception.SistemaException;

public class RelatorioJasperUtil {

	/**
	 * Gera arquivo PDF gravando o resultado no response para download do Usuário
	 * @param context
	 * @param nomeJasper
	 * @param parametros
	 * @param nomeArquivo
	 * @param formato
	 * @throws Exception
	 */
	public void gerarRelatorioPdf(FacesContext context, String nomeJasper, Map<String, Object> parametros, String nomeArquivo, Formato formato) throws SistemaException {
		gerarRelatorioPdf(context, nomeJasper, parametros, nomeArquivo, null, formato);
	}

	/**
	 * 
	 * @param context
	 * @param nomeJasper nome do arquivo Arquivo .jasper, se estiver em alguma pasta do WEB-INF/relatorios, informar com a pasta
	 * @param parametros
	 * @param nomeArquivo
	 * @param lista
	 * @param formato PDF
	 * @throws Exception
	 */
	public void gerarRelatorioPdf(FacesContext context, String nomeJasper, Map<String, Object> parametros, String nomeArquivo, Collection<?> lista, Formato formato) throws SistemaException {

		OutputStream os = null;  

		HttpServletResponse response =  (HttpServletResponse) context.getExternalContext().getResponse();
		String caminhoReportDir = recuperaCaminhoRelatorio(context);

		parametros.put(JRParameter.REPORT_LOCALE, new Locale("pt","BR"));
		parametros.put("CAMINHO_RELATORIO", caminhoReportDir);
		parametros.put("SUBREPORT_DIR", caminhoReportDir);

		JasperPrint impressao = null;


		// GERA O RELATORIO E O DEFINE COMO CONTEUDO DO 'RESPONSE'
		try{
			JasperReport jasper  =  JasperCompileManager.compileReport(caminhoReportDir+nomeJasper);
//			InputStream jasper = new FileInputStream(caminhoReportDir+nomeJasper);
			if(lista != null){
				JRBeanCollectionDataSource datasource = new JRBeanCollectionDataSource(lista);
				impressao = JasperFillManager.fillReport(jasper, parametros, datasource);
			}else{
				impressao = JasperFillManager.fillReport(jasper, parametros);
			}
			byte[] arquivo = null;
			os = response.getOutputStream();  
			response.setContentType("application/download");  
			response.setHeader("Content-disposition","attachment; filename=\""+nomeArquivo+formato.getExtensao()+"\"");  
			
			if(Formato.RTF.equals(formato)){
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				JRRtfExporter rtf = new JRRtfExporter();
				rtf.setParameter(JRExporterParameter.JASPER_PRINT, impressao);
				rtf.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
				rtf.exportReport();
				String retorno = out.toString().replaceAll("\\\\line", "\\\\par");
				out = new ByteArrayOutputStream();
				out.write(retorno.getBytes());
				arquivo = out.toByteArray();
			}else if(Formato.ODT.equals(formato)){
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				JROdtExporter ods = new JROdtExporter();
				ods.setParameter(JRExporterParameter.JASPER_PRINT, impressao);
				ods.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
				ods.exportReport();
				arquivo = out.toByteArray();
			}else if(Formato.DOCX.equals(formato)){
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				JRDocxExporter ods = new JRDocxExporter();
				ods.setParameter(JRExporterParameter.JASPER_PRINT, impressao);
				ods.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
				ods.exportReport();
				arquivo = out.toByteArray();
			}else{
				arquivo = JasperExportManager.exportReportToPdf(impressao);
			}
		
			response.setContentLength(arquivo.length);
			os.write(arquivo);  
			os.flush(); 
		} catch (JRException e) {
			throw new SistemaException(e.getMessage());
		} catch (IOException e) {
			throw new SistemaException(e.getMessage());
		}finally{
			try {  
				if(os != null){  
					os.close();  
				}  
			} catch (IOException e) {  
				e.printStackTrace();  
			} 
		}
	}


	/**
	 * 
	 * @param context
	 * @return
	 */
	public static String recuperaCaminhoRelatorio(FacesContext context) {
		String caminhoAplicacao = null;
		String caminhoRelatorio = null;
		if (context != null) {
			caminhoAplicacao = context.getExternalContext().getRealPath("/");
			caminhoRelatorio = "WEB-INF" + java.io.File.separator + "relatorios" + java.io.File.separator;
		}
		return caminhoAplicacao + caminhoRelatorio ;
	}
}

