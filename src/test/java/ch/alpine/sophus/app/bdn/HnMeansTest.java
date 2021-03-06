// code by jph
package ch.alpine.sophus.app.bdn;

import java.io.IOException;

import ch.alpine.sophus.bm.BiinvariantMean;
import ch.alpine.sophus.hs.hn.HnWeierstrassCoordinate;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.nrm.NormalizeTotal;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.NormalDistribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.UniformDistribution;
import junit.framework.TestCase;

public class HnMeansTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    Distribution distribution = NormalDistribution.standard();
    for (HnMeans hnMeans : HnMeans.values()) {
      BiinvariantMean biinvariantMean = Serialization.copy(hnMeans).get();
      for (int d = 1; d < 5; ++d) {
        final int fd = d;
        Tensor sequence = Array.of(l -> HnWeierstrassCoordinate.toPoint(RandomVariate.of(distribution, fd)), 10);
        Tensor weights = NormalizeTotal.FUNCTION.apply(RandomVariate.of(UniformDistribution.unit(), 10));
        biinvariantMean.mean(sequence, weights);
      }
    }
  }
}
