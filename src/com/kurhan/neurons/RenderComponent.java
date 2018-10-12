package com.kurhan.neurons;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import java.awt.Graphics;

public class RenderComponent extends JComponent {
  
  private static final long serialVersionUID = 0xFEE1DEAD;
  private Renderer renderer;
  
  public RenderComponent() { }
  
  public RenderComponent(Renderer renderer) {
    this.renderer = renderer;
  }
  
  
  public void setRenderer(Renderer renderer) {
    this.renderer = renderer;
  }
  
  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    if(renderer != null) {
      renderer.render(g, getSize());
    }
    SwingUtilities.invokeLater( () -> {
      this.revalidate();
      this.repaint();
    });
  }
}