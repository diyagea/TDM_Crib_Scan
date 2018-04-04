package application.view;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import application.Main;
import application.dao.StockDAO;
import application.dao.UserDAO;
import application.modal.Comp;
import application.modal.CostCenter;
import application.modal.User;
import application.util.Common;
import application.util.Config;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;

public class PhysicalCompScannerController {

	Logger logger = LogManager.getLogger(PhysicalCompScannerController.class);

	//Reference
	private Main mainApp;
	private User operator;
	private CostCenter toWhere;

	private ObservableList<Comp> compData = FXCollections.observableArrayList();
	private StockDAO stockDAO = new StockDAO();
	private UserDAO userDAO = new UserDAO();

	@FXML
	private Label operatorLabel;
	@FXML
	private Label toWhereLabel;
	@FXML
	private TextField inputTextField;
	@FXML
	private TableView<Comp> compTable;
	@FXML
	private TableColumn<Comp, String> compIDColumn = new TableColumn<Comp, String>();
	@FXML
	private TableColumn<Comp, String> physicalIDColumn = new TableColumn<Comp, String>();
	@FXML
	private TableColumn<Comp, String> desc1Column = new TableColumn<Comp, String>();
	@FXML
	private TableColumn<Comp, String> desc2Column = new TableColumn<Comp, String>();
	@FXML
	private TableColumn<Comp, String> costCenterColumn = new TableColumn<Comp, String>();
	@FXML
	private TableColumn<Comp, String> workplaceColumn = new TableColumn<Comp, String>();
	@FXML
	private TableColumn<Comp, String> stockplaceIDColumn = new TableColumn<Comp, String>();
	@FXML
	private TableColumn<Comp, String> stockplaceDescColumn = new TableColumn<Comp, String>();
	@FXML
	private ComboBox<CostCenter> cribBox;

	// config separator
	private String toolSep = null;
	private String userSep = null;
	private String cribSep = null;

	@FXML
	private void initialize() {
		//get config data
		Config con = new Config();
		toolSep = con.get("tool");
		userSep = con.get("user");
		cribSep = con.get("crib");

		// init combBox CostCenter Options
		List<CostCenter> cribList = null;
		try {
			cribList = stockDAO.queryCostCenters();
		} catch (SQLException e) {
			logger.error("query cost center error", e);
		}
		cribBox.getItems().addAll(cribList);

		// Initialize columns.
		compIDColumn.setCellValueFactory(cellData -> cellData.getValue().compIDProperty());
		physicalIDColumn.setCellValueFactory(cellData -> cellData.getValue().physicalIDProperty());
		desc1Column.setCellValueFactory(cellData -> cellData.getValue().desc1Property());
		desc2Column.setCellValueFactory(cellData -> cellData.getValue().desc2Property());
		costCenterColumn.setCellValueFactory(cellData -> cellData.getValue().costunitProperty());
		workplaceColumn.setCellValueFactory(cellData -> cellData.getValue().workplaceProperty());
		stockplaceIDColumn.setCellValueFactory(cellData -> cellData.getValue().stockplaceIDProperty());
		stockplaceDescColumn.setCellValueFactory(cellData -> cellData.getValue().stockplaceDescProperty());
	}

	/**
	 * key action method
	 * @param event
	 */
	@FXML
	private void pressKey(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER) {//scan and input a Enter
			getStockInfo();
		}
	}

	@FXML
	private void getStockInfo() {

		//load input text
		String inputText = inputTextField.getText();
		if (Common.isBlankStr(inputText)) {// check blank
			mainApp.alert(AlertType.WARNING, "输入格式错误", null, "输入框值不能为空！");
			return;
		}

		String compID = "";
		String physicalID = "";

		try {
			/** choose input type(issue|user|tool|crib) **/
			if (inputText.startsWith("!") || inputText.equals("issue")) {//input type : issue action
				issueConfirm();
			} else if (inputText.startsWith(cribSep)) {//input type : crib
				/** CostCenter **/
				// issue to where info, [#COSTUNIT#WORKPLACE]
				// String[] info = inputText.split(cribSep);
				try {
					// toWhere = stockDAO.queryCostCenter(info[1], info[2]);
					// toWhereLabel.setText(toWhere.getName());
					// toWhereLabel.setTextFill(Color.BLACK);
					// clear input field
					inputTextField.clear();
				} catch (Exception e) {
					mainApp.alert(AlertType.ERROR, "查询失败", null, "请检查库房信息是否正确，再重新扫码/输入！");
					logger.error("CostCenter Query Error, input text:[" + inputText + "]", e);
				}

			} else if (inputText.startsWith(userSep)) { //input type : user
				/** operator **/
				// user info, [@pincode]
				String pin = inputText.substring(userSep.length());
				try {
					operator = userDAO.queryUser(-1, pin, 2);
					operatorLabel.setText(operator.getName());
					operatorLabel.setTextFill(Color.BLACK);
					// clear input field
					inputTextField.clear();
				} catch (Exception e) {
					mainApp.alert(AlertType.ERROR, "查询失败", null, "请检查用户信息是否正确，再重新扫码/输入");
					logger.error("User Query Error, input text:[" + inputText + "]", e);
				}
			} else {
				/** physical comp **/
				// physical comp info, [COMPID|PHYSICALID]
				String[] info = inputText.split(toolSep);
				compID = info[0];
				physicalID = info[1];// java.lang.ArrayIndexOutOfBoundsException:  1
				try {
					//query from Database
					List<Comp> compList = stockDAO.queryCompStock(compID, physicalID);

					if (compList.size() == 0) {// no data in stock
						mainApp.alert(AlertType.WARNING, "未找到数据", null, "未找到刀具的库存，请在TDM中检查该刀具的库存信息！");
					} else {
						for (Comp c : compList) {
							if (compData.contains(c)) {
								mainApp.alert(AlertType.WARNING, "数据重复", null, "请勿重复扫码物理刀具！刀具编号：" + c.getCompID() + "，物理号：" + c.getPhysicalID());
							} else {
								compData.add(c);
							}
						}
						/** table show data **/
						compTable.setItems(compData);
						// clear input field
						inputTextField.clear();
					}
				} catch (Exception e) {
					mainApp.alert(AlertType.ERROR, "查询失败", null, "请检查刀具信息是否正确，再重新扫码/输入！");
					logger.error("Tool Query Error, input text:[" + inputText + "]", e);
				}
			}
		} catch (Exception e) {
			mainApp.alert(AlertType.ERROR, "格式错误", null, "请检查信息是否正确，再重新扫码/输入！");
			logger.error("Input Text Format Error, input text:[" + inputText + "]", e);
			return;
		}
	}

	/**
	 * issue
	 */
	@FXML
	private void issueConfirm() {
		// 1. check choose toWhere
		if (cribBox.getSelectionModel().getSelectedIndex() == -1) {
			mainApp.alert(AlertType.WARNING, "操作失败", null, "请先选择目的地成本中心！");
			return;
		}

		// 1. check scan toWhere(NOT USE)
		/*if (toWhereLabel.getText().contains("未扫描")) {
			mainApp.alert(AlertType.WARNING, "领取失败", null, "未扫描目的地成本中心条码！");
			//clear input field 
			inputTextField.clear();
			return;
		}*/

		// 2. check issue operator
		if (operatorLabel.getText().contains("未扫描") || operator == null || operator.getUserID() == 0) {
			mainApp.alert(AlertType.WARNING, "领取失败", null, "未扫描领取人条码！");
			// clear input field
			inputTextField.clear();
			return;
		}

		// 3. check comp list size
		if (compData.size() < 1) {
			mainApp.alert(AlertType.WARNING, "领取失败", null, "未扫描刀具，领取列表为空！");
			// clear input field
			inputTextField.clear();
			return;
		}

		// check items in the toWhere cost center
		String warningMsg = "";
		for (Comp comp : compData) {
			if (comp.getCostunit().equals(toWhere.getCostunit()) && comp.getWorkplace().equals(toWhere.getWorkplace())) {
				warningMsg += "[ " + comp.getCompID() + " | " + comp.getPhysicalID() + " ]\n";
			}
		}
		if (warningMsg.length() > 0) {
			Optional<ButtonType> result = mainApp.confirm("警告", "以下刀具已存在成本中心：[ " + toWhere.getName() + " ], 确认要继续领取吗？", warningMsg);
			if (result.get() != ButtonType.OK) {
				// ... user chose CANCEL or closed the dialog
				return;
			}
			logger.warn("已存在成本中心[ " + toWhere.getCostunit() + " - " + toWhere.getWorkplace() + " ]的刀具被领取, 已存在成本中心的刀具领取清单为：" + warningMsg);
		}

		// do issue action
		String errorMsg = "";
		String succMsg = "";
		int toWhereType = toWhere.getType();
		String tdmInitials = operator.getTdmInitials();
		String timestamp = Common.getTimestamp_TDM();
		DecimalFormat format = new DecimalFormat("#.000000");
		for (Comp comp : compData) {
			try {
				//init data ref 
				String compID = comp.getCompID();
				String physicalID = comp.getPhysicalID();
				String costunitFrom = comp.getCostunit();
				String workplaceFrom = comp.getWorkplace();
				String costunitTo = toWhere.getCostunit();
				String workplaceTo = toWhere.getWorkplace();
				String stockplaceID = comp.getStockplaceID();
				int countNew = comp.getCountNew();
				int countUsed = comp.getCountUsed();
				
				/**************Do Issue*****************/
				//1. create new cancel key
				BigDecimal cancelKey = stockDAO.newCancelKey();
				
				//2. add cancel base key
				stockDAO.addCancelKey(cancelKey);
				//cancel steps key : cancelKey + 0.00100
				cancelKey = new BigDecimal(format.format(cancelKey.add(new BigDecimal(0.001))));

				//3. add cancel detail data
				stockDAO.addCancelDetail(comp.getCostCenterType(), timestamp, cancelKey, compID, physicalID, costunitFrom, workplaceFrom, stockplaceID, -countNew, -countUsed);
				stockDAO.addCancelDetail(toWhereType, timestamp, cancelKey, compID, physicalID, costunitTo, workplaceTo, stockplaceID, 0, 1);
				
				//4. do issue action in database, update stock cost center with toWhere
				if(countUsed==0 && countNew>0){
					//new tool, after issue countNew become countUsed
					stockDAO.issueWithCancel(timestamp, cancelKey, compID, physicalID, costunitTo, workplaceTo, countUsed, countNew);
				}else{
					//used tool
					stockDAO.issueWithCancel(timestamp, cancelKey, compID, physicalID, costunitTo, workplaceTo, countNew, countUsed);
				}

				//5. add Consumption Evaluation Record
				stockDAO.addConsumptionWithCancel(timestamp, cancelKey, compID, physicalID, costunitTo, workplaceTo, countNew, countUsed);
				
				//6. add History Base Record 
				stockDAO.addHistoryBase(-3, timestamp, cancelKey, compID, costunitFrom, workplaceFrom, costunitTo, workplaceTo, tdmInitials, countNew, countUsed);
				
				//7. add History Base List Record 
				//From
				stockDAO.addHistoryBaseList(timestamp, cancelKey, physicalID, stockplaceID, -countNew, -countUsed, countNew, countUsed);
				//To
				if(countUsed==0 && countNew>0){
					//new tool, after issue countNew become countUsed
					stockDAO.addHistoryBaseList(timestamp, cancelKey, physicalID, stockplaceID, countUsed, countNew, 0, 0);
				}else{
					//used tool
					stockDAO.addHistoryBaseList(timestamp, cancelKey, physicalID, stockplaceID, countNew, countUsed, 0, 0);
				}

				/**************Do Issue End*****************/

				succMsg += "CompID:[" + comp.getCompID() + "], PhysicalID:[" + comp.getPhysicalID() + "]\n";
			} catch (Exception e) {
				logger.error("issue exception, CompID:[" + comp.getCompID() + "], PhysicalID:[" + comp.getPhysicalID() + "]" + e.getMessage(), e);
				errorMsg += "CompID:[" + comp.getCompID() + "], PhysicalID:[" + comp.getPhysicalID() + "]\n";
			}
		}

		// error alert
		if (errorMsg.length() > 0) {
			mainApp.alert(AlertType.ERROR, "发生错误", "以下刀具领取失败！", errorMsg);
		} else {
			logger.info("Issue Successfully, To CostCenter[" + toWhere.getCostunit() + " - " + toWhere.getWorkplace() + "], Operator[" + operator.getName() + "], CompList:\n[" + succMsg + "]");
			mainApp.alert(AlertType.INFORMATION, "信息", null, "领取完成！");
		}

		//init all
		clearAll();
	}

	/*
	 * clear all control
	 */
	private void clearAll() {
		// init clear
		inputTextField.clear();
		compData.clear();
		compTable.setItems(compData);
		operatorLabel.setText(" [ 未扫描领取人条码 ]");
		operatorLabel.setTextFill(Color.RED);
		cribBox.getSelectionModel().select(-1);
		// toWhereLabel.setText("[ 未扫描目的地成本中心 ]");
		// toWhereLabel.setTextFill(Color.RED);
	}

	/**
	 * delete chosen item in scan tool list
	 */
	@FXML
	private void handleDeleteComp() {
		int selectedIndex = compTable.getSelectionModel().getSelectedIndex();
		if (selectedIndex >= 0) {
			compTable.getItems().remove(selectedIndex);
		} else {
			mainApp.alert(AlertType.ERROR, "移除失败", null, "未选择数据！");
		}
	}

	/**
	 * choose cost center and get object
	 */
	@FXML
	private void chooseCostCenter() {
		// chosen crib
		toWhere = cribBox.getSelectionModel().getSelectedItem();
	}

	/**
	 * Is called by the main application to give a reference back to itself.
	 */
	public void setMainApp(Main mainApp) {
		this.mainApp = mainApp;
		// check separator
		if (Common.isBlankStr(cribSep) || Common.isBlankStr(toolSep) || Common.isBlankStr(userSep)) {
			mainApp.alert(AlertType.ERROR, "配置错误", null, "配置信息为空，请联系相关人员，检查配置文件内容！");
			logger.error("Separator Config Error: " + "User=[" + userSep + "], Crib=[" + cribSep + "], Tool=[" + toolSep + "]");
			inputTextField.setDisable(true);
		}

		inputTextField.requestFocus();
	}
}
