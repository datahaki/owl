// code by jph
package ch.alpine.owl.bot.r2;

import java.io.IOException;
import java.util.List;

import ch.alpine.owl.math.ImageGradient;
import ch.alpine.owl.util.img.FloodFill2D;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.ext.HomeDirectory;
import ch.alpine.tensor.io.Export;
import ch.alpine.tensor.qty.Timing;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;

/* package */ enum FloodFill2DDemo {
  ;
  public static final ScalarUnaryOperator GRAYSCALE = new ScalarUnaryOperator() {
    final Scalar scale = RealScalar.of(63.0);
    final Scalar middl = RealScalar.of(128.0);
    final Clip clip = Clips.interval(0, 255);

    @Override // from Function
    public Scalar apply(Scalar scalar) {
      Scalar r = scalar.multiply(scale).add(middl);
      return clip.isInside(r) ? r : middl;
    }
  };

  static void main() throws IOException {
    final Tensor tensor = R2ImageRegions.inside_0f5c_2182_charImage();
    int ttl = 30;
    // ---
    System.out.println("export image " + Dimensions.of(tensor));
    Export.of(HomeDirectory.Pictures.resolve("image.png"), tensor);
    Timing timing = Timing.started();
    Tensor cost_raw = FloodFill2D.of(tensor, ttl);
    System.out.println("floodfill    " + timing.seconds());
    System.out.println("export cost  " + Dimensions.of(cost_raw));
    Export.of(HomeDirectory.Pictures.resolve("image_cost_raw.png"), cost_raw);
    // ---
    Tensor cost = cost_raw;
    // MeanFilter.of(cost_raw, 2);
    // ---
    Tensor field_copy = ImageGradient.rotated(cost).multiply(RealScalar.of(1.0));
    System.out.println("field: " + Dimensions.of(field_copy));
    Tensor dx = field_copy.get(Tensor.ALL, Tensor.ALL, 0);
    Tensor dy = field_copy.get(Tensor.ALL, Tensor.ALL, 1);
    dx = dx.maps(GRAYSCALE);
    dy = dy.maps(GRAYSCALE);
    List<Integer> dims = Dimensions.of(dx);
    Tensor visual = Array.of(_ -> RealScalar.of(255), dims.get(0), dims.get(1), 4);
    visual.set(dx, Tensor.ALL, Tensor.ALL, 0);
    visual.set(dy, Tensor.ALL, Tensor.ALL, 1);
    // Export.of(UserHome.Pictures("cost_dx.png"), dx);
    // Export.of(UserHome.Pictures("cost_dy.png"), dy);
    Export.of(HomeDirectory.Pictures.resolve("visual.png"), visual);
  }
}
