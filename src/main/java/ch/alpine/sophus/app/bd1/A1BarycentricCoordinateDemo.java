// code by jph
package ch.alpine.sophus.app.bd1;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.owl.gui.ren.AxesRender;
import ch.alpine.owl.gui.win.GeometricLayer;
import ch.alpine.sophus.app.lev.LogWeightingDemo;
import ch.alpine.sophus.gds.ManifoldDisplays;
import ch.alpine.sophus.gui.ren.PathRender;
import ch.alpine.sophus.opt.LogWeighting;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.api.ScalarTensorFunction;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.img.ColorDataIndexed;
import ch.alpine.tensor.img.ColorDataLists;

/* package */ abstract class A1BarycentricCoordinateDemo extends LogWeightingDemo {
  public A1BarycentricCoordinateDemo(List<LogWeighting> array) {
    super(true, ManifoldDisplays.R2_ONLY, array);
    // ---
    setControlPointsSe2(Tensors.fromString("{{0, 0, 0}, {1, 1, 0}, {2, 2, 0}}"));
    // ---
    timerFrame.geometricComponent.addRenderInterfaceBackground(AxesRender.INSTANCE);
  }

  @Override // from RenderInterface
  public final void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    renderControlPoints(geometricLayer, graphics);
    Tensor control = getGeodesicControlPoints();
    if (1 < control.length()) {
      Tensor support = control.get(Tensor.ALL, 0);
      Tensor funceva = control.get(Tensor.ALL, 1);
      // ---
      Tensor domain = domain(support);
      // ---
      Tensor sequence = support.map(this::lift);
      TensorUnaryOperator weightingInterface = operator(sequence);
      ScalarTensorFunction scalarTensorFunction = //
          point -> weightingInterface.apply(lift(point));
      Tensor basis = domain.map(scalarTensorFunction);
      {
        Tensor curve = Transpose.of(Tensors.of(domain, basis.dot(funceva)));
        new PathRender(Color.BLUE, 1.25f).setCurve(curve, false).render(geometricLayer, graphics);
      }
      ColorDataIndexed colorDataIndexed = ColorDataLists._097.cyclic();
      for (int index = 0; index < funceva.length(); ++index) {
        Color color = colorDataIndexed.getColor(index);
        Tensor curve = Transpose.of(Tensors.of(domain, basis.get(Tensor.ALL, index)));
        new PathRender(color, 1f).setCurve(curve, false).render(geometricLayer, graphics);
      }
    }
  }

  abstract Tensor domain(Tensor support);

  abstract Tensor lift(Scalar x);
}
