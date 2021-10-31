// code by jph
package ch.alpine.sophus.demo.clt;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Objects;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.gfx.GfxMatrix;
import ch.alpine.java.ref.ann.FieldClip;
import ch.alpine.java.ref.ann.FieldPreferredWidth;
import ch.alpine.java.ref.ann.FieldSlider;
import ch.alpine.java.ref.ann.ReflectionMarker;
import ch.alpine.java.ref.gui.ToolbarFieldsEditor;
import ch.alpine.java.ren.AxesRender;
import ch.alpine.java.ren.ImageRender;
import ch.alpine.sophus.clt.ClothoidBuilder;
import ch.alpine.sophus.clt.ClothoidSampler;
import ch.alpine.sophus.demo.ControlPointsDemo;
import ch.alpine.sophus.gds.ManifoldDisplays;
import ch.alpine.sophus.lie.se2c.Se2CoveringGroup;
import ch.alpine.sophus.lie.se2c.Se2CoveringGroupElement;
import ch.alpine.sophus.math.Geodesic;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Join;
import ch.alpine.tensor.alg.Reverse;
import ch.alpine.tensor.io.ImageFormat;
import ch.alpine.tensor.io.ResourceData;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.UniformDistribution;

@ReflectionMarker
public class ClothoidBrushDemo extends ControlPointsDemo {
  @FieldPreferredWidth(width = 200)
  public Tensor shiftL = Tensors.vector(-1, -1, 0);
  @FieldPreferredWidth(width = 200)
  public Tensor shiftR = Tensors.vector(0, 0, 0);
  @FieldSlider
  @FieldClip(min = "0.01", max = "1")
  public final Scalar beta = RealScalar.of(0.05);
  public Boolean shade = true;
  @FieldSlider
  @FieldClip(min = "0", max = "1.5708")
  public Scalar angle = RealScalar.of(0.8);
  @FieldSlider
  @FieldClip(min = "0", max = "0.7")
  public Scalar width = RealScalar.of(0.3);
  // private bufferedImage = null;

  public ClothoidBrushDemo() {
    super(true, ManifoldDisplays.CLC_ONLY);
    ToolbarFieldsEditor.add(this, timerFrame.jToolBar);
    // ---
    Tensor image = ResourceData.of("/letter/cal1/hi/a.png");
    if (Objects.nonNull(image)) {
      image = image.map(s -> Scalars.isZero(s) ? RealScalar.of(192) : s);
      BufferedImage bufferedImage = ImageFormat.of(image);
      ImageRender imageRender = ImageRender.range(bufferedImage, Tensors.vector(10, 10));
      timerFrame.geometricComponent.addRenderInterfaceBackground(imageRender);
    }
    setControlPointsSe2(RandomVariate.of(UniformDistribution.of(0, 8), 1 * 2, 3));
    timerFrame.geometricComponent.setOffset(100, 700);
    timerFrame.geometricComponent.addRenderInterfaceBackground(AxesRender.INSTANCE);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    Tensor sequence = getGeodesicControlPoints();
    Geodesic geodesic = manifoldDisplay().geodesic();
    ClothoidBuilder clothoidBuilder = (ClothoidBuilder) geodesic;
    for (int index = 0; index < sequence.length() - 1; index += 2) {
      Tensor beg0 = sequence.get(index + 0);
      Tensor end0 = sequence.get(index + 1);
      Se2CoveringGroupElement shL = Se2CoveringGroup.INSTANCE.element(shiftL);
      Tensor beg1 = shL.combine(beg0);
      Tensor end1 = shL.combine(end0);
      Tensor crv0 = ClothoidSampler.of(clothoidBuilder.curve(beg0, end0), beta);
      Tensor crv1 = ClothoidSampler.of(clothoidBuilder.curve(beg1, end1), beta);
      {
        graphics.setColor(new Color(0, 0, 0, 128));
        graphics.setStroke(new BasicStroke(1));
        Tensor polygon = Join.of(crv0, Reverse.of(crv1));
        graphics.fill(geometricLayer.toPath2D(polygon));
        geometricLayer.pushMatrix(GfxMatrix.translation(10, 0));
        graphics.setColor(new Color(64, 64, 64));
        graphics.fill(geometricLayer.toPath2D(polygon));
        geometricLayer.popMatrix();
      }
    }
    renderControlPoints(geometricLayer, graphics);
  }

  public static void main(String[] args) {
    new ClothoidBrushDemo().setVisible(1700, 800);
  }
}
