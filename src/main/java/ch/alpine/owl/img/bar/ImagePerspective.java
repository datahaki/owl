// code by jph
package ch.alpine.owl.img.bar;

import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Reverse;
import ch.alpine.tensor.img.ImageResize;
import ch.alpine.tensor.io.ImageFormat;
import ch.alpine.tensor.itp.Interpolation;
import ch.alpine.tensor.itp.LinearInterpolation;
import ch.alpine.tensor.jet.LinearFractionalTransform;
import ch.alpine.tensor.mat.sv.SingularValueDecomposition;
import ch.alpine.tensor.opt.nd.CoordinateBoundingBox;
import ch.alpine.tensor.opt.nd.CoordinateBounds;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.sca.Ceiling;
import ch.alpine.tensor.sca.Round;
import ch.alpine.tensor.sca.pow.Sqrt;

public enum ImagePerspective {
  ;
  public static class Format {
    public Tensor values;
    public final CoordinateBoundingBox box;
    public final Scalar factor;

    public Format(Tensor points, long pixels) {
      Tensor mean = Mean.of(points);
      Tensor of = Tensor.of(points.stream().map(r -> r.subtract(mean)));
      values = SingularValueDecomposition.of(of).values();
      box = CoordinateBounds.of(points);
      boolean s1 = Scalars.lessEquals(box.clip(0).width(), box.clip(1).width());
      boolean s2 = Scalars.lessEquals(values.Get(0), values.Get(1));
      if (s1 != s2) {
        System.out.println(" SWAP !!!");
        values = Reverse.of(values);
      }
      Scalar scalar = values.Get(0).multiply(values.Get(1));
      factor = Sqrt.FUNCTION.apply(scalar.divide(RealScalar.of(pixels))).reciprocal();
    }

    public int h() {
      return Round.intValueExact(values.Get(0).multiply(factor));
    }

    public int w() {
      return Round.intValueExact(values.Get(1).multiply(factor));
    }
  }

  public static BufferedImage rectify(BufferedImage uncropped, Tensor points, long pixels) {
    Format format = new Format(points, pixels);
    CoordinateBoundingBox box = format.box;
    int x = box.clip(1).min().number().intValue();
    int y = box.clip(0).min().number().intValue();
    int w = box.clip(1).max().number().intValue() - x;
    int h = box.clip(0).max().number().intValue() - y;
    BufferedImage subimage = uncropped.getSubimage(x, y, w + 1, h + 1);
    Scalar factor = format.factor;
    Tensor resized = ImageFormat.from(ImageResize.of(subimage, //
        Ceiling.intValueExact(RealScalar.of(w).multiply(factor)) + 1, //
        Ceiling.intValueExact(RealScalar.of(h).multiply(factor)) + 1, AffineTransformOp.TYPE_BICUBIC));
    Tensor sft = Tensors.vectorInt(y, x).negate();
    Tensor pnu = Tensor.of(points.stream().map(sft::add)).multiply(factor);
    return ImageFormat.of(rectify(resized, pnu, format.w(), format.h()));
  }

  private static Tensor rectify(Tensor src, Tensor points, int width, int height) {
    Tensor reference = Tensors.matrixInt( //
        new int[][] { { 0, 0 }, { 0, width }, { height, width }, { height, 0 } });
    reference = reference.maps(RealScalar.of(-0.5)::add);
    LinearFractionalTransform lft = LinearFractionalTransform.fit(reference, points);
    Interpolation interpolation = LinearInterpolation.of(src);
    return Tensors.matrix((i, j) -> //
    interpolation.get(lft.apply(Tensors.vectorDouble(i, j))), height, width);
  }
}
