package org.spiderland.Psh.Coevolution;

import java.util.ArrayList;

import org.spiderland.Psh.GAIndividual;
import org.spiderland.Psh.PushGPIndividual;

public class FloatRegFitPrediction extends PredictionGA {
	private static final long serialVersionUID = 1L;

	@Override
	protected void InitIndividual(GAIndividual inIndividual) {
		FloatRegFitPredictionIndividual i = (FloatRegFitPredictionIndividual) inIndividual;
		
		int[] samples = new int[FloatRegFitPredictionIndividual._sampleSize];
		for(int j = 0; j < samples.length; j++){
			samples[j] = _RNG.nextInt(_solutionGA._testCases.size());
		}
		i.SetSampleIndicesAndSolutionGA(_solutionGA, samples);
	}

	@Override
	protected void EvaluateIndividual(GAIndividual inIndividual) {
		
		FloatRegFitPredictionIndividual predictor = (FloatRegFitPredictionIndividual) inIndividual;
		ArrayList<Float> errors = new ArrayList<Float>();

		for(int i = 0; i < _trainerPopulationSize; i++){
			float e = predictor.PredictSolutionFitness(_trainerPopulation.get(i));
			errors.add(e);
		}
		
		predictor.SetFitness(AbsoluteAverageOfErrors(errors));
		predictor.SetErrors(errors);
	}

	/**
	 * Determines the predictor's fitness on a trainer, where the trainer is the
	 * inInput, and the trainer's actual fitness is inOutput. The fitness of
	 * the predictor is the absolute error between the prediction and the
	 * trainer's actual fitness.
	 * 
	 * @return Predictor's fitness (i.e. error) for the given trainer.
	 * @throws Exception 
	 */
	@Override
	public float EvaluateTestCase(GAIndividual inIndividual, Object inInput,
			Object inOutput) {

		PushGPIndividual trainer = (PushGPIndividual) inInput;
		float trainerFitness = (Float) inOutput;

		float predictedTrainerFitness = ((PredictionGAIndividual) inIndividual)
				.PredictSolutionFitness(trainer);

		return Math.abs(predictedTrainerFitness - trainerFitness);
	}

	@Override
	protected PushGPIndividual ChooseNewTrainer() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	protected void EvaluateTrainerFitnesses() {
		for(PushGPIndividual trainer : _trainerPopulation){
			if(!trainer.FitnessIsSet()){
				EvaluateTrainer(trainer);
			}	
		}
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
