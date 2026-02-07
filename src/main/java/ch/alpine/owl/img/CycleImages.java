package ch.alpine.owl.img;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;

import ch.alpine.bridge.fig.ImagePlot;
import ch.alpine.bridge.fig.ListPlot;
import ch.alpine.bridge.fig.Show;
import ch.alpine.bridge.fig.Showable;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Ordering;
import ch.alpine.tensor.ext.HomeDirectory;
import ch.alpine.tensor.img.ColorDataIndexed;
import ch.alpine.tensor.img.ColorDataLists;
import ch.alpine.tensor.opt.nd.CoordinateBoundingBox;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Clips;

class CycleImages {
  static final String[] labels = { "Spain 2024", "France 2024", "Spain 2025" };
  static final int height = 200;
  static final IbericoImports ibericoImports = new IbericoImports(true);

  public static Show image(int col, Scalar factor) {
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
    ColorDataIndexed colorDataIndexed = ColorDataLists._250.cyclic();
    System.out.println("count=" + count);
    BufferedImage bufferedImage = new BufferedImage(count, height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D graphics = bufferedImage.createGraphics();
    int index = 0;
    Show show = new Show(colorDataIndexed);
    for (Entry<Integer, Tensor> entry : map.entrySet()) {
      Showable showable = ListPlot.of(Tensors.empty());
      // DiscretePlot.of(i->value.Get(i.number().intValue()), Clips.positive(value.length()-1));
      showable.setLabel(labels[index]);
      show.add(showable);
      Tensor value = entry.getValue();
      graphics.setColor(colorDataIndexed.getColor(index));
      for (Tensor xy : value) {
        int x1 = xy.Get(0).number().intValue();
        int y1 = (int) ((1 - xy.Get(1).divide(factor).number().doubleValue()) * height);
        graphics.drawLine(x1, y1, x1, height);
      }
      index++;
    }
    CoordinateBoundingBox cbb = CoordinateBoundingBox.of( //
        Clips.positive(RealScalar.of(count)), //
        Clips.positive(factor));
    show.add(ImagePlot.of(bufferedImage, cbb));
    return show;
  }

  static void main() throws IOException {
    int routeCount = ibericoImports.winter.stream() //
        .mapToInt(s -> s.routes.length()).sum();
    System.out.println("count=" + routeCount);
    Dimension dimension = new Dimension(routeCount * 2 + 80, height + 38);
    {
      Show show = CycleImages.image(1, Quantity.of(112, "km"));
      show.setPlotLabel("Days sorted by distance");
      show.export(HomeDirectory.Pictures.resolve("cycling_distance.png"), dimension);
    }
    {
      Show show = CycleImages.image(2, Quantity.of(4020, "m"));
      show.setPlotLabel("Days sorted by ascent");
      show.export(HomeDirectory.Pictures.resolve("cycling_ascent.png"), dimension);
    }
    {
      Show show = CycleImages.image(3, Quantity.of(100, "m*km^-1"));
      show.setPlotLabel("Days sorted by ascent rate");
      show.export(HomeDirectory.Pictures.resolve("cycling_rate.png"), dimension);
    }
  }
}
