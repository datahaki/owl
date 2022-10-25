// code by jph
package ch.alpine.ubongo;

import java.awt.Dimension;
import java.io.IOException;
import java.util.List;

import ch.alpine.bridge.fig.ListPlot;
import ch.alpine.bridge.fig.Show;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.HomeDirectory;

enum Candidates {
  ;
  public static void main(String[] args) throws IOException {
    Show show = new Show();
    for (int use = 1; use <= 12; ++use) {
      Tensor xy = Tensors.empty();
      for (int count = 2; count <= 50; ++count) {
        List<List<Ubongo>> list = Ubongo.candidates(use, count);
        xy.append(Tensors.vectorInt(count, list.size()));
      }
      show.add(ListPlot.of(xy)).setLabel("" + use);
    }
    show.export(HomeDirectory.Pictures("ubongo_candidate_size.png"), new Dimension(800, 600));
  }
}
