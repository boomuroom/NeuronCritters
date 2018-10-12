package com.kurhan.neurons;

import javax.swing.*;

public class Redrawer {
  public static void main(String[] args) {
    SwingUtilities.invokeLater( Redrawer::createAndShow );
  }
  
  private static void createAndShow() {
    JFrame frame = new JFrame();
    int[] count = new int[1];
    new Thread( () -> {
      while(true) {
        count[0]++;
      }
    }).start();
    RenderComponent comp = new RenderComponent( (g, size) -> {
      g.drawString(String.valueOf(count[0]), 0, 12);
    });
    frame.setContentPane(comp);
    frame.setSize(100, 100);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
  }
}