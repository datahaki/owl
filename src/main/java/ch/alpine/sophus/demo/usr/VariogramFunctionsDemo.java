// code by jph
package ch.alpine.sophus.demo.usr;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;

import ch.alpine.java.fig.ListPlot;
import ch.alpine.java.fig.VisualRow;
import ch.alpine.java.fig.VisualSet;
import ch.alpine.sophus.math.var.VariogramFunctions;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.ext.HomeDirectory;

public enum VariogramFunctionsDemo {
  ;
  public static void main(String[] args) throws IOException {
    File folder = HomeDirectory.Pictures("Variograms");
    folder.mkdir();
    Tensor domain = Subdivide.of(0.0, 2.0, 30);
    Scalar[] params = { RealScalar.ZERO, RealScalar.of(0.1), RationalScalar.HALF, RealScalar.ONE, RealScalar.TWO };
    for (VariogramFunctions variograms : VariogramFunctions.values()) {
      VisualSet visualSet = new VisualSet();
      visualSet.setPlotLabel(variograms.toString());
      for (Scalar param : params)
        try {
          Tensor values = domain.map(variograms.of(param));
          VisualRow visualRow = visualSet.add(domain, values);
          visualRow.setLabel("" + param);
        } catch (Exception exception) {
          System.out.println(variograms);
        }
      JFreeChart jFreeChart = ListPlot.of(visualSet, true);
      jFreeChart.setBackgroundPaint(Color.WHITE);
      File file = new File(folder, variograms + ".png");
      ChartUtils.saveChartAsPNG(file, jFreeChart, 500, 300);
    }
  }
}
