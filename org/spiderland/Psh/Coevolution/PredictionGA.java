/*
 * Copyright 2009-2010 Jon Klein
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.spiderland.Psh.Coevolution;

import java.util.ArrayList;
import java.util.HashMap;

import org.spiderland.Psh.GA;
import org.spiderland.Psh.GAIndividual;
import org.spiderland.Psh.PushGP;
import org.spiderland.Psh.PushGPIndividual;

/**
 * An abstract class for a population of co-evolving predictors of fitness,
 * rank, or something similar. In this class, "fitness" or "predicted fitness"
 * of a solution individual may be referring to the actual fitness of the
 * individual, or it may be referring to something similar, such as the
 * individual's rank.
 */
public abstract class PredictionGA extends GA {
	private static final long serialVersionUID = 1L;

	// Note: Oldest trainer has the lowest index; newest trainer has the highest
	// index.
	protected ArrayList<PushGPIndividual> _trainerPopulation;
	protected int _generationsBetweenTrainers;
	protected int _trainerPopulationSize;

	// The solution population and genetic algorithm.
	protected PushGP _solutionGA;

	/**
	 * Customizes GA.GAWithParameters to allow the inclusion of the solution GA,
	 * which is required for the initialization of the prediction GA.
	 * 
	 * @param ceFloatSymbolicRegression
	 * @param getPredictorParameters
	 * @return
	 * @throws Exception 
	 */
	public static PredictionGA PredictionGAWithParameters(PushGP inSolutionGA,
			HashMap<String, String> inParams) throws Exception {

		Class<?> cls = Class.forName(inParams.get("problem-class"));
		Object gaObject = cls.newInstance();
		if (!(gaObject instanceof PredictionGA))
			throw (new Exception("Predictor problem-class must inherit from"
					+ " class PredictorGA"));

		PredictionGA ga = (PredictionGA) gaObject;

		// Must set the solution GA before InitFromParameters, since the latter
		// uses _solutionGA while creating the predictor population.
		ga.SetSolutionGA(inSolutionGA);
		ga.SetParams(inParams);
		ga.InitFromParameters();

		return ga;
	}

	@Override
	protected void InitFromParameters() throws Exception {
		_generationsBetweenTrainers = (int) GetFloatParam("generations-between-trainers");
		_trainerPopulationSize = (int) GetFloatParam("trainer-population-size");

		InitTrainerPopulation();
		
		super.InitFromParameters();
	}

	/**
	 * Runs a single generation.
	 * 
	 * @throws Exception
	 */
	public void RunGeneration() throws Exception {
		Run(1);
	}

	@Override
	protected void BeginGeneration() {
		if (_generationCount % _generationsBetweenTrainers == _generationsBetweenTrainers - 1) {
			// Time to add a new trainer
			PushGPIndividual newTrainer = (PushGPIndividual) ChooseNewTrainer().clone();
			EvaluateSolutionIndividual(newTrainer);
			
			_trainerPopulation.remove(0);
			_trainerPopulation.add(newTrainer);
			
			EvaluateTrainerFitnesses();
			
		}
	}

	@Override
	public boolean Terminate() {
		return false;
	}
	
	@Override
	protected boolean Success() {
		return false;
	}

	/**
	 * Chooses a new trainer from the solution population to add to the trainer
	 * population. The solution individual is chosen with the highest variance
	 * of the predictions from the current predictor population.
	 */
	protected PushGPIndividual ChooseNewTrainer() {
		ArrayList<Float> individualVariances = new ArrayList<Float>();
		
		for (int i = 0; i < _solutionGA.GetPopulationSize(); i++) {
			PushGPIndividual individual = (PushGPIndividual) _solutionGA
					.GetIndividualFromPopulation(i);

			ArrayList<Float> predictions = new ArrayList<Float>();
			for (int j = 0; j < _populations[_currentPopulation].length; j++) {
				PredictionGAIndividual predictor = (PredictionGAIndividual) _populations[_currentPopulation][j];
				predictions.add(predictor.PredictSolutionFitness(individual));		
			}

			individualVariances.add(Variance(predictions));
		}

		// Find individual with the highest variance
		int highestVarianceIndividual = 0;
		float highestVariance = individualVariances.get(0);

		for (int i = 0; i < _solutionGA.GetPopulationSize(); i++) {
			if (highestVariance < individualVariances.get(i)) {
				highestVarianceIndividual = i;
				highestVariance = individualVariances.get(i);
			}
		}

		return (PushGPIndividual) _solutionGA
				.GetIndividualFromPopulation(highestVarianceIndividual);
	}

	protected PredictionGAIndividual GetBestPredictor(){
		float bestFitness = Float.MAX_VALUE;
		GAIndividual bestPredictor = _populations[_currentPopulation][0];
		
		for(GAIndividual ind : _populations[_currentPopulation]){
			if(ind.GetFitness() < bestFitness){
				bestPredictor = ind;
				bestFitness = ind.GetFitness();
			}
		}
		
		return (PredictionGAIndividual) bestPredictor;		
	}
	
	/**
	 * Calculates and sets the exact fitness from any individual of the
	 * _solutionGA population. This includes trainers.
	 * 
	 * @param inIndividual
	 */
	protected void EvaluateSolutionIndividual(PushGPIndividual inIndividual) {
		_solutionGA.EvaluateIndividual(inIndividual);
	}
	
	protected void SetSolutionGA(PushGP inGA) {
		_solutionGA = inGA;
	}

	/**
	 * This must be private, since there must be a _solutionGA set before this
	 * method is invoked. Use SetGAandTrainers() instead.
	 */
	private void InitTrainerPopulation() {
		_trainerPopulation = new ArrayList<PushGPIndividual>();

		PushGPIndividual individual = new PushGPIndividual();

		for (int i = 0; i < _trainerPopulationSize; i++) {
			_trainerPopulation.add((PushGPIndividual) individual.clone());
			_solutionGA.InitIndividual(_trainerPopulation.get(i));
		}
		
		EvaluateTrainerFitnesses();
	}
	
	protected String Report() {
		String report = super.Report();
		report = report.replace('-', '#');
		report = report.replaceFirst("Report for", " Predictor");

		report += ";; Best Predictor: "
				+ _populations[_currentPopulation][_bestIndividual] + "\n";
		report += ";; Best Predictor Fitness: " + _bestMeanFitness + "\n\n";

		report += ";; Mean Predictor Fitness: " + _populationMeanFitness + "\n";
				
		// The following code prints all of the predictors.
		/*
		report += "\n;; Mean Predictor Fitness: \n";
		for(GAIndividual predictor : _populations[_currentPopulation]){
			report += "          " + predictor + "\n";
			
		}
		*/
		
		report += ";;########################################################;;\n\n";
		
		return report;
	}

	protected String FinalReport() {
		return "";
	}
	
	private Float Variance(ArrayList<Float> list) {
		float sampleMean = SampleMean(list);
		float sum = 0;
		
		for(float element : list){
			sum += (element - sampleMean) * (element - sampleMean);
		}
		
		return (sum / (list.size() - 1));
	}

	private float SampleMean(ArrayList<Float> list) {
		float total = 0;
		for(float element : list){
			total += element;
		}
		return (total / list.size());
	}

	/**
	 * Initiates inIndividual as a random predictor individual.
	 */
	@Override
	protected abstract void InitIndividual(GAIndividual inIndividual);

	/**
	 * Evaluates a PredictionGAIndividual's fitness, based on the difference
	 * between the predictions of the fitnesses of the trainers and the actual
	 * fitnesses of the trainers.
	 */
	@Override
	protected abstract void EvaluateIndividual(GAIndividual inIndividual);

	/**
	 * Determines the predictor's fitness on a trainer, where the trainer is the
	 * inInput, and the trainer's actual fitness (or rank, whatever is to be
	 * predicted) is inOutput.
	 * 
	 * @return Predictor's fitness (or rank, etc.) for the given trainer.
	 */
	@Override
	public abstract float EvaluateTestCase(GAIndividual inIndividual,
			Object inInput, Object inOutput);

	/**
	 * Actual fitnesses of trainers will always be stored as part of the
	 * PushGPIndividual object. Some predictor types, such as rank predictors,
	 * will also need a separate storage of data, such as a method of storing
	 * the ranking of the predictors. Others, such as fitness predictors, may
	 * just need the fitness information directly from the trainers. This
	 * function may be used to make sure fitnesses or ranks are updated, i.e. to
	 * recalculate rank order with the addition of a new trainer.
	 */
	protected abstract void EvaluateTrainerFitnesses();

	@Override
	protected abstract GAIndividual ReproduceByMutation(int inIndex);
	
	@Override
	protected abstract GAIndividual ReproduceByCrossover(int inIndex);

}
