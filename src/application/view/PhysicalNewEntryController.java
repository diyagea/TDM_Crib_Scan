package application.view;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import application.Main;
import application.dao.StockDAO;
import application.modal.Comp;
import application.modal.CostCenter;
import application.modal.User;
import application.util.Common;
import application.util.Config;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class PhysicalNewEntryController {
	Logger logger = LogManager.getLogger(PhysicalNewEntryController.class);

	//tool separator
	private String toolSep;

	//ref
	private User operator;
	private Main mainApp;

	//DAO
	private StockDAO stockDAO = new StockDAO();

	// UI control
	@FXML
	private TextField inputTextField;
	@FXML
	private ComboBox<CostCenter> cribBox;
	@FXML
	private ComboBox<String> stockBox;
	@FXML
	private TableView<Comp> compTable;
	@FXML
	private TableColumn<Comp, String> compIDColumn;
	@FXML
	private TableColumn<Comp, String> physicalIDColumn;
	@FXML
	private TableColumn<Comp, String> desc1Column;
	@FXML
	private TableColumn<Comp, String> desc2Column;
	@FXML
	private TableColumn<Comp, String> costunitColumn;
	@FXML
	private TableColumn<Comp, String> workplaceColumn;
	@FXML
	private TableColumn<Comp, String> stockplaceIDColumn;
	@FXML
	private TableColumn<Comp, String> stockplaceDescColumn;
	@FXML
	private Button clearButton;

	@FXML
	private void initialize() {
		//load config separator
		Config con = new Config();
		toolSep = con.get("tool");

		//init combBox cribOptions
		List<CostCenter> cribList = null;
		try {
			cribList = stockDAO.queryCostCenters(1);
		} catch (SQLException e) {
			logger.error("cost center query error", e);
		}
		cribBox.getItems().addAll(cribList);

		// Initialize columns.
		compIDColumn.setCellValueFactory(cellData -> cellData.getValue().compIDProperty());
		physicalIDColumn.setCellValueFactory(cellData -> cellData.getValue().physicalIDProperty());
		desc1Column.setCellValueFactory(cellData -> cellData.getValue().desc1Property());
		desc2Column.setCellValueFactory(cellData -> cellData.getValue().desc2Property());
		costunitColumn.setCellValueFactory(cellData -> cellData.getValue().costunitProperty());
		workplaceColumn.setCellValueFactory(cellData -> cellData.getValue().workplaceProperty());
		stockplaceIDColumn.setCellValueFactory(cellData -> cellData.getValue().stockplaceIDProperty());
		stockplaceDescColumn.setCellValueFactory(cellData -> cellData.getValue().stockplaceDescProperty());
	}

	/**
	 * auto load stocks after choose crib
	 */
	@FXML
	private void loadStocks() {
		//must choose crib firstly
		if (cribBox.getSelectionModel().getSelectedIndex() == -1) {
			mainApp.alert(AlertType.ERROR, "操作失败", null, "请先选择入库库房！");
			return;
		}

		//get chosen crib object
		CostCenter chooseCrib = cribBox.getSelectionModel().getSelectedItem();
		try {
			List<String> stockList = stockDAO.queryStocks(chooseCrib.getCostunit(), chooseCrib.getWorkplace());
			stockBox.setDisable(false);
			stockBox.getItems().clear();//clear other stock in last time
			stockBox.getItems().addAll(stockList);
		} catch (SQLException e) {
			logger.info("Query Stock List Error, Crib:[" + chooseCrib.getCostunit() + "]-[" + chooseCrib.getWorkplace() + "]", e);
		}
	}

	/**
	 * press key action method
	 * @param event
	 */
	@FXML
	private void pressKey(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER) {//scan code
			checkNewEntry();
		}
	}

	@SuppressWarnings("rawtypes")
	private void checkNewEntry() {
		//check if choose crib
		if (cribBox.getSelectionModel().getSelectedIndex() == -1) {
			mainApp.alert(AlertType.ERROR, "操作失败", null, "未选择库房！");
			return;
		}
		//check if choose stock place
		if (stockBox.getSelectionModel().getSelectedIndex() == -1) {
			/*Optional<ButtonType> result = mainApp.confirm("警告", null, "未选择库位，确定继续入库吗？");
			if (result.get() != ButtonType.OK) {
				return;
			}*/
			mainApp.alert(AlertType.ERROR, "操作失败", null, "未选择库位！");
			return;
		}
		//check scan input text
		String inputText = inputTextField.getText();
		if (Common.isBlankStr(inputText)) {
			mainApp.alert(AlertType.WARNING, "输入格式错误", null, "未扫描刀具，输入框值不能为空！");
			return;
		}
		try {

			// Comp ID info, [COMPID|PHYSICALID]
			String[] info = inputText.split(toolSep);
			String compID = info[0];//compID
			String physicalID = info[1];//physicalID

			//check compID is exist
			if (!stockDAO.isExistComp(compID)) {
				mainApp.alert(AlertType.ERROR, "入库失败", null, "刀具单项ID不存在，请在TDM中检查此刀具单项是否存在！");
				logger.error("Tool is not exist, ID：[" + compID + "]");
				return;
			}
			
			//load selected Crib and Stock object
			CostCenter chooseCrib = cribBox.getSelectionModel().getSelectedItem();
			String stockInfo = stockBox.getSelectionModel().getSelectedItem();
			String stockplaceID = "";
			String stockplaceDesc = "";
			//StockPlaceID : stockInfo=[name|stockplaceID]
			String[] stock = stockInfo.split("\\|");
			if (stock.length > 1) {
				stockplaceDesc = stock[0];
				stockplaceID = stock[1];
			} else {
				stockplaceID = stock[0];
			}

			//check if in stock
			Map stockMap = stockDAO.checkStock(compID, physicalID);
			if (stockMap == null) {//not in stock, put into table list
				//addToTableList
				addToList(compID, physicalID,chooseCrib.getType(), chooseCrib.getCostunit(), chooseCrib.getWorkplace(), stockplaceID, stockplaceDesc);
			} else {//exist in stock, return
				mainApp.alert(AlertType.ERROR, "入库失败", null, "刀具[" + compID + "_" + physicalID + "]已存在库房[" + stockMap.get("COSTUNIT") + "-" + stockMap.get("WORKPLACE") + "]中，请勿重复入库！");
				logger.error("Tool : [" + compID + "]-[" + physicalID + "] had exist in Stock : [" + stockMap.get("COSTUNIT") + "-" + stockMap.get("WORKPLACE") + "]");
			}
			// clear input field
			inputTextField.clear();
		} catch (Exception e) {
			mainApp.alert(AlertType.ERROR, "输入格式错误", null, "请检查输入信息是否正确！刀具条码格式[ 刀具ID" + toolSep + "物理号 ]");
			logger.error("Input Format Error. Check[" + inputText + "]", e);
		}
	}

	/**
	 * add to comp list (table view) 
	 * type：0=physicalID not existed， 1=physicalID existed,but not in stock
	 */
	@SuppressWarnings("rawtypes")
	private void addToList(String compID, String physicalID,int costCenterType, String costunit, String workplace, String stockplaceID, String stockplaceDesc) {
		try {
			//query comp info
			Map m = stockDAO.queryCompInfoMap(compID);
			//new a comp object
			Comp c = new Comp(compID, physicalID, Common.objectToString(m.get("NAME")), Common.objectToString(m.get("NAME2")), costunit, workplace, stockplaceID, stockplaceDesc, 1, 0);
			c.setCostCenterType(costCenterType);
			
			if (!compTable.getItems().contains(c)) {//not exist in table
				//add to table view
				compTable.getItems().add(c);
			} else {//exist in table
				mainApp.alert(AlertType.ERROR, "添加失败", null, "清单表中已存在该刀具，请勿重复添加！");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			mainApp.exceptionDialog("添加刀具异常！", e);
		}
	}

	/**
	 * delete comp from list
	 */
	@FXML
	private void delFromList() {
		try {
			int selectedIndex = compTable.getSelectionModel().getSelectedIndex();
			if (selectedIndex >= 0) {
				compTable.getItems().remove(selectedIndex);
			} else {
				mainApp.alert(AlertType.ERROR, "移除失败", null, "未选择数据！");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * confirmed and issue action
	 */
	@FXML
	private void issueConfirm() {

		//check before
		if (compTable.getItems().isEmpty()) {
			mainApp.alert(AlertType.ERROR, "执行警告", null, "刀具列表为空！");
			return;
		}
		String tdmInitials = operator.getTdmInitials();
		String timestamp = Common.getTimestamp_TDM();
		DecimalFormat format = new DecimalFormat("#.000000");
		String errorMsg = "";
		for (Comp comp : compTable.getItems()) {
			//temp ref
			String compID = comp.getCompID();
			String physicalID = comp.getPhysicalID();
			String costunit = comp.getCostunit();
			String workplace = comp.getWorkplace();
			String stockplaceID = comp.getStockplaceID();
			try {
				/********************Put In Action**************************/
				//check physicalID existed
				if (!stockDAO.isExistPhysical(compID, physicalID)) {
					//create physicalID
					if (stockDAO.createPhysicalID(timestamp, compID, physicalID) != 1) {
						//create fail
					}
				}
				//create new cancel key
				BigDecimal cancelKey = stockDAO.newCancelKey();

				//add cancel key base
				stockDAO.addCancelKey(cancelKey);
				//calculate cancel key (+0.001)
				cancelKey = new BigDecimal(format.format(cancelKey.add(new BigDecimal(0.001))));

				//add cancel key detail
				stockDAO.addCancelDetail(comp.getCostCenterType(), timestamp, cancelKey, compID, physicalID, costunit, workplace, stockplaceID, 1, 0);

				//put into crib
				stockDAO.putInStockWithCancel(timestamp, cancelKey, compID, physicalID, costunit, workplace, stockplaceID, 1, 0);

				//add consumption
				stockDAO.addConsumptionWithCancel(timestamp, cancelKey, compID, physicalID, costunit, workplace, 1);

				//add history base
				stockDAO.addHistoryBase(-1, timestamp, cancelKey, compID, null, null, costunit, workplace, tdmInitials, 1, 0);

				//add history base list
				stockDAO.addHistoryBaseList(timestamp, cancelKey, physicalID, stockplaceID, 1, 0, 0, 0);

				/********************Put In Action End**************************/
				//logfile
				logger.info("Put Existed Physical Item:[" + compID + "-" + physicalID + "] into CostCenter[" + costunit + "-" + workplace + "]");
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				errorMsg += "Error:[" + e.getMessage() + "], COMPID=" + compID + ", PhysicalID=" + physicalID + ".\n";
			}
		}

		if (errorMsg.length() > 0) {
			mainApp.alert(AlertType.ERROR, "执行失败", "执行入库时，以下刀具发生错误，请在TDM中检查！", errorMsg);
		} else {
			mainApp.alert(AlertType.INFORMATION, "执行完毕", null, "刀具入库成功！");
			//clear
			compTable.getItems().clear();
			inputTextField.clear();
			//get focus
			inputTextField.requestFocus();
		}

	}

	/**
	 * Is called by the main application to give a reference back to itself.
	 */
	public void setMainApp(Main mainApp) {
		this.mainApp = mainApp;
		this.operator = mainApp.getLoginUser();
	}
}
