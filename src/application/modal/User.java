package application.modal;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class User {
	private IntegerProperty userID;
	private IntegerProperty type;
	private StringProperty pincode;
	private StringProperty username;
	private StringProperty password;
	
	private StringProperty tdmInitials;
	private StringProperty name;
	private StringProperty info;
	private StringProperty info2;
	private StringProperty department;
	private StringProperty work;
	private StringProperty mobile;
	private StringProperty email;

	public User() {
		this.userID = new SimpleIntegerProperty();
		this.type = new SimpleIntegerProperty();
		this.pincode = new SimpleStringProperty("");
		this.username = new SimpleStringProperty("");
		this.password = new SimpleStringProperty("");
		this.tdmInitials = new SimpleStringProperty("");
		this.name = new SimpleStringProperty("");
        this.department = new SimpleStringProperty("");
        this.work = new SimpleStringProperty("");
        this.mobile = new SimpleStringProperty("");
        this.email = new SimpleStringProperty("");
        this.info = new SimpleStringProperty("");
        this.info2 = new SimpleStringProperty("");
	}
	
	public User(Integer userID, Integer type, String pincode, String username, String password,String tdmInitials,String name, String department, String work, String mobile, String email,String info,String info2) {
		this.userID = new SimpleIntegerProperty(userID);
		this.type = new SimpleIntegerProperty(type);
		this.pincode = new SimpleStringProperty(pincode);
		
		this.username = new SimpleStringProperty(username);
		this.password = new SimpleStringProperty(password);
		this.tdmInitials = new SimpleStringProperty(tdmInitials);
        this.name = new SimpleStringProperty(name);
        
        this.department = new SimpleStringProperty(department);
        this.work = new SimpleStringProperty(work);
        this.mobile = new SimpleStringProperty(mobile);
        this.email = new SimpleStringProperty(email);
        this.info = new SimpleStringProperty(info);
        this.info2 = new SimpleStringProperty(info2);
        
	}


	public IntegerProperty userIDProperty() {
		return this.userID;
	}
	


	public int getUserID() {
		return this.userIDProperty().get();
	}
	


	public void setUserID(final int userID) {
		this.userIDProperty().set(userID);
	}
	


	public IntegerProperty typeProperty() {
		return this.type;
	}
	


	public int getType() {
		return this.typeProperty().get();
	}
	


	public void setType(final int type) {
		this.typeProperty().set(type);
	}
	


	public StringProperty pincodeProperty() {
		return this.pincode;
	}
	


	public String getPincode() {
		return this.pincodeProperty().get();
	}
	


	public void setPincode(final String pincode) {
		this.pincodeProperty().set(pincode);
	}
	


	public StringProperty usernameProperty() {
		return this.username;
	}
	


	public String getUsername() {
		return this.usernameProperty().get();
	}
	


	public void setUsername(final String username) {
		this.usernameProperty().set(username);
	}
	


	public StringProperty passwordProperty() {
		return this.password;
	}
	


	public String getPassword() {
		return this.passwordProperty().get();
	}
	


	public void setPassword(final String password) {
		this.passwordProperty().set(password);
	}
	


	public StringProperty nameProperty() {
		return this.name;
	}
	


	public String getName() {
		return this.nameProperty().get();
	}
	


	public void setName(final String name) {
		this.nameProperty().set(name);
	}
	


	public StringProperty infoProperty() {
		return this.info;
	}
	


	public String getInfo() {
		return this.infoProperty().get();
	}
	


	public void setInfo(final String info) {
		this.infoProperty().set(info);
	}
	


	public StringProperty info2Property() {
		return this.info2;
	}
	


	public String getInfo2() {
		return this.info2Property().get();
	}
	


	public void setInfo2(final String info2) {
		this.info2Property().set(info2);
	}
	


	public StringProperty departmentProperty() {
		return this.department;
	}
	


	public String getDepartment() {
		return this.departmentProperty().get();
	}
	


	public void setDepartment(final String department) {
		this.departmentProperty().set(department);
	}
	


	public StringProperty workProperty() {
		return this.work;
	}
	


	public String getWork() {
		return this.workProperty().get();
	}
	


	public void setWork(final String work) {
		this.workProperty().set(work);
	}
	


	public StringProperty mobileProperty() {
		return this.mobile;
	}
	


	public String getMobile() {
		return this.mobileProperty().get();
	}
	


	public void setMobile(final String mobile) {
		this.mobileProperty().set(mobile);
	}
	


	public StringProperty emailProperty() {
		return this.email;
	}
	


	public String getEmail() {
		return this.emailProperty().get();
	}
	


	public void setEmail(final String email) {
		this.emailProperty().set(email);
	}

	public StringProperty tdmInitialsProperty() {
		return this.tdmInitials;
	}
	

	public String getTdmInitials() {
		return this.tdmInitialsProperty().get();
	}
	

	public void setTdmInitials(final String tdmInitials) {
		this.tdmInitialsProperty().set(tdmInitials);
	}
	
	
	
	
	
}
