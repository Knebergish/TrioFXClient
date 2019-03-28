package trio.core;


import trio.model.field.*;

import java.util.*;


public final class FieldManipulator {
	private static final Random rnd = new Random();
	
	private FieldManipulator() {
	}
	
	public static StepResult move(Field field, Coordinates source, Coordinates dest) {
		final List<Field>  states = new ArrayList<>();
		final CellType[][] cells  = field.copyCells();
		int                score  = 0;
		
		if (Math.abs(source.getX() - dest.getX()) + Math.abs(source.getY() - dest.getY()) != 1) {
			return new StepResult(states, score);
		}
		
		swap(source, dest, cells);
		
		Field newField = new FieldImpl(cells);
		states.add(newField);
		
		while (true) {
			final Set<Coordinates> cellsForDelete = getAllCellsForDelete(newField);
			if (cellsForDelete.isEmpty()) {
				List<PossibleStep> possibleSteps = getPossibleSteps(newField);
				if (possibleSteps.isEmpty()) {
					states.add(createField(newField.getWidth(), newField.getHeight()));
				}
				break;
			} else {
				for (Coordinates c : cellsForDelete) {
					score += newField.get(c.getX(), c.getY()).getCost();
				}
				newField = deleteCells(newField, cellsForDelete);
				states.add(newField);
				newField = vacuumAndFillField(newField);
				states.add(newField);
			}
		}
		
		if (score == 0) states.clear();
		return new StepResult(states, score);
	}
	
	public static Field deleteCells(Field field, Set<Coordinates> coordinates) {
		CellType[][] cells = field.copyCells();
		for (Coordinates coord : coordinates) {
			cells[coord.getY()][coord.getX()] = null;
		}
		return new FieldImpl(cells);
	}
	
	public static Field vacuumAndFillField(Field field) {
		CellType[][] cells = field.copyCells();
		for (int x = 0; x < field.getWidth(); x++) {
			for (int y = field.getHeight() - 1; y >= 0; y--) {
				if (cells[y][x] == null) {
					for (int k = y - 1; k >= 0; k--) {
						if (cells[k][x] != null) {
							cells[y][x] = cells[k][x];
							cells[k][x] = null;
							break;
						}
					}
				}
			}
			for (int y = 0; y < field.getHeight(); y++) {
				if (cells[y][x] == null) {
					cells[y][x] = getRandomCellValue();
				} else {
					break;
				}
			}
		}
		
		return new FieldImpl(cells);
	}
	
	public static Field createField(int width, int height) {
		Field field = generateRandomField(width, height);
		
		while (true) {
			while (true) {
				final Set<Coordinates> cellsForDelete = getAllCellsForDelete(field);
				if (cellsForDelete.isEmpty()) {
					break;
				} else {
					field = deleteCells(field, cellsForDelete);
					field = vacuumAndFillField(field);
				}
			}
			if (getPossibleSteps(field).isEmpty()) {
				field = generateRandomField(width, height);
			} else {
				break;
			}
		}
		
		return field;
	}
	
	private static Field generateRandomField(int width, int height) {
		CellType[][] cells = new CellType[height][width];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				cells[y][x] = getRandomCellValue();
			}
		}
		return new FieldImpl(cells);
	}
	
	public static List<PossibleStep> getPossibleSteps(Field field) {
		List<PossibleStep> steps = new ArrayList<>();
		CellType[][]       cells = field.copyCells();
		
		for (int x = 0; x < field.getWidth(); x++) {
			for (int y = 0; y < field.getHeight(); y++) {
				Coordinates       source = new Coordinates(x, y);
				List<Coordinates> dests  = new ArrayList<>();
				if (x < field.getWidth() - 1) {
					dests.add(new Coordinates(x + 1, y));
				}
				if (y < field.getHeight() - 1) {
					dests.add(new Coordinates(x, y + 1));
				}
				
				for (Coordinates dest : dests) {
					swap(source, dest, cells);
					Set<Coordinates> allCellsForDelete = getAllCellsForDelete(new FieldImpl(cells));
					if (!allCellsForDelete.isEmpty()) {
						steps.add(new PossibleStep(source,
						                           dest,
						                           allCellsForDelete.parallelStream()
						                                            .mapToInt(c -> cells[c.getY()][c.getX()].getCost())
						                                            .sum()));
					}
					swap(dest, source, cells);
				}
			}
		}
		
		steps.sort(Comparator.comparingInt(PossibleStep::getScore).reversed());
		return steps;
	}
	
	public static void swap(Coordinates source, Coordinates dest, CellType[][] cells) {
		CellType temp = cells[dest.getY()][dest.getX()];
		cells[dest.getY()][dest.getX()] = cells[source.getY()][source.getX()];
		cells[source.getY()][source.getX()] = temp;
	}
	
	public static Set<Coordinates> getAllCellsForDelete(Field field) {
		Set<Coordinates> cellsForDelete;
		cellsForDelete = new HashSet<>();
		for (int y = 0; y < field.getHeight(); y++) {
			for (int x = 0; x < field.getWidth(); x++) {
				cellsForDelete.addAll(getCellsForDelete(field, new Coordinates(x, y)));
			}
		}
		return cellsForDelete;
	}
	
	public static Set<Coordinates> getCellsForDelete(Field field, Coordinates target) {
		Set<Coordinates> coordinates = new HashSet<>();
		
		int firstX = target.getX();
		while (firstX > 0
		       && field.get(firstX - 1, target.getY()) == field.get(target.getX(), target.getY())) {
			firstX--;
		}
		int countX = 1;
		while (firstX + countX < field.getWidth()
		       && field.get(firstX + countX, target.getY()) == field.get(target.getX(), target.getY())) {
			countX++;
		}
		
		int firstY = target.getY();
		while (firstY > 0
		       && field.get(target.getX(), firstY - 1) == field.get(target.getX(), target.getY())) {
			firstY--;
		}
		int countY = 1;
		while (firstY + countY < field.getHeight()
		       && field.get(target.getX(), firstY + countY) == field.get(target.getX(), target.getY())) {
			countY++;
		}
		
		if (countX > 2) {
			for (int x = firstX; x < firstX + countX; x++) {
				coordinates.add(new Coordinates(x, target.getY()));
			}
		}
		if (countY > 2) {
			for (int y = firstY; y < firstY + countY; y++) {
				coordinates.add(new Coordinates(target.getX(), y));
			}
		}
		
		return coordinates;
	}
	
	public static CellType getRandomCellValue() {
		CellType[] values = CellType.values();
		return values[rnd.nextInt(values.length)];
	}
}
