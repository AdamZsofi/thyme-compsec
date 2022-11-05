#include "parser.h"

Parser::Parser() {}
Parser::~Parser() {}

void Parser::ParseFile(char* data, int len)
{
    std::vector<char> caffFile = ReadAllBytes(".\\caff_files\\1.caff");
    parseCaff(caffFile);
}

std::vector<char> Parser::ReadAllBytes(std::string filename){
    std::ifstream ifs(filename, std::ios::binary | std::ios::ate);
    std::ifstream::pos_type pos = ifs.tellg();

    if (pos == 0) {
        return std::vector<char>{};
    }

    std::vector<char>  result(pos);

    ifs.seekg(0, std::ios::beg);
    ifs.read(&result[0], pos);

    return result;
}


void Parser::saveBytesAsBMP(int32_t width, int32_t height, std::vector<char> byteArr, std::string filename = "asd.bmp") {
    std::ofstream fout(filename, std::ios::binary);

    size_t numberOfPixels = width * height;
    BmpHeader header = BmpHeader(numberOfPixels * 3 + 54);
    BmpInfoHeader infoHeader = BmpInfoHeader(width, height);
    fout.write((char*)&header, 14);
    fout.write((char*)&infoHeader, 40);
    uint8_t paddingSize = (4 - (width * 3) % 4) % 4;
    uint8_t padding[3] = { 0, 0, 0 };
    for (int i = 0; i < height; i++) {
        int y = height - (i + 1);
        for (int x = 0; x < width; x++) {
            uint8_t r = byteArr[y * width + x * 3];
            uint8_t g = byteArr[y * width + x * 3 + 1];
            uint8_t b = byteArr[y * width + x * 3 + 2];
            Pixel p = Pixel(r, g, b);
            fout.write((char*)&p, 3);
        }
        fout.write((char*)&padding, paddingSize);

    }
    fout.close();

}

size_t Parser::parseCiff(std::vector<char> ciffFile) {

    if (ciffFile[0] != 'C' || ciffFile[1] != 'I' || ciffFile[2] != 'F' || ciffFile[3] != 'F') {
        return CIFF_MAGIC_ERROR;
    }

    //todo length test and other stuff.
    int64_t headerSize = bytes2uint64_t({ ciffFile.begin() + 4, ciffFile.begin() + 12 });
    int64_t contentSize = bytes2uint64_t({ ciffFile.begin() + 12, ciffFile.begin() + 20 });
    int64_t width = bytes2uint64_t({ ciffFile.begin() + 20, ciffFile.begin() + 28 });
    int64_t height = bytes2uint64_t({ ciffFile.begin() + 28, ciffFile.begin() + 36 });
    std::string caption = "";
    size_t idx = 0;
    while (ciffFile[36 + idx] != '\n') {
        caption += ciffFile[36 + idx++];
    }
    std::vector<char> pixels = { ciffFile.begin() + headerSize, ciffFile.end() };

    saveBytesAsBMP(width, height, pixels);
    std::cout << headerSize << "; " << contentSize << "; " << width << "; " << height << "; " << caption << std::endl;
    return 0;
}


size_t Parser::validateCAFFHeaderBlock(std::vector<char> CAFFHeaderBlock) {

    if (CAFFHeaderBlock[0] != 0x1) return BLOCK_ID_ERROR;
    uint64_t length = bytes2uint64_t({ CAFFHeaderBlock.begin() + 1, CAFFHeaderBlock.begin() + 1 + 8 });
    if (length != 4 + 8 + 8) return BLOCK_LENGTH_ERROR;
    if (CAFFHeaderBlock.size() != 9 + 4 + 8 + 8) return BLOCK_LENGTH_ERROR;
    std::vector<char> CAFFHeader = { CAFFHeaderBlock.begin() + 9, CAFFHeaderBlock.end() };

    if (CAFFHeader[0] != 'C' || CAFFHeader[1] != 'A' || CAFFHeader[2] != 'F' || CAFFHeader[3] != 'F') {
        return CAFF_MAGIC_ERROR;
    }
    uint64_t headerSize = bytes2uint64_t({ CAFFHeader.begin() + 4, CAFFHeader.begin() + 4 + 8 });
    uint64_t numAnim = bytes2uint64_t({ CAFFHeader.begin() + 12, CAFFHeader.begin() + 12 + 8 });

    return 0;
}

size_t Parser::validateCAFFCredit(std::vector<char> CAFFCredit) {

    uint16_t year = bytes2uint16_t({ CAFFCredit.begin(), CAFFCredit.begin() + 2 });
    uint8_t month = CAFFCredit[2];
    uint8_t day = CAFFCredit[3];
    uint8_t hour = CAFFCredit[4];
    uint8_t minute = CAFFCredit[5];
    uint64_t creator_len = bytes2uint64_t({ CAFFCredit.begin() + 5, CAFFCredit.begin() + 5 + 8 });
    //todo check length and date....
    std::vector<char> creator{ CAFFCredit.begin() + 13, CAFFCredit.end() };
    return 0;
}

size_t Parser::parseCaff(std::vector<char> caffFile) {
    size_t creditOffset = 9 + 4 + 8 + 8;
    //CAFF_HEADER STUFF
    std::vector<char> CAFFHeaderBlock = { caffFile.begin(), caffFile.begin() + 1 + 8 + 4 + 8 + 8 };
    if (CAFFHeaderBlock[0] != 0x1) return BLOCK_ID_ERROR;
    uint64_t headerLength = bytes2uint64_t({ CAFFHeaderBlock.begin() + 1, CAFFHeaderBlock.begin() + 1 + 8 });
    if (headerLength != 4 + 8 + 8) return BLOCK_LENGTH_ERROR;
    if (CAFFHeaderBlock.size() != 9 + 4 + 8 + 8) return BLOCK_LENGTH_ERROR;
    std::vector<char> CAFFHeader = { CAFFHeaderBlock.begin() + 9, CAFFHeaderBlock.end() };
    if (CAFFHeader[0] != 'C' || CAFFHeader[1] != 'A' || CAFFHeader[2] != 'F' || CAFFHeader[3] != 'F') {
        return CAFF_MAGIC_ERROR;
    }
    uint64_t headerSize = bytes2uint64_t({ CAFFHeader.begin() + 4, CAFFHeader.begin() + 4 + 8 });
    uint64_t numAnim = bytes2uint64_t({ CAFFHeader.begin() + 12, CAFFHeader.begin() + 12 + 8 });
    //CAFF_CREDITS STUFF
    std::vector<char> CAFFCreditBlockFrame = { caffFile.begin() + creditOffset, caffFile.begin() + creditOffset + 9 };
    if (CAFFCreditBlockFrame[0] != 0x2) return BLOCK_ID_ERROR;
    uint64_t creditsLength = bytes2uint64_t({ CAFFCreditBlockFrame.begin() + 1, CAFFCreditBlockFrame.begin() + 9 });
    size_t ciffBlockOffset = creditOffset + 9 + creditsLength;
    //todo length validate etc.....
    std::vector<char> CAFFCredits = { caffFile.begin() + creditOffset + 9, caffFile.begin() + creditOffset + 9 + creditsLength };
    validateCAFFCredit(CAFFCredits);

    std::vector<char> CIFFAnimationBLock = { caffFile.begin() + ciffBlockOffset, caffFile.begin() + ciffBlockOffset + 9 };
    if (CIFFAnimationBLock[0] != 0x3) return BLOCK_ID_ERROR;
    uint64_t ciffAnimationLength = bytes2uint64_t({ CIFFAnimationBLock.begin() + 1, CIFFAnimationBLock.end() });
    //todo block size check
    std::vector<char> CIFFAnimation = { caffFile.begin() + ciffBlockOffset + 9, caffFile.begin() + ciffBlockOffset + 9 + ciffAnimationLength }; //caffFile.begin() should be good as well?

    uint64_t duration = bytes2uint64_t({ CIFFAnimation.begin(), CIFFAnimation.begin() + 8 });
    std::vector<char> cifFile = { CIFFAnimation.begin() + 8, CIFFAnimation.begin() + ciffAnimationLength };//CIFFAnimation.end()
    parseCiff(cifFile);
    return 0;
}

uint16_t Parser::bytes2uint16_t(std::vector<char> vec) {
    uint16_t result;
    uint8_t* ptr = (uint8_t*)&result;
    uint8_t i = 0;
    while (i < 2)
        *ptr++ = vec[i++];
    return result;
}

uint32_t Parser::bytes2uint32_t(std::vector<char> vec) {
    uint32_t result;
    uint8_t* ptr = (uint8_t*)&result;
    uint8_t i = 0;
    while (i < 4)
        *ptr++ = vec[i++];
    return result;
}

uint64_t Parser::bytes2uint64_t(std::vector<char> vec) {
    uint64_t result;
    uint8_t* ptr = (uint8_t*)&result;
    uint8_t i = 0;
    while (i < 8)
        *ptr++ = vec[i++];
    return result;
}