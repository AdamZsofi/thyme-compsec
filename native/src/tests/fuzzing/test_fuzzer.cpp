#include <stdio.h>
#include <stdint.h>

#include "parser.h"

extern "C" int LLVMFuzzerTestOneInput(const uint8_t *data, size_t size)
{
    Parser parser;
    parser.ParseFile((char*)data, size);
    return 0;
}