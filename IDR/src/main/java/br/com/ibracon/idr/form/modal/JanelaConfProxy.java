package br.com.ibracon.idr.form.modal;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import br.com.ibracon.idr.form.bo.ProxyBO;
import net.java.dev.designgridlayout.DesignGridLayout;

public class JanelaConfProxy extends JDialog {

	private static final long serialVersionUID = 4155990782025328279L;

	JLabel lblProxy = new JLabel("Servidor Proxy da web");
	JTextField proxyField = new JTextField();

	JLabel lblPorta = new JLabel("Porta:");
	JTextField portaField = new JTextField();

	JLabel lblUsuario = new JLabel("Usuário: ");
	JTextField usuarioField = new JTextField();
//
	JLabel lblPassword = new JLabel("Senha: ");
	JPasswordField passwordField = new JPasswordField();
	
//	JLabel lblIgnorar = new JLabel("Ignorar Configurações de proxy para os seguintes domínios:");
//	JTextArea ignorarTextArea = new JTextArea();
	
//	JCheckBox utilizarSistemaCheckBox = new JCheckBox("Utilizar configuração de proxy do sistema operacional.");

	JButton btnSalvar = new JButton("Salvar");

	public JanelaConfProxy(Frame frame) {
		super(frame);
		configuracoesBasicas();
		acrescentaComponentes();
		setSize(300,150);
		centralizaDialog();
		carregaInformacoesProperties();
	}

	private void carregaInformacoesProperties() {
		Properties propProxy = new ProxyBO().findProxyProperties();
		proxyField.setText(propProxy.getProperty("proxy"));
		portaField.setText(propProxy.getProperty("porta"));
//		usuarioField.setText(propProxy.getProperty("usuario"));
//		passwordField.setText(propProxy.getProperty("senha"));
//		ignorarTextArea.setText(propProxy.getProperty("ignorar"));
//		utilizarSistemaCheckBox.setSelected(new Boolean(propProxy.getProperty("proxySistema")));
	}

	private void acrescentaComponentes() {
		DesignGridLayout design = new DesignGridLayout(this);

		btnSalvar.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				Properties properties = new Properties();
				properties.setProperty("proxy", proxyField.getText());
				properties.setProperty("porta", portaField.getText());
				properties.setProperty("usuario", usuarioField.getText());
				properties.setProperty("senha", passwordField.getText());
//				properties.setProperty("ignorar", ignorarTextArea.getText());
//				properties.setProperty("proxySistema", String.valueOf(utilizarSistemaCheckBox.isSelected()));
				
				ProxyBO proxyBO = new ProxyBO();

				proxyBO.salvarProxyProperties(properties);
				proxyBO.configurarProxy();
				
				JOptionPane.showMessageDialog(getInstance(),
						"Configurações de proxy salvas com sucesso!");
			}
		});

//		ignorarTextArea.setRows(5);
		
		design.row().grid(lblProxy).add(proxyField);
		design.row().grid(lblPorta).add(portaField);
		design.row().grid(lblUsuario).add(usuarioField);
		design.row().grid(lblPassword).add(passwordField);
//		design.row().center().add(lblIgnorar);
//		design.row().center().add(ignorarTextArea);
//		design.row().center().add(utilizarSistemaCheckBox);
		design.emptyRow();
		design.row().center().add(btnSalvar);
	}

	private void configuracoesBasicas() {
		setVisible(true);
		setTitle("IDR - Configurar proxy");
	}

	private void centralizaDialog() {
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (screen.width - getWidth()) / 2;
		int y = (screen.height - getHeight()) / 2;
		setLocation(x, y);
	}

	private JanelaConfProxy getInstance() {
		return this;
	}
}
