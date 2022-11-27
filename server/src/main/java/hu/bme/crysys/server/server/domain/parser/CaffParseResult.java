import java.util.ArrayList;
import java.util.List;

public class CaffParseResult {
    // metadata, tags, etc.
    // path to preview
    // extracted from parser call
    //CAFF description: https://www.crysys.hu/downloads/vihima06/2020/CAFF.txt
    //CIFF descripton: https://www.crysys.hu/downloads/vihima06/2020/CIFF.txt
    public final int numAnim;
    public final String date;
    public final String creator;
    public final List<CIFF> ciffList;
    public CaffParseResult(int numAnim, String date, String creator, ArrayList<CIFF> ciffList){
        this.ciffList = ciffList;
        this.creator = creator;
        this.date = date;
        this.numAnim = numAnim;
    }
    public static class CIFF{
        public final String fileName;
        public final int duration;
        public final String caption;
        public final List<String> tags;
        public CIFF(String fileName, int duration, String caption, ArrayList<String> tags){
            this.fileName = fileName;
            this.duration = duration;
            this.caption = caption;
            this.tags = tags;
        }
    }

}
