package ch.alpine.owl.img;

import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;

import ch.alpine.bridge.fig.ListLinePlot;
import ch.alpine.bridge.fig.ListPlot;
import ch.alpine.bridge.fig.Show;
import ch.alpine.bridge.fig.ShowDialog;
import ch.alpine.bridge.fig.Showable;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Ordering;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.img.ColorDataLists;
import ch.alpine.tensor.pdf.BinCounts;
import ch.alpine.tensor.qty.Quantity;

class CycleStats {
  static final String[] labels = { "Spain 2024", "France", "Spain 2025" };
  IbericoImports ibericoImports = new IbericoImports(true);

  public Show listPlot(int col) {
    Tensor tensor = Tensor.of(ibericoImports.winter.stream() //
        .flatMap(s -> s.routes.get(Tensor.ALL, col).stream()));
    int[] order = Ordering.INCREASING.of(tensor);
    NavigableMap<Integer, Tensor> map = new TreeMap<>();
    map.put(0, Tensors.empty());
    int c0 = ibericoImports.winter.get(0).routes.length();
    map.put(c0, Tensors.empty());
    int c1 = ibericoImports.winter.get(1).routes.length();
    map.put(c0 + c1, Tensors.empty());
    int count = 0;
    for (int index : order) {
      map.floorEntry(index).getValue().append(Tensors.of(RealScalar.of(count++), tensor.Get(index)));
    }
    Show show = new Show(ColorDataLists._250.cyclic());
    int lab = 0;
    for (Entry<Integer, Tensor> entry : map.entrySet()) {
      Tensor value = entry.getValue();
      ListPlot showable = (ListPlot) ListPlot.of(value);
      showable.filling = true;
      // DiscretePlot.of(i->value.Get(i.number().intValue()), Clips.positive(value.length()-1));
      showable.setLabel(labels[lab++]);
      show.add(showable);
    }
    return show;
  }

  public Show asd2(int col, Scalar dx) {
    Tensor tensor = Tensor.of(ibericoImports.winter.stream() //
        .flatMap(s -> s.routes.get(Tensor.ALL, col).stream()));
    Show show = new Show();
    Tensor bins = BinCounts.of(tensor, dx);
    Showable showable = ListLinePlot.of(Range.of(0, bins.length()).multiply(dx), bins);
    show.add(showable);
    return show;
  }

  static void main() {
    CycleStats cycleStats = new CycleStats();
    ShowDialog.of(cycleStats.listPlot(1), cycleStats.listPlot(2), cycleStats.listPlot(3), //
        cycleStats.asd2(1, Quantity.of(8, "km")), //
        cycleStats.asd2(2, Quantity.of(250, "m")), //
        cycleStats.asd2(3, Quantity.of(10, "m*km^-1")) //
    );
  }
}
