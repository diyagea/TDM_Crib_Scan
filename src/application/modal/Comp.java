package application.modal;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Comp {
	private StringProperty compID;
	private StringProperty physicalID;
	private StringProperty desc1;
	private StringProperty desc2;
	private IntegerProperty countNew;
	private IntegerProperty countUsed;

	private StringProperty costunit;
	private StringProperty workplace;
	private StringProperty stockplaceID;
	private StringProperty stockplaceDesc;
	
	private int costCenterType=1;

	public int getCostCenterType() {
		return costCenterType;
	}

	public void setCostCenterType(int costCenterType) {
		this.costCenterType = costCenterType;
	}

	public Comp() {
		
		this.compID = new SimpleStringProperty("");
		this.physicalID = new SimpleStringProperty("");
		this.desc1 = new SimpleStringProperty("");
		this.desc2 = new SimpleStringProperty("");
		this.stockplaceID = new SimpleStringProperty("");
		this.countNew = new SimpleIntegerProperty(0);
		this.countUsed = new SimpleIntegerProperty(0);

	}
	
	public Comp(String compID, String physicalID, String desc1, String desc2, String costunit, String workplace, String stockplaceID,String stockplaceDesc, Integer countNew, Integer countUsed) {
		this.compID = new SimpleStringProperty(compID);
		this.physicalID = new SimpleStringProperty(physicalID);
		this.desc1 = new SimpleStringProperty(desc1);
		this.desc2 = new SimpleStringProperty(desc2);
		this.costunit = new SimpleStringProperty(costunit);
		this.workplace = new SimpleStringProperty(workplace);
		this.stockplaceID = new SimpleStringProperty(stockplaceID);
		this.stockplaceDesc = new SimpleStringProperty(stockplaceDesc);
		
		this.countNew = new SimpleIntegerProperty(countNew);
		this.countUsed = new SimpleIntegerProperty(countUsed);

	}

	public StringProperty compIDProperty() {
		return this.compID;
	}

	public String getCompID() {
		return this.compIDProperty().get();
	}

	public void setCompID(final String compID) {
		this.compIDProperty().set(compID);
	}

	public StringProperty physicalIDProperty() {
		return this.physicalID;
	}

	public String getPhysicalID() {
		return this.physicalIDProperty().get();
	}

	public void setPhysicalID(final String physicalID) {
		this.physicalIDProperty().set(physicalID);
	}

	public StringProperty desc1Property() {
		return this.desc1;
	}

	public String getDesc1() {
		return this.desc1Property().get();
	}

	public void setDesc1(final String desc1) {
		this.desc1Property().set(desc1);
	}

	public StringProperty desc2Property() {
		return this.desc2;
	}

	public String getDesc2() {
		return this.desc2Property().get();
	}

	public void setDesc2(final String desc2) {
		this.desc2Property().set(desc2);
	}


	public IntegerProperty countNewProperty() {
		return this.countNew;
	}

	public int getCountNew() {
		return this.countNewProperty().get();
	}

	public void setCountNew(final int countNew) {
		this.countNewProperty().set(countNew);
	}

	public IntegerProperty countUsedProperty() {
		return this.countUsed;
	}

	public int getCountUsed() {
		return this.countUsedProperty().get();
	}

	public void setCountUsed(final int countUsed) {
		this.countUsedProperty().set(countUsed);
	}
	
	public StringProperty stockplaceIDProperty() {
		return this.stockplaceID;
	}
	
	public String getStockplaceID() {
		return this.stockplaceIDProperty().get();
	}
	
	public void setStockplaceID(final String stockplaceID) {
		this.stockplaceIDProperty().set(stockplaceID);
	}
	
	public StringProperty stockplaceDescProperty() {
		return this.stockplaceDesc;
	}
	
	public String getStockplaceDesc() {
		return this.stockplaceDescProperty().get();
	}
	
	public void setStockplaceDesc(final String stockplaceDesc) {
		this.stockplaceDescProperty().set(stockplaceDesc);
	}
	
	public StringProperty costunitProperty() {
		return this.costunit;
	}
	
	public String getCostunit() {
		return this.costunitProperty().get();
	}
	
	public void setCostunit(final String costunit) {
		this.costunitProperty().set(costunit);
	}
	
	public StringProperty workplaceProperty() {
		return this.workplace;
	}
	
	public String getWorkplace() {
		return this.workplaceProperty().get();
	}
	
	public void setWorkplace(final String workplace) {
		this.workplaceProperty().set(workplace);
	}
	

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((compID == null) ? 0 : compID.hashCode());
		result = prime * result + ((physicalID == null) ? 0 : physicalID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		Comp other = (Comp) obj;

		if (this.getCompID() == null) {
			if (other.getCompID() != null) {
				return false;
			}
		} else if (!this.getCompID().equals(other.getCompID())) {
			return false;
		}
		if (this.getPhysicalID() == null) {
			if (other.getPhysicalID() != null) {
				return false;
			}
		} else if (!this.getPhysicalID().equals(other.getPhysicalID())) {
			return false;
		}
		return true;
	}

}
