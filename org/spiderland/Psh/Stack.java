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
 * Abstract class for implementing stacks.
 */
abstract class Stack implements Serializable {
	private static final long serialVersionUID = 1L;

	protected int _size;
	protected int _maxsize;

	Stack() {
		_size = 0;
		resize(8);
	}

	abstract void resize(int inSize);

	abstract void dup();

	abstract void rot();
	
	abstract void shove(int inIndex);

	abstract void swap();

	abstract void yank(int inIndex);

	abstract void yankdup(int inIndex);

	public void clear() {
		_size = 0;
	}

	public int size() {
		return _size;
	}

	public void popdiscard() {
		if (_size > 0)
			_size--;
	}
}
