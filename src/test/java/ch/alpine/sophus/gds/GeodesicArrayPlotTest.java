// code by jph
package ch.alpine.sophus.gds;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Dot;
import ch.alpine.tensor.mat.DiagonalMatrix;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.NormalDistribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.red.Times;
import ch.alpine.tensor.sca.Chop;
import junit.framework.TestCase;

public class GeodesicArrayPlotTest extends TestCase {
  public void testSimple() {
    Distribution distribution = NormalDistribution.standard();
    Tensor a = RandomVariate.of(distribution, 3, 3);
    Tensor v = RandomVariate.of(distribution, 3);
    Tensor b = DiagonalMatrix.with(v);
    Tensor c = RandomVariate.of(distribution, 3, 3);
    Chop._09.requireClose(Dot.of(b, c), Times.of(v, c));
    Chop._09.requireClose(Dot.of(a, b, c), a.dot(Times.of(v, c)));
  }
}
