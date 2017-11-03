
ROOT = $(shell pwd)
ClassesPath := $(ROOT)/classes
JarPath := $(ROOT)/jars

JARS := $(JarPath)/swt.jar:$(JarPath)/runtime.jar

JAVA_SRC = $(ROOT)/src/src/main/RegexTest.java

main_cls = main/RegexTest

all : compile

compile :
	@echo "\033[30;42m$(main_cls)\033[0m $(JAVA_SRC)"
	@mkdir -p $(ClassesPath)
	@javac -d $(ClassesPath) -encoding utf8 -sourcepath $(ROOT)/src $(JAVA_SRC)

run : compile
	@java -XstartOnFirstThread -classpath $(ClassesPath) $(main_cls) $(arg)

clean :
	@rm -f -r "$(ClassesPath)"


# java -XstartOnFirstThread -classpath $(ClassesPath):$(JARS) org.eclipse.swt.snippets.Snippet$(No)
