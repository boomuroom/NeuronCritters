package com.kurhan.neurons;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class NeuralNetwork {
  
  public final List<NeuronLayer> layers;
  
  public NeuralNetwork(List<NeuronLayer> layers) {
    this.layers = Collections.unmodifiableList(new ArrayList<>(layers));
  }
  
  public NeuralNetwork(NeuronLayer ... layers) {
    this(Arrays.asList(layers));
  }
  
  public List<Double> compute(List<Double> inputs) {
    List<List<Double>> layerInput = split(inputs);
    NeuronLayer layer = layers.get(0);
    List<Double> layerOutput = layer.compute(layerInput);
    for(int i=1;i<layers.size();++i) {
      layer = layers.get(i);
      layerInput = pack(layer.neuronCount(), layerOutput);
      layerOutput = layer.compute(layerInput);
    }
    return layerOutput;
  }
  
  public static NeuralNetwork randomNetwork(int inputs, int outputs) {
    int middles = (inputs + outputs) / 2;
    List<Neuron> input = new ArrayList<>(inputs);
    List<Neuron> middle = new ArrayList<>(middles);
    List<Neuron> output = new ArrayList<>(outputs);
    Supplier<Double> weightSupplier = () -> Math.random() * 10 - 5;
    Supplier<Double> toleranceSupplier = () -> Math.random();
    
    for(int i=0;i<inputs;++i) {
      input.add(new Neuron(0, 1));
    }
    
    for(int i=0;i<middles;++i) {
      List<Double> weights = new ArrayList<>();
      for(int j=0;j<inputs;++j) {
        weights.add(weightSupplier.get());
      }
      middle.add(new Neuron(toleranceSupplier.get(), weights));
    }
    
    for(int i=0;i<outputs;++i) {
      List<Double> weights = new ArrayList<>();
      for(int j=0;j<middles;++j) {
        weights.add(weightSupplier.get());
      }
      output.add(new Neuron(toleranceSupplier.get(), weights));
    }
    NeuronLayer inputLayer = new NeuronLayer(input);
    NeuronLayer middleLayer = new NeuronLayer(middle);
    NeuronLayer outputLayer = new NeuronLayer(output);
    return new NeuralNetwork(inputLayer, middleLayer, outputLayer);
  }
  
  private static List<List<Double>> pack(int copies, List<Double> values) {
    List<List<Double>> result = new ArrayList<>(copies);
    for(int i=0;i<copies;++i) {
      result.add(values);
    }
    return result;
  }
  
  private static List<List<Double>> split(List<Double> values) {
    List<List<Double>> result = new ArrayList<>(values.size());
    for(double value : values) {
      result.add(Collections.singletonList(value));
    }
    return result;
  }
  
  @Override
  public String toString() {
    return String.format("%s%s", getClass().getName(), layers);
  }
}
