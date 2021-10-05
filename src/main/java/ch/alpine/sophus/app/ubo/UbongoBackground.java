// code by jph
package ch.alpine.sophus.app.ubo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics2D;
import java.util.List;
import java.util.Random;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.ref.ann.FieldInteger;
import ch.alpine.java.ref.gui.PanelFieldsEditor;
import ch.alpine.java.win.AbstractDemo;
import ch.alpine.owl.gui.ren.AxesRender;
import ch.alpine.sophus.gui.ren.MeshRender;
import ch.alpine.sophus.math.MatrixArray;
import ch.alpine.sophus.math.noise.SimplexContinuousNoise;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.img.ColorDataGradients;
import ch.alpine.tensor.pdf.NormalDistribution;
import ch.alpine.tensor.pdf.RandomVariate;

public class UbongoBackground extends AbstractDemo {
  @FieldInteger
  public Scalar resol = RealScalar.of(30);
  public Scalar delta = RealScalar.of(0.1);
  public Scalar amp = RealScalar.of(0.1);
  public Scalar ofs = RealScalar.of(0);

  public UbongoBackground() {
    Container container = timerFrame.jFrame.getContentPane();
    container.add("West", new PanelFieldsEditor(this).getJScrollPane());
  }

  public Tensor noise(List<Integer> list) {
    Tensor param = Tensors.vector(list.get(0), list.get(1)).multiply(delta);
    double nx = SimplexContinuousNoise.FUNCTION.at( //
        param.Get(0).number().doubleValue(), //
        param.Get(1).number().doubleValue(), ofs.number().doubleValue() + 0);
    double ny = SimplexContinuousNoise.FUNCTION.at( //
        param.Get(0).number().doubleValue(), //
        param.Get(1).number().doubleValue(), ofs.number().doubleValue() + 1);
    return Tensors.vector(nx, ny).multiply(amp);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    int res = resol.number().intValue();
    Tensor dx = Subdivide.of(0.0, 2.0, 2 * res - 1);
    Tensor dy = Subdivide.of(0.0, 1.0, res - 1);
    Tensor domain = Tensors.matrix((cx, cy) -> Tensors.of(dx.get(cx), dy.get(cy)), dx.length(), dy.length());
    Tensor random = RandomVariate.of(NormalDistribution.of(0, 0.01), new Random(3), Dimensions.of(domain));
    random = Array.of(this::noise, dx.length(), dy.length());
    // System.out.println("d=" + Dimensions.of(domain));
    // System.out.println(Dimensions.of(random));
    Tensor target = domain.add(random);
    Tensor[][] forward = MatrixArray.of(target);
    RenderQuality.setQuality(graphics);
    graphics.setStroke(new BasicStroke(0.6f));
    graphics.setColor(new Color(128, 128, 128));
    new MeshRender(forward, ColorDataGradients.CLASSIC) //
        .render(geometricLayer, graphics);
    AxesRender.INSTANCE.render(geometricLayer, graphics);
    // RenderQuality.setDefault(graphics);
  }

  public static void main(String[] args) {
    UbongoBackground ubongoBackground = new UbongoBackground();
    ubongoBackground.setVisible(1200, 600);
  }
}
