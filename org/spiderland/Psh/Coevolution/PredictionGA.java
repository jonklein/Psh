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

	// Note: Oldest trainer has the highest index; newest trainer has the lowest
	// index.
	protected PushGPIndividual _trainerPopulation[];
	protected float _trainerFitnesses[];
	protected int _generationsBetweenTrainers;
	protected int _trainerPopulationSize;

	protected PushGP _solutionGA;

	@Override
	protected void InitFromParameters() throws Exception {
		_generationsBetweenTrainers = (int) GetFloatParam("generations-between-trainers");
		_trainerPopulationSize = (int) GetFloatParam("trainer-population-size");

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
		if (_generationCount % _generationsBetweenTrainers == (-1)
				% _generationsBetweenTrainers) {
			// Time to add a new trainer
			
			//TODO Fix below with the new functions
			/*AddNewTrainer();
			EvaluateTrainerFitnesses();*/
		}
	}

	@Override
	protected boolean Terminate() {
		// TODO Make sure it makes sense for this GA to never terminate.
		return false;
	}

	protected void SetGAandTrainers(PushGP inGA) {
		SetSolutionGA(inGA);
		InitTrainerPopulation();
	}

	private void SetSolutionGA(PushGP inGA) {
		_solutionGA = inGA;
	}

	/**
	 * This must be private, since there must be a _solutionGA set before this
	 * method is invoked. Use SetGAandTrainers() instead.
	 */
	private void InitTrainerPopulation() {
		_trainerPopulation = new PushGPIndividual[_trainerPopulationSize];

		PushGPIndividual individual = new PushGPIndividual();

		for (int i = 0; i < _trainerPopulationSize; i++) {
			_trainerPopulation[i] = (PushGPIndividual) individual.clone();
			_solutionGA.InitIndividual(_trainerPopulation[i]);
		}
	}

	protected String Report() {
		return "";
	}

	protected String FinalReport() {
		return "";
	}
	
	/**
	 * Initiates inIndividual as a random predictor individual.
	 */
	@Override
	protected abstract void InitIndividual(GAIndividual inIndividual);
	
	/**
	 * Evaluates a CEPredictorGAIndividual's fitness, based on the difference
	 * between the prediction of the fitness and the actual fitness of the
	 * trainers.
	 */
	@Override
	protected abstract void EvaluateIndividual(GAIndividual inIndividual);

	/**
	 * Determines the predictor's fitness on a trainer, where the trainer is the
	 * inInput, and the trainer's actual fitness (or rank, whatever is to be
	 * predicted) is inOutput.
	 */
	@Override
	public abstract float EvaluateTestCase(GAIndividual inIndividual,
			Object inInput, Object inOutput);

	/**
	 * Chooses a new trainer from the solution population to add to the trainer
	 * population. The solution individual is chosen with the highest variance
	 * of the predictions from the predictors.
	 */
	protected abstract PushGPIndividual ChooseNewTrainer();

	/**
	 * Calculates and sets inTrainer's fitness.
	 * @param inTrainer
	 */
	protected void EvaluateTrainer(PushGPIndividual inTrainer){
		_solutionGA.EvaluateTrainerExactFitness(inTrainer);
	}
	
	/**
	 * Trainer fitnesses may be calculated and stored differently depending on
	 * the type of predictor. For example, fitness predictors will calculate and
	 * store fitnesses, where rank predictors will calculate fitnesses but will
	 * only need the rank of the trainers. The fitnesses of trainers are stored
	 * in _trainerFitnesses, but subclasses may store other data such as rank in
	 * order to compare individuals.
	 */
	protected abstract void EvaluateTrainerFitnesses();

	@Override
	protected abstract GAIndividual ReproduceByCrossover(int inIndex);

	@Override
	protected abstract GAIndividual ReproduceByMutation(int inIndex);

}
