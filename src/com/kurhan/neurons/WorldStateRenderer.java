package com.kurhan.neurons;

import java.awt.*;

public class WorldStateRenderer implements Renderer {
  
  private final WorldState state;
	private final double worldSize;
  
  public WorldStateRenderer(WorldState state, double worldSize) {
    this.state = state;
	  this.worldSize = worldSize;
  }
  
  @Override
  public void render(Graphics g, Dimension size) {
  	clear(g, size);
    state.forEachAnimal(animal -> {
      IntVector pos = convertToPixelSpace(animal.getPosition(), size);
      g.setColor(animal.getColor());
      g.drawString("X", pos.x, pos.y);
    });
    state.forEachFood(food -> {
      IntVector pos = convertToPixelSpace(food.getPosition(), size);
      g.setColor(Color.green);
      g.drawString("O", pos.x, pos.y);
    });
  }

  private void clear(Graphics g, Dimension size){
  	g.setColor(Color.black);
	g.fillRect(0, 0, size.width, size.height);
  }
  
  private IntVector convertToPixelSpace(Vector worldSpace, Dimension size) {
  	double widthRatio = size.width / worldSize;
	  double heightRatio = size.height / worldSize;
    int x = (int)(worldSpace.x * widthRatio) + size.width / 2;
    int y = -(int)(worldSpace.y * heightRatio) + size.height / 2;
    return new IntVector(x, y);
  }
}