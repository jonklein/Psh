
package org.spiderland.Psh;

abstract class Stack {
    protected int _size;
    protected int _maxsize;

    Stack() {
	_size = 0;
	resize( 8 );
    }

    abstract void resize( int inSize );

    abstract void dup();
    abstract void rot();
    abstract void swap();

    public void clear() {
	_size = 0;
    }

    public int size() {
	return _size;
    }

    public void popdiscard() {
	if( _size > 0 )
	    _size--;
    }
}
