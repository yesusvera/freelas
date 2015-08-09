package br.com.ibracon.idr.gerador;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.security.auth.callback.ConfirmationCallback;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import net.java.dev.designgridlayout.DesignGridLayout;
import br.com.ibracon.idr.form.bo.GeradorBO;
import br.com.ibracon.idr.form.modal.JanelaProgresso;
import br.com.ibracon.idr.form.model.LivroIDR;

public class GeradorIDR extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;

	DesignGridLayout layout = new DesignGridLayout(this);

	JLabel lblCodigoLivro = new JLabel("Código");
	JTextField fieldCodigoLivro = new JTextField();

	JLabel lblTitulo = new JLabel("Título");
	JTextField fieldTitulo = new JTextField();

	JLabel lblVersao = new JLabel("Versão");
	JTextField fieldVersao = new JTextField();

	JLabel lblCodigoLoja = new JLabel("Código Loja");
	JTextField fieldCodigoLoja = new JTextField();

	JLabel lblFotoFile = new JLabel("Arquivo Foto");
	JTextField fieldFotoFile = new JTextField();
	JButton btnFotoFile = new JButton("...");

	JLabel lblIndiceXmlFile = new JLabel("Arquivo Índice XML");
	JTextField fieldIndiceXmlFile = new JTextField();
	JButton btnIndiceXmlFile = new JButton("...");

	JLabel lblPdfFile = new JLabel("Arquivo PDF");
	JTextField fieldPdfFile = new JTextField();
	JButton btnPdfFile = new JButton("...");

	JButton btnGerarIBR = new JButton("Gerar arquivo IDR");


	public JanelaProgresso jp;
	/**
	 * FILTROS FileChooser
	 */
	/** The pdf filter. */
	FileFilter filtroPDF = new FileFilter() {
		public boolean accept(File f) {
			return f.isDirectory() || f.getName().endsWith(".pdf");
		}

		public String getDescription() {
			return "Escolha o livro PDF Ibracon";
		}
	};
	FileFilter filtroXml = new FileFilter() {
		public boolean accept(File f) {
			return f.isDirectory() || f.getName().endsWith(".xml");
		}

		public String getDescription() {
			return "Escolha o arquivo xml do índice";
		}
	};
	FileFilter filtroImagem = new FileFilter() {
		public boolean accept(File f) {
			return f.isDirectory() || f.getName().endsWith(".png")
					|| f.getName().endsWith(".jpg");
		}

		public String getDescription() {
			return "Escolha um arquivo de imagem (JPG ou PNG)";
		}
	};
	FileFilter filtroDiretorio = new FileFilter() {
		public boolean accept(File f) {
			return f.isDirectory();
		}

		public String getDescription() {
			return "Escolha um diretório";
		}
	};

	public GeradorIDR() {
		super("IBRACON - Gerador do livro *.IDR");
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		acrescentaComponentes();
		configuraComponentes();

		configuraAcoesBotoes();
	
		
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				System.exit(0);
				dispose();
			}
		});
		setVisible(true);
		pack();
		centraliza();
		
	}

	private void configuraComponentes() {
		fieldFotoFile.setEditable(false);
		fieldIndiceXmlFile.setEditable(false);
		fieldPdfFile.setEditable(false);
	}

	private void acrescentaComponentes() {
		layout.row().grid().add(lblCodigoLivro).add(fieldCodigoLivro);
		layout.row().grid().add(lblTitulo).add(fieldTitulo);
		layout.row().grid().add(lblVersao).add(fieldVersao);
		layout.row().grid().add(lblCodigoLoja).add(fieldCodigoLoja);
		layout.row().grid().add(lblFotoFile).add(fieldFotoFile)
				.add(btnFotoFile);
		layout.row().grid().add(lblIndiceXmlFile).add(fieldIndiceXmlFile)
				.add(btnIndiceXmlFile);
		layout.row().grid().add(lblPdfFile).add(fieldPdfFile).add(btnPdfFile);
		layout.row().center().add(btnGerarIBR);
	}

	private void configuraAcoesBotoes() {
		btnFotoFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				escolherArquivo(filtroImagem, fieldFotoFile);
			}
		});
		btnIndiceXmlFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				escolherArquivo(filtroXml, fieldIndiceXmlFile);
			}
		});
		btnPdfFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				escolherArquivo(filtroPDF, fieldPdfFile);
			}
		});
		btnGerarIBR.addActionListener(this);
	}

	public LivroIDR getLivroIDR() {
		LivroIDR livroIDR = new LivroIDR();
		livroIDR.setCodigoLivro(fieldCodigoLivro.getText());
		livroIDR.setCodigoLoja(fieldCodigoLoja.getText());
		if (!fieldFotoFile.getText().equals("")) {
			livroIDR.setFotoFile(new File(fieldFotoFile.getText()));
		}
		if (!fieldIndiceXmlFile.getText().equals("")) {
			livroIDR.setIndiceXmlFile(new File(fieldIndiceXmlFile.getText()));
		}
		if (!fieldPdfFile.getText().equals("")) {
			livroIDR.setPdfFile(new File(fieldPdfFile.getText()));
		}
		livroIDR.setTitulo(fieldTitulo.getText());
		livroIDR.setVersao(fieldVersao.getText());
		return livroIDR;
	}

	private void centraliza() {
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (screen.width - getWidth()) / 2;
		int y = (screen.height - getHeight()) / 2;
		setLocation(x, y);
	}

	public void escolherArquivo(FileFilter filtro, JTextField fieldPath) {
		JFileChooser fc = new JFileChooser();
		fc.setFileFilter(filtro);
		fc.setMultiSelectionEnabled(false);
		int returnVal = fc.showOpenDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			fieldPath.setText(fc.getSelectedFile().getAbsolutePath());
		}
	}

	public GeradorIDR getInstance() {
		return this;
	}

	public void actionPerformed(ActionEvent e) {
		try {
			btnGerarIBR.setEnabled(false);
			new Thread(new Runnable() {
				public void run() {
					JOptionPane.showMessageDialog(getInstance(),
							"Escolha o diretório onde o arquivo será salvo...");

					JFileChooser fc = new JFileChooser();
					fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					fc.setMultiSelectionEnabled(false);
					int returnVal = fc.showOpenDialog(getInstance());

					if (returnVal == JFileChooser.APPROVE_OPTION) {
						jp = new JanelaProgresso(getInstance());
						jp.aparecer();
						jp.setTexto("Montando o arquivo");
						
						String caminhoDiretorioSalvar = fc.getSelectedFile().getAbsolutePath();
						LivroIDR livroIDR = getLivroIDR();

						GeradorBO geradorBo = new GeradorBO();
						try {
							int intConfirmIndexacao = (JOptionPane.showConfirmDialog(null, "Deseja gerar a indexação dentro do IDR?"));
							boolean gerarIndexacao = intConfirmIndexacao == JOptionPane.OK_OPTION;
							
							geradorBo.salvarLivroIDR(livroIDR, caminhoDiretorioSalvar,getInstance(), gerarIndexacao);
						} catch (IOException e) {
							e.printStackTrace();
						}
						jp.encerrar();
						JOptionPane.showMessageDialog(getInstance(), "Arquivo gerado com sucesso: " + caminhoDiretorioSalvar + File.separator + livroIDR.getCodigoLivro() + ".idr");
					}
				}
			}
			).start();
		} catch (Exception e2) {
		}
		btnGerarIBR.setEnabled(true);
	}
	
	public static void main(String[] args) {
		new GeradorIDR();
	}
}
