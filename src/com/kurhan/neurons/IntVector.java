package com.kurhan.neurons;

public class IntVector {
  public final int x, y;
  
  public IntVector(int x, int y) {
    this.x = x;
    this.y = y;
  }

  @Override
  public int hashCode() {
    return x * 17321 + y;
  }

  @Override
  public boolean equals(Object o) {
    if(o == this) return true;
    if(o == null) return false;
    if(o.getClass() != IntVector.class) return false;
    IntVector other = (IntVector)o;
    return other.x == x && other.y == y;
  }
  
  @Override
  public String toString() {
    return String.format("%s{x=%d, y=%d}", getClass(), x, y);
  }
}