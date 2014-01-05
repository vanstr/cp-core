package cloud;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import commons.CloudFile;
import commons.Initializator;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class GDrive {

    private static String CLIENT_ID = Initializator.getLocalProperties().getProperty("drive.client.id");
    private static String CLIENT_SECRET = Initializator.getLocalProperties().getProperty("drive.client.secret");
    private static String REDIRECT_URI = Initializator.getLocalProperties().getProperty("drive.redirect.uri");
    private static final List<String> SCOPES = Arrays.asList(
            "https://www.googleapis.com/auth/drive");

    private String accessToken;

    public GDrive(String accessToken){
        this.accessToken = accessToken;
    }


    public String retrieveAccessToken(String code){
        HttpTransport httpTransport = new NetHttpTransport();
        JacksonFactory jsonFactory = new JacksonFactory();
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(httpTransport, jsonFactory,
                        CLIENT_ID,
                        CLIENT_SECRET, SCOPES)
                        .setAccessType("offline").setApprovalPrompt("force").build();
        try {
            GoogleTokenResponse response =
                    flow.newTokenRequest(code).setRedirectUri(REDIRECT_URI).execute();
            Credential cred = flow.createAndStoreCredential(response, null);
            return cred.getAccessToken();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> getFileList(String folderPath, boolean recursive, List<String> fileTypes){
        ArrayList<String> files = new ArrayList<String>();
        Credential credential = new GoogleCredential().setAccessToken(this.accessToken);
        HttpTransport httpTransport = new NetHttpTransport();
        JacksonFactory jsonFactory = new JacksonFactory();
        Drive service = new Drive.Builder(httpTransport, jsonFactory, credential).build();
        List<File> filez = null;
        try {
            filez = retrieveAllFiles(service);
            for(File f: filez){
                if ( CloudFile.checkFileType(f.getOriginalFilename(), fileTypes) ) {
                    files.add(f.getOriginalFilename());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        return files;
    }

    private static List<File> retrieveAllFiles(Drive service) throws IOException {
        List<File> result = new ArrayList<File>();
        Drive.Files.List request = service.files().list();

        do {
            try {
                FileList files = request.execute();

                result.addAll(files.getItems());
                request.setPageToken(files.getNextPageToken());
            } catch (IOException e) {
                System.out.println("An error occurred: " + e);
                request.setPageToken(null);
            }
        } while (request.getPageToken() != null &&
                request.getPageToken().length() > 0);

        return result;
    }
}
