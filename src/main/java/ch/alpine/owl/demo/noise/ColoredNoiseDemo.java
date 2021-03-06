// code by jph
package ch.alpine.owl.demo.noise;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.swing.JButton;

import org.jfree.chart.JFreeChart;

import ch.alpine.java.awt.SpinnerLabel;
import ch.alpine.java.fig.ListPlot;
import ch.alpine.java.fig.VisualSet;
import ch.alpine.owl.gui.win.GeometricLayer;
import ch.alpine.owl.math.noise.ColoredNoise;
import ch.alpine.sophus.gds.GeodesicDisplayDemo;
import ch.alpine.sophus.gds.R2Display;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.pdf.RandomVariate;

/* package */ class ColoredNoiseDemo extends GeodesicDisplayDemo implements ActionListener {
  private final SpinnerLabel<Scalar> spinnerAlpha = new SpinnerLabel<>();
  private JFreeChart jFreeChart;

  // private final Tensor vector;
  public ColoredNoiseDemo() {
    super(Arrays.asList(R2Display.INSTANCE));
    {
      spinnerAlpha.setList(Subdivide.of(-2, 2, 8 * 2).stream().map(Scalar.class::cast).collect(Collectors.toList()));
      spinnerAlpha.setIndex(0);
      spinnerAlpha.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "alpha");
      spinnerAlpha.addSpinnerListener(v -> actionPerformed(null));
    }
    {
      JButton jButton = new JButton("generate");
      jButton.addActionListener(this);
      timerFrame.jToolBar.add(jButton);
    }
    // ---
    timerFrame.geometricComponent.setRotatable(false);
    actionPerformed(null);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    double alpha = spinnerAlpha.getValue().number().doubleValue();
    ColoredNoise coloredNoise = new ColoredNoise(alpha);
    Tensor values = RandomVariate.of(coloredNoise, 1000);
    VisualSet visualSet = new VisualSet();
    visualSet.add(Subdivide.of(0, 1, values.length() - 1), values);
    jFreeChart = ListPlot.of(visualSet);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    Point2D point2d = geometricLayer.toPoint2D(0, 0);
    float width = geometricLayer.model2pixelWidth(10);
    jFreeChart.draw(graphics, new Rectangle((int) point2d.getX(), (int) point2d.getY(), (int) width, (int) (width * 2 / 3)));
  }

  public static void main(String[] args) {
    new ColoredNoiseDemo().setVisible(1000, 800);
  }
}
