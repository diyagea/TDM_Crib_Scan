package application.view;

import application.Main;
import application.modal.User;
import application.util.Common;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

/**
 * Dialog to edit details of a person.
 * 
 * @author Marco Jakob
 */
public class PersonEditDialogController {
	
	private Main main;

	@FXML
	private Label titleLabel;
	
    @FXML
    private TextField usernameInput;
    @FXML
    private TextField passwordInput;
    @FXML
    private TextField nameInput;
    @FXML
    private TextField tdmInitialsInput;
    @FXML
    private TextField pincodeInput;
    @FXML
    private TextField mobileInput;
    @FXML
    private TextField emailInput;
    @FXML
    private TextField departmentInput;
    @FXML
    private TextField workInput;
    @FXML
    private TextArea infoInput;
    @FXML
    private TextArea info2Input;
    @FXML
    private RadioButton adminRadio;
    @FXML
    private RadioButton userRadio;
    @FXML
    private RadioButton managerRadio;

    private ToggleGroup radioGroup;
    
    private Stage dialogStage;
    private User user;
    private boolean okClicked = false;

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
    	//set radio group
    	radioGroup = new ToggleGroup();
    	adminRadio.setToggleGroup(radioGroup);
    	managerRadio.setToggleGroup(radioGroup);
    	userRadio.setToggleGroup(radioGroup);
    	//set user data
    	adminRadio.setUserData(0);
    	managerRadio.setUserData(1);
    	userRadio.setUserData(2);
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
     * Sets the person to be edited in the dialog.
     * 
     * @param user 
     * type : 0 : add  1 ： update
     */
    public void setPerson(User user, int type) {
        this.user = user;
        usernameInput.setText(user.getUsername());
        passwordInput.setText(user.getPassword().replaceAll(".", "*"));
        pincodeInput.setText(user.getPincode());
        nameInput.setText(user.getName());
        tdmInitialsInput.setText(user.getTdmInitials());
        mobileInput.setText(user.getMobile());
        emailInput.setText(user.getEmail());
        departmentInput.setText(user.getDepartment());
        workInput.setText(user.getWork());
        infoInput.setText(user.getInfo());
        info2Input.setText(user.getInfo2());
        
    	if(type == 0){//add
    		radioGroup.selectToggle(userRadio);
    	}else{//update
    		//username can not change
    		//usernameInput.setDisable(true);
    		//usernameInput.setEditable(false);
    		//passwordInput.setEditable(false);
    		
    		//radio
    		switch (user.getType()) {
    		case 0:
    			radioGroup.selectToggle(adminRadio);
    			break;
    		case 1:
    			radioGroup.selectToggle(managerRadio);
    			break;
    		case 2:
    			radioGroup.selectToggle(userRadio);
    			break;
    		default:
    			radioGroup.selectToggle(userRadio);
    			break;
    		}
    	}
    }

    /**
     * Returns true if the user clicked OK, false otherwise.
     * 
     * @return
     */
    public boolean isOkClicked() {
        return okClicked;
    }

    /**
     * Called when the user clicks ok.
     */
    @FXML
    private void handleOk() {
        if (isInputValid()) {
            
        	user.setUsername(usernameInput.getText());
        	if(!passwordInput.getText().contains("*")){
        		user.setPassword(passwordInput.getText());
        	}
        	user.setName(nameInput.getText());
        	user.setPincode(pincodeInput.getText());
        	user.setTdmInitials(tdmInitialsInput.getText());
        	user.setMobile(mobileInput.getText());
        	user.setEmail(emailInput.getText());
        	user.setDepartment(departmentInput.getText());
        	user.setWork(workInput.getText());
        	user.setInfo(infoInput.getText());
        	user.setInfo2(info2Input.getText());
        	
        	user.setType((Integer)radioGroup.getSelectedToggle().getUserData());
        	
            okClicked = true;
            dialogStage.close();
        }
    }

    /**
     * Called when the user clicks cancel.
     */
    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    /**
     * Validates the user input in the text fields.
     * 
     * @return true if the input is valid
     */
    private boolean isInputValid() {
        String errorMessage = "";
        
        /*if (Common.checkNull(usernameInput.getText())) {
            errorMessage += "用户名不能为空！\n"; 
        }
        if (Common.checkNull(passwordInput.getText())) {
            errorMessage += "密码不能为空！\n"; 
        }
        if(usernameInput.getText().length() < 4 || passwordInput.getText().length() < 4){
        	errorMessage += "帐号或密码不能少于4位字符！\n"; 
        }
        */
        if (Common.isNull(nameInput.getText())) {
            errorMessage += "真实姓名不能为空！\n"; 
        }

        if (Common.isNull(tdmInitialsInput.getText())) {
            errorMessage += "TDM缩写不能为空！\n"; 
        }
        
        if (Common.isNull(pincodeInput.getText())) {
            errorMessage += "员工ID号不能为空！\n"; 
        }
        
        /*if (Common.checkNull(mobileInput.getText())) {
            errorMessage += "手机号码不能为空！\n"; 
        }*/

       

        if (errorMessage.length() == 0) {
            return true;
        } else {
	        main.alert(AlertType.ERROR, "Error Message", "参数输入错误！", "请检查以下参数错误信息：\n"+errorMessage);
            return false;
        }
    }

	public void setMain(Main main) {
		this.main = main;
	}
    
    
}