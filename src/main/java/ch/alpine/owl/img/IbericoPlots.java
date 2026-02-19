// code by jph
package ch.alpine.owl.img;

import java.awt.Color;
import java.util.List;

import ch.alpine.bridge.fig.ListLinePlot;
import ch.alpine.bridge.fig.ListPlot;
import ch.alpine.bridge.fig.Show;
import ch.alpine.bridge.fig.Showable;
import ch.alpine.bridge.pro.ShowWindow;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.alg.Sort;
import ch.alpine.tensor.pdf.BinCounts;
import ch.alpine.tensor.qty.Quantity;

class IbericoPlots {
  private Scalar date = null;
  private Show show0 = new Show();
  private Show show1 = new Show();
  private Show show11 = new Show();
  private Show show2 = new Show();
  private Show show21 = new Show();
  private Show show3 = new Show();
  private Show show31 = new Show();
  private Show show4 = new Show();
  private Show show5 = new Show();

  public IbericoPlots() {
    show1.setPlotLabel("Length");
    show2.setPlotLabel("Ascent");
    show3.setPlotLabel("Rate of Ascent");
  }

  void add(Tensor routes, String label) {
    System.out.println(Dimensions.of(routes));
    if (date == null)
      date = routes.Get(0, 0);
    {
      Showable showable = ListPlot.of(routes.get(Tensor.ALL, 0), routes.get(Tensor.ALL, 2));
      showable.setLabel(label);
      show0.add(showable);
    }
    {
      Tensor data = Sort.of(routes.get(Tensor.ALL, 1));
      Showable showable = ListPlot.of(Range.of(0, data.length()), data);
      showable.setLabel(label);
      show1.add(showable);
    }
    {
      Scalar dx = Quantity.of(5, "km");
      Tensor bins = BinCounts.of(routes.get(Tensor.ALL, 1), dx);
      Showable showable = ListLinePlot.of(Range.of(0, bins.length()).multiply(dx), bins);
      showable.setLabel(label);
      show11.add(showable);
    }
    {
      Tensor data = Sort.of(routes.get(Tensor.ALL, 2));
      Showable showable = ListPlot.of(Range.of(0, data.length()), data);
      showable.setLabel(label);
      show2.add(showable);
    }
    {
      Scalar dx = Quantity.of(200, "m");
      Tensor data = BinCounts.of(routes.get(Tensor.ALL, 2), dx);
      Showable showable = ListLinePlot.of(Range.of(0, data.length()).multiply(dx), data);
      showable.setLabel(label);
      show21.add(showable);
    }
    {
      Tensor data = Sort.of(routes.get(Tensor.ALL, 3));
      Showable showable = ListPlot.of(Range.of(0, data.length()), data);
      showable.setLabel(label);
      show3.add(showable);
    }
    {
      Scalar dx = Quantity.of(5, "m*km^-1");
      Tensor data = BinCounts.of(routes.get(Tensor.ALL, 3), dx);
      Showable showable = ListLinePlot.of(Range.of(0, data.length()).multiply(dx), data);
      showable.setLabel(label);
      show31.add(showable);
    }
    {
      Tensor datax = routes.get(Tensor.ALL, 1);
      Tensor datay = routes.get(Tensor.ALL, 2);
      Showable showable = ListPlot.of(datax, datay);
      showable.setLabel(label);
      show4.add(showable);
    }
    {
      Tensor datax = routes.get(Tensor.ALL, 1);
      Tensor datay = routes.get(Tensor.ALL, 3);
      Showable showable = ListPlot.of(datax, datay);
      showable.setLabel(label);
      show5.add(showable);
    }
  }

  void show() {
    show0.add(ListPlot.of(Tensors.fromString("{{" + date + ",0[m]}}"))).setColor(new Color(0, 0, 0, 0));
    show1.add(ListPlot.of(Tensors.fromString("{{0,0[km]}}"))).setColor(new Color(0, 0, 0, 0));
    show2.add(ListPlot.of(Tensors.fromString("{{0,0[m]}}"))).setColor(new Color(0, 0, 0, 0));
    show3.add(ListPlot.of(Tensors.fromString("{{0,0[m*km^-1]}}"))).setColor(new Color(0, 0, 0, 0));
    show4.add(ListPlot.of(Tensors.fromString("{{0[km],0[m]}}"))).setColor(new Color(0, 0, 0, 0));
    show5.add(ListPlot.of(Tensors.fromString("{{0[km],0[m*km^-1]}}"))).setColor(new Color(0, 0, 0, 0));
    ShowWindow.asDialog(List.of(show0, show1, show11, show2, show21, show3, show31, show4, show5));
  }
}
