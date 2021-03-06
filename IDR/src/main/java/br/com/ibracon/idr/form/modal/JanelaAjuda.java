package br.com.ibracon.idr.form.modal;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import br.com.ibracon.idr.form.FormPrincipal;
import br.com.ibracon.idr.form.bo.NotaBO;
import br.com.ibracon.idr.form.model.Nota;
import net.java.dev.designgridlayout.DesignGridLayout;

public class JanelaAjuda extends JDialog {

	private static final long serialVersionUID = 4155990782025328279L;

	JLabel lblTitulo = new JLabel("Título");
	JTextField tituloField = new JTextField();

	JLabel lblTexto = new JLabel("Texto");
	JTextArea textoTextArea = new JTextArea();

	JLabel lblPagina = new JLabel("Página");
	JTextField paginaField = new JTextField();
	
	JLabel lblDataCriacao = new JLabel("Data de Criação");
	JTextField dtCriacaoField = new JTextField();
	
	JLabel lblDataModificacao = new JLabel("Data de modificação");
	JTextField dtModificacaoField = new JTextField();

	JButton btnSalvar = new JButton("Salvar");

	String serialPDF;
	int pagina;
	
	FormPrincipal formPrincipal;

	public JanelaAjuda(FormPrincipal formPrincipal, String serialPDF, int pagina) {
		super(formPrincipal);

		this.formPrincipal = formPrincipal;
		
		this.serialPDF = serialPDF;
		this.pagina = pagina;

		setTitle("Adicionar nota - " + formPrincipal.getTitle());

		configuracoesBasicas();
		acrescentaComponentes();
		centralizaDialog();
		carregaInformacoes();
	}

	private void carregaInformacoes() {
		Properties propNota = new NotaBO().pesquisarNota(this.pagina, this.serialPDF);
		tituloField.setText(propNota.getProperty("titulo"));
		textoTextArea.setText(propNota.getProperty("texto"));
		dtCriacaoField.setText(propNota.getProperty("dataCriacao"));
		dtModificacaoField.setText(propNota.getProperty("dataModificacao"));
		
		paginaField.setText(String.valueOf(pagina));
	}

	private void acrescentaComponentes() {
		DesignGridLayout design = new DesignGridLayout(this);

		btnSalvar.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				int resp = JOptionPane.showConfirmDialog(getInstance(), "Deseja salvar esta nota?", "Salvar Nota", JOptionPane.YES_NO_OPTION);
				if(resp == JOptionPane.NO_OPTION){
					return;
				}
				NotaBO notaBO = new NotaBO();
				Nota nota = new Nota(null, Integer.parseInt(paginaField
						.getText()), textoTextArea.getText(), new Date(),
						tituloField.getText(), new Date());
				notaBO.salvarNota(nota, serialPDF);
//				formPrincipal.carregarNotas();
				JOptionPane.showMessageDialog(getInstance(), "Nota salva com sucesso!");
			}
		});

		textoTextArea.setRows(5);

		design.row().grid(lblTitulo).add(tituloField);
		design.row().grid(lblTexto).add(new JScrollPane(textoTextArea));
		design.row().grid(lblPagina).add(paginaField);
		design.row().grid(lblDataCriacao).add(dtCriacaoField);
		design.row().grid(lblDataModificacao).add(dtModificacaoField);
		design.emptyRow();
		design.row().center().add(btnSalvar);
	}

	private void configuracoesBasicas() {
		setModal(true);
		setVisible(true);
		setSize(500, 400);

		lblPagina.setEnabled(false);
		paginaField.setEnabled(false);
		
		lblDataCriacao.setEnabled(false);
		dtCriacaoField.setEnabled(false);
		
		lblDataModificacao.setEnabled(false);
		dtModificacaoField.setEnabled(false);
		
		textoTextArea.setAutoscrolls(true);
		textoTextArea.setLineWrap(true);
	}

	private void centralizaDialog() {
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (screen.width - getWidth()) / 2;
		int y = (screen.height - getHeight()) / 2;
		setLocation(x, y);
	}

	private JanelaAjuda getInstance() {
		return this;
	}
	
}
