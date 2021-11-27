// code by jph
package ch.alpine.sophus.demo.misc;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.Arrays;

import org.jfree.chart.JFreeChart;

import ch.alpine.java.fig.ListPlot;
import ch.alpine.java.fig.VisualSet;
import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.ref.ToolbarFieldsEditor;
import ch.alpine.java.ref.ann.FieldFuse;
import ch.alpine.java.ref.ann.FieldInteger;
import ch.alpine.java.ref.ann.FieldPreferredWidth;
import ch.alpine.java.ref.ann.ReflectionMarker;
import ch.alpine.sophus.gds.GeodesicDisplayDemo;
import ch.alpine.sophus.gds.R2Display;
import ch.alpine.sophus.math.noise.ColoredNoise;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.pdf.RandomVariate;

@ReflectionMarker
public class ColoredNoiseDemo extends GeodesicDisplayDemo {
  @FieldPreferredWidth(width = 150)
  public Scalar alpha = RealScalar.of(2);
  @FieldPreferredWidth(width = 150)
  @FieldInteger
  public Scalar length = RealScalar.of(300);
  @FieldFuse(text = "generate")
  public Boolean generate = true;
  // ---
  private JFreeChart jFreeChart;

  // private final Tensor vector;
  public ColoredNoiseDemo() {
    super(Arrays.asList(R2Display.INSTANCE));
    ToolbarFieldsEditor.add(this, timerFrame.jToolBar).addUniversalListener(this::compute);
    // ---
    timerFrame.geometricComponent.setRotatable(false);
    compute();
  }

  private void compute() {
    ColoredNoise coloredNoise = new ColoredNoise(alpha.number().doubleValue());
    Tensor values = RandomVariate.of(coloredNoise, length.number().intValue());
    VisualSet visualSet = new VisualSet();
    visualSet.add(Subdivide.of(0, 1, values.length() - 1), values);
    jFreeChart = ListPlot.of(visualSet, true);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    Point2D point2d = geometricLayer.toPoint2D(0, 0);
    float width = geometricLayer.model2pixelWidth(RealScalar.of(10));
    jFreeChart.draw(graphics, new Rectangle((int) point2d.getX(), (int) point2d.getY(), (int) width, (int) (width * 2 / 3)));
  }

  public static void main(String[] args) {
    new ColoredNoiseDemo().setVisible(1000, 800);
  }
}
