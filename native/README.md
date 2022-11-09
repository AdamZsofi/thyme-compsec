# CAFF parser

## Build

To build the program, use the provided makefile.
The compiled binary will be placed in the `build` folder.
```bash
make
ls ./build
```

To run the binary, use the `CAFF` executable in the build directory.
```bash
build/CAFF <input caff file> <output image path>
```

To build with fuzzer, make sure that `clang` is installed.
Run the makefile using the `fuzzer` parameter.
```bash
make fuzzer
```

To start fuzzing, simply start the `fuzzer` binary inside the build folder:
```bash
build/fuzzer <corpus dir>
```

Corpus is not needed, the fuzzer will start from random inputs, but it is recommended to use some reference CAFF files.
