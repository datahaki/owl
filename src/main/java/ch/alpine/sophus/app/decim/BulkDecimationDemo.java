// code by jph
package ch.alpine.sophus.app.decim;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.owl.gui.ren.AxesRender;
import ch.alpine.owl.gui.win.GeometricLayer;
import ch.alpine.sophus.decim.CurveDecimation;
import ch.alpine.sophus.decim.LineDistances;
import ch.alpine.sophus.gds.GeodesicDisplays;
import ch.alpine.sophus.gds.ManifoldDisplay;
import ch.alpine.sophus.gui.ren.PathRender;
import ch.alpine.sophus.gui.win.ControlPointsDemo;
import ch.alpine.sophus.lie.se2c.Se2CoveringExponential;
import ch.alpine.sophus.math.Geodesic;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.img.ColorDataIndexed;
import ch.alpine.tensor.img.ColorDataLists;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.NormalDistribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.UniformDistribution;

/* package */ class BulkDecimationDemo extends ControlPointsDemo {
  private static final ColorDataIndexed COLOR_DATA_INDEXED_DRAW = ColorDataLists._097.cyclic().deriveWithAlpha(192);
  private static final Stroke STROKE = //
      new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3 }, 0);

  public BulkDecimationDemo() {
    super(true, GeodesicDisplays.SE2_R2);
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
    ManifoldDisplay geodesicDisplay = manifoldDisplay();
    Geodesic geodesicInterface = geodesicDisplay.geodesicInterface();
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
        LineDistances.STANDARD.supply(geodesicDisplay.hsManifold()), //
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
