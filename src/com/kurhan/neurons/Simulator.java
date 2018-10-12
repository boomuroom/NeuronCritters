package com.kurhan.neurons;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.atomic.AtomicInteger;

public class Simulator {
  
  private static final int[] MS_PER_TICK = {0, 5, 10};
  private static final int MIN_ANIMALS = 100;
  private static final int WORLD_SIZE = 1000;
	private static final int MAX_FOOD_SPAWN = 200;

	public static void main(String[] args) {
    SwingUtilities.invokeLater( Simulator::createAndShow );
  }
  
  private final WorldState world = new WorldState(WORLD_SIZE);
  private final AnimalFactory factory = new AnimalFactory();
  private int tick;
  
  private Simulator() {
    for(int i=0;i<100;++i) {
      world.add(factory.getRandomAnimal(WORLD_SIZE));
    }
    for(int i=0;i<100;++i) {
      double x = Math.random() * WORLD_SIZE * 2 - WORLD_SIZE;
      double y = Math.random() * WORLD_SIZE * 2 - WORLD_SIZE;
      world.add(new Food(x, y));
    }
  }

  private void step() {
    if((++tick & 127) == 0) System.out.printf("World Age: %d - Animals: %d - Foods: %d%n", tick, world.getAllAnimals().size(), world.getAllFoods().size());
	  if(world.getAllFoods().size() < MAX_FOOD_SPAWN) {
		  world.add(new Food(Math.random() * WORLD_SIZE * 2 - WORLD_SIZE, Math.random() * WORLD_SIZE * 2 - WORLD_SIZE));
	  }
	  world.optimize();
    for(Animal animal : world.getAllAnimals()) {
      animal.react(world);
      world.keepInbounds(animal);
    }
    int living = world.getAllAnimals().size();
    while(living++ < MIN_ANIMALS) {
      world.add(factory.getRandomAnimal(WORLD_SIZE));
    }
  }
  
  private static void createAndShow() {
    Simulator sim = new Simulator();
    JFrame window = new JFrame("Animal House");
    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    RenderComponent mainComp = new RenderComponent(new WorldStateRenderer(sim.world, WORLD_SIZE*2 + 20));
    window.setContentPane(mainComp);
    window.setSize(600, 600);
    window.setLocationRelativeTo(null);
    window.setVisible(true);
	  AtomicInteger tickDelay = new AtomicInteger();
    window.addMouseListener(new MouseAdapter() {
	    @Override
	    public void mouseClicked(MouseEvent e) {
	    	tickDelay.incrementAndGet();
        //sim.step();
	    }
    });
    new Thread( () -> {
      try {
        while(true) {
          SwingUtilities.invokeAndWait( sim::step );
	        Thread.sleep(MS_PER_TICK[tickDelay.get() % MS_PER_TICK.length]);
        }
      } catch(Exception ex) {
        ex.printStackTrace();
      }
    }).start();
  }
}