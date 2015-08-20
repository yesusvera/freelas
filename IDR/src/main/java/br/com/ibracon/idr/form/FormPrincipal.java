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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
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
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.OceanTheme;

import org.apache.log4j.Logger;
import org.faceless.pdf2.viewer3.PDFViewer;
import org.faceless.pdf2.viewer3.ViewerFeature;

import com.sun.pdfview.OutlineNode;
import com.sun.pdfview.PDFDestination;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFObject;
import com.sun.pdfview.PageChangeListener;
import com.sun.pdfview.action.GoToAction;
import com.sun.pdfview.action.PDFAction;

import br.com.ibracon.idr.form.bo.EstantesBO;
import br.com.ibracon.idr.form.bo.InstalacaoBO;
import br.com.ibracon.idr.form.bo.LivroIdrBO;
import br.com.ibracon.idr.form.bo.ProxyBO;
import br.com.ibracon.idr.form.bo.RegistrarLivroBO;
import br.com.ibracon.idr.form.bo.RegistroBO;
import br.com.ibracon.idr.form.criptografia.CriptografiaUtil;
import br.com.ibracon.idr.form.modal.JanelaBoasVindas;
import br.com.ibracon.idr.form.modal.JanelaConfProxy;
import br.com.ibracon.idr.form.modal.JanelaProgresso;
import br.com.ibracon.idr.form.modal.JanelaRegistro;
import br.com.ibracon.idr.form.modal.JanelaSplash;
import br.com.ibracon.idr.form.model.EstanteXML;
import br.com.ibracon.idr.form.model.Livro;
import br.com.ibracon.idr.form.model.LivroIDR;
import br.com.ibracon.idr.form.model.RegistroXml;
import br.com.ibracon.idr.form.util.IdrUtil;
import br.com.ibracon.idr.webservice.estante.ResponseEstante;
import net.java.dev.designgridlayout.DesignGridLayout;

/**
 * The Class FormPrincipal.
 */
public class FormPrincipal extends JFrame implements 
		TreeSelectionListener, PageChangeListener {
	
	static Logger logger = Logger.getLogger(FormPrincipal.class);
	
	/** The Constant TITLE. */
	public final static String TITLE = "IDR - Ibracon";
	
	// start the viewer
	public static FormPrincipal formPrincipal;

	InstalacaoBO instalacaoBO = new InstalacaoBO();

	/** The cur file. */
	PDFFile curFile;

	/** The split. */
	static JSplitPane split;

	/** The page. */
	JPanel page;

	Flag docWaiter;


	/** The slider navegacao. */
	JSlider sliderNavegacao;

	/** The abas. */
	JTabbedPane abas;

	JanelaProgresso jp;

	/** The scroll pagina. */
	JScrollPane scrollPagina;

	/** The estante. */
	JPanel estante;


	/** The painel central. */
	JPanel painelCentral;

	JPanel pnlPage;

	DesignGridLayout designPainelCentral;

	JPanel pnlPagina;

	JScrollBar barraRolagemVertical;

	JScrollBar barraRolagemHorizontal;

	public RegistroXml registroXML;

	public ResponseEstante responseEstanteXML;
	public ResponseEstante responseEstanteXMLLocal;

	public EstanteXML estanteXML;
	
	public LivroIDR livroIDR = null;
	
	
	 // Specify the look and feel to use by defining the LOOKANDFEEL constant
    // Valid values are: null (use the default), "Metal", "System", "Motif",
    // and "GTK"
    final static String LOOKANDFEEL = "Metal";
    
    // If you choose the Metal L&F, you can also choose a theme.
    // Specify the theme to use by defining the THEME constant
    // Valid values are: "DefaultMetal", "Ocean",  and "Test"
    final static String THEME = "DefaultMetal";
    

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
//			abrir();
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

	/** The quit action. */
	Action quitAction = new AbstractAction("Sair do leitor") {
		public void actionPerformed(ActionEvent evt) {
			doQuit();
		}
	};

	
	private static void initLookAndFeel() {
        String lookAndFeel = null;
       
        if (LOOKANDFEEL != null) {
            if (LOOKANDFEEL.equals("Metal")) {
                lookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();
              //  an alternative way to set the Metal L&F is to replace the 
              // previous line with:
              // lookAndFeel = "javax.swing.plaf.metal.MetalLookAndFeel";
                
            }
            
            else if (LOOKANDFEEL.equals("System")) {
                lookAndFeel = UIManager.getSystemLookAndFeelClassName();
            } 
            
            else if (LOOKANDFEEL.equals("Motif")) {
                lookAndFeel = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
            } 
            
            else if (LOOKANDFEEL.equals("GTK")) { 
                lookAndFeel = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
            } 
            
            else {
                System.err.println("Unexpected value of LOOKANDFEEL specified: "
                                   + LOOKANDFEEL);
                lookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();
            }

            try {
            	
            	
                UIManager.setLookAndFeel(lookAndFeel);
                
                // If L&F = "Metal", set the theme
                
                if (LOOKANDFEEL.equals("Metal")) {
                  if (THEME.equals("DefaultMetal"))
                     MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme());
                  else if (THEME.equals("Ocean"))
                     MetalLookAndFeel.setCurrentTheme(new OceanTheme());
//                  else
//                     MetalLookAndFeel.setCurrentTheme(new TestTheme());
                     
                  UIManager.setLookAndFeel(new MetalLookAndFeel()); 
                }	
                	
                	
                  
                
            } 
            
            catch (ClassNotFoundException e) {
                System.err.println("Couldn't find class for specified look and feel:"
                                   + lookAndFeel);
                System.err.println("Did you include the L&F library in the class path?");
                System.err.println("Using the default look and feel.");
            } 
            
            catch (UnsupportedLookAndFeelException e) {
                System.err.println("Can't use the specified look and feel ("
                                   + lookAndFeel
                                   + ") on this platform.");
                System.err.println("Using the default look and feel.");
            } 
            
            catch (Exception e) {
                System.err.println("Couldn't get specified look and feel ("
                                   + lookAndFeel
                                   + "), for some reason.");
                System.err.println("Using the default look and feel.");
                e.printStackTrace();
            }
        }
    }
	
	/**
	 * Create a new PDFViewer based on a user, with or without a thumbnail
	 * panel.
	 * 
	 * @param usarMiniaturas
	 *            true if the thumb panel should exist, false if not.
	 */
	public FormPrincipal() {
		super(TITLE);
		
		initLookAndFeel();
		
		setEnabled(false);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				doQuit();
			}
		});
		ImageIcon icone = IdrUtil.getImageIcon("gfx/icone.png");
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

			JButton btnBaixar = new JButton(IdrUtil.getImageIcon("gfx/download.png"));			
			btnBaixar.setBorderPainted(false);
			btnBaixar.setBackground(Color.WHITE);
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
			Image image = ImageIO.read(new File(InstalacaoBO
					.getDiretorioBaixados().getAbsolutePath()
					+ File.separator
					+ arquivoFotoLocal));
			lblLivro = new JLabel(new ImageIcon(image));
		} catch (Exception e) {
			logger.debug("Não consegui ler a imagem...");
		}

		JButton btnAbrir = new JButton(IdrUtil.getImageIcon("gfx/abrir.png"));			
		btnAbrir.setBorderPainted(false);
		btnAbrir.setBackground(Color.WHITE);
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
					logger.debug(livro);

					abrirLivroAPINova(livro);
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
		btnTodos.setIcon(IdrUtil.getImageIcon("gfx/visaoGeral.png"));
		btnTodos.setBackground(Color.WHITE);
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
		btnBaixar.setBackground(Color.WHITE);
		btnBaixar.setIcon(IdrUtil.getImageIcon("gfx/disponiveis.png"));
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
		btnMeusLivros.setBackground(Color.WHITE);
		btnMeusLivros.setIcon(IdrUtil.getImageIcon("gfx/direitoDeUso.png"));
		btnMeusLivros.setHorizontalAlignment(SwingConstants.LEFT);
		btnMeusLivros.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new JanelaBoasVindas(getInstance());

			}

		});

		JButton btnBaixados = new JButton("Minha biblioteca");
		btnBaixados.setBackground(Color.WHITE);
		btnBaixados.setIcon(IdrUtil.getImageIcon("gfx/minhaBiblioteca.png"));
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


	/**
	 * Montar abas.
	 */
	private void montarAbas() {
		logger.info("Swing: Montando as abas");
		abas = new JTabbedPane();

//		abas.addTab("Notas", new JScrollPane(notas));
		
		abas.addTab("Estantes", estante);

		abas.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				split.setRightComponent(painelCentral);
			}
		});

		split.setLeftComponent(abas);
	}

	/**
	 * Initialize this PDFViewer by creating the GUI.
	 */
	protected void init() {
		logger.info("Swing: inicializando componentes visuais");
		page = new JPanel();

		this.setResizable(true);
		this.setExtendedState(MAXIMIZED_BOTH);
		painelCentral = new JPanel(new BorderLayout());

		montarEstante();
		split = new JSplitPane(split.HORIZONTAL_SPLIT);
		montarPagina();
		getContentPane().add(split, BorderLayout.CENTER);
		montarAbas();
		

		logger.info("Swing: Configurando Menubar");
		JMenuBar mb = new JMenuBar();
		JMenu file = new JMenu("Arquivo");
//		file.add(openAction);
//		file.addSeparator();
		file.addSeparator();
//		file.add(pageSetupAction);
//		file.add(printAction);
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
		
	}


	public void centralizarPageNoPainelCentral() {
		logger.info("Centralizando o pdf no painel central");
		Dimension screen = pnlPage.getSize();
		int x = (screen.width - page.getWidth()) / 2;
		int y = (screen.height - page.getHeight()) / 2;
		page.setLocation(x, y);
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

	/** The prev dir choice. */
	private File prevDirChoice;


	public void montaLivro() {
		logger.info("Montando o livro na tela");
		montarAbas();
//		carregarNotas();

		sliderNavegacao.setMinimum(0);
		sliderNavegacao.setMaximum(curFile.getNumPages());
		sliderNavegacao.setValue(0);
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
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 * @throws InvalidKeySpecException 
	 * @throws UnsupportedEncodingException 
	 * @throws InvalidAlgorithmParameterException 
	 * @throws NoSuchAlgorithmException 
	 * @throws IllegalBlockSizeException 
	 * @throws NoSuchPaddingException 
	 * @throws BadPaddingException 
	 * @throws InvalidKeyException 
	 */
	public static void main(String args[]) throws InvalidKeyException, BadPaddingException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, UnsupportedEncodingException, InvalidKeySpecException {

		
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
				IdrUtil.getImageIcon(("gfx/splash.png")));
		telaSplash.setVisible(true);
		telaSplash.ControlaTempoApresentacao();

		// TODO DEPOIS REALIZAR A VERIFICAÇÃO SE O ARQUIVO LIC É IGUAL AS
		// INFORMACOES DA MAQUINA
		RegistroBO registroBO = new RegistroBO();

//		String fileName = "configuracoes/ibracon.pdf";
		
		formPrincipal = new FormPrincipal();

		// AUTENTICAÇÃO DE PROXY
		Authenticator.setDefault(new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				Properties prop = new ProxyBO().findProxyProperties();
				final String usuario = CriptografiaUtil.decrypt(prop.getProperty("usuario"), "IBRACON", CriptografiaUtil.ALGORITMO_DES) ;
				final String senha = CriptografiaUtil.decrypt(prop.getProperty("senha"), "IBRACON", CriptografiaUtil.ALGORITMO_DES) ;

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

		
		JPanel pnlEstanteDeDireito = new JPanel();
		DesignGridLayout design = new DesignGridLayout(pnlEstanteDeDireito);
		pnlEstanteDeDireito.setBackground(Color.WHITE);
		ImageIcon icone = IdrUtil.getImageIcon("gfx/splash.png");
		JLabel labelImagem = new JLabel();
		labelImagem.setIcon(icone);
		
//		pnlEstanteDeDireito.add(labelImagem, BorderLayout.CENTER);
		
		
		split.setRightComponent(new JScrollPane(pnlEstanteDeDireito));
		
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


	public FormPrincipal getInstance() {
		return this;
	}

	/**
	 * Abrindo livros com a API nova AGO/2015
	 * @param livro
	 */
	public void abrirLivroAPINova(final Livro livro) {
		// Uso da API BFO para abrir o PDF
		File arquivoIdr = new File(InstalacaoBO.getDiretorioBaixados().getAbsolutePath() + File.separator
				+ livro.getNomeArquivoBaixado());

		if (arquivoIdr.getName().endsWith(".idr")) {
			try {
				livroIDR = new LivroIdrBO().getLivroIDRArrayBytes(arquivoIdr);
			} catch (InvalidKeyException e1) {
				e1.printStackTrace();
			} catch (NoSuchAlgorithmException e1) {
				e1.printStackTrace();
			} catch (NoSuchPaddingException e1) {
				e1.printStackTrace();
			} catch (InvalidAlgorithmParameterException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			arquivoIdr = livroIDR.getPdfFile();
		} else {
			return;
		}
		List<ViewerFeature> featuresx = new ArrayList<>(ViewerFeature.getAllEnabledFeatures());

		System.out.println(featuresx);

		List<ViewerFeature> features = new ArrayList<>();

		for(ViewerFeature vf: featuresx){ 
			if(!vf.toString().equalsIgnoreCase("Menus") && !vf.toString().equalsIgnoreCase("Widget:Open") && !vf.toString().equalsIgnoreCase("Widget:Save")
					 && !vf.toString().equalsIgnoreCase("Widget:ManageIdentities") && !vf.toString().equalsIgnoreCase("Widget:SelectArea")
					 && !vf.toString().equalsIgnoreCase("Widget:AnnotationAddLine")){
					features.add(vf);
			}
		}

		PDFViewer viewerLeitorIbracon = new PDFViewer(features);

		JFrame frame = new JFrame();
		frame.setTitle(livro.getTitulo());
		frame.setExtendedState(6);

		frame.getContentPane().add(viewerLeitorIbracon, BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);
		ImageIcon icone = IdrUtil.getImageIcon("gfx/icone.png");
		frame.setIconImage(icone.getImage());
		viewerLeitorIbracon.loadPDF(livroIDR,new ByteArrayInputStream(livroIDR.getPdfByteArray()), null,
				livro.getTitulo(), null);
	}

}
