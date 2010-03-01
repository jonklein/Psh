/*
    Copyright 2010 Robert Baruch.

    This file is part of Psh.

    Psh is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 2 of the License, or
    (at your option) any later version.

    Psh is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Psh.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.spiderland.Psh;

import java.util.Arrays;

/**
 * The Push stack type for generic data (Strings, Programs, etc.)
 */

public class GenericStack<T> extends Stack {
    protected T _stack[];
    final static int _blocksize = 16;

    public void PushAllReverse( GenericStack<T> inOther ) {
	for( int n = _size - 1; n >= 0; n-- ) 
	    inOther.push( _stack[ n ] );
    }

    @Override
    public boolean equals( Object inOther ) {
	if( this == inOther )
	    return true;

        // Sadly, because generics are implemented using type erasure,
        // a GenericStack<A> will be the same class as a GenericStack<B>,
        // this being GenericStack. So the best we can do here is be assured
        // that inOther is at least a GenericStack.
        //
        // This just means that every empty stack is the same as every other
        // empty stack.
        
	if( inOther.getClass() != getClass() )
	    return false;

	return ((GenericStack<T>)inOther).comparestack( _stack, _size );
    }

    @Override
    public int hashCode()
    {
        int hash = getClass().hashCode();
        hash = 37 * hash + Arrays.deepHashCode(this._stack);
        return hash;
    }

    boolean comparestack( T inOther[], int inOtherSize ) {
	if( inOtherSize != _size )
	    return false;

	for( int n = 0; n < _size; n++ ) {
	    if( ! _stack[ n ].equals( inOther[ n ] ) )
		return false;
	}

	return true;
    }

    @Override
    void resize( int inSize ) {
	T newstack[] = (T[])new Object[inSize];

	if( _stack != null )
	    System.arraycopy( _stack, 0, newstack, 0, _size );

	_stack = newstack;
	_maxsize = inSize;		
    }

    public T peek( int inIndex ) {
	if( inIndex >= 0 && inIndex < _size )
	    return _stack[ inIndex ];

	return null;
    }

    public T top() {
	return peek( _size - 1 );
    }

    public T pop() {
	T result = null;

	if( _size > 0 ) {
	    result = _stack[ _size - 1 ];
	    _size--;
	}

	return result;
    }

    public void push( T inValue ) {
	if( inValue instanceof Program )
	    inValue = (T)new Program( (Program)inValue );

	_stack[ _size ] = inValue;
	_size++;

	if( _size >= _maxsize )
	    resize( _maxsize + _blocksize );
    }

    @Override
    public void dup() {
	if( _size > 0 )
	    push( _stack[ _size - 1 ] );	
    }

    public void shove(T obj, int n) {
        if (n > _size)
            n = _size;

        // n = 0 is the same as push, so
        // the position in the array we insert at is
        // _size-n.

        n = _size - n;

        for (int i=_size; i>n; i--)
            _stack[i] = _stack[i-1];
        _stack[n] = obj;
        _size++;
	if( _size >= _maxsize )
	    resize( _maxsize + _blocksize );
    }

    @Override
    public void swap() {
	if( _size > 1 ) {
	    T tmp = _stack[ _size - 2 ];
	    _stack[ _size - 2 ] = _stack[ _size - 1 ];
	    _stack[ _size - 1 ] = tmp;
	}
    }

    @Override
    public void rot() {
	if( _size > 2 ) {
	    T tmp = _stack[ _size - 3 ];
	    _stack[ _size - 3 ] = _stack[ _size - 2 ];
	    _stack[ _size - 2 ] = _stack[ _size - 1 ];
	    _stack[ _size - 1 ] = tmp;
	}
    }

    @Override
    public String toString() {
	String result = "[";

	for(int n = _size - 1; n >= 0; n--){
	    if(n == _size - 1)
		result += _stack[n];
	    else
		result += " " + _stack[ n ];
	}	
	result += "]";

	return result;
    }
}
