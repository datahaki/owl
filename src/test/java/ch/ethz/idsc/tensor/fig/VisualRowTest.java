// code by gjoel, jph
package ch.ethz.idsc.tensor.fig;

import ch.ethz.idsc.owl.math.AssertFail;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import junit.framework.TestCase;

public class VisualRowTest extends TestCase {
  public void testConstructors() {
    Tensor domain = Tensors.fromString("{1, 2, 3, 4, 5}");
    Tensor values = RandomVariate.of(UniformDistribution.unit(), 5);
    Tensor points = Transpose.of(Tensors.of(domain, values));
    VisualSet visualSet = new VisualSet();
    VisualRow row1 = visualSet.add(points);
    VisualRow row2 = visualSet.add(domain, values);
    assertEquals(row1.points(), row2.points());
    assertEquals(visualSet.visualRows().size(), 2);
  }

  public void testFailNull() {
    AssertFail.of(() -> new VisualSet(null));
  }

  public void testPointNonMatrix() {
    VisualSet visualSet = new VisualSet();
    AssertFail.of(() -> visualSet.add(Tensors.vector(1, 2, 3, 4)));
    AssertFail.of(() -> visualSet.add(RealScalar.ZERO));
  }
}
