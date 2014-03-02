package cloud;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.session.*;
import commons.CloudFile;
import commons.Initializator;
import commons.Tokens;
import ejb.ContentBeanRemote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * UserEntity: vanstr
 * Date: 13.29.6
 * Time: 20:55
 * Class represent basic cloud.Dropbox API functionality
 * Like: getFileList, getFileLink
 */
public class Dropbox {

    final static Logger logger = LoggerFactory.getLogger(Dropbox.class);

    // Define application params
    private static final String APP_KEY = Initializator.getLocalProperties().getProperty("dropbox.app.key");
    private static final String APP_SECRET = Initializator.getLocalProperties().getProperty("dropbox.app.secret");

    private static final String EXCEPTION_EMPTY_ACCESS_TOKENS = "EXCEPTION_EMPTY_ACCESS_TOKENS";
    private static final String EXCEPTION_EMPTY_REQUEST_TOKENS = "EXCEPTION_EMPTY_REQUEST_TOKENS";
    private static final String EXCEPTION_UNDEFINED_DIR = "EXCEPTION_UNDEFINED_DIR";

    private static final Session.AccessType ACCESS_TYPE = Session.AccessType.DROPBOX;
    private static final AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);

    private WebAuthSession session;
    private DropboxAPI<WebAuthSession> api;
    private WebAuthSession.WebAuthInfo authInfo = null;

    /**
     * Start session to likn user account with CloudMusic
     */
    public Dropbox() throws Exception {

        session = new WebAuthSession(appKeys, ACCESS_TYPE);
        authInfo = session.getAuthInfo();

        logger.debug("instance created");
    }

    public Dropbox(String accessTokenKey, String accessTokenSecret) throws Exception {

        if (accessTokenKey == null || accessTokenSecret == null) {
            logger.info("EXCEPTION_EMPTY_ACCESS_TOKENS");
            throw new Exception(EXCEPTION_EMPTY_ACCESS_TOKENS);
        }

        AccessTokenPair accessTokenPair = new AccessTokenPair(accessTokenKey, accessTokenSecret);
        session = new WebAuthSession(appKeys, ACCESS_TYPE);
        session.setAccessTokenPair(accessTokenPair);
        api = new DropboxAPI<WebAuthSession>(session);

        logger.debug("debug instance with access key created");

    }


    /**
     * @return  Tokens
     */
    public Tokens getRequestTokens() {
        // Obtaining oAuth request token to be used for the rest of the authentication process.
        RequestTokenPair pair = authInfo.requestTokenPair;

        return new Tokens(pair.key, pair.secret);

    }

    /**
     * generate link, where user provides privileges to access his account data
     *
     * @return Auth link
     */
    public String getAuthLink() {
        logger.debug("Dropbox auth link:" + authInfo.url);
        return authInfo.url;
    }

    /**
     * Get User access token pair
     *
     * @return Access tokens
     */
    public Tokens getUserAccessTokens(Tokens requestTokens) throws Exception {

        if (requestTokens == null || requestTokens.key == null || requestTokens.secret == null) {
            logger.error(EXCEPTION_EMPTY_REQUEST_TOKENS);
            throw new Exception(EXCEPTION_EMPTY_REQUEST_TOKENS);
        }
        RequestTokenPair pair = new RequestTokenPair(requestTokens.key, requestTokens.secret);

        session.retrieveWebAccessToken(pair);

        AccessTokenPair tokens = session.getAccessTokenPair();

        return new Tokens(tokens.key, tokens.secret);
    }


    /**
     * Get file link for downloading
     * @return  file download link
     */
    public String getFileLink(String filePath) throws Exception {

        DropboxAPI.DropboxLink media = api.media(filePath, false);

        return media.url;
    }

    /**
     * @param folderPath - in which folder look up
     * @param requestedFileTypes  - if file type == NULL return all list, ex: folder, files, mp3, txt
     *
     * @return    array of file
     */
    public List<String[]> getFileList(String folderPath, List<String> requestedFileTypes) throws Exception {

        ArrayList<String[]> files = new ArrayList<String[]>();

        for(String fileType : requestedFileTypes){
            List<Entry> dropboxEntries = api.search(folderPath, "." + fileType, 0, false);
            if(dropboxEntries != null){
                for(Entry dropboxEntry : dropboxEntries){
                    if(CloudFile.checkFileType(dropboxEntry.fileName(), requestedFileTypes)){
                        //TODO maybe url, id?
                        // TODO: move DROPBOX_CLOUD_ID to settings
                        files.add(new String[]{ContentBeanRemote.DROPBOX_CLOUD_ID.toString()
                                , dropboxEntry.path, null, null});
                    }
                }
            }
        }

        return files;
    }
}