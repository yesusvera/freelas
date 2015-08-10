package br.com.ibracon.idr.form.modal;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;

import br.com.ibracon.idr.form.FormPrincipal;
import net.java.dev.designgridlayout.DesignGridLayout;

/**
 * 
 * @author Administrador
 */
public class JanelaSplash extends JWindow {

	private JPanel pnlImagem;
	JPanel pnlStatus;
	private JLabel imagemLogoTipo;
	private ImageIcon imagem;
	private long tempoApresentacao;

	/** Creates a new instance of TelaSplash */
	public JanelaSplash(long tempo, ImageIcon imagem, boolean adicionaBtnSair) {
		this.imagem = imagem;
		tempoApresentacao = tempo;
		getContentPane().setBackground(Color.WHITE);
		getContentPane().setLayout(new BorderLayout());

		montaPnlImagem(imagem);

		montaPnlStatus(adicionaBtnSair);

		getContentPane().add(pnlImagem, BorderLayout.CENTER);
		getContentPane().add(pnlStatus, BorderLayout.SOUTH);
		pack();

		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (screen.width - getWidth()) / 2;
		int y = (screen.height - getHeight()) / 2;
		setLocation(x, y);
	}

	public JanelaSplash(long tempo, ImageIcon imagem) {
		this(tempo, imagem, false);
	}

	public void montaPnlImagem(ImageIcon imagem) {
		pnlImagem = new JPanel();
		pnlImagem.setBackground(Color.white);
		imagemLogoTipo = new JLabel(imagem);
		// imagemLogoTipo.setBounds(0,0,479,299);

		pnlImagem.add(imagemLogoTipo);
	}

	public void montaPnlStatus(boolean adicionarBtnSair) {
		pnlStatus = new JPanel();
		pnlStatus.setBackground(Color.white);
		DesignGridLayout ds = new DesignGridLayout(pnlStatus);
		ds.row().center().add(new JLabel("AGO/2014 - Versão 3.1"));
		if (adicionarBtnSair) {
			JButton btnSair = new JButton("Fechar");
			btnSair.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					getInstance().hide();
					getInstance().dispose();
				}
			});
			ds.row().center().add(btnSair);
		}
	}

	// Método que controla o tempo de espera para a abertura

	@SuppressWarnings({ "static-access", "deprecation" })
	public void ControlaTempoApresentacao() {
		try {
			Thread Contador = new Thread();
			Contador.start();
			Contador.sleep(tempoApresentacao);
			Contador.stop();
			// oculta a janela de apresentação e destrói o objeto da memória
			this.hide();
			this.dispose();
		} catch (Exception Erro) {
		}
	}

	public static void main(String[] args) {
		ImageIcon img = new ImageIcon(FormPrincipal.class.getResource(
				"gfx/splash.png").getFile());
		JanelaSplash telaSplash = new JanelaSplash(5000, img, true);
		telaSplash.setVisible(true);
		telaSplash.ControlaTempoApresentacao();
	}

	public JanelaSplash getInstance() {
		return this;
	}

}