// code by jph
package ch.alpine.owl.net.sig;

import java.awt.image.BufferedImage;
import java.time.Duration;

import ch.alpine.bridge.awt.ScalableImage;
import ch.alpine.bridge.fig.Show;
import ch.alpine.bridge.fig.plt.DensityPlot;
import ch.alpine.bridge.fig.plt.ListLinePlot;
import ch.alpine.bridge.io.ani.ImageIconRecorder;
import ch.alpine.subare.net.NetParamTracker;
import ch.alpine.subare.net.TrainingData;
import ch.alpine.subare.net.lay.ElementwiseLayer;
import ch.alpine.subare.net.lay.NetChains;
import ch.alpine.subare.net.lay.NetLayer;
import ch.alpine.subare.net.opt.NetMomentumGradientDescent;
import ch.alpine.subare.net.opt.NetOptimizer;
import ch.alpine.subare.net.opt.NetSimpleGradientDescent;
import ch.alpine.subare.net.run.NetInitialize;
import ch.alpine.subare.net.run.NetTrain;
import ch.alpine.subare.net.run.NetTrainListener;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.col.ColorDataGradients;
import ch.alpine.tensor.img.ImageResize;
import ch.alpine.tensor.io.TableBuilder;
import ch.alpine.tensor.opt.nd.CoordinateBoundingBox;
import ch.alpine.tensor.opt.nd.CoordinateBounds;
import ch.alpine.tensor.qty.Quantity;

class NetSigmoid {
  final NetLayer netLayer;
  final NetTrain netTrain;
  final NetParamTracker netParamTracker = new NetParamTracker();
  CoordinateBoundingBox cbb;
  ColorDataGradients cdg = ColorDataGradients.COPPER;
  final ImageIconRecorder imageIconRecorder = ImageIconRecorder.loop(Duration.ofMillis(200));

  public NetSigmoid(TrainingData trainingData, int hidden, Scalar beta, Scalar learningRate, Integer batch_size) {
    int d_in = Unprotect.dimension1(trainingData.xdata());
    netLayer = NetChains.binary(NetChains.linear(() -> ElementwiseLayer.gelu(), d_in, hidden, hidden, 1));
    NetInitialize.of(netLayer);
    NetOptimizer netOptimizer = new NetSimpleGradientDescent(netLayer, learningRate);
    netOptimizer = new NetMomentumGradientDescent(netLayer, beta, learningRate);
    netTrain = new NetTrain(netLayer, netOptimizer);
    netTrain.addListener(netParamTracker);
    cbb = CoordinateBounds.of(trainingData.xdata());
    netTrain.addListener(new NetTrainListener() {
      @Override
      public boolean epoch(NetTrain netTrain, int epoch) {
        DensityPlot densityPlot = DensityPlot.of((x, y) -> netLayer.forward(Tensors.of(x, y)).Get(0), cbb, cdg);
        ScalableImage scalableImage = densityPlot.getScalableImage(100);
        BufferedImage bufferedImage = scalableImage.getScaledInstance(ImageResize.DEGREE_3, RealScalar.ONE);
        imageIconRecorder.write(bufferedImage);
        return true;
      }
    });
    netTrain.runAsBatches(trainingData, batch_size, 3000, Quantity.of(5, "s"));
    // netTrain.run(RealScalar.of(0.3), 3, 3000, Quantity.of(1.3, "s"));
  }

  public Show showTParam() {
    Show show = new Show();
    show.setShowLabel("net param");
    TableBuilder table = netParamTracker.tparam;
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
