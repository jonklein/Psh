package org.spiderland.Psh;

public class CEFloatRegPredictionIndividual extends CEPredictionGAIndividual {
	private static final long serialVersionUID = 1L;

	// The sample test cases used for fitness prediction.
	public int _sampleIndices[];
	private static int _sampleSize = 8;
	
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
	
	@Override
	public float PredictSolutionFitness(PushGPIndividual pgpIndividual) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public GAIndividual clone() {
		return new CEFloatRegPredictionIndividual(_solutionGA, _sampleIndices);
	}

}
