// code by jph
package ch.alpine.sophus.app.hermite;

import org.jfree.chart.JFreeChart;

import ch.alpine.java.fig.ListPlot;
import ch.alpine.java.fig.VisualSet;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Range;

/* package */ enum StaticHelper {
  ;
  public static JFreeChart listPlot(Tensor deltas) {
    return listPlot(deltas, Range.of(0, deltas.length()));
  }

  public static JFreeChart listPlot(Tensor deltas, Tensor domain) {
    VisualSet visualSet = new VisualSet();
    int dims = deltas.get(0).length();
    for (int index = 0; index < dims; ++index)
      visualSet.add(domain, deltas.get(Tensor.ALL, index));
    return ListPlot.of(visualSet);
  }
}
