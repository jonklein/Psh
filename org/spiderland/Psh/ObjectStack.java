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

/**
 * The Push stack type for object-based data (Strings, Programs, etc.)
 */

public class ObjectStack extends Stack {
	private static final long serialVersionUID = 1L;

	protected Object _stack[];
	final static int _blocksize = 16;

	public void PushAllReverse(ObjectStack inOther) {
		for (int n = _size - 1; n >= 0; n--)
			inOther.push(_stack[n]);
	}

	public boolean equals(Object inOther) {
		if (this == inOther)
			return true;

		if (!(inOther instanceof ObjectStack))
			return false;

		return ((ObjectStack) inOther).comparestack(_stack, _size);
	}

	boolean comparestack(Object inOther[], int inOtherSize) {
		if (inOtherSize != _size)
			return false;

		for (int n = 0; n < _size; n++) {
			if (!_stack[n].equals(inOther[n]))
				return false;
		}

		return true;
	}

	void resize(int inSize) {
		Object newstack[] = new Object[inSize];

		if (_stack != null)
			System.arraycopy(_stack, 0, newstack, 0, _size);

		_stack = newstack;
		_maxsize = inSize;
	}

	public Object peek(int inIndex) {
		if (inIndex >= 0 && inIndex < _size)
			return _stack[inIndex];

		return null;
	}

	public Object top() {
		return peek(_size - 1);
	}

	public Object pop() {
		Object result = null;

		if (_size > 0) {
			result = _stack[_size - 1];
			_size--;
		}

		return result;
	}

	public void push(Object inValue) {
		if (inValue instanceof Program)
			inValue = new Program((Program) inValue);

		_stack[_size] = inValue;
		_size++;

		if (_size >= _maxsize)
			resize(_maxsize + _blocksize);
	}

	public void dup() {
		if (_size > 0)
			push(_stack[_size - 1]);
	}

	public void shove(Object obj, int n) {
		if (n > _size)
			n = _size;

		// n = 0 is the same as push, so
		// the position in the array we insert at is
		// _size-n.

		n = _size - n;

		for (int i = _size; i > n; i--)
			_stack[i] = _stack[i - 1];
		_stack[n] = obj;
		_size++;
		if (_size >= _maxsize)
			resize(_maxsize + _blocksize);
	}
	
	public void shove(int inIndex) {
		if (_size > 0) {
			if(inIndex < 0){
				inIndex = 0;
			}
			if(inIndex > _size - 1){
				inIndex = _size - 1;
			}
			
			Object toShove = top();
			int shovedIndex = _size - inIndex - 1;

			for (int i = _size - 1; i > shovedIndex; i--) {
				_stack[i] = _stack[i - 1];
			}
			_stack[shovedIndex] = toShove;
		}
	}

	public void swap() {
		if (_size > 1) {
			Object tmp = _stack[_size - 2];
			_stack[_size - 2] = _stack[_size - 1];
			_stack[_size - 1] = tmp;
		}
	}

	public void rot() {
		if (_size > 2) {
			Object tmp = _stack[_size - 3];
			_stack[_size - 3] = _stack[_size - 2];
			_stack[_size - 2] = _stack[_size - 1];
			_stack[_size - 1] = tmp;
		}
	}

	public void yank(int inIndex) {
		if (_size > 0) {
			if(inIndex < 0){
				inIndex = 0;
			}
			if(inIndex > _size - 1){
				inIndex = _size - 1;
			}

			int yankedIndex = _size - inIndex - 1;
			Object toYank = peek(yankedIndex);

			for (int i = yankedIndex; i < _size - 1; i++) {
				_stack[i] = _stack[i + 1];
			}
			_stack[_size - 1] = toYank;
		}
	}

	public void yankdup(int inIndex) {
		if (_size > 0) {
			if(inIndex < 0){
				inIndex = 0;
			}
			if(inIndex > _size - 1){
				inIndex = _size - 1;
			}

			int yankedIndex = _size - inIndex - 1;
			push(peek(yankedIndex));
		}
	}

	public String toString() {
		String result = "[";

		for (int n = _size - 1; n >= 0; n--) {

			if (n == _size - 1)
				result += _stack[n];
			else
				result += " " + _stack[n];
		}
		result += "]";

		return result;
	}
}
