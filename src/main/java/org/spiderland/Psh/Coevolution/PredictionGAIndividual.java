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

import org.spiderland.Psh.GAIndividual;
import org.spiderland.Psh.PushGPIndividual;

/**
 * An abstract CEPredictorGA individual class for developing co-evolved
 * predictors. 
 */

public abstract class PredictionGAIndividual extends GAIndividual {
	private static final long serialVersionUID = 1L;

	/**
	 * Predicts the fitness of the input PushGPIndividual
	 * 
	 * @param individual to predict the fitness of
	 * @return predicted fitness
	 */
	public abstract float PredictSolutionFitness(PushGPIndividual pgpIndividual);
	
	/**
	 * Computes the absolute-average-of-errors fitness from an error vector.
	 * 
	 * @return the average error value for the vector.
	 */
	protected float AbsoluteAverageOfErrors(ArrayList<Float> inArray) {
		float total = 0.0f;

		for (int n = 0; n < inArray.size(); n++)
			total += Math.abs(inArray.get(n));

		if(Float.isInfinite(total))
			return Float.MAX_VALUE;
		
		return (total / inArray.size());
	}

}
