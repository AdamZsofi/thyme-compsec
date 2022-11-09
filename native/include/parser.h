#pragma once

#include <iostream>
#include <fstream>
#include <iomanip>
#include <cstdint>
#include <vector>

//error codes:
#define SUCCESS 0
#define CIFF_MAGIC_ERROR 1
#define CIFF_HEADER_SIZE_ERROR 2
#define CIFF_CONTENT_SIZE_ERROR 3
#define CIFF_CAPTION_ERROR 4
#define CIFF_TAGS_ERROR 5
#define CIFF_PIXELS_ERROR 6

#define BLOCK_ID_ERROR 7
#define BLOCK_LENGTH_ERROR 8 
#define CAFF_MAGIC_ERROR 9
#define CAFF_HEADER_SIZE_ERROR 10
#define CAFF_DATE_ERROR 11
#define CAFF_CREATOR_LENGTH_ERROR 12
#define CAFF_NO_ANIM_NUM 13
#define CAFF_ANIMATION_BLOCK_LENGTH_ERROR 14
#define CAFF_TOO_SHORT 15


//************************************************************************************************
#pragma pack(push, 1) //alignment!
struct BmpHeader {
    BmpHeader(uint32_t size) :sizeOfBitmapFile(size) {}
    char bitmapSignatureBytes[2] = { 'B', 'M' };
    uint32_t sizeOfBitmapFile;
    uint32_t reservedBytes = 0;
    uint32_t pixelDataOffset = 54;
};
#pragma pack(pop)

struct BmpInfoHeader {
    BmpInfoHeader(int32_t w, int32_t h) : width(w), height(h) {};
    uint32_t sizeOfThisHeader = 40;
    int32_t width; // in pixels
    int32_t height; // in pixels
    uint16_t numberOfColorPlanes = 1; // must be 1
    uint16_t colorDepth = 24;
    uint32_t compressionMethod = 0;
    uint32_t rawBitmapDataSize = 0; // generally ignored
    int32_t horizontalResolution = 0; // in pixel per meter
    int32_t verticalResolution = 0; // in pixel per meter
    uint32_t colorTableEntries = 0;
    uint32_t importantColors = 0;
};

struct Pixel {
    Pixel(uint8_t r, uint8_t g, uint8_t b) :red(r), green(g), blue(b) {};
    uint8_t blue = 255;
    uint8_t green = 255;
    uint8_t red = 0;
};
//************************************************************************************************

class Parser
{
public:
    Parser();
    ~Parser();
    void saveBytesAsBMP(int32_t width, int32_t height, std::vector<char> byteArr, std::string filename);
    void saveMetaData(std::string filename, uint64_t durationm, std::string caption, std::vector<std::string> tags);
    size_t parseCiff(std::vector<char> ciffFile, std::string filename, uint64_t duration);
    size_t validateCAFFHeaderBlock(std::vector<char> CAFFHeaderBlock);
    size_t validateCAFFCredit(std::vector<char> &CAFFCredit);
    size_t parseCaff(std::vector<char> caffFile, std::string filename);
    uint16_t bytes2uint16_t(std::vector<char> vec);
    uint32_t bytes2uint32_t(std::vector<char> vec);
    uint64_t bytes2uint64_t(std::vector<char> vec);
    bool validateDate(uint16_t year, uint8_t month, uint8_t day, uint8_t hour, uint8_t minute);
};
