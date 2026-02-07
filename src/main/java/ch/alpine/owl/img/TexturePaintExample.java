// code from google
package ch.alpine.owl.img;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class TexturePaintExample extends JFrame {
  public TexturePaintExample() {
    setTitle("TexturePaint Example");
    setSize(400, 300);
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);
    add(new TexturePanel());
  }

  static void main() {
    new TexturePaintExample().setVisible(true);
  }
}

class TexturePanel extends JComponent {
  private BufferedImage textureImage;
  private TexturePaint texturePaint;

  public TexturePanel() {
    // Create a simple texture image (e.g., a checkerboard pattern)
    int textureWidth = 20;
    int textureHeight = 20;
    textureImage = new BufferedImage(textureWidth, textureHeight, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2d = textureImage.createGraphics();
    // Draw a checkerboard pattern onto the texture image
    for (int i = 0; i < textureWidth / 10; i++) {
      for (int j = 0; j < textureHeight / 10; j++) {
        if ((i + j) % 2 == 0) {
          g2d.setColor(Color.LIGHT_GRAY);
        } else {
          g2d.setColor(Color.DARK_GRAY);
        }
        g2d.fillRect(i * 10, j * 10, 10, 10);
      }
    }
    g2d.dispose();
    // Create the TexturePaint object
    // The anchor rectangle defines how the texture image is tiled.
    // Here, it's set to the size of the texture image, causing it to tile repeatedly.
    Rectangle anchorRect = new Rectangle(0, 0, textureImage.getWidth(), textureImage.getHeight());
    texturePaint = new TexturePaint(textureImage, anchorRect);
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2d = (Graphics2D) g;
    // Set the TexturePaint as the current paint for drawing
    g2d.setPaint(texturePaint);
    // Fill a rectangle with the texture
    g2d.fillRect(50, 50, 150, 100);
    // Fill an ellipse with the texture
    g2d.fill(new Ellipse2D.Double(220, 70, 100, 100));
  }
}
