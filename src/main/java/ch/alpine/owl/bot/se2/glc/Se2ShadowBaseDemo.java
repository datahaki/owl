// code by ynager, jph
package ch.alpine.owl.bot.se2.glc;

import java.awt.Color;

import ch.alpine.owl.bot.util.StreetScenario;
import ch.alpine.owl.bot.util.StreetScenarioData;
import ch.alpine.owl.math.region.ImageRegion;
import ch.alpine.owl.math.state.SimpleTrajectoryRegionQuery;
import ch.alpine.owl.math.state.TrajectoryRegionQuery;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

public abstract class Se2ShadowBaseDemo extends Se2CarDemo {
  static final StreetScenarioData STREET_SCENARIO_DATA = StreetScenario.S5.load();
  static final float PED_VELOCITY = 1.5f;
  static final float PED_RADIUS = 0.2f;
  static final Color PED_COLOR_LEGAL = new Color(211, 249, 114, 200);
  static final Tensor RANGE = Tensors.vector(57.2, 44.0);
  Tensor imagePed = STREET_SCENARIO_DATA.imagePedLegal;
  Tensor imageLid = STREET_SCENARIO_DATA.imagePedIllegal;
  ImageRegion imageRegionPed = new ImageRegion(imagePed, RANGE, false);
  ImageRegion imageRegionLid = new ImageRegion(imageLid, RANGE, true);
  TrajectoryRegionQuery trajectoryRegionQuery = SimpleTrajectoryRegionQuery.timeInvariant(imageRegionLid);
}
