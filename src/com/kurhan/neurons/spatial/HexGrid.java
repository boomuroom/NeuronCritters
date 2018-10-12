package com.kurhan.neurons.spatial;

import com.kurhan.neurons.*;
import com.kurhan.neurons.Vector;

import java.util.*;
import java.util.function.Function;

public class HexGrid<T> {

	private final Map<IntVector, Collection<T>> data = new HashMap<>();
	private final com.kurhan.neurons.Vector cellSize;

	public HexGrid(Vector cellSize) {
		this.cellSize = cellSize;
	}

	public Cell getCell(Vector position) {
		return getCell(position.x, position.y);
	}

	public Cell getCell(double x, double y) {

		final double gridWidth = cellSize.x;
		final double gridHeight = cellSize.y;
		final double halfWidth = gridWidth / 2;
		final double m = 2 * gridHeight / Math.sqrt(3);
		final double c = m * halfWidth;

		// Find the row and column of the box that the point falls in.
		int row = (int) (y / gridHeight);
		int column;

		boolean rowIsOdd = row % 2 == 1;

		// Is the row an odd number?
		if (rowIsOdd) {// Yes: Offset x to match the indent of the row
			column = (int) ((x - halfWidth) / gridWidth);
		} else {// No: Calculate normally
			column = (int) (x / gridWidth);
		}
		// Work out the position of the point relative to the box it is in
		double relY = y - (row * gridHeight);
		double relX = x - (column * gridWidth);

		if (rowIsOdd) {
			relX -= halfWidth;
		}
		// Work out if the point is above either of the hexagon's top edges
		if (relY < (-m * relX) + c) // LEFT edge
		{
			row--;
			if (!rowIsOdd)
				column--;
		}
		else if (relY < (m * relX) - c) // RIGHT edge
		{
			row--;
			if (rowIsOdd)
				column++;
		}

		return new Cell(column, row);
	}

	public Cell getCenter() {
		return new Cell(0, 0);
	}

	public class Cell {
		private final IntVector pos;

		private Cell(int x, int y) {
			pos = new IntVector(x, y);
		}

		public void add(T item) {
			get().add(item);
		}

		public Collection<T> get() {
			Collection<T> items = data.get(pos);
			if(items == null) {
				items = new HashSet<>();
				data.put(pos, items);
			}
			return items;
		}

		public void clear() {
			get().clear();
		}

		public Collection<Cell> getCellsInRadius(int radius) {
			Cell iterator = this;
			List<Cell> cells = new ArrayList<>(6 * radius);
			for(int i=0;i<radius;++i) {
				iterator = iterator.left();
			}
			List<Function<Cell, Cell>>  mappers =  Arrays.asList(Cell::upRight, Cell::right, Cell::downRight, Cell::downLeft,Cell::left, Cell::upLeft);
			for(Function<Cell, Cell> mapper : mappers) {
				for (int i = 0; i < radius; ++i) {
					iterator = mapper.apply(iterator);
					cells.add(iterator);
				}
			}
			return cells;
		}

		private boolean isShifted() {
			return (pos.y % 2) != 0;
		}

		@Override
		public String toString() {
			return String.format("(%d, %d)", pos.x, pos.y);
		}

		public Cell left() {
			return new Cell(pos.x-1, pos.y);
		}
		public Cell right() {
			return new Cell(pos.x+1, pos.y);
		}
		public Cell upLeft() {
			if(isShifted()) {
				return new Cell(pos.x, pos.y-1);
			} else {
				return new Cell(pos.x -1, pos.y-1);
			}
		}
		public Cell upRight() {
			if(isShifted()) {
				return new Cell(pos.x+1, pos.y-1);
			} else {
				return new Cell(pos.x, pos.y-1);
			}
		}
		public Cell downLeft() {
			if(isShifted()) {
				return new Cell(pos.x, pos.y+1);
			} else {
				return new Cell(pos.x-1, pos.y+1);
			}
		}
		public Cell downRight() {
			if(isShifted()) {
				return new Cell(pos.x+1, pos.y+1);
			} else {
				return new Cell(pos.x, pos.y+1);
			}
		}

		public void remove(T item) {
			get().remove(item);
		}
	}
}
