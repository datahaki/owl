// code by jph
package ch.alpine.owl.util.bot;

import java.awt.image.BufferedImage;

import ch.alpine.owl.bot.r2.ImageEdges;
import ch.alpine.owl.util.img.ImageBlackWhiteQ;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.ResourceData;
import ch.alpine.tensor.io.Import;

public class StreetScenarioData {
  public static StreetScenarioData load(String id) {
    return new StreetScenarioData(id);
  }

  // ---
  /** rgba */
  public final BufferedImage render;
  /** bw; filename "ped_obs_legal.png" */
  public final Tensor imagePedLegal;
  /** bw; filename "ped_obs_illegal.png" */
  public final Tensor imagePedIllegal;
  /** bw; filename "car_obs_1.png" */
  private final Tensor imageCar;
  /** bw obstacles detected by lidar and creating occlusions */
  public final Tensor imageLid;
  /** string to resource with grayscale */
  public final String imageLanesString;
  // public final Tensor imageLanes;

  private StreetScenarioData(String id) {
    final String prefix = "/simulation/" + id + "/";
    render = ResourceData.bufferedImage(prefix + "render.png");
    imagePedLegal = ImageBlackWhiteQ.require(Import.of(prefix + "ped_obs_legal.png"));
    imagePedIllegal = ImageBlackWhiteQ.require(Import.of(prefix + "ped_obs_illegal.png"));
    imageCar = ImageBlackWhiteQ.require(Import.of(prefix + "car_obs_1.png"));
    imageLid = ImageBlackWhiteQ.require(Import.of(prefix + "lidar_obs.png"));
    imageLanesString = prefix + "car_lanes.png";
  }

  public Tensor imageCar_extrude(int width) {
    return ImageEdges.extrusion(imageCar, width);
  }

  Tensor imageLanes() {
    return Import.of(imageLanesString);
  }
}
