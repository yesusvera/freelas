package br.com.ibracon.idr.form.modal;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.JWindow;

import org.apache.log4j.Logger;

import br.com.ibracon.idr.form.criptografia.FileCrypt;

public class JanelaProgresso extends JWindow {
	
	static Logger logger = Logger.getLogger(JanelaProgresso.class);

	private static final long serialVersionUID = -7763925838311676458L;

	private JProgressBar progresso = new JProgressBar();

	JFrame frm = null;

	public JanelaProgresso(JFrame owner) {
		super(owner);
		frm = owner;
		getContentPane().setLayout(new FlowLayout());
		centralizaDialog();
		configuracoesBasicas();
		pack();
	}

	private void configuracoesBasicas() {
		progresso.setMinimum(1);
		progresso.setMaximum(100);
		getContentPane().add(progresso);
	}

	private void centralizaDialog() {
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

		int x = (screen.width - getWidth()) / 2;
		int y = (screen.height - getHeight()) / 2;
		setLocation(x, y);
	}

	public void aumentaPercentual(final int percentual) {
		progresso.setValue(progresso.getValue() + percentual);
		progresso.repaint();
	}

	public void setTexto(String texto) {
		progresso.setStringPainted(true);
		progresso.setString(texto);
		pack();
	}

	public void setPercentual(int percentual) {
		progresso.setValue(percentual);
		progresso.repaint();
	}

	public void aparecer() {
		setVisible(true);
	}

	@SuppressWarnings("deprecation")
	public void encerrar() {
		this.hide();
		this.dispose();
	}

	public static void main(String[] args) {
		JanelaProgresso jp = new JanelaProgresso(null);
		jp.aparecer();

		logger.debug("teste");
	}

}
