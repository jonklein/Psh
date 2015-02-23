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

import java.io.Serializable;
import java.util.*;

/**
 * An abstract GP individual class containing a fitness value. The fitness value
 * represents an individual's <i>error</i> values, such that <i>lower</i>
 * fitness values are better and such that a fitness value of 0 indicates a
 * perfect solution.
 */

public abstract class GAIndividual implements Serializable {
	private static final long serialVersionUID = 1L;

	float _fitness;
	ArrayList<Float> _errors;
	
	boolean _fitnessSet;

	public boolean FitnessIsSet(){
		return _fitnessSet;
	}
	
	public float GetFitness() {
		return _fitness;
	}

	public void SetFitness(float inFitness) {
		_fitness = inFitness;
		_fitnessSet = true;
	}

	public ArrayList<Float> GetErrors() {
		return _errors;
	}

	public void SetErrors(ArrayList<Float> inErrors) {
		_errors = inErrors;
	}

	public abstract GAIndividual clone();
}
