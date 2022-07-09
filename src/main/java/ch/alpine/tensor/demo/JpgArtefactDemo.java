// code by jph
package ch.alpine.tensor.demo;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.imageio.ImageIO;

import ch.alpine.ascona.util.win.AbstractDemo;
import ch.alpine.bridge.gfx.GeometricLayer;
import ch.alpine.bridge.ref.ann.FieldClip;
import ch.alpine.bridge.ref.ann.FieldInteger;
import ch.alpine.bridge.ref.ann.FieldSelectionCallback;
import ch.alpine.bridge.ref.ann.FieldSlider;
import ch.alpine.bridge.ref.ann.ReflectionMarker;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;

public class JpgArtefactDemo extends AbstractDemo {
  private static final File ROOT = //
      new File("/run/media/datahaki/data/public_html/photos/2016_germany/image");
  // ---
  private BufferedImage bufferedImage;

  @ReflectionMarker
  public static class Param {
    @FieldSelectionCallback("files")
    public String string = "";
    @FieldSlider
    @FieldClip(min = "0", max = "1")
    public Scalar ratio = RationalScalar.HALF;
    @FieldSlider
    @FieldInteger
    @FieldClip(min = "0", max = "100")
    public Scalar len = RealScalar.of(50);
    @FieldSlider
    @FieldInteger
    @FieldClip(min = "0", max = "100")
    public Scalar step = RealScalar.of(73);
    @FieldSlider
    @FieldInteger
    @FieldClip(min = "0", max = "100")
    public Scalar val = RealScalar.of(0);

    @ReflectionMarker
    public List<String> files() {
      File[] files = ROOT.listFiles();
      return Arrays.stream(files).map(File::getName).toList();
    }
  }

  private final Param param;

  public JpgArtefactDemo() {
    this(new Param());
  }

  public JpgArtefactDemo(Param param) {
    super(param);
    this.param = param;
    fieldsEditor(0).addUniversalListener(this::stateChanged);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (Objects.nonNull(bufferedImage))
      graphics.drawImage(bufferedImage, 0, 0, null);
  }

  public void stateChanged() {
    try {
      File file = new File(ROOT, param.string);
      byte[] data = Files.readAllBytes(file.toPath());
      int offset = (int) (data.length * (param.ratio.number().doubleValue()));
      int len = param.len.number().intValue();
      int step = param.step.number().intValue();
      byte val = param.val.number().byteValue();
      for (int count = 0; count < len; ++count) {
        int index = offset + step * count;
        if (index < data.length)
          data[index] = val;
      }
      bufferedImage = ImageIO.read(new ByteArrayInputStream(data));
    } catch (Exception exception) {
      // System.err.println("give up");
    }
  }

  public static void main(String[] args) {
    launch();
  }
}
