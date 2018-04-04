package application.view;

import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import application.Main;
import application.dao.UserDAO;
import application.modal.User;
import application.util.Config;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class LoginController {
	
	Logger logger = LogManager.getLogger(LoginController.class);
	
	// main ref
	private Main mainApp;

	private int loginType=0;
	@FXML
	private Hyperlink scanLink;
	@FXML
	private Label usercodeLabel;
	@FXML 
	private TextField codeInput;
	@FXML
	private TextField usernameInput;
	@FXML
	private TextField passwordInput;
	@FXML
	private Label usernameLabel;
	@FXML
	private Label passwordLabel;
	
	
	private Stage dialogStage;
	private boolean loginFLag = false;

	private UserDAO userDAO = new UserDAO();
	
	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {
	}

	@FXML
	private void changeLoginType(){
		if(loginType == 0){//input login
			loginType = 1;
			
			scanLink.setText("帐号登录");
			codeInput.setVisible(true);
			usercodeLabel.setVisible(true);

			usernameLabel.setVisible(false);
			passwordLabel.setVisible(false);
			usernameInput.setVisible(false);
			passwordInput.setVisible(false);
		}else{//scan login
			loginType = 0;
			
			scanLink.setText("扫码登录");
			codeInput.setVisible(false);
			usercodeLabel.setVisible(false);

			usernameLabel.setVisible(true);
			passwordLabel.setVisible(true);
			usernameInput.setVisible(true);
			passwordInput.setVisible(true);
		}
	}
	
	/**
	 * press keyborad on username testfield.
	 */
	@FXML
	private void doEnter(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER) {
			//press Enter, to focus next textfield password
			passwordInput.requestFocus();
		}
		if (event.getCode() == KeyCode.ESCAPE) {
			//ESC close
			dialogStage.close();
		}
	}

	/**
	 * Login Action
	 * press keyborad on password testfield.
	 */
	@FXML
	private void doLogin(KeyEvent event) {
		//if press enter execute login action
		if (event.getCode() == KeyCode.ENTER) {
			//input user account login
			if(loginType == 0){
				//load username & password
				String username = usernameInput.getText();
				String password = passwordInput.getText();
				
				logger.info("User Input Account Login: username=["+username+"]");

				//check account
				if(checkLogin(username, password)){
					//Login Successfully
					loginFLag = true;
					
					try {
						//query login user
						User loginUser = userDAO.queryUser(0, username, 1);
						//set back to main app
						mainApp.setLoginUser(loginUser);

						logger.info("User["+loginUser.getName()+"] Login Successfully");
					} catch (SQLException e) {
						logger.error("query user error, username:"+username);
					}
					
					dialogStage.close();
				}else{
					//Login fail
					logger.info("User["+username+"] Login Failed");
					//alert with owner object
					mainApp.alert(AlertType.ERROR, "登录失败", null, "帐号或密码错误，请重新输入！", dialogStage);
				}
			}else{// scan user code login
				Config con = new Config();
				String userSep = con.get("user");
				String usercode = null;
				try{
					usercode = codeInput.getText().substring(userSep.length());
				}catch(Exception e){
					logger.error("User code format error, usercode:["+usercode+"]", e);
					//alert with owner object
					mainApp.alert(AlertType.ERROR, "登录失败", null, "扫码登录失败，请检查用户码后重新扫描！", dialogStage);
					return;
				}
				
				User loginUser = null;
				try{
					loginUser = userDAO.queryUser(-1, usercode, 2);
				}catch(SQLException e){
					logger.error("Scan login query error, usercode:["+usercode+"]", e);
				}

				//check
				if(loginUser != null && loginUser.getUserID() != 0){
					loginFLag = true;
					
					//set back to application
					mainApp.setLoginUser(loginUser);
					//log
					logger.info("User["+loginUser.getName()+"] Login Successfully");
					
					dialogStage.close();
				}else{
					// login fail
					logger.info("Usercode["+usercode+"] Scan Login Failed");
					//alert with owner object
					mainApp.alert(AlertType.ERROR, "登录失败", null, "扫码登录失败，请检查用户码后重新扫描！",dialogStage);
				}
			}
		}
		
		//ESC close dialog
		if (event.getCode() == KeyCode.ESCAPE) {
			dialogStage.close();
		}
	}

	/**
	 * checkLogin
	 * @return
	 */
	private boolean checkLogin(String username, String password){
		try {
			return userDAO.checkUser(username, password);
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			return false;
		}
	}
	
	/**
	 * Sets the stage of this dialog.
	 * 
	 * @param dialogStage
	 */
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

    /**
     * Returns true if the user clicked OK, false otherwise.
     * 
     * @return
     */
    public boolean isOkClicked() {
        return loginFLag;
    }
    
	/**
	 * Is called by the main application to give a reference back to itself.
	 */
	public void setMainApp(Main mainApp) {
		this.mainApp = mainApp;
	}
    
}
