package commons;


import java.util.List;

public class CloudFile {

    private CloudFile() {
    }

    private static String getExtension(String filename) {
        int index = filename.lastIndexOf('.');
        return filename.substring(index + 1).toLowerCase();
    }

    public static boolean checkFileType(String fileName, List<String> requestedFileTypes) {

        if (fileName == null) {
            return false;
        }
        fileName = fileName.toLowerCase();
        boolean result = false;

        if (requestedFileTypes == null) {
            result = true;
        } else {
            String fileType = getExtension(fileName);
            if (requestedFileTypes.contains(fileType)) {

                int nameLength = fileName.length();
                int extensionLength = fileType.length();

                // file name ".mp3" - not allowed, at least "a.mp3"
                if (nameLength > (extensionLength + 2)) {
                    result = true;
                }
            }
        }
        return result;
    }


}
