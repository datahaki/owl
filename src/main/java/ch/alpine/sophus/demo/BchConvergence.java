// code by jph
package ch.alpine.sophus.demo;

import java.awt.Color;
import java.io.IOException;

import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;

import ch.alpine.java.fig.ListPlot;
import ch.alpine.java.fig.VisualRow;
import ch.alpine.java.fig.VisualSet;
import ch.alpine.sophus.lie.ad.BakerCampbellHausdorff;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.ext.HomeDirectory;
import ch.alpine.tensor.lie.LeviCivitaTensor;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.sca.N;
import ch.alpine.tensor.sca.exp.Log10;

public enum BchConvergence {
  ;
  private static final Tensor SE2 = Tensors.fromString( //
      "{{{0, 0, 0}, {0, 0, -1}, {0, 1, 0}}, {{0, 0, 1}, {0, 0, 0}, {-1, 0, 0}}, {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}}}");
  private static final Tensor SO3 = LeviCivitaTensor.of(3).negate();
  private static final Tensor SL2 = Tensors.fromString( //
      "{{{0, 0, 0}, {0, 0, -2}, {0, 2, 0}}, {{0, 0, -2}, {0, 0, 0}, {2, 0, 0}}, {{0, -2, 0}, {2, 0, 0}, {0, 0, 0}}}").multiply(RationalScalar.HALF);

  public static void main(String[] args) throws IOException {
    VisualSet visualSet = new VisualSet();
    visualSet.setPlotLabel("bch convergence");
    visualSet.getAxisY().setLabel("log");
    for (int index = 0; index < 3; ++index) {
      Tensor ad = null;
      String pl = "";
      switch (index) {
      case 0:
        ad = SE2;
        pl = "se2";
        break;
      case 1:
        ad = SO3;
        pl = "so3";
        break;
      case 2:
        ad = SL2;
        pl = "sl2";
        break;
      default:
        break;
      }
      System.out.println("algebra=" + pl);
      ad = ad.map(N.DOUBLE);
      BakerCampbellHausdorff bakerCampbellHausdorff = (BakerCampbellHausdorff) BakerCampbellHausdorff.of(ad, 12);
      Tensor series = bakerCampbellHausdorff.series( //
          Tensors.vector(+0.3, +0.23, +0.37), //
          Tensors.vector(+0.2, -0.36, +0.18));
      Tensor tensor = Tensor.of(series.stream().map(Vector2Norm::of));
      VisualRow visualRow = visualSet.add(Range.of(0, tensor.length()), tensor.map(Log10.FUNCTION));
      visualRow.setLabel(pl);
    }
    JFreeChart jFreeChart = ListPlot.of(visualSet, true);
    jFreeChart.setBackgroundPaint(Color.WHITE);
    ChartUtils.saveChartAsPNG(HomeDirectory.Pictures(BchConvergence.class.getSimpleName() + ".png"), jFreeChart, 400, 300);
  }
}
