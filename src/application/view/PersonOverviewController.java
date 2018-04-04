package application.view;


import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import application.Main;
import application.dao.UserDAO;
import application.modal.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class PersonOverviewController {

	Logger logger = LogManager.getLogger(PersonOverviewController.class);

	private UserDAO userDAO = new UserDAO();
	
    @FXML
    private TableView<User> personTable;
    @FXML
    private TableColumn<User, String> nameColumn;
    @FXML
    private TableColumn<User, String> usernameColumn;
    @FXML
    private TableColumn<User, String> tdmInitialsColumn;
    

    @FXML
    private Label usernameLabel;
    @FXML
    private Label passwordLabel;
    @FXML
    private Label nameLabel;
    @FXML
    private Label typeLabel;
    @FXML
    private Label pincodeLabel;
    @FXML
    private Label tdmInitialsLabel;
    @FXML
    private Label departmentLabel;
    @FXML
    private Label workLabel;
    @FXML
    private Label mobileLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Label infoLabel;
    @FXML
    private Label info2Label;

    // Reference to the main application.
    private Main main;

    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public PersonOverviewController() {
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
    	
    	//init user table
    	showAllUser();
    	
    	// Initialize the person table with the two columns.
        nameColumn.setCellValueFactory(
        		cellData -> cellData.getValue().nameProperty());
        usernameColumn.setCellValueFactory(
        		cellData -> cellData.getValue().pincodeProperty());
        tdmInitialsColumn.setCellValueFactory(
        		cellData -> cellData.getValue().tdmInitialsProperty());
        
        // Clear person details.
        showPersonDetails(null);

        // Listen for selection changes and show the person details when changed.
		personTable.getSelectionModel().selectedItemProperty().addListener(
				(observable, oldValue, newValue) -> showPersonDetails(newValue));
    }

    /**
     * Is called by the main application to give a reference back to itself.
     * 
     * @param main
     */
    public void setMainApp(Main main) {
        this.main = main;

        // Add observable list data to the table
        //personTable.setItems(main.getPersonData());
    }
    
    /**
     * show all user in table
     */
    private void showAllUser(){
    	ObservableList<User> userList = FXCollections.observableArrayList();
    	try {
			userList.addAll(userDAO.getAll());
		} catch (Exception e) {
			logger.error("query all user error", e);
		}
    	personTable.setItems(userList); 
    }
    
    /**
     * Fills all text fields to show details about the person.
     * If the specified person is null, all text fields are cleared.
     * 
     * @param person the person or null
     */
    private void showPersonDetails(User user) {
    	if (user != null) {
    		// Fill the labels with info from the person object.
    		usernameLabel.setText(user.getUsername());
    		// replace password by *
    		passwordLabel.setText(user.getPassword().replaceAll(".", "*"));
    		nameLabel.setText(user.getName());
    		
    		//user type desc
    		switch (user.getType()) {
			case 0:
				typeLabel.setText("超级管理员");
				break;
			case 1:
				typeLabel.setText("库房人员");
				break;
			case 2:
				typeLabel.setText("一般用户");
				break;
			default:
				break;
			}
    		
    		pincodeLabel.setText(user.getPincode());
    		tdmInitialsLabel.setText(user.getTdmInitials());
    		departmentLabel.setText(user.getDepartment());
    		workLabel.setText(user.getWork());
    		mobileLabel.setText(user.getMobile());
    		emailLabel.setText(user.getEmail());
    		infoLabel.setText(user.getInfo());
    		info2Label.setText(user.getInfo2());
    	} else {
    		// Person is null, remove all the text.
    		usernameLabel.setText("");
    		passwordLabel.setText("");
    		nameLabel.setText("");
    		typeLabel.setText("");
    		pincodeLabel.setText("");
    		tdmInitialsLabel.setText("");
    		departmentLabel.setText("");
    		workLabel.setText("");
    		mobileLabel.setText("");
    		emailLabel.setText("");
    		infoLabel.setText("");
    		info2Label.setText("");
    	}
    }

	/**
	 * Called when the user clicks the new button. Opens a dialog to edit
	 * details for a new person.
	 */
	@FXML
	private void handleNewPerson() {
		User tempPerson = new User();
		boolean okClicked = main.showPersonEditDialog(tempPerson, 0);
		if (okClicked) {
			//Add user
			try {
				int sql_result = userDAO.add(tempPerson);
				if(sql_result > 0){
					main.alert(AlertType.INFORMATION, "Infomation", null, "添加用户成功！");
					//reload user table
					showAllUser();
				}
			} catch (Exception e) {
				main.exceptionDialog("添加用户失败！", e);
				logger.error("add user error");
				logger.error(e.getMessage(), e);
			}
			showPersonDetails(tempPerson);
		}
	}

	/**
	 * Called when the user clicks the edit button. Opens a dialog to edit
	 * details for the selected person.
	 */
	@FXML
	private void handleEditPerson() {
		User selectedPerson = personTable.getSelectionModel().getSelectedItem();
		if (selectedPerson != null) {
			boolean okClicked = main.showPersonEditDialog(selectedPerson, 1);
			if (okClicked) {
				//update user
				try {
					int sql_result = userDAO.update(selectedPerson);
					if(sql_result > 0){
						main.alert(AlertType.INFORMATION, "Infomation", null, "修改信息成功！");
						//reload user table
						showAllUser();
					}
				} catch (Exception e) {
					main.exceptionDialog("修改信息失败！", e);
					logger.error("update user error");
					logger.error(e.getMessage(), e);
				}
				showPersonDetails(selectedPerson);
			}

		} else {
			// Nothing selected.
			main.alert(Alert.AlertType.ERROR, "ERROR", null, "请先选择一行！");
		}
	}
	
	/**
	 * Called when the user clicks on the delete button.
	 */
	@FXML
	private void handleDeletePerson() {
		User selectedPerson = personTable.getSelectionModel().getSelectedItem();
		
		if (selectedPerson != null) {
			Optional<ButtonType> result = main.confirm("Delete Person", null, "确认删除该用户吗？");
			if (result.get() == ButtonType.OK) {
				//delete user 
				try {
					int sql_result = userDAO.delete(selectedPerson);
					if(sql_result > 0){
						main.alert(AlertType.INFORMATION, "Infomation", null, "删除用户成功！");
						//reload user table
						showAllUser();
					}
				} catch (Exception e) {
					main.exceptionDialog("删除用户失败！", e);
					logger.error("delete user error");
					logger.error(e.getMessage(), e);
				}
			}
		} else {
			main.alert(Alert.AlertType.ERROR, "ERROR", null, "请先选择一行！");
		}
	}
}