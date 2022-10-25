// code by fluric
package ch.alpine.subare.demo;

import java.awt.Dimension;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ch.alpine.bridge.fig.ListPlot;
import ch.alpine.bridge.fig.Show;
import ch.alpine.bridge.fig.Showable;
import ch.alpine.subare.analysis.DiscreteModelErrorAnalysis;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.HomeDirectory;

/* package */ enum StaticHelper {
  ;
  private static final int WIDTH = 1280;
  private static final int HEIGHT = 720;

  private static File directory() {
    File directory = HomeDirectory.Pictures("plots");
    directory.mkdir();
    return directory;
  }

  public static void createPlot(Map<String, Tensor> map, String path, List<DiscreteModelErrorAnalysis> errorAnalysisList) {
    for (int index = 0; index < errorAnalysisList.size(); ++index) {
      Show show = StaticHelper.create(map, index);
      // return a new chart containing the overlaid plot...
      String subPath = path + "_" + errorAnalysisList.get(index).name().toLowerCase();
      plot(subPath, subPath, "Number batches", "Error", show);
      try {
        savePlot(directory(), path, show);
      } catch (Exception exception) {
        exception.printStackTrace();
      }
    }
  }

  private static Show create(Map<String, Tensor> map, int index) {
    Show show = new Show();
    for (Entry<String, Tensor> entry : map.entrySet()) {
      Showable visualRow = show.add(ListPlot.of(entry.getValue()));
      visualRow.setLabel(entry.getKey());
    }
    return show;
  }

  private static void plot( //
      String filename, String diagramTitle, String axisLabelX, String axisLabelY, Show show) {
    show.setPlotLabel(diagramTitle);
    // show.add(ListPlot.of(null))
    // show.getAxisX().setLabel(axisLabelX);
    // show.getAxisY().setLabel(axisLabelY);
    // return ListPlot.of(show);
  }

  private static File savePlot(File directory, String fileTitle, Show show) throws Exception {
    File file = new File(directory, fileTitle + ".png");
    show.export(file, new Dimension(WIDTH, HEIGHT));
    System.out.println("Exported " + fileTitle + ".png to " + directory);
    return file;
  }
}
