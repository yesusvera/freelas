package br.com.rsm.bean;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import br.com.rsm.model.Account;
import br.com.rsm.model.Notification;
import br.com.rsm.model.User;
import br.com.rsm.service.AccountService;
import br.com.rsm.service.MensagemService;
import br.com.rsm.service.NotificationService;
import br.com.rsm.service.UserService;

@ManagedBean(name="cadastroBean")
@SessionScoped
public class CadastroBean extends BaseRSMBean {
	/**
	 * 	
	 */
	private static final long serialVersionUID = 1L;
	String validation = "";
	
	@ManagedProperty(value = "#{loginBean}")  
	private LoginBean loginBean; 
	User user = new User();
	User userAuth = new User();
	
	
	private AccountService accountService = new AccountService();
	private MensagemService messageService = new MensagemService();
	private NotificationService notificationService = new NotificationService();
	private UserService userService = new UserService();
	private int progresso;
	private String barraDeProgresso;
	
	
	public CadastroBean(){}

	
	public User getUser() {
		return user;
	}



	public void setUser(User user) {
		this.user = user;
	}


	

	public User getUserAuth() {
		return userAuth;
	}


	public void setUserAuth(User userAuth) {
		this.userAuth = userAuth;
	}


	public String iniciarCadastro() throws ClassNotFoundException, SQLException {
		this.setProgresso(25);
		if (user != null && user.getAccount().getParentId() != null) {
			boolean idValidParentId = accountService.verificarIdIndicacao(user.getAccount().getParentId());
			if (!idValidParentId) {
//				System.out.println("ParentId not valid!");
				message("Id de indicação inválido", FacesMessage.SEVERITY_ERROR);
//				return "pretty:home";
				return "";
			}
			return "pretty:cadastro";
		}
		return "";
	}

	public boolean isTabActive(int tab){
		return getProgresso() / 25 == tab;
	}
	
	public String getEhAtivo(String indiceASerVerificado) {
		StringBuffer result = new StringBuffer();
		result.append("step ");
		if (this.getProgresso() / 25 == Integer.parseInt(indiceASerVerificado)) {
			result.append("active");
		}

		return result.toString();

	}

	public String getStepEhAtivo(String indiceASerVerificado) {
		StringBuffer result = new StringBuffer();
		result.append("step ");
		if (this.getProgresso() / 25 == Integer.parseInt(indiceASerVerificado)) {
			result.append("active");
		}

		return result.toString();
	}

	public String getBarraDeProgresso() {
		this.barraDeProgresso = "width:" + this.getProgresso() + "%";
//		System.out.println("Barra de Progresso:"+barraDeProgresso);
		return barraDeProgresso;
	}

	public void setBarraDeProgresso(String barraDeProgresso) {
		this.barraDeProgresso = barraDeProgresso;
	}

	public String getExibeVoltar() {
		String result = "false";
		if (this.getProgresso() / 25 == 1) {
			result = "false";
		} else
			result = "true";

		return result;
	}

	public String getExibeAvancar() {
		String result = "false";
		if (this.getProgresso() / 25 == 4) {
			result = "false";
		} else
			result = "true";

		return result;
	}

	public String getExibeFinalizar() {
		String result = "false";
		if (this.getProgresso() / 25 == 4) {
			result = "true";
		} else
			result = "false";

		return result;

	}

	public String getEnableAvancar() {
		String result = "true";
		//se finalizou o carregamento da tela, 
		
			result = "true";
		//se nao 
			result = "false";
		
//		
//		if (this.getProgresso() / 25 == 4) {
//			result = "false";
//		} else
//			result = "true";

		return result;
	}
	
	
	public void goToStep(String step) {
//		System.out.println("go to step: " + step);
		int stepValue = Integer.parseInt(step);
		this.setProgresso(stepValue * 25);
	}

	public void voltar() {
		if (this.getProgresso() == 50 || this.getProgresso() == 75 || this.getProgresso() == 100) {
			this.setProgresso(this.getProgresso() - 25);
		}
	}

	public void avancar() {
//		System.out.println("Avançar");
		if (this.getProgresso() == 25 || this.getProgresso() == 50 || this.getProgresso() == 75) {
			this.setProgresso(this.getProgresso() + 25);
		}
	}

	
	public String finalizar(){
				
		try {
			this.user = accountService.saveAccountAndUser(this.user);
			//depois de salvar a conta e o user, executar um update na conta para colocar o account.userId
			accountService.updateUserIdAndParentId(this.user);
			messageService.enviarMensagem("patrick.nascimento@gmail.com", user.getAccount().getEmail(), "[RSM] Bem-vindo à RSM Network", gerarMensagemBemvindo());
			notificationService.save(new Notification("Bem vindo à RSM", "Bla bla bla", user.getAccount().getId()));
			loginBean.efetuarLogin(this.user);
			this.user = new User();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "pretty:bemvindo";
		
	}
	
	

	//generating a timestamp
	private String getTimestamp(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss.SSSSSS");
		return sdf.format(new Date());
	}

	
	private String gerarMensagemBemvindo() {

		StringBuffer mensagem = new StringBuffer();
		mensagem.append("<html><body>");
		
		
		mensagem.append("<h1>Bem vindo à Família RSM!</h1>");
		mensagem.append("NOTA: Lembre-se de realizar o pagamento para ativar sua conta antes de cadastrar seu primeiro parceiro.");
		mensagem.append("<h3>Informações de Cadastro</h3>");
		mensagem.append("Nome: ").append(user.getAccount().getFullname()).append(" <br />");
		mensagem.append("Email: ").append(user.getAccount().getEmail()).append(" <br />");
		mensagem.append("CPF: ").append(user.getAccount().getCpf()).append(" <br />");
		mensagem.append("Seu ID: ").append(user.getAccount().getUserId()).append(" <br />");
		mensagem.append("<br />").append(" <br />");
		mensagem.append("Clique <a href='http://localhost:8888/'>aqui</a> e efetue login em sua conta para acessar o link de pagamento! ");
		mensagem.append("</body></html>");

		return mensagem.toString();
	}


		
	
	

//
//	public List<String> getCityList() {
//		return cityList;
//	}
//
//
//	public void setCityList(List<String> cityList) {
//		this.cityList = cityList;
//	}


	public LoginBean getLoginBean() {
		return loginBean;
	}


	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}
	
	

	public String saveChangePass(){
		try {
			
			//loginBean.
			this.userAuth = userService.findByUser(loginBean.getUserAuth());
			
			
			if(this.userAuth.getPass().equals(user.getPass())){
				
				if(user.getNewPass().equalsIgnoreCase(user.getPassConfirmation())){
					String newPass = user.getNewPass();
					user = loginBean.getUserAuth();
					user.setPass(newPass);
					User result = accountService.saveChangePass(user);
					if(result != null){
						loginBean.getUserAuth().setPass(result.getPass());
						this.user = new User();
						message(null, "Dados de Acesso alterados com sucesso");
						messageService.enviarMensagem("patrick.nascimento@gmail.com", "patrick.nascimento@gmail.com", "[RSM] Você acabou de alterar sua senha", gerarMensagemSenhaAlterada());
						loginBean.efetuarLogin(this.user);
					} else {
						message(null, "Erro ao alterar Dados de Acesso");
					}
					
					if(result != null){
						//loginbean.setUserAuth(this.user);
						//loginBean.getUserAuth().setPass(result.getPass());
						this.user = new User();
						message(null, "Dados Pessoais alterados com sucesso");
						//messageService.enviarMensagem("patrick.nascimento@gmail.com", "patrick.nascimento@gmail.com", "[RSM] Você acabou de alterar sua senha", gerarMensagemSenhaAlterada());
//						loginBean.efetuarLogin(this.user);
					} else {
						message(null, "Erro ao alterar Dados Pessoais", FacesMessage.SEVERITY_ERROR);
					}
					
					
				} else {
					message("Nova senha não coincide com a confirmação");
				}
			} else {
				message("Senha incorreta");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		//return "pretty:bemvindo";
		return "pretty:dashboard";
		
	}

	

	public String saveChangePersonalInfo(User user) {
		try {
			
			//loginBean.
			//this.userAuth = userService.findByUser(loginBean.getUserAuth());
			
			
			//if(this.userAuth.getPass().equals(user.getPass())){
				
				//if(user.getNewPass().equalsIgnoreCase(user.getPassConfirmation())){
				//	String newPass = user.getNewPass();
					//user = loginBean.getUserAuth();
					//user.setPass(newPass);
					Account result = accountService.saveChangePersonalInfo(user);
					//if(result != null && result.getId() != null){
						//loginbean.setUserAuth(this.user);
						//loginBean.getUserAuth().setPass(result.getPass());
						loginBean.getUserAuth().setAccount(result);
						this.user = new User();
						message(null, "Dados Pessoais alterados com sucesso");
						//messageService.enviarMensagem("patrick.nascimento@gmail.com", "patrick.nascimento@gmail.com", "[RSM] Você acabou de alterar sua senha", gerarMensagemSenhaAlterada());
//						loginBean.efetuarLogin(this.user);
//					} else {
//						message(null, "Erro ao alterar Dados Pessoais", FacesMessage.SEVERITY_ERROR);
//					}
//					
					
//				} else {
//					System.out.println("Nova senha não coincide com a confirmação");
//				}
//			} else {
//				System.out.println("Senha incorreta");
//			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		//return "pretty:bemvindo";
		return "pretty:dashboard";
		
	}

	
	private String gerarMensagemSenhaAlterada() {

		StringBuffer mensagem = new StringBuffer();
		mensagem.append("<html><body>");
		mensagem.append("<h1>Você acabou de alterar sua senha!</h1>");
		mensagem.append(""+this.user.getAccount().getFullname()+", sua senha na plataforma RSM acaba de ser alterada. Caso não tenha sido você quem alterou a senha, entre em contato conosco imediatamente pelo número 0800-892-1555.");
		mensagem.append("</body></html>");

		return mensagem.toString();
	}
	
	public String saveChangePersonalInfo(){
		
	//	try {
			//this.user = accountService.updateAccountAndUser(this.user);
			//messageService.enviarMensagem("patrick.nascimento@gmail.com", "patrick.nascimento@gmail.com", "[RSM] Você acabou de alterar informações de sua conta", gerarMensagemDadosDaContaAlterados());
			loginBean.efetuarLogin(this.user);
			this.user = new User();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
		return "pretty:bemvindo";
		
	}

	
	
	private String gerarMensagemDadosDaContaAlterados() {
		
		StringBuffer mensagem = new StringBuffer();
		mensagem.append("<html><body>");
		mensagem.append("<h1>Você acabou de alterar informações de sua conta!</h1>");
		mensagem.append(""+this.user.getAccount().getFullname()+", informações de sua conta na plataforma RSM acabam de ser alteradas. Caso não tenha sido você quem as alterou, entre em contato conosco imediatamente pelo número 0800-892-1555.");
		mensagem.append("</body></html>");
		
		return mensagem.toString();
	}
	
	


	public int getProgresso() {
		return progresso;
	}


	public void setProgresso(int progresso) {
		this.progresso = progresso;
	}


	public void saveChangeContactInfo(User user) {
		Account result = new Account();
		try {
			result = accountService.saveChangeContactInfo(user);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		loginBean.getUserAuth().setAccount(result);
		this.user = new User();
		message(null, "Dados de Contato alterados com sucesso");
		
	}

	
	
	
	

	
}
