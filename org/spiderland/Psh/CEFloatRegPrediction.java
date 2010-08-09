package org.spiderland.Psh;

public class CEFloatRegPrediction extends CEPredictionGA {
	private static final long serialVersionUID = 1L;

	@Override
	protected void InitIndividual(GAIndividual inIndividual) {
		CEFloatRegPredictionIndividual i = (CEFloatRegPredictionIndividual) inIndividual;

		int[] samples = new int[CEFloatRegPredictionIndividual._sampleSize];
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
	protected float EvaluateTestCase(GAIndividual inIndividual, Object inInput,
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
