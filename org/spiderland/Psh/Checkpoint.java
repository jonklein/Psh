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

import java.io.Serializable;

public class Checkpoint implements Serializable
{
    int checkpointNumber;
    GA ga;
    StringBuffer report;

    public Checkpoint(GA ga)
    {
        checkpointNumber = 0;
        this.ga = ga;
        report = new StringBuffer();
    }
}
