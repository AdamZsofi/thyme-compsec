package hu.bme.crysys.server.server.domain.parser;

public class ParserController {

public bool parse(string CAFFFileName, string outputPath){
    Runtime rt = Runtime.getRuntime();
    Process pr = rt.exec("stuff "  + CAFFFileName + " " + outputPath);
}

}
