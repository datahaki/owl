// code by jph
package ch.alpine.owl.net;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import ch.alpine.ascony.dis.ManifoldDisplay;
import ch.alpine.ascony.ren.PointsRender;
import ch.alpine.ascony.win.ControlPointType;
import ch.alpine.ascony.win.ControlPointTypes;
import ch.alpine.ascony.win.EuclideanPlaneDemo;
import ch.alpine.bridge.fig.DensityPlot;
import ch.alpine.bridge.fig.Show;
import ch.alpine.bridge.gfx.GeometricLayer;
import ch.alpine.bridge.ref.ann.FieldFuse;
import ch.alpine.bridge.ref.ann.FieldSelectionArray;
import ch.alpine.bridge.ref.ann.ReflectionMarker;
import ch.alpine.subare.net.NetChain;
import ch.alpine.subare.net.NetChains;
import ch.alpine.subare.net.NetTrain;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.img.ColorDataGradients;
import ch.alpine.tensor.img.ColorDataIndexed;
import ch.alpine.tensor.img.ColorDataLists;
import ch.alpine.tensor.opt.nd.CoordinateBoundingBox;
import ch.alpine.tensor.opt.nd.CoordinateBounds;
import ch.alpine.tensor.pdf.RandomSample;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;
import ch.alpine.tensor.qty.Quantity;

class NetClassifyDemo extends EuclideanPlaneDemo {
  @ReflectionMarker
  static class Param0 {
    @FieldSelectionArray({ "10", "20", "50" })
    public Integer size = 20;
    @FieldSelectionArray({ "2", "3", "4", "5" })
    public Integer labels = 3;
    @FieldFuse
    public transient Boolean shuffle = false;
  }

  @ReflectionMarker
  static class Param1 {
    @FieldFuse
    public transient Boolean train = false;
  }

  @ReflectionMarker
  static class Param2 {
    public ColorDataGradients cdg = ColorDataGradients.COPPER;
    public ColorDataLists cdl = ColorDataLists._097;
  }

  private final Param0 param0;
  @SuppressWarnings("unused")
  private final Param1 param1;
  private final Param2 param2;
  // ---
  protected Tensor vector;

  public NetClassifyDemo() {
    super(param0 = new Param0(), param1 = new Param1(), param2 = new Param2());
    fieldsEditor(0).addUniversalListener(this::shuffle);
    fieldsEditor(1).addUniversalListener(this::train);
    shuffle();
  }

  @Override
  protected ControlPointType controlPointType() {
    return ControlPointTypes.DELEGATED;
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

  private void train() {
    Tensor xdata = getGeodesicControlPoints();
    netChain = NetChains.argMaxMLP(2, 7, param0.labels);
    NetTrain.of(netChain, xdata, vector, RealScalar.of(0.05), _ -> {
    }, Quantity.of(0.3, "s"), 3000, 10);
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    ManifoldDisplay manifoldDisplay = manifoldDisplay();
    Tensor xdata = getGeodesicControlPoints();
    CoordinateBoundingBox cbb = CoordinateBounds.of(xdata);
    Rectangle rectangle = geometricLayer.toRectangle(cbb);
    Show show = new Show();
    show.add(DensityPlot.of((x, y) -> (Scalar) netChain.forward(Tensors.of(x, y)), cbb, param2.cdg));
    // show.setAspectRatioDontCare();
    show.render(graphics, rectangle);
    // ---
    render(geometricLayer, graphics, manifoldDisplay, getGeodesicControlPoints(), vector, param2.cdl.cyclic());
  }

  static void render(GeometricLayer geometricLayer, Graphics2D graphics, ManifoldDisplay manifoldDisplay, Tensor sequence, Tensor vector,
      ColorDataIndexed colorDataIndexedT) {
    Tensor shape = manifoldDisplay.shape().multiply(RealScalar.of(1.0));
    int index = 0;
    ColorDataIndexed colorDataIndexedO = colorDataIndexedT.deriveWithAlpha(128);
    for (Tensor point : sequence) {
      int label = vector.Get(index).number().intValue();
      PointsRender pointsRender = new PointsRender( //
          colorDataIndexedO.getColor(label), //
          colorDataIndexedT.getColor(label));
      pointsRender.show(manifoldDisplay::matrixLift, shape, Tensors.of(point)).render(geometricLayer, graphics);
      ++index;
    }
  }

  static void main() {
    new NetClassifyDemo().runStandalone();
  }
}
