// code by jph
package ch.alpine.sophus.demo.decim;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.stream.IntStream;

import org.jfree.chart.JFreeChart;

import ch.alpine.java.fig.ListPlot;
import ch.alpine.java.fig.Spectrogram;
import ch.alpine.java.fig.VisualSet;
import ch.alpine.sophus.hs.HsDifferences;
import ch.alpine.sophus.hs.sn.SnManifold;
import ch.alpine.sophus.hs.sn.TSnMemberQ;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.img.ColorDataGradients;
import ch.alpine.tensor.io.ImageFormat;

public class SnDeltaContainer {
  final Tensor sequence;
  final Tensor differences;
  private final List<Tensor> endos;
  private final Tensor t0_deltas;
  final JFreeChart jFreeChart;
  private final Tensor[] spectrogram = new Tensor[2];
  final BufferedImage[] bufferedImage = new BufferedImage[2];

  public SnDeltaContainer(Tensor sequence, ScalarUnaryOperator window) {
    this.sequence = sequence;
    endos = SnTransportChain.endos(sequence);
    differences = new HsDifferences(SnManifold.INSTANCE).apply(sequence);
    TSnMemberQ tSnMemberQ = new TSnMemberQ(sequence.get(0));
    t0_deltas = Tensor.of(IntStream.range(0, differences.length()).mapToObj( //
        index -> tSnMemberQ.require(endos.get(index).dot(differences.get(index, 1)))));
    // ---
    VisualSet visualSet = new VisualSet();
    Tensor domain = Range.of(0, t0_deltas.length());
    for (int d = 1; d < 3; ++d) {
      Tensor values = t0_deltas.get(Tensor.ALL, d);
      spectrogram[d - 1] = Spectrogram.of(values, window, ColorDataGradients.VISIBLESPECTRUM);
      bufferedImage[d - 1] = ImageFormat.of(spectrogram[d - 1]);
      visualSet.add(domain, values);
    }
    jFreeChart = ListPlot.of(visualSet);
  }
}
