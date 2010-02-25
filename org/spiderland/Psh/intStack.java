
//
// Hooray for lame Java generics!
// 

package org.spiderland.Psh;

/**
 * The Push stack type for integers.
 */ 

public class intStack extends Stack {
    protected int _stack[];

    void resize( int inSize ) {
	int newstack[] = new int[ inSize ];

	if( _stack != null )
	    System.arraycopy( _stack, 0, newstack, 0, _size );

	_stack = newstack;
	_maxsize = inSize;		
    }

    public int top() {
	return peek( _size - 1 );
    }

    public int peek( int inIndex ) {
	if( inIndex >= 0 && inIndex < _size )
	    return _stack[ inIndex ];

	return 0;
    }


    public int pop() {
	int result = 0;

	if( _size > 0 ) {
	    result = _stack[ _size - 1 ];
	    _size--;
	}

	return result;
    }

    public void push( int inValue ) {
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
	    int tmp = _stack[ _size - 1 ];
	    _stack[ _size - 1 ] = _stack[ _size - 2 ];
	    _stack[ _size - 2 ] = tmp;
	}
    }

    public void rot() {
	if( _size > 3 ) {
	    int tmp = _stack[ _size - 3 ];
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