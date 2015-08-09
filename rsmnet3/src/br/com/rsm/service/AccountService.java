package br.com.rsm.service;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import br.com.rsm.dao.AccountDao;
import br.com.rsm.dao.UserDao;
import br.com.rsm.model.Account;
import br.com.rsm.model.User;


public class AccountService implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3401354213363357024L;
	private AccountDao accountDao = new AccountDao();
	private UserDao userDao;
	private Map<Long, String> domain = new HashMap<Long, String>();
	private char [] newId = new char []{'9', '9', '9', '9', '9'}; 
	

	public AccountService() {
		init();
	}
	
	public boolean verificarIdIndicacao(String parentId) throws ClassNotFoundException, SQLException {
		boolean result = false; //nao encontrou
		Account accountFound = accountDao.findByParentId(parentId);
		if (accountFound != null && accountFound.getId() != null){
			result = true; //encontrou
		}
		return result;
	}
	
	
	

	public User saveAccountAndUser(User user) throws SQLException {
		User result = new User();
		this.userDao = new UserDao();
		String parentId = user.getAccount().getParentId();
//		user.getAccount().setStatus("CRIADA");
		user.getAccount().setStatus("C");
		Account contaSalva = accountDao.save(user.getAccount());
		
		//update na conta para aproveitar o account.id para gerar o account.userId.
		Long idConta = contaSalva.getId(); //o update nao ta retornando o id, bkp dele aqui.
		
		user.getAccount().setId(idConta);
		
		this.userDao = new UserDao();
		user.setAccount(contaSalva);
		User usuarioSalvo = userDao.save(user);
		
		
		contaSalva = accountDao.findById(contaSalva.getId());
		contaSalva.setParentId(parentId);
		result = userDao.findByUser(usuarioSalvo);
		result.setAccount(contaSalva);
		result.setLogin(contaSalva.getEmail());
		
		return result;
	}
	
	//Logo após criar a conta do usuário, precisamos gerar um id para depois poder gerar e salvar o account.userId e o user.parentId
	public User updateUserIdAndParentId(User user) {
		User result = new User();
			this.userDao = new UserDao();
			
			//User parentUser = userDao.findParentByParentId(user.getAccount().getParentId());
			//user = userDao.findByUser(user);
			//account = accountDao.findByUser();
			//user.setParentId(parentUser.getId());
			user.getAccount().setUserId(generateId(user.getAccount().getId()));
			
			//user.getAccount.getParentId tem uma string com a chave do parent
			//usar essa chave para achar o id long do user
			//continuar como esta...
			try {
				Account parentAccount = accountDao.findByParentId(user.getAccount().getParentId());
				//User userAccount = userDao.findParentByParentId(parentAccount.getId());
				
				accountDao.saveUserId(user.getAccount());
				user.setParentId(parentAccount.getId());
				User userUpdated = userDao.saveParentId(user);
				//Account contaSalva = accountDao.update(user.getAccount());
				
				//update na conta para aproveitar o account.id para gerar o account.userId.
				//Long idUser = userUpdated.getId(); //o update nao ta retornando o id, bkp dele aqui.
				result = userUpdated;
				//user.getAccount().setId(idConta);
			} catch(Exception e){
				e.printStackTrace();
			}
			
		return result;
		
	}
	
	public Account findUpAccount(Long currentId) {
		Account accountFound;
		try {
			accountFound = this.accountDao.findUpAccountByCurrentId(currentId);
			return accountFound;
		} catch (NumberFormatException | SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public Account findById(Long id) throws SQLException, ClassNotFoundException{
			Account accountFound = this.accountDao.findById(id);
			return accountFound;
	}
	
	//recuperar o id do pai, baseado no userId dele, esta esta no parentId do novo user.
	//User parentUser = userDao.findParentByParentId(user.getAccount().getParentId());
	//user.setParentId(parentUser.getId());
	//user.getAccount().setUserId(generateId(user.getAccount().getId()));
	
//	public User updateAccountAndUser(User user) throws SQLException {
//		User result = new User();
//		try (Connection connection = new ConnectionFactory().getConnection()) {
//			connection.setAutoCommit(false);
//			this.accountDao = new AccountDao(connection);
//			Account contaSalva = accountDao.update(user.getAccount());
//			this.userDao = new UserDao(connection);
//			user.setAccount(contaSalva);
//			User usuarioSalvo = userDao.save(user);
//			contaSalva = accountDao.findByAccount(contaSalva);
//			result = userDao.findByUser(usuarioSalvo);
//			result.setAccount(contaSalva);
//			result.setLogin(contaSalva.getEmail());
//			
//			
//		} catch(Exception e){
//			e.printStackTrace();
//		}
//		
//		return result;
//	}
	
	public User saveChangePass(User user) throws SQLException{
		User result = new User();
		//this.accountDao = new AccountDao(connection);
		//user.getAccount().setStatus("CRIADA");
		//Account contaSalva = accountDao.save(user.getAccount());
		this.userDao = new UserDao();
		//user.setAccount(contaSalva);
		//recuperar o usuario logado, setar o novo password, criar um metodo update e chama-lo.
		User usuarioSalvo = userDao.save(user);
		if(usuarioSalvo != null){
			result = userDao.findByUser(usuarioSalvo);
		}
		//contaSalva = accountDao.findByAccount(contaSalva);
			
		//	result.setAccount(contaSalva);
		//	result.setLogin(contaSalva.getEmail());
			
		return result;
	}
	
	
	public Account saveChangePersonalInfo(User user) throws SQLException {
		Account result = new Account();
//		this.accountDao = new UserDao();
		this.accountDao = new AccountDao();
		Account contaSalva = accountDao.saveChangePersonalInfo(user.getAccount());
		if(contaSalva != null && contaSalva.getId() != null){
			//System.out.println("refresh nos dados do usuário");
			result = accountDao.findById(contaSalva.getId()); //refresh nos dados do usuário
		}
		
		return result;
	}

	public char[] getNewId() {
		return newId;
	}

	public void setNewId(char[] newId) {
		this.newId = newId;
	}

	private void init() {
		
		getDomain().put(0L, "9");
		getDomain().put(1L, "Y");
		getDomain().put(2L, "V");
		getDomain().put(3L, "D");
		getDomain().put(4L, "E");
		getDomain().put(5L, "6");
		getDomain().put(6L, "F");
		getDomain().put(7L, "G");
		getDomain().put(8L, "I");
		getDomain().put(9L, "4");
		getDomain().put(10L, "J");
		getDomain().put(11L, "K");
		getDomain().put(12L, "7");
		getDomain().put(13L, "L");
		getDomain().put(14L, "B");
		getDomain().put(15L, "M");
		getDomain().put(16L, "3");
		getDomain().put(17L, "P");
		getDomain().put(18L, "Q");
		getDomain().put(19L, "R");
		getDomain().put(20L, "C");
		getDomain().put(21L, "U");
		getDomain().put(22L, "0");
		getDomain().put(23L, "S");
		getDomain().put(24L, "T");
		getDomain().put(25L, "W");
		getDomain().put(26L, "5");
		getDomain().put(27L, "X");
		getDomain().put(28L, "H");
		getDomain().put(29L, "Z");
		getDomain().put(30L, "1");
		getDomain().put(31L, "A");
		getDomain().put(32L, "2");
		getDomain().put(33L, "N");
		getDomain().put(34L, "8");
		
		
	}

	public Map<Long, String> getDomain() {
		return domain;
	}

	public void setDomain(Map<Long, String> domain) {
		this.domain = domain;
	}
	

	private String generateId(Long id) {
		
		if(id <= 1224L){
			this.newId[0] = domain.get(new Long((id % 35))).charAt(0);  //A para B, B para C
			newId[1] = domain.get(new Long((id / 35))).charAt(0);  //A para B, B para C
		}else if(id <= 42874L){
			Long aux1 = id / 35L;
			Long aux1b = id % 35L;
			Long aux2 = aux1 / 35L;
			Long aux2b = aux1 % 35L;
			
			newId[0] = domain.get(new Long((aux1b))).charAt(0); 
			newId[1] = domain.get(new Long((aux2b))).charAt(0); 
			newId[2] = domain.get(new Long((aux2))).charAt(0);
		}else if(id <= 1500624L){
			Long aux1 = id / 35L;
			Long aux1b = id % 35L;
			Long aux2 = aux1 / 35L;
			Long aux2b = aux1 % 35L;
			Long aux3 = aux2 / 35L;
			Long aux3b = aux2 % 35L;
			
			newId[0] = domain.get(new Long((aux1b))).charAt(0); 
			newId[1] = domain.get(new Long((aux2b))).charAt(0); 
			newId[2] = domain.get(new Long((aux3b))).charAt(0);
			newId[3] = domain.get(new Long((aux3))).charAt(0);
		}else if(id <= 52521874L){
			Long aux1 = id / 35L;
			Long aux1b = id % 35L;
			Long aux2 = aux1 / 35L;
			Long aux2b = aux1 % 35L;
			Long aux3 = aux2 / 35L;
			Long aux3b = aux2 % 35L;
			Long aux4 = aux3 / 35L;
			Long aux4b = aux3 % 35L;
			
			newId[0] = domain.get(new Long((aux1b))).charAt(0); 
			newId[1] = domain.get(new Long((aux2b))).charAt(0); 
			newId[2] = domain.get(new Long((aux3b))).charAt(0);
			newId[3] = domain.get(new Long((aux4b))).charAt(0);
			newId[4] = domain.get(new Long((aux4))).charAt(0);
		}
		
		String result = newId[4] +""+ newId[3] +""+ newId[2] +""+ newId[1] +""+ newId[0];      
		return result;
	
		
	}

	/**
	 * @author yesus
	 * @param userId
	 * @return
	 */
	public int getTotalIndicadosDiretos(String userId) {
		return new AccountDao().getTotalIndicadosDiretos(userId);
	}

	public Account findByUserId(String id) {
		try {
			Account accountFound;
			accountFound = this.accountDao.findByUserId(id);
			return accountFound;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @author yesus
	 * @param id
	 * @param selectConfigNetworkValue
	 */
	public void saveNetworkSide(Long id, String selectConfigNetworkValue) {
		accountDao.saveNetworkSide(id,selectConfigNetworkValue);
	}

	public Account isActivated(String userIdAtivar) {
		return accountDao.isActivated(userIdAtivar);
	}

	public void activatedAccount(String userIdAtivar) {
		accountDao.activatedAccount(userIdAtivar);		
	}

	public void useTicket(Account account) {
		accountDao.useTicket(account);		
	}

	/**
	 * @author yesus
	 */
	public void suspendID(String userIdSuspend) {
		accountDao.suspendID(userIdSuspend);		
	}

	/**
	 * @author yesus
	 */
	public void reactivateSuspendedID(String userIdSuspend) {
		accountDao.reactivateSuspendedID(userIdSuspend);
	}

	public Account saveChangeContactInfo(User user) throws SQLException {
		Account result = new Account();
		this.accountDao = new AccountDao();
		Account contaSalva = accountDao.saveChangeContactInfo(user.getAccount());
		if(contaSalva != null && contaSalva.getId() != null){
			//System.out.println("refresh nos dados do usuário");
			result = accountDao.findById(contaSalva.getId()); //refresh nos dados do usuário
		}
		
		return result;
	}



}
