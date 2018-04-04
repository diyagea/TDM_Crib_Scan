package application;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.exe4j.Controller;
import com.exe4j.Controller.StartupListener;
import com.exe4j.runtime.splash.AwtSplashScreen;

import application.modal.User;
import application.util.JdbcHelper;
import application.view.LoginController;
import application.view.PersonEditDialogController;
import application.view.PersonOverviewController;
import application.view.PhysicalCompScannerController;
import application.view.PhysicalNewEntryController;
import application.view.RootLayoutController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Main extends Application implements StartupListener{

	Logger logger = LogManager.getLogger(Main.class);

	private Stage primaryStage;
	private BorderPane rootLayout;
	private Image TDMIcon;
	
	private User loginUser;
	
	
	//main method
	public static void main(String[] args) {
		launch(args);
	}

	//For close EXE4j SplashScreen implements StartupListener
	@Override
	public void startupPerformed(String arg0) {
		
	}
	
	//launch UI
	@Override
	public void start(Stage primaryStage) {
		try {
			
			//check database connection
			if(!JdbcHelper.testConn()){
				this.alert(AlertType.ERROR, "Connection Error", null, "Can not connect to Database, Please check DataBase config file.");
				return;
			}
			
			//Close EXE4j SplashScreen
			try {
				//EXE4j api
				Controller.registerStartupListener(this);
				Controller.hide();
				//hide splash screen[导出exe是要取消注释]
				AwtSplashScreen ss = new AwtSplashScreen();
				ss.hideScreen();
			} catch (Exception e) {}//ignore exception
			

			//init
			this.primaryStage = primaryStage;
			this.TDMIcon = new Image("file:resources/img/LogoV4.png");
			
			//check login
			if (initLogin()) {//success
				// set UI property( title and icon )
				this.primaryStage.setTitle("TDM Crib Scanner - Login User : [ "+loginUser.getName()+" ]");
				this.primaryStage.getIcons().add(TDMIcon);
				this.primaryStage.setResizable(false);
				this.primaryStage.setMinWidth(800);
				this.primaryStage.setMinHeight(550);
				
				//init root view
				initRootLayout();
			
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Initializes the Login View.
	 */
	public boolean initLogin() {
		try {
			// Load the fxml file and create a new stage for the popup dialog.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("view/Login.fxml"));
			AnchorPane page = (AnchorPane) loader.load();

			// Create the dialog Stage.
			Stage dialogStage = new Stage();
			dialogStage.setTitle("TDM Crib Scanner Login");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.getIcons().add(TDMIcon);
			dialogStage.setAlwaysOnTop(true);
			dialogStage.setResizable(false);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);

			//load login controller
			LoginController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setMainApp(this);

			// Show the dialog and wait until the user closes it
			dialogStage.showAndWait();

			return controller.isOkClicked();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return false;
		}
	}

	/**
	 * Initializes the root layout.
	 */
	public void initRootLayout() {
		try {
			// Load root layout from fxml file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("view/RootLayout.fxml"));
			rootLayout = (BorderPane) loader.load();

			// Show the scene containing the root layout.
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);

			// Give the controller access to the main app.
			RootLayoutController controller = loader.getController();
			controller.setMainApp(this);

			primaryStage.show();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Init View - Physical Item Scanner.
	 */
	public void initPhysicalCompScanner() {
		try {
			// Load person overview.
			FXMLLoader loader = new FXMLLoader();

			loader.setLocation(Main.class.getResource("view/PhysicalCompScanner.fxml"));

			AnchorPane operateScanner = (AnchorPane) loader.load();

			// Give the controller access to the main app.
			PhysicalCompScannerController controller = loader.getController();
			controller.setMainApp(this);
			
			// Set person overview into the center of root layout.
			rootLayout.setCenter(operateScanner);

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Shows the new entry phusical inside the root layout.
	 */
	public void initPhysicalCompNewEntry() {
		try {
			// Load person overview.
			FXMLLoader loader = new FXMLLoader();

			loader.setLocation(Main.class.getResource("view/PhysicalNewEntry.fxml"));

			AnchorPane operateScanner = (AnchorPane) loader.load();

			// Give the controller access to the main app.
			PhysicalNewEntryController controller = loader.getController();
			controller.setMainApp(this);

			// Set person overview into the center of root layout.
			rootLayout.setCenter(operateScanner);

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	
	
	/**
	 * Shows the person overview inside the root layout.
	 */
	public void showPersonOverview() {
		try {
			// Load person overview.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("view/PersonOverview.fxml"));
			AnchorPane personOverview = (AnchorPane) loader.load();

			// Set person overview into the center of root layout.
			rootLayout.setCenter(personOverview);

			// Give the controller access to the main app.
			PersonOverviewController controller = loader.getController();
			controller.setMainApp(this);

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Opens a dialog to edit details for the specified person. If the user
	 * clicks OK, the changes are saved into the provided person object and true
	 * is returned.
	 * 
	 * @param user
	 *            the person object to be edited
	 *            
	 * 			type 0：add 1:update
	 * @return true if the user clicked OK, false otherwise.
	 */
	public boolean showPersonEditDialog(User user, int type) {
		try {
			// Load the fxml file and create a new stage for the popup dialog.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("view/PersonEditDialog.fxml"));
			AnchorPane page = (AnchorPane) loader.load();

			// Create the dialog Stage.
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Edit Person");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			dialogStage.getIcons().add(TDMIcon);
			dialogStage.setResizable(false);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);

			// Set the person into the controller.
			PersonEditDialogController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setPerson(user, type);
			controller.setMain(this);

			// Show the dialog and wait until the user closes it
			dialogStage.showAndWait();

			return controller.isOkClicked();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return false;
		}
	}
	
	
	/**********************Dialog*************************/

	/**
	 * alert dialog without owner
	 * 
	 * @param type
	 * @param title
	 * @param header
	 * @param content
	 */
	public void alert(AlertType type, String title, String header, String content) {
		alert(type, title, header, content, null);
	}

	/**
	 * alert dialog with owner
	 * 
	 * @param type
	 * @param title
	 * @param header
	 * @param content
	 */
	public void alert(AlertType type, String title, String header, String content, Stage owner) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		// Get the Stage.
		Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();

		// Add a custom icon.
		Image img = TDMIcon;
		stage.getIcons().add(img);
		// owner
		alert.initOwner(owner);

		alert.show();
	}

	/**
	 * alert confirm
	 * @param title
	 * @param header
	 * @param content
	 * @return
	 */
	public Optional<ButtonType> confirm(String title, String header, String content) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		// Get the Stage.
		Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();

		// Add a custom icon.
		Image img = TDMIcon;
		stage.getIcons().add(img);
		
		return alert.showAndWait();
		/*Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK) {
			// ... user chose OK
			System.out.println("Confirm");
		} else {
			// ... user chose CANCEL or closed the dialog
			System.out.println("CANCEL");
		}*/
	}
	
	/**
	 * exception dialog
	 * @param header
	 * @param e
	 */
	public void exceptionDialog(String header, Exception e){
    	Alert alert = new Alert(AlertType.ERROR);
    	alert.setTitle("Exception Dialog");
    	alert.setHeaderText(header);
    	alert.setContentText("查看并记录错误信息，与技术人员联系解决！");

    	// Create expandable Exception.
    	StringWriter sw = new StringWriter();
    	PrintWriter pw = new PrintWriter(sw);
    	e.printStackTrace(pw);
    	String exceptionText = sw.toString();

    	Label label = new Label("The exception stacktrace was:");

    	TextArea textArea = new TextArea(exceptionText);
    	textArea.setEditable(false);
    	textArea.setWrapText(true);

    	textArea.setMaxWidth(Double.MAX_VALUE);
    	textArea.setMaxHeight(Double.MAX_VALUE);
    	GridPane.setVgrow(textArea, Priority.ALWAYS);
    	GridPane.setHgrow(textArea, Priority.ALWAYS);

    	GridPane expContent = new GridPane();
    	expContent.setMaxWidth(Double.MAX_VALUE);
    	expContent.add(label, 0, 0);
    	expContent.add(textArea, 0, 1);

    	// Set expandable Exception into the dialog pane.
    	alert.getDialogPane().setExpandableContent(expContent);

    	alert.showAndWait();
    }
	
	public void showTooltip(String content, boolean autoHide){
		Tooltip tooltip = new Tooltip();
		tooltip.setText(content);
		tooltip.setAutoHide(autoHide);
		tooltip.centerOnScreen();
		tooltip.show(primaryStage);
	}
	
	/**********************Dialog-END*************************/

	public User getLoginUser() {
		return loginUser;
	}

	public void setLoginUser(User loginUser) {
		this.loginUser = loginUser;
	}
}
