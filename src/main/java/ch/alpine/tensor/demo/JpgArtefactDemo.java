// code by jph
package ch.alpine.tensor.demo;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Objects;

import javax.imageio.ImageIO;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ch.alpine.ascona.util.win.AbstractDemo;
import ch.alpine.bridge.gfx.GeometricLayer;
import ch.alpine.bridge.ref.ann.FieldPreferredWidth;
import ch.alpine.bridge.ref.util.ToolbarFieldsEditor;
import ch.alpine.bridge.swing.LookAndFeels;
import ch.alpine.bridge.swing.SpinnerLabel;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;

public class JpgArtefactDemo extends AbstractDemo implements ChangeListener {
  private static final File ROOT = //
      new File("/run / 134media/datahaki/data/public_html/photos/2016_germany/image");
  // ---
  private final SpinnerLabel<String> spinnerLabel;
  private final JSlider jSlider = new JSlider(0, 1231, 0);
  private BufferedImage bufferedImage;

  public static class Param {
    @FieldPreferredWidth(60)
    public Scalar len = RealScalar.of(50);
    @FieldPreferredWidth(60)
    public Scalar step = RealScalar.of(73);
    @FieldPreferredWidth(60)
    public Scalar val = RealScalar.of(0);
  }

  private final Param param = new Param();

  public JpgArtefactDemo() {
    ToolbarFieldsEditor.add(param, timerFrame.jToolBar).addUniversalListener(() -> stateChanged(null));
    // ---
    File[] files = ROOT.listFiles();
    spinnerLabel = SpinnerLabel.of(Arrays.stream(files).map(File::getName).toArray(String[]::new));
    spinnerLabel.addToComponentReduced(timerFrame.jToolBar, new Dimension(120, 28), "file");
    spinnerLabel.addSpinnerListener(s -> stateChanged(null));
    // ---
    jSlider.setValue(jSlider.getMaximum() / 2);
    jSlider.setPreferredSize(new Dimension(1000, 28));
    timerFrame.jToolBar.add(jSlider);
    jSlider.addChangeListener(this);
    stateChanged(null);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (Objects.nonNull(bufferedImage))
      graphics.drawImage(bufferedImage, 0, 0, null);
  }

  @Override
  public void stateChanged(ChangeEvent e) {
    try {
      File file = new File(ROOT, spinnerLabel.getValue());
      byte[] data = Files.readAllBytes(file.toPath());
      int offset = (int) (data.length * (jSlider.getValue() / (double) jSlider.getMaximum()));
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
    LookAndFeels.DARK.updateComponentTreeUI();
    new JpgArtefactDemo().setVisible(1500, 950);
  }
}
