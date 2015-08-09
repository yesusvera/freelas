package br.com.ibracon.idr.form.bo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.NoSuchPaddingException;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import br.com.ibracon.idr.form.FormPrincipal;
import br.com.ibracon.idr.form.criptografia.FileCrypt;
import br.com.ibracon.idr.form.model.RegistroXml;
import br.com.ibracon.idr.form.util.IdrUtil;
import br.com.ibracon.idr.webservice.ConnectionWS;
import br.com.ibracon.idr.webservice.ResponseWS;
import br.com.ibracon.idr.webservice.registrar.ConnectionRegistrar;
import br.com.ibracon.idr.webservice.registrar.RequestRegistrar;
import br.com.ibracon.idr.webservice.registrar.ResponseRegistrar;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * The Class RegistroBO.
 */
public class RegistroBO {

	static Logger logger = Logger.getLogger(RegistroBO.class);
	
	private static final String REGISTRAR_LIVRO_IDR_LIC = "registroLivroIDR.lic";
	InstalacaoBO instalacaoBO = new InstalacaoBO();
	FileCrypt cripto = new FileCrypt(FileCrypt.CHAVE_LIVRO_IDR);

	public String hostName() {
		InetAddress ip;
		try {
			ip = InetAddress.getLocalHost();
			logger.info("hostname: " + ip);
			return ip.getHostName();
		} catch (UnknownHostException e) {
			logger.info(e.getMessage());
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * Ip maquina cliente.
	 * 
	 * @return the string
	 */
	public String ipMaquinaCliente() {
		try {
			InetAddress ip = InetAddress.getLocalHost();
			return ip.getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			logger.info(e.getMessage());
		}
		return "";
	}

	/**
	 * Mac adress maquina cliente.
	 * 
	 * @return the string
	 */
	public String macAdressMaquinaCliente() {
		try {
			InetAddress ip = InetAddress.getLocalHost();
			NetworkInterface ni = NetworkInterface.getByInetAddress(ip);
			byte[] mac = ni.getHardwareAddress();

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < mac.length; i++) {
				sb.append(String.format("%02X%s", mac[i],
						(i < mac.length - 1) ? "-" : ""));
			}
			logger.info("Mac adress capturado " + sb.toString());
			return sb.toString();
		} catch (SocketException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		} catch (UnknownHostException e1) {
			logger.error(e1.getMessage());
			e1.printStackTrace();
		} catch (NullPointerException npe){
			logger.error(npe.getMessage());
			npe.printStackTrace();
		}
		return "";
	}

	public String getHDSerial() {
		String os = System.getProperty("os.name");

		try {
			if (os.startsWith("Windows")) {
				return getHDSerialWindows("C");
			} else if (os.startsWith("Linux")) {
				return getHDSerialLinux();
			} else if (os.startsWith("Mac OS X")) {
				return getHDSerialMacOsX();
			} else {
				logger.debug(new IOException("unknown operating system: "
						+ os));
			}
			logger.info("Capturado o OS: " + os);
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(ex.getCause());
		}

		return "";
	}

	public final static String getCPUSerial() throws IOException {
		String os = System.getProperty("os.name");

		try {
			if (os.startsWith("Windows")) {
				return getCPUSerialWindows();
			} else if (os.startsWith("Linux")) {
				return getCPUSerialLinux();
			} else {
				throw new IOException("unknown operating system: " + os);
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			ex.printStackTrace();
			throw new IOException(ex.getMessage());
		}
	}

	private final static String getMotherboardSerial() throws IOException {
		String os = System.getProperty("os.name");

		try {
			if (os.startsWith("Windows")) {
				return getMotherboardSerialWindows();
			} else if (os.startsWith("Linux")) {
				return getMotherboardSerialLinux();
			} else {
				throw new IOException("unknown operating system: " + os);
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			ex.printStackTrace();
			throw new IOException(ex.getMessage());
		}
	}

	// Implementacoes

	/*
	 * Captura serial de placa mae no WINDOWS, atraves da execucao de script
	 * visual basic
	 */
	public static String getMotherboardSerialWindows() {
		String result = "";
		try {
			File file = File.createTempFile("realhowto", ".vbs");
			file.deleteOnExit();
			FileWriter fw = new java.io.FileWriter(file);

			String vbs = "Set objWMIService = GetObject(\"winmgmts:\\\\.\\root\\cimv2\")\n"
					+ "Set colItems = objWMIService.ExecQuery _ \n"
					+ "   (\"Select * from Win32_BaseBoard\") \n"
					+ "For Each objItem in colItems \n"
					+ "    Wscript.Echo objItem.SerialNumber \n"
					+ "    exit for  ' do the first cpu only! \n" + "Next \n";

			fw.write(vbs);
			fw.close();
			Process p = Runtime.getRuntime().exec(
					"cscript //NoLogo " + file.getPath());
			BufferedReader input = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			String line;
			while ((line = input.readLine()) != null) {
				result += line;
			}
			input.close();
		} catch (Exception e) {
			logger.error(e.getCause());
			e.printStackTrace();
		}
		return result.trim();
	}

	/*
	 * Captura serial de placa mae em sistemas LINUX, atraves da execucao de
	 * comandos em shell.
	 */
	public static String getMotherboardSerialLinux() {
		String result = "";
		try {
			String[] args = { "bash", "-c", "lshw -class bus | grep serial" };
			Process p = Runtime.getRuntime().exec(args);
			BufferedReader input = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			String line;
			while ((line = input.readLine()) != null) {
				result += line;
			}
			input.close();

		} catch (Exception e) {

		}
		if (result.trim().length() < 1 || result == null) {
			result = "NO_DISK_ID";

		}

		return filtraString(result, "serial: ");

	}

	/*
	 * Captura serial de HD no WINDOWS, atraves da execucao de script visual
	 * basic
	 */
	public static String getHDSerialWindows(String drive) {
		String result = "";
		try {
			File file = File.createTempFile("tmp", ".vbs");
			file.deleteOnExit();
			FileWriter fw = new java.io.FileWriter(file);
			String vbs = "Set objFSO = CreateObject(\"Scripting.FileSystemObject\")\n"
					+ "Set colDrives = objFSO.Drives\n"
					+ "Set objDrive = colDrives.item(\""
					+ drive
					+ "\")\n"
					+ "Wscript.Echo objDrive.SerialNumber";
			fw.write(vbs);
			fw.close();
			Process p = Runtime.getRuntime().exec(
					"cscript //NoLogo " + file.getPath());
			BufferedReader input = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			String line;
			while ((line = input.readLine()) != null) {
				result += line;
			}
			input.close();
		} catch (Exception e) {

		}
		if (result.trim().length() < 1 || result == null) {
			result = "NO_DISK_ID";
		}
		return result.trim();
	}

	/*
	 * Captura serial de HD em sistemas Linux, atraves da execucao de comandos
	 * em shell.
	 */
	public static String getHDSerialLinux() {
		String result = "";
		try {
			String[] args = { "bash", "-c", "lshw -class disk | grep serial" };
			Process p = Runtime.getRuntime().exec(args);
			BufferedReader input = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			String line;
			while ((line = input.readLine()) != null) {
				result += line;
			}
			input.close();

		} catch (Exception e) {

		}
		if (result.trim().length() < 1 || result == null) {
			result = "NO_DISK_ID";

		}

		return filtraString(result, "serial: ");

	}

	public static String getHDSerialMacOsX() {
		String result = "";
		try {
			String[] args = { "bash", "-c",
					"system_profiler SPHardwareDataType | awk '/Serial/ {print $4}'" };
			Process p = Runtime.getRuntime().exec(args);
			BufferedReader input = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			String line;
			while ((line = input.readLine()) != null) {
				result += line;
			}
			input.close();

		} catch (Exception e) {

		}
		if (result.trim().length() < 1 || result == null) {
			result = "NO_DISK_ID";

		}

		return result;

	}

	/*
	 * Captura serial da CPU no WINDOWS, atraves da execucao de script visual
	 * basic
	 */
	public static String getCPUSerialWindows() {
		String result = "";
		try {
			File file = File.createTempFile("tmp", ".vbs");
			file.deleteOnExit();
			FileWriter fw = new java.io.FileWriter(file);

			String vbs = "On Error Resume Next \r\n\r\n"
					+ "strComputer = \".\"  \r\n"
					+ "Set objWMIService = GetObject(\"winmgmts:\" _ \r\n"
					+ "    & \"{impersonationLevel=impersonate}!\\\\\" & strComputer & \"\\root\\cimv2\") \r\n"
					+ "Set colItems = objWMIService.ExecQuery(\"Select * from Win32_Processor\")  \r\n "
					+ "For Each objItem in colItems\r\n "
					+ "    Wscript.Echo objItem.ProcessorId  \r\n "
					+ "    exit for  ' do the first cpu only! \r\n"
					+ "Next                    ";

			fw.write(vbs);
			fw.close();
			Process p = Runtime.getRuntime().exec(
					"cscript //NoLogo " + file.getPath());
			BufferedReader input = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			String line;
			while ((line = input.readLine()) != null) {
				result += line;
			}
			input.close();
		} catch (Exception e) {

		}
		if (result.trim().length() < 1 || result == null) {
			result = "NO_CPU_ID";
		}
		return result.trim();
	}

	/*
	 * Captura serial de CPU em sistemas Linux, atraves da execucao de comandos
	 * em shell.
	 */
	public static String getCPUSerialLinux() {
		String result = "";
		try {
			String[] args = { "bash", "-c",
					"lshw -class processor | grep serial" };
			Process p = Runtime.getRuntime().exec(args);
			BufferedReader input = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			String line;
			while ((line = input.readLine()) != null) {
				result += line;
			}
			input.close();

		} catch (Exception e) {

		}
		if (result.trim().length() < 1 || result == null) {
			result = "NO_DISK_ID";

		}

		return filtraString(result, "serial: ");
	}

	public static String filtraString(String nome, String delimitador) {
		return nome.split(delimitador)[1];
	}

	public void gravaRegistroEmDisco(RequestRegistrar requestRegistrar,
			ResponseRegistrar responseRegistrar) throws IOException {

		logger.info("Gravando o registro em disco");
		
		logger.info("MONTA O OBJETO DE REGISTRO");
		// MONTA O OBJETO DE REGISTRO
		RegistroXml registroXML = new RegistroXml();
		registroXML.setRequestRegistrar(requestRegistrar);
		registroXML.setResponseRegistrar(responseRegistrar);

		logger.info("PEGA O XML");
		// PEGA O XML
		XStream xstream = new XStream();
		xstream.alias("registroXML", RegistroXml.class);
		String strRegistroXML = xstream.toXML(registroXML);

		
		// GRAVA O XML REGISTRO EM DISCO
		File fileXML = new File(instalacaoBO.getPathInstalacao()
				+ File.separator + "registroLivroIDR.xml");
		if (fileXML.exists()) {
			fileXML.delete();
		}
		fileXML.createNewFile();
		logger.info("GRAVA O XML REGISTRO EM DISCO : " + fileXML);

		FileWriter fw = new FileWriter(fileXML);
		BufferedWriter bf = new BufferedWriter(fw);

		bf.write(strRegistroXML);
		bf.close();
		fw.close();

		logger.info("CRIPTOGRAFA O REGISTRO");
		// CRIPTOGRAFA O REGISTRO
		File fileXMLCripto = new File(instalacaoBO.getPathInstalacao()
				+ File.separator + REGISTRAR_LIVRO_IDR_LIC);

		try {
			cripto.criptografa(new FileInputStream(fileXML),
					new FileOutputStream(fileXMLCripto));
		} catch (InvalidKeyException | NoSuchAlgorithmException
				| NoSuchPaddingException | InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}

		logger.info("DELETA O ARQUIVO XML");
		// DELETA O ARQUIVO XML
		fileXML.delete();

	}

	/**
	 * Sempre que o aplicativo for instanciado, deve-se verificar se o valor de
	 * macadress e serial são compatíveis (idênticos) ao que foi estocado em
	 * arquivo local para garantir que o cliente não fez uma cópia das pastas do
	 * aplicativo e as levou para um novo dispositivo (outro computador por
	 * exemplo). Se forem diferentes, o sistema deve assumir que trata-se de uma
	 * cópia do aplicativo levada a outro dispositivo (onde o cliente levou o
	 * arquivo de cadastro local, mas foi pego ao tentar usar outro macadress e
	 * serial que são obtidos dinamicamente). Neste caso o aplicativo deve
	 * informar ao usuário que: “Os dados cadastrais deste aplicativo não
	 * condizem com o computador que foi registrado, para utilizar o aplicativo
	 * em outro dispositivo queira por gentileza acessar o site do IBRACON e
	 * efetuar novo download do aplicativo”.
	 * 
	 * @return
	 */
	public boolean licencaEValida(FormPrincipal formPrincipal) {
		logger.info("Valida a licença do leitor");
		
		File fileXMLCripto = new File(instalacaoBO.getPathInstalacao()
				+ File.separator + REGISTRAR_LIVRO_IDR_LIC);

		if (!fileXMLCripto.exists()) {
			logger.info("Licença não é valida");
			return false;
		}

		File fileXmlDescript = new File(instalacaoBO.getPathInstalacao()
				+ File.separator + REGISTRAR_LIVRO_IDR_LIC + ".xml");	

		try {
			logger.info("Verificando licença");
			// DESCRIPTOGRAFA O .LIC
			cripto.descriptografa(new FileInputStream(fileXMLCripto),
					new FileOutputStream(fileXmlDescript));

			if (fileXmlDescript.exists()) {
				// MONTA OBJETO
				XStream xStream = new XStream(new DomDriver());
				xStream.processAnnotations(RegistroXml.class);
				RegistroXml registroXML = (RegistroXml) xStream
						.fromXML(new InputStreamReader(new FileInputStream(fileXmlDescript), "ISO-8859-1")) ;
				
				// DELETA O DESCRIPTOGRAFADO
				fileXmlDescript.delete();

				
				formPrincipal.registroXML = registroXML;
				
				boolean registroValido = false;
				
				logger.info("Verifica se internet está ativa");
				if(IdrUtil.internetEstaAtiva()){
					// VERIFICA SE O SERIAL E MACADRESS SAO IGUAIS
					if(registroXML.getRequestRegistrar().getSerial()
						.equalsIgnoreCase(getHDSerial())
						&& registroXML.getRequestRegistrar().getMacadress()
								.equalsIgnoreCase(macAdressMaquinaCliente())){
						registroValido = true;
					}
				}else{
					logger.info("Leitor sem internet. TESTANDO O MACADRESS");
					//SE NAO TEM INTERNET NAO TESTAR O MACADRESS
					if(registroXML.getRequestRegistrar().getSerial()
							.equalsIgnoreCase(getHDSerial())){
							registroValido = true;
					}else{
						return false;
					}
				}
				
				if (registroValido) {
					logger.info("O registro é válido");
					/**
					 * Se o macadress e serial do registro (arquivo local) e os
					 * obtidos (nova chamada) forem iguais efetue a chamada de
					 * ws_registrar e passe todos os parâmetros(como da primeira
					 * vez).
					 */

					ConnectionRegistrar connectionRegistrar = new ConnectionRegistrar();
					try {
						ResponseWS resp = connectionRegistrar.serviceConnect(
								registroXML.getRequestRegistrar(),
								ConnectionWS.WS_REGISTRAR);
						ResponseRegistrar responseRegistrar = (ResponseRegistrar) resp;

						logger.debug(responseRegistrar);

						
						verificarVersaoLeitor(responseRegistrar);
						
						
						if (responseRegistrar.getErro() != null
								&& !responseRegistrar.getErro().equals("0")) {
							JOptionPane.showMessageDialog(null,
									responseRegistrar.getMsgErro(),
									"Erro ao registrar",
									JOptionPane.ERROR_MESSAGE);
						} else {

							// SE ESTA ATIVADO
							if (responseRegistrar.getStatus().equalsIgnoreCase(
									"ativado")) {
								logger.info("Leitor ativado");
								/**
								 * Caso o codCliente e codDispositivo para o
								 * MacAdress e Serial passado (todas as vezes
								 * que o registro for chamado) forem iguais quer
								 * dizer que o cliente está fazendo o uso
								 * previsto do aplicativo, usando ele no
								 * computador em que baixou.
								 */
								if (!responseRegistrar.getCodCliente()
										.equalsIgnoreCase(
												registroXML
														.getResponseRegistrar()
														.getCodCliente())
										|| !responseRegistrar
												.getCodDispositivo()
												.equalsIgnoreCase(
														registroXML
																.getResponseRegistrar()
																.getCodDispositivo())) {
									/**
									 * Se forem diferentes, o sistema deve
									 * assumir que trata-se de uma cópia do
									 * aplicativo levada a outro dispositivo
									 * (onde o cliente levou o arquivo de
									 * cadastro local, mas foi pego ao tentar
									 * usar outro macadress e serial que são
									 * obtidos dinamicamente).
									 */
									JOptionPane
											.showMessageDialog(
													null,
													"Os dados cadastrais deste aplicativo não condizem com o computador que foi registrado,\n "
															+ "para utilizar o aplicativo em outro dispositivo queira por gentileza\n"
															+ " acessar o site do IBRACON e efetuar novo download do aplicativo");
									fileXMLCripto.delete();
									return false;
								}
							}//TODO IMPLEMENTAR O RETORNO DE ERRO CASO NAO REGISTRADO
						}
					} catch (Exception exception) {
						exception.printStackTrace();
					}

					formPrincipal.registroXML = registroXML;
					return true;
				} else {
					/**
					 * Se forem diferentes, o sistema deve assumir que trata-se
					 * de uma cópia do aplicativo levada a outro dispositivo
					 * (onde o cliente levou o arquivo de cadastro local, mas
					 * foi pego ao tentar usar outro macadress e serial que são
					 * obtidos dinamicamente).
					 */
					JOptionPane
							.showMessageDialog(
									null,
									"Os dados cadastrais deste aplicativo não condizem com o computador que foi registrado,\n "
											+ "para utilizar o aplicativo em outro dispositivo queira por gentileza\n"
											+ " acessar o site do IBRACON e efetuar novo download do aplicativo");
					fileXMLCripto.delete();
					return false;
				}
			}
		} catch (InvalidKeyException | NoSuchAlgorithmException
				| NoSuchPaddingException | InvalidAlgorithmParameterException
				| FileNotFoundException e) {
			logger.error(e);
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return false;
	}

	public void verificarVersaoLeitor(ResponseRegistrar responseRegistrar) {
		logger.info("Verificando a versão do Leitor");
		/**
		 * VERIFICANDO A VERSAO DO LEITOR
		 */
		String versaoAppLocal = PropertiesBO.findWebServicesProperties()
				.getString("versaoAppLocal");
		
		logger.info("Versão atual " + versaoAppLocal);
		
		if (responseRegistrar!=null && !versaoAppLocal.trim().equals(responseRegistrar.getAppVersion().trim())) {
			/**
			 * Caso o valor de informação de appVersion seja
			 * diferente do valor estocado alertar o
			 * cliente: “Há uma nova versão do software
			 * IDR-Ibracon Digital Reader a disposição para
			 * download, deseja atualizar?” com opção de SIM
			 * ou NÃO, caso o usuário escolha SIM abrir um
			 * navegador com o endereço do portal (definido
			 * via parâmetro em arquivo do leitor que iremos
			 * distribuir). Se o cliente optar por NÃO então
			 * não fazemos nada.
			 */
			
			logger.info("Versão nova encontrada " + responseRegistrar.getAppVersion());
			if (JOptionPane
					.showConfirmDialog(
							null,
							"Há uma nova versão ("
									+ responseRegistrar
											.getAppVersion()
									+ ") do software IDR-Ibracon Digital "
									+ "Reader a disposição para download, deseja atualizar?",
									"Versão "
									+ responseRegistrar
											.getAppVersion()
									+ " para download",
							JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				logger.info("Redirecionando para o download de nova versão: " + responseRegistrar.getUrlApp());
				IdrUtil.abrirBrowser(responseRegistrar.getUrlApp());
			}
		}
	}

	public boolean registrarDispositivo(RequestRegistrar requestRegistrar, FormPrincipal formPrincipal) {
		logger.info("Registrando o dispositivo");
		ConnectionRegistrar connectionRegistrar = new ConnectionRegistrar();
		try {
			ResponseWS resp = connectionRegistrar.serviceConnect(
					requestRegistrar, ConnectionWS.WS_REGISTRAR);
			ResponseRegistrar responseRegistrar = (ResponseRegistrar) resp;

			
			verificarVersaoLeitor(responseRegistrar);
			
			logger.debug(responseRegistrar);

			if (responseRegistrar.getErro() != null
					&& !responseRegistrar.getErro().equals("0")) {
				JOptionPane.showMessageDialog(null,
						responseRegistrar.getMsgErro(), "Erro ao registrar",
						JOptionPane.ERROR_MESSAGE);
			} else {
				// SE ESTA ATIVADO
				if (responseRegistrar.getStatus().equalsIgnoreCase("ativado")) {
					logger.info("Dispositivo ativado. Gravando o registro.");
					// GRAVA O REGISTRO
					gravaRegistroEmDisco(requestRegistrar, responseRegistrar);
					JOptionPane.showMessageDialog(null,
							"Leitor registrado com sucesso!" ,
							"Registro Efetuado",
							JOptionPane.INFORMATION_MESSAGE);
					
					RegistroXml registroXML = new RegistroXml();
					registroXML.setRequestRegistrar(requestRegistrar);
					registroXML.setResponseRegistrar(responseRegistrar);
					
					formPrincipal.registroXML = registroXML;
					return true;
				}
			}
		} catch (Exception exception) {
			JOptionPane.showMessageDialog(null, "Contate o administrador: "
					+ exception.getMessage());
			exception.printStackTrace();
			logger.error(exception);
		}

		return false;
	}
}
