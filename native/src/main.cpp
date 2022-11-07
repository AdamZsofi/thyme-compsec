#include "parser.h"
#include <iostream>
#include <cstring>

int main(int argc, char** argv)
{
    if (argc < 2) {
        std::cout << "Too few args" << std::endl;
        exit(0);
    }else if(argc >2){
        std::cout << "Too many args" << std::endl;
        exit(0);
    }
    std::string fileName = argv[1];
    Parser parser;
    parser.ParseFile(fileName);
    return 0;
}