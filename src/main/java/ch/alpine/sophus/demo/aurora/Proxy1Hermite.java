// code by jph
package ch.alpine.sophus.demo.aurora;

import java.io.File;
import java.io.IOException;

import ch.alpine.sophus.ref.d1h.Hermite1Subdivisions;
import ch.alpine.tensor.Parallelize;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.ext.HomeDirectory;
import ch.alpine.tensor.sca.N;
import ch.alpine.tensor.sca.exp.Log;

/* package */ class Proxy1Hermite extends ProxyHermite {
  public Proxy1Hermite(String name, int levels) {
    super(name, levels);
  }

  private Scalar h1(Scalar lambda, Scalar mu) {
    return process(Hermite1Subdivisions.of(HS_EXPONENTIAL, HS_TRANSPORT, lambda, mu));
  }

  @Override // from HermiteArray
  Tensor compute(int rows, int cols) {
    Tensor lambda = N.DOUBLE.of(Subdivide.of(RationalScalar.of(-3, 4), RationalScalar.of(-1, 6), rows - 1));
    Tensor mu = N.DOUBLE.of(Subdivide.of(RationalScalar.of(-2, 1), RationalScalar.of(+5, 2), cols - 1));
    return Parallelize.matrix((i, j) -> h1(lambda.Get(i), mu.Get(j)), rows, cols);
    // return Parallelize.matrix((i, j) -> lambda.Get(i), rows, cols);
  }

  public static void main(String[] args) throws IOException {
    String name = "20190701T163225_01";
    // name = "20190701T170957_03";
    // name = "20190701T174152_03";
    int levels = 3;
    ProxyHermite proxyHermite = new Proxy1Hermite(name, levels);
    File folder = HomeDirectory.Pictures(proxyHermite.getClass().getSimpleName(), String.format("p_%1d", levels));
    folder.mkdirs();
    Tensor matrix = proxyHermite.getMatrix();
    HermiteArray.export(new File(folder, "id"), matrix);
    HermiteArray.export(new File(folder, "ln"), matrix.map(RealScalar.ONE::add).map(Log.FUNCTION));
  }
}
