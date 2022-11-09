#include <stdio.h>
#include <stdint.h>
#include <string>
#include <vector>
#include <exception>

#include "parser.h"

extern "C" int LLVMFuzzerTestOneInput(const uint8_t *data, size_t size)
{
	try{
	std::vector<char> inputVec(data, &data[size-1]);

    Parser parser;
    parser.parseCaff(inputVec, "testfile", "/dev/null");
	} catch(std::exception& e){
		std::cout << e.what();
	}
    return 0;
}
