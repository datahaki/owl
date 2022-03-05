// code by jph
package ch.alpine.sophus.demo.bd2;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JButton;

import ch.alpine.java.ren.ArrayPlotRender;
import ch.alpine.sophus.demo.bdn.AbstractScatteredSetWeightingDemo;
import ch.alpine.sophus.demo.opt.LogWeighting;
import ch.alpine.sophus.demo.opt.LogWeightings;
import ch.alpine.sophus.gds.GeodesicArrayPlot;
import ch.alpine.sophus.gds.ManifoldDisplay;
import ch.alpine.sophus.hs.Biinvariant;
import ch.alpine.sophus.hs.Biinvariants;
import ch.alpine.sophus.hs.MetricBiinvariant;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.ConstantArray;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.ext.HomeDirectory;

public abstract class AbstractExportWeightingDemo extends AbstractScatteredSetWeightingDemo implements ActionListener {
  private static final int REFINEMENT = 120; // presentation 60
  private final JButton jButtonExport = new JButton("export");

  public AbstractExportWeightingDemo( //
      boolean addRemoveControlPoints, List<ManifoldDisplay> list, List<LogWeighting> array) {
    super(addRemoveControlPoints, list, array);
    {
      jButtonExport.addActionListener(this);
      timerFrame.jToolBar.add(jButtonExport);
    }
  }

  @Override
  public void actionPerformed(ActionEvent actionEvent) {
    LogWeighting logWeighting = logWeighting();
    File root = HomeDirectory.Pictures( //
        getClass().getSimpleName(), //
        manifoldDisplay().toString(), //
        logWeighting.toString());
    root.mkdirs();
    for (Biinvariant biinvariant : distinct()) {
      Tensor sequence = getGeodesicControlPoints();
      TensorUnaryOperator tensorUnaryOperator = logWeighting.operator( //
          biinvariant, //
          manifoldDisplay().hsManifold(), //
          variogram(), //
          sequence);
      System.out.print("computing " + biinvariant);
      // ---
      ArrayPlotRender arrayPlotRender = arrayPlotRender(sequence, REFINEMENT, tensorUnaryOperator, 1);
      BufferedImage bufferedImage = arrayPlotRender.export();
      try {
        ImageIO.write(bufferedImage, "png", new File(root, biinvariant.toString() + ".png"));
      } catch (Exception exception) {
        exception.printStackTrace();
      }
      System.out.println(" done");
    }
    System.out.println("all done");
  }

  protected final ArrayPlotRender arrayPlotRender(Tensor sequence, int refinement, TensorUnaryOperator tensorUnaryOperator, int magnification) {
    GeodesicArrayPlot geodesicArrayPlot = manifoldDisplay().geodesicArrayPlot();
    Tensor fallback = ConstantArray.of(DoubleScalar.INDETERMINATE, sequence.length());
    Tensor wgs = geodesicArrayPlot.raster(refinement, tensorUnaryOperator, fallback);
    return StaticHelper.arrayPlotFromTensor(wgs, magnification, logWeighting().equals(LogWeightings.DISTANCES), colorDataGradient());
  }

  private static List<Biinvariant> distinct() {
    return Arrays.asList( //
        MetricBiinvariant.EUCLIDEAN, // FIXME OWL ALG should be retrieved from bitype
        Biinvariants.LEVERAGES, //
        Biinvariants.GARDEN, //
        Biinvariants.HARBOR);
  }
}
