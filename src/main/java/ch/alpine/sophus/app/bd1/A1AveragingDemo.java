// code by jph
package ch.alpine.sophus.app.bd1;

import java.awt.BasicStroke;
import java.awt.Stroke;
import java.util.Arrays;

import ch.alpine.sophus.gds.ManifoldDisplay;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Join;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.red.Max;
import ch.alpine.tensor.red.Min;
import ch.alpine.tensor.sca.N;

/* package */ abstract class A1AveragingDemo extends AnAveragingDemo {
  static final Stroke STROKE = //
      new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3 }, 0);
  private static final Scalar MARGIN = RealScalar.of(2);

  public A1AveragingDemo(ManifoldDisplay manifoldDisplay) {
    super(Arrays.asList(manifoldDisplay));
  }

  final Tensor domain() {
    Tensor support = getControlPointsSe2().get(Tensor.ALL, 0).map(N.DOUBLE);
    Tensor subdiv = Subdivide.of( //
        support.stream().reduce(Min::of).orElseThrow().add(MARGIN.negate()), //
        support.stream().reduce(Max::of).orElseThrow().add(MARGIN), 100).map(N.DOUBLE);
    Tensor predom = Join.of(subdiv, support);
    return Tensor.of(predom.stream().distinct().sorted());
  }
}
