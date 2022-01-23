// code by jph
package ch.alpine.sophus.demo.misc;

import java.io.IOException;

import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;

import ch.alpine.java.fig.Spectrogram;
import ch.alpine.java.fig.VisualSet;
import ch.alpine.sophus.math.noise.ColoredNoise;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.ext.HomeDirectory;
import ch.alpine.tensor.pdf.RandomVariate;

/* package */ enum ColoredNoiseSpectrogram {
  ;
  public static void main(String[] args) throws IOException {
    ColoredNoise coloredNoise = new ColoredNoise(1); // 1 == pink noise
    Tensor tensor = RandomVariate.of(coloredNoise, 1024 * 4);
    VisualSet visualSet = new VisualSet();
    visualSet.add(Range.of(0, tensor.length()), tensor);
    JFreeChart jFreeChart = Spectrogram.of(visualSet);
    ChartUtils.saveChartAsPNG( //
        HomeDirectory.Pictures(ColoredNoiseSpectrogram.class.getSimpleName() + ".png"), jFreeChart, 480, 360);
  }
}
