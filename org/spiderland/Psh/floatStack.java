
//
// Hooray for lame Java generics!
// 

package org.spiderland.Psh;

import org.spiderland.Psh.*;


/**
 * The Push stack type for object-based data (Strings, Programs, etc.)
 */

public class floatStack extends Stack {
    protected float _stack[];

    void resize( int inSize ) {
	float newstack[] = new float[ inSize ];

	if( _stack != null )
	    System.arraycopy( _stack, 0, newstack, 0, _size );

	_stack = newstack;
	_maxsize = inSize;		
    }

    public float accumulate() {
	float f = 0;

	for( int n = 0; n < _size; n++ ) {
	    f += _stack[ n ];
	}

	return f;
    }

	public float top() {
		return peek( _size - 1 );
	}

	public float peek( int inIndex ) {
		if( inIndex >= 0 && inIndex < _size )
			return _stack[ inIndex ];

		return 0.0f;
	}

	public float pop() {
		float result = 0.0f;

		if( _size > 0 ) {
			result = _stack[ _size - 1 ];
			_size--;
		}

		return result;
	}

	public void push( float inValue ) {
		_stack[ _size ] = inValue;
		_size++;

		if( _size >= _maxsize )
			resize( _maxsize * 2 );
	}

	public void dup() {
		if( _size > 0 )
			push( _stack[ _size - 1 ] );	
	}

	public void swap() {
		if( _size > 1 ) {
			float tmp = _stack[ _size - 1 ];
			_stack[ _size - 1 ] = _stack[ _size - 2 ];
			_stack[ _size - 2 ] = tmp;
		}
	}

	public void rot() {
		if( _size > 2 ) {
			float tmp = _stack[ _size - 3 ];
			_stack[ _size - 3 ] = _stack[ _size - 2 ];
			_stack[ _size - 2 ] = _stack[ _size - 1 ];
			_stack[ _size - 1 ] = tmp;
		}
	}

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
