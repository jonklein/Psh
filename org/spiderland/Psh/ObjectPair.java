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
		return _first.toString() + _second.toString();
	}
	
}
