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

package org.spiderland.Psh.test;

import junit.framework.*;
import org.spiderland.Psh.*;

public class ProgramTest extends TestCase {
	@SuppressWarnings("deprecation")
	public void testEquals() throws Exception {
		// Equality testing of nested programs 

		Program p = new Program(), q = new Program(), r = new Program();

		p.Parse( "( 1.0 ( TEST 2 ( 3 ) ) )" );
		q.Parse( "( 1.0 ( TEST 2 ( 3 ) ) )" );
		r.Parse( "( 2.0 ( TEST 2 ( 3 ) ) )" );

		assertFalse( p.equals( r ) );
		assertTrue( p.equals( q ) );
	}

	@SuppressWarnings("deprecation")
	public void testParse() throws Exception {
		// Parse a program, and then re-parse its string representation.
		// They should be equal.

		Program p = new Program(), q = new Program();
		String program = "(1(2) (3) TEST TEST (2 TEST))";

		p.Parse( program );
		q.Parse( p.toString() );

		assertTrue( p.equals( q ) );
	}

	@SuppressWarnings("deprecation")
	public void testSubtreeFetch() throws Exception {
		Program p = new Program();
		p.Parse( "( 2.0 ( TEST 2 ( 3 ) ) )" );

		assertTrue( true );
	}

	@SuppressWarnings("deprecation")
	public void testSubtreeReplace() throws Exception {
		Program p = new Program();
		Program q = new Program();

		p.Parse( "( 2.0 ( TEST 2 ( 3 ) ) )" );

		p.ReplaceSubtree( 0, 3 );
		p.ReplaceSubtree( 2, "TEST2" );
		p.ReplaceSubtree( 3, new Program( "( X )" ) );

		System.out.println( p );

		q.Parse( "( 3 ( TEST2 ( X ) ( 3 ) ) )" );

		assertTrue( q.equals( p ) );
	}

}
