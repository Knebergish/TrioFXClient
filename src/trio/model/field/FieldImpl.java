package trio.model.field;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;


public class FieldImpl implements Field, Serializable {
	@JsonProperty("cells")
	private final CellType[][] cells;
	
	@JsonProperty("costs")
	private final Map<CellType, Integer> costs;
	
	@JsonCreator
	public FieldImpl(@JsonProperty("cells") CellType[][] cells,
	                 @JsonProperty("costs") Map<CellType, Integer> costs) {
		this.cells = cells;
		this.costs = costs;
	}
	
	public CellType[][] copyCells() {
		CellType[][] newCells = new CellType[cells.length][];
		for (int i = 0; i < newCells.length; i++) {
			newCells[i] = Arrays.copyOf(cells[i], cells[i].length);
		}
		return newCells;
	}
	
	@Override
	@JsonIgnore
	public CellType get(int x, int y) {
		return cells[y][x];
	}
	
	@Override
	@JsonIgnore
	public int getWidth() {
		return cells[0].length;
	}
	
	@Override
	@JsonIgnore
	public int getHeight() {
		return cells.length;
	}
	
	@Override
	public Map<CellType, Integer> getCosts() {
		return costs;
	}
}
