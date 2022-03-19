// code by jph
package ch.alpine.sophus.demo.misc;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.Arrays;

import org.jfree.chart.JFreeChart;

import ch.alpine.java.fig.ListPlot;
import ch.alpine.java.fig.Spectrogram;
import ch.alpine.java.fig.VisualSet;
import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.ref.ann.FieldClip;
import ch.alpine.java.ref.ann.FieldFuse;
import ch.alpine.java.ref.ann.FieldInteger;
import ch.alpine.java.ref.ann.FieldPreferredWidth;
import ch.alpine.java.ref.ann.FieldSlider;
import ch.alpine.java.ref.ann.ReflectionMarker;
import ch.alpine.java.ref.util.ToolbarFieldsEditor;
import ch.alpine.sophus.ext.api.AbstractGeodesicDisplayDemo;
import ch.alpine.sophus.ext.dis.R2Display;
import ch.alpine.sophus.math.noise.ColoredNoise;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.sca.Clips;

@ReflectionMarker
public class ColoredNoiseDemo extends AbstractGeodesicDisplayDemo {
  @FieldSlider
  @FieldClip(min = "0", max = "2")
  @FieldPreferredWidth(250)
  public Scalar alpha = RealScalar.of(2);
  @FieldPreferredWidth(150)
  @FieldInteger
  public Scalar length = RealScalar.of(300);
  @FieldFuse(value = "generate")
  public Boolean generate = true;
  // ---
  private JFreeChart jFreeChart;
  private JFreeChart spectrogra;

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
    Tensor domain = Range.of(0, values.length());
    {
      VisualSet visualSet = new VisualSet();
      visualSet.add(domain, values);
      visualSet.getAxisX().setClip(Clips.interval(0, values.length() - 1));
      jFreeChart = ListPlot.of(visualSet, true);
    }
    {
      VisualSet visualSet = new VisualSet();
      visualSet.add(domain, values);
      spectrogra = Spectrogram.of(visualSet);
    }
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    float width = geometricLayer.model2pixelWidth(RealScalar.of(15));
    int piw = (int) width;
    int pih = (int) (width * 0.4);
    jFreeChart.draw(graphics, new Rectangle(0, 0, piw, pih));
    spectrogra.draw(graphics, new Rectangle(0, pih, piw, pih));
  }

  public static void main(String[] args) {
    new ColoredNoiseDemo().setVisible(1000, 800);
  }
}
