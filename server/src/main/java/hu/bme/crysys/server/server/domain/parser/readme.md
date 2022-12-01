# Parser integration and usage

### ParserController.java
Has 1 important static method: `parse(String CAFFFileName, String outputPath)`
The metadata and .png files will be generated inside the folder `outputPath`. 
¿¿¿This folder will be overwritten (all content will be ereased), if exists, created otherwise.


Usage recommendation: target\<ID>\

### CaffParseResult.java
Check out the code, stuct-like java class, contains the metadata of the CAFF, a list of CIFFs.

## Setup
1. Build the CAFF native parser.
2. Give the path of the parser.
3. Use the parameters wisely, especially outputpath.
