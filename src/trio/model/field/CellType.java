package trio.model.field;


public enum CellType {
	TYPE1(1),
	TYPE2(2),
	TYPE3(3),
	TYPE4(4),
	TYPE5(5);
	
	private final int cost;
	
	CellType(int cost) {
		this.cost = cost;
	}
	
	@Override
	public String toString() {
		return "CellType{" +
		       "cost=" + cost +
		       '}';
	}
	
	public int getCost() {
		return cost;
	}
}
