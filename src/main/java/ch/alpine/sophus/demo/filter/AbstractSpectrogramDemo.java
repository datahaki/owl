// code by ob, jph
package ch.alpine.sophus.demo.filter;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.jfree.chart.JFreeChart;

import ch.alpine.java.fig.ListPlot;
import ch.alpine.java.fig.Spectrogram;
import ch.alpine.java.fig.VisualSet;
import ch.alpine.javax.swing.SpinnerLabel;
import ch.alpine.sophus.demo.io.GokartPoseData;
import ch.alpine.sophus.demo.io.GokartPoseDatas;
import ch.alpine.sophus.gds.ManifoldDisplay;
import ch.alpine.sophus.gds.ManifoldDisplays;
import ch.alpine.sophus.lie.LieDifferences;
import ch.alpine.sophus.lie.LieGroup;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.img.ColorDataGradient;
import ch.alpine.tensor.img.ColorDataGradients;
import ch.alpine.tensor.io.ImageFormat;
import ch.alpine.tensor.qty.QuantityMagnitude;
import ch.alpine.tensor.sca.win.WindowFunctions;

/* package */ abstract class AbstractSpectrogramDemo extends AbstractDatasetFilterDemo {
  private static final ScalarUnaryOperator MAGNITUDE_PER_SECONDS = QuantityMagnitude.SI().in("s^-1");
  // ---
  private final GokartPoseData gokartPoseData;
  protected final SpinnerLabel<String> spinnerLabelString = new SpinnerLabel<>();
  protected final SpinnerLabel<Integer> spinnerLabelLimit = new SpinnerLabel<>();
  protected final SpinnerLabel<WindowFunctions> spinnerKernel = new SpinnerLabel<>();
  // TODO JPH refactor
  protected Tensor _control = null;
  // protected final SpinnerLabel<ColorDataGradients> spinnerLabelCDG = new SpinnerLabel<>();

  protected AbstractSpectrogramDemo(GokartPoseData gokartPoseData) {
    this(ManifoldDisplays.CL_SE2_R2, gokartPoseData);
  }

  protected AbstractSpectrogramDemo(List<ManifoldDisplay> list, GokartPoseData gokartPoseData) {
    super(list);
    this.gokartPoseData = gokartPoseData;
    timerFrame.geometricComponent.setModel2Pixel(GokartPoseDatas.HANGAR_MODEL2PIXEL);
    {
      spinnerLabelString.setList(gokartPoseData.list());
      spinnerLabelString.addSpinnerListener(type -> updateState());
      spinnerLabelString.setIndex(0);
      spinnerLabelString.addToComponentReduced(timerFrame.jToolBar, new Dimension(200, 28), "data");
    }
    {
      spinnerLabelLimit.setList(Arrays.asList(10, 20, 50, 100, 250, 500, 1000, 2000, 5000));
      spinnerLabelLimit.setIndex(4);
      spinnerLabelLimit.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "limit");
      spinnerLabelLimit.addSpinnerListener(type -> updateState());
    }
    {
      spinnerKernel.setList(Arrays.asList(WindowFunctions.values()));
      spinnerKernel.setValue(WindowFunctions.GAUSSIAN);
      spinnerKernel.addToComponentReduced(timerFrame.jToolBar, new Dimension(180, 28), "smoothing kernel");
      spinnerKernel.addSpinnerListener(value -> updateState());
    }
  }

  protected void updateState() {
    int limit = spinnerLabelLimit.getValue();
    String name = spinnerLabelString.getValue();
    _control = gokartPoseData.getPose(name, limit);
  }

  @Override
  protected final Tensor control() {
    return Tensor.of(_control.stream().map(manifoldDisplay()::project)).unmodifiable();
  }

  /** @return */
  protected abstract String plotLabel();

  private static final ColorDataGradient COLOR_DATA_GRADIENT = //
      ColorDataGradients.VISIBLESPECTRUM.deriveWithOpacity(RealScalar.of(0.75));
  private static final int MAGNIFY = 4;

  @Override
  protected void differences_render( //
      Graphics2D graphics, ManifoldDisplay manifoldDisplay, Tensor refined, boolean spectrogram) {
    Dimension dimension = timerFrame.geometricComponent.jComponent.getSize();
    LieGroup lieGroup = manifoldDisplay.lieGroup();
    if (Objects.nonNull(lieGroup)) {
      LieDifferences lieDifferences = new LieDifferences(manifoldDisplay.lieExponential());
      Scalar sampleRate = MAGNITUDE_PER_SECONDS.apply(gokartPoseData.getSampleRate());
      Tensor speeds = lieDifferences.apply(refined).multiply(sampleRate);
      if (0 < speeds.length()) {
        int dimensions = speeds.get(0).length();
        VisualSet visualSet = new VisualSet();
        visualSet.setPlotLabel(plotLabel());
        visualSet.getAxisX().setLabel("sample no.");
        Tensor domain = Range.of(0, speeds.length());
        final int width = timerFrame.geometricComponent.jComponent.getWidth();
        int offset_y = 0;
        for (int index = 0; index < dimensions; ++index) {
          Tensor signal = speeds.get(Tensor.ALL, index).unmodifiable();
          visualSet.add(domain, signal);
          // ---
          if (spectrogram) {
            ScalarUnaryOperator window = spinnerKernel.getValue().get();
            Tensor image = Spectrogram.of(signal, window, COLOR_DATA_GRADIENT);
            BufferedImage bufferedImage = ImageFormat.of(image);
            int wid = bufferedImage.getWidth() * MAGNIFY;
            int hgt = bufferedImage.getHeight() * MAGNIFY;
            graphics.drawImage(bufferedImage, width - wid, offset_y, wid, hgt, null);
            offset_y += hgt + MAGNIFY;
          }
        }
        JFreeChart jFreeChart = ListPlot.of(visualSet, true);
        int dwidth = 80 + speeds.length();
        int height = 400;
        jFreeChart.draw(graphics, new Rectangle2D.Double( //
            dimension.getWidth() - dwidth, dimension.getHeight() - height, //
            80 + speeds.length(), height));
      }
    }
  }
}
