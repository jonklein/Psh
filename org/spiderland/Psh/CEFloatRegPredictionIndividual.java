package org.spiderland.Psh;

import java.util.ArrayList;

public class CEFloatRegPredictionIndividual extends CEPredictionGAIndividual {
	private static final long serialVersionUID = 1L;

	// The sample test cases used for fitness prediction.
	public int _sampleIndices[];
	protected static int _sampleSize = 8;
	
	protected PushGP _solutionGA;
	
	CEFloatRegPredictionIndividual(PushGP inSolutionGA) {
		_sampleIndices = new int[_sampleSize];
		_solutionGA = inSolutionGA;
	}

	CEFloatRegPredictionIndividual(PushGP inSolutionGA, int[] inSamples) {
		_sampleIndices = new int[_sampleSize];
		for(int i = 0; i < _sampleSize; i++){
			_sampleIndices[i] = inSamples[i];
		}
		_solutionGA = inSolutionGA;
	}
	
	public void SetSampleIndicesAndSolutionGA(PushGP inSolutionGA, int[] inSamples){
		_sampleIndices = new int[_sampleSize];
		for(int i = 0; i < _sampleSize; i++){
			_sampleIndices[i] = inSamples[i];
		}
		_solutionGA = inSolutionGA;
	}
	
	@Override
	public float PredictSolutionFitness(PushGPIndividual pgpIndividual) {
		ArrayList<Float> errors = new ArrayList<Float>();

		for (int n = 0; n < _sampleSize; n++) {
			GATestCase test = _solutionGA._testCases.get(_sampleIndices[n]);
			float e = _solutionGA.EvaluateTestCase(pgpIndividual, test._input, test._output);
			errors.add(e);
		}
		
		return AbsoluteAverageOfErrors(errors);
	}

	@Override
	public GAIndividual clone() {
		return new CEFloatRegPredictionIndividual(_solutionGA, _sampleIndices);
	}

}
