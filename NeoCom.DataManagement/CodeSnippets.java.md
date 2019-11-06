# Java Code Snippets
## Core
[GET THE LAST NAME OF CLASSPATH]
longName.substring (longName.lastIndexOf ('.'))

[COMPARE TWO FILES]
assertEquals("The files differ!", 
    FileUtils.readFileToString(file1, "utf-8"), 
    FileUtils.readFileToString(file2, "utf-8"));
    