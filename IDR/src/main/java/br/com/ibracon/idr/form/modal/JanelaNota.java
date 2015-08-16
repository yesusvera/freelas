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

import org.faceless.pdf2.viewer3.PDFViewer;

import br.com.ibracon.idr.form.bo.NotaBO;
import br.com.ibracon.idr.form.model.Nota;
import net.java.dev.designgridlayout.DesignGridLayout;

public class JanelaNota extends JDialog {

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
	JButton btnExcluir = new JButton("Excluir");
	
	private PDFViewer viewer;

	String serialPDF;
	int pagina;
	
	private NotaBO notaBO = new NotaBO();

	public JanelaNota(PDFViewer viewer, String serialPDF, int pagina) {
		super(viewer.getParentFrame());

		this.viewer = viewer;
		
		this.serialPDF = serialPDF;
		this.pagina = pagina;

//		setTitle("Adicionar nota - " + formPrincipal.getTitle());

		configuracoesBasicas();
		acrescentaComponentes();
		centralizaDialog();
		carregaInformacoes();
		setModal(true);
		setVisible(true);
	}

	private void carregaInformacoes() {
		Properties propNota = notaBO.pesquisarNota(this.pagina, this.serialPDF);
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
				Nota nota = new Nota(null, Integer.parseInt(paginaField
						.getText()), textoTextArea.getText(), new Date(),
						tituloField.getText(), new Date());
				notaBO.salvarNota(nota, serialPDF);
				JOptionPane.showMessageDialog(getInstance(), "Nota salva com sucesso!");
				viewer.getDocumentPanels()[0].carregarNotas();
				viewer.getDocumentPanels()[0].notas.repaint();
			}
		});
		
		btnExcluir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (JOptionPane.showConfirmDialog(getInstance(),
						"Deseja realmente excluir esta nota?",
						"Excluir nota: " + tituloField.getText(),
						JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					notaBO.excluirNota(pagina, serialPDF);
					JOptionPane.showMessageDialog(getInstance(),
							"Nota excluída com sucesso!");
					viewer.getDocumentPanels()[0].carregarNotas();
					viewer.getDocumentPanels()[0].notas.repaint();
					getInstance().dispose();
				}

			}
		});

		textoTextArea.setRows(5);

		design.row().grid(lblTitulo).add(tituloField);
		design.row().grid(lblTexto).add(new JScrollPane(textoTextArea));
		design.row().grid(lblPagina).add(paginaField);
		design.row().grid(lblDataCriacao).add(dtCriacaoField);
		design.row().grid(lblDataModificacao).add(dtModificacaoField);
		design.emptyRow();
		design.row().center().add(btnSalvar).add(btnExcluir);
	}

	private void configuracoesBasicas() {
		
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

	private JanelaNota getInstance() {
		return this;
	}
	
}
