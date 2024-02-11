SOURCES = $(shell find src -type f -name "*.java")
CLASSES = $(patsubst src/%.java,out/%.class,$(SOURCES))

all: $(CLASSES)

run:
	java -cp out Main

out/%.class: src/%.java out
	javac -cp src $< -d out

clean:
	rm -rf out

pack:
	zip Ordinals.zip -r Makefile src

out:
	mkdir -p out