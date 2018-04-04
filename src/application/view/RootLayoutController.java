package application.view;

import application.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

/**
 * Root View
 */
public class RootLayoutController {

	// Reference to the main application
	private Main main;
	
	@FXML
	private MenuItem physicalItemManage;
	@FXML
	private MenuItem physicalNewEntry;
	@FXML
	private MenuItem userManage;
	

	@FXML
	private void initialize() {
	}
	
	/**
	 * show physical scanner operate
	 */
	@FXML
	private void showPhysicalScanner(){
		main.initPhysicalCompScanner();
	}
	
	/**
	 * show physical scanner new entry
	 */
	@FXML
	private void showPhysicalNewEntry(){
		main.initPhysicalCompNewEntry();
	}
	
	@FXML
	private void userManagement (){
		if(main.getLoginUser().getType() == 0){
			//show user management view
			main.showPersonOverview();
		}
	}
	
	/**
	 * Opens an about dialog.
	 */
	@FXML
	private void handleAbout() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("TDM Crib Scanner");
		alert.setHeaderText("Application About");
		alert.setContentText("TDM Systems GmbH.\n\n" + "Author : Allen Wang\n" + "Email: service@tooling-intelligence.com\n\n" + "Thanks for your approval.");

		// Set the icon (must be included in the project).
		ImageView imgView = new ImageView("file:resources/img/LogoV4.png");
		imgView.setFitWidth(100);
		imgView.setFitHeight(100);
		alert.setGraphic(imgView);
		
		// Get the Stage.
		Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
		
		// Add a custom icon.
		Image img = new Image("file:resources/img/LogoV4.png");
		stage.getIcons().add(img);
		alert.showAndWait();
	}

	/**
	 * Closes the application.
	 */
	@FXML
	private void handleExit() {
		System.exit(0);
	}

	/**
	 * get Main ref
	 * @param main
	 */
	public void setMainApp(Main main) {
		this.main = main;
		
		//accounding to user type show the menu
		switch (main.getLoginUser().getType()) {
		case 0://admin
			showMenu(true, true);
			break;
		case 1://crib manager
			showMenu(false, true);
			break;
		case 2://general user
			showMenu(false, false);
			break;
		default:
			break;
		}
	}
	
	/**
	 * menu switch method
	 * @param userFlag user manager
	 * @param cribFlag crib manager
	 */
	public void showMenu(boolean userFlag, boolean cribFlag){
		
		userManage.setVisible(userFlag);
		userManage.setDisable(!userFlag);
		
		physicalItemManage.setVisible(cribFlag);
		physicalItemManage.setDisable(!cribFlag);
		
		physicalNewEntry.setVisible(cribFlag);
		physicalNewEntry.setDisable(!cribFlag);
		
	}
	
}
