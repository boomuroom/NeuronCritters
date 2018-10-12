package com.kurhan.neurons;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Animal extends WorldObject implements Comparable<Animal> {

	private static final double REACH = 2;
	private static final double ENERGY_PER_FOOD = 100;
	private static final double ENERGY_TO_SPAWN = 1000;
	private static final double ENERGY_USED_TO_SPAWN = 500;
	private static final double MUTATION_RATE = 0.01;

	private final NeuralNetwork brain;
	private double energy = 50;
	private int age = 0;
	private int children = 0;
	private int generation = 0;
	private double r, g, b;

	public Animal(double x, double y) {
		this(NeuralNetwork.randomNetwork(5, 6), x, y);
	}

	public Animal(NeuralNetwork brain, double x, double y) {
		super(x, y);
		this.brain = brain;
	}

	public int getGeneration() {
		return generation;
	}

	public void react(WorldState world) {
		++age;
		List<Double> actions = think(world);
		act(actions, world);
		if(energy <= 0) world.kill(this);
		//System.out.printf("%s has %s energy%n", getName(), energy);
	}

	public Color getColor() {
		return new Color((float) r, (float) g, (float) b);
	}

	private List<Double> think(WorldState world) {
		Vector animalVector = world.getClosestAnimal(this)
				.map(this::getVectorTo)
				.orElseGet(() -> new Vector(Double.MAX_VALUE, Double.MAX_VALUE));
		Vector foodVector = world.getClosestFood(this)
				.map(this::getVectorTo)
				.orElseGet(() -> new Vector(Double.MAX_VALUE, Double.MAX_VALUE));

		return brain.compute(Arrays.asList(
				animalVector.x,
				animalVector.y,
				foodVector.x,
				foodVector.y,
				energy
		));
	}

	private void act(List<Double> actions, WorldState world) {
		double xMove = actions.get(0);
		double yMove = actions.get(1);
		double speed = (actions.get(2) + 1) / 2; // normalize to 0-1
		double energyUsed = computeUsedEnergy(speed);
		energy -= energyUsed;
		if(energyUsed < 1) {
			System.out.println("Energy Used: " + energyUsed);
		}
		Vector movement = Vector.normalize(xMove, yMove).scale(speed);
		r = (actions.get(3) + 1) / 2;
		g = (actions.get(4) + 1) / 2;
		b = (actions.get(5) + 1) / 2;
		move(movement);
		tryToEat(world);
		tryToSpawn(world);
	}

	private double computeUsedEnergy(double speed) {
		// x^2 + x + 0.1;
		// moving faster costs more energy
		// not moving at all costs almost nothing
		return speed * speed + speed + 1;
	}

	private void tryToSpawn(WorldState world) {
		if (energy >= ENERGY_TO_SPAWN) {
			energy -= ENERGY_USED_TO_SPAWN;
			Animal baby = createOffspring();
			world.add(baby);
			System.out.println("Baby gen " + baby.generation);
			if (baby.generation > 10 || children > 10) {
				System.out.println(baby.brain);
			}
		}
	}

	private Animal createOffspring() {
		NeuralNetwork childBrain = createBrain();
		Vector pos = getPosition();
		Animal baby = new Animal(childBrain, pos.x-0.5, pos.y-0.5);
		baby.generation = generation + 1;
		children += 1;
		baby.setName(getName() + "." + children);
		return baby;
	}

	private NeuralNetwork createBrain() {
		List<NeuronLayer> layers = new ArrayList<>();
		for(NeuronLayer layer : brain.layers) {
			List<Neuron> neurons = new ArrayList<>();
			for(Neuron neuron : layer.neurons) {
				List<Double> weights = new ArrayList<>();
				for(double weight : neuron.weights) {
					weights.add(copyGene(weight));
				}
				Neuron newNeuron = new Neuron(copyGene(neuron.tolerance), weights);
				neurons.add(newNeuron);
			}
			NeuronLayer newLayer = new NeuronLayer(neurons);
			layers.add(newLayer);
		}
		NeuralNetwork newBrain = new NeuralNetwork(layers);
		return newBrain;
	}

	private double copyGene(double original) {
		if(Math.random() < MUTATION_RATE) {
			return mutate(original);
		}
		return original;
	}

	private double mutate(double original) {
		double roll = Math.random();
		if(roll < 0.9) {
			double change = Math.random();
			return original + change;
		} else if(roll < 0.95) {
			return -original;
		} else {
			return Math.random() * 6 - 3;
		}
	}

	private void tryToEat(WorldState world) {
		world.getClosestFood(this).ifPresent(food -> {
			Vector direction = getVectorTo(food);
			if (direction.length() < REACH) {
				world.consume(this, food);
				energy += ENERGY_PER_FOOD;
			}
		});
	}

	@Override
	public String toString() {
		return String.format("%s[name=%s, position=%s]", getClass().getSimpleName(), getName(), getPosition());
	}

	@Override
	public int compareTo(Animal o) {
		return (int)(100 * (this.energy = o.energy));
	}
}