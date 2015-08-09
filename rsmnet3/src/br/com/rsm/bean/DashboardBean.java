package br.com.rsm.bean;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import br.com.rsm.enumeradores.EnumEstado;
import br.com.rsm.enumeradores.EnumImg;
import br.com.rsm.enumeradores.EnumOperadoras;
import br.com.rsm.enumeradores.EnumSupportSubject;
import br.com.rsm.model.Account;
import br.com.rsm.model.Carrier;
import br.com.rsm.model.City;
import br.com.rsm.model.Notification;
import br.com.rsm.model.Sanction;
import br.com.rsm.model.State;
import br.com.rsm.model.SupportMessage;
import br.com.rsm.model.User;
import br.com.rsm.service.AccountService;
import br.com.rsm.service.DirectionService;
import br.com.rsm.service.MensagemService;
import br.com.rsm.service.NotificationService;
import br.com.rsm.service.SanctionService;
import br.com.rsm.service.UserService;


@ManagedBean(name = "dashboardBean")
@SessionScoped
public class DashboardBean extends BaseRSMBean{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String validation = "";
	transient User user = new User();
	private String activeTab;
	private int activeSubTab1;
	private User userAuth = new User();
	private List<Notification> notifications;
	private List<EnumSupportSubject> subjectList = new ArrayList<EnumSupportSubject>();
	private SupportMessage supportMessage = new SupportMessage();

	private UserService userService = new UserService();
	private AccountService accountService = new AccountService();
	private SanctionService sanctionService = new SanctionService();
	private MensagemService messageService = new MensagemService();
	private NotificationService notificationService = new NotificationService();
	private DirectionService directionService = new DirectionService();
	
	private List<Carrier> carrierList = new ArrayList<Carrier>();
	private List<State> stateList = new ArrayList<State>();
	private List<City> cityList = new ArrayList<City>();
	
	private long totalRede;
	private long totalBracoDir = 0L;
	private long totalBracoEsq = 0L;
	
	private String userIdPesquisa;
	
	private Sanction sanctionSuspend = new Sanction();
	private Sanction sanctionActivate = new Sanction();
	
	private String userIdSuspend;
	private String userIdAtivar;

	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	@ManagedProperty(value = "#{cadastroBean}")
	private CadastroBean cadastroBean;

	@ManagedProperty(value = "#{teste}")
	private TesteBean testeBean;

	private Account rootNode = new Account(); // this.loginBean.getUserAuth().getAccount();

	private Account tree = new Account();
	
	private List<SelectItem> itensConfigNetwork = new ArrayList<>();
	
	
	private String selectConfigNetworkValue = "";

	public DashboardBean() throws SQLException {
//		System.out.println("Constructor");
		
		itensConfigNetwork.add(new SelectItem("A","Menor braço"));
		itensConfigNetwork.add(new SelectItem("D","Braço direito"));
		itensConfigNetwork.add(new SelectItem("E","Braço esquerdo"));
		
		this.activeTab = "notifications";
		this.activeSubTab1 = 1;
	}

	@PostConstruct
	public void init() throws SQLException {
//		System.out.println("Post Constructor");
		notifications = new ArrayList<Notification>();
		popularLista(5);
		this.userAuth = loginBean.getUserAuth();
		this.user = loginBean.getUser();
	}

	public void setTesteBean(TesteBean testeBean) {
		this.testeBean = testeBean;
	}

	
	/**
	 * @author yesus
	 * @return
	 */
	public int getIndicadosDiretos(){
//		Account contaLogada = this.loginBean.getUserAuth().getAccount();
		return accountService.getTotalIndicadosDiretos(tree.getUserId());
	}
	
	/**
	 * Este metodo sera chamando quando o usuario chegar no fim da rolagem
	 * 
	 * @throws SQLException
	 */
	public void carregarMais() throws SQLException {
		popularLista(5);
		notificationService.paginate(0L, 10, 36L);
	}

	/**
	 * Criei este método para popular a lista, simulando informações vindas de
	 * um banco.
	 * 
	 * @param qtd
	 * @throws SQLException
	 */
	public void popularLista(int qtd) throws SQLException {
		setNotifications(notificationService.paginate(0L, qtd, loginBean
				.getUserAuth().getAccount().getId()));
	}

//	public String saveUser() {
//		try {
//			User result = accountService.saveChangePass(getUserAuth());
//			if (result != null) {
//				setUser(result);
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		// return "pretty:bemvindo";
//		return "pretty:dashboard";
//
//	}

	public void saveChangePass(){
		try {
//            System.out.println("Save Change Pass!!!");
			// loginBean.
			setUserAuth(userService.findByUser(loginBean.getUserAuth()));

			if (getUserAuth() != null
					&& getUserAuth().getPass().equals(user.getPass())) {

				if (user.getNewPass().equals(user.getPassConfirmation())) {
					String newPass = user.getNewPass();
					user.setPass(newPass);
					user.setId(getUserAuth().getId());
					User result = accountService.saveChangePass(user);
					if (result != null) {
						loginBean.getUserAuth().setPass(result.getPass());
						setUser(result);
						message("Senha alterada com sucesso.");
//						System.out
//								.println("Senha alterada com sucesso.");
					}
				} else {
					message("Nova senha não coincide com a confirmação.");
//					System.out
//							.println("Nova senha não coincide com a confirmação");
				}
			} else {
				message("Senha incorreta.");
//				System.out.println("Senha incorreta");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
//
//		goToAccount(3);

			//this.user = new User();
//		return "pretty:dashboard";

	}

	// getters and setters

	public List<Notification> getNotifications() {
		return notifications;
	}

	public void setNotifications(List<Notification> notifications) {
		this.notifications = notifications;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void goToNotifications() {
		activeSubTab1 = 1; // Level 1 option - resets level 2 options
//		System.out.println("go to tab Notifications");
		this.activeTab = "notifications";
	}

	public void goToNetwork() throws SQLException, ClassNotFoundException {
		activeSubTab1 = 1; // Level 1 option - resets level 2 options
//		System.out.println("go to tab Network");
		this.rootNode = this.loginBean.getUserAuth().getAccount();
		
		totalRede = -1;
		totalBracoEsq = 0;
		totalBracoDir = 0;
		
//		this.tree = this.accountService.findById(rootNode.getId());

		// Varrer a arvore binária em pré-ordem para popular os slots de nós.
		this.tree = crawl(rootNode);

		this.activeTab = "network";
	}

	public void moveToNode(String targetNode) throws SQLException,
			ClassNotFoundException {
		
		
		activeSubTab1 = 1; // Level 1 option - resets level 2 options
//		System.out.println("move to new node under tab Network");
//		System.out.println("targetNode: " + targetNode);
		
		totalRede=-1;
		totalBracoDir=0;
		totalBracoEsq=0;
		
		Account accountFound = getAccountByTarget(targetNode);
		

		if(accountFound!=null && !accountFound.getStatus().equals("A")){
			switch (accountFound.getStatus()) {
			case "C":{
				message("Deseja ativar o usuário? Use um ticket de ativação.");
				break;
			}	
			case "I":{
				message("Deseja ativar o usuário? Use um ticket de ativação.");
				break;
			}
			case "S":{
				message("Usuário suspenso!");
				break;
			}
			default:
				break;
			}
			
		}

		// Varrer a arvore binária em pré-ordem para popular os slots de nós.
		if (accountFound != null && accountFound.getId() != null
				&& accountFound.getStatus().equals("A")
				&& accountFound.getId() != this.tree.getId()) { // apenas se há
																// uma conta
																// ATIVADA na
																// posição
																// clicada.
			this.tree = crawl(accountFound);
		}

		this.activeTab = "network";
	}
	
	public void moveToNodeBySearchID() throws SQLException,
	ClassNotFoundException {
		activeSubTab1 = 1; // Level 1 option - resets level 2 options
		
		if(userIdPesquisa!=null) userIdPesquisa = userIdPesquisa.replaceAll("#", "");
		
		
		Account accountFound = null;
		if(userIdPesquisa!=null && userIdPesquisa.equals("")){
			accountFound = this.loginBean.getUserAuth().getAccount();
		}else{
		  accountFound = accountService.findByUserId(userIdPesquisa);
		}
		
		// this.rootNode = accountFound;
		
		// Varrer a arvore binária em pré-ordem para popular os slots de nós.
		if (accountFound != null && accountFound.getId() != null
				&& accountFound.getStatus().equals("A")
				&& accountFound.getId() != this.tree.getId()) { // apenas se há
																// uma conta
																// ATIVADA na
																// posição
																// clicada.
			
			totalRede=-1;
			totalBracoDir=0;
			totalBracoEsq=0;
			
			this.tree = crawl(accountFound);
		}else{
			if(!(userIdPesquisa!=null && userIdPesquisa.equals(""))){
				message("Pesquisa","Nenhum parceiro encontrado.");
			}
		}
		
		this.activeTab = "network";
	}
	
	/**
	 * @author Yesus Castillo
	 * @param targetNode
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public void moveToUP() throws SQLException,
		ClassNotFoundException {
		activeSubTab1 = 1; // Level 1 option - resets level 2 options
//		System.out.println("move to up");
		
		
		
		Account accountFound = accountService.findUpAccount(tree.getId());
		// this.rootNode = accountFound;
		
		// Varrer a arvore binária em pré-ordem para popular os slots de nós.
		if (accountFound != null && accountFound.getId() != null
				&& accountFound.getStatus().equals("A")
				&& accountFound.getId() != this.tree.getId()) { // apenas se há
																// uma conta
																// ATIVADA na
																// posição
																// clicada.
			totalRede=-1;
			totalBracoDir=0;
			totalBracoEsq=0;
			this.tree = crawl(accountFound);
		}
		
		this.activeTab = "network";
	}

	public void moveToRootNode() throws ClassNotFoundException, SQLException {
		activeSubTab1 = 1; // Level 1 option - resets level 2 options
//		System.out.println("move to root node under tab Network");
//		System.out.println("rootNode1: "
//				+ this.loginBean.getUserAuth().getAccount());
//		System.out.println("rootNode2: " + rootNode);

		// Varrer a arvore binária em pré-ordem para popular os slots de nós.
		this.tree = crawl(rootNode);

		this.activeTab = "network";
	}

	private Account getAccountByTarget(String targetNode) {
//		System.out.println("targetNode: " + targetNode);
		String[] coordinatesToTarget = targetNode.split("_");
		Account cursor = this.tree;
		for (int i = 0; i < coordinatesToTarget.length; i++) {
			if (coordinatesToTarget[i].equals("1") && cursor != null
					&& cursor.getId() != null && cursor.getLeftSide() != null) {
				cursor = cursor.getLeftSide();
//				System.out.println("moving cursor left got " + cursor.getId());
			} else if (coordinatesToTarget[i].equals("2") && cursor != null
					&& cursor.getRightSide() != null) {
				cursor = cursor.getRightSide();
//				System.out.println("moving cursor right got " + cursor.getId());
			}
		}

		return cursor;
	}

	private Account crawl(Account node, char ... refT) throws SQLException,
			ClassNotFoundException {
		if (node != null) {
//			System.out.print(node.getId() + " ");
			node = this.accountService.findById(node.getId());
//			System.out.println(node.getFullname());
			
			totalRede ++;
			
			if(refT!=null && refT.length>0){
				if(refT[0] == 'D'){
					totalBracoDir++;
				}else{
					totalBracoEsq++;
				}
			}
			
			if (node != null) {	
//				if(node.getLeftSide() !=null ) totalBraco	Esq++;
//				if(node.getRightSide() !=null ) totalBracoDir++;
					if(totalRede>=1){
						node.setLeftSide(crawl(node.getLeftSide(), refT ));
						node.setRightSide(crawl(node.getRightSide(), refT));
					}else{
						node.setLeftSide(crawl(node.getLeftSide(), 'E' ));
						node.setRightSide(crawl(node.getRightSide(), 'D'));
					}
			}
		}
		return node;
	}
	

	public String getLabelUserIdNode(String nodePosition) {
		String retorno = "";

		if (this.rootNode != null && this.rootNode.getUserId() != null) {

			if (nodePosition.equals("0")) { // se for ROOT
				if (this.tree != null) { // active
					retorno = new StringBuilder(this.tree.getUserId())
							.toString();
				}
			} else if (nodePosition.equals("1")) { // se for 1
				if (this.tree.getLeftSide() != null) { // not empty
					retorno = new StringBuilder(this.tree.getLeftSide()
							.getUserId())
							.toString();
				}

			} else if (nodePosition.equals("1_1")) { // se for 1_1

				if (this.tree.getLeftSide() != null
						&& this.tree.getLeftSide().getLeftSide() != null) { // not
																			// empty
					retorno = new StringBuilder(this.tree.getLeftSide()
							.getLeftSide().getUserId())
							.toString();
				}

			} else if (nodePosition.equals("1_1_1")) { // se for 1_1_1

				if (this.tree.getLeftSide() != null
						&& this.tree.getLeftSide().getLeftSide() != null
						&& this.tree.getLeftSide().getLeftSide().getLeftSide() != null) { // not
																							// empty
					retorno = new StringBuilder(this.tree.getLeftSide()
							.getLeftSide().getLeftSide().getUserId())
							.toString();
				}

			} else if (nodePosition.equals("1_1_1_1")) { // se for 1_1_1_1

				if (this.tree.getLeftSide() != null
						&& this.tree.getLeftSide().getLeftSide() != null
						&& this.tree.getLeftSide().getLeftSide().getLeftSide() != null
						&& this.tree.getLeftSide().getLeftSide().getLeftSide()
								.getLeftSide() != null) { // not empty
					retorno = new StringBuilder(this.tree.getLeftSide()
							.getLeftSide().getLeftSide().getLeftSide()
							.getUserId())
							.toString();
				}

			} else if (nodePosition.equals("1_1_1_2")) { // se for 1_1_1_2

				if (this.tree.getLeftSide() != null
						&& this.tree.getLeftSide().getLeftSide() != null
						&& this.tree.getLeftSide().getLeftSide().getLeftSide() != null
						&& this.tree.getLeftSide().getLeftSide().getLeftSide()
								.getRightSide() != null) { // not empty
					retorno = new StringBuilder(this.tree.getLeftSide()
							.getLeftSide().getLeftSide().getRightSide()
							.getUserId())
							.toString();
				}

			} else if (nodePosition.equals("1_1_2")) { // se for 1_1_2

				if (this.tree.getLeftSide() != null
						&& this.tree.getLeftSide().getLeftSide() != null
						&& this.tree.getLeftSide().getLeftSide().getRightSide() != null) { // not
																							// empty
					retorno = new StringBuilder(this.tree.getLeftSide()
							.getLeftSide().getRightSide().getUserId())
							.toString();
				}

			} else if (nodePosition.equals("1_1_2_1")) { // se for 1_1_2_1

				if (this.tree.getLeftSide() != null
						&& this.tree.getLeftSide().getLeftSide() != null
						&& this.tree.getLeftSide().getLeftSide().getRightSide() != null
						&& this.tree.getLeftSide().getLeftSide().getRightSide()
								.getLeftSide() != null) { // not empty
					retorno = new StringBuilder(this.tree.getLeftSide()
							.getLeftSide().getRightSide().getLeftSide()
							.getUserId())
							.toString();
				}

			} else if (nodePosition.equals("1_1_2_2")) { // se for 1_1_2_2

				if (this.tree.getLeftSide() != null
						&& this.tree.getLeftSide().getLeftSide() != null
						&& this.tree.getLeftSide().getLeftSide().getRightSide() != null
						&& this.tree.getLeftSide().getLeftSide().getRightSide()
								.getRightSide() != null) { // not empty
					retorno = new StringBuilder(this.tree.getLeftSide()
							.getLeftSide().getRightSide().getRightSide()
							.getUserId())
							.toString();
				}

			} else if (nodePosition.equals("1_2")) { // se for 1_2

				if (this.tree.getLeftSide() != null
						&& this.tree.getLeftSide().getRightSide() != null) { // not
																				// empty
					retorno = new StringBuilder(this.tree.getLeftSide()
							.getRightSide().getUserId())
							.toString();
				}

			} else if (nodePosition.equals("1_2_1")) { // se for 1_2_1

				if (this.tree.getLeftSide() != null
						&& this.tree.getLeftSide().getRightSide() != null
						&& this.tree.getLeftSide().getRightSide().getLeftSide() != null) { // not
																							// empty
					retorno = new StringBuilder(this.tree.getLeftSide()
							.getRightSide().getLeftSide().getUserId())
							.toString();
				}
			} else if (nodePosition.equals("1_2_1_1")) { // se for 1_2_1_1

				if (this.tree.getLeftSide() != null
						&& this.tree.getLeftSide().getRightSide() != null
						&& this.tree.getLeftSide().getRightSide().getLeftSide() != null
						&& this.tree.getLeftSide().getRightSide().getLeftSide()
								.getLeftSide() != null) { // not empty
					retorno = new StringBuilder(this.tree.getLeftSide()
							.getRightSide().getLeftSide().getLeftSide()
							.getUserId())
							.toString();
				}
			} else if (nodePosition.equals("1_2_1_2")) { // se for 1_2_1_2

				if (this.tree.getLeftSide() != null
						&& this.tree.getLeftSide().getRightSide() != null
						&& this.tree.getLeftSide().getRightSide().getLeftSide() != null
						&& this.tree.getLeftSide().getRightSide().getLeftSide()
								.getRightSide() != null) { // not empty
					retorno = new StringBuilder(this.tree.getLeftSide()
							.getRightSide().getLeftSide().getRightSide()
							.getUserId())
							.toString();
				}
			} else if (nodePosition.equals("1_2_2")) { // se for 1_2_2

				if (this.tree.getLeftSide() != null
						&& this.tree.getLeftSide().getRightSide() != null
						&& this.tree.getLeftSide().getRightSide()
								.getRightSide() != null) { // not empty
					retorno = new StringBuilder(this.tree.getLeftSide()
							.getRightSide().getRightSide().getUserId())
							.toString();
				}
			} else if (nodePosition.equals("1_2_2_1")) { // se for 1_2_2_1

				if (this.tree.getLeftSide() != null
						&& this.tree.getLeftSide().getRightSide() != null
						&& this.tree.getLeftSide().getRightSide()
								.getRightSide() != null
						&& this.tree.getLeftSide().getRightSide()
								.getRightSide().getLeftSide() != null) { // not
																			// empty
					retorno = new StringBuilder(this.tree.getLeftSide()
							.getRightSide().getRightSide().getLeftSide()
							.getUserId())
							.toString();
				}
			} else if (nodePosition.equals("1_2_2_2")) { // se for 1_2_2_2

				if (this.tree.getLeftSide() != null
						&& this.tree.getLeftSide().getRightSide() != null
						&& this.tree.getLeftSide().getRightSide()
								.getRightSide() != null
						&& this.tree.getLeftSide().getRightSide()
								.getRightSide().getRightSide() != null) { // not
																			// empty
					retorno = new StringBuilder(this.tree.getLeftSide()
							.getRightSide().getRightSide().getRightSide()
							.getUserId())
							.toString();

				}
			} else if (nodePosition.equals("2")) { // se for 2
				if (this.tree.getRightSide() != null) { // not empty
					retorno = new StringBuilder(this.tree.getRightSide()
							.getUserId())
							.toString();
				}

			} else if (nodePosition.equals("2_1")) { // se for 2_1

				if (this.tree.getRightSide() != null
						&& this.tree.getRightSide().getLeftSide() != null) { // not
																				// empty
					retorno = new StringBuilder(this.tree.getRightSide()
							.getLeftSide().getUserId())
							.toString();
				}

			} else if (nodePosition.equals("2_1_1")) { // se for 2_1_1

				if (this.tree.getRightSide() != null
						&& this.tree.getRightSide().getLeftSide() != null
						&& this.tree.getRightSide().getLeftSide().getLeftSide() != null) { // not
																							// empty
					retorno = new StringBuilder(this.tree.getRightSide()
							.getLeftSide().getLeftSide().getUserId())
							.toString();
				}

			} else if (nodePosition.equals("2_1_1_1")) { // se for 2_1_1_1

				if (this.tree.getRightSide() != null
						&& this.tree.getRightSide().getLeftSide() != null
						&& this.tree.getRightSide().getLeftSide().getLeftSide() != null
						&& this.tree.getRightSide().getLeftSide().getLeftSide()
								.getLeftSide() != null) { // not empty
					retorno = new StringBuilder(this.tree.getRightSide()
							.getLeftSide().getLeftSide().getLeftSide()
							.getUserId())
							.toString();
				}

			} else if (nodePosition.equals("2_1_1_2")) { // se for 2_1_1_2

				if (this.tree.getRightSide() != null
						&& this.tree.getRightSide().getLeftSide() != null
						&& this.tree.getRightSide().getLeftSide().getLeftSide() != null
						&& this.tree.getRightSide().getLeftSide().getLeftSide()
								.getRightSide() != null) { // not empty
					retorno = new StringBuilder(this.tree.getRightSide()
							.getLeftSide().getLeftSide().getRightSide()
							.getUserId())
							.toString();
				}

			} else if (nodePosition.equals("2_1_2")) { // se for 2_1_2

				if (this.tree.getRightSide() != null
						&& this.tree.getRightSide().getLeftSide() != null
						&& this.tree.getRightSide().getLeftSide()
								.getRightSide() != null) { // not empty
					retorno = new StringBuilder(this.tree.getRightSide()
							.getLeftSide().getRightSide().getUserId())
							.toString();
				}

			} else if (nodePosition.equals("2_1_2_1")) { // se for 2_1_2_1

				if (this.tree.getRightSide() != null
						&& this.tree.getRightSide().getLeftSide() != null
						&& this.tree.getRightSide().getLeftSide()
								.getRightSide() != null
						&& this.tree.getRightSide().getLeftSide()
								.getRightSide().getLeftSide() != null) { // not
																			// empty
					retorno = new StringBuilder(this.tree.getRightSide()
							.getLeftSide().getRightSide().getLeftSide()
							.getUserId())
							.toString();
				}

			} else if (nodePosition.equals("2_1_2_2")) { // se for 2_1_2_2

				if (this.tree.getRightSide() != null
						&& this.tree.getRightSide().getLeftSide() != null
						&& this.tree.getRightSide().getLeftSide()
								.getRightSide() != null
						&& this.tree.getRightSide().getLeftSide()
								.getRightSide().getRightSide() != null) { // not
																			// empty
					retorno = new StringBuilder(this.tree.getRightSide()
							.getLeftSide().getRightSide().getRightSide()
							.getUserId())
							.toString();
				}

			} else if (nodePosition.equals("2_2")) { // se for 2_2

				if (this.tree.getRightSide() != null
						&& this.tree.getRightSide().getRightSide() != null) { // not
																				// empty
					retorno = new StringBuilder(this.tree.getRightSide()
							.getRightSide().getUserId())
							.toString();
				}

			} else if (nodePosition.equals("2_2_1")) { // se for 2_2_1

				if (this.tree.getRightSide() != null
						&& this.tree.getRightSide().getRightSide() != null
						&& this.tree.getRightSide().getRightSide()
								.getLeftSide() != null) { // not empty
					retorno = new StringBuilder(this.tree.getRightSide()
							.getRightSide().getLeftSide().getUserId())
							.toString();
				}
			} else if (nodePosition.equals("2_2_1_1")) { // se for 2_2_1_1

				if (this.tree.getRightSide() != null
						&& this.tree.getRightSide().getRightSide() != null
						&& this.tree.getRightSide().getRightSide()
								.getLeftSide() != null
						&& this.tree.getRightSide().getRightSide()
								.getLeftSide().getLeftSide() != null) { // not
																		// empty
					retorno = new StringBuilder(this.tree.getRightSide()
							.getRightSide().getLeftSide().getLeftSide()
							.getUserId())
							.toString();
				}
			} else if (nodePosition.equals("2_2_1_2")) { // se for 2_2_1_2

				if (this.tree.getRightSide() != null
						&& this.tree.getRightSide().getRightSide() != null
						&& this.tree.getRightSide().getRightSide()
								.getLeftSide() != null
						&& this.tree.getRightSide().getRightSide()
								.getLeftSide().getRightSide() != null) { // not
																			// empty
					retorno = new StringBuilder(this.tree.getRightSide()
							.getRightSide().getLeftSide().getRightSide()
							.getUserId())
							.toString();
				}
			} else if (nodePosition.equals("2_2_2")) { // se for 2_2_2

				if (this.tree.getRightSide() != null
						&& this.tree.getRightSide().getRightSide() != null
						&& this.tree.getRightSide().getRightSide()
								.getRightSide() != null) { // not empty
					retorno = new StringBuilder(this.tree.getRightSide()
							.getRightSide().getRightSide().getUserId())
							.toString();
				}
			} else if (nodePosition.equals("2_2_2_1")) { // se for 2_2_2_1

				if (this.tree.getRightSide() != null
						&& this.tree.getRightSide().getRightSide() != null
						&& this.tree.getRightSide().getRightSide()
								.getRightSide() != null
						&& this.tree.getRightSide().getRightSide()
								.getRightSide().getLeftSide() != null) { // not
																			// empty
					retorno = new StringBuilder(this.tree.getRightSide()
							.getRightSide().getRightSide().getLeftSide()
							.getUserId())
							.toString();
				}
			} else if (nodePosition.equals("2_2_2_2")) { // se for 2_2_2_2

				if (this.tree.getRightSide() != null
						&& this.tree.getRightSide().getRightSide() != null
						&& this.tree.getRightSide().getRightSide()
								.getRightSide() != null
						&& this.tree.getRightSide().getRightSide()
								.getRightSide().getRightSide() != null) { // not
																			// empty
					retorno = new StringBuilder(this.tree.getRightSide()
							.getRightSide().getRightSide().getRightSide()
							.getUserId())
							.toString();
				}
			}

		}
		if(retorno!=null && !retorno.equals("")){
			return "#" + retorno;
		}else{
			return retorno;
		}
	}
	
	
	public String getLabelAccountNode(String nodePosition) {
		String retorno = "";

		if (this.rootNode != null && this.rootNode.getUserId() != null) {

			if (nodePosition.equals("0")) { // se for ROOT
				if (this.tree != null) { // active
					retorno = new StringBuilder(this.tree.getShortName())
							.toString();
				}
			} else if (nodePosition.equals("1")) { // se for 1
				if (this.tree.getLeftSide() != null) { // not empty
					retorno = new StringBuilder(this.tree.getLeftSide()
							.getShortName())
							.toString();
				}

			} else if (nodePosition.equals("1_1")) { // se for 1_1

				if (this.tree.getLeftSide() != null
						&& this.tree.getLeftSide().getLeftSide() != null) { // not
																			// empty
					retorno = new StringBuilder(this.tree.getLeftSide()
							.getLeftSide().getShortName())
							.toString();
				}

			} else if (nodePosition.equals("1_1_1")) { // se for 1_1_1

				if (this.tree.getLeftSide() != null
						&& this.tree.getLeftSide().getLeftSide() != null
						&& this.tree.getLeftSide().getLeftSide().getLeftSide() != null) { // not
																							// empty
					retorno = new StringBuilder(this.tree.getLeftSide()
							.getLeftSide().getLeftSide().getShortName())
							.toString();
				}

			} else if (nodePosition.equals("1_1_1_1")) { // se for 1_1_1_1

				if (this.tree.getLeftSide() != null
						&& this.tree.getLeftSide().getLeftSide() != null
						&& this.tree.getLeftSide().getLeftSide().getLeftSide() != null
						&& this.tree.getLeftSide().getLeftSide().getLeftSide()
								.getLeftSide() != null) { // not empty
					retorno = new StringBuilder(this.tree.getLeftSide()
							.getLeftSide().getLeftSide().getLeftSide()
							.getShortName())
							.toString();
				}

			} else if (nodePosition.equals("1_1_1_2")) { // se for 1_1_1_2

				if (this.tree.getLeftSide() != null
						&& this.tree.getLeftSide().getLeftSide() != null
						&& this.tree.getLeftSide().getLeftSide().getLeftSide() != null
						&& this.tree.getLeftSide().getLeftSide().getLeftSide()
								.getRightSide() != null) { // not empty
					retorno = new StringBuilder(this.tree.getLeftSide()
							.getLeftSide().getLeftSide().getRightSide()
							.getShortName())
							.toString();
				}

			} else if (nodePosition.equals("1_1_2")) { // se for 1_1_2

				if (this.tree.getLeftSide() != null
						&& this.tree.getLeftSide().getLeftSide() != null
						&& this.tree.getLeftSide().getLeftSide().getRightSide() != null) { // not
																							// empty
					retorno = new StringBuilder(this.tree.getLeftSide()
							.getLeftSide().getRightSide().getShortName())
							.toString();
				}

			} else if (nodePosition.equals("1_1_2_1")) { // se for 1_1_2_1

				if (this.tree.getLeftSide() != null
						&& this.tree.getLeftSide().getLeftSide() != null
						&& this.tree.getLeftSide().getLeftSide().getRightSide() != null
						&& this.tree.getLeftSide().getLeftSide().getRightSide()
								.getLeftSide() != null) { // not empty
					retorno = new StringBuilder(this.tree.getLeftSide()
							.getLeftSide().getRightSide().getLeftSide()
							.getShortName())
							.toString();
				}

			} else if (nodePosition.equals("1_1_2_2")) { // se for 1_1_2_2

				if (this.tree.getLeftSide() != null
						&& this.tree.getLeftSide().getLeftSide() != null
						&& this.tree.getLeftSide().getLeftSide().getRightSide() != null
						&& this.tree.getLeftSide().getLeftSide().getRightSide()
								.getRightSide() != null) { // not empty
					retorno = new StringBuilder(this.tree.getLeftSide()
							.getLeftSide().getRightSide().getRightSide()
							.getShortName())
							.toString();
				}

			} else if (nodePosition.equals("1_2")) { // se for 1_2

				if (this.tree.getLeftSide() != null
						&& this.tree.getLeftSide().getRightSide() != null) { // not
																				// empty
					retorno = new StringBuilder(this.tree.getLeftSide()
							.getRightSide().getShortName())
							.toString();
				}

			} else if (nodePosition.equals("1_2_1")) { // se for 1_2_1

				if (this.tree.getLeftSide() != null
						&& this.tree.getLeftSide().getRightSide() != null
						&& this.tree.getLeftSide().getRightSide().getLeftSide() != null) { // not
																							// empty
					retorno = new StringBuilder(this.tree.getLeftSide()
							.getRightSide().getLeftSide().getShortName())
							.toString();
				}
			} else if (nodePosition.equals("1_2_1_1")) { // se for 1_2_1_1

				if (this.tree.getLeftSide() != null
						&& this.tree.getLeftSide().getRightSide() != null
						&& this.tree.getLeftSide().getRightSide().getLeftSide() != null
						&& this.tree.getLeftSide().getRightSide().getLeftSide()
								.getLeftSide() != null) { // not empty
					retorno = new StringBuilder(this.tree.getLeftSide()
							.getRightSide().getLeftSide().getLeftSide()
							.getShortName())
							.toString();
				}
			} else if (nodePosition.equals("1_2_1_2")) { // se for 1_2_1_2

				if (this.tree.getLeftSide() != null
						&& this.tree.getLeftSide().getRightSide() != null
						&& this.tree.getLeftSide().getRightSide().getLeftSide() != null
						&& this.tree.getLeftSide().getRightSide().getLeftSide()
								.getRightSide() != null) { // not empty
					retorno = new StringBuilder(this.tree.getLeftSide()
							.getRightSide().getLeftSide().getRightSide()
							.getShortName())
							.toString();
				}
			} else if (nodePosition.equals("1_2_2")) { // se for 1_2_2

				if (this.tree.getLeftSide() != null
						&& this.tree.getLeftSide().getRightSide() != null
						&& this.tree.getLeftSide().getRightSide()
								.getRightSide() != null) { // not empty
					retorno = new StringBuilder(this.tree.getLeftSide()
							.getRightSide().getRightSide().getShortName())
							.toString();
				}
			} else if (nodePosition.equals("1_2_2_1")) { // se for 1_2_2_1

				if (this.tree.getLeftSide() != null
						&& this.tree.getLeftSide().getRightSide() != null
						&& this.tree.getLeftSide().getRightSide()
								.getRightSide() != null
						&& this.tree.getLeftSide().getRightSide()
								.getRightSide().getLeftSide() != null) { // not
																			// empty
					retorno = new StringBuilder(this.tree.getLeftSide()
							.getRightSide().getRightSide().getLeftSide()
							.getShortName())
							.toString();
				}
			} else if (nodePosition.equals("1_2_2_2")) { // se for 1_2_2_2

				if (this.tree.getLeftSide() != null
						&& this.tree.getLeftSide().getRightSide() != null
						&& this.tree.getLeftSide().getRightSide()
								.getRightSide() != null
						&& this.tree.getLeftSide().getRightSide()
								.getRightSide().getRightSide() != null) { // not
																			// empty
					retorno = new StringBuilder(this.tree.getLeftSide()
							.getRightSide().getRightSide().getRightSide()
							.getShortName())
							.toString();

				}
			} else if (nodePosition.equals("2")) { // se for 2
				if (this.tree.getRightSide() != null) { // not empty
					retorno = new StringBuilder(this.tree.getRightSide()
							.getShortName())
							.toString();
				}

			} else if (nodePosition.equals("2_1")) { // se for 2_1

				if (this.tree.getRightSide() != null
						&& this.tree.getRightSide().getLeftSide() != null) { // not
																				// empty
					retorno = new StringBuilder(this.tree.getRightSide()
							.getLeftSide().getShortName())
							.toString();
				}

			} else if (nodePosition.equals("2_1_1")) { // se for 2_1_1

				if (this.tree.getRightSide() != null
						&& this.tree.getRightSide().getLeftSide() != null
						&& this.tree.getRightSide().getLeftSide().getLeftSide() != null) { // not
																							// empty
					retorno = new StringBuilder(this.tree.getRightSide()
							.getLeftSide().getLeftSide().getShortName())
							.toString();
				}

			} else if (nodePosition.equals("2_1_1_1")) { // se for 2_1_1_1

				if (this.tree.getRightSide() != null
						&& this.tree.getRightSide().getLeftSide() != null
						&& this.tree.getRightSide().getLeftSide().getLeftSide() != null
						&& this.tree.getRightSide().getLeftSide().getLeftSide()
								.getLeftSide() != null) { // not empty
					retorno = new StringBuilder(this.tree.getRightSide()
							.getLeftSide().getLeftSide().getLeftSide()
							.getShortName())
							.toString();
				}

			} else if (nodePosition.equals("2_1_1_2")) { // se for 2_1_1_2

				if (this.tree.getRightSide() != null
						&& this.tree.getRightSide().getLeftSide() != null
						&& this.tree.getRightSide().getLeftSide().getLeftSide() != null
						&& this.tree.getRightSide().getLeftSide().getLeftSide()
								.getRightSide() != null) { // not empty
					retorno = new StringBuilder(this.tree.getRightSide()
							.getLeftSide().getLeftSide().getRightSide()
							.getShortName())
							.toString();
				}

			} else if (nodePosition.equals("2_1_2")) { // se for 2_1_2

				if (this.tree.getRightSide() != null
						&& this.tree.getRightSide().getLeftSide() != null
						&& this.tree.getRightSide().getLeftSide()
								.getRightSide() != null) { // not empty
					retorno = new StringBuilder(this.tree.getRightSide()
							.getLeftSide().getRightSide().getShortName())
							.toString();
				}

			} else if (nodePosition.equals("2_1_2_1")) { // se for 2_1_2_1

				if (this.tree.getRightSide() != null
						&& this.tree.getRightSide().getLeftSide() != null
						&& this.tree.getRightSide().getLeftSide()
								.getRightSide() != null
						&& this.tree.getRightSide().getLeftSide()
								.getRightSide().getLeftSide() != null) { // not
																			// empty
					retorno = new StringBuilder(this.tree.getRightSide()
							.getLeftSide().getRightSide().getLeftSide()
							.getShortName())
							.toString();
				}

			} else if (nodePosition.equals("2_1_2_2")) { // se for 2_1_2_2

				if (this.tree.getRightSide() != null
						&& this.tree.getRightSide().getLeftSide() != null
						&& this.tree.getRightSide().getLeftSide()
								.getRightSide() != null
						&& this.tree.getRightSide().getLeftSide()
								.getRightSide().getRightSide() != null) { // not
																			// empty
					retorno = new StringBuilder(this.tree.getRightSide()
							.getLeftSide().getRightSide().getRightSide()
							.getShortName())
							.toString();
				}

			} else if (nodePosition.equals("2_2")) { // se for 2_2

				if (this.tree.getRightSide() != null
						&& this.tree.getRightSide().getRightSide() != null) { // not
																				// empty
					retorno = new StringBuilder(this.tree.getRightSide()
							.getRightSide().getShortName())
							.toString();
				}

			} else if (nodePosition.equals("2_2_1")) { // se for 2_2_1

				if (this.tree.getRightSide() != null
						&& this.tree.getRightSide().getRightSide() != null
						&& this.tree.getRightSide().getRightSide()
								.getLeftSide() != null) { // not empty
					retorno = new StringBuilder(this.tree.getRightSide()
							.getRightSide().getLeftSide().getShortName())
							.toString();
				}
			} else if (nodePosition.equals("2_2_1_1")) { // se for 2_2_1_1

				if (this.tree.getRightSide() != null
						&& this.tree.getRightSide().getRightSide() != null
						&& this.tree.getRightSide().getRightSide()
								.getLeftSide() != null
						&& this.tree.getRightSide().getRightSide()
								.getLeftSide().getLeftSide() != null) { // not
																		// empty
					retorno = new StringBuilder(this.tree.getRightSide()
							.getRightSide().getLeftSide().getLeftSide()
							.getShortName())
							.toString();
				}
			} else if (nodePosition.equals("2_2_1_2")) { // se for 2_2_1_2

				if (this.tree.getRightSide() != null
						&& this.tree.getRightSide().getRightSide() != null
						&& this.tree.getRightSide().getRightSide()
								.getLeftSide() != null
						&& this.tree.getRightSide().getRightSide()
								.getLeftSide().getRightSide() != null) { // not
																			// empty
					retorno = new StringBuilder(this.tree.getRightSide()
							.getRightSide().getLeftSide().getRightSide()
							.getShortName())
							.toString();
				}
			} else if (nodePosition.equals("2_2_2")) { // se for 2_2_2

				if (this.tree.getRightSide() != null
						&& this.tree.getRightSide().getRightSide() != null
						&& this.tree.getRightSide().getRightSide()
								.getRightSide() != null) { // not empty
					retorno = new StringBuilder(this.tree.getRightSide()
							.getRightSide().getRightSide().getShortName())
							.toString();
				}
			} else if (nodePosition.equals("2_2_2_1")) { // se for 2_2_2_1

				if (this.tree.getRightSide() != null
						&& this.tree.getRightSide().getRightSide() != null
						&& this.tree.getRightSide().getRightSide()
								.getRightSide() != null
						&& this.tree.getRightSide().getRightSide()
								.getRightSide().getLeftSide() != null) { // not
																			// empty
					retorno = new StringBuilder(this.tree.getRightSide()
							.getRightSide().getRightSide().getLeftSide()
							.getShortName())
							.toString();
				}
			} else if (nodePosition.equals("2_2_2_2")) { // se for 2_2_2_2

				if (this.tree.getRightSide() != null
						&& this.tree.getRightSide().getRightSide() != null
						&& this.tree.getRightSide().getRightSide()
								.getRightSide() != null
						&& this.tree.getRightSide().getRightSide()
								.getRightSide().getRightSide() != null) { // not
																			// empty
					retorno = new StringBuilder(this.tree.getRightSide()
							.getRightSide().getRightSide().getRightSide()
							.getShortName())
							.toString();
				}
			}

		}
		return retorno;
	}

	public String getIconNode(String nodePosition) {

		String retorno = EnumImg.BLANK_ICON.getUri(); // endereço de image not
														// found

		if (this.rootNode != null && this.rootNode.getUserId() != null) {

			if (nodePosition.equals("0")) { // se for ROOT
				if (this.tree != null) { // active
					
					retorno = chooseIcon(rootNode);
				}
				// return new
				// StringBuilder(this.rootNode.getFullname()).append(" - ").append(this.rootNode.getUserId()).toString();
			} else if (nodePosition.equals("1")) { // se for 1
				// return new StringBuilder(new
				// Account(this.rootNode.getLeftSide()).getFullname()).append(" - ").append(this.rootNode.getUserId()).toString();
				if (this.tree.getLeftSide() != null) { // not empty
					retorno = chooseIcon(this.tree.getLeftSide());
				}

			} else if (nodePosition.equals("1_1")) { // se for 1_1

				if (this.tree.getLeftSide() != null
						&& this.tree.getLeftSide().getLeftSide() != null) { // not
																			// empty
					retorno = chooseIcon(this.tree.getLeftSide().getLeftSide());
				}

			} else if (nodePosition.equals("1_1_1")) { // se for 1_1_1

				if (this.tree.getLeftSide() != null
						&& this.tree.getLeftSide().getLeftSide() != null
						&& this.tree.getLeftSide().getLeftSide().getLeftSide() != null) { // not
																							// empty
					retorno = chooseIcon(this.tree.getLeftSide().getLeftSide().getLeftSide());
				}

			} else if (nodePosition.equals("1_1_1_1")) { // se for 1_1_1_1

				if (this.tree.getLeftSide() != null
						&& this.tree.getLeftSide().getLeftSide() != null
						&& this.tree.getLeftSide().getLeftSide().getLeftSide() != null
						&& this.tree.getLeftSide().getLeftSide().getLeftSide()
								.getLeftSide() != null) { // not empty
					retorno = chooseIcon(this.tree.getLeftSide().getLeftSide().getLeftSide()
							.getLeftSide());
				}

			} else if (nodePosition.equals("1_1_1_2")) { // se for 1_1_1_2

				if (this.tree.getLeftSide() != null
						&& this.tree.getLeftSide().getLeftSide() != null
						&& this.tree.getLeftSide().getLeftSide().getLeftSide() != null
						&& this.tree.getLeftSide().getLeftSide().getLeftSide()
								.getRightSide() != null) { // not empty
					retorno = chooseIcon(this.tree.getLeftSide().getLeftSide().getLeftSide()
							.getRightSide());
				}

			} else if (nodePosition.equals("1_1_2")) { // se for 1_1_2

				if (this.tree.getLeftSide() != null
						&& this.tree.getLeftSide().getLeftSide() != null
						&& this.tree.getLeftSide().getLeftSide().getRightSide() != null) { // not
																							// empty
					retorno = chooseIcon(this.tree.getLeftSide().getLeftSide().getRightSide());
				}

			} else if (nodePosition.equals("1_1_2_1")) { // se for 1_1_2_1

				if (this.tree.getLeftSide() != null
						&& this.tree.getLeftSide().getLeftSide() != null
						&& this.tree.getLeftSide().getLeftSide().getRightSide() != null
						&& this.tree.getLeftSide().getLeftSide().getRightSide()
								.getLeftSide() != null) { // not empty
					retorno = chooseIcon(this.tree.getLeftSide().getLeftSide().getRightSide()
							.getLeftSide());
				}

			} else if (nodePosition.equals("1_1_2_2")) { // se for 1_1_2_2

				if (this.tree.getLeftSide() != null
						&& this.tree.getLeftSide().getLeftSide() != null
						&& this.tree.getLeftSide().getLeftSide().getRightSide() != null
						&& this.tree.getLeftSide().getLeftSide().getRightSide()
								.getRightSide() != null) { // not empty
					retorno = chooseIcon(this.tree.getLeftSide().getLeftSide().getRightSide()
							.getRightSide());
				}

			} else if (nodePosition.equals("1_2")) { // se for 1_2

				if (this.tree.getLeftSide() != null
						&& this.tree.getLeftSide().getRightSide() != null) { // not
																				// empty
					retorno = chooseIcon(this.tree.getLeftSide().getRightSide());
				}

			} else if (nodePosition.equals("1_2_1")) { // se for 1_2_1

				if (this.tree.getLeftSide() != null
						&& this.tree.getLeftSide().getRightSide() != null
						&& this.tree.getLeftSide().getRightSide().getLeftSide() != null) { // not
																							// empty
					retorno = chooseIcon(this.tree.getLeftSide().getRightSide().getLeftSide());
				}
			} else if (nodePosition.equals("1_2_1_1")) { // se for 1_2_1_1

				if (this.tree.getLeftSide() != null
						&& this.tree.getLeftSide().getRightSide() != null
						&& this.tree.getLeftSide().getRightSide().getLeftSide() != null
						&& this.tree.getLeftSide().getRightSide().getLeftSide()
								.getLeftSide() != null) { // not empty
					retorno = chooseIcon(this.tree.getLeftSide().getRightSide().getLeftSide()
							.getLeftSide() );
				}
			} else if (nodePosition.equals("1_2_1_2")) { // se for 1_2_1_2

				if (this.tree.getLeftSide() != null
						&& this.tree.getLeftSide().getRightSide() != null
						&& this.tree.getLeftSide().getRightSide().getLeftSide() != null
						&& this.tree.getLeftSide().getRightSide().getLeftSide()
								.getRightSide() != null) { // not empty
					retorno = chooseIcon(this.tree.getLeftSide().getRightSide().getLeftSide()
							.getRightSide());
				}
			} else if (nodePosition.equals("1_2_2")) { // se for 1_2_2

				if (this.tree.getLeftSide() != null
						&& this.tree.getLeftSide().getRightSide() != null
						&& this.tree.getLeftSide().getRightSide()
								.getRightSide() != null) { // not empty
					retorno = chooseIcon(this.tree.getLeftSide().getRightSide()
							.getRightSide());
				}
			} else if (nodePosition.equals("1_2_2_1")) { // se for 1_2_2_1

				if (this.tree.getLeftSide() != null
						&& this.tree.getLeftSide().getRightSide() != null
						&& this.tree.getLeftSide().getRightSide()
								.getRightSide() != null
						&& this.tree.getLeftSide().getRightSide()
								.getRightSide().getLeftSide() != null) { // not
																			// empty
					retorno = chooseIcon( this.tree.getLeftSide().getRightSide()
							.getRightSide().getLeftSide());
				}
			} else if (nodePosition.equals("1_2_2_2")) { // se for 1_2_2_2

				if (this.tree.getLeftSide() != null
						&& this.tree.getLeftSide().getRightSide() != null
						&& this.tree.getLeftSide().getRightSide()
								.getRightSide() != null
						&& this.tree.getLeftSide().getRightSide()
								.getRightSide().getRightSide() != null) { // not
																			// empty
					retorno = chooseIcon(this.tree.getLeftSide().getRightSide()
							.getRightSide().getRightSide());
				}
			} else if (nodePosition.equals("2")) { // se for 2

				if (this.tree.getRightSide() != null) { // not empty
					retorno = chooseIcon(this.tree.getRightSide());
				}

			} else if (nodePosition.equals("2_1")) { // se for 2_1

				if (this.tree.getRightSide() != null
						&& this.tree.getRightSide().getLeftSide() != null) { // not
																				// empty
					retorno = chooseIcon( this.tree.getRightSide().getLeftSide());
				}

			} else if (nodePosition.equals("2_1_1")) { // se for 2_1_1

				if (this.tree.getRightSide() != null
						&& this.tree.getRightSide().getLeftSide() != null
						&& this.tree.getRightSide().getLeftSide().getLeftSide() != null) { // not
																							// empty
					retorno = chooseIcon(this.tree.getRightSide().getLeftSide().getLeftSide());
				}

			} else if (nodePosition.equals("2_1_1_1")) { // se for 2_1_1_1

				if (this.tree.getRightSide() != null
						&& this.tree.getRightSide().getLeftSide() != null
						&& this.tree.getRightSide().getLeftSide().getLeftSide() != null
						&& this.tree.getRightSide().getLeftSide().getLeftSide()
								.getLeftSide() != null) { // not empty
					retorno = chooseIcon( this.tree.getRightSide().getLeftSide().getLeftSide()
							.getLeftSide());
				}

			} else if (nodePosition.equals("2_1_1_2")) { // se for 2_1_1_2

				if (this.tree.getRightSide() != null
						&& this.tree.getRightSide().getLeftSide() != null
						&& this.tree.getRightSide().getLeftSide().getLeftSide() != null
						&& this.tree.getRightSide().getLeftSide().getLeftSide()
								.getRightSide() != null) { // not empty
					retorno = chooseIcon(this.tree.getRightSide().getLeftSide().getLeftSide()
							.getRightSide());
				}

			} else if (nodePosition.equals("2_1_2")) { // se for 2_1_2

				if (this.tree.getRightSide() != null
						&& this.tree.getRightSide().getLeftSide() != null
						&& this.tree.getRightSide().getLeftSide()
								.getRightSide() != null) { // not empty
					retorno = chooseIcon( this.tree.getRightSide().getLeftSide()
							.getRightSide());
				}

			} else if (nodePosition.equals("2_1_2_1")) { // se for 2_1_2_1

				if (this.tree.getRightSide() != null
						&& this.tree.getRightSide().getLeftSide() != null
						&& this.tree.getRightSide().getLeftSide()
								.getRightSide() != null
						&& this.tree.getRightSide().getLeftSide()
								.getRightSide().getLeftSide() != null) { // not
																			// empty
					retorno = chooseIcon(this.tree.getRightSide().getLeftSide()
							.getRightSide().getLeftSide());
				}

			} else if (nodePosition.equals("2_1_2_2")) { // se for 2_1_2_2

				if (this.tree.getRightSide() != null
						&& this.tree.getRightSide().getLeftSide() != null
						&& this.tree.getRightSide().getLeftSide()
								.getRightSide() != null
						&& this.tree.getRightSide().getLeftSide()
								.getRightSide().getRightSide() != null) { // not
																			// empty
					retorno = chooseIcon(this.tree.getRightSide().getLeftSide()
							.getRightSide().getRightSide());
				}

			} else if (nodePosition.equals("2_2")) { // se for 2_2

				if (this.tree.getRightSide() != null
						&& this.tree.getRightSide().getRightSide() != null) { // not
																				// empty
					retorno = chooseIcon(this.tree.getRightSide().getRightSide());
				}

			} else if (nodePosition.equals("2_2_1")) { // se for 2_2_1

				if (this.tree.getRightSide() != null
						&& this.tree.getRightSide().getRightSide() != null
						&& this.tree.getRightSide().getRightSide()
								.getLeftSide() != null) { // not empty
					retorno = chooseIcon(this.tree.getRightSide().getRightSide()
							.getLeftSide());
				}
			} else if (nodePosition.equals("2_2_1_1")) { // se for 2_2_1_1

				if (this.tree.getRightSide() != null
						&& this.tree.getRightSide().getRightSide() != null
						&& this.tree.getRightSide().getRightSide()
								.getLeftSide() != null
						&& this.tree.getRightSide().getRightSide()
								.getLeftSide().getLeftSide() != null) { // not
																		// empty
					retorno = chooseIcon(this.tree.getRightSide().getRightSide()
							.getLeftSide().getLeftSide());
				}
			} else if (nodePosition.equals("2_2_1_2")) { // se for 2_2_1_2

				if (this.tree.getRightSide() != null
						&& this.tree.getRightSide().getRightSide() != null
						&& this.tree.getRightSide().getRightSide()
								.getLeftSide() != null
						&& this.tree.getRightSide().getRightSide()
								.getLeftSide().getRightSide() != null) { // not
																			// empty
					retorno = chooseIcon(this.tree.getRightSide().getRightSide()
							.getLeftSide().getRightSide());
				}
			} else if (nodePosition.equals("2_2_2")) { // se for 2_2_2

				if (this.tree.getRightSide() != null
						&& this.tree.getRightSide().getRightSide() != null
						&& this.tree.getRightSide().getRightSide()
								.getRightSide() != null) { // not empty
					retorno = chooseIcon(this.tree.getRightSide().getRightSide()
							.getRightSide());
				}
			} else if (nodePosition.equals("2_2_2_1")) { // se for 2_2_2_1

				if (this.tree.getRightSide() != null
						&& this.tree.getRightSide().getRightSide() != null
						&& this.tree.getRightSide().getRightSide()
								.getRightSide() != null
						&& this.tree.getRightSide().getRightSide()
								.getRightSide().getLeftSide() != null) { // not
																			// empty
					retorno = chooseIcon(this.tree.getRightSide().getRightSide()
							.getRightSide().getLeftSide());
				}
			} else if (nodePosition.equals("2_2_2_2")) { // se for 2_2_2_2

				if (this.tree.getRightSide() != null
						&& this.tree.getRightSide().getRightSide() != null
						&& this.tree.getRightSide().getRightSide()
								.getRightSide() != null
						&& this.tree.getRightSide().getRightSide()
								.getRightSide().getRightSide() != null) { // not
																			// empty
					retorno = chooseIcon(this.tree.getRightSide().getRightSide()
							.getRightSide().getRightSide() );

				}
			}

		}

		return retorno;
	}

	private String chooseIcon(Account rootNode) {
		if(rootNode!=null && rootNode.getStatus()!=null){
			switch (rootNode.getStatus()) {
			case "C":
				return EnumImg.CREATED_ICON.getUri();
			case "A":
				return EnumImg.ACTIVATED_ICON.getUri();
			case "I":
				return EnumImg.DEACTIVATED_ICON.getUri();
			case "S":
				return EnumImg.SUSPENDED_ICON.getUri();
			default:
				break;
			}
		}
		return "";
	}

	
	/**
	 * @author yesus
	 */
	public void suspendID(){
		sanctionSuspend.setOperation("S");
		sanctionSuspend.setOperator(loginBean.getUserAuth().getAccount().getId());
		sanctionSuspend.setDate(new Timestamp(new Date().getTime()));
		
		if(sanctionSuspend.getUserId().equals(loginBean.getUserAuth().getAccount().getUserId())){
			message("Você não pode suspender sua própria conta.");
			return;
		}
		
		if(sanctionSuspend.getUserId()!=null) sanctionSuspend.setUserId(sanctionSuspend.getUserId().replaceAll("#", ""));
		
		Account contaTmp = accountService.findByUserId(sanctionSuspend.getUserId());
		
		if(contaTmp==null || contaTmp.getId()==null){
			message("Conta com ID \"" + sanctionSuspend.getUserId() + "\" inválida.");
			sanctionSuspend = new Sanction();
			return;
		}
		
		if(!contaTmp.getStatus().equals("A")){
			message("Esta conta não está ativa!");
			sanctionSuspend = new Sanction();
			return;
		}else{
			accountService.suspendID(sanctionSuspend.getUserId());
			sanctionSuspend = sanctionService.save(sanctionSuspend);
			message("Suspensão de conta ID \""+sanctionSuspend.getUserId() +"\" efetuada!");
			sanctionSuspend = new Sanction();
			return;
		}
		
		
	}
	
	/**
	 * @author yesus
	 */
	public void reactivateSuspendedID(){
		sanctionActivate.setOperation("R");
		sanctionActivate.setOperator(loginBean.getUserAuth().getAccount().getId());
		sanctionActivate.setDate(new Timestamp(new Date().getTime()));
		
		if(sanctionActivate.getUserId().equals(loginBean.getUserAuth().getAccount().getUserId())){
			message("Você não pode reativar sua própria conta.");
			return;
		}
		
		if(sanctionActivate.getUserId()!=null) sanctionActivate.setUserId(sanctionActivate.getUserId().replaceAll("#", ""));
		
		Account contaTmp = accountService.findByUserId(sanctionActivate.getUserId());
		
		if(!contaTmp.getStatus().equals("S")){
			message("Esta conta não está suspensa!");
			return;
		}else{
			accountService.reactivateSuspendedID(sanctionActivate.getUserId());
			sanctionActivate = sanctionService.save(sanctionActivate);
			message("Reativação de conta efetuada!");
		}
	}
	
	
	/**
	 * @author yesus
	 * 
	 */
	public void ativarID(){
		Account contaAtivar;

		Account contaUserLogado;
		try {
			contaUserLogado = accountService.findById(loginBean.getUserAuth().getAccount().getId());
			if(contaUserLogado.getTickets()<=0){
				message("Você não possui tickets, compre já um \"Pacote de liderança\" na loja.");
				return;
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		
		
		if(userIdAtivar!=null) userIdAtivar = userIdAtivar.replaceAll("#", "");
		
		contaAtivar = accountService.findByUserId(userIdAtivar);
		
		//S - SUSPENSA -> Bloqueada pelo administrador do sistema.
		if(contaAtivar.getStatus().equals("S")){
			message("Esta conta não pode ser ativada. Procure o suporte!");
			return;
		}
		
		if(contaAtivar!=null && contaAtivar.getId()==null){
			message("Digite um ID válido.");
			return;
		}
		
		//ATIVADO
		if(contaAtivar!=null && contaAtivar.getStatus().equals("A")){
			String dataAtivacao = new SimpleDateFormat("dd/MM/yyyy").format(new Date(contaAtivar.getActivated().getTime()));
			message("Você não pode ativar esta conta. " +
							 					(contaAtivar.getActivated()!=null? 
							 							("Conta ativa desde " + dataAtivacao)
							 							:""));
		}

		//CRIADO
		if(contaAtivar!=null && contaAtivar.getStatus().equals("C")){
			//Aqui temos que ativar a conta e já jogar no braço de acordo com as preferências.
			accountService.useTicket(loginBean.getUserAuth().getAccount());
			accountService.activatedAccount(userIdAtivar);
			message("Usuário ativado com sucesso!");
			try {
				goToNetwork();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
		//I - INATIVA -> Expirou o prazo de um ano do pagamento.
		if(contaAtivar!=null && contaAtivar.getStatus().equals("I")){
			//Aqui somente ativar a conta
			accountService.useTicket(loginBean.getUserAuth().getAccount());
			accountService.activatedAccount(userIdAtivar);
			message("Usuário ativado com sucesso!");
			try {
				goToNetwork();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			//Futuro: 
		}
		//S - SUSPENSA -> Bloqueada pelo administrador do sistema.
//		if(contaAtivar!=null && contaAtivar.getStatus().equals("S")){
//		}
		
		
		try {
			loginBean.getUserAuth().setAccount(accountService.findById(loginBean.getUserAuth().getAccount().getId()));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
//		accountService.activatedAccount(userIdAtivar);
	}
	

	public String getUserIdSuspend() {
		return userIdSuspend;
	}
	
	

	public void setUserIdSuspend(String userIdSuspend) {
		this.userIdSuspend = userIdSuspend;
	}

	/**
	 * @author yesus
	 */
	public void saveConfigNetwork(){
		accountService.saveNetworkSide(loginBean.getUserAuth().getAccount().getId(), selectConfigNetworkValue);	
		loginBean.getUserAuth().getAccount().setNetworkSide(selectConfigNetworkValue);
		message("Definições de rede salvas com sucesso.");
//		System.out.println(selectConfigNetworkValue);
	}
	
	public void goToAccount() {
		activeSubTab1 = 1; // Level 1 option - resets level 2 options
//		System.out.println("go to tab Account");
		setUser(loginBean.getUserAuth());
		this.activeTab = "account";
	}

	public void goToAccount(int subtab) {
		this.activeSubTab1 = subtab; // Level 1 option - resets level 2 options
//		System.out.println("go to tab Account");
		setUser(loginBean.getUserAuth());
		this.activeTab = "account";
	}

	public void goToHelp() {
		activeSubTab1 = 1; // Level 1 option - resets level 2 options
		// this.userAuth = loginBean.getUserAuth();
//		System.out.println("go to tab Help");
		this.activeTab = "help";
	}

	public void goToMarket() {
		activeSubTab1 = 1; // Level 1 option - resets level 2 options
//		System.out.println("go to tab Market");
		this.activeTab = "market";
	}
	public void goToAdministrator() {
		activeSubTab1 = 1; // Level 1 option - resets level 2 options
//		System.out.println("go to tab Market");
		this.activeTab = "administrator";
	}

	// activeTab - Account
	// activeSubTab1 - Dados Pessoais
	public void goToChangePersonalInfo() throws SQLException {
//		System.out.println("go to Change Personal Info");
		setUserAuth(userService.findByUser(loginBean.getUserAuth()));
		setUser(loginBean.getUserAuth());
		this.activeSubTab1 = 1;
	}

	// activeTab - Account
	// activeSubTab1 - Dados de Contato
	public void goToChangeContactInfo() {
//		System.out.println("go to Change Contact Info");
		setUser(loginBean.getUserAuth());
		this.activeSubTab1 = 2;
	}

	// activeTab - Account
	// activeSubTab1 - ChangePassor
	public void goToChangeAccessInfo() {
//		System.out.println("go to Change Access Info");
		setUser(loginBean.getUserAuth());
		this.user = new User();
		this.activeSubTab1 = 3;
	}
	
	// activeTab - Account
	// activeSubTab1 - ChangePassor
	public void goToNetworkSetup() {
//		System.out.println("go to Change Access Info");
		setUser(loginBean.getUserAuth());
		
		selectConfigNetworkValue = loginBean.getUserAuth().getAccount().getNetworkSide();
		
		this.user = new User();
		this.activeSubTab1 = 4;
	}

	// //activeTab - Account
	// //activeSubTab1 - ChangePassord
	// public void goToChangePassword(){
	// System.out.println("go to Change Password");
	// this.activeSubTab1 = 2;
	// }

	// activeTab - Help
	// activeSubTab1 - ActivitiesCycle
	public void goToActivitiesCycle() {
//		System.out.println("go to Activities Cycle");
		this.activeSubTab1 = 1;
	}

	// activeTab - Help
	// activeSubTab1 - ActivitiesCycle
	public void goToMarketingPlan() {
//		System.out.println("go to Marketing Plan");
		this.activeSubTab1 = 2;
	}

	// activeTab - Help
	// activeSubTab1 - Manuals
	public void goToManuals() {
//		System.out.println("go to Manuals");
		this.activeSubTab1 = 3;
	}

	// activeTab - Help
	// activeSubTab1 - Manuals
	public void goToTerms() {
//		System.out.println("go to Terms");
		this.activeSubTab1 = 4;
	}

	// activeTab - Help
	// activeSubTab1 - Support
	public void goToSupport() {
//		System.out.println("go to Support");
		EnumSupportSubject[] ess = EnumSupportSubject.values();
		// this.subjectList.addAll(ess);
		this.activeSubTab1 = 5;
	}

	public void saveChangePersonalInfo() {
//		System.out.println("SaveChangePersonalInfo");
		//cadastroBean.saveChangePersonalInfo(this.userAuth);
		cadastroBean.saveChangePersonalInfo(this.user);
	}
	
	
	public void saveChangeContactInfo(){
		cadastroBean.saveChangeContactInfo(this.user);
	}

//	 public void saveChangePass(){
//		 System.out.println("SaveChangePass");
//		 cadastroBean.saveChangePass();
//	 }

	public String getTabEhAtiva(String tab) {
//		System.out.println("getTabEhAtiva tab:" + tab + " active"
//				+ activeTab.equalsIgnoreCase(tab));
		StringBuffer result = new StringBuffer();
		result.append("tab-pane ");
		if (activeTab.equalsIgnoreCase(tab)) {
			result.append("active");
		}
		return result.toString();
	}

	public String getSubTab1EhAtiva(int subtab) {
//		System.out.println("getSubTab1EhAtiva tab:" + subtab + " active");
		StringBuffer result = new StringBuffer();
		result.append(" ");
		if (activeSubTab1 == subtab) {
			result.append("active");
		}
		return result.toString();
	}

	public CadastroBean getCadastroBean() {
		return cadastroBean;
	}

	public LoginBean getLoginBean() {
		return loginBean;
	}

	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}

	public void setCadastroBean(CadastroBean cadastroBean) {
		this.cadastroBean = cadastroBean;
	}

	public User getUserAuth() {
		return userAuth;
	}

	public void setUserAuth(User userAuth) {
		this.userAuth = userAuth;
	}

	public List<EnumSupportSubject> getSubjectList() {
		if (this.subjectList.isEmpty()) {
			setSubjectList();
		}
		return subjectList;
	}

	public void setSubjectList() {

		for (EnumSupportSubject subject : EnumSupportSubject.values()) {
			// SupportMessage sm = new SupportMessage(subject.getCode(),
			// subject, "body test");
			this.subjectList.add(subject);
		}

	}

	public SupportMessage getSupportMessage() {
		return supportMessage;
	}

	public void setSupportMessage(SupportMessage supportMessage) {
		this.supportMessage = supportMessage;
	}

	public String sendSupportMsg() {

		// try {
		// this.user = accountService.saveAccountAndUser(this.user);
		// depois de salvar a conta e o user, executar um update na conta para
		// colocar o account.userId
		// accountService.updateUserIdAndParentId(this.user);

		// support@rsmnetwork.zohosupport.com

//		this.messageService.enviarMensagem("patrick.nascimento@gmail.com",
//				"suporte@rsmnetwork.com.br", this.supportMessage.getSubject()
//						.name(), "testando!!!");
		this.messageService.enviarMensagem("patrick.nascimento@gmail.com",
				"suporte@rsmnetwork.com.br", this.supportMessage.getSubject().toString() ,"testando!!!");
		// notificationService.save(new Notification("Bem vindo à RSM",
		// "Bla bla bla", user.getAccount().getId()));
		// loginBean.efetuarLogin(this.user);
		// this.user = new User();
		// } catch (SQLException e) {
		// e.printStackTrace();
		// }
		return "pretty:bemvindo";

	}

	// public List<State> getStateList() {
	// if(stateList.isEmpty()){
	// setStateList();
	// }
	// return stateList;
	// }
	//
	//
	//
	// public void setStateList() {
	//
	// for(EnumEstado estado : EnumEstado.values()){
	// this.stateList.add(new State(estado.getSigla(), estado.getNome()));
	// }
	//
	// }

	public boolean isTabAtiva(String tab) {
//		System.out.println(">>>isTabAtiva("+tab+")");
		return activeTab.equalsIgnoreCase(tab);
	}

	public boolean isSubTabAtiva(int subtab) {
//		System.out.println(">>>isSubTabAtiva("+subtab+")");
		return activeSubTab1 == subtab;
	}

	public long getTotalRede() {
		return totalRede;
	}

	public void setTotalRede(long totalRede) {
		this.totalRede = totalRede;
	}

	public long getTotalBracoDir() {
		return totalBracoDir;
	}

	public void setTotalBracoDir(long totalBracoDir) {
		this.totalBracoDir = totalBracoDir;
	}

	public long getTotalBracoEsq() {
		return totalBracoEsq;
	}

	public void setTotalBracoEsq(long totalBracoEsq) {
		this.totalBracoEsq = totalBracoEsq;
	}

	public String getUserIdPesquisa() {
		return userIdPesquisa;
	}

	public void setUserIdPesquisa(String userIdPesquisa) {
		this.userIdPesquisa = userIdPesquisa;
	}

	public List<SelectItem> getItensConfigNetwork() {
		return itensConfigNetwork;
	}

	public void setItensConfigNetwork(List<SelectItem> itensConfigNetwork) {
		this.itensConfigNetwork = itensConfigNetwork;
	}

	public String getSelectConfigNetworkValue() {
		return selectConfigNetworkValue;
	}

	public void setSelectConfigNetworkValue(String selectConfigNetworkValue) {
		this.selectConfigNetworkValue = selectConfigNetworkValue;
	}
	
	public String getUserIdAtivar() {
		return userIdAtivar;
	}

	public void setUserIdAtivar(String userIdAtivar) {
		this.userIdAtivar = userIdAtivar;
	}

	public Sanction getSanctionSuspend() {
		return sanctionSuspend;
	}

	public void setSanctionSuspend(Sanction sanctionSuspend) {
		this.sanctionSuspend = sanctionSuspend;
	}

	public Sanction getSanctionActivate() {
		return sanctionActivate;
	}

	public void setSanctionActivate(Sanction sanctionActivate) {
		this.sanctionActivate = sanctionActivate;
	}
	
//	public boolean isSubTab1Ativa(int subtab){
//		System.out.println(">>>isSubTabAtiva("+subtab+")");
//		return activeSubTab1.equalsIgnoreCase(subtab);
//	}
	
	
	public void setCarrierList() {
		this.carrierList.add(new Carrier(1, "Vivo"));
		this.carrierList.add(new Carrier(2, "Tim"));
		this.carrierList.add(new Carrier(3, "Claro"));
		this.carrierList.add(new Carrier(4, "Oi"));
		this.carrierList.add(new Carrier(5, "Algar"));
		this.carrierList.add(new Carrier(6, "Nextel"));
		EnumOperadoras.values();
	}



	public List<Carrier> getCarrierList() {
		if(carrierList.isEmpty()){
			setCarrierList();
		}
		return carrierList;
	}

	
	public List<City> getCityList() throws SQLException {
		if(cityList.isEmpty()){
			setCityList();
		}
		return cityList;
	}

	//TODO Alterar a lógica para a busca de cidades
	private void setCityList() throws SQLException {
		
//		System.out.println("Estado Selecionado em setCityList: "+getUser().getAccount().getState());
		if (getUser().getAccount().getState() != null) { 
			cityList = directionService.findCitiesByState(new State(getUser().getAccount().getState()));
		}
			
			//cityList = directionService.findCitiesByState(new State(getUser().getAccount().getState()));
//			if(getUser().getAccount().getState().equals("AC")){
//				cityList = directionService.findCitiesByState(new State("AC"));
//			} else if(getUser().getAccount().getState().equals("AL")){
//				cityList = directionService.findCitiesByState(new State("AL"));
//			} else if(getUser().getAccount().getState().equals("AP")){
//				cityList = directionService.findCitiesByState(new State("AP"));
//			} else if(getUser().getAccount().getState().equals("AM")){
//				cityList = directionService.findCitiesByState(new State("AM"));
//			} else if(getUser().getAccount().getState().equals("BA")){
//				cityList = directionService.findCitiesByState(new State("BA"));
//			} else if(getUser().getAccount().getState().equals("CE")){
//				cityList = directionService.findCitiesByState(new State("CE"));
//			} else if(getUser().getAccount().getState().equals("DF")){
//				cityList = directionService.findCitiesByState(new State("DF"));
//			}
//			
//			
//				cityList = new ArrayList<String>();
//				cityList.add("Salvador");
//				cityList = directionService.findCitiesByState(new State(5));
//				
//				return;
//			case 7:
//				cityList = new ArrayList<String>();
//				cityList.add("Brasília");
//				return;
//			case 13:
//				cityList = new ArrayList<String>();
//				cityList.add("Belo Horizonte");
//				return;
//			case 19:
//				cityList = new ArrayList<String>();
//				cityList.add("Rio de Janeiro");
//				return;
//			case 25:
//				cityList = new ArrayList<String>();
//				cityList.add("Sao Paulo");
//				return;
//
//		}
//		}
	}



	public List<State> getStateList() {
		if(stateList.isEmpty()){
			setStateList();
		}
		return stateList;
	}

	

	public void setStateList() {
		
		for(EnumEstado estado : EnumEstado.values()){
			this.stateList.add(new State(estado.getSigla(), estado.getNome()));
		}

	}

	
}
