package trio.model.field;


public interface Field {
	CellType[][] copyCells();
	
	CellType get(int x, int y);
	
	int getWidth();
	
	int getHeight();
}
