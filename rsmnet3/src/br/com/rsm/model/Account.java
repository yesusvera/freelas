package br.com.rsm.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;
//import javax.validation.constraints.Pattern;


@Entity(name="tb_account")
public class Account implements Serializable{

	/**
	 *   
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	private Long id;
	
	private String userId;
	
	//private String Status;
	private String fullname;
	private String email;
	private Date birthdate;
	private String cpf;
	private String rg;
	private String address;
	private String addressNumber;
	private String complement;
	private String cep;
	@Transient
	private String state;
	private City city = new City();
	private String phone;
	private String mobile;
	private String carrier;
	@Transient
	private boolean agreement;
	private String status;
	private Account leftSide;
	private Account rightSide;
	private Timestamp created;
	private Timestamp activated;
	
	@Transient
	private String parentId;
	
	private String networkSide;
	
	private Long tickets;
	
	public Account(){}
	
	public Account(Long id){
		this.id = id;
	}


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public String getFullname() {
		
		return fullname;
	}
	
	public void setFullname(String fullname) {
		this.fullname = fullname;
	}
	
	public String getShortName(){
		if(fullname==null){
			return "";
		}
		
		String[] arrTmp = fullname.replace("\n", " ").split(" ");
		
		String shortName = "";
		
		if(arrTmp.length >= 2){
			shortName = arrTmp[0] + " " + arrTmp[1] ;
		}else if(arrTmp.length >= 1){
			shortName = arrTmp[0];
		}
	
		//Passo o limitador de tamanho para ambas situações.
		if(shortName!=null){
			if(shortName.length() > 20){
				shortName = fullname.substring(0, 19) + "...";
			}
		}
		return shortName;

//		return getFullname();
	}

	

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCpf() {
		return cpf;
	}	

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public Date getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(Date birthdate) {
		this.birthdate = birthdate;
	}

	public String getRg() {
		return rg;
	}

	public void setRg(String rg) {
		this.rg = rg;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAddressNumber() {
		return addressNumber;
	}

	public void setAddressNumber(String addressNumber) {
		this.addressNumber = addressNumber;
	}

	public String getComplement() {
		return complement;
	}

	public void setComplement(String complement) {
		this.complement = complement;
	}

	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getCarrier() {
		return carrier;
	}

	public void setCarrier(String carrier) {
		this.carrier = carrier;
	}

	public boolean isAgreement() {
		return agreement;
	}

	public void setAgreement(boolean agreement) {
		this.agreement = agreement;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	

	public Account getLeftSide() {
		return leftSide;
	}

	public Timestamp getCreated() {
		return created;
	}

	public void setCreated(Timestamp created) {
		this.created = created;
	}

	public void setLeftSide(Account leftSide) {
		this.leftSide = leftSide;
	}

	public Account getRightSide() {
		return rightSide;
	}

	public void setRightSide(Account rightSide) {
		this.rightSide = rightSide;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getNetworkSide() {
		return networkSide;
	}

	public void setNetworkSide(String networkSide) {
		this.networkSide = networkSide;
	}
	
	public Long getTickets() {
		return tickets;
	}

	public void setTickets(Long tickets) {
		this.tickets = tickets;
	}

	public Timestamp getActivated() {
		return activated;
	}

	public void setActivated(Timestamp activated) {
		this.activated = activated;
	}

	public String getLabelBracoPreferencia(){
		if(networkSide==null){
			return "";
		}
		switch (networkSide) {
		case "A":
			return "Menor Braço";
		case "D":
			return "Braço direito";
		case "E":
			return "Braço esquerdo";
		}
		
		return "";
	}
	
}