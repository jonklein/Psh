
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
