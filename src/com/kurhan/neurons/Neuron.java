package com.kurhan.neurons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Neuron {
  
  private static final double EPSILON = 0.00001;
  public static final Neuron INPUT = new Neuron(0, 1);
  
  public final List<Double> weights;
  public final double tolerance;
  
  public Neuron(double tolerance, double ... weights) {
    this.tolerance = tolerance;
    List<Double> temp = new ArrayList<Double>(weights.length);
    for(double weight : weights) {
      temp.add(weight);
    }
    this.weights = Collections.unmodifiableList(temp);
  }
  
  public Neuron(double tolerance, List<Double> weights) {
    this.tolerance = tolerance;
    this.weights = Collections.unmodifiableList(new ArrayList<>(weights));
  }
  
  public double compute(List<Double> inputs) {
    if(inputs.size() != weights.size()) {
      throw new IllegalArgumentException("Expecting input size " + weights.size() + " but got " + inputs.size());
    }
    double result = 0;
    for(int i=0;i<weights.size();++i) {
      double weight = weights.get(i);
      double input = inputs.get(i);
      double activation = weight * input;
      result += activation;
    }
    result -= tolerance;
    result = 1 / (1 + Math.exp(-result));
    return result * 2 - 1; // Normalize to [-1, 1]
  }
  
  @Override
  public String toString() {
    return String.format("%s[%f, %s]", getClass().getName(), tolerance, weights);
  }
}
