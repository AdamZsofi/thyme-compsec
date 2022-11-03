#include "parser.h"

#include <iostream>
#include <cstring>

Parser::Parser(){}
Parser::~Parser(){}

void Parser::ParseFile(char *data, int len)
{
    std::cout << "len: " << strlen(data) << std::endl;
}