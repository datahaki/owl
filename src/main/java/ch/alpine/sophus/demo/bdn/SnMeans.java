// code by jph
package ch.alpine.sophus.demo.bdn;

import java.util.function.Supplier;

import ch.alpine.sophus.bm.BiinvariantMean;
import ch.alpine.sophus.hs.sn.SnBiinvariantMean;
import ch.alpine.sophus.hs.sn.SnFastMean;
import ch.alpine.sophus.hs.sn.SnPhongMean;
import ch.alpine.tensor.sca.Chop;

/** RMF(p,t,w)[x] == w.t for w = IDC(p,x) */
/* package */ enum SnMeans implements Supplier<BiinvariantMean> {
  EXACT(SnBiinvariantMean.of(Chop._05)), //
  FAST(SnFastMean.INSTANCE), //
  PHONG(SnPhongMean.INSTANCE), //
  ;

  private final BiinvariantMean biinvariantMean;

  private SnMeans(BiinvariantMean biinvariantMean) {
    this.biinvariantMean = biinvariantMean;
  }

  @Override
  public BiinvariantMean get() {
    return biinvariantMean;
  }
}
