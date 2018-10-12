package com.kurhan.neurons.spatial;

import com.kurhan.neurons.IntVector;
import com.kurhan.neurons.Vector;
import com.kurhan.neurons.WorldObject;

import java.util.*;

public class Spatial<T extends WorldObject> {

	private final HexGrid<T> grid = new HexGrid<>(new Vector(5, 5));

	public static <T extends WorldObject> Spatial<T> of(Collection<T> items) {
		Spatial<T> result = new Spatial<>();
		for(T item : items) {
			HexGrid<T>.Cell cell = result.grid.getCell(item.getPosition());
			cell.add(item);
		}
		return result;
	}

	public Optional<T> findClosest(WorldObject target) {
		RadialIterator iter = new RadialIterator(target.getPosition());
		List<HexGrid<T>.Cell> cells = new ArrayList<>();
		cells.addAll(iter.next());  // First three to ensure no weird gaps
		cells.addAll(iter.next());
		cells.addAll(iter.next());
		T best = null;
		double dist = Double.MAX_VALUE;
		for(HexGrid<T>.Cell cell : cells) {
			for(T item : cell.get()) {
				double cellDist = item.getVectorTo(target).fastLength();
				if(cellDist < dist) {
					dist = cellDist;
					best = item;
				}
			}
		}
		while(best == null && iter.hasNext()) {
			for(HexGrid<T>.Cell cell : iter.next()) {
				for(T item : cell.get()) {
					double cellDist = item.getVectorTo(target).fastLength();
					if(cellDist < dist) {
						dist = cellDist;
						best = item;
					}
				}
			}
		}
		return Optional.ofNullable(best);

	}

	public void remove(T item) {
		HexGrid<T>.Cell cell = grid.getCell(item.getPosition());
		cell.remove(item);
	}

	private class RadialIterator implements Iterator<Collection<HexGrid<T>.Cell>> {

		private final HexGrid<T>.Cell center;
		private int radius = -1;

		public RadialIterator(Vector center) {
			this.center = grid.getCell(center.x, center.y);
		}

		public RadialIterator(HexGrid<T>.Cell center) {
			this.center = center;
		}

		@Override
		public boolean hasNext() {
			return true;
		}

		@Override
		public Collection<HexGrid<T>.Cell> next() {
			if(++radius == 0) {
				return Arrays.asList(center);
			}
			return center.getCellsInRadius(radius);
		}
	}
}
