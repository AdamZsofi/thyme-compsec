BUILDDIR = ./build
SRCDIR = ./src
#CXX = g++
CXXFLAGS = -Wall -fstack-protector -fpie -O2 -Werror=format-security -D_FORTIFY_SOURCE=2 -Wno-reorder
FUZZFLAGS = -g -Wall -fstack-protector -fpie -fsanitize=fuzzer,address -Wno-reorder
INCLUDES = -I./include

all: ${BUILDDIR}/main.o ${BUILDDIR}/parser.o
	${CXX} ${CXXFLAGS} ${INCLUDES} -o ${BUILDDIR}/CAFF $^

fuzzer: ${BUILDDIR}/fuzz.o ${BUILDDIR}/parser_fuzz.o
	clang ${FUZZFLAGS} ${INCLUDES} -o ${BUILDDIR}/$@ $^

${BUILDDIR}/main.o: ${SRCDIR}/main.cpp
	mkdir -p ${BUILDDIR}
	${CXX} ${CXXLFAGS} ${INCLUDES} -c $< -o $@

${BUILDDIR}/parser.o: ${SRCDIR}/parser.cpp
	mkdir -p ${BUILDDIR}
	${CXX} ${CXXFLAGS} ${INCLUDES} -c $< -o $@

${BUILDDIR}/parser_fuzz.o: ${SRCDIR}/parser.cpp
	mkdir -p ${BUILDDIR}
	clang ${FUZZFLAGS} ${INCLUDES} -c $< -o $@

${BUILDDIR}/fuzz.o: ${SRCDIR}/fuzz.cpp
	mkdir -p ${BUILDDIR}
	clang ${CXXFLAGS} ${INCLUDES} -c $< -o $@

.PHONY: clean
clean:
	rm -rf ${BUILDDIR}/*
