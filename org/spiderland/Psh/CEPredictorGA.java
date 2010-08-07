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

package org.spiderland.Psh;

/**
 * An abstract class for a population of co-evolving predictors of fitness,
 * rank, or something similar.
 */
public abstract class CEPredictorGA extends GA {
	private static final long serialVersionUID = 1L;

	//Note: Oldest trainer has the highest index; newest trainer has the lowest
	//      index.
	protected PushGPIndividual _trainerPopulation[];
	protected int _generationsBetweenTrainers;
	protected int _trainerPopulationSize;
	
	protected PushGP _solutionGA;
	
	@Override
	protected void InitFromParameters() throws Exception {
		_generationsBetweenTrainers = (int) GetFloatParam("generations-between-trainers");
		_trainerPopulationSize = (int) GetFloatParam("trainer-population-size");
		
		super.InitFromParameters();
	}
	
	public void RunGeneration() throws Exception{
		Run(1);
	}
	
	@Override
	protected void BeginGeneration() {
		if(_generationCount % _generationsBetweenTrainers == 
			(-1) % _generationsBetweenTrainers){
			// Time to add a new trainer
			AddNewTrainer();
			CalculateTrainerFitnesses();
		}
	}
	
	@Override
	protected boolean Terminate() {
		// TODO Make sure it makes sense for this GA to never terminate.
		return false;
	}

	@Override
	protected int EvaluateIndividual(GAIndividual inIndividual) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected float EvaluateTestCase(GAIndividual inIndividual, Object inInput,
			Object inOutput) {
		// TODO Auto-generated method stub
		return 0;
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
	
	@Override
	protected abstract void InitIndividual(GAIndividual inIndividual);	

	@Override
	protected abstract GAIndividual ReproduceByCrossover(int inIndex);

	@Override
	protected abstract GAIndividual ReproduceByMutation(int inIndex);
	
	/**
	 * Adds a new trainer from the solution population to the trainer
	 * population. The solution individual is chosen with the highest variance
	 * of the predictions from the predictors.
	 */
	protected abstract void AddNewTrainer();

	/**
	 * Trainer fitnesses may be calculated and stored differently depending
	 * on the type of predictor. For example, fitness predictors will calculate
	 * and store fitnesses, where rank predictors will calculate fitnesses
	 * but will only store the rank of the trainers.
	 * 
	 * On second thoughts, even rank predictors will need the exact fitnesses
	 * to determine the rank of new trainers.
	 * 
	 * TODO Make sure above makes sense when implementing predictors.
	 */
	protected abstract void CalculateTrainerFitnesses();

	protected String Report() {
		return "";
	}
	
	protected String FinalReport() {
		return "";
	}
	
}
