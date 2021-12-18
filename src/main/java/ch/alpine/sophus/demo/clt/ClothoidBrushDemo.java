// code by jph
package ch.alpine.sophus.demo.clt;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Objects;

import ch.alpine.java.awt.RenderQuality;
import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.gfx.GfxMatrix;
import ch.alpine.java.ref.ann.FieldClip;
import ch.alpine.java.ref.ann.FieldPreferredWidth;
import ch.alpine.java.ref.ann.FieldSlider;
import ch.alpine.java.ref.ann.ReflectionMarker;
import ch.alpine.java.ref.util.ToolbarFieldsEditor;
import ch.alpine.java.ren.AxesRender;
import ch.alpine.java.ren.ImageRender;
import ch.alpine.sophus.clt.ClothoidBuilder;
import ch.alpine.sophus.clt.ClothoidBuilders;
import ch.alpine.sophus.clt.ClothoidSampler;
import ch.alpine.sophus.demo.ControlPointsDemo;
import ch.alpine.sophus.gds.ManifoldDisplays;
import ch.alpine.sophus.lie.se2c.Se2CoveringGroup;
import ch.alpine.sophus.lie.se2c.Se2CoveringGroupElement;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Join;
import ch.alpine.tensor.alg.Reverse;
import ch.alpine.tensor.ext.Cache;
import ch.alpine.tensor.io.ImageFormat;
import ch.alpine.tensor.io.ResourceData;

@ReflectionMarker
public class ClothoidBrushDemo extends ControlPointsDemo {
  public final Cache<Tensor, Tensor> cache = Cache.of(ClothoidBrushDemo::sample, 100);
  @FieldPreferredWidth(200)
  public Tensor shiftL = Tensors.vector(-1.3, -1.3, 0);
  @FieldPreferredWidth(200)
  public Tensor shiftR = Tensors.vector(0, 0, 0);
  @FieldSlider
  @FieldClip(min = "0.00", max = "1")
  public Scalar round = RealScalar.of(0.1);
  public static final Scalar beta = RealScalar.of(0.05);
  public Boolean shade = true;
  @FieldSlider
  @FieldClip(min = "0", max = "1.5708")
  public Scalar angle = RealScalar.of(0.8);
  @FieldSlider
  @FieldClip(min = "0", max = "0.7")
  public Scalar width = RealScalar.of(0.3);
  // private bufferedImage = null;
  private Font font = null;

  public ClothoidBrushDemo() {
    super(true, ManifoldDisplays.CLC_ONLY);
    try {
      // Font.TYPE1_FONT
      // Font[] fonts = Font.createFonts(new File("/usr/share/fonts/urw-base35/Z003-MediumItalic.t1"));
      Font[] fonts = Font.createFonts(new File("/home/datahaki/.local/share/fonts/DS Elzevier Initialen.ttf"));
      System.out.println(fonts.length);
      if (0 < fonts.length)
        font = fonts[0].deriveFont(500f);
    } catch (Exception e) {
      e.printStackTrace();
    }
    ToolbarFieldsEditor.add(this, timerFrame.jToolBar);
    // ---
    Tensor image = ResourceData.of("/letter/cal2/hi/a.png");
    if (Objects.nonNull(image)) {
      image = image.map(s -> Scalars.isZero(s) ? RealScalar.of(192) : s);
      BufferedImage bufferedImage = ImageFormat.of(image);
      ImageRender imageRender = ImageRender.range(bufferedImage, Tensors.vector(10, 10));
      // timerFrame.geometricComponent.addRenderInterfaceBackground(imageRender);
    }
    timerFrame.geometricComponent.setOffset(100, 700);
    timerFrame.geometricComponent.addRenderInterfaceBackground(AxesRender.INSTANCE);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    if (Objects.nonNull(font)) {
      graphics.setColor(new Color(164, 164, 64));
      graphics.setFont(font);
      graphics.drawString("ABCDEF", 0, 500);
    }
    Tensor sequence = getGeodesicControlPoints();
    for (int index = 1; index < sequence.length(); ++index) {
      Tensor beg0 = sequence.get(index - 1);
      Tensor end0 = sequence.get(index + 0);
      Se2CoveringGroupElement shL = Se2CoveringGroup.INSTANCE.element(shiftL);
      Tensor beg1 = Se2CoveringGroup.INSTANCE.element(shL.combine(beg0)).combine(shiftR);
      Tensor end1 = Se2CoveringGroup.INSTANCE.element(shL.combine(end0)).combine(shiftR);
      Tensor crv0 = cache.apply(Tensors.of(beg0, end0));
      Tensor crv1 = cache.apply(Tensors.of(beg1, end1));
      {
        graphics.setColor(new Color(0, 0, 0, 128));
        float model2pixelWidth = geometricLayer.model2pixelWidth(round);
        graphics.setStroke(new BasicStroke(model2pixelWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        Tensor polygon = Join.of(crv0, Reverse.of(crv1));
        {
          Path2D path2d = geometricLayer.toPath2D(polygon, true);
          graphics.draw(path2d);
          graphics.fill(path2d);
        }
        geometricLayer.pushMatrix(GfxMatrix.translation(11, 0));
        graphics.setColor(new Color(64, 64, 64));
        {
          Path2D path2d = geometricLayer.toPath2D(polygon, true);
          graphics.draw(path2d);
          graphics.fill(path2d);
        }
        geometricLayer.popMatrix();
        graphics.setStroke(new BasicStroke(1));
      }
    }
    renderControlPoints(geometricLayer, graphics);
  }

  private static Tensor sample(Tensor be) {
    Tensor beg0 = be.get(0);
    Tensor end0 = be.get(1);
    ClothoidBuilder clothoidBuilder = ClothoidBuilders.SE2_COVERING.clothoidBuilder();
    return ClothoidSampler.of(clothoidBuilder.curve(beg0, end0), beta);
  }

  public static void main(String[] args) {
    new ClothoidBrushDemo().setVisible(1700, 800);
  }
}
