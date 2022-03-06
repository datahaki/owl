// code by jph
package ch.alpine.sophus.demo.decim;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.ren.AxesRender;
import ch.alpine.java.ren.PathRender;
import ch.alpine.sophus.api.Geodesic;
import ch.alpine.sophus.decim.CurveDecimation;
import ch.alpine.sophus.decim.LineDistances;
import ch.alpine.sophus.demo.ControlPointsDemo;
import ch.alpine.sophus.gds.ManifoldDisplay;
import ch.alpine.sophus.gds.ManifoldDisplays;
import ch.alpine.sophus.lie.se2c.Se2CoveringExponential;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.img.ColorDataIndexed;
import ch.alpine.tensor.img.ColorDataLists;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.pdf.c.UniformDistribution;

/* package */ class BulkDecimationDemo extends ControlPointsDemo {
  private static final ColorDataIndexed COLOR_DATA_INDEXED_DRAW = ColorDataLists._097.cyclic().deriveWithAlpha(192);
  private static final Stroke STROKE = //
      new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3 }, 0);

  public BulkDecimationDemo() {
    super(true, ManifoldDisplays.SE2_R2);
    Distribution dX = UniformDistribution.of(-3, 3);
    Distribution dY = NormalDistribution.of(0, .3);
    Distribution dA = NormalDistribution.of(1, .5);
    Tensor tensor = Tensor.of(Array.of(l -> Tensors.of( //
        RandomVariate.of(dX), RandomVariate.of(dY), RandomVariate.of(dA)), 4).stream() //
        .map(Se2CoveringExponential.INSTANCE::exp));
    setControlPointsSe2(tensor);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    AxesRender.INSTANCE.render(geometricLayer, graphics);
    Tensor sequence = getGeodesicControlPoints();
    int length = sequence.length();
    if (0 == length)
      return;
    ManifoldDisplay manifoldDisplay = manifoldDisplay();
    Geodesic geodesicInterface = manifoldDisplay.geodesic();
    graphics.setColor(Color.LIGHT_GRAY);
    graphics.setStroke(STROKE);
    RenderQuality.setQuality(graphics);
    graphics.setStroke(new BasicStroke(1));
    renderControlPoints(geometricLayer, graphics);
    Tensor domain = Subdivide.of(0, 1, 10);
    {
      PathRender pathRender = new PathRender(COLOR_DATA_INDEXED_DRAW.getColor(0));
      for (int index = 1; index < sequence.length(); ++index) {
        Tensor tensor = domain.map(geodesicInterface.curve(sequence.get(index - 1), sequence.get(index)));
        pathRender.setCurve(tensor, false);
        pathRender.render(geometricLayer, graphics);
      }
    }
    CurveDecimation curveDecimation = CurveDecimation.of( //
        LineDistances.STANDARD.supply(manifoldDisplay.hsManifold()), //
        RealScalar.ONE);
    Tensor decimate = curveDecimation.apply(sequence);
    {
      PathRender pathRender = new PathRender(COLOR_DATA_INDEXED_DRAW.getColor(1));
      for (int index = 1; index < decimate.length(); ++index) {
        Tensor tensor = domain.map(geodesicInterface.curve(decimate.get(index - 1), decimate.get(index)));
        pathRender.setCurve(tensor, false);
        pathRender.render(geometricLayer, graphics);
      }
    }
  }

  public static void main(String[] args) {
    new BulkDecimationDemo().setVisible(1200, 600);
  }
}
