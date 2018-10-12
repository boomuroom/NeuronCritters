package com.kurhan.neurons;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class NeuronLayer {
  
  public final List<Neuron> neurons;
  
  public NeuronLayer(List<Neuron> neurons) {
    this.neurons = Collections.unmodifiableList(new ArrayList<>(neurons));
  }
  
  public NeuronLayer(Neuron ... neurons) {
    this(Arrays.asList(neurons));
  }
  
  public List<Double> compute(List<List<Double>> inputs) {
    if(inputs.size() != neurons.size()) {
      throw new IllegalArgumentException("Expecting input size " + neurons.size() + " but got " + inputs.size());
    }
    List<Double> outputs = new ArrayList<>(neurons.size());
    for(int i=0;i<neurons.size();++i) {
      Neuron neuron = neurons.get(i);
      List<Double> input = inputs.get(i);
      outputs.add(neuron.compute(input));
    }
    return outputs;
  }
  
  public int neuronCount() {
    return neurons.size();
  }
  
  @Override
  public String toString() {
    return String.format("%s%s", getClass().getName(), neurons);
  }

}
