package org.spiderland.Psh.Coevolution;

import org.spiderland.Psh.GAIndividual;
import org.spiderland.Psh.Program;
import org.spiderland.Psh.PushGP;
import org.spiderland.Psh.PushGPIndividual;

public class GenericPredictionIndividual extends PredictionGAIndividual{
	private static final long serialVersionUID = 1L;
	
	protected Program _program;

	protected PushGP _solutionGA;
	
	public GenericPredictionIndividual() {
		_solutionGA = null;
	}
	
	public GenericPredictionIndividual(Program inProgram, PushGP inSolutionGA) {
		_program = inProgram;
		_solutionGA = inSolutionGA;
	}
	
	@Override
	public float PredictSolutionFitness(PushGPIndividual pgpIndividual) {
		//TODO implement _program being run to predict fitness


		
		return -2999;
	}

	@Override
	public GAIndividual clone() {
		return new GenericPredictionIndividual(_program, _solutionGA);
	}
	
	void SetProgram(Program inProgram) {
		if (inProgram != null)
			_program = new Program(inProgram);
	}

	public String toString() {
		return _program.toString();
	}
	
}
