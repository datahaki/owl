// code by jph
package ch.alpine.owl.img;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.random.RandomGenerator;

import javax.imageio.ImageIO;

import ch.alpine.ascony.win.AbstractDemo;
import ch.alpine.bridge.gfx.GeometricLayer;
import ch.alpine.bridge.ref.ann.FieldClip;
import ch.alpine.bridge.ref.ann.FieldSelectionCallback;
import ch.alpine.bridge.ref.ann.FieldSlider;
import ch.alpine.bridge.ref.ann.ReflectionMarker;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;

public class JpgBlenderDemo extends AbstractDemo {
  private static final File ROOT = //
      new File("/home/datahaki/Downloads/jpgblender");
  // ---
  private BufferedImage bufferedImage;

  @ReflectionMarker
  public static class Param {
    @FieldSelectionCallback("files")
    public String s1 = "9fdc57df.jpg";
    @FieldSelectionCallback("files")
    public String s2 = "8c0c1941.jpg";
    @FieldSlider
    @FieldClip(min = "0", max = "100")
    public final Integer seed = 73;
    @FieldSlider
    @FieldClip(min = "0", max = "20")
    public Integer exp = 10;
    @FieldSlider
    @FieldClip(min = "0", max = "7")
    public Integer bit = 0;
    @FieldSlider
    @FieldClip(min = "0", max = "1")
    public Scalar ratio = RealScalar.of(0.5);

    @ReflectionMarker
    public List<String> files() {
      File[] files = ROOT.listFiles();
      return Arrays.stream(files).map(File::getName).toList();
    }
  }

  private final Param param;

  public JpgBlenderDemo() {
    this(new Param());
  }

  public JpgBlenderDemo(Param param) {
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
      byte[] d1 = Files.readAllBytes(new File(ROOT, param.s1).toPath());
      byte[] d2 = Files.readAllBytes(new File(ROOT, param.s2).toPath());
      System.out.println("===");
      System.out.println(d1.length);
      System.out.println(d2.length);
      RandomGenerator random = new Random(param.seed);
      int min = Math.min(d1.length, d2.length);
      double exp = Math.exp(-param.exp);
      double fac = exp / (double) min;
      byte mask = (byte) (1 << param.bit);
      int half = (int) (min * param.ratio.number().doubleValue());
      System.out.println(half);
      for (int count = 1000; count < min; ++count) {
        double p = count * fac;
        if (random.nextDouble() < p) {
          if (count < half)
            d1[count] ^= d2[count] & mask;
          else
            d1[count] = d2[count];
        }
      }
      bufferedImage = ImageIO.read(new ByteArrayInputStream(d1));
    } catch (Exception exception) {
      System.err.println("give up");
    }
  }

  static void main() {
    new JpgBlenderDemo().runStandalone();
  }
}
