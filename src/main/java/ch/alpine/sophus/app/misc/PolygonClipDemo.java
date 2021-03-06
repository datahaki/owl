// code by jph
package ch.alpine.sophus.app.misc;

import java.awt.Color;
import java.awt.Graphics2D;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.owl.gui.win.GeometricLayer;
import ch.alpine.sophus.app.lev.AbstractPlaceDemo;
import ch.alpine.sophus.app.lev.LeversRender;
import ch.alpine.sophus.gds.GeodesicDisplays;
import ch.alpine.sophus.gui.ren.PathRender;
import ch.alpine.sophus.gui.ren.PointsRender;
import ch.alpine.sophus.lie.se2.Se2Matrix;
import ch.alpine.sophus.ply.CogPoints;
import ch.alpine.sophus.ply.PolygonCentroid;
import ch.alpine.sophus.ply.SutherlandHodgmanAlgorithm;
import ch.alpine.sophus.ply.SutherlandHodgmanAlgorithm.PolyclipResult;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.img.ColorDataIndexed;
import ch.alpine.tensor.img.ColorDataLists;
import ch.alpine.tensor.io.ScalarArray;
import ch.alpine.tensor.lie.Cross;
import ch.alpine.tensor.lie.r2.CirclePoints;
import ch.alpine.tensor.red.Mean;

/* package */ class PolygonClipDemo extends AbstractPlaceDemo {
  private static final ColorDataIndexed COLOR_DATA_INDEXED = ColorDataLists._097.strict();
  private static final Tensor CIRCLE = CirclePoints.of(7).multiply(RealScalar.of(4));
  private static final SutherlandHodgmanAlgorithm POLYGON_CLIP = SutherlandHodgmanAlgorithm.of(CIRCLE);

  public PolygonClipDemo() {
    super(true, GeodesicDisplays.R2_ONLY);
    setControlPointsSe2(Tensor.of(CogPoints.of(4, RealScalar.of(5), RealScalar.of(-2)).stream().map(row -> row.append(RealScalar.ZERO))));
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    new PathRender(COLOR_DATA_INDEXED.getColor(3), 1.5f).setCurve(CIRCLE, true).render(geometricLayer, graphics);
    renderControlPoints(geometricLayer, graphics);
    Tensor sequence = getGeodesicControlPoints();
    new PathRender(COLOR_DATA_INDEXED.getColor(0), 1.5f).setCurve(sequence, true).render(geometricLayer, graphics);
    PolyclipResult polyclipResult = POLYGON_CLIP.apply(sequence);
    graphics.setColor(new Color(128, 255, 128, 128));
    Tensor result = polyclipResult.tensor();
    graphics.fill(geometricLayer.toPath2D(result));
    new PathRender(COLOR_DATA_INDEXED.getColor(1), 3.5f).setCurve(result, true).render(geometricLayer, graphics);
    {
      for (int index = 0; index < result.length(); ++index) {
        int cind = polyclipResult.belong().Get(index).number().intValue();
        Color color = COLOR_DATA_INDEXED.getColor(cind);
        PointsRender pointsRender = new PointsRender(color, Color.BLACK);
        pointsRender.show( //
            manifoldDisplay()::matrixLift, //
            manifoldDisplay().shape().multiply(RealScalar.of(2)), //
            Tensors.of(result.get(index))) //
            .render(geometricLayer, graphics);
      }
    }
    Tensor nsum = Array.zeros(2);
    {
      Scalar[] prop = ScalarArray.ofVector(polyclipResult.belong());
      Tensor[] array = result.stream().toArray(Tensor[]::new);
      for (int index = 0; index < result.length(); ++index) {
        int iprev = Math.floorMod(index - 1, result.length());
        Tensor a = array[iprev];
        Tensor b = array[index];
        int ap = prop[iprev].number().intValue();
        int bp = prop[index].number().intValue();
        Tensor point = Mean.of(Tensors.of(a, b));
        Tensor norma = Cross.of(b.subtract(a)).multiply(RealScalar.of(0.3));
        if (ap == 1 && bp == 1)
          norma = norma.negate();
        nsum = nsum.add(norma);
        geometricLayer.pushMatrix(Se2Matrix.translation(point));
        graphics.draw(geometricLayer.toLine2D(norma));
        geometricLayer.popMatrix();
      }
    }
    if (0 < result.length()) {
      Tensor centroid = PolygonCentroid.of(result);
      geometricLayer.pushMatrix(Se2Matrix.translation(centroid));
      graphics.draw(geometricLayer.toLine2D(nsum));
      geometricLayer.popMatrix();
    }
    LeversRender leversRender = LeversRender.of(manifoldDisplay(), result, null, geometricLayer, graphics);
    leversRender.renderIndexP();
    RenderQuality.setDefault(graphics);
    // new PathRender(COLOR_DATA_INDEXED.getColor(1), 2.5f).setCurve(HILBERT, false).render(geometricLayer, graphics);
  }

  public static void main(String[] args) {
    new PolygonClipDemo().setVisible(1000, 600);
  }
}
