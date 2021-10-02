// code by jph
package ch.alpine.sophus.app.misc;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.owl.gui.ren.AxesRender;
import ch.alpine.sophus.gds.ManifoldDisplay;
import ch.alpine.sophus.gds.ManifoldDisplays;
import ch.alpine.sophus.gui.win.ControlPointsDemo;
import ch.alpine.sophus.hs.HsManifold;
import ch.alpine.sophus.lie.LieExponential;
import ch.alpine.sophus.lie.se2c.Se2CoveringExponential;
import ch.alpine.sophus.math.Exponential;
import ch.alpine.sophus.math.Geodesic;
import ch.alpine.sophus.ply.Arrowhead;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Join;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.api.ScalarTensorFunction;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.mat.Orthogonalize;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.sca.Sqrt;

/* package */ class Se2UnprojectDemo extends ControlPointsDemo {
  private static final Tensor ARROWHEAD = Arrowhead.of(0.5);

  public Se2UnprojectDemo() {
    super(false, ManifoldDisplays.SE2C_SE2);
    Tensor tensor = Tensors.fromString("{{0, 0, 0}, {5, 0, 1}}");
    setControlPointsSe2(tensor);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    AxesRender.INSTANCE.render(geometricLayer, graphics);
    RenderQuality.setQuality(graphics);
    renderControlPoints(geometricLayer, graphics);
    Tensor sequence = getControlPointsSe2();
    ManifoldDisplay manifoldDisplay = manifoldDisplay();
    HsManifold hsManifold = LieExponential.of(manifoldDisplay.lieGroup(), Se2CoveringExponential.INSTANCE);
    // ---
    Geodesic geodesicInterface = manifoldDisplay.geodesic();
    Tensor p = sequence.get(0);
    Tensor q = sequence.get(1);
    {
      ScalarTensorFunction curve = geodesicInterface.curve(p, q);
      Tensor tensor = Subdivide.of(-0.05, 1.05, 25).map(curve);
      Path2D path2d = geometricLayer.toPath2D(Tensor.of(tensor.stream().map(manifoldDisplay::toPoint)));
      graphics.setColor(Color.BLUE);
      graphics.draw(path2d);
    }
    Exponential exponential = hsManifold.exponential(p);
    Tensor log = exponential.log(q);
    Tensor matrix = Join.of(Tensors.of(log), IdentityMatrix.of(3));
    Tensor tensor = Orthogonalize.of(matrix).extract(0, 3);
    graphics.setColor(new Color(192, 192, 192, 64));
    Scalar nl = Vector2Norm.of(log);
    Scalar un = RealScalar.of(0.2).divide(Sqrt.FUNCTION.apply(nl));
    for (Tensor x : Subdivide.of(nl.zero(), nl, 11))
      for (Tensor y : Subdivide.of(un.negate(), un, 5))
        for (Tensor z : Subdivide.of(un.negate(), un, 5)) {
          Tensor px = Tensors.of(x, y, z);
          Tensor coord = px.dot(tensor);
          Tensor exp = exponential.exp(coord);
          // ---
          geometricLayer.pushMatrix(manifoldDisplay.matrixLift(exp));
          Path2D path2d = geometricLayer.toPath2D(ARROWHEAD);
          path2d.closePath();
          graphics.draw(path2d);
          geometricLayer.popMatrix();
        }
  }

  public static void main(String[] args) {
    new Se2UnprojectDemo().setVisible(1200, 600);
  }
}
