package com.kurhan.neurons;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class NetworkTest {
  public static void main(String[] args) {
    NeuralNetwork network = NeuralNetwork.randomNetwork(3, 1);
    List<Double> data = Arrays.asList(1.0, 1.0, 1.0);
    System.out.printf("Testing network %s -> %s", data, network.compute(data));
  }
  
  private static void testNeuron(Neuron node, double input) {
    List<Double> il = Collections.singletonList(input);
    System.out.printf("Testing neuron: %f -> %f%n", input, node.compute(il));
  }
  
  private static void testLayer() {
    NeuronLayer input = inputLayer(3);
    List<List<Double>> data = Arrays.asList(Arrays.asList(1.0), Arrays.asList(1.0), Arrays.asList(1.0));
    System.out.printf("Layer testing %s -> %s", data, input.compute(data));
  }
  
  private static void testNetwork() {
    NeuronLayer input = inputLayer(3);
    NeuronLayer output = new NeuronLayer(new Neuron(0, 1, -1, 1));
    NeuralNetwork network = new NeuralNetwork(input, output);
    List<Double> data = Arrays.asList(1.0, 1.0, 1.0);
    System.out.printf("Network testing %s -> %s", data, network.compute(data));
  }
  
  private static void testNetwork2() {
    List<NeuronLayer> layers = new ArrayList<>();
    NeuronLayer layer = new NeuronLayer(Neuron.INPUT);
    for(int i=0;i<100000;++i) layers.add(layer);
    NeuralNetwork network = new NeuralNetwork(layers);
    List<Double> data = Arrays.asList(0.0);
    System.out.printf("Network testing %s -> %s", data, network.compute(data));
  }
  
  private static NeuronLayer inputLayer(int nodes) {
    List<Neuron> neurons = new ArrayList<>(nodes);
    for(int i=0;i<nodes;++i) {
      neurons.add(Neuron.INPUT);
    }
    return new NeuronLayer(neurons);
  }
}