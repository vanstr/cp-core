package commons;


import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: vanstr
 * Date: 13.26.12
 * Time: 14:39
 * To change this template use CloudFile | Settings | CloudFile Templates.
 */
public class CloudFile {

    public static String getExtension(String filename) {
        if (filename == null) {
            return null;
        }

        int index = filename.lastIndexOf('.');
        return filename.substring(index + 1).toLowerCase();
    }



    public static boolean checkFileType(String fileName,  ArrayList<String> requestedFileTypes){

        boolean result = false;

        if(requestedFileTypes == null ){
            result = true;
        }
        else{
            String fileType = getExtension(fileName);
            if( requestedFileTypes.contains(fileType) ){

                int nameLength = fileName.length();
                int extensionLength = fileType.length();

                // file name ".mp3" - not allowed, at least "a.mp3"
                if (nameLength > (extensionLength + 2)){
                    result = true;
                }

            }

        }

        return result;

    }
}
