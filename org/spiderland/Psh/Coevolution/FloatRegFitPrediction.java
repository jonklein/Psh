package org.spiderland.Psh.Coevolution;

import org.spiderland.Psh.GAIndividual;
import org.spiderland.Psh.PushGPIndividual;

public class FloatRegFitPrediction extends PredictionGA {
	private static final long serialVersionUID = 1L;

	@Override
	protected void InitIndividual(GAIndividual inIndividual) {
		FloatRegPredictionIndividual i = (FloatRegPredictionIndividual) inIndividual;

		int[] samples = new int[FloatRegPredictionIndividual._sampleSize];
		for(int j = 0; j < samples.length; j++){
			samples[j] = _RNG.nextInt(_testCases.size());
			
		}
		i.SetSampleIndicesAndSolutionGA(_solutionGA, samples);
	}

	@Override
	protected void EvaluateIndividual(GAIndividual inIndividual) {
		// TODO Auto-generated method stub

	}

	@Override
	public float EvaluateTestCase(GAIndividual inIndividual, Object inInput,
			Object inOutput) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	protected PushGPIndividual ChooseNewTrainer() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	protected void EvaluateTrainerFitnesses() {
		// TODO Auto-generated method stub

	}

	@Override
	protected GAIndividual ReproduceByCrossover(int inIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected GAIndividual ReproduceByMutation(int inIndex) {
		// TODO Auto-generated method stub
		return null;
	}

}
