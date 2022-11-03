#pragma once

class Parser
{
public:
    Parser();
    ~Parser();
    void ParseFile(char *data, int len);
};