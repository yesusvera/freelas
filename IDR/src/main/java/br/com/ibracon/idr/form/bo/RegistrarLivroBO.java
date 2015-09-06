package br.com.ibracon.idr.form.bo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;

import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

import org.apache.log4j.Logger;

import br.com.ibracon.idr.form.FormPrincipal;
import br.com.ibracon.idr.form.modal.JanelaProgresso;
import br.com.ibracon.idr.form.model.EstanteXML;
import br.com.ibracon.idr.form.model.Livro;
import br.com.ibracon.idr.webservice.ConnectionWS;
import br.com.ibracon.idr.webservice.ResponseWS;
import br.com.ibracon.idr.webservice.estante.ConnectionEstante;
import br.com.ibracon.idr.webservice.estante.RequestEstante;
import br.com.ibracon.idr.webservice.estante.ResponseEstante;
import br.com.ibracon.idr.webservice.registrarLivro.ConnectionRegistrarLivro;
import br.com.ibracon.idr.webservice.registrarLivro.RequestRegistrarLivro;
import br.com.ibracon.idr.webservice.registrarLivro.ResponseRegistrarLivro;

public class RegistrarLivroBO {
	static Logger logger = Logger.getLogger(RegistrarLivroBO.class);

	public void downloadLivro(final FormPrincipal formPrincipal,
			final Livro livro, final JProgressBar progressBar) {
		downloadLivro(formPrincipal, livro, progressBar, true);
	}

	public void downloadLivro(final FormPrincipal formPrincipal,
			final Livro livro, final JProgressBar progressBar, final boolean registrar) {

		logger.info("Iniciando o download do livro "
				+ livro.getNomeArquivoBaixado());
		new Thread(new Runnable() {
			@Override
			public void run() {
				JanelaProgresso progresso = new JanelaProgresso(formPrincipal);
				try {
					// CONFIGURACOES DE TRANSFERENCIA
					final int n = 8192;// 4096;
					final byte[] b = new byte[n];

					// BAIXANDO ARQUIVO LIVRO
					progresso.aparecer();
					progresso.setPercentual(0);
					progresso.setTexto("Baixando Livro: " + livro.getTitulo());

					URL url = new URL(livro.getArquivo().replace(" ", "%20"));
					logger.info("conectando com a url de download " + url);
					HttpURLConnection httpConnection = (HttpURLConnection) url
							.openConnection();

					logger.info("Abrindo a conexão via protocolo http");
					InputStream in = httpConnection.getInputStream();

					progresso.setPercentual(10);

					progressBar.setIndeterminate(true);

					progresso.setPercentual(20);

					File outFile = new File(InstalacaoBO.getDiretorioBaixados().getPath()
							+ File.separator
							+ livro.getNomeArquivoBaixado());
					FileOutputStream out = new FileOutputStream(outFile);

					progresso.setPercentual(30);

					String tituloCortado = livro.getTitulo();
					if (livro.getTitulo().length() > 20) {
						tituloCortado = livro.getTitulo().substring(0, 20);
					}

					NumberFormat format = NumberFormat.getInstance();
					format.setMaximumFractionDigits(2);

					for (int r = -1; (r = in.read(b, 0, n)) != -1; out.write(b,
							0, r)) {
						progresso.setTexto("     Download - "
								+ tituloCortado
								+ "... ("
								+ format.format((double) (outFile.length() / 1024) / 1024)
								+ " MB)       ");
					}
					out.flush();

					logger.info("    Download do livro finalizado");

					progresso.setPercentual(80);

					in.close();
					out.close();

					progresso.setPercentual(100);

					// BAIXANDO ARQUIVO FOTO
					logger.info("BAIXANDO ARQUIVO FOTO");
					progresso.aparecer();
					progresso.setPercentual(0);

					URL urlFoto = new URL(livro.getFoto().replace(" ", "%20"));
					httpConnection = (HttpURLConnection) urlFoto
							.openConnection();
					in = httpConnection.getInputStream();
					progresso.setTexto("   Baixando Foto ");
					progresso.setPercentual(10);
					progressBar.setIndeterminate(true);
					String nomeFoto = livro.getFoto().substring(
							livro.getFoto().lastIndexOf("/"));
					progresso.setPercentual(20);

					outFile = new File(InstalacaoBO.getDiretorioBaixados().getPath()
							+ File.separator
							+ nomeFoto);
					out = new FileOutputStream(outFile);

					progresso.setPercentual(30);

					for (int r = -1; (r = in.read(b, 0, n)) != -1; out.write(b,
							0, r)) {
						progresso.setTexto("    Download - "
								+ tituloCortado
								+ "... ("
								+ format.format((double) (outFile.length() / 1024) / 1024)
								+ " MB)");
					}
					out.flush();

					logger.info("Arquivo de foto baixado com sucesso.");
					progresso.setPercentual(80);

					in.close();
					out.close();

					progresso.setPercentual(100);

					// REGISTRAR LIVRO
					if (registrar) {
						logger.info("Registrando o livro...");
						progresso.setTexto("Registrando o livro...");

						RequestRegistrarLivro requestRegistrarLivro = new RequestRegistrarLivro();
						requestRegistrarLivro
								.setCliente(formPrincipal.registroXML
										.getResponseRegistrar().getCodCliente());
						requestRegistrarLivro
								.setDocumento(formPrincipal.registroXML
										.getRequestRegistrar().getDocumento());
						requestRegistrarLivro
								.setDispositivo(formPrincipal.registroXML
										.getResponseRegistrar()
										.getCodDispositivo());
						requestRegistrarLivro
								.setKeyworkd(formPrincipal.estanteXML
										.getRequestEstante().getKeyword());
						requestRegistrarLivro.setProduto(livro.getCodigolivro());
						requestRegistrarLivro.setSenha(formPrincipal.estanteXML
								.getRequestEstante().getSenha());
						ConnectionRegistrarLivro connectionRegistrar = new ConnectionRegistrarLivro();
						try {
							ResponseWS resp = connectionRegistrar
									.serviceConnect(requestRegistrarLivro,
											ConnectionWS.WS_REGISTRAR_LIVRO);
							ResponseRegistrarLivro responseRegistrarLivro = (ResponseRegistrarLivro) resp;

							if (responseRegistrarLivro.getErro().equals("0")) {
								JOptionPane.showMessageDialog(null,
										responseRegistrarLivro.getLivro()
												.getStatus());

								logger.info("Livro registrado com sucesso");
								// CONECTA NA ESTANTE NOVAMENTE
								conectaEstante(formPrincipal);

								// ABRIR O LIVRO
								logger.debug(livro);
								File arquivoIdr = new File(InstalacaoBO.getDiretorioBaixados()
										.getAbsolutePath()
										+ File.separator
										+ livro.getNomeArquivoBaixado());
								if (arquivoIdr.exists()) {
									formPrincipal.abrirLivroAPINova(livro);
								}

							} else {
								JOptionPane.showMessageDialog(null,
										responseRegistrarLivro.getMsgErro());
							}
							logger.debug(responseRegistrarLivro);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}else{
						conectaEstante(formPrincipal);
						formPrincipal.mostraLivrosBaixados();
					}
					progresso.encerrar();

				} catch (MalformedURLException e1) {
					logger.error(e1);
					progresso.encerrar();
					e1.printStackTrace();
				} catch (IOException e1) {
					logger.error(e1);
					progresso.encerrar();
					e1.printStackTrace();
				}

			}

			private void conectaEstante(final FormPrincipal formPrincipal) {
				logger.info("Conectando com a estante");
				RequestEstante requestEstante = new RequestEstante();
				requestEstante.setCliente(formPrincipal.registroXML
						.getResponseRegistrar().getCodCliente());
				requestEstante.setDocumento(formPrincipal.registroXML
						.getRequestRegistrar().getDocumento());
				requestEstante.setDispositivo(formPrincipal.registroXML
						.getResponseRegistrar().getCodDispositivo());
				requestEstante.setKeyword(formPrincipal.estanteXML
						.getRequestEstante().getKeyword());
				requestEstante.setSenha(formPrincipal.estanteXML
						.getRequestEstante().getSenha());

				ConnectionEstante connectionEstante = new ConnectionEstante();
				try {
					ResponseWS resp = connectionEstante.serviceConnect(
							requestEstante, ConnectionWS.WS_ESTANTES);
					ResponseEstante responseEstante = (ResponseEstante) resp;

					if (!responseEstante.getErro().equals("0")) {
						logger.info("Conexão com a estante retornou erro "
								+ responseEstante.getMsgErro());
						JOptionPane.showMessageDialog(formPrincipal,
								responseEstante.getMsgErro());
					} else {
						EstanteXML estanteXML = new EstanteXML();
						estanteXML.setRequestEstante(requestEstante);
						estanteXML.setResponseEstante(responseEstante);
						formPrincipal.estanteXML = estanteXML;
						formPrincipal.responseEstanteXML = responseEstante;

						new EstantesBO().gravaEstanteEmDisco(responseEstante);
					}

				} catch (Exception exception) {
					logger.error(exception);
					exception.printStackTrace();
				}

			}
		}).start();

		logger.info("Thread de download iniciada");

	}
}
