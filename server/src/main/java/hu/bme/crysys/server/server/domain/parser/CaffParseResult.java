package hu.bme.crysys.server.server.domain.parser;

public class CaffParseResult {
// metadata, tags, etc.
// path to preview
// extracted from parser call
//CAFF description: https://www.crysys.hu/downloads/vihima06/2020/CAFF.txt
//CIFF descripton: https://www.crysys.hu/downloads/vihima06/2020/CIFF.txt

private int numAnim;
private String date;
private List<CIFF> ciffList;


public static class CIFF{
    private int duration;
    private int width;
    private int height;
    private String caption;
    private List<String> tags;
    private String fileName;
}

}
