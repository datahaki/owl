// code by jph
package ch.alpine.tensor.demo;

import java.awt.Color;
import java.io.IOException;

import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;

import ch.alpine.java.fig.ListPlot;
import ch.alpine.java.fig.VisualSet;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.ext.HomeDirectory;
import ch.alpine.tensor.itp.Fit;
import ch.alpine.tensor.qty.Quantity;

/* package */ enum FitDemo {
  ;
  public static void main(String[] args) throws IOException {
    for (int degree = 0; degree <= 4; ++degree) {
      Tensor x = Tensors.fromString("{100[K], 110.0[K], 120[K], 133[K], 140[K], 150[K]}");
      Tensor y = Tensors.fromString("{10[bar], 20[bar], 22[bar], 23[bar], 25[bar], 26.0[bar]}");
      ScalarUnaryOperator x_to_y = Fit.polynomial(x, y, degree);
      ScalarUnaryOperator y_to_x = Fit.polynomial(y, x, degree);
      Tensor samples_x = Subdivide.of(Quantity.of(100, "K"), Quantity.of(150, "K"), 50);
      Tensor samples_y = Subdivide.of(Quantity.of(10, "bar"), Quantity.of(26, "bar"), 50);
      samples_x.map(x_to_y);
      samples_y.map(y_to_x);
      VisualSet visualSet = new VisualSet();
      visualSet.add(samples_x, samples_x.map(x_to_y));
      visualSet.add(samples_y.map(y_to_x), samples_y);
      JFreeChart jFreeChart = ListPlot.of(visualSet, true);
      jFreeChart.setBackgroundPaint(Color.WHITE);
      ChartUtils.saveChartAsPNG(HomeDirectory.Pictures("here" + degree + ".png"), jFreeChart, 400, 300);
    }
  }
}
