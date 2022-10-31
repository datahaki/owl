// code by jph
package ch.alpine.ubongo;

import java.util.List;

import ch.alpine.bridge.fig.ListLinePlot;
import ch.alpine.bridge.fig.Show;
import ch.alpine.bridge.fig.ShowDialog;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

enum Candidates {
  ;
  public static void main(String[] args) {
    Show show = new Show();
    for (int use = 1; use <= 12; ++use) {
      Tensor xy = Tensors.empty();
      for (int count = 2; count <= 50; ++count) {
        List<List<Ubongo>> list = Ubongo.candidates(use, count);
        xy.append(Tensors.vectorInt(count, list.size()));
      }
      show.add(ListLinePlot.of(xy)).setLabel("" + use);
    }
    ShowDialog.of(show);
  }
}
