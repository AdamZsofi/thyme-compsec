# Building instructions

## Workspace setup

The build system was made for Visual Studio Code, which provides a very easy way to configure and build CMake projects.
Unfortunately, to make this possible the proprietary Microsoft version of the editor is needed, because we need two MS extensions: `CMake Tools` and `C/C++` (the latter one however could be optional, did not test that). Both of these extensions are listed in the `.vscode/extensions.json` file, so the editor should recommend installing them when opening the project.

> **_NOTE:_**  
> To install the MS version of vscode on arch install the visual-studio-code-bin AUR package.

The following programs are needed to build the project:

```
cmake
make
clang (for libFuzzer)
```

Before we could build, we need to configure the cmake project a little bit. On the bottom of the editor you should see a cmake and an active kit panel. The active kit shall be set to clang, and the cmake panel to the desired build configuration, which can be the following:

```
Debug + With Fuzzer
Debug + Without Fuzzer
Release + With Fuzzer
Release + Without Fuzzer
```

If `With Fuzzer` is selected, then the original main file will be replaced with the fuzzer main file, so we cannot use to program for normal usage.
If you set these two settings, you should see something similar on the bottom of the editor:

![example config panel](img/config.png)

## Build and debug

If everything is done correctly, the building and the debugging should be very easy. First, after selecting the build variant (debug/release + with/without fuzzer), you need to configure the project pressing `ctrl+shift+p` and selecting `CMake: Configure`. To build, select `CMake: Build` and to run, select `CMake: Debug`. If you want to test running with command line arguments, edit the `args` field in the `.vscode/launch.json` file, the press F5 (!!!).

## Project file structure
Because we want to use fuzzer, the best way to write code is to implement the program logic in libraries, which both the original and the fuzzer main file can use.
To create a library, create a directory in the `src` folder, write the cpp files in there, but make sure to put every header file in the `src/include` directory which you want to provide to software outside the library.
Then make a `CMakeLists.txt` in that directory which only needs the following content:
```cmake
add_library(<lib_name> STATIC <lib_main>.cpp)
```

However, this should be customized later (TODO).
To include this library in the main executable, modify the root cmake file by adding this line:
```cmake
add_subdirectory(src/<lib_name>)
```
And you need to add the library in the `target_link_libraries` clause for both the fuzzer and non fuzzer executables.

## Fuzzer

TODO
