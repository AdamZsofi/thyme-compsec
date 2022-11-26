package hu.bme.crysys.server.server.domain.parser;
import org.json.*;

public class ParserController {

String parserPath = "parserpath";//todo provide the path of your compiled CAFF parser.

public CaffParseResult parse(string CAFFFileName, string outputPath){
    Runtime rt = Runtime.getRuntime();
    Process pr = rt.exec("stuff "  + CAFFFileName + " " + outputPath);

    CaffParseResult result = new CaffParseResult();
    //maybe current wd + outputpath or something else? 
    final File outputFolder = new File(outputPath); 
    scanAndSaveMetaData(folder);

}

private void scanAndSaveMetaData(final File folder) {
    for (final File file : folder.listFiles()) {
        if (file.getName()) {
            CaffParseResult.CIFF ciff = jsonToCIFF(file.getPath());
        }
    }
}

Boolean isJson(String filename){
    ArrayList fileSplit = filename.split(".");
    return fileSplit.get(fileSplit.size() - 1) == "json";
}

CaffParseResult.CIFF jsonToCIFF(Path path){
    String jsonfilename = path.getFileName();
    String imgFileName = jsonfilename.replaceAll("json", "bmp");
    String jsonString = Files.readString(path, encoding);
    JSONObject obj = new JSONObject(jsonString);
    Integer duration = obj.getInt("duration");
    String caption = obj.getString("caption");
    JSONArray tagsArr = obj.getJSONArray("tags");
    ArrayList<String> tagsArrList = new ArrayList<String>();
    for (int i = 0; i < tagsArr.length(); i++){
        String tag = arr.getJsonString(i);
        tagsArrList.add(tag);
    }
    return CaffParseResult.CIFF( , duration, caption, tagsArrList);
}


}
