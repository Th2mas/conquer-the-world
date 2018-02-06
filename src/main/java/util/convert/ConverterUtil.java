package util.convert;

import java.io.File;

public class ConverterUtil {
    private ConverterUtil(){}

    /**
     * Checks the valid file name with the given data type
     * @param file the filename to be checked
     * @param fileType the requested file type
     */
    public static void checkFormat(String file, String fileType){
        if(!file.endsWith(fileType)) throw new IllegalArgumentException(file + " is not a " + fileName(file).toUpperCase() + " file!");
    }

    /**
     * Gets the file name of the given string
     * @param file file to be truncated
     * @return the file name
     */
    public static String fileName(String file){
        return !file.contains(File.separator)
                ?file.substring(0, file.lastIndexOf("."))
                :file.substring(file.lastIndexOf(File.separator)+1, file.lastIndexOf("."));
    }


}
