// code by jph
package ch.alpine.tensor.demo;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.util.Objects;
import java.util.stream.Stream;

import javax.imageio.ImageIO;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.ref.ann.FieldPreferredWidth;
import ch.alpine.java.ref.util.ToolbarFieldsEditor;
import ch.alpine.java.win.AbstractDemo;
import ch.alpine.java.win.LookAndFeels;
import ch.alpine.javax.swing.SpinnerLabel;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;

public class JpgArtefact extends AbstractDemo implements ChangeListener {
  private static final File ROOT = //
      new File("/run/media/datahaki/data/public_html/photos/2016_germany/image");
  // ---
  private final SpinnerLabel<String> spinnerLabel = new SpinnerLabel<>();
  private final JSlider jSlider = new JSlider(0, 1231, 0);
  private BufferedImage bufferedImage;

  public static class Param {
    @FieldPreferredWidth(width = 60)
    public Scalar len = RealScalar.of(50);
    @FieldPreferredWidth(width = 60)
    public Scalar step = RealScalar.of(73);
    @FieldPreferredWidth(width = 60)
    public Scalar val = RealScalar.of(0);
  }

  private final Param param = new Param();

  public JpgArtefact() {
    ToolbarFieldsEditor.add(param, timerFrame.jToolBar).addUniversalListener(() -> stateChanged(null));
    // ---
    File[] files = ROOT.listFiles();
    spinnerLabel.setArray(Stream.of(files).map(File::getName).toArray(String[]::new));
    spinnerLabel.setIndex(0);
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
    LookAndFeels.DARK.updateUI();
    new JpgArtefact().setVisible(1500, 950);
  }
}
