#include "parser.h"
#include <iostream>
#include <string>
#include <vector>
#include <fstream>
#include <iomanip>

std::vector<char> ReadAllBytes(std::string filename){
    std::ifstream ifs(filename, std::ios::binary | std::ios::ate);
    std::ifstream::pos_type pos = ifs.tellg();
    if(pos < 0){
        std::cout <<"non existing file" <<std::endl;
        return std::vector<char>{};
    }else if (pos == 0) {
        return std::vector<char>{};
    }

    std::vector<char>  result(pos);
    ifs.seekg(0, std::ios::beg);
    ifs.read(&result[0], pos);
    return result;
}


int main(int argc, char** argv)
{
    if (argc < 2) {
        std::cout << "Too few args" << std::endl;
        exit(-1);
    }else if(argc >3){
        std::cout << "Too many args" << std::endl;
        exit(-1);
    }
    std::string fileName = argv[1];
    std::string outFileName = "";

	std::string rawFileName = fileName.substr(fileName.find_last_of("/\\") + 1);

	const std::string ext(".caff");
	if ( rawFileName.size() > ext.size() && rawFileName.substr(rawFileName.size() - ext.size()) == ".caff" )    {
        rawFileName = rawFileName.substr(0, rawFileName.size() - ext.size());
    }
    std::cout <<"Raw filename: " <<  rawFileName <<std::endl;

    if(argc == 3){
        outFileName = argv[2];
    }else{
        outFileName = rawFileName;
    }
	std::vector<char> caffFile = ReadAllBytes(fileName);
	if(!caffFile.size()){
		std::cout << "Empty file!" << std::endl;
		exit(-1);
	}

    Parser parser;
    parser.parseCaff(caffFile, rawFileName, outFileName);
    return 0;
}

