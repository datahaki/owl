// code by jph
package ch.alpine.owl.net;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.time.Duration;

import javax.swing.ImageIcon;

import ch.alpine.ascony.dis.ManifoldDisplay;
import ch.alpine.ascony.ren.ColorPairIndexed;
import ch.alpine.ascony.ren.PointsRender;
import ch.alpine.ascony.win.ControlPointType;
import ch.alpine.ascony.win.EuclideanPlaneDemo;
import ch.alpine.bridge.awt.AwtUtil;
import ch.alpine.bridge.awt.ScalableImage;
import ch.alpine.bridge.fig.Show;
import ch.alpine.bridge.fig.plt.DensityPlot;
import ch.alpine.bridge.fig.plt.ListLinePlot;
import ch.alpine.bridge.gfx.GeometricLayer;
import ch.alpine.bridge.io.ImageIconRecorder;
import ch.alpine.bridge.pro.ManipulateProvider;
import ch.alpine.bridge.ref.ann.FieldFuse;
import ch.alpine.bridge.ref.ann.FieldSelectionArray;
import ch.alpine.bridge.ref.ann.ReflectionMarker;
import ch.alpine.subare.net.NetChain;
import ch.alpine.subare.net.NetChains;
import ch.alpine.subare.net.NetTrain;
import ch.alpine.subare.net.NetTrainListener;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.img.ColorDataGradients;
import ch.alpine.tensor.img.ColorDataIndexed;
import ch.alpine.tensor.img.ColorDataLists;
import ch.alpine.tensor.img.ImageResize;
import ch.alpine.tensor.io.TableBuilder;
import ch.alpine.tensor.opt.nd.CoordinateBoundingBox;
import ch.alpine.tensor.opt.nd.CoordinateBounds;
import ch.alpine.tensor.pdf.RandomSample;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;
import ch.alpine.tensor.qty.Quantity;

class NetClassifyDemo extends EuclideanPlaneDemo {
  @ReflectionMarker
  static class Param0 {
    @FieldSelectionArray({ "10", "20", "30", "40", "50" })
    public Integer size = 20;
    @FieldSelectionArray({ "2", "3", "4", "5" })
    public Integer labels = 3;
    @FieldFuse
    public transient Boolean shuffle = false;
  }

  @ReflectionMarker
  static class Param1 {
    @FieldSelectionArray({ "4", "5", "6", "7", "8", "9", "10" })
    public Integer hidden = 7;
    public Boolean animate = false;
    @FieldFuse
    public transient Boolean train = false;
  }

  @ReflectionMarker
  static class Param2 {
    public ColorDataGradients cdg = ColorDataGradients.COPPER;
    public ColorDataLists cdl = ColorDataLists._097;
  }

  private final Param0 param0;
  private final Param1 param1;
  private final Param2 param2;
  // ---
  protected Tensor vector;

  public NetClassifyDemo() {
    super(param0 = new Param0(), param1 = new Param1(), param2 = new Param2());
    geometricComponent().setRotatable(false);
    fieldsEditor(param0).addUniversalListener(this::shuffle);
    fieldsEditor(param1).addUniversalListener(this::train);
    shuffle();
  }

  @Override
  protected ControlPointType controlPointType() {
    return ControlPointType.DELEGATED;
  }

  private void shuffle() {
    int n = param0.size;
    ManifoldDisplay manifoldDisplay = manifoldDisplay();
    Tensor tensor = Tensor.of(RandomSample.of(manifoldDisplay.randomSampleInterface(), n).stream() //
        .map(manifoldDisplay::point2xya));
    setControlPointsSe2(tensor);
    // assignment of random labels to points
    int k = param0.labels;
    vector = RandomVariate.of(DiscreteUniformDistribution.forArray(k), n);
    train();
  }

  NetChain netChain;
  NetTrain netTrain;

  private void train() {
    Tensor xdata = getGeodesicControlPoints();
    netChain = NetChains.argMaxMLP(2, param1.hidden, param0.labels);
    netTrain = new NetTrain(netChain, xdata, vector);
    CoordinateBoundingBox cbb = CoordinateBounds.of(xdata);
    ImageIconRecorder imageIconRecorder = ImageIconRecorder.loop(Duration.ofMillis(200));
    netTrain.addListener(new NetTrainListener() {
      @Override
      public void epoch(int epoch) {
        DensityPlot densityPlot = DensityPlot.of((x, y) -> (Scalar) netChain.forward(Tensors.of(x, y)), cbb, param2.cdg);
        ScalableImage scalableImage = densityPlot.getScalableImage(100);
        BufferedImage bufferedImage = scalableImage.getScaledInstance(ImageResize.DEGREE_3, RealScalar.ONE);
        imageIconRecorder.write(bufferedImage);
      }
    });
    netTrain.run(RealScalar.of(0.05), Quantity.of(1.3, "s"), 3000, 10);
    if (param1.animate) {
      ImageIcon imageIcon = imageIconRecorder.getIconImage();
      ManipulateProvider manipulateProvider = new ManipulateProvider() {
        @Override
        public Container getContainer() {
          return AwtUtil.iconAsLabel(imageIcon);
        }
      };
      manipulateProvider.runStandalone();
    }
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    ManifoldDisplay manifoldDisplay = manifoldDisplay();
    Tensor xdata = getGeodesicControlPoints();
    CoordinateBoundingBox cbb = CoordinateBounds.of(xdata);
    Rectangle rectangle = geometricLayer.toRectangle(cbb).orElseThrow();
    {
      Show show = new Show();
      show.add(DensityPlot.of((x, y) -> (Scalar) netChain.forward(Tensors.of(x, y)), cbb, param2.cdg));
      // show.setAspectRatioDontCare();
      show.render(graphics, rectangle);
    } // ---
    render(geometricLayer, graphics, manifoldDisplay, getGeodesicControlPoints(), vector, param2.cdl.cyclic());
    Dimension dimension = geometricComponent().getSize();
    dimension.width /= 2;
    dimension.height /= 2;
    {
      Show show = new Show();
      TableBuilder table = netTrain.tparam;
      int n = table.getRow(0).length();
      for (int i = 1; i < n; ++i)
        show.add(ListLinePlot.of(table.getColumns(0, i)));
      // show.setPlotLabel("Error: " + error.maps(Round._3));
      show.render_autoIndent(graphics, new Rectangle(dimension.width, 0, dimension.width, dimension.height));
    }
    {
      Show show = new Show();
      TableBuilder table = netTrain.tloss;
      int n = table.getRow(0).length();
      for (int i = 1; i < n; ++i)
        show.add(ListLinePlot.of(table.getColumns(0, i)));
      // show.setPlotLabel("Error: " + error.maps(Round._3));
      show.render_autoIndent(graphics, new Rectangle(dimension.width, dimension.height, dimension.width, dimension.height));
    }
  }

  static void render(GeometricLayer geometricLayer, Graphics2D graphics, ManifoldDisplay manifoldDisplay, Tensor sequence, Tensor vector,
      ColorDataIndexed colorDataIndexedT) {
    Tensor shape = manifoldDisplay.shape().multiply(RealScalar.of(1.0));
    int index = 0;
    ColorPairIndexed colorPairIndexed = new ColorPairIndexed(colorDataIndexedT, 128, 255);
    for (Tensor point : sequence) {
      int label = vector.Get(index).number().intValue();
      new PointsRender(colorPairIndexed.getColorPair(label), manifoldDisplay::matrixLift, shape, Tensors.of(point)) //
          .render(geometricLayer, graphics);
      ++index;
    }
  }

  static void main() {
    new NetClassifyDemo().runStandalone();
  }
}
