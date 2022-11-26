package hu.bme.crysys.server.server.domain.parser;

public class CaffParseResult {
// metadata, tags, etc.
// path to preview
// extracted from parser call
//CAFF description: https://www.crysys.hu/downloads/vihima06/2020/CAFF.txt
//CIFF descripton: https://www.crysys.hu/downloads/vihima06/2020/CIFF.txt

public int numAnim;
public String date;
public List<CIFF> ciffList = new ArrayList();

public static class CIFF{
    public CIFF(String fileName, int duration, String caption, ArrayList<String> tags){
        this.fileName = fileName;
        this.duration = duration;
        this.caption = caption;
        this.tags = tags;
    }
    public final String fileName;
    public final int duration;
    public final String caption;
    public final List<String> tags;
}

}
