package com.kurhan.neurons.spatial;

import com.kurhan.neurons.*;

import java.util.*;
import java.util.stream.Stream;

public class Grid<T extends WorldObject> {

	private final Set<T>[][] grid;
	private final int columns;
	private final int rows;
	private final Rectangle boundary;

	public Grid(Rectangle boundary, int width, int height) {
		Objects.requireNonNull(boundary);
		if(width < 1 || height < 1) throw new IllegalArgumentException("Width and height must be >= 1");
		this.boundary = boundary;
		grid = new Set[height][width];
		columns = width;
		rows = height;
		for(int i=0;i<height;++i) {
			for(int j=0;j<width;++j) {
				grid[i][j] = new HashSet<>();
			}
		}
	}

	public boolean insert(T item) {
		com.kurhan.neurons.Vector position = item.getPosition();
		if(!boundary.contains(position)) return false;
		Set<T> cell = getCell(position);
		cell.add(item);
		return true;
	}

	private IntVector getCellPosition(com.kurhan.neurons.Vector position) {
		com.kurhan.neurons.Vector aligned = position.minus(boundary.left(), boundary.bottom());
		double px = aligned.x / boundary.width();
		double py = aligned.y / boundary.height();
		int cx = (int)(px * grid[0].length);
		int cy = (int)(py * grid.length);
		return new IntVector(cx, cy);
	}

	private Set<T> getCell(com.kurhan.neurons.Vector position) {
		IntVector cellPosition = getCellPosition(position);
		int cx = Math.min(Math.max(cellPosition.x, 0), columns-1);
		int cy = Math.min(Math.max(cellPosition.y, 0), rows-1);
		return grid[cy][cx];
	}

	private Thing search(WorldObject target, int x, int y) {
		if(!isInBounds(x, y)) return null;
		return grid[y][x].stream()
				.map( item -> new Thing(item, target.getVectorTo(item).fastLength()))
				.reduce( (Thing best, Thing next) -> (next.dist < best.dist) ? next : best).orElse(null);
	}

	private boolean isInBounds(int x, int y) {
		if(y < 0 || y >= grid.length) return false;
		if(x < 0 || x >= grid[y].length) return false;
		return true;
	}

	public static <T extends WorldObject> Grid<T> of(Collection<T> items) {
		double minX = Double.MAX_VALUE;
		double minY = Double.MAX_VALUE;
		double maxX = -Double.MAX_VALUE;
		double maxY = -Double.MAX_VALUE;
		for(T item : items) {
			com.kurhan.neurons.Vector pos = item.getPosition();
			minX = Math.min(minX, pos.x);
			minY = Math.min(minY, pos.y);
			maxX = Math.max(maxX, pos.x);
			maxY = Math.max(maxY, pos.y);
		}
		Rectangle boundary = new Rectangle(minX, minY, maxX, maxY);
		IntVector cells = getCellSize(boundary, items.size());
		Grid<T> grid = new Grid<>(boundary, cells.x, cells.y);
		items.forEach(grid::insert);
		/*System.out.printf("Grid created [%d, %d] with boundaries [%.1f, %.1f, %.1f, %.1f] for %d items %n",
				cells.x, cells.y,
				boundary.left(), boundary.top(), boundary.width(), boundary.height(),
				items.size());*/
		return grid;
	}

	private static IntVector getCellSize(Rectangle boundary, int items) {
		if(items <= 1 || boundary.width() <= 0 || boundary.height() <= 0) return new IntVector(1, 1);
    double x = Math.ceil(items / boundary.height());
    double y = Math.ceil(items / boundary.width());
    double total = x*y;
    double rf = Math.sqrt(total / items);
		return new IntVector(
			(int)Math.max(1, Math.floor(x / rf)),
			(int)Math.max(1, Math.floor(y / rf)));
	}

	public Optional<T> findClosest(WorldObject target) {
		com.kurhan.neurons.Vector position = target.getPosition();
		IntVector cellPosition = getCellPosition(position);
		Optional<T> result = Stream.of(
				search(target, cellPosition.x, cellPosition.y),
				search(target, cellPosition.x+1, cellPosition.y),
				search(target, cellPosition.x+1, cellPosition.y-1),
				search(target, cellPosition.x, cellPosition.y-1),
				search(target, cellPosition.x-1, cellPosition.y-1),
				search(target, cellPosition.x-1, cellPosition.y),
				search(target, cellPosition.x-1, cellPosition.y+1),
				search(target, cellPosition.x, cellPosition.y+1),
				search(target, cellPosition.x+1, cellPosition.y+1))
				.filter(Objects::nonNull) //TODO: Figure out to do if not in directly neighboring cells
				.reduce( (Thing best, Thing next) -> (next.dist < best.dist) ? next : best)
				.map(Thing::getItem);
		/*Optional<T> result = Arrays.stream(grid).flatMap( Arrays::stream ).flatMap( Set::stream )
				.parallel()
				.map( item -> new Thing(item, target.getVectorTo(item).fastLength()))
				.reduce( (Thing best, Thing next) -> (next.dist < best.dist) ? next : best)
				.map(Thing::getItem);*/
		return result;
	}

	private class Thing {
		final T item;
		final double dist;

		public Thing(T item, double dist) {
			this.item = item;
			this.dist = dist;
		}

		public T getItem() {
			return item;
		}
	}
}
