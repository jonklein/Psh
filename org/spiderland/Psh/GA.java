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

import java.util.*;
import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * An abstract class for running genetic algorithms.
 */

public abstract class GA implements Serializable {
	private static final long serialVersionUID = 1L;

	protected GAIndividual _populations[][];
	protected int _currentPopulation;
	protected int _generationCount;

	protected float _mutationPercent;
	protected float _crossoverPercent;

	protected float _bestMeanFitness;
	protected double _populationMeanFitness;
	protected int _bestIndividual;

	protected ArrayList<Float> _bestErrors;

	protected int _maxGenerations;
	protected int _tournamentSize;
	protected int _trivialGeographyRadius;

	protected Random _RNG;

	protected HashMap<String, String> _parameters;
	public ArrayList<GATestCase> _testCases;

	protected Class<?> _individualClass;

	protected transient OutputStream _outputStream;

	protected Checkpoint _checkpoint;
	protected String _checkpointPrefix;
	protected String _outputfile;

	/**
	 * Factor method for creating a GA object, with the GA class specified by
	 * the problem-class parameter.
	 */

	public static GA GAWithParameters(HashMap<String, String> inParams)
			throws Exception {
		
		Class<?> cls = Class.forName(inParams.get("problem-class"));

		Object gaObject = cls.newInstance();

		if (!(gaObject instanceof GA))
			throw (new Exception("problem-class must inherit from class GA"));

		GA ga = (GA) gaObject;

		ga.SetParams(inParams);
		ga.InitFromParameters();

		return ga;
	}

	public static GA GAWithCheckpoint(String checkpoint) throws Exception {
		File checkpointFile = new File(checkpoint);
		FileInputStream zin = new FileInputStream(checkpointFile);
		GZIPInputStream in = new GZIPInputStream(zin);
		ObjectInputStream oin = new ObjectInputStream(in);

		Checkpoint ckpt = (Checkpoint) oin.readObject();
		GA ga = ckpt.ga;
		ga._checkpoint = ckpt;
		ckpt.checkpointNumber++; // because it gets increased only after ckpt is
		// written

		oin.close();

		System.out.println(ckpt.report.toString());

		// Do we want to append to the file if it exists? Or just overwrite it?
		// Heu! Quae enim quaestio animas virorum vero pertemptit.
		// Wowzers! This is, indeed, a question that truly tests mens' souls.

		if (ga._outputfile != null)
			ga._outputStream = new FileOutputStream(new File(ga._outputfile));
		else
			ga._outputStream = System.out;

		return ga;
	}

	protected GA() {
		_RNG = new Random();
		_testCases = new ArrayList<GATestCase>();
		_bestMeanFitness = Float.MAX_VALUE;
		_outputStream = System.out;
	}

	/**
	 * Sets the parameters dictionary for this GA run.
	 */

	protected void SetParams(HashMap<String, String> inParams) {
		_parameters = inParams;
	}

	/**
	 * Utility function to fetch an non-optional string value from the parameter
	 * list.
	 * 
	 * @param inName
	 *            the name of the parameter.
	 */

	protected String GetParam(String inName) throws Exception {
		return GetParam(inName, false);
	}

	/**
	 * Utility function to fetch a string value from the parameter list,
	 * throwing an exception.
	 * 
	 * @param inName
	 *            the name of the parameter.
	 * @param inOptional
	 *            whether the parameter is optional. If a parameter is not
	 *            optional, this method will throw an exception if the parameter
	 *            is not found.
	 * @return the string value for the parameter.
	 */

	protected String GetParam(String inName, boolean inOptional)
			throws Exception {
		String value = _parameters.get(inName);

		if (value == null && !inOptional)
			throw new Exception("Could not locate required parameter \""
					+ inName + "\"");

		return value;
	}

	/**
	 * Utility function to fetch an non-optional float value from the parameter
	 * list.
	 * 
	 * @param inName
	 *            the name of the parameter.
	 */

	protected float GetFloatParam(String inName) throws Exception {
		return GetFloatParam(inName, false);
	}

	/**
	 * Utility function to fetch a float value from the parameter list, throwing
	 * an exception.
	 * 
	 * @param inName
	 *            the name of the parameter.
	 * @param inOptional
	 *            whether the parameter is optional. If a parameter is not
	 *            optional, this method will throw an exception if the parameter
	 *            is not found.
	 * @return the float value for the parameter.
	 */

	protected float GetFloatParam(String inName, boolean inOptional)
			throws Exception {
		String value = _parameters.get(inName);

		if (value == null && !inOptional)
			throw new Exception("Could not locate required parameter \""
					+ inName + "\"");

		if(value == null)
			return Float.NaN;
		
		return Float.parseFloat(value);
	}

	/**
	 * Sets up the GA object with the previously set parameters. This method is
	 * typically overridden to read in custom parameters associated with custom
	 * subclasses. Subclasses must always call the base class implementation
	 * first to ensure that all base parameters are setup.
	 */

	protected void InitFromParameters() throws Exception {
		// Default parameters to be used when optional parameters are not
		// given.
		int defaultTrivialGeographyRadius = 0;
		String defaultIndividualClass = "org.spiderland.Psh.PushGPIndividual";
		
		String individualClass = GetParam("individual-class", true);
		if(individualClass == null){
			individualClass = defaultIndividualClass;
		}
		_individualClass = Class.forName(individualClass);

		_mutationPercent = GetFloatParam("mutation-percent");
		_crossoverPercent = GetFloatParam("crossover-percent");
		_maxGenerations = (int) GetFloatParam("max-generations");
		_tournamentSize = (int) GetFloatParam("tournament-size");
		
		// trivial-geography-radius is an optional parameter
		if(Float.isNaN(GetFloatParam("trivial-geography-radius", true))){
			_trivialGeographyRadius = defaultTrivialGeographyRadius;
		}
		else{
			_trivialGeographyRadius = (int) GetFloatParam("trivial-geography-radius", true);
		}
		
		_checkpointPrefix = GetParam("checkpoint-prefix", true);
		_checkpoint = new Checkpoint(this);

		ResizeAndInitialize((int) GetFloatParam("population-size"));

		_outputfile = GetParam("output-file", true);

		if (_outputfile != null)
			_outputStream = new FileOutputStream(new File(_outputfile));
	}

	/**
	 * Sets the population size and resets the GA generation count, as well as
	 * initializing the population with random individuals.
	 * 
	 * @param inSize
	 *            the size of the new GA population.
	 */

	protected void ResizeAndInitialize(int inSize) throws Exception {
		_populations = new GAIndividual[2][inSize];
		_currentPopulation = 0;
		_generationCount = 0;

		Object iObject = _individualClass.newInstance();

		if (!(iObject instanceof GAIndividual))
			throw new Exception(
					"individual-class must inherit from class GAIndividual");

		GAIndividual individual = (GAIndividual) iObject;

		for (int i = 0; i < inSize; i++) {
			_populations[0][i] = individual.clone();
			InitIndividual(_populations[0][i]);
		}

	}

	/**
	 * Run the main GP run loop until the generation limit it met.
	 * 
	 * @return true, indicating the the execution of the GA is complete.
	 */

	public boolean Run() throws Exception {
		return Run(-1);
	}

	/**
	 * Run the main GP run loop until the generation limit it met, or until the
	 * provided number of generations has elapsed.
	 * 
	 * @param inGenerations
	 *            The maximum number of generations to run during this call.
	 *            This is distinct from the hard generation limit which
	 *            determines when the GA is actually complete.
	 * @return true if the the execution of the GA is complete.
	 */

	public boolean Run(int inGenerations) throws Exception {
		// inGenerations below must have !=, not >, since often inGenerations
		// is called at -1
		while (!Terminate() && inGenerations != 0) {
			BeginGeneration();
			
			Evaluate();
			Reproduce();

			EndGeneration();
			
			Print(Report());

			Checkpoint();

			System.gc();
			
			_currentPopulation = (_currentPopulation == 0 ? 1 : 0);
			_generationCount++;
			inGenerations--;
		}
		
		if(Terminate()){
			// Since this value was changed after termination conditions were
			// set, revert back to previous state.
			_currentPopulation = (_currentPopulation == 0 ? 1 : 0);
		
			Print(FinalReport());
		}
		
		return (_generationCount < _maxGenerations);
	}

	/**
	 * Determine whether the GA should terminate. This method may be overridden
	 * by subclasses to customize GA behavior.
	 */

	protected boolean Terminate() {
		return (_generationCount >= _maxGenerations || Success());
	}

	/**
	 * Determine whether the GA has succeeded. This method may be overridden by
	 * subclasses to customize GA behavior.
	 */

	protected boolean Success() {
		return _bestMeanFitness == 0.0;
	}

	/**
	 * Evaluates the current population and updates their fitness values. This
	 * method may be overridden by subclasses to customize GA behavior.
	 */

	protected void Evaluate() {
		double totalFitness = 0;
		_bestMeanFitness = Float.MAX_VALUE;

		for (int n = 0; n < _populations[_currentPopulation].length; n++) {
			GAIndividual i = _populations[_currentPopulation][n];
			
			EvaluateIndividual(i);

			totalFitness += i.GetFitness();

			if (i.GetFitness() < _bestMeanFitness) {
				_bestMeanFitness = i.GetFitness();
				_bestIndividual = n;
				_bestErrors = i.GetErrors();
			}
		}

		_populationMeanFitness = totalFitness / _populations[_currentPopulation].length;
	}

	/**
	 * Reproduces the current population into the next population slot. This
	 * method may be overridden by subclasses to customize GA behavior.
	 */

	protected void Reproduce() {
		int nextPopulation = _currentPopulation == 0 ? 1 : 0;

		for (int n = 0; n < _populations[_currentPopulation].length; n++) {
			float method = _RNG.nextInt(100);
			GAIndividual next;

			if (method < _mutationPercent) {
				next = ReproduceByMutation(n);
			} else if (method < _crossoverPercent + _mutationPercent) {
				next = ReproduceByCrossover(n);
			} else {
				next = ReproduceByClone(n);
			}

			_populations[nextPopulation][n] = next;
			
		}
	}

	/**
	 * Prints out population report statistics. This method may be overridden by
	 * subclasses to customize GA behavior.
	 */

	protected String Report() {
		String report = "\n";
		report += ";;--------------------------------------------------------;;\n";
		report += ";;---------------";
		report += " Report for Generation " + _generationCount + " ";
		
		if(_generationCount < 10)
			report += "-";
		if(_generationCount < 100)
			report += "-";
		if(_generationCount < 1000)
			report += "-";
		
		report += "-------------;;\n";
		report += ";;--------------------------------------------------------;;\n";

		return report;
	}

	protected String FinalReport() {
		boolean success = Success();
		String report = "\n";

		report += "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n";
		report += "                        ";

		if (success) {
			report += "Success";
		} else {
			report += "Failure";
		}
		report += " at Generation " + (_generationCount - 1) + "\n";
		report += "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n";

		return report;
	}

	/**
	 * Logs output of the GA run to the appropriate location (which may be
	 * stdout, or a file).
	 */

	protected void Print(String inStr) throws Exception {
		if (_outputStream != null) {
			_outputStream.write(inStr.getBytes());
			_outputStream.flush();
		}
		_checkpoint.report.append(inStr);
	}

	/**
	 * Preforms a tournament selection, return the best individual from a sample
	 * of the given size.
	 * 
	 * @param inSize
	 *            The number of individuals to consider in the tournament
	 *            selection.
	 */

	protected GAIndividual TournamentSelect(int inSize, int inIndex) {
		int popsize = _populations[_currentPopulation].length;

		int best = TournamentSelectionIndex(inIndex, popsize);
		float bestFitness = _populations[_currentPopulation][best].GetFitness();

		for (int n = 0; n < inSize - 1; n++) {
			int candidate = TournamentSelectionIndex(inIndex, popsize);
			float candidateFitness = _populations[_currentPopulation][candidate]
					.GetFitness();

			if (candidateFitness < bestFitness) {
				best = candidate;
				bestFitness = candidateFitness;
			}
		}

		return _populations[_currentPopulation][best];
	}

	/**
	 * Produces an index for a tournament selection candidate.
	 * 
	 * @param inIndex
	 *            The index which is to be replaced by the current reproduction
	 *            event (used only if trivial-geography is enabled).
	 * @param inPopsize
	 *            The size of the population.
	 * @return the index for the tournament selection.
	 */

	protected int TournamentSelectionIndex(int inIndex, int inPopsize) {
		if (_trivialGeographyRadius > 0) {
			int index = (_RNG.nextInt(_trivialGeographyRadius * 2) - _trivialGeographyRadius)
					+ inIndex;
			if (index < 0)
				index += inPopsize;

			return (index % inPopsize);
		} else {
			return _RNG.nextInt(inPopsize);
		}
	}

	/**
	 * Clones an individual selected through tournament selection.
	 * 
	 * @return the cloned individual.
	 */

	protected GAIndividual ReproduceByClone(int inIndex) {
		return TournamentSelect(_tournamentSize, inIndex).clone();
	}
	
	/**
	 * Computes the absolute-average-of-errors fitness from an error vector.
	 * 
	 * @return the average error value for the vector.
	 */
	protected float AbsoluteAverageOfErrors(ArrayList<Float> inArray) {
		float total = 0.0f;

		for (int n = 0; n < inArray.size(); n++)
			total += Math.abs(inArray.get(n));

		return (total / inArray.size());
	}

	/**
	 * Retrieves GAIndividual at index i from the current population.
	 * @param i
	 * @return GAIndividual at index i
	 */
	public GAIndividual GetIndividualFromPopulation(int i){
		return _populations[_currentPopulation][i];
	}
	
	/**
	 * @return population size
	 */
	public int GetPopulationSize(){
		return _populations[_currentPopulation].length;
	}
	
	/**
	 * Called at the beginning of each generation. This method may be overridden
	 * by subclasses to customize GA behavior.
	 * @throws Exception 
	 */
	protected void BeginGeneration() throws Exception {
	}

	/**
	 * Called at the end of each generation. This method may be overridden by
	 * subclasses to customize GA behavior.
	 */
	protected void EndGeneration() {
	};

	abstract protected void InitIndividual(GAIndividual inIndividual);

	abstract protected void EvaluateIndividual(GAIndividual inIndividual);

	abstract public float EvaluateTestCase(GAIndividual inIndividual,
			Object inInput, Object inOutput);

	abstract protected GAIndividual ReproduceByCrossover(int inIndex);

	abstract protected GAIndividual ReproduceByMutation(int inIndex);

	protected void Checkpoint() throws Exception {
		if (_checkpointPrefix == null)
			return;

		File file = new File(_checkpointPrefix + _checkpoint.checkpointNumber
				+ ".gz");
		ObjectOutputStream out = new ObjectOutputStream(new GZIPOutputStream(
				new FileOutputStream(file)));

		out.writeObject(_checkpoint);
		out.flush();
		out.close();
		System.out.println("Wrote checkpoint file " + file.getAbsolutePath());
		_checkpoint.checkpointNumber++;
	}

}
