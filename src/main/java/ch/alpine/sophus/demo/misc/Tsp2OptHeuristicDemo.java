// code by jph
package ch.alpine.sophus.demo.misc;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;

import org.jfree.chart.JFreeChart;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.java.fig.ListPlot;
import ch.alpine.java.fig.VisualSet;
import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.ref.ann.FieldInteger;
import ch.alpine.java.ref.ann.ReflectionMarker;
import ch.alpine.java.ref.util.ToolbarFieldsEditor;
import ch.alpine.sophus.api.Geodesic;
import ch.alpine.sophus.ext.api.ControlPointsDemo;
import ch.alpine.sophus.ext.dis.ManifoldDisplay;
import ch.alpine.sophus.ext.dis.ManifoldDisplays;
import ch.alpine.sophus.ext.dis.R2Display;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.api.ScalarTensorFunction;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.lie.Symmetrize;
import ch.alpine.tensor.mat.SymmetricMatrixQ;
import ch.alpine.tensor.opt.ts.Tsp2OptHeuristic;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;

@ReflectionMarker
public class Tsp2OptHeuristicDemo extends ControlPointsDemo {
  public static class Param {
    @FieldInteger
    public Scalar attempts = RealScalar.of(20);
    public Boolean active = false;
  }

  private final Param param = new Param();
  private final Tsp2OptHeuristic tsp2OptHeuristic;
  private final Tensor points = Tensors.empty();

  public Tsp2OptHeuristicDemo() {
    super(false, ManifoldDisplays.R2_H2_S2);
    setPositioningEnabled(false);
    ToolbarFieldsEditor.add(param, timerFrame.jToolBar);
    // ---
    Distribution distribution = UniformDistribution.of(-4, 4);
    setControlPointsSe2(RandomVariate.of(distribution, 200, 3));
    // TODO OWL random sample
    // TODO refresh when manifold selection changes
    Tensor matrix = distanceMatrix(getGeodesicControlPoints());
    tsp2OptHeuristic = new Tsp2OptHeuristic(matrix, new Random());
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (param.active) {
      for (int i = 0; i < param.attempts.number().intValue(); ++i)
        tsp2OptHeuristic.next();
      points.append(Tensors.of(RealScalar.of(points.length()), tsp2OptHeuristic.cost()));
    }
    ManifoldDisplay manifoldDisplay = manifoldDisplay();
    Geodesic geodesic = manifoldDisplay.geodesic();
    RenderQuality.setQuality(graphics);
    Tensor sequence = getGeodesicControlPoints();
    Color color = Color.BLACK;
    graphics.setColor(color);
    for (Tensor p : sequence) {
      Point2D point2d = geometricLayer.toPoint2D(manifoldDisplay.toPoint(p));
      graphics.fillRect((int) point2d.getX() - 1, (int) point2d.getY() - 1, 3, 3);
    }
    // for (int index = 0; index < sequence.length(); ++index) {
    // PointsRender pointsRender = new PointsRender(color, color);
    // pointsRender.show(manifoldDisplay()::matrixLift, getControlPointShape(), Tensors.of(sequence.get(index))).render(geometricLayer, graphics);
    // }
    int[] index = tsp2OptHeuristic.index();
    // TODO OWL is there a smart way to select how to draw lines
    int res = manifoldDisplay.equals(R2Display.INSTANCE) ? 1 : 10;
    Tensor domain = Subdivide.of(0.0, 1.0, res);
    graphics.setColor(Color.CYAN);
    for (int i = 0; i < index.length; ++i) {
      Tensor p = sequence.get(index[i]);
      Tensor q = sequence.get(index[(i + 1) % index.length]);
      ScalarTensorFunction curve = geodesic.curve(p, q);
      Path2D line = geometricLayer.toPath2D(domain.map(curve));
      graphics.draw(line);
    }
    VisualSet visualSet = new VisualSet();
    visualSet.add(points);
    JFreeChart jFreeChart = ListPlot.of(visualSet, true);
    jFreeChart.draw(graphics, new Rectangle2D.Double(0, 0, 300, 200));
  }

  public Tensor distanceMatrix(Tensor sequence) {
    ManifoldDisplay manifoldDisplay = manifoldDisplay();
    TensorUnaryOperator tuo = manifoldDisplay.metricBiinvariant().distances(manifoldDisplay.hsManifold(), sequence);
    Tensor matrix = Tensor.of(sequence.stream().map(tuo));
    return SymmetricMatrixQ.of(matrix) //
        ? matrix
        : Symmetrize.of(matrix);
  }

  public static void main(String[] args) {
    new Tsp2OptHeuristicDemo().setVisible(1000, 600);
  }
}
