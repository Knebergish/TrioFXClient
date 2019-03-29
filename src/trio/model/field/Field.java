package trio.model.field;


import java.util.Map;


public interface Field {
	CellType[][] copyCells();
	
	CellType get(int x, int y);
	
	int getWidth();
	
	int getHeight();
	
	Map<CellType, Integer> getCosts();
}
