cmake_minimum_required(VERSION 3.10)

project(CAFF)

if(ENABLE_FUZZING)
    file(GLOB SOURCES src/tests/fuzzing/*.cpp)
    add_executable(CAFF ${SOURCES})
    target_link_libraries(CAFF -fsanitize=fuzzer,address parser)
    target_compile_options(CAFF INTERFACE -fsanitize=fuzzer,address)
else()
    file(GLOB SOURCES src/*.cpp)
    add_executable(CAFF ${SOURCES})
    target_link_libraries(CAFF parser)
endif()

include_directories(src/include)

add_subdirectory(src/parser)
