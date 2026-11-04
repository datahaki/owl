// code by jph
package ch.alpine.owl.net;

import java.awt.image.BufferedImage;
import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

import ch.alpine.bridge.awt.ScalableImage;
import ch.alpine.bridge.fig.Show;
import ch.alpine.bridge.fig.plt.DensityPlot;
import ch.alpine.bridge.fig.plt.ListLinePlot;
import ch.alpine.bridge.io.ani.ImageIconRecorder;
import ch.alpine.subare.net.NetChains;
import ch.alpine.subare.net.NetLayer;
import ch.alpine.subare.net.NetTrain;
import ch.alpine.subare.net.NetTrainListener;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.col.ColorDataGradients;
import ch.alpine.tensor.img.ImageResize;
import ch.alpine.tensor.io.TableBuilder;
import ch.alpine.tensor.opt.nd.CoordinateBoundingBox;
import ch.alpine.tensor.opt.nd.CoordinateBounds;
import ch.alpine.tensor.qty.Quantity;

public class NetClassify {
  final NetLayer netLayer;
  final NetTrain netTrain;
  CoordinateBoundingBox cbb;
  ColorDataGradients cdg = ColorDataGradients.COPPER;
  final ImageIconRecorder imageIconRecorder = ImageIconRecorder.loop(Duration.ofMillis(200));

  public NetClassify(Tensor xdata, Tensor vector, int hidden, int labels) {
    int d_in = Unprotect.dimension1(xdata);
    netLayer = NetChains.argMaxMLP(d_in, hidden, labels);
    netLayer.initialize(ThreadLocalRandom.current());
    netTrain = new NetTrain(netLayer, xdata, vector);
    cbb = CoordinateBounds.of(xdata);
    netTrain.addListener(new NetTrainListener() {
      @Override
      public void epoch(int epoch) {
        DensityPlot densityPlot = DensityPlot.of((x, y) -> (Scalar) netLayer.forward(Tensors.of(x, y)), cbb, cdg);
        ScalableImage scalableImage = densityPlot.getScalableImage(100);
        BufferedImage bufferedImage = scalableImage.getScaledInstance(ImageResize.DEGREE_3, RealScalar.ONE);
        imageIconRecorder.write(bufferedImage);
      }
    });
  }

  public void run() {
    netTrain.run(RealScalar.of(0.05), Quantity.of(1.3, "s"), 3000, 10);
  }

  public Show showTParam() {
    Show show = new Show();
    show.setShowLabel("net param");
    TableBuilder table = netTrain.tparam;
    int n = table.getRow(0).length();
    for (int i = 1; i < n; ++i)
      show.add(ListLinePlot.of(table.getColumns(0, i)));
    return show;
  }

  public Show showLoss() {
    Show show = new Show();
    show.setShowLabel("loss");
    TableBuilder table = netTrain.tloss;
    int n = table.getRow(0).length();
    for (int i = 1; i < n; ++i)
      show.add(ListLinePlot.of(table.getColumns(0, i)));
    return show;
  }
}
