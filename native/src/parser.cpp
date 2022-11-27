#include "parser.h"

Parser::Parser() {}
Parser::~Parser() {}

void Parser::saveBytesAsBMP(int32_t width, int32_t height, std::vector<char> byteArr, std::string filename) {
    std::ofstream fout(filename, std::ios::binary);
    int32_t numberOfPixels = width * height;
    BmpHeader header = BmpHeader(numberOfPixels * 3 + 54);
    BmpInfoHeader infoHeader = BmpInfoHeader(width, height);
    fout.write((char*)&header, 14);
    fout.write((char*)&infoHeader, 40);

    uint8_t paddingSize = (4 - (width*3) % 4) % 4;
    uint8_t padding[3] = { 0, 0, 0 };
    for (int32_t i = 0; i < height; i++) {
        int32_t y = height - (i + 1);
        int32_t offset = y * width * 3;
        for (int x = 0; x < width; x++) {
            uint8_t r = byteArr[offset + x * 3];
            uint8_t g = byteArr[offset + x * 3 + 1];
            uint8_t b = byteArr[offset + x * 3 + 2];
            Pixel p = Pixel(r, g, b);
            fout.write((char*)&p, 3);
        }
        fout.write((char*)&padding, paddingSize);

    }
    fout.close();
}

void Parser::saveMetaData(std::string filename, uint64_t duration, std::string caption, std::vector<std::string> tags){
    std::ofstream fout(filename);
    fout << "{" << std::endl;
    fout << " \"duration\" : " << duration << "," << std::endl;
    fout << " \"caption\" : \"" <<  caption << "\"," << std::endl;    
    fout << " \"tags\" : [" << std::endl;
    int i = 1; 
    for(std::string tag : tags){
        fout << "\"" << tag << "\"";
        if(i++ != tags.size()) fout << ",";
    }
    fout << "]" << std::endl;
    
    fout << "}" << std::endl;
    fout.close();
}

size_t Parser::parseCiff(std::vector<char> ciffFile, std::string filename, std::string outFilename, uint64_t duration) {
    if(ciffFile.size()< 36){
        std::cout << "SHORT" << std::endl;
        return CAFF_TOO_SHORT;
    }
    if (ciffFile[0] != 'C' || ciffFile[1] != 'I' || ciffFile[2] != 'F' || ciffFile[3] != 'F') {
        return CIFF_MAGIC_ERROR;
    }

    uint64_t headerSize = bytes2uint64_t({ ciffFile.begin() + 4, ciffFile.begin() + 12 });
    if(headerSize < 36){
        std::cout << "SHORT" << std::endl;
        return CAFF_TOO_SHORT;
    }
    if(ciffFile.size()>headerSize && ciffFile[headerSize-1] != '\0'){
        std::cout<<"CIFFHEADERERRROR"<< std::endl;
        return CIFF_HEADER_SIZE_ERROR;
    }
    uint64_t contentSize = bytes2uint64_t({ ciffFile.begin() + 12, ciffFile.begin() + 20 });
    if(ciffFile.size() != contentSize + headerSize){
        std::cout<<ciffFile.size() << "; " << contentSize + headerSize << std::endl;
        std::cout<<"CIFF_CONTENT_SIZE_ERROR"<< std::endl;
        return CIFF_CONTENT_SIZE_ERROR;        
    }
    uint64_t width = bytes2uint64_t({ ciffFile.begin() + 20, ciffFile.begin() + 28 });
    uint64_t height = bytes2uint64_t({ ciffFile.begin() + 28, ciffFile.begin() + 36 });
    if(height*width*3 != contentSize){
        std::cout<<"CIFF_CONTENT_SIZE_ERROR width* height*3"<< std::endl;
        return CIFF_CONTENT_SIZE_ERROR;        
    }
    if(ciffFile.size()< headerSize){
        std::cout << "SHORT" << std::endl;
        return CAFF_TOO_SHORT;
    }
    std::string cat(ciffFile.begin() + 36, ciffFile.begin() + headerSize);
    if(cat.find("\n") == std::string::npos){
        std::cout << "no \\n error" << std::endl;
        return CIFF_NO_ENDL;
    }
    std::istringstream captionAndTag(cat);
    std::string caption;
    if(!getline(captionAndTag, caption)){
        std::cout << "No caption and tag" << std::endl;
        return CIFF_NO_CAPTION_AND_TAG;
    }
    std::vector<std::string> tags;
    std::string tmp;

    while (getline(captionAndTag, tmp, '\0')) {
        if(tmp.find("\n") != std::string::npos){
            std::cout << "Multiline tag detected" << std::endl;
            return CIFF_MULTILINE_TAG;
        }
        tags.push_back(tmp);
    }

    std::vector<char> pixels = { ciffFile.begin() + headerSize, ciffFile.end() };
    saveMetaData(outFilename, duration, caption, tags);
    saveBytesAsBMP(width, height, pixels, filename);
    std::cout << headerSize << "; " << contentSize << "; " << width << "; " << height << "; " << caption << std::endl;
    return 0;
}


size_t Parser::validateCAFFHeaderBlock(std::vector<char> CAFFHeaderBlock) {

    if (CAFFHeaderBlock.size() != 9 + 4 + 8 + 8) return BLOCK_LENGTH_ERROR;
    if (CAFFHeaderBlock[0] != 0x1) return BLOCK_ID_ERROR;
    uint64_t length = bytes2uint64_t({ CAFFHeaderBlock.begin() + 1, CAFFHeaderBlock.begin() + 1 + 8 });
    if (length != 4 + 8 + 8) return BLOCK_LENGTH_ERROR;
    std::vector<char> CAFFHeader = { CAFFHeaderBlock.begin() + 9, CAFFHeaderBlock.end() };

    if (CAFFHeader[0] != 'C' || CAFFHeader[1] != 'A' || CAFFHeader[2] != 'F' || CAFFHeader[3] != 'F') {
        return CAFF_MAGIC_ERROR;
    }

    return 0;
}

size_t Parser::validateCAFFCredit(std::vector<char> &CAFFCredit) {

    if(CAFFCredit.size() < 14){
        std::cout << "CAFF_TOO_SHORT" << std::endl;
        return CAFF_TOO_SHORT;
    }
    uint16_t year = bytes2uint16_t({ CAFFCredit.begin(), CAFFCredit.begin() + 2 });
    uint8_t month = CAFFCredit[2];
    uint8_t day = CAFFCredit[3];
    uint8_t hour = CAFFCredit[4];
    uint8_t minute = CAFFCredit[5];
    if (!validateDate(year, month, day, hour, minute)) {
        std::cout << "CAFF_DATE_ERROR" << std::endl;
        return CAFF_DATE_ERROR;
    }
    std::cout<<"Date: ";
    std::cout<< year << "."<< +month << "." << +day << " " << +hour << ":" << +minute << std::endl;
    uint64_t creatorLen = bytes2uint64_t({ CAFFCredit.begin() + 6, CAFFCredit.begin() + 6 + 8 });
    if (CAFFCredit.size() != creatorLen + 6 + 8) {
        std::cout<<CAFFCredit.size() << creatorLen;
        std::cout << "CAFF_CREATOR_LENGTH_ERROR" << std::endl;
        return CAFF_CREATOR_LENGTH_ERROR;
    }
    if(!creatorLen){
        std::cout<< "Empty creator " << std::endl;
        return CIFF_EMPTY_CREATOR;
    }
    std::string creator{ CAFFCredit.begin() + 13, CAFFCredit.end() };
    std::cout<< "Creator: " << creator << std::endl;
    return 0;
}

size_t Parser::parseCaff(std::vector<char> caffFile, std::string filename, std::string outFileName) {
    size_t creditOffset = 9 + 4 + 8 + 8;
    size_t rawSize = caffFile.size();
    if(rawSize < creditOffset + 9){//safe utill caff credits creatorLen
        return CAFF_TOO_SHORT;
    }
    //CAFF_HEADER STUFF
    //--------------------------------------------------------------------------------------------------
    std::vector<char> CAFFHeaderBlock = { caffFile.begin(), caffFile.begin() + 1 + 8 + 4 + 8 + 8};
    if (CAFFHeaderBlock[0] != 0x1) {
        std::cout<< "Invalid CAFF header block id" << std::endl;
        return BLOCK_ID_ERROR;
    }
    uint64_t headerLength = bytes2uint64_t({ CAFFHeaderBlock.begin() + 1, CAFFHeaderBlock.begin() + 1 + 8 });
    if (headerLength != 4 + 8 + 8) {
        std::cout<< "Invalid CAFF header block length" << std::endl;
        return BLOCK_LENGTH_ERROR;
    }
    if (CAFFHeaderBlock.size() != 9 + 4 + 8 + 8) {
        std::cout<< "Invalid CAFF block length" << std::endl;
        return BLOCK_LENGTH_ERROR;
    }
    std::vector<char> CAFFHeader = { CAFFHeaderBlock.begin() + 9, CAFFHeaderBlock.end() };
    if (CAFFHeader[0] != 'C' || CAFFHeader[1] != 'A' || CAFFHeader[2] != 'F' || CAFFHeader[3] != 'F') {
        std::cout<< "CAFF_MAGIC_ERROR" << std::endl;
        return CAFF_MAGIC_ERROR;
    }
    uint64_t headerSize = bytes2uint64_t({ CAFFHeader.begin() + 4, CAFFHeader.begin() + 4 + 8 });
    if(headerSize != 20){
        std::cout<< "CAFF_HEADER_SIZE_ERROR" << std::endl;
        return CAFF_HEADER_SIZE_ERROR;
    }
    uint64_t numAnim = bytes2uint64_t({ CAFFHeader.begin() + 12, CAFFHeader.begin() + 12 + 8 });
    if(numAnim < 1){
        std::cout<< "CAFF_NO_ANIM_NUM" << std::endl;
        return CAFF_NO_ANIM_NUM;
    }

    //CAFF_CREDITS STUFF
    //--------------------------------------------------------------------------------------------------
    std::vector<char> CAFFCreditBlockFrame = { caffFile.begin() + creditOffset, caffFile.begin() + creditOffset + 9 };
    if (CAFFCreditBlockFrame[0] != 0x2) {
        std::cout<< "BLOCK_ID_ERROR" << std::endl;
        return BLOCK_ID_ERROR;
    }
    uint64_t creditsLength = bytes2uint64_t({ CAFFCreditBlockFrame.begin() + 1, CAFFCreditBlockFrame.begin() + 9 });
    size_t ciffBlockOffset = creditOffset + 9 + creditsLength;
    if(caffFile.size() <= ciffBlockOffset){
        std::cout<< "CAFF_CREATOR_LENGTH_ERROR1" << std::endl;
        return CAFF_CREATOR_LENGTH_ERROR;
    }else if(caffFile.size() > ciffBlockOffset && caffFile[ciffBlockOffset] != 0x3){
        std::cout<< "CAFF_CREATOR_LENGTH_ERROR2" << std::endl;
        return CAFF_CREATOR_LENGTH_ERROR;
    }else if(creditsLength == 0){
        std::cout<< "CAFF_CREATOR_LENGTH_ERROR2" << std::endl;
        return CAFF_CREATOR_LENGTH_ERROR;
    }
    std::vector<char> CAFFCredits = { caffFile.begin() + creditOffset + 9, caffFile.begin() + creditOffset + 9 + creditsLength };
    size_t result = validateCAFFCredit(CAFFCredits);
    if(result){
        return result;
    }

    //CAFF ANIMATION STUFF
    //--------------------------------------------------------------------------------------------------
    std::cout << "Expected CIFF Frames: "<< numAnim << std::endl;
    for(uint64_t ciffNum = 0; ciffNum < numAnim; ciffNum++){
        if(caffFile.size() < ciffBlockOffset + 9){
            std::cout << "short caffFile" << std::endl;
            return CAFF_TOO_SHORT;
        }
        std::vector<char> CIFFAnimationBLock = { caffFile.begin() + ciffBlockOffset, caffFile.begin() + ciffBlockOffset + 9 };
        if (CIFFAnimationBLock[0] != 0x3) {
            return BLOCK_ID_ERROR;
        }
        uint64_t caffAnimationBlockLength = bytes2uint64_t({ CIFFAnimationBLock.begin() + 1, CIFFAnimationBLock.end() });
        if(caffFile.size() < caffAnimationBlockLength + ciffBlockOffset + 9){
            std::cout<< "wrong caffAnimationBlockLength" << std::endl;
            return CAFF_ANIMATION_BLOCK_LENGTH_ERROR;
        }

        if(caffFile.size() < ciffBlockOffset + 9 + caffAnimationBlockLength || caffFile.size() < ciffBlockOffset + 9 + 8){
            std::cout << "short caffFile" << std::endl;
            return CAFF_TOO_SHORT;
        }        
        std::vector<char> CIFFAnimation = { caffFile.begin() + ciffBlockOffset + 9, caffFile.begin() + ciffBlockOffset + 9 + caffAnimationBlockLength }; //caffFile.begin() should be good as well?

        if(CIFFAnimation.size() < 8 || CIFFAnimation.size() < caffAnimationBlockLength){
            std::cout << "short caffFile" << std::endl;
            return CAFF_TOO_SHORT;
        }        
        uint64_t duration = bytes2uint64_t({ CIFFAnimation.begin(), CIFFAnimation.begin() + 8 });
        std::vector<char> cifFile = { CIFFAnimation.begin() + 8, CIFFAnimation.begin() + caffAnimationBlockLength};//CIFFAnimation.end()
        std::ostringstream ss;
        ss << std::setw(3) << std::setfill('0') << ciffNum;
        std::string oNum(ss.str());
        std::string FileName = filename  + "_"+ oNum + ".bmp";
        std::string OutFileName = outFileName + "_" + oNum + ".json";
        parseCiff(cifFile, FileName, OutFileName, duration);
        ciffBlockOffset += caffAnimationBlockLength + 9;
    }
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


bool Parser::validateDate(uint16_t year, uint8_t month, uint8_t day, uint8_t hour, uint8_t minute) {
    if (year > 2022 || year < 1900) return false;
    if (month > 12 || month == 0) return false;
    if (day > 31 || day == 0) return false;
    if (hour > 23) return false;
    if (minute > 59) return false;
    return true;
}
