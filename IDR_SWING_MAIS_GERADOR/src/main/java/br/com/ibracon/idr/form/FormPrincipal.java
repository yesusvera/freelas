package br.com.ibracon.idr.form;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.InvocationTargetException;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Properties;

import javax.crypto.NoSuchPaddingException;
import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.print.attribute.AttributeSetUtilities;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.CopiesSupported;
import javax.print.attribute.standard.MultipleDocumentHandling;
import javax.print.attribute.standard.PageRanges;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DebugGraphics;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import net.java.dev.designgridlayout.DesignGridLayout;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.record.aggregates.PageSettingsBlock;

import br.com.ibracon.idr.form.bo.EstantesBO;
import br.com.ibracon.idr.form.bo.IndiceBO;
import br.com.ibracon.idr.form.bo.InstalacaoBO;
import br.com.ibracon.idr.form.bo.LivroIdrBO;
import br.com.ibracon.idr.form.bo.NotaBO;
import br.com.ibracon.idr.form.bo.ProxyBO;
import br.com.ibracon.idr.form.bo.RegistrarLivroBO;
import br.com.ibracon.idr.form.bo.RegistroBO;
import br.com.ibracon.idr.form.indexador.Buscador;
import br.com.ibracon.idr.form.modal.JanelaBoasVindas;
import br.com.ibracon.idr.form.modal.JanelaConfProxy;
import br.com.ibracon.idr.form.modal.JanelaNota;
import br.com.ibracon.idr.form.modal.JanelaProgresso;
import br.com.ibracon.idr.form.modal.JanelaRegistro;
import br.com.ibracon.idr.form.modal.JanelaSplash;
import br.com.ibracon.idr.form.model.EstanteXML;
import br.com.ibracon.idr.form.model.ItemResultado;
import br.com.ibracon.idr.form.model.Livro;
import br.com.ibracon.idr.form.model.LivroIDR;
import br.com.ibracon.idr.form.model.Nota;
import br.com.ibracon.idr.form.model.RegistroXml;
import br.com.ibracon.idr.form.model.indice.Item;
import br.com.ibracon.idr.form.util.IdrUtil;
import br.com.ibracon.idr.webservice.estante.ResponseEstante;

import com.sun.pdfview.OutlineNode;
import com.sun.pdfview.PDFDestination;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFObject;
import com.sun.pdfview.PDFPage;
import com.sun.pdfview.PageChangeListener;
import com.sun.pdfview.PagePanel;
import com.sun.pdfview.ThumbPanel;
import com.sun.pdfview.action.GoToAction;
import com.sun.pdfview.action.PDFAction;

/**
 * The Class FormPrincipal.
 */
public class FormPrincipal extends JFrame implements KeyListener,
		TreeSelectionListener, PageChangeListener {
	
	static Logger logger = Logger.getLogger(FormPrincipal.class);
	
	/** The Constant TITLE. */
	public final static String TITLE = "IDR - Ibracon";
	
	// start the viewer
	public static FormPrincipal formPrincipal;

	InstalacaoBO instalacaoBO = new InstalacaoBO();

	/** The cur file. */
	PDFFile curFile;

	/** The doc name. */
	String docName;

	/** The split. */
	JSplitPane split;

	/** The thumbscroll. */
	JScrollPane thumbscroll;

	/** The thumbs. */
	ThumbPanel thumbs;

	/** The page. */
	PagePanel page;

	/** The fspp. */
	PagePanel fspp;

	/** The curpage. */
	int curpage = -1;

	/** The full screen button. */
	JToggleButton fullScreenButton;

	/** The page field. */
	JTextField pageField;

	/** The num pages label. */
	JLabel numPagesLabel;

	/** The qtde zoom field. */
	JTextField qtdeZoomField;

	JSlider sliderZoom;

	/** The qtde zoom label. */
	JLabel qtdeZoomLabel;

	/** The qtde zoom percentual label. */
	JLabel qtdeZoomPercentualLabel;

	/** The full screen. */
	JanelaTelaCheia fullScreen;

	/** The indice. */
	OutlineNode indice = null;

	/** The pformat. */
	PageFormat pformat = PrinterJob.getPrinterJob().defaultPage();

	/** The fazer miniatura. */
	boolean fazerMiniatura = true;

	/** The doc waiter. */
	Flag docWaiter;

	/** The olf. */
	JDialog olf;

	/** The doc menu. */
	JMenu docMenu;

	/** The slider navegacao. */
	JSlider sliderNavegacao;

	/** The abas. */
	JTabbedPane abas;

	/** The scroll indice. */
	JScrollPane scrollIndice;

	JanelaProgresso jp;

	File selectedFile = null;

	/** The scroll pagina. */
	JScrollPane scrollPagina;

	/** The estante. */
	JPanel estante;

	JPanel notas;

	/** The painel central. */
	JPanel painelCentral;

	JPanel pnlPage;

	DesignGridLayout designPainelCentral;

	JTextField pesquisaField;

	JLabel pesquisaLabel;

	JButton notaBtn;

	String serialPDF = "";

	JPanel pnlPagina;

	JScrollBar barraRolagemVertical;

	JScrollBar barraRolagemHorizontal;

	public RegistroXml registroXML;

	public ResponseEstante responseEstanteXML;
	public ResponseEstante responseEstanteXMLLocal;

	public EstanteXML estanteXML;
	
	public LivroIDR livroIDR = null;
	
	private	JList<ItemResultado> resultadoJList;
	
	private ArrayList<ItemResultado> listaResultadoPesquisa;

	/**
	 * Gets the icon.
	 * 
	 * @param name
	 *            the name
	 * @return the icon
	 */
	public static Icon getIcon(String name) {
		Icon icon = null;
		URL url = null;
		try {
			url = FormPrincipal.class.getResource(name);

			icon = new ImageIcon(url);
		} catch (Exception e) {
			logger.debug("Couldn't find " + FormPrincipal.class.getName()
					+ "/" + name);
			e.printStackTrace();
		}
		return icon;
	}

	Action proxyAction = new AbstractAction("Proxy") {

		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent evt) {
			new JanelaConfProxy(getInstance());
		}
	};

	// / FILE MENU
	/** The open action. */
	Action openAction = new AbstractAction("Abrir...") {
		public void actionPerformed(ActionEvent evt) {
			abrir();
		}
	};

	// / FILE MENU
	/** The open action. */
	Action sobreAction = new AbstractAction("Sobre...") {
		public void actionPerformed(ActionEvent evt) {
			ImageIcon img = new ImageIcon(FormPrincipal.class.getResource(
					"gfx/splash.png").getFile());
			JanelaSplash telaSplash = new JanelaSplash(5000, img, true);
			telaSplash.setVisible(true);
		}
	};

	/** The page setup action. */
	Action pageSetupAction = new AbstractAction("Configurar página...") {
		public void actionPerformed(ActionEvent evt) {
			doPageSetup();
		}
	};

	/** The print action. */
	Action printAction = new AbstractAction("Imprimir...",
			getIcon("gfx/print.gif")) {
		public void actionPerformed(ActionEvent evt) {
			imprimirPaginaAtual();
		}
	};

	/** The close action. */
	Action closeAction = new AbstractAction("Fechar livro") {
		public void actionPerformed(ActionEvent evt) {
			doClose();
			getInstance().abrir("configuracoes/ibracon.pdf");
			recarregaPagina();
		}
	};

	/** The quit action. */
	Action quitAction = new AbstractAction("Sair do leitor") {
		public void actionPerformed(ActionEvent evt) {
			doQuit();
		}
	};

	/**
	 * The Class ZoomAction.
	 */
	class ZoomAction extends AbstractAction {

		/** The zoomfactor. */
		double zoomfactor = 1.0;

		/**
		 * Instantiates a new zoom action.
		 * 
		 * @param name
		 *            the name
		 * @param factor
		 *            the factor
		 */
		public ZoomAction(String name, double factor) {
			super(name);
			zoomfactor = factor;
		}

		/**
		 * Instantiates a new zoom action.
		 * 
		 * @param name
		 *            the name
		 * @param icon
		 *            the icon
		 * @param factor
		 *            the factor
		 */
		public ZoomAction(String name, Icon icon, double factor) {
			super(name, icon);
			zoomfactor = factor;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent
		 * )
		 */
		public void actionPerformed(ActionEvent evt) {
			doZoom(zoomfactor);
		}
	}

	/** The zoom in action. */
	ZoomAction zoomInAction = new ZoomAction("Zoom in",
			getIcon("gfx/zoomin.gif"), 1.3);

	/** The zoom out action. */
	ZoomAction zoomOutAction = new ZoomAction("Zoom out",
			getIcon("gfx/zoomout.gif"), 0.5);

	/** The zoom tool action. */
	Action zoomToolAction = new AbstractAction("", getIcon("gfx/zoom.gif")) {
		public void actionPerformed(ActionEvent evt) {
			doZoomTool();
		}
	};

	/** The fit in window action. */
	Action fitInWindowAction = new AbstractAction("",
			getIcon("gfx/controleremoto.png")) {
		public void actionPerformed(ActionEvent evt) {
			sliderZoom.setValue(90);
		}
	};

	/**
	 * The Class ThumbAction.
	 */
	class ThumbAction extends AbstractAction implements PropertyChangeListener {

		/** The is open. */
		boolean isOpen = true;

		/**
		 * Instantiates a new thumb action.
		 */
		public ThumbAction() {
			super("Esconder miniaturas");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.
		 * PropertyChangeEvent)
		 */
		public void propertyChange(PropertyChangeEvent evt) {
			int v = ((Integer) evt.getNewValue()).intValue();
			if (v <= 1) {
				isOpen = false;
				putValue(ACTION_COMMAND_KEY, "Mostrar miniaturas");
				putValue(NAME, "Mostrar miniaturas");
			} else {
				isOpen = true;
				putValue(ACTION_COMMAND_KEY, "Esconder miniaturas");
				putValue(NAME, "Esconder miniaturas");
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent
		 * )
		 */
		public void actionPerformed(ActionEvent evt) {
			doThumbs(!isOpen);
		}
	}

	/** The thumb action. */
	ThumbAction thumbAction = new ThumbAction();

	/** The next action. */
	Action nextAction = new AbstractAction("Next", getIcon("gfx/next.gif")) {
		public void actionPerformed(ActionEvent evt) {
			doNext();
		}
	};

	/** The first action. */
	Action firstAction = new AbstractAction("First", getIcon("gfx/first.gif")) {
		public void actionPerformed(ActionEvent evt) {
			doFirst();
		}
	};

	/** The last action. */
	Action lastAction = new AbstractAction("Próximo", getIcon("gfx/last.gif")) {
		public void actionPerformed(ActionEvent evt) {
			doLast();
		}
	};

	/** The prev action. */
	Action prevAction = new AbstractAction("Anterior", getIcon("gfx/prev.gif")) {
		public void actionPerformed(ActionEvent evt) {
			doPrev();
		}
	};

	Action deslocaAcimaAction = new AbstractAction("", getIcon("gfx/acima.png")) {
		public void actionPerformed(ActionEvent evt) {
			deslocamentoY(20);
		}
	};

	Action deslocaAbaixoAction = new AbstractAction("",
			getIcon("gfx/abaixo.png")) {
		public void actionPerformed(ActionEvent evt) {
			deslocamentoY(-20);
		}
	};
	Action deslocaDireitoAction = new AbstractAction("",
			getIcon("gfx/next.gif")) {
		public void actionPerformed(ActionEvent evt) {
			deslocamentoX(20);
		}
	};
	Action deslocaEsquerdoAction = new AbstractAction("",
			getIcon("gfx/prev.gif")) {
		public void actionPerformed(ActionEvent evt) {
			deslocamentoX(-20);
		}
	};

	/**
	 * Create a new PDFViewer based on a user, with or without a thumbnail
	 * panel.
	 * 
	 * @param usarMiniaturas
	 *            true if the thumb panel should exist, false if not.
	 */
	public FormPrincipal(boolean usarMiniaturas) {
		super(TITLE);

		fazerMiniatura = usarMiniaturas;
		setEnabled(false);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				doQuit();
			}
		});
		ImageIcon icone = (ImageIcon) getIcon("gfx/icone.png");
		setIconImage(icone.getImage());
		init();
	}

	private void adicionaLivroNoPainel(DesignGridLayout design,
			final Livro livro, boolean baixado) {
		JPanel pnlTmp = new JPanel(new GridLayout(0, 2));
		pnlTmp.setBackground(Color.WHITE);
		pnlTmp.setBorder(javax.swing.BorderFactory.createTitledBorder(livro
				.getTitulo()));

		final JProgressBar progressBar = new JProgressBar(JProgressBar.VERTICAL);

		try {
			URL url = new URL(livro.getFoto().replace(" ", "%20"));

			JLabel lblLivro = new JLabel();

			try {
				Image image = ImageIO.read(url);
				lblLivro = new JLabel(new ImageIcon(image));
			} catch (IIOException ioe) {
				logger.debug("Não consegui ler: " + url);
			}

			JButton btnBaixar = new JButton("Baixar");
			btnBaixar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			Font fontBaixar = new Font("ARIAL", Font.HANGING_BASELINE, 13);
			btnBaixar.setFont(fontBaixar);
			btnBaixar.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (JOptionPane.showConfirmDialog(getInstance(),
							"Deseja realmente fazer o download do livro?",
							"Download livro", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
						RegistrarLivroBO registrarLivroBO = new RegistrarLivroBO();
						registrarLivroBO.downloadLivro(getInstance(), livro,
								progressBar);
					}
				}
			});
//			JButton btnAbrir = new JButton("Abrir");
//			btnAbrir.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
//			btnAbrir.addActionListener(new ActionListener() {
//				@Override
//				public void actionPerformed(ActionEvent e) {
//					
//					//VERIFICANDO NOVA VERSÃO DO LIVRO.
//					responseEstanteXMLLocal = new EstantesBO().pegaEstanteEmDisco();
//					if(responseEstanteXMLLocal!=null){
//						ArrayList<Livro> listaBaixados =  responseEstanteXMLLocal.baixados;
//						if(listaBaixados!=null && listaBaixados.size() > 0){
//							for(Livro lvTmp : listaBaixados){
//								if(lvTmp.getCodigolivro().equals(livro.getCodigolivro()) && 
//									lvTmp.getCodigoloja().equals(livro.getCodigoloja())){
//									if(!lvTmp.getVersao().equals(livro.getVersao())){
//										if (JOptionPane.showConfirmDialog(getInstance(),
//												"Existe uma versão diferente deste livro nos servidores da IBRACON. Deseja baixar a nova versão?", "Versão nova do livro",
//												JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
//											RegistrarLivroBO registrarLivroBO = new RegistrarLivroBO();
//											registrarLivroBO.downloadLivro(getInstance(), livro,
//													progressBar);
//											return;
//										}
//									}
//								}
//							}
//						}
//					}
//					
//					if (JOptionPane.showConfirmDialog(getInstance(),
//							"Deseja abrir o livro?", "Abrir livro",
//							JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
//						logger.debug(livro);
//						File arquivoIdr = new File(instalacaoBO
//								.getDiretorioBaixados().getAbsolutePath()
//								+ File.separator
//								+ livro.getNomeArquivoBaixado());
//						if (arquivoIdr.exists()) {
//							abrirIDR(arquivoIdr, livro.getTitulo());
//						}
//					}
//				}
//			});

			lblLivro.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

			JPanel pnlLivro = new JPanel(new BorderLayout());
			pnlLivro.setBackground(Color.WHITE);
			pnlLivro.add(lblLivro, BorderLayout.CENTER);
			if (baixado) {
				//pnlLivro.add(btnAbrir, BorderLayout.SOUTH);
			} else {
				pnlLivro.add(btnBaixar, BorderLayout.SOUTH);
			}

			JTextArea txtInformacoes = new JTextArea(10, 10);
			txtInformacoes.setWrapStyleWord(true);
			txtInformacoes.append("Título: " + livro.getTitulo() + "\n");
			txtInformacoes.append("Versão: " + livro.getVersao() + "\n");
			txtInformacoes.append("Código Loja: " + livro.getCodigoloja()
					+ "\n");

			pnlTmp.add(pnlLivro);
			pnlTmp.add(txtInformacoes);

			design.row().center().add(pnlTmp);
		} catch (MalformedURLException e2) {
			e2.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private void adicionaLivroBaixadoNoPainel(DesignGridLayout design,
			final Livro livro) {
		
		System.out.println(livro);
		JPanel pnlTmp = new JPanel(new GridLayout(0, 2));
		pnlTmp.setBackground(Color.WHITE);
		pnlTmp.setBorder(javax.swing.BorderFactory.createTitledBorder(livro
				.getTitulo()));

		final JProgressBar progressBar = new JProgressBar(JProgressBar.VERTICAL);
		
		String arquivoFotoLocal = livro.getFoto().substring(
				livro.getFoto().lastIndexOf("/"));
		JLabel lblLivro = new JLabel("");

		try {
			Image image = ImageIO.read(new File(instalacaoBO
					.getDiretorioBaixados().getAbsolutePath()
					+ File.separator
					+ arquivoFotoLocal));
			lblLivro = new JLabel(new ImageIcon(image));
		} catch (Exception e) {
			logger.debug("Não consegui ler a imagem...");
		}
		JButton btnAbrir = new JButton("Abrir");
		btnAbrir.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnAbrir.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				//VERIFICANDO NOVA VERSÃO DO LIVRO.
				if(responseEstanteXML!=null){
					ArrayList<Livro> listaBaixados =  responseEstanteXML.baixados;
					if(listaBaixados!=null && listaBaixados.size() > 0){
						for(Livro livroNovo : listaBaixados){
							if(livroNovo.getCodigolivro().equals(livro.getCodigolivro()) && 
								livroNovo.getCodigoloja().equals(livro.getCodigoloja())){
								if(!livroNovo.getVersao().equals(livro.getVersao())){
									if (JOptionPane.showConfirmDialog(getInstance(),
											"Existe uma versão diferente deste livro nos servidores da IBRACON. Deseja baixar a nova versão?", "Versão nova do livro",
											JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
										RegistrarLivroBO registrarLivroBO = new RegistrarLivroBO();
										registrarLivroBO.downloadLivro(getInstance(), livroNovo,
												progressBar, false);
										return;
									}
								}
							}
						}
					}
				}
				
				
				if (JOptionPane.showConfirmDialog(getInstance(),
						"Deseja abrir o livro?", "Abrir livro",
						JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					InstalacaoBO instalacaoBO = new InstalacaoBO();
					logger.debug(livro);
					File arquivoIdr = new File(instalacaoBO
							.getDiretorioBaixados().getAbsolutePath()
							+ File.separator + livro.getNomeArquivoBaixado());
					abrirIDR(arquivoIdr, livro.getTitulo());
				}
			}
		});

		lblLivro.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		JPanel pnlLivro = new JPanel(new BorderLayout());
		pnlLivro.setBackground(Color.white);
		pnlLivro.add(btnAbrir, BorderLayout.SOUTH);
		pnlLivro.add(lblLivro, BorderLayout.CENTER);

		JTextArea txtInformacoes = new JTextArea(10, 10);
		txtInformacoes.setWrapStyleWord(true);
		txtInformacoes.append("Título: " + livro.getTitulo() + "\n");
		txtInformacoes.append("Versão: " + livro.getVersao() + "\n");
		txtInformacoes.append("Código Loja: " + livro.getCodigoloja() + "\n");

		pnlTmp.add(pnlLivro);
		pnlTmp.add(txtInformacoes);

		design.row().center().add(pnlTmp);
	}

	public void mostraLivrosDeDireito() {
		JPanel pnlEstanteDeDireito = new JPanel();
		DesignGridLayout design = new DesignGridLayout(pnlEstanteDeDireito);
		pnlEstanteDeDireito.setBackground(Color.white);

		if (responseEstanteXML != null && responseEstanteXML.dedireito != null) {
			for (Livro livro : responseEstanteXML.dedireito) {
				adicionaLivroNoPainel(design, livro, false);
			}
		}
		split.setRightComponent(new JScrollPane(pnlEstanteDeDireito));
	}

	public void mostraLivrosBaixados() {
		JPanel pnlEstanteBaixados = new JPanel();
		DesignGridLayout design = new DesignGridLayout(pnlEstanteBaixados);

		pnlEstanteBaixados.setBackground(Color.white);

		responseEstanteXMLLocal = new EstantesBO().pegaEstanteEmDisco();
		if (responseEstanteXMLLocal != null) {
			for (Livro livro : responseEstanteXMLLocal.baixados) {
				try {
					if(!livroEstaRevogado(livro)){
						adicionaLivroBaixadoNoPainel(design, livro);
					}
				} catch (Exception exc) {
					exc.printStackTrace();
				}
			}
		}
		
		setAbaIndex(3);
		split.setRightComponent(new JScrollPane(pnlEstanteBaixados));
	}

	public boolean livroEstaRevogado(Livro livro){
		if(responseEstanteXML==null){
			return false;
		}
		
		boolean flagRevogado = true;
		
		ArrayList<Livro> livrosBaixadosOnline = responseEstanteXML.baixados;
		for(Livro lvrTmp: livrosBaixadosOnline){
			if(lvrTmp!=null &&
				lvrTmp.getCodigolivro().equals(livro.getCodigolivro())){
				flagRevogado = false;
			}
				
		}
		
		return flagRevogado;
	}
	
	/**
	 * Montar estante.
	 */
	private void montarEstante() {
		estante = new JPanel();
		estante.setBackground(Color.WHITE);
		DesignGridLayout layout = new DesignGridLayout(estante);
		JButton btnTodos = new JButton("Visão Geral");
		btnTodos.setHorizontalAlignment(SwingConstants.LEFT);
		btnTodos.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JPanel pnlEstanteTodos = new JPanel();
				DesignGridLayout design = new DesignGridLayout(pnlEstanteTodos);
				pnlEstanteTodos.setBackground(Color.white);

				if (responseEstanteXML != null
						&& responseEstanteXML.dedireito != null) {
					for (Livro livro : responseEstanteXML.dedireito) {
						adicionaLivroNoPainel(design, livro, false);
					}
				}

				if (responseEstanteXML != null
						&& responseEstanteXML.parabaixar != null) {
					for (Livro livro : responseEstanteXML.parabaixar) {
						adicionaLivroNoPainel(design, livro, false);
					}
				}
				
				responseEstanteXMLLocal = new EstantesBO().pegaEstanteEmDisco();
				if (responseEstanteXMLLocal != null) {
					for (Livro livro : responseEstanteXMLLocal.baixados) {
						try {
							if(!livroEstaRevogado(livro)){
								adicionaLivroBaixadoNoPainel(design, livro);
							}
						} catch (Exception exc) {
							exc.printStackTrace();
						}
					}
				}
				split.setRightComponent(new JScrollPane(pnlEstanteTodos));
			}
		});

		JButton btnBaixar = new JButton("Disponíveis");
		btnBaixar.setHorizontalAlignment(SwingConstants.LEFT);
		btnBaixar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JPanel pnlEstanteParaBaixar = new JPanel();
				DesignGridLayout design = new DesignGridLayout(
						pnlEstanteParaBaixar);
				pnlEstanteParaBaixar.setBackground(Color.white);

				if (responseEstanteXML != null
						&& responseEstanteXML.parabaixar != null) {
					for (Livro livro : responseEstanteXML.parabaixar) {
						adicionaLivroNoPainel(design, livro, false);
					}
				}

				split.setRightComponent(new JScrollPane(pnlEstanteParaBaixar));

			}

		});
		JButton btnMeusLivros = new JButton("Direito de uso");
		btnMeusLivros.setHorizontalAlignment(SwingConstants.LEFT);
		btnMeusLivros.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new JanelaBoasVindas(getInstance());

			}

		});

		JButton btnBaixados = new JButton("Minha biblioteca");
		btnBaixados.setHorizontalAlignment(SwingConstants.LEFT);
		btnBaixados.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JPanel pnlEstanteBaixados = new JPanel();
				DesignGridLayout design = new DesignGridLayout(
						pnlEstanteBaixados);

				pnlEstanteBaixados.setBackground(Color.white);

				responseEstanteXMLLocal = new EstantesBO().pegaEstanteEmDisco();
				if (responseEstanteXMLLocal != null) {
					for (Livro livro : responseEstanteXMLLocal.baixados) {
						try {
							if(!livroEstaRevogado(livro)){
								adicionaLivroBaixadoNoPainel(design, livro);
							}
						} catch (Exception exc) {
							exc.printStackTrace();
						}
					}
				}
				
				split.setRightComponent(new JScrollPane(pnlEstanteBaixados));
			}
		});

		layout.row().grid().add(btnTodos);
		layout.row().grid().add(btnBaixar);
		layout.row().grid().add(btnMeusLivros);
		layout.row().grid().add(btnBaixados);

	}

	MouseListener mouseListener = new MouseListener() {
		@Override
		public void mouseReleased(MouseEvent e) {
			recarregaPagina();
		}

		@Override
		public void mousePressed(MouseEvent e) {
			recarregaPagina();
		}

		@Override
		public void mouseExited(MouseEvent e) {
			recarregaPagina();
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			recarregaPagina();
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			recarregaPagina();

		}
	};

	/**
	 * Montar abas.
	 */
	private void montarAbas() {
		logger.info("Swing: Montando as abas");
		abas = new JTabbedPane();

		abas.addTab("Índice", scrollIndice);
		abas.addTab("Miniaturas", thumbscroll);
		abas.addTab("Notas", new JScrollPane(notas));
		abas.addTab("Estantes", estante);
		if(resultadoJList!=null && listaResultadoPesquisa != null){
			abas.addTab("Resultado da pesquisa ("+listaResultadoPesquisa.size()+") ocorrências", new JScrollPane(resultadoJList));
		}

		abas.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				split.setRightComponent(painelCentral);
			}
		});
		abas.addVetoableChangeListener(new VetoableChangeListener() {

			@Override
			public void vetoableChange(PropertyChangeEvent evt)
					throws PropertyVetoException {
				recarregaPagina();

			}
		});

		if (scrollIndice != null) {
			scrollIndice.addMouseListener(mouseListener);
		}
		if (scrollIndice != null) {
			thumbscroll.addMouseListener(mouseListener);
		}

		if (scrollIndice != null) {
			notas.addMouseListener(mouseListener);
		}

		split.setLeftComponent(abas);

	}

	/**
	 * Montar tool bars.
	 * 
	 * @param painelCentral
	 *            the painel central
	 */
	public void montarToolBars() {
		logger.info("Montando toolbars");
		JToolBar toolBarSlider = new JToolBar();

		sliderNavegacao = new JSlider(JSlider.HORIZONTAL, 1, 1, 1);
		sliderNavegacao.addChangeListener(new ChangeListener() {
			public void stateChanged(final ChangeEvent e) {
				JSlider sl = (JSlider) e.getSource();
				gotoPage(sl.getValue());
			}
		});

		JButton jb;
		//
		// jb = new JButton(firstAction);
		// jb.setText("");
		// toolbar.add(jb);
		jb = new JButton(prevAction);
		jb.setText("");
		toolBarSlider.add(jb);
		toolBarSlider.setFloatable(false);

		toolBarSlider.add(sliderNavegacao);

		// toolBarSlider.add(getBotaoDebug());

		jb = new JButton(nextAction);
		jb.setText("");
		toolBarSlider.add(jb);
		// jb = new JButton(lastAction);
		// jb.setText("");
		// toolbar.add(jb);
		// toolbar.add(Box.createHorizontalGlue());

		// fullScreenButton = new JToggleButton(fullScreenAction);
		// fullScreenButton.setText("");
		// toolbar.add(fullScreenButton);
		// fullScreenButton.setEnabled(true);

		// toolbar.add(Box.createHorizontalGlue());
		//
		// JToggleButton jtb;
		// ButtonGroup bg = new ButtonGroup();
		//
		// jtb = new JToggleButton(zoomToolAction);
		// jtb.setText("");
		// bg.add(jtb);
		// toolbar.add(jtb);
		// jtb = new JToggleButton(fitInWindowAction);
		// jtb.setText("");
		// bg.add(jtb);
		// jtb.setSelected(true);
		// toolbar.add(jtb);
		//
		// toolbar.add(Box.createHorizontalGlue());

		// jb = new JButton(printAction);
		// jb.setText("");
		// toolbar.add(jb);

		JPanel pnl = new JPanel();
		DesignGridLayout dgl = new DesignGridLayout(pnl);

		pageField = new JTextField("", 6);
		pageField.setMaximumSize(new Dimension(45, 32));
		pageField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				doPageTyped();
				int pag = sliderNavegacao.getValue();
				
				try {
					pag = Integer.parseInt(pageField.getText());
				} catch (NumberFormatException e) {
				}
				
				sliderNavegacao.setValue(pag);
			}
		});

		numPagesLabel = new JLabel();

		JPanel pnlIrPara = new JPanel();
		DesignGridLayout dsIrPara = new DesignGridLayout(pnlIrPara);
		dsIrPara.row().center().add(pageField).add(numPagesLabel);

		JToolBar toolbarTop = new JToolBar();
		toolbarTop.setLayout(new BorderLayout());
		toolbarTop.setFloatable(false);
		JPanel pnlCenterToolbarTop = new JPanel();

		qtdeZoomField = new JTextField("", 6);
		qtdeZoomField.setMaximumSize(new Dimension(45, 32));
		qtdeZoomField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				String strVl = ((JTextField) (evt.getSource())).getText();
				try {
					int valor = new Integer(strVl);
					sliderZoom.setValue(valor);
				} catch (NumberFormatException nfe) {
					nfe.printStackTrace();
					sliderZoom.setValue(100);
				}
			}
		});

		qtdeZoomLabel = new JLabel("Zoom");
		qtdeZoomPercentualLabel = new JLabel("%");
		sliderZoom = new JSlider(JSlider.HORIZONTAL);
		sliderZoom.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider sl = (JSlider) e.getSource();
				qtdeZoomField.setText(String.valueOf(sl.getValue()));
				mudaZoom(new Double(sl.getValue()));

				if (barraRolagemVertical != null) {
					barraRolagemVertical.setMinimum(0);
					barraRolagemVertical.setUnitIncrement(30);
					barraRolagemVertical.setMaximum(page.getHeight()
							- (int) page.getHeight() / 3);
					barraRolagemVertical.setValue(0 - (int) page.getLocation()
							.getY());
				}
				// if (barraRolagemHorizontal != null) {
				// barraRolagemHorizontal.setMinimum(0);
				// barraRolagemHorizontal.setMaximum(pnlPage.getWidth() * 2);
				// barraRolagemHorizontal
				// .setValue((int) barraRolagemHorizontal.getMaximum() / 2);
				// }
			}
		});
		sliderZoom.setMinimum(50);
		sliderZoom.setMaximum(200);

		notaBtn = new JButton(getIcon("gfx/marcador.png"));
		notaBtn.setSize(40, 20);
		notaBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new JanelaNota(getInstance(), serialPDF, Integer
						.valueOf(pageField.getText()));

			}
		});

		pnlCenterToolbarTop.add(qtdeZoomLabel);
		pnlCenterToolbarTop.add(qtdeZoomField);
		pnlCenterToolbarTop.add(qtdeZoomPercentualLabel);
		pnlCenterToolbarTop.add(sliderZoom);
		pnlCenterToolbarTop.add(notaBtn);

		JPanel pnlWestToolBarTop = new JPanel();

		pesquisaField = new JTextField("", 20);
		pesquisaField.setMaximumSize(new Dimension(45, 32));
		pesquisaField.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent evt) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						String textoPesquisa = ((JTextField) evt.getSource()).getText();
							Buscador b = new Buscador();
							String pathIndex = InstalacaoBO.getPathInstalacao() + File.separator + serialPDF + File.separator + "indexacao";
							listaResultadoPesquisa = b.buscaComParser(pathIndex, textoPesquisa);
							
							DefaultListModel<ItemResultado> dataModel = new DefaultListModel<>();
							resultadoJList = new JList<>(dataModel);
								
							for(ItemResultado item: listaResultadoPesquisa){
								dataModel.addElement(item);
							}
							

						    MouseListener mouseListener = new MouseAdapter() {
						      public void mouseClicked(MouseEvent mouseEvent) {
						          int index = resultadoJList.locationToIndex(mouseEvent.getPoint());
						          if (index >= 0) {
						            ItemResultado item = resultadoJList.getModel().getElementAt(index);
						            sliderNavegacao.setValue(item.getPagina()-1);
						          }
						      }
						    };
						    
						    resultadoJList.addMouseListener(mouseListener);
							
							montarAbas();
							setAbaIndex(4);
					}
				}).start();
			}
		});

		pesquisaLabel = new JLabel("Pesquisar:");

		pnlWestToolBarTop.add(pesquisaLabel);
		pnlWestToolBarTop.add(pesquisaField);

		toolbarTop.add(pnlWestToolBarTop, BorderLayout.WEST);
		toolbarTop.add(pnlCenterToolbarTop, BorderLayout.CENTER);
		dgl.row().center().add(pnlIrPara);
		dgl.row().grid().add(toolBarSlider);

		JPanel sul = new JPanel(new BorderLayout());
		// sul.add(criaBarraRolagemHorizontal(), BorderLayout.NORTH);
		sul.add(pnl, BorderLayout.SOUTH);
		// sul.add(getControlePaginas(), BorderLayout.EAST);

		// toolbarTop.setBackground(Color.WHITE);
		// pnl.setBackground(Color.WHITE);

		painelCentral.add(toolbarTop, BorderLayout.NORTH);
		painelCentral.add(sul, BorderLayout.SOUTH);
		painelCentral.add(criaBarraRolagemVertical(), BorderLayout.EAST);
	}

	private JButton getBotaoDebug() {
		JButton btn = new JButton("Debug");
		btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				JOptionPane.showMessageDialog(null, page.getSize());

			}
		});

		return btn;
	}

	public JScrollBar criaBarraRolagemVertical() {
		logger.info("Criando barra de rolagem vertical para o pdf");
		
		barraRolagemVertical = new JScrollBar();
		barraRolagemVertical.setMinimum(0);
		barraRolagemVertical.setUnitIncrement(30);
		barraRolagemVertical.setMaximum(2000);
		barraRolagemVertical.setOrientation(JProgressBar.VERTICAL);
		barraRolagemVertical.addAdjustmentListener(new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				// pesquisaField.setText("" + barraRolagemVertical.getValue());
				page.setLocation(page.getX(),
						0 - barraRolagemVertical.getValue());
				if (barraRolagemVertical.getValue() == barraRolagemVertical
						.getMaximum() - 10) {
					barraRolagemVertical.setValue(1);
					sliderNavegacao.setValue(sliderNavegacao.getValue() + 1);
					recarregaPagina();
				} else if (barraRolagemVertical.getValue() == 0) {
					if (sliderNavegacao.getValue() > 0) {
						barraRolagemVertical.setValue(barraRolagemVertical
								.getMaximum() - 15);
						sliderNavegacao.setValue(sliderNavegacao.getValue() - 1);
						recarregaPagina();
					}
				}
			}
		});
		return barraRolagemVertical;
	}

	public JScrollBar criaBarraRolagemHorizontal() {
		logger.info("Criando barra de rolagem horizontal");
		barraRolagemHorizontal = new JScrollBar();
		barraRolagemHorizontal.setUnitIncrement(10);
		barraRolagemHorizontal.setMinimum(0);
		barraRolagemHorizontal.setMaximum(1000);
		barraRolagemHorizontal.setOrientation(JProgressBar.HORIZONTAL);
		barraRolagemHorizontal.addAdjustmentListener(new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				page.setLocation(
						0 - (barraRolagemHorizontal.getValue() - (int) pnlPage
								.getWidth() / 2), page.getY());
			}
		});
		return barraRolagemHorizontal;
	}

	/**
	 * Initialize this PDFViewer by creating the GUI.
	 */
	protected void init() {
		logger.info("Swing: inicializando componentes visuais");
		
		page = new PagePanel();
		page.useZoomTool(false);
		page.setDoubleBuffered(true);
		page.setDebugGraphicsOptions(DebugGraphics.NONE_OPTION);

		this.setResizable(true);
		this.setExtendedState(MAXIMIZED_BOTH);
		painelCentral = new JPanel(new BorderLayout());

		montarEstante();

		if (fazerMiniatura) {
			split = new JSplitPane(split.HORIZONTAL_SPLIT);
			split.setOpaque(true);
			split.setResizeWeight(0.5);
			split.setOneTouchExpandable(true);
			thumbscroll = new JScrollPane(thumbs,
					thumbscroll.VERTICAL_SCROLLBAR_ALWAYS,
					thumbscroll.HORIZONTAL_SCROLLBAR_NEVER);

			montarPagina();

			getContentPane().add(split, BorderLayout.CENTER);
		} else {
			getContentPane().add(painelCentral, BorderLayout.CENTER);
		}

		montarToolBars();

		logger.info("Swing: Configurando Menubar");
		JMenuBar mb = new JMenuBar();
		JMenu file = new JMenu("Arquivo");
//		file.add(openAction);
//		file.addSeparator();
		file.add(closeAction);
		file.addSeparator();
//		file.add(pageSetupAction);
		file.add(printAction);
		file.addSeparator();
		file.add(quitAction);
		mb.add(file);

		JMenu configurar = new JMenu("Configurar");
		configurar.add(proxyAction);
		mb.add(configurar);

		JMenu ajuda = new JMenu("Ajuda");
		ajuda.add(sobreAction);

		mb.add(ajuda);

		setJMenuBar(mb);
		setEnabling();
		// pack();
		
		logger.info("Centralizando janela Toolkit.getDefaultToolkit().getScreenSize()");
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (screen.width - getWidth()) / 2;
		int y = (screen.height - getHeight()) / 2;
		setLocation(x, y);
		if (SwingUtilities.isEventDispatchThread()) {
			setVisible(true);
		} else {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						setVisible(true);
					}
				});
			} catch (InvocationTargetException ie) {
			} catch (InterruptedException ie) {
			}
		}
		pnlPage.addMouseListener(mouseListener);
	}

	public JPanel getControlePaginas() {
		logger.info("Criando o componente de controle de páginas");
		
		JPanel jpanel = new JPanel();
		jpanel.setBackground(Color.WHITE);

		DesignGridLayout ds = new DesignGridLayout(jpanel);

		JButton btnAcima = new JButton(deslocaAcimaAction);
		JButton btnAbaixo = new JButton(deslocaAbaixoAction);
		JButton btnDireito = new JButton(deslocaDireitoAction);
		JButton btnEsquerdo = new JButton(deslocaEsquerdoAction);

		JButton fitInWindow = new JButton(fitInWindowAction);

		ds.row().center().add(btnAcima);
		ds.row().left().add(btnEsquerdo).add(fitInWindow).add(btnDireito);
		ds.row().center().add(btnAbaixo);

		return jpanel;
	}

	/**
	 * Montar pagina.
	 * 
	 * @param painelCentral
	 *            the painel central
	 */
	private void montarPagina() {
		logger.info("montarPagina()");
		
		split.setOpaque(true);
		split.setResizeWeight(0.4);

		pnlPagina = new JPanel(new BorderLayout());

		pnlPagina.addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent e) {
				int acrescentaRolagem = 0;
				if (e.getWheelRotation() < 0) {
					acrescentaRolagem = -20;
				} else if (e.getWheelRotation() > 0) {
					acrescentaRolagem = 20;
				}
				barraRolagemVertical.setValue(barraRolagemVertical.getValue()
						+ e.getWheelRotation() + acrescentaRolagem);

			}
		});

		pnlPagina.setBackground(Color.WHITE);
		pnlPagina.add(page, BorderLayout.CENTER);

		pnlPage = new JPanel();
		pnlPage.setBackground(Color.gray);
		pnlPage.add(page);
		pnlPagina.add(pnlPage, BorderLayout.CENTER);

		page.setBackground(Color.gray);
		painelCentral.setBackground(Color.gray);
		painelCentral.add(pnlPagina, BorderLayout.CENTER);
		split.setRightComponent(painelCentral);
	}

	/**
	 * Changes the displayed page, desyncing if we're not on the same page as a
	 * presenter.
	 * 
	 * @param pagenum
	 *            the page to display
	 */
	public void gotoPage(int pagenum) {
		logger.info("Ir para página: " + pagenum);
		if (pagenum < 0) {
			pagenum = 0;
		} else if (curFile !=null && pagenum >= curFile.getNumPages()) {
			pagenum = curFile.getNumPages() - 1;
		}
		
		forceGotoPage(pagenum);

		recarregaPagina();
	}

	/**
	 * Changes the displayed page.
	 * 
	 * @param pagenum
	 *            the page to display
	 */
	public void forceGotoPage(int pagenum) {
		logger.info("Forçando ir para página: " + pagenum);
		try {
			if (pagenum <= 0) {
				pagenum = 0;
			} else if (pagenum >= curFile.getNumPages()) {
				pagenum = curFile.getNumPages() - 1;
			}
			// logger.debug("Going to page " + pagenum);
			curpage = pagenum;

			// update the page text field
			pageField.setText(String.valueOf(curpage + 1));
			numPagesLabel.setText("/" + curFile.getNumPages());

			// fetch the page and show it in the appropriate place
			PDFPage pg = curFile.getPage(pagenum + 1);
			// if (fspp != null) {
			// fspp.showPage(pg);
			// fspp.requestFocus();
			// } else {
			try {
				page.showPage(pg);
			} catch (IllegalArgumentException iae) {
				try {
					page.showPage(pg);
				} catch (IllegalArgumentException d) {
					return;
				}
			}
			// page.requestFocus();
			// }

			// update the thumb panel
			if (fazerMiniatura) {
				thumbs.pageShown(pagenum);
			}

			setEnabling();

			recarregaPagina();

		} catch (NullPointerException e) {
			logger.debug("Ops, deu nullpointer, mas agora te peguei!");
			forceGotoPage(pagenum);
		}
	}

	public void centralizarPageNoPainelCentral() {
		logger.info("Centralizando o pdf no painel central");
		Dimension screen = pnlPage.getSize();
		int x = (screen.width - page.getWidth()) / 2;
		int y = (screen.height - page.getHeight()) / 2;
		page.setLocation(x, y);
	}

	/**
	 * Enable or disable all of the actions based on the current state.
	 */
	public void setEnabling() {
		boolean fileavailable = curFile != null;
		boolean pageshown = ((fspp != null) ? fspp.getPage() != null : page
				.getPage() != null);
		boolean printable = fileavailable && curFile.isPrintable();

		pesquisaLabel.setEnabled(fileavailable);
		pesquisaField.setEnabled(fileavailable);

		notaBtn.setEnabled(fileavailable);

		qtdeZoomField.setEnabled(fileavailable);
		qtdeZoomLabel.setEnabled(fileavailable);

		qtdeZoomPercentualLabel.setEnabled(fileavailable);

		pageField.setEnabled(fileavailable);
		numPagesLabel.setEnabled(fileavailable);
		sliderNavegacao.setEnabled(pageshown);
		printAction.setEnabled(printable);
		closeAction.setEnabled(fileavailable);
		prevAction.setEnabled(pageshown);
		nextAction.setEnabled(pageshown);
		firstAction.setEnabled(fileavailable);
		lastAction.setEnabled(fileavailable);
		zoomToolAction.setEnabled(pageshown);
		fitInWindowAction.setEnabled(pageshown);
		zoomInAction.setEnabled(pageshown);
		zoomOutAction.setEnabled(pageshown);
	}

	/**
	 * open a URL to a PDF file. The file is read in and processed with an
	 * in-memory buffer.
	 * 
	 * @param url
	 *            the url
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void openFile(URL url) throws IOException {
		logger.info("Abrir arquivo : " + url);
		URLConnection urlConnection = url.openConnection();
		int contentLength = urlConnection.getContentLength();
		InputStream istr = urlConnection.getInputStream();
		byte[] byteBuf = new byte[contentLength];
		int offset = 0;
		int read = 0;
		while (read >= 0) {
			read = istr.read(byteBuf, offset, contentLength - offset);
			if (read > 0) {
				offset += read;
			}
		}
		if (offset != contentLength) {
			throw new IOException("Could not read all of URL file.");
		}
		ByteBuffer buf = ByteBuffer.allocate(contentLength);
		buf.put(byteBuf);
		openPDFByteBuffer(buf, url.toString(), url.getFile());

	}

	/**
	 * <p>
	 * Open a specific pdf file. Creates a DocumentInfo from the file, and opens
	 * that.
	 * </p>
	 * 
	 * <p>
	 * <b>Note:</b> Mapping the file locks the file until the PDFFile is closed.
	 * </p>
	 * 
	 * @param file
	 *            the file to open
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void openFile(File file) throws IOException {
		logger.info("Abrindo arquivo : " + file);
		
		// first open the file for random access
		RandomAccessFile raf = new RandomAccessFile(file, "r");

		jp.aumentaPercentual(10);

		// extract a file channel
		FileChannel channel = raf.getChannel();

		// now memory-map a byte-buffer
		ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0,
				channel.size());

		raf.close();

		jp.aumentaPercentual(10);

		String path = file.getPath();

		file.delete();

		openPDFByteBuffer(buf, path, file.getName());
	}

	public void openFile(InputStream in, String nome) throws IOException {
		logger.info("Abrindo arquivo : " + nome);
		ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();

		int bytesread = 0;
		byte[] tbuff = new byte[1024];
		while ((bytesread = in.read(tbuff)) != -1) {
			byteArrayStream.write(tbuff, 0, bytesread);
		}

		byte[] byteBuff = byteArrayStream.toByteArray();

		ByteBuffer buf = ByteBuffer.allocate(byteBuff.length);
		buf.put(byteBuff);
		openPDFByteBuffer(buf, "", nome);
	}

	public void openFile(byte[] byteBuff, String nome) throws IOException {
		logger.info("Abrindo arquivo : " + nome);
		ByteBuffer buf = ByteBuffer.allocate(byteBuff.length);
		buf.put(byteBuff);
		openPDFByteBuffer(buf, "", nome);
	}

	/**
	 * <p>
	 * Open a specific pdf file. Creates a DocumentInfo from the file, and opens
	 * that.
	 * </p>
	 * 
	 * <p>
	 * <b>Note:</b> By not memory mapping the file its contents are not locked
	 * down while PDFFile is open.
	 * </p>
	 * 
	 * @param file
	 *            the file to open
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void openFileUnMapped(File file) throws IOException {
		logger.info("Abrindo arquivo : " + file);
		DataInputStream istr = null;
		try {
			// load a pdf from a byte buffer
			// avoid using a RandomAccessFile but fill a ByteBuffer directly
			istr = new DataInputStream(new FileInputStream(file));
			long len = file.length();
			if (len > Integer.MAX_VALUE) {
				throw new IOException("File too long to decode: "
						+ file.getName());
			}
			int contentLength = (int) len;
			byte[] byteBuf = new byte[contentLength];
			int offset = 0;
			int read = 0;
			while (read >= 0) {
				read = istr.read(byteBuf, offset, contentLength - offset);
				if (read > 0) {
					offset += read;
				}
			}
			ByteBuffer buf = ByteBuffer.allocate(contentLength);
			buf.put(byteBuf);
			openPDFByteBuffer(buf, file.getPath(), file.getName());
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			if (istr != null) {
				try {
					istr.close();
				} catch (Exception e) {
					// ignore error on close
				}
			}
		}
	}

	/**
	 * Open pdf byte buffer.
	 * 
	 * @param buf
	 *            the buf
	 * @param path
	 *            the path
	 * @param name
	 *            the name
	 */
	private void openPDFByteBuffer(ByteBuffer buf, String path, String name) {
		logger.info("openPDFByteBuffer : " + path);
		PDFFile newfile = null;
		try {
			newfile = new PDFFile(buf);

		} catch (IOException ioe) {
			erroAoAbrir(path + " doesn't appear to be a PDF file." + "\n: "
					+ ioe.getMessage());
			return;
		}
		jp.aumentaPercentual(5);

		doClose();

		this.curFile = newfile;
		docName = name;
		setTitle(TITLE + ": " + docName);

		if (fazerMiniatura) {
			thumbs = new ThumbPanel(curFile);
			thumbs.addPageChangeListener(this);
			thumbscroll.getViewport().setView(thumbs);
			thumbscroll.getViewport().setBackground(Color.gray);
		}

		jp.aumentaPercentual(10);

		setEnabling();

		// display page 1.
		// forceGotoPage(-1);

		// if the PDF has an outline, display it.
		try {
			indice = curFile.getOutline();
		} catch (IOException ioe) {
		}
		if (indice != null) {
			if (indice.getChildCount() > 0) {
				// olf = new JDialog(this, "Índice");
				// olf.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
				// olf.setLocation(this.getLocation());
				JTree jt = new JTree(indice);
				jt.setRootVisible(false);
				jt.addTreeSelectionListener(this);

				scrollIndice = new JScrollPane(jt);
				scrollIndice.setSize(100, 400);

			} else {
				if (olf != null) {
					olf.setVisible(false);
					olf = null;
				}
			}
		}
	}

	/**
	 * Erro ao abrir.
	 * 
	 * @param message
	 *            the message
	 */
	public void erroAoAbrir(String message) {
		JOptionPane.showMessageDialog(split, message,
				"Erro ao abrir o arquivo.", JOptionPane.ERROR_MESSAGE);
	}

	/** The pdf filter. */
	FileFilter pdfIdrFilter = new FileFilter() {
		public boolean accept(File f) {
			return f.isDirectory() || f.getName().endsWith(".pdf")
					|| f.getName().endsWith(".idr");
		}

		public String getDescription() {
			return "Escolha um arquivo PDF";
		}
	};

	/** The prev dir choice. */
	private File prevDirChoice;

	/**
	 * Abrir.
	 */
	public void abrir() {

		logger.info("Abrindo livro");
		try {
			new Thread(new Runnable() {
				public void run() {
					JFileChooser fc = new JFileChooser();
					fc.setCurrentDirectory(prevDirChoice);
					fc.setFileFilter(pdfIdrFilter);
					fc.setMultiSelectionEnabled(false);
					int returnVal = fc.showOpenDialog(null);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						selectedFile = fc.getSelectedFile();
						if (selectedFile != null) {
							try {
								jp = new JanelaProgresso(getInstance());
								jp.aparecer();
								jp.aumentaPercentual(10);
								jp.setTexto("Abrindo o livro");
								scrollIndice = null;

								File arquivoPDF = null;
								LivroIDR livroIDR = null;

								jp.aumentaPercentual(5);
								// VERIFICA SE É UM ARQUIVO IDR
								if (selectedFile.getName().endsWith(".idr")) {
									try {
										livroIDR = new LivroIdrBO()
												.getLivroIDRArrayBytes(selectedFile);
										
									} catch (InvalidKeyException e) {
										e.printStackTrace();
									} catch (NoSuchAlgorithmException e) {
										e.printStackTrace();
									} catch (NoSuchPaddingException e) {
										e.printStackTrace();
									} catch (InvalidAlgorithmParameterException e) {
										e.printStackTrace();
									}
									arquivoPDF = livroIDR.getPdfFile();
								} else {
									arquivoPDF = selectedFile;
								}

								
								jp.aumentaPercentual(5);
								serialPDF = selectedFile.getName();

								prevDirChoice = selectedFile;
								recarregaPagina();
								
								openFile(livroIDR.getPdfByteArray(), serialPDF);
								
								if (selectedFile.getName().endsWith(".idr")
										&& livroIDR != null) {
									try {
										DefaultMutableTreeNode dmtIndice = new IndiceBO(
												getInstance())
												.montarArvoreIndice(livroIDR
														.getIndiceByteArray());
										JTree arvoreIndice = new JTree(
												dmtIndice);
										arvoreIndice
												.getSelectionModel()
												.setSelectionMode(
														TreeSelectionModel.SINGLE_TREE_SELECTION);
										arvoreIndice
												.addTreeSelectionListener(new TreeSelectionListener() {
													public void valueChanged(
															TreeSelectionEvent arg0) {
														try {
															Item item = (Item) ((DefaultMutableTreeNode) (arg0
																	.getPath()
																	.getLastPathComponent()))
																	.getUserObject();
															page.setBounds(
																	page.getX(),
																	-10,
																	page.getWidth(),
																	page.getHeight());
															if (sliderZoom
																	.getValue() < 100) {
																sliderZoom
																		.setValue(100);
															}

															sliderNavegacao
																	.setValue(new Integer(
																			item.getPaginareal()) - 1);
														} catch (Exception e) {
															e.printStackTrace();
														}
													}
												});
										scrollIndice = new JScrollPane(
												arvoreIndice);
									} catch (Exception exc) {
										exc.printStackTrace();
									}
								}
								jp.aumentaPercentual(20);
								montaLivro();

								//Copiando e extraindo o zip de indexação
								
								new LivroIdrBO().copiaExtraiZipIndexacao(selectedFile.getName(), livroIDR);

								// EXCLUI A PASTA TEMP
								InstalacaoBO.excluirPastaTemp();

								sliderZoom.setValue(100);
								jp.aumentaPercentual(20);
								jp.encerrar();

								recarregaPagina();

							} catch (IOException ioe) {
								ioe.printStackTrace();
							}
						}
					}
				}
			}).start();
			
			logger.info("Thread de abertura de livro inciada");
			
			recarregaPagina();
		} catch (Exception e) {
			JOptionPane
					.showMessageDialog(split,
							"Erro ao abrir o arquivo. Por favor, faça contato com o suporte técnico.");
			logger.error(e);
			e.printStackTrace();
		}
	}

	public void montaLivro() {
		logger.info("Montando o livro na tela");
		montarAbas();
		carregarNotas();

		sliderNavegacao.setMinimum(0);
		sliderNavegacao.setMaximum(curFile.getNumPages());
		sliderNavegacao.setValue(0);
	}

	public void carregarNotas() {
		logger.info("Carregando as notas do livro");
		notas = new JPanel();
		DesignGridLayout ds = new DesignGridLayout(notas);
		// DesignGridLayout layoutNotas = new DesignGridLayout(notas);
		NotaBO notaBO = new NotaBO();
		ArrayList<Nota> listaNotas = notaBO.listaNotasGravadas(serialPDF);

		for (final Nota nota : listaNotas) {
			JButton btnNota = new JButton("Pág. " + nota.getPagina() + " - "
					+ nota.getTitulo());
			btnNota.setHorizontalAlignment(SwingConstants.LEFT);
			btnNota.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					gotoPage(nota.getPagina() - 1);
					new JanelaNota(getInstance(), serialPDF, nota.getPagina());
				}
			});
			ds.row().grid().add(btnNota);
		}
		montarAbas();
	}

	public void setAbaIndex(int index) {
		abas.setSelectedIndex(index);
	}

	/**
	 * Open a local file, given a string filename.
	 * 
	 * @param name
	 *            the name of the file to open
	 */
	public void abrir(String resource) {
		logger.info("Abrindo " + resource);
		try {
			jp = new JanelaProgresso(this);
			jp.aparecer();
			InstalacaoBO instalacaoBo = new InstalacaoBO();
			File ibraconPDFFile = new File(instalacaoBo.getPathInstalacao()
					+ File.separator + "ibracon.pdf");
			jp.aumentaPercentual(10);
			openFile(ibraconPDFFile);
			jp.aumentaPercentual(10);
			scrollIndice = null;
			serialPDF = "ABERTURA";
			montaLivro();
			jp.aumentaPercentual(100);
			jp.encerrar();
			sliderZoom.setValue(60);
			recarregaPagina();

		} catch (IOException ioe) {
			try {
				openFile(new File(resource));
			} catch (IOException ex) {
				Logger.getLogger(FormPrincipal.class.getName()).log(
						Level.FATAL, null, ex);
			}
		}
	}

	/**
	 * Método utilizado pela estante de livros
	 */
	public void abrirIDR(final File arquivoPDF, final String titulo) {
		new Thread(new Runnable() {
			public void run() {
				try {
					jp.aumentaPercentual(5);
					if (arquivoPDF.getName().endsWith(".idr")) {
						try {
							livroIDR = new LivroIdrBO()
									.getLivroIDRArrayBytes(arquivoPDF);
						} catch (InvalidKeyException e) {
							e.printStackTrace();
						} catch (NoSuchAlgorithmException e) {
							e.printStackTrace();
						} catch (NoSuchPaddingException e) {
							e.printStackTrace();
						} catch (InvalidAlgorithmParameterException e) {
							e.printStackTrace();
						}
						// arquivoPDF = livroIDR.getPdfFile();
					} else {
						return;
					}
					jp = new JanelaProgresso(getInstance());
					jp.aparecer();
					jp.aumentaPercentual(10);
					jp.setTexto("Abrindo o livro");
					scrollIndice = null;

					jp.aumentaPercentual(5);
					serialPDF = titulo;

					recarregaPagina();
					// openFile(livroIDR.getPdfFile());
					
					openFile(livroIDR.getPdfByteArray(), titulo);

					try {
						DefaultMutableTreeNode dmtIndice = new IndiceBO(
								getInstance()).montarArvoreIndice(livroIDR
								.getIndiceByteArray());
						JTree arvoreIndice = new JTree(dmtIndice);
						arvoreIndice.getSelectionModel().setSelectionMode(
								TreeSelectionModel.SINGLE_TREE_SELECTION);
						arvoreIndice
								.addTreeSelectionListener(new TreeSelectionListener() {
									public void valueChanged(
											TreeSelectionEvent arg0) {
										try {
											Item item = (Item) ((DefaultMutableTreeNode) (arg0
													.getPath()
													.getLastPathComponent()))
													.getUserObject();
											page.setBounds(page.getX(), -10,
													page.getWidth(),
													page.getHeight());
											if (sliderZoom.getValue() < 100) {
												sliderZoom.setValue(100);
											}

											sliderNavegacao.setValue(new Integer(
													item.getPaginareal()) - 1);
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								});
						scrollIndice = new JScrollPane(arvoreIndice);
					} catch (Exception exc) {
						exc.printStackTrace();
						jp.encerrar();
					}
					jp.aumentaPercentual(20);
					montaLivro();

					//Copiando e extraindo o zip de indexação
					new LivroIdrBO().copiaExtraiZipIndexacao(serialPDF, livroIDR);
					
					// EXCLUI A PASTA TEMP
					InstalacaoBO.excluirPastaTemp();

					sliderZoom.setValue(100);
					jp.aumentaPercentual(20);
					jp.encerrar();

					setTitle(TITLE + ": " + serialPDF);

					recarregaPagina();

				} catch (IOException ioe) {
					jp.encerrar();
					ioe.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * Posts the Page Setup dialog.
	 */
	public void doPageSetup() {
		PrinterJob pjob = PrinterJob.getPrinterJob();
		pformat = pjob.pageDialog(pformat);
	}

	/**
	 * A thread for printing in.
	 */
	class PrintThread extends Thread {

		/** The pt pages. */
		PDFPrintPage ptPages;

		/** The pt pjob. */
		PrinterJob ptPjob;

		/**
		 * Instantiates a new prints the thread.
		 * 
		 * @param pages
		 *            the pages
		 * @param pjob
		 *            the pjob
		 */
		public PrintThread(PDFPrintPage pages, PrinterJob pjob) {
			ptPages = pages;
			ptPjob = pjob;
			setName(getClass().getName());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Thread#run()
		 */
		public void run() {
			try {
				ptPages.show(ptPjob);
				ptPjob.print();
			} catch (PrinterException pe) {
				JOptionPane.showMessageDialog(FormPrincipal.this,
						"Printing Error: " + pe.getMessage(), "Print Aborted",
						JOptionPane.ERROR_MESSAGE);
			}
			ptPages.hide();
		}
	}

	/**
	 * Print the current document.
	 */
	public void doPrintX() {

		try{
		PDFPrintPage pages = new PDFPrintPage(curFile);

		PrinterJob pjob = PrinterJob.getPrinterJob();
		pformat = PrinterJob.getPrinterJob().pageDialog(pformat);
//		pformat.setOrientation(pformat.PORTRAIT);

		PDFPage pdfPage = curFile.getPage(curpage);
		curFile.getPage(pdfPage.getPageNumber());

		if (pjob.printDialog()) {
			pformat = pjob.validatePage(pformat);
			Book book = new Book();

			book.append(pages, pformat, pdfPage.getPageNumber());
			pjob.setPageable(book);
		}

			pjob.print();
		} catch (PrinterException exc) {
			logger.debug(exc);
		} catch (NullPointerException ne){
			logger.debug(ne);
		}
	}
	
	/**
	 * @author yesus
	 * @since FEV/2014
	 */
	public void imprimirPaginaAtual(){
		
		if(JOptionPane.showConfirmDialog(this, 
											"Deseja realmente imprimir esta página? ", 
											"Imprimir página (".concat(String.valueOf(sliderNavegacao.getValue()+1)).concat(")"),
											JOptionPane.YES_NO_OPTION) ==
									JOptionPane.OK_OPTION){	
				PDFPrintPage pages = new PDFPrintPage(curFile);
				PrinterJob job = PrinterJob.getPrinterJob();
				job.setPrintable(pages);
				
				PrintRequestAttributeSet atributosImpressao = new HashPrintRequestAttributeSet();
				atributosImpressao.add(new Copies(1));
				//atributosImpressao.add(new CopiesSupported(1));
				atributosImpressao.add(MultipleDocumentHandling.SINGLE_DOCUMENT);
				atributosImpressao.add(new PageRanges(sliderNavegacao.getValue()+1, sliderNavegacao.getValue()+1));
				
				try {
					job.print(atributosImpressao);
				} catch (PrinterException e) {
					e.printStackTrace();
				}
		}
		
//		
//		if(job.printDialog(atributosImpressao)){
//			try {
//				job.print();
//			} catch (PrinterException e) {
//				e.printStackTrace();
//			}
//		}
	}

	/**
	 * Close the current document.
	 */
	public void doClose() {
		if (thumbs != null) {
			thumbs.stop();
		}
		if (olf != null) {
			olf.setVisible(false);
			olf = null;
		}
		if (fazerMiniatura) {
			thumbs = new ThumbPanel(null);
			thumbscroll.getViewport().setView(thumbs);
		}

		abas = new JTabbedPane();
		split.setLeftComponent(abas);

		page.showPage(null);
		curFile = null;
		setTitle(TITLE);
		setEnabling();
	}

	public static Image getFaviIcon(String fileName) {
		URL url = null;
		url = FormPrincipal.class.getResource(fileName);
		Image faviIcon = Toolkit.getDefaultToolkit().getImage(url);
		return faviIcon;
	}

	/**
	 * Shuts down all known threads. This ought to cause the JVM to quit if the
	 * PDFViewer is the only application running.
	 */
	public void doQuit() {
		dispose();
		System.exit(0);
	}

	/**
	 * Turns on zooming.
	 */
	public void doZoomTool() {
		if (fspp == null) {
			page.useZoomTool(true);
		}
	}

	/**
	 * Turns off zooming; makes the page fit in the window.
	 */
	public void doFitInWindow() {
		if (fspp == null) {
			// page.useZoomTool(false);
			page.setClip(null);
			// montarPagina();
		}
	}

	/**
	 * Shows or hides the thumbnails by moving the split pane divider.
	 * 
	 * @param show
	 *            the show
	 */
	public void doThumbs(boolean show) {
		if (show) {
			split.setDividerLocation((int) thumbs.getPreferredSize().width
					+ (int) thumbscroll.getVerticalScrollBar().getWidth() + 4);
		} else {
			split.setDividerLocation(0);
		}
	}

	public void deslocamentoY(int value) {
		page.setLocation(page.getX(), page.getY() + value);
		// pesquisaField.setText(page.getBounds() + "");
	}

	public void deslocamentoX(int value) {
		page.setLocation(page.getX() + value, page.getY());
		// pesquisaField.setText(page.getBounds() + "");
	}

	public void deslocamentoXY() {

	}

	/**
	 * Do zoom.
	 * 
	 * @param factor
	 *            the factor
	 */
	public void doZoom(double factor) {
		logger.info("Zoom para factor " + factor );
		int width = (int) Math.round(pnlPage.getWidth() * factor);
		int heigth = (int) Math.round(pnlPage.getHeight() * factor);
		page.setSize(width - 30, heigth - 30);
		centralizarPageNoPainelCentral();
	}

	/**
	 * Goes to the next page.
	 */
	public void doNext() {
		gotoPage(curpage + 1);
	}

	/**
	 * Goes to the previous page.
	 */
	public void doPrev() {
		gotoPage(curpage - 1);
	}

	/**
	 * Goes to the first page.
	 */
	public void doFirst() {
		gotoPage(0);
	}

	/**
	 * Goes to the last page.
	 */
	public void doLast() {
		gotoPage(curFile.getNumPages() - 1);
	}

	/**
	 * Goes to the page that was typed in the page number text field.
	 */
	public void doPageTyped() {
		int pagenum = -1;
		try {
			pagenum = Integer.parseInt(pageField.getText()) - 1;
		} catch (NumberFormatException nfe) {
		}
		if (pagenum >= curFile.getNumPages()) {
			pagenum = curFile.getNumPages() - 1;
		}
		if (pagenum >= 0) {
			if (pagenum != curpage) {
				gotoPage(pagenum);
			}
		} else {
			pageField.setText(String.valueOf(curpage));
		}
	}

	/**
	 * Runs the FullScreenMode change in another thread.
	 */
	class PerformFullScreenMode implements Runnable {

		/** The force. */
		boolean force;

		/**
		 * Instantiates a new perform full screen mode.
		 * 
		 * @param forcechoice
		 *            the forcechoice
		 */
		public PerformFullScreenMode(boolean forcechoice) {
			force = forcechoice;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			fspp = new PagePanel();
			fspp.setBackground(Color.black);
			page.showPage(null);
			fullScreen = new JanelaTelaCheia(fspp, force);
			fspp.addKeyListener(FormPrincipal.this);
			gotoPage(curpage);
		}
	}

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main(String args[]) {

		
		logger.info("Iniciando o LeitorIDR");
		// REALIZA INSTALACAO DE ARQUIVOS NECESSARIOS
		InstalacaoBO instalacaoBo = new InstalacaoBO();
		instalacaoBo.instalar();

		try {
			UIManager.setLookAndFeel(UIManager
					.getCrossPlatformLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		logger.info("Abrindo janela splash");
		
		// TELA SPLASH
		JanelaSplash telaSplash = new JanelaSplash(3000,
				(ImageIcon) (getIcon("gfx/splash.png")));
		telaSplash.setVisible(true);
		telaSplash.ControlaTempoApresentacao();

		// TODO DEPOIS REALIZAR A VERIFICAÇÃO SE O ARQUIVO LIC É IGUAL AS
		// INFORMACOES DA MAQUINA
		RegistroBO registroBO = new RegistroBO();

		String fileName = "configuracoes/ibracon.pdf";
		boolean useThumbs = true;

		
		formPrincipal = new FormPrincipal(useThumbs);

		// AUTENTICAÇÃO DE PROXY
		Authenticator.setDefault(new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				Properties prop = new ProxyBO().findProxyProperties();
				final String usuario = prop.getProperty("usuario");
				final String senha = prop.getProperty("senha");

				logger.info("Configurando proxy no Authenticator.setDefault");
				
				if (usuario != null && senha != null && !usuario.equals("")
						&& !senha.equals("")) {
					return new PasswordAuthentication(usuario, senha
							.toCharArray());
				} else {
					JOptionPane.showMessageDialog(null,
							"Autenticação inválida. Tente novamente");
					return null;
				}
			}
		});

		if (fileName != null) {
			formPrincipal.abrir(fileName);
		}

		new ProxyBO().dialogConfigurarProxy(formPrincipal);

		if (!registroBO.licencaEValida(formPrincipal)) {
			if (IdrUtil.internetEstaAtiva()) {
				new JanelaRegistro(formPrincipal, true);
			} else {
				JOptionPane
						.showMessageDialog(
								formPrincipal,
								"Sua licença não é válida e não existe conexão com a internet. O Aplicativo será encerrado.");
				System.exit(0);
			}
		} else {
			if (!IdrUtil.internetEstaAtiva()) {
				JOptionPane
						.showMessageDialog(
								formPrincipal,
								"Não foi encontrada conexão com a internet.\n "
										+ "Seu aplicativo será aberto sem recursos para procura de livros online.\n "
										+ "Se for necessário ajuste suas configurações de proxy em (Configurar/Proxy).",
								"Sem Internet", JOptionPane.WARNING_MESSAGE);
			}
			EstantesBO estantesBO = new EstantesBO();
			estantesBO.conectarEstante(formPrincipal, "", "");
		}

		// Não retirar esta linha
		formPrincipal.setEnabled(true);

		formPrincipal.mostraLivrosBaixados();
	}

	/**
	 * Handle a key press for navigation.
	 * 
	 * @param evt
	 *            the evt
	 */
	public void keyPressed(KeyEvent evt) {
		int code = evt.getKeyCode();
		if (code == evt.VK_LEFT) {
			doPrev();
		} else if (code == evt.VK_RIGHT) {
			doNext();
		} else if (code == evt.VK_UP) {
			doPrev();
		} else if (code == evt.VK_DOWN) {
			doNext();
		} else if (code == evt.VK_HOME) {
			doFirst();
		} else if (code == evt.VK_END) {
			doLast();
		} else if (code == evt.VK_PAGE_UP) {
			doPrev();
		} else if (code == evt.VK_PAGE_DOWN) {
			doNext();
		} else if (code == evt.VK_SPACE) {
			doNext();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	public void keyReleased(KeyEvent evt) {
	}

	/**
	 * Someone changed the selection of the outline tree. Go to the new page.
	 * 
	 * @param e
	 *            the e
	 */
	public void valueChanged(TreeSelectionEvent e) {
		if (e.isAddedPath()) {
			OutlineNode node = (OutlineNode) e.getPath().getLastPathComponent();
			if (node == null) {
				return;
			}

			try {
				PDFAction action = node.getAction();
				if (action == null) {
					return;
				}

				if (action instanceof GoToAction) {
					PDFDestination dest = ((GoToAction) action)
							.getDestination();
					if (dest == null) {
						return;
					}

					PDFObject page = dest.getPage();
					if (page == null) {
						return;
					}

					int pageNum = curFile.getPageNumber(page);
					if (pageNum >= 0) {
						gotoPage(pageNum);
					}
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

	public void recarregaPagina() {
		sliderZoom.setValue(sliderZoom.getValue() + 1);
		sliderZoom.setValue(sliderZoom.getValue() - 1);

		barraRolagemVertical.setValue(barraRolagemVertical.getValue() + 1);
		barraRolagemVertical.setValue(barraRolagemVertical.getValue() - 1);
	}

	public void mudaZoom(double valor) {
		try {
			if (valor <= 300) {
				doZoom((valor * 1.6) / 100);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public FormPrincipal getInstance() {
		return this;
	}

	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

}
