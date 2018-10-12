package com.kurhan.neurons;

public class WorldObject {
  
  private double x, y;
  private String name = "Unnamed";

  public WorldObject() {
    this(0, 0);
  }
  
  public WorldObject(double x, double y) {
    this.x = x;
    this.y = y;
  }
  
  public Vector getPosition() {
    return new Vector(x, y);
  }
  
  public void move(double dx, double dy) {
    x += dx;
    y += dy;
  }
  
  public void moveTo(double x, double y) {
    this.x = x;
    this.y = y;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String getName() {
    return name;
  }
  
  public void move(Vector vector) {
    move(vector.x, vector.y);
  }
  
  public Vector getVectorTo(WorldObject object) {
    Vector other = object.getPosition();
    Vector me = getPosition();
    return new Vector(other.x - me.x, other.y - me.y);
  }
}