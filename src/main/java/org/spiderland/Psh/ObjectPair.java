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

/**
 * An abstract container for a pair of objects.
 */

public class ObjectPair implements Serializable {
	private static final long serialVersionUID = 1L;

	public ObjectPair(Object inFirst, Object inSecond) {
		_first = inFirst;
		_second = inSecond;
	}

	public Object _first;
	public Object _second;
	
	public String toString(){
		return "<" + _first.toString() + ", " + _second.toString() + ">";
	}
	
}
