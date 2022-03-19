// code by fluric
package ch.alpine.subare.demo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ch.alpine.subare.analysis.DiscreteModelErrorAnalysis;
import ch.alpine.subare.analysis.MonteCarloAlgorithms;
import ch.alpine.subare.analysis.MonteCarloExamples;
import ch.alpine.subare.analysis.SarsaMonteCarloTrial;
import ch.alpine.subare.core.MonteCarloInterface;
import ch.alpine.subare.core.StandardModel;
import ch.alpine.subare.core.StateActionCounter;
import ch.alpine.subare.core.alg.ActionValueIterations;
import ch.alpine.subare.core.td.SarsaType;
import ch.alpine.subare.core.util.ConstantLearningRate;
import ch.alpine.subare.core.util.DiscreteQsa;
import ch.alpine.subare.core.util.DiscreteStateActionCounter;
import ch.alpine.subare.core.util.PolicyBase;
import ch.alpine.subare.core.util.PolicyType;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.Timing;

/* package */ enum MonteCarloAnalysis {
  ;
  public static void analyse(MonteCarloInterface monteCarloInterface, int trials, int batches, List<MonteCarloAlgorithms> algorithmList,
      List<DiscreteModelErrorAnalysis> errorAnalysisList) {
    DiscreteQsa optimalQsa = getOptimalQsa(monteCarloInterface, batches);
    Map<String, Tensor> algorithmResults = new LinkedHashMap<>();
    // ---
    for (MonteCarloAlgorithms monteCarloAlgorithms : algorithmList)
      algorithmResults.put(monteCarloAlgorithms.name(),
          monteCarloAlgorithms.analyseNTimes(monteCarloInterface, batches, optimalQsa, errorAnalysisList, trials));
    StaticHelper.createPlot(algorithmResults,
        "Convergence_" + monteCarloInterface.getClass().getSimpleName().toString() + "_" + trials + "trials" + "_" + batches + "batches", errorAnalysisList);
  }

  public static DiscreteQsa getOptimalQsa(MonteCarloInterface monteCarloInterface, int batches) {
    if (!(monteCarloInterface instanceof StandardModel)) { // if no AVI is possible, try to approximate it
      System.out.println("Approximating optimal QSA because the model does not implement StandardModel!");
      DiscreteQsa qsa = DiscreteQsa.build(monteCarloInterface);
      StateActionCounter sac = new DiscreteStateActionCounter();
      PolicyBase policy = PolicyType.EGREEDY.bestEquiprobable(monteCarloInterface, qsa, sac);
      final SarsaMonteCarloTrial sarsa = SarsaMonteCarloTrial.of(monteCarloInterface, SarsaType.QLEARNING, //
          ConstantLearningRate.of(RealScalar.of(0.05)), qsa, sac, policy, 1);
      Timing timing = Timing.started();
      for (int index = 0; index < batches * 10; ++index)
        sarsa.executeBatch();
      System.out.println("Time for optimal QSA approximation: " + timing.seconds() + "s");
      return sarsa.qsa();
    }
    Timing timing = Timing.started();
    DiscreteQsa optimalQsa = ActionValueIterations.solve((StandardModel) monteCarloInterface, RealScalar.of(0.0001));
    System.out.println("Time for AVI: " + timing.seconds() + "s");
    return optimalQsa;
  }

  public static void main(String[] args) {
    MonteCarloInterface monteCarloInterface = MonteCarloExamples.AIRPORT.get();
    // ---
    List<MonteCarloAlgorithms> list = new ArrayList<>();
    // list.add(MonteCarloAlgorithms.MONTE_CARLO);
    // list.add(MonteCarloAlgorithms.ORIGINAL_SARSA);
    // list.add(MonteCarloAlgorithms.ORIGINAL_TRUE_ONLINE_SARSA);
    // list.add(MonteCarloAlgorithms.DOUBLE_ORIGINAL_SARSA);
    // list.add(MonteCarloAlgorithms.EXPECTED_SARSA);
    // list.add(MonteCarloAlgorithms.EXPECTED_TRUE_ONLINE_SARSA);
    // list.add(MonteCarloAlgorithms.DOUBLE_EXPECTED_SARSA);
    // list.add(MonteCarloAlgorithms.QLEARNING_SARSA);
    // list.add(MonteCarloAlgorithms.QLEARNING_SARSA_UCB);
    // list.add(MonteCarloAlgorithms.QLEARNING_SARSA_LINEAR_EXPLORATION);
    // list.add(MonteCarloAlgorithms.QLEARNING_SARSA_EXPONENTIAL_EXPLORATION);
    // list.add(MonteCarloAlgorithms.QLEARNING_TRUE_ONLINE_SARSA);
    // list.add(MonteCarloAlgorithms.DOUBLE_QLEARNING_SARSA);
    // ---
    List<DiscreteModelErrorAnalysis> errorAnalysis = new ArrayList<>();
    errorAnalysis.add(DiscreteModelErrorAnalysis.LINEAR_POLICY);
    errorAnalysis.add(DiscreteModelErrorAnalysis.LINEAR_QSA);
    // ---
    analyse(monteCarloInterface, 1, 10, list, errorAnalysis);
  }
}
