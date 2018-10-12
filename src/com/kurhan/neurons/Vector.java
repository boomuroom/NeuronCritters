package com.kurhan.neurons;

public class Vector {
  public final double x;
  public final double y;
  
  public Vector(double x, double y) {
    this.x = x;
    this.y = y;
  }
  
  public Vector scale(double factor) {
    return new Vector(x * factor, y * factor);
  }
  
  public Vector normalize() {
    return normalize(x, y);
  }
  
  public double length() {
    return length(x, y);
  }

  /**
   * returns length * length
   * called fast because it does not do the sqrt(length * length) required to compute the real length
   */
  public double fastLength() {
    return x * x + y * y;
  }
  
  public static double length(double x, double y) {
    return Math.sqrt(x * x + y * y);
  }
  
  public static Vector normalize(double x, double y) {
    double len = length(x, y);
    x /= len;
    y /= len;
    return new Vector(x, y);
  }
  
  @Override
  public String toString() {
    return String.format("%s{x=%f, y=%f}", getClass(), x, y);
  }

	public Vector minus(Vector other) {
		return minus(other.x, other.y);
	}

	public Vector minus(double x, double y) {
		return new Vector(this.x - x, this.y - y);
	}
}