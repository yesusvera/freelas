package br.com.ibracon.idr.form.modal;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import net.java.dev.designgridlayout.DesignGridLayout;
import br.com.ibracon.idr.form.FormPrincipal;
import br.com.ibracon.idr.form.bo.EstantesBO;

public class JanelaBoasVindas extends JDialog {

	private static final long serialVersionUID = 4155990782025328279L;

	JTextField palavraChaveField = new JTextField();
	JPasswordField senhaField = new JPasswordField();

	JButton btnProsseguir = new JButton("Prosseguir");
	JButton btnCancelar = new JButton("Cancelar");

	JLabel lblTexto = new JLabel();

	FormPrincipal formPrincipal;

	public JanelaBoasVindas(FormPrincipal formPrincipal) {
		super(formPrincipal, true);

		this.formPrincipal = formPrincipal;

		setTitle("Seja bem vindo ao " + formPrincipal.getTitle());

		configuracoesBasicas();
		acrescentaComponentes();
		carregaInformacoes();
		centralizaDialog();
		pack();
		setVisible(true);
	}

	private void carregaInformacoes() {
		StringBuffer strTexto = new StringBuffer();
//		strTexto.append("<html><body><center> ");
//		strTexto.append("Bem vindo ao IDR - Ibracon Digital Reader <br><br>");
//		strTexto.append("O Ibracon Digital Reader é sua ferramenta oficial para leitura de livros disponibilzados aos associados e interessados em conteúdos oferecidos pelo IBRACON. <br><br>");
//		strTexto.append("Através do IDR  ter acesso a conteúdos (livros, artigos e etc) baixados no portal do Ibracon ou em nossa loja virtual.<br><br>");
//		strTexto.append("Para leitura de conteúdos cedidos através de empresas contratantes (caso você seja um profissional de uma empresa associada) ");
//		strTexto.append("basta preencher abaixo os campos palavra chave e senha (que sua empresa lhe forneceu) ");
//		strTexto.append("e você passará a ter acesso de direito ao conteúdo. <br><br>");
//		strTexto.append("Se não for o seu caso basta prosseguir.<br><br>");
//		strTexto.append("Muito obrigado pelo uso de nossos produtos e serviços. <br><br>");
//
//		strTexto.append("</center> </body></html>");
		strTexto.append("Por favor informar a palavra chave e senha:");
		lblTexto.setText(strTexto.toString());
	}

	private void acrescentaComponentes() {

		getContentPane().setLayout(new BorderLayout());

		btnProsseguir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EstantesBO estantesBO = new EstantesBO();
				estantesBO.conectarEstante(formPrincipal, palavraChaveField.getText(), senhaField.getText());
				hide();
				dispose();
				formPrincipal.setAbaIndex(3);
				formPrincipal.mostraLivrosDeDireito();
			}

			
		});
		
		btnCancelar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				hide();
				dispose();
			}
		});

		JPanel pnlCentro = new JPanel();
		DesignGridLayout design = new DesignGridLayout(pnlCentro);
		design.row().grid(new JLabel("Palavra-chave:")).add(palavraChaveField);
		design.row().grid(new JLabel("Senha:")).add(senhaField);
		design.emptyRow();
		design.row().center().add(btnProsseguir).add(btnCancelar);

		getContentPane().add(lblTexto, BorderLayout.NORTH);
		getContentPane().add(pnlCentro, BorderLayout.CENTER);
	}

	private void configuracoesBasicas() {
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
//		this.addWindowListener(new WindowAdapter() {
//			public void windowClosing(WindowEvent we) {
//				System.exit(0);
//				dispose();
//			}
//		});
//		setSize(500, 400);
	}

	private void centralizaDialog() {
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (screen.width - getWidth()) / 2;
		int y = (screen.height - getHeight()) / 2;
		setLocation(x, y);
	}

	private JanelaBoasVindas getInstance() {
		return this;
	}

}
