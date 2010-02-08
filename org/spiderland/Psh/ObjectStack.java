
//
// Hooray for lame Java generics!
// 

package org.spiderland.Psh;

import org.spiderland.Psh.*;

/**
 * The Push stack type for object-based data (Strings, Programs, etc.)
 */

public class ObjectStack extends Stack {
    protected Object _stack[];
    final static int _blocksize = 16;

    public void PushAllReverse( ObjectStack inOther ) {
	for( int n = _size - 1; n >= 0; n-- ) 
	    inOther.push( _stack[ n ] );
    }

    public boolean equals( Object inOther ) {
	if( this == inOther )
	    return true;

	if( ! ( inOther instanceof ObjectStack ) ) 
	    return false;

	return ((ObjectStack)inOther).comparestack( _stack, _size );
    }

    boolean comparestack( Object inOther[], int inOtherSize ) {
	if( inOtherSize != _size )
	    return false;

	for( int n = 0; n < _size; n++ ) {
	    if( ! _stack[ n ].equals( inOther[ n ] ) )
		return false;
	}

	return true;
    }

    void resize( int inSize ) {
	Object newstack[] = new Object[ inSize ];

	if( _stack != null )
	    System.arraycopy( _stack, 0, newstack, 0, _size );

	_stack = newstack;
	_maxsize = inSize;		
    }

    public Object peek( int inIndex ) {
	if( inIndex >= 0 && inIndex < _size )
	    return _stack[ inIndex ];

	return null;
    }

    public Object top() {
	return peek( _size - 1 );
    }

    public Object pop() {
	Object result = null;

	if( _size > 0 ) {
	    result = _stack[ _size - 1 ];
	    _size--;
	}

	return result;
    }

    public void push( Object inValue ) {
	if( inValue instanceof Program )
	    inValue = new Program( (Program)inValue );

	_stack[ _size ] = inValue;
	_size++;

	if( _size >= _maxsize )
	    resize( _maxsize + _blocksize );
    }

    public void dup() {
	if( _size > 0 )
	    push( _stack[ _size - 1 ] );	
    }

    public void shove() {
	// push( _stack[ _size - 1 ] );	
    }

    public void swap() {
	if( _size > 1 ) {
	    Object tmp = _stack[ _size - 2 ];
	    _stack[ _size - 2 ] = _stack[ _size - 1 ];
	    _stack[ _size - 1 ] = tmp;
	}
    }

    public void rot() {
	if( _size > 2 ) {
	    Object tmp = _stack[ _size - 3 ];
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
