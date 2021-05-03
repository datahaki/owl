// code by jph
package ch.alpine.sophus.app.bd2;

import java.io.IOException;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.ext.HomeDirectory;
import ch.alpine.tensor.img.ColorDataIndexed;
import ch.alpine.tensor.img.ColorFormat;
import ch.alpine.tensor.img.Hue;
import ch.alpine.tensor.img.ImageResize;
import ch.alpine.tensor.img.StrictColorDataIndexed;
import ch.alpine.tensor.io.Export;
import ch.alpine.tensor.sca.Mod;

/* package */ enum HueColorData {
  ;
  private static final Mod MOD = Mod.function(1);

  public static ColorDataIndexed of(int max, int sep) {
    Tensor tensor = Tensors.reserve(max * sep);
    Scalar goldenAngle = RealScalar.of(0.38196601125010515180);
    Scalar offset = RealScalar.of(0.66);
    Tensor sats = Subdivide.of(1.0, 0.2, sep - 1);
    for (int index = 0; index < max; ++index) {
      for (Tensor sat : sats)
        tensor.append(ColorFormat.toVector(Hue.of(offset.number().doubleValue(), ((Scalar) sat).number().doubleValue(), 1.0, 1.0)));
      offset = MOD.apply(offset.add(goldenAngle));
    }
    return StrictColorDataIndexed.of(tensor);
  }

  public static void main(String[] args) throws IOException {
    ColorDataIndexed colorDataIndexed = of(10, 5);
    Tensor tensor = Range.of(0, colorDataIndexed.length()).map(Tensors::of).map(colorDataIndexed);
    tensor = ImageResize.nearest(tensor, 10);
    Export.of(HomeDirectory.Pictures("huecolordata.png"), tensor);
  }
}
