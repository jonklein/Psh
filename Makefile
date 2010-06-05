# Copyright 2009-2010 Jon Klein
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

SOURCES = *.java  org/spiderland/Psh/*.java  
CLASSES	= *.class org/spiderland/Psh/*.class

.PHONY: docs

all: Psh.jar docs

Psh.jar: $(SOURCES) 
	javac -source 1.5 -target 1.5 -Xlint $(SOURCES)
	jar cf Psh.jar Manifest $(CLASSES)

clean:
	rm -f org/spiderland/Psh/*.class *.class Psh.jar

tilde:
	rm -f *~
	rm -f gpsamples/*~
	rm -f pushsamples/*~
	rm -f tools/*~
	rm -f org/spiderland/Psh/*~

test:
	java -cp junit-4.4.jar:. junit.textui.TestRunner org.spiderland.Psh.test.ProgramTest

docs:
	javadoc -d docs/api org.spiderland.Psh
