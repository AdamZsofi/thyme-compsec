//todo download jar, https://github.com/stleary/JSON-java
//todo add to classpath: Intellij-->file->project structure--> modules --> dependencies tab --> hit +, choose JAR, browse the downloaded file.

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class ParserController {

    //todo provide the path of your compiled CAFF parser.
    private static String parserExecutablePath = "C:\\Users\\pinte\\OneDrive\\Dokumentumok\\semester9\\comSec\\thyme-compsec\\native\\build\\CAFF.exe";
    //TODO provide source path
    private static String root = "C:\\Users\\pinte\\OneDrive\\Dokumentumok\\semester9\\comSec\\thyme-compsec\\native\\build\\";

    public static CaffParseResult parse(String CAFFFileName, String id){
        String outputPath = Paths.get(root, id).toString();
        System.out.println(outputPath);
        File outputFolder = new File(outputPath);
        deleteFolder(outputFolder);//ATTENTION, former content will be deleted!
        if (!outputFolder.exists()){
            outputFolder.mkdirs();//can create the entire directory path including parents
        }

        CaffParseResult result;
        Runtime rt = Runtime.getRuntime();
        try {
            Process pr = rt.exec(parserExecutablePath + " "  + CAFFFileName + " " + outputPath + "/");
            int exitCode = pr.waitFor();
            logPr(pr);
            logPrErr(pr);
            System.out.println("Parser returned with code " + exitCode);
            if(exitCode != 0) {
                System.out.println("Parsing failed");
                return null;//todo handle null (or throw some exception)
            }
            result = scanAndSaveMetaData(outputFolder);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
    public static void logPr(Process pr) throws IOException {
        BufferedReader output = new BufferedReader(new InputStreamReader(pr.getInputStream()));
        String outputLog = "";
        while ((outputLog = output.readLine()) != null) {
            System.out.println(outputLog);
        }
    }
    public static void logPrErr(Process pr) throws IOException {
        BufferedReader output = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
        String outputLog = "";
        while ((outputLog = output.readLine()) != null) {
            System.out.println(outputLog);
        }
    }


    private static CaffParseResult scanAndSaveMetaData(final File folder) throws IOException {
        folder.getPath();

        int numAnim = 0;
        String creator = "";
        String date = "";
        ArrayList<CaffParseResult.CIFF> ciffList = new ArrayList<>();
        for (final File file : folder.listFiles()) {
            if (isJson(file.getName())) {
                CaffParseResult.CIFF ciff = jsonToCIFF(file);
                ciffList.add(ciff);
            }
        }
        return  new CaffParseResult(numAnim, date, creator, ciffList);
    }

    private static Boolean isJson(String filename){
        String[] fileSplit = filename.split(Pattern.quote("."));
        if(fileSplit.length < 2) return false;
        return fileSplit[1].equals("json");
    }

    public static void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if(files!=null) { //some JVMs return null for empty dirs
            for(File f: files) {
                if(f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    f.delete();
                }
            }
        }
        folder.delete();
    }

    private static CaffParseResult.CIFF jsonToCIFF(File file) throws IOException {
        String jsonfilename = file.getName();
        String imgFileName = jsonfilename.replaceAll("json", "bmp");
        String jsonString = Files.readString( file.toPath());
        JSONObject obj = new JSONObject(jsonString);
        Integer duration = obj.getInt("duration");
        String caption = obj.getString("caption");
        JSONArray tagsArr = obj.getJSONArray("tags");
        ArrayList<String> tagsArrList = new ArrayList<String>();
        for (int i = 0; i < tagsArr.length(); i++){
            String tag = tagsArr.getString(i);
            tagsArrList.add(tag);
        }
        return new CaffParseResult.CIFF( imgFileName, duration, caption, tagsArrList);
    }
}
