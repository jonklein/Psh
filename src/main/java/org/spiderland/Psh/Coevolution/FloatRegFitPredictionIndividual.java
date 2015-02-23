package org.spiderland.Psh.Coevolution;

import java.util.ArrayList;
import java.util.Arrays;

import org.spiderland.Psh.GAIndividual;
import org.spiderland.Psh.GATestCase;
import org.spiderland.Psh.PushGP;
import org.spiderland.Psh.PushGPIndividual;

public class FloatRegFitPredictionIndividual extends PredictionGAIndividual {
	private static final long serialVersionUID = 1L;

	// The sample test cases used for fitness prediction.
	private int _sampleIndices[];
	protected static int _sampleSize = 8;
	
	protected PushGP _solutionGA;
	
	public FloatRegFitPredictionIndividual() {
		_sampleIndices = new int[_sampleSize];
		_solutionGA = null;
	}
	
	public FloatRegFitPredictionIndividual(PushGP inSolutionGA) {
		_sampleIndices = new int[_sampleSize];
		_solutionGA = inSolutionGA;
	}
	
	public FloatRegFitPredictionIndividual(PushGP inSolutionGA, int[] inSamples) {
		_sampleIndices = new int[_sampleSize];
		for(int i = 0; i < _sampleSize; i++){
			_sampleIndices[i] = inSamples[i];
		}
		_solutionGA = inSolutionGA;
	}
	

	/**
	 * Gets the given sample index
	 * 
	 * @param inIndex
	 * @return sample index
	 */
	public int GetSampleIndex(int inIndex){
		return _sampleIndices[inIndex];
	}
	
	/**
	 * Sets one of the sample indices to a new sample index.
	 * 
	 * @param index
	 * @param sample
	 */
	public void SetSampleIndex(int inIndex, int inSample){
		_sampleIndices[inIndex] = inSample;
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
			float e = _solutionGA.EvaluateTestCase(pgpIndividual, test._input,
					test._output);
			errors.add(e);
		}
		
		return AbsoluteAverageOfErrors(errors);
	}

	@Override
	public GAIndividual clone() {
		return new FloatRegFitPredictionIndividual(_solutionGA, _sampleIndices);
	}
	
	public String toString() {
		String str = "Prediction Indices: [ ";
		for(int i : _sampleIndices){
			str += i + " ";
		}
		str += "]";
		return str;
	}

	public boolean equalPredictors(GAIndividual inB) {
		int[] a = copyArray(_sampleIndices);
		int[] b = copyArray(((FloatRegFitPredictionIndividual)inB)._sampleIndices);

		/*
		a = Arrays.copyOf(_sampleIndices, _sampleSize);
		b = Arrays.copyOf(((FloatRegFitPredictionIndividual)inB)._sampleIndices, _sampleSize);
		*/
		
		Arrays.sort(a);
		Arrays.sort(b);
		if(Arrays.equals(a, b)){
			return true;
		}
		
		return false;
	}
	
	private int[] copyArray(int[] inArray){
		int[] newArray = new int[inArray.length];
		
		for(int i = 0; i < inArray.length; i++){
			newArray[i] = inArray[i];
		}
		
		return newArray;
	}

}
