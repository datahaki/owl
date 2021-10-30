// code by jph
package ch.alpine.sophus.demo.usr;

import java.io.IOException;

import ch.alpine.java.fig.ArrayPlot;
import ch.alpine.sophus.lie.se2.Se2Group;
import ch.alpine.sophus.lie.se2.Se2GroupElement;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.ext.HomeDirectory;
import ch.alpine.tensor.img.ColorDataGradients;
import ch.alpine.tensor.img.Raster;
import ch.alpine.tensor.io.Export;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.red.Min;
import ch.alpine.tensor.red.Nest;

/* package */ class MendelbretDemo implements TensorUnaryOperator {
  private final Tensor c;

  public MendelbretDemo(Tensor c) {
    this.c = c;
  }

  @Override
  public Tensor apply(Tensor zn) {
    Se2GroupElement gzn = Se2Group.INSTANCE.element(zn);
    Tensor g2 = gzn.combine(zn);
    return Se2Group.INSTANCE.element(g2).combine(c);
  }

  public static void main(String[] args) throws IOException {
    Tensor _x = Subdivide.of(-4, 4, 512);
    Tensor _y = Subdivide.of(-4, 4, 512);
    Tensor image = Array.zeros(_x.length(), _y.length());
    for (int x = 0; x < _x.length(); ++x) {
      for (int y = 0; y < _y.length(); ++y) {
        Tensor c = Tensors.of(_x.Get(x), RealScalar.ZERO, _y.get(y));
        MendelbretDemo mendelbretDemo = new MendelbretDemo(c);
        Tensor tensor = Nest.of(mendelbretDemo, c, 1);
        // if (Scalars.lessThan(Norm._2.ofVector(tensor.extract(0, 2)), RealScalar.of(2)))
        // image.set(RealScalar.ONE, x, y);
        image.set(Min.of(Vector2Norm.of(tensor.extract(0, 2)), RealScalar.of(1)), x, y);
      }
    }
    Tensor tensor = Raster.of(image, ColorDataGradients.CLASSIC);
    Export.of(HomeDirectory.Pictures(MendelbretDemo.class.getSimpleName() + ".png"), tensor);
  }
}
