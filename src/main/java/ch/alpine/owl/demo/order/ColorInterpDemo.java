// code by jph
package ch.alpine.owl.demo.order;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;

import ch.alpine.ascona.util.win.AbstractDemo;
import ch.alpine.ascona.util.win.LookAndFeels;
import ch.alpine.bridge.awt.Cielab;
import ch.alpine.bridge.gfx.GeometricLayer;
import ch.alpine.bridge.ref.ann.ReflectionMarker;
import ch.alpine.bridge.ref.util.ToolbarFieldsEditor;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.io.ImageFormat;
import ch.alpine.tensor.io.Primitives;
import ch.alpine.tensor.red.Times;

/** uses ColorSpace.CS_CIEXYZ to interpolate colors */
@ReflectionMarker
public class ColorInterpDemo extends AbstractDemo {
  private static final ColorSpace COLOR_SPACE = ColorSpace.getInstance(ColorSpace.CS_CIEXYZ);
  public Color c1 = Color.RED;
  public Color c2 = Color.BLUE;
  public Tensor offset = Tensors.vector(0, 0, 0);
  public Tensor weights = Tensors.vector(1, 1, 1);

  public ColorInterpDemo() {
    ToolbarFieldsEditor.add(this, timerFrame.jToolBar);
  }

  private static final int HEIGHT = 100;
  private static final int HEIGHT_INCR = 110;

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    float[] rgba1 = new float[4];
    c1.getComponents(rgba1);
    float[] rgba2 = new float[4];
    c2.getComponents(rgba2);
    // ---
    {
      Tensor tensor = Tensors.of(Subdivide.of(Tensors.vectorFloat(rgba1), Tensors.vectorFloat(rgba2), 255));
      tensor = tensor.multiply(RealScalar.of(255));
      BufferedImage bufferedImage = ImageFormat.of(tensor);
      graphics.drawImage(bufferedImage, 0, 0, bufferedImage.getWidth() * 2, HEIGHT, null);
    }
    // ---
    int piy = HEIGHT_INCR;
    {
      float[] xyz1 = COLOR_SPACE.fromRGB(rgba1);
      float[] xyz2 = COLOR_SPACE.fromRGB(rgba2);
      Tensor result = Tensors.empty();
      for (Tensor xyz : Subdivide.of( //
          Times.of(Tensors.vectorFloat(xyz1), weights).add(offset), //
          Times.of(Tensors.vectorFloat(xyz2), weights).add(offset), //
          255)) {
        float[] rgb = COLOR_SPACE.toRGB(Primitives.toFloatArray(xyz));
        Tensor append = Tensors.vectorFloat(rgb);
        result.append(append.multiply(RealScalar.of(255)).append(RealScalar.of(255)));
      }
      BufferedImage bufferedImage = ImageFormat.of(Tensors.of(result));
      graphics.drawImage(bufferedImage, 0, piy, bufferedImage.getWidth() * 2, HEIGHT, null);
      piy += HEIGHT_INCR;
    }
    for (Cielab cielab : Cielab.values()) {
      float[] xyz1 = COLOR_SPACE.fromRGB(rgba1);
      float[] xyz2 = COLOR_SPACE.fromRGB(rgba2);
      Tensor result = Tensors.empty();
      Tensor lab1 = cielab.xyz2lab(Tensors.vectorFloat(xyz1));
      Tensor lab2 = cielab.xyz2lab(Tensors.vectorFloat(xyz2));
      for (Tensor lab : Subdivide.of( //
          Times.of(lab1, weights).add(offset), //
          Times.of(lab2, weights).add(offset), //
          255)) {
        Tensor xyz = cielab.lab2xyz(lab);
        float[] rgb = COLOR_SPACE.toRGB(Primitives.toFloatArray(xyz));
        Tensor append = Tensors.vectorFloat(rgb);
        result.append(append.multiply(RealScalar.of(255)).append(RealScalar.of(255)));
      }
      BufferedImage bufferedImage = ImageFormat.of(Tensors.of(result));
      graphics.drawImage(bufferedImage, 0, piy, bufferedImage.getWidth() * 2, HEIGHT, null);
      piy += HEIGHT_INCR;
    }
  }

  public static void main(String[] args) {
    LookAndFeels.DARK.updateUI();
    new ColorInterpDemo().setVisible(1000, 600);
  }
}
