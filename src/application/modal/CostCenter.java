package application.modal;

public class CostCenter {
	private String costunit = "";
	private String workplace = "";
	private String name = "";
	private int type;
	private int locked;
	
	public CostCenter() {
	}
	
	public CostCenter(String costunit, String workplace) {
		this.costunit = costunit;
		this.workplace = workplace;
	}

	public CostCenter(String costunit, String workplace, String name, int type, int locked) {
		this.costunit = costunit;
		this.workplace = workplace;
		this.name = name;
		this.type = type;
		this.locked = locked;
	}
	
	public String getCostunit() {
		return costunit;
	}
	public void setCostunit(String costunit) {
		this.costunit = costunit;
	}
	public String getWorkplace() {
		return workplace;
	}
	public void setWorkplace(String workplace) {
		this.workplace = workplace;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getLocked() {
		return locked;
	}
	public void setLocked(int locked) {
		this.locked = locked;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((costunit == null) ? 0 : costunit.hashCode());
		result = prime * result + ((workplace == null) ? 0 : workplace.hashCode());
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
		CostCenter other = (CostCenter) obj;
		if (costunit == null) {
			if (other.costunit != null)
				return false;
		} else if (!costunit.equals(other.costunit))
			return false;
		if (workplace == null) {
			if (other.workplace != null)
				return false;
		} else if (!workplace.equals(other.workplace))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		//return "成本中心[" + costunit + "]车间[" + workplace + "]";
		return name;
	}
	
}
