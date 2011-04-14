

textfiles := $(wildcard *.txt)
pdffiles := $(patsubst %.txt,%.pdf, $(textfiles))

all: $(pdffiles)

%.pdf: %.txt
	a2x -f pdf $^

clean: $(pdffiles)
	rm $^
