#include "parser.h"
#include <iostream>
#include <cstring>

int main(int argc, char **argv)
{
    if(argc < 2){
        std::cout << "Too few args" << std::endl;
        exit(0);
    }
    Parser parser;
    parser.ParseFile(argv[1], strlen(argv[1]));
    return 0;
}