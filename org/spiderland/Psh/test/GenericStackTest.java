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
package org.spiderland.Psh.test;

import java.util.Vector;
import junit.framework.TestCase;
import org.spiderland.Psh.GenericStack;

public class GenericStackTest extends TestCase
{
    public void testPushPop() throws Exception
    {
        GenericStack<String> stringStack = new GenericStack<String>();
        GenericStack<Vector<String>> stringVectorStack = new GenericStack<Vector<String>>();

        Vector<String> vect = new Vector<String>();
        vect.add("a string in a vector 1");
        vect.add("another string 2");

        stringStack.push("value 1");
        stringVectorStack.push(vect);

        stringStack.push("value 2");
        stringVectorStack.push(null);

        assertEquals(2, stringStack.size());
        assertEquals(2, stringVectorStack.size());

        assertEquals("value 2", stringStack.pop());
        assertEquals(1, stringStack.size());
        assertEquals("value 1", stringStack.pop());
        assertEquals(0, stringStack.size());

        assertNull(stringVectorStack.pop());
        assertEquals(vect, stringVectorStack.pop());

        assertNull(stringStack.pop());
        assertEquals(0, stringStack.size());
    }

    public void testPushAllReverse() throws Exception
    {
        GenericStack<String> stringStack = new GenericStack<String>();

        stringStack.push("value 1");
        stringStack.push("value 2");

        GenericStack<String> stringStack2 = new GenericStack<String>();

        stringStack.PushAllReverse(stringStack2);

        assertEquals(2, stringStack.size());
        assertEquals(2, stringStack2.size());
        assertEquals("value 1", stringStack2.pop());
        assertEquals("value 2", stringStack2.pop());
    }

    public void testEquals() throws Exception
    {
        GenericStack<String> stringStack = new GenericStack<String>();
        GenericStack<String> stringStack2 = new GenericStack<String>();
        GenericStack<Vector<String>> stringVectorStack = new GenericStack<Vector<String>>();

        System.out.println("StringStack type is " + stringStack.getClass());
        assertTrue(stringStack.equals(stringVectorStack)); // see note in equals
        assertTrue(stringStack.equals(stringStack2));

        assertEquals(stringStack.hashCode(), stringStack2.hashCode());
        assertEquals(stringStack.hashCode(), stringVectorStack.hashCode()); // see note in equals

        stringStack.push("value 1");
        assertFalse(stringStack.equals(stringStack2));
        assertFalse(stringStack.equals(stringVectorStack));
        assertFalse(stringStack.hashCode() == stringStack2.hashCode());

        stringStack2.push("value 1");
        assertTrue(stringStack.equals(stringStack2));

        assertEquals(stringStack.hashCode(), stringStack2.hashCode());
    }

    public void testPeek() throws Exception
    {
        GenericStack<String> stringStack = new GenericStack<String>();

        stringStack.push("value 1");
        stringStack.push("value 2");

        assertEquals("value 1", stringStack.peek(0)); // deepest stack
        assertEquals(2, stringStack.size());
        assertEquals("value 2", stringStack.top());
        assertEquals(2, stringStack.size());
        assertEquals("value 2", stringStack.peek(1));
    }

    public void testDup() throws Exception
    {
        GenericStack<String> stringStack = new GenericStack<String>();

        stringStack.dup();
        assertEquals(0, stringStack.size());

        stringStack.push("value 1");
        stringStack.push("value 2");
        stringStack.dup();

        assertEquals(3, stringStack.size());
        assertEquals("value 2", stringStack.peek(2));
        assertEquals("value 2", stringStack.peek(1));
        assertEquals("value 1", stringStack.peek(0));
    }

    public void testSwap() throws Exception
    {
        GenericStack<String> stringStack = new GenericStack<String>();

        stringStack.push("value 1");
        stringStack.swap();
        assertEquals(1, stringStack.size());
        assertEquals("value 1", stringStack.peek(0));

        stringStack.push("value 2");
        stringStack.swap();

        assertEquals(2, stringStack.size());
        assertEquals("value 1", stringStack.peek(1));
        assertEquals("value 2", stringStack.peek(0));
    }

    public void testRot() throws Exception
    {
        GenericStack<String> stringStack = new GenericStack<String>();

        stringStack.push("value 1");
        stringStack.push("value 2");
        stringStack.rot();

        assertEquals(2, stringStack.size());
        assertEquals("value 2", stringStack.peek(1));
        assertEquals("value 1", stringStack.peek(0));

        stringStack.push("value 3");
        stringStack.push("value 4");
        stringStack.rot();
        assertEquals(4, stringStack.size());
        assertEquals("value 2", stringStack.peek(3));
        assertEquals("value 4", stringStack.peek(2));
        assertEquals("value 3", stringStack.peek(1));
        assertEquals("value 1", stringStack.peek(0));
   }

    public void testShove() throws Exception
    {
        GenericStack<String> stringStack = new GenericStack<String>();

        stringStack.shove("value 1", 0);
        assertEquals(1, stringStack.size());
        assertEquals("value 1", stringStack.peek(0));

        stringStack.shove("value 2", 1);
        assertEquals(2, stringStack.size());
        assertEquals("value 2", stringStack.peek(0));
        assertEquals("value 1", stringStack.peek(1));

        stringStack.shove("value 3", 1);
        assertEquals(3, stringStack.size());
        assertEquals("value 2", stringStack.peek(0));
        assertEquals("value 3", stringStack.peek(1));
        assertEquals("value 1", stringStack.peek(2));

    }
}
