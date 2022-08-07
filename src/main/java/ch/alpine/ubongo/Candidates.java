// code by jph
package ch.alpine.ubongo;

import java.awt.Color;
import java.io.IOException;
import java.util.List;

import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;

import ch.alpine.bridge.fig.ListPlot;
import ch.alpine.bridge.fig.VisualRow;
import ch.alpine.bridge.fig.VisualSet;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.HomeDirectory;

enum Candidates {
  ;
  public static void main(String[] args) throws IOException {
    VisualSet visualSet = new VisualSet();
    for (int use = 1; use <= 12; ++use) {
      Tensor xy = Tensors.empty();
      for (int count = 2; count <= 50; ++count) {
        List<List<Ubongo>> list = Ubongo.candidates(use, count);
        xy.append(Tensors.vectorInt(count, list.size()));
      }
      VisualRow visualRow = visualSet.add(xy);
      visualRow.setLabel("" + use);
    }
    JFreeChart jFreeChart = ListPlot.of(visualSet, true);
    jFreeChart.setBackgroundPaint(Color.WHITE);
    ChartUtils.saveChartAsPNG(HomeDirectory.Pictures("ubongo_candidate_size.png"), jFreeChart, 800, 600);
  }
}
