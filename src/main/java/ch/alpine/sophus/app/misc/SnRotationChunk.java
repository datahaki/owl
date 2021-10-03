// code by jph
package ch.alpine.sophus.app.misc;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.win.RenderInterface;
import ch.alpine.sophus.hs.sn.SnRandomSample;
import ch.alpine.sophus.math.sample.RandomSample;
import ch.alpine.sophus.math.sample.RandomSampleInterface;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.ConstantArray;
import ch.alpine.tensor.ext.BoundedLinkedList;
import ch.alpine.tensor.img.ColorDataGradient;
import ch.alpine.tensor.img.ColorFormat;
import ch.alpine.tensor.lie.MatrixExp;
import ch.alpine.tensor.lie.TensorWedge;
import ch.alpine.tensor.sca.Mod;

public class SnRotationChunk implements RenderInterface {
  private final ColorDataGradient colorDataGradient;
  private final BoundedLinkedList<Tensor> boundedLinkedList;
  private final Tensor rotation;
  private Tensor samples;

  public SnRotationChunk(int dimension, int numel, int max_size, double speed, ColorDataGradient colorDataGradient) {
    this.colorDataGradient = colorDataGradient;
    boundedLinkedList = new BoundedLinkedList<>(max_size);
    RandomSampleInterface randomSampleInterface = SnRandomSample.of(dimension);
    samples = RandomSample.of(randomSampleInterface, numel);
    Tensor angle = RandomSample.of(randomSampleInterface).multiply(RealScalar.of(speed));
    rotation = MatrixExp.of(TensorWedge.of(angle, ConstantArray.of(RealScalar.ONE, dimension + 1)));
  }

  public void integrate() {
    samples = samples.dot(rotation);
    boundedLinkedList.add(samples);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    for (int count = 0; count < samples.length(); ++count) {
      Tensor rgba = colorDataGradient.apply(Mod.function(1).apply(RationalScalar.of(count, 20)));
      Color color = ColorFormat.toColor(rgba);
      int fi = count;
      Tensor trace = Tensor.of(boundedLinkedList.stream().map(p -> p.get(fi)));
      Path2D path2d = geometricLayer.toPath2D(trace);
      graphics.setColor(color);
      graphics.draw(path2d);
    }
  }
}
