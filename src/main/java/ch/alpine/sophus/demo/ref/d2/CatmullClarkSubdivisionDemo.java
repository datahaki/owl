// code by jph
package ch.alpine.sophus.demo.ref.d2;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.ref.ann.FieldClip;
import ch.alpine.java.ref.ann.FieldInteger;
import ch.alpine.java.ref.ann.FieldPreferredWidth;
import ch.alpine.java.ref.ann.FieldSlider;
import ch.alpine.java.ref.gui.ToolbarFieldsEditor;
import ch.alpine.sophus.crv.d2.Arrowhead;
import ch.alpine.sophus.demo.ControlPointsDemo;
import ch.alpine.sophus.gds.ManifoldDisplay;
import ch.alpine.sophus.gds.ManifoldDisplays;
import ch.alpine.sophus.math.Geodesic;
import ch.alpine.sophus.ref.d2.GeodesicCatmullClarkSubdivision;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.ArrayReshape;
import ch.alpine.tensor.red.Nest;

public class CatmullClarkSubdivisionDemo extends ControlPointsDemo {
  private static final Tensor ARROWHEAD_LO = Arrowhead.of(0.18);
  // ---
  @FieldSlider
  @FieldPreferredWidth(width = 100)
  @FieldInteger
  @FieldClip(min = "0", max = "5")
  public Scalar refine = RealScalar.of(2);

  CatmullClarkSubdivisionDemo() {
    super(false, ManifoldDisplays.SE2C_SE2);
    ToolbarFieldsEditor.add(this, timerFrame.jToolBar);
    // ---
    setControlPointsSe2(Tensors.fromString("{{0, 0, 0}, {1, 0, 0}, {2, 0, 0}, {0, 1, 0}, {1, 1, 0}, {2, 1, 0}}").multiply(RealScalar.of(2)));
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    renderControlPoints(geometricLayer, graphics);
    Tensor control = getGeodesicControlPoints();
    ManifoldDisplay manifoldDisplay = manifoldDisplay();
    Geodesic geodesicInterface = manifoldDisplay.geodesic();
    GeodesicCatmullClarkSubdivision catmullClarkSubdivision = //
        new GeodesicCatmullClarkSubdivision(geodesicInterface);
    Tensor refined = Nest.of( //
        catmullClarkSubdivision::refine, //
        ArrayReshape.of(control, 2, 3, 3), //
        refine.number().intValue());
    for (Tensor points : refined)
      for (Tensor point : points) {
        geometricLayer.pushMatrix(manifoldDisplay.matrixLift(point));
        Path2D path2d = geometricLayer.toPath2D(ARROWHEAD_LO);
        geometricLayer.popMatrix();
        int rgb = 128 + 32;
        path2d.closePath();
        graphics.setColor(new Color(rgb, rgb, rgb, 128 + 64));
        graphics.fill(path2d);
        graphics.setColor(Color.BLACK);
        graphics.draw(path2d);
      }
  }

  public static void main(String[] args) {
    new CatmullClarkSubdivisionDemo().setVisible(1000, 600);
  }
}
