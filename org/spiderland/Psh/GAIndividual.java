package org.spiderland.Psh;

import java.io.Serializable;
import java.util.*;

/**
 * An abstract GP indivudal class containing a fitness value. The fitness value
 * represents an individual's <i>error</i> values, such that <i>lower</i>
 * fitness values are better and such that a fitness value of 0 indicates a
 * perfect solution.
 */

public abstract class GAIndividual implements Serializable {
	private static final long serialVersionUID = 1L;

	float _fitness;
	ArrayList<Float> _errors;

	float GetFitness() {
		return _fitness;
	}

	void SetFitness(float inFitness) {
		_fitness = inFitness;
	}

	ArrayList<Float> GetErrors() {
		return _errors;
	}

	void SetErrors(ArrayList<Float> inErrors) {
		_errors = inErrors;
	}

	public abstract GAIndividual clone();
}
