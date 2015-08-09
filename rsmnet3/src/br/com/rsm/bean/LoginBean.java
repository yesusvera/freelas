package br.com.rsm.bean;

import java.sql.SQLException;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import br.com.rsm.model.User;
import br.com.rsm.service.UserService;

@ManagedBean(name="loginBean")
@SessionScoped
public class LoginBean extends BaseRSMBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8377256561601676217L;
	private User userAuth = new User();
	private User user = new User();
	
//  Nao pode, ficara cíclico
//	@ManagedProperty(value = "#{cadastroBean}")  
//	private CadastroBean cadastroBean;
//	
	public String efetuarLogin() {
		String retorno = null;
		UserService userService = new UserService();
		
		//User userAuth = null;
		
//		if(this.user.getLogin() == null || 
//		  this.user.getPass() == null || 
//		  this.user.getLogin().equals("") ||
//		  this.user.getPass().equals("")){
//			message("Digite seu login e sua senha", FacesMessage.SEVERITY_INFO);
//			//message("Digite seu login e sua senha");
//			retorno = "pretty:home";
//		}
		
		try {
			userAuth = userService.findByLoginAndPass(this.user);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(userAuth.getId() == null){ 
			this.user = new User();
			this.userAuth = new User();
			message("Login ou senha inválida!");
//			System.out.println("Login ou senha inválida!");
//			retorno = "pretty:home";
			retorno = "";
		} else { //conta existe
			if(userAuth.getAccount() != null) {
				if(userAuth.getAccount().getStatus().equals("C")||userAuth.getAccount().getStatus().equals("I")){
					retorno =  "pretty:bemvindo";
				} else if(userAuth.getAccount().getStatus().equals("A")) {
					retorno =  "pretty:dashboard";
				} else if(userAuth.getAccount().getStatus().equals("S")){
					message("Conta suspensa. Procure o suporte!");
					return "";
				} 
			} else { //not found
				message("Login ou senha inválida!");
//				System.out.println("Login ou senha inválida!");
//				retorno = "pretty:home";
				retorno = "";
			}
		}
//		System.out.println("retorno: "+retorno);
		return retorno;
	}
	
	public void efetuarLogin(User user) {
		this.user = user;
		this.efetuarLogin();
	}
	
	public String efetuarLogout() {
//		System.out.println("Logout");
		this.userAuth = null;
		
		HttpServletRequest request = ((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest());
		request.getSession().invalidate();
		
		return "pretty:home";
	}

	
	public User getUserAuth() {
		return userAuth;
	}


	public void setUserAuth(User userAuth) {
		this.userAuth = userAuth;
	}

	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public boolean isLogged(){
		if (userAuth != null && userAuth.getId() != null){
			return true;
		} else {
			return false;
		}
			
	}

//	public LoginBean getLoginBean() {
//		return loginBean;
//	}
//
//	public void setLoginBean(LoginBean loginBean) {
//		this.loginBean = loginBean;
//	}
	
	

	
}
