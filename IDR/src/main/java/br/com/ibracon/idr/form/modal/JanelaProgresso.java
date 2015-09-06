package br.com.ibracon.idr.form.modal;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.Window;

import javax.swing.JProgressBar;
import javax.swing.JWindow;

import org.apache.log4j.Logger;

public class JanelaProgresso extends JWindow {

	static Logger logger = Logger.getLogger(JanelaProgresso.class);

	private static final long serialVersionUID = -7763925838311676458L;

	private JProgressBar progresso = new JProgressBar();

	public JanelaProgresso(Window owner) {
		super(owner);

		getContentPane().setLayout(new FlowLayout());
		centralizaDialog();
		configuracoesBasicas();

		validate();
	}

	private void configuracoesBasicas() {
		progresso.setMinimum(1);
		progresso.setMaximum(100);
		progresso.setStringPainted(true);
		progresso.setBorderPainted(false);

//		getContentPane().setBackground(new Color(35, 142, 35));
//		getContentPane().setForeground(Color.white);
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
		progresso.validate();
		validate();
	}

	public void setTexto(String texto) {

		progresso.setString(texto);
		 validate();
		pack();
	}

	public void setPercentual(int percentual) {
		progresso.setValue(percentual);
		progresso.validate();
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

		jp.setTexto("Fazendo um texto longo para testar como o progress se comporta.");

		jp.aumentaPercentual(10);
		logger.debug("teste");
	}

}
