// code by fluric
package ch.alpine.subare.demo;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import ch.alpine.subare.analysis.DiscreteModelErrorAnalysis;
import ch.alpine.subare.analysis.MonteCarloAlgorithms;
import ch.alpine.subare.analysis.MonteCarloExamples;
import ch.alpine.subare.core.MonteCarloInterface;
import ch.alpine.subare.core.util.DiscreteQsa;

class MonteCarloAlgorithmsTest {
  @Test
  public void testExamplesWithSarsa() {
    checkExampleWithSarsa(MonteCarloExamples.AIRPORT, true);
    checkExampleWithSarsa(MonteCarloExamples.CLIFFWALK, false);
    checkExampleWithSarsa(MonteCarloExamples.GAMBLER_20, true);
    checkExampleWithSarsa(MonteCarloExamples.GRIDWORLD, true);
    checkExampleWithSarsa(MonteCarloExamples.INFINITEVARIANCE, true);
    checkExampleWithSarsa(MonteCarloExamples.MAXBIAS, true);
    checkExampleWithSarsa(MonteCarloExamples.MAZE2, false);
    checkExampleWithSarsa(MonteCarloExamples.RACETRACK, false);
    // checkExampleWithSarsa(MonteCarloExamples.WINDYGRID, false); // too slow
    checkExampleWithSarsa(MonteCarloExamples.WIRELOOP_4, false);
    checkExampleWithSarsa(MonteCarloExamples.WIRELOOP_C, false);
  }

  private static void checkExampleWithSarsa(MonteCarloExamples example, boolean withTrueOnline) {
    System.out.println("Testing: " + example.toString());
    int batches = 5;
    DiscreteQsa optimalQsa = MonteCarloAnalysisShow.getOptimalQsa(example.get(), batches);
    List<MonteCarloAlgorithms> list = new ArrayList<>();
    list.add(MonteCarloAlgorithms.ORIGINAL_SARSA);
    list.add(MonteCarloAlgorithms.EXPECTED_SARSA);
    list.add(MonteCarloAlgorithms.QLEARNING_SARSA);
    list.add(MonteCarloAlgorithms.DOUBLE_QLEARNING_SARSA);
    if (withTrueOnline) {
      list.add(MonteCarloAlgorithms.ORIGINAL_TRUE_ONLINE_SARSA);
      list.add(MonteCarloAlgorithms.EXPECTED_TRUE_ONLINE_SARSA);
      list.add(MonteCarloAlgorithms.QLEARNING_TRUE_ONLINE_SARSA);
    }
    // ---
    List<DiscreteModelErrorAnalysis> errorAnalysis = new ArrayList<>();
    errorAnalysis.add(DiscreteModelErrorAnalysis.LINEAR_POLICY);
    // ---
    MonteCarloInterface monteCarloInterface = example.get();
    for (MonteCarloAlgorithms monteCarloAlgorithms : list)
      monteCarloAlgorithms.analyseNTimes(monteCarloInterface, batches, optimalQsa, errorAnalysis, 1);
  }

  @Test
  public void testExamplesWithSeveralTrials() {
    MonteCarloExamples example = MonteCarloExamples.AIRPORT;
    System.out.println("Testing: " + example.toString());
    int batches = 5;
    DiscreteQsa optimalQsa = MonteCarloAnalysisShow.getOptimalQsa(example.get(), batches);
    List<MonteCarloAlgorithms> list = new ArrayList<>();
    list.add(MonteCarloAlgorithms.ORIGINAL_SARSA);
    list.add(MonteCarloAlgorithms.DOUBLE_QLEARNING_SARSA);
    list.add(MonteCarloAlgorithms.ORIGINAL_TRUE_ONLINE_SARSA);
    list.add(MonteCarloAlgorithms.MONTE_CARLO);
    // ---
    List<DiscreteModelErrorAnalysis> errorAnalysis = new ArrayList<>();
    errorAnalysis.add(DiscreteModelErrorAnalysis.LINEAR_POLICY);
    // ---
    MonteCarloInterface monteCarloInterface = example.get();
    for (MonteCarloAlgorithms monteCarloAlgorithms : list) {
      monteCarloAlgorithms.analyseNTimes(monteCarloInterface, batches, optimalQsa, errorAnalysis, 10);
    }
  }

  @Test
  public void testExamplesWithSeveralErrorAnalysis() {
    MonteCarloExamples example = MonteCarloExamples.AIRPORT;
    System.out.println("Testing: " + example.toString());
    int batches = 5;
    DiscreteQsa optimalQsa = MonteCarloAnalysisShow.getOptimalQsa(example.get(), batches);
    List<MonteCarloAlgorithms> list = new ArrayList<>();
    list.add(MonteCarloAlgorithms.ORIGINAL_SARSA);
    list.add(MonteCarloAlgorithms.DOUBLE_QLEARNING_SARSA);
    list.add(MonteCarloAlgorithms.ORIGINAL_TRUE_ONLINE_SARSA);
    list.add(MonteCarloAlgorithms.MONTE_CARLO);
    // ---
    List<DiscreteModelErrorAnalysis> errorAnalysis = new ArrayList<>();
    errorAnalysis.add(DiscreteModelErrorAnalysis.LINEAR_POLICY);
    errorAnalysis.add(DiscreteModelErrorAnalysis.LINEAR_QSA);
    // ---
    MonteCarloInterface monteCarloInterface = example.get();
    for (MonteCarloAlgorithms monteCarloAlgorithms : list)
      monteCarloAlgorithms.analyseNTimes(monteCarloInterface, batches, optimalQsa, errorAnalysis, 2);
  }

  @Test
  public void testExamplesWithMC() {
    checkExampleWithMC(MonteCarloExamples.AIRPORT);
    checkExampleWithMC(MonteCarloExamples.GAMBLER_20);
    checkExampleWithMC(MonteCarloExamples.INFINITEVARIANCE);
    checkExampleWithMC(MonteCarloExamples.MAXBIAS);
    checkExampleWithMC(MonteCarloExamples.MAZE2);
    checkExampleWithMC(MonteCarloExamples.RACETRACK);
  }

  private static void checkExampleWithMC(MonteCarloExamples example) {
    System.out.println("Testing: " + example.toString());
    int batches = 10;
    DiscreteQsa optimalQsa = MonteCarloAnalysisShow.getOptimalQsa(example.get(), batches);
    List<MonteCarloAlgorithms> list = new ArrayList<>();
    list.add(MonteCarloAlgorithms.MONTE_CARLO);
    // ---
    List<DiscreteModelErrorAnalysis> errorAnalysis = new ArrayList<>();
    errorAnalysis.add(DiscreteModelErrorAnalysis.LINEAR_POLICY);
    // ---
    MonteCarloInterface monteCarloInterface = example.get();
    for (MonteCarloAlgorithms monteCarloAlgorithms : list)
      monteCarloAlgorithms.analyseNTimes(monteCarloInterface, batches, optimalQsa, errorAnalysis, 1);
  }

  @Test
  public void testVirtualStationExample() {
    MonteCarloExamples example = MonteCarloExamples.VIRTUALSTATIONS;
    System.out.println("Testing: " + example.toString());
    int batches = 1;
    DiscreteQsa optimalQsa = MonteCarloAnalysisShow.getOptimalQsa(example.get(), batches);
    List<MonteCarloAlgorithms> list = new ArrayList<>();
    list.add(MonteCarloAlgorithms.MONTE_CARLO);
    list.add(MonteCarloAlgorithms.EXPECTED_SARSA);
    list.add(MonteCarloAlgorithms.QLEARNING_SARSA);
    list.add(MonteCarloAlgorithms.DOUBLE_QLEARNING_SARSA);
    // ---
    List<DiscreteModelErrorAnalysis> errorAnalysis = new ArrayList<>();
    errorAnalysis.add(DiscreteModelErrorAnalysis.LINEAR_POLICY);
    // ---
    MonteCarloInterface monteCarloInterface = example.get();
    for (MonteCarloAlgorithms monteCarloAlgorithms : list)
      monteCarloAlgorithms.analyseNTimes(monteCarloInterface, batches, optimalQsa, errorAnalysis, 1);
  }
}
