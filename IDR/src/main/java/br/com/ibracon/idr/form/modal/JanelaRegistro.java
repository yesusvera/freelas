package br.com.ibracon.idr.form.modal;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.ParseException;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import br.com.ibracon.idr.form.FormPrincipal;
import br.com.ibracon.idr.form.bo.EstantesBO;
import br.com.ibracon.idr.form.bo.RegistroBO;
import br.com.ibracon.idr.form.util.IdrUtil;
import br.com.ibracon.idr.webservice.registrar.RequestRegistrar;
import net.java.dev.designgridlayout.DesignGridLayout;

public class JanelaRegistro extends JDialog {

	private static final long serialVersionUID = -4078892796646341350L;

	private boolean eAssociado = false;
	private static int CPF = 1;
	private static int CNPJ = 2;
	private JLabel lblMensagens = new JLabel("");

	// private JTextField matriculaField = new JTextField();
	private JTextField registroField = new JTextField();
	private JTextField nomeRazaoField = new JTextField();
	private JTextField enderecoField = new JTextField();
	private JTextField numeroField = new JTextField();
	private JTextField complementoField = new JTextField();
	private JTextField bairroField = new JTextField();
	private JTextField cidadeField = new JTextField();
	private JTextField cepField = new JTextField();
	private JTextField emailField = new JTextField();
	private JPasswordField senhaField = new JPasswordField();
	private JTextField serialField = new JTextField();
	private JTextField macadressField = new JTextField();
	private JTextField ipField = new JTextField();
	private JFormattedTextField documentoField = new JFormattedTextField();
	private JTextField telefoneField = new JTextField();
	private JTextField dispositivoField = new JTextField();
	private JComboBox<String> cpfCnpjCombo = new JComboBox<String>(
			new String[] { "CPF", "CNPJ" });

	private JLabel registroLabel = new JLabel("Registro Nacional:");
	private JLabel nomeRazaoLabel = new JLabel("(*) Nome/Razão Social:");
	private JLabel enderecoLabel = new JLabel("Endereço:");
	private JLabel numeroLabel = new JLabel("Número");
	private JLabel complementoLabel = new JLabel("Complemento:");
	private JLabel bairroLabel = new JLabel("Bairro:");
	private JLabel cidadeLabel = new JLabel("Cidade:");
	private JLabel ufLabel = new JLabel("UF:");
	private JLabel cepLabel = new JLabel("CEP:");
	private JLabel emailLabel = new JLabel("Email:");
	private JLabel senhaLabel = new JLabel("(*)Nova Senha:");
	private JLabel serialLabel = new JLabel("Serial:");
	private JLabel macadressLabel = new JLabel("Mac Adress:");
	private JLabel ipLabel = new JLabel("IP:");
	private JLabel documentoLabel = new JLabel("CPF/CNPJ:");
	private JLabel telefoneLabel = new JLabel("Telefone:");
	private JLabel dispositivoLabel = new JLabel("Dispositivo:");

	private JButton btnRegistrar = new JButton("Registrar");
	private JButton btnCancelar = new JButton("Cancelar");

	String[] ufs = { "AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO",
			"MA", "MT", "MS", "MG", "PA", "PB", "PR", "PE", "PI", "RJ", "RN",
			"RS", "RO", "RR", "SC", "SP", "SE", "TO" };
	JComboBox<String> listaUFCombo = new JComboBox<String>(ufs);

	JRadioButton simAssociadoRb = new JRadioButton("Sim");
	JRadioButton naoAssociadoRb = new JRadioButton("Não");

	JRadioButton cpfRb = new JRadioButton("CPF");
	JRadioButton cnpjRb = new JRadioButton("CNPJ");

	ButtonGroup eAssociadoBg = new ButtonGroup();
	ButtonGroup cpfCnpjBg = new ButtonGroup();

	RegistroBO registroBO = new RegistroBO();

	FormPrincipal formPrincipal = null;

	ActionListener actionAssociado = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			getInstance().hide();
			getInstance().dispose();
			new JanelaRegistro(formPrincipal,simAssociadoRb.isSelected());
		}
	};

	ActionListener actionCpfCnpj = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (cpfRb.isSelected()) {
				setMascaraCpfCnpj(CPF);
			}
			if (cnpjRb.isSelected()) {
				setMascaraCpfCnpj(CNPJ);
			}
		}
	};

	public JanelaRegistro(FormPrincipal formPrincipal, boolean associado) {
		super(formPrincipal, true);

		setTitle("Registro - Leitor IDR ");

		
		this.eAssociado = associado;
		
		this.formPrincipal = formPrincipal;
		configuracoesBasicas();
		acrescentaComponentes();
		
		montarFormularios(associado);

		carregaInformacoes();

		pack();
		centralizaDialog();
		setVisible(true);
	}

	private void carregaInformacoes() {
		ipField.setText(registroBO.ipMaquinaCliente());
		macadressField.setText(registroBO.macAdressMaquinaCliente());
		dispositivoField.setText(registroBO.hostName());
		serialField.setText(registroBO.getHDSerial());

	}

	private void acrescentaComponentes() {
		btnCancelar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (JOptionPane.showConfirmDialog(getInstance(),
						"Deseja realmente cancelar o registro?",
						"Cancelar Registro", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					getInstance().hide();
					dispose();
					System.exit(0);
				}
			}
		});

		btnRegistrar.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				if (!IdrUtil.internetEstaAtiva()) {

					JOptionPane
							.showMessageDialog(
									getInstance(),
									"Você não possui conexão com a internet ou o servidor do ibracon está fora do ar. Contate o Administrador");

					return;
				}
				
				//VALIDAÇÕES
				if(!eAssociado && nomeRazaoField.getText().trim().equals("")){
					JOptionPane.showMessageDialog(getInstance(), "O campo Razão Social é de preenchimento obrigatório.");
					return;
				}
				
//				if(senhaField.getText().replace(".", "").replace("-", "").trim().equals("")){
//					JOptionPane.showMessageDialog(getInstance(), "O campo Senha é de preenchimento obrigatório.");
//					return;
//				}
				
				if(documentoField.getText().replace(".", "").replace("-", "").trim().equals("")){
					JOptionPane.showMessageDialog(getInstance(), "O campo Documento é de preenchimento obrigatório.");
					return;
				}

				final RequestRegistrar requestRegistrar = new RequestRegistrar();

				requestRegistrar.setBairro(bairroField.getText());
				requestRegistrar.setCep(cepField.getText());
				requestRegistrar.setCidade(cidadeField.getText());
				requestRegistrar.setCliente(nomeRazaoField.getText());
				requestRegistrar.setComplemento(complementoField.getText());
				requestRegistrar.setEmail(emailField.getText());
				requestRegistrar.setEndereco(enderecoField.getText());
				requestRegistrar.setIp(ipField.getText());
				requestRegistrar.setMacadress(macadressField.getText());
				requestRegistrar.setNumero(numeroField.getText());
				requestRegistrar.setRegistro(registroField.getText());
				requestRegistrar.setSenha(senhaField.getText());
				requestRegistrar.setSerial(serialField.getText());
				requestRegistrar.setUf(listaUFCombo.getSelectedItem()
						.toString());
				requestRegistrar.setDocumento(documentoField.getText());
				requestRegistrar.setTelefone(telefoneField.getText());
				requestRegistrar.setDispositivo(dispositivoField.getText());
				if (simAssociadoRb.isSelected()) {
					requestRegistrar.setAssociado("s");
				} else {
					requestRegistrar.setAssociado("n");
				}

				getInstance().setVisible(false);
				
				new Thread(new Runnable() {
					@Override
					public void run() {
						if (registroBO.registrarDispositivo(requestRegistrar, formPrincipal)) {
							// new JanelaBoasVindas(formPrincipal);
							EstantesBO estantesBO = new EstantesBO();
							estantesBO.conectarEstante(formPrincipal, "", "");
							formPrincipal.mostraLivrosBaixados();
							dispose();
						}else{
							getInstance().setVisible(true);
						}
					}
				}).start();;
			}

		});
	}

	private void montarFormularios(boolean associado) {
		JPanel pnl = new JPanel();
		DesignGridLayout design = new DesignGridLayout(pnl);

		JPanel pnlEAssociado = new JPanel(new FlowLayout());
		pnlEAssociado.add(new JLabel("Associado Ibracon?"));
		pnlEAssociado.add(simAssociadoRb);
		pnlEAssociado.add(naoAssociadoRb);

		JPanel pnlCPFCNPJ = new JPanel(new GridLayout(2, 1));
		pnlCPFCNPJ.add(cpfRb);
		pnlCPFCNPJ.add(cnpjRb);

		JPanel pnlDoc = new JPanel(new BorderLayout());
		pnlDoc.add(documentoField, BorderLayout.CENTER);
		pnlDoc.add(new JLabel(" "), BorderLayout.NORTH);
		pnlDoc.add(new JLabel(" "), BorderLayout.SOUTH);

		design.row().center().add(pnlEAssociado);

		if (associado) {
			design.row().grid(registroLabel).add(registroField);
		} else {
			design.row().grid(nomeRazaoLabel).add(nomeRazaoField);
		}
//		design.row().grid(senhaLabel).add(senhaField);	
		design.row().center().add(pnlCPFCNPJ).add(pnlDoc);

		if (!associado) {
			design.row().grid(telefoneLabel).add(telefoneField);
			design.row().grid(enderecoLabel).add(enderecoField);
			design.row().grid(numeroLabel).add(numeroField)
					.add(complementoLabel).add(complementoField);
			design.row().grid(bairroLabel).add(bairroField).add(cidadeLabel)
					.add(cidadeField);
			design.row().grid(ufLabel).add(listaUFCombo).add(cepLabel)
					.add(cepField);
			design.row().grid(emailLabel).add(emailField);
		}

		design.row().grid(dispositivoLabel).add(dispositivoField)
				.add(serialLabel).add(serialField);
		design.row().grid(macadressLabel).add(macadressField).add(ipLabel)
		.add(ipField);
		design.row().grid(new JLabel()).add(new JLabel()).add(new JLabel())
		.add(new JLabel("(*) Campos obrigatórios"));

		getContentPane().setLayout(new BorderLayout());

		JPanel pnlBotoes = new JPanel(new GridLayout(1, 2));
		pnlBotoes.add(btnRegistrar);
		pnlBotoes.add(btnCancelar);

		getContentPane().add(lblMensagens, BorderLayout.NORTH);
		getContentPane().add(new JScrollPane(pnl), BorderLayout.CENTER);
		getContentPane().add(pnlBotoes, BorderLayout.SOUTH);
	}


	private void configuracoesBasicas() {
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				System.exit(0);
				dispose();
			}
		});

		lblMensagens.setForeground(Color.red);
		ipField.setEditable(false);
		macadressField.setEditable(false);
		serialField.setEditable(false);

		eAssociadoBg.add(simAssociadoRb);
		eAssociadoBg.add(naoAssociadoRb);

		cpfCnpjBg.add(cpfRb);
		cpfCnpjBg.add(cnpjRb);

		cpfRb.setSelected(true);

		simAssociadoRb.addActionListener(actionAssociado);
		naoAssociadoRb.addActionListener(actionAssociado);

		cpfRb.addActionListener(actionCpfCnpj);
		cnpjRb.addActionListener(actionCpfCnpj);

		if(eAssociado)
			simAssociadoRb.setSelected(true);
		else
			naoAssociadoRb.setSelected(true);
		
		documentoField.setSize(500, (int)(documentoField.getSize().getHeight()));

		setMascaraCpfCnpj(CPF);
	}

	/**
	 * 
	 * @param qualComponente
	 *            JanelaRegistro.CPF OU JanelaRegistro.CNPJ
	 */
	private void setMascaraCpfCnpj(int qualComponente) {
		try {
			documentoField.setValue(null);
			if (qualComponente == CPF) {
				documentoField
						.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(
								new javax.swing.text.MaskFormatter(
										"###.###.###-##")));
			}
			if (qualComponente == CNPJ) {
				documentoField
						.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(
								new javax.swing.text.MaskFormatter(
										"##.###.###/####-##")));
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	private void centralizaDialog() {
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (screen.width - getWidth()) / 2;
		int y = (screen.height - getHeight()) / 2;
		setLocation(x, y);
	}

	public JanelaRegistro getInstance() {
		return this;
	}
}