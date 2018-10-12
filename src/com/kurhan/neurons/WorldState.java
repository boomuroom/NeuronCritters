package com.kurhan.neurons;

import com.kurhan.neurons.spatial.Grid;
import com.kurhan.neurons.spatial.Spatial;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Optional;
import java.util.function.Consumer;

public class WorldState {
  
  private final Set<Animal> animals = new HashSet<>();
  private final Set<Food> foods = new HashSet<>();
  private Spatial<Animal> animalGrid = Spatial.of(animals);
  private Spatial<Food> foodGrid = Spatial.of(foods);
  private final double worldSize;
  
  public WorldState(double worldSize) {
    if(worldSize <= 0) throw new IllegalArgumentException("WorldSize must be > 0");
    this.worldSize =  worldSize;
  }

  public void add(Food food) {
    foods.add(food);
  }

  public void add(Animal animal) {
	  animals.add(animal);
  }

  public void kill(Animal animal) {
    Vector pos = animal.getPosition();
    add(new Food(pos.x, pos.y));
    remove(animal);
  }
  
  public void consume(Animal animal, Food food) {
    remove(food);
  }
  
  public void remove(Food food) {
    foods.remove(food);
	  foodGrid.remove(food);
  }

  public void remove(Animal animal) {
    animals.remove(animal);
	  animalGrid.remove(animal);
  }
  
  public void keepInbounds(WorldObject object) {
    Vector position = object.getPosition();
    double x = Math.min(Math.max(position.x, -worldSize), worldSize);
    double y = Math.min(Math.max(position.y, -worldSize), worldSize);
    object.moveTo(x, y);
  }

  public Collection<Animal> getAllAnimals() {
    return new ArrayList<>(animals);
  }

  public Collection<Food> getAllFoods() {
    return new ArrayList<>(foods);
  }

  public void forEachAnimal(Consumer<? super Animal> action) {
    animals.forEach(action);
  }

  public void forEachFood(Consumer<? super Food> action) {
    foods.forEach(action);
  }

  public Optional<Food> getClosestFood(WorldObject target) {
  	//return findClosestIn(target, foods);
  	Optional<Food> result = foodGrid.findClosest(target);
	  //if(!result.isPresent()) {
		//  System.out.printf("No food found: %s%n", target.getName());
	  //}
	  return result;
  }

  public Optional<Animal> getClosestAnimal(WorldObject target) {
  	//return findClosestIn(target, animals);
  	Optional<Animal> result = animalGrid.findClosest(target);
	  return result;
  }

  private <T extends WorldObject> Optional<T> findClosestIn(WorldObject target, Collection<T> items) {
    T item = null;
    double distance = Double.MAX_VALUE;
    for(T currentItem : items) {
      if(target == currentItem) continue; // Don't match the same guy. That would be silly
      double newDist = currentItem.getVectorTo(target).fastLength();
      if(newDist < distance) {
        distance = newDist;
        item = currentItem;
      }
    }
    return Optional.ofNullable(item);
  }

	public void optimize() {
    animalGrid = Spatial.of(animals);
    foodGrid = Spatial.of(foods);
	}

	private <T extends WorldObject> Grid<T> buildGrid(Collection<T> items) {
		Rectangle boundary = new Rectangle(-worldSize, worldSize, worldSize, -worldSize);
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

}