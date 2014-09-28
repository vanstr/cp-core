package controllers;

import clouds.DriveFileFetcher;
import clouds.Dropbox;
import clouds.DropboxFileFetcher;
import clouds.GDrive;
import commons.FileFetcher;
import commons.SongMetadataPopulation;
import commons.SystemProperty;
import models.Song;
import models.User;
import play.Logger;
import play.mvc.Result;
import play.mvc.Security;
import structure.PlayList;
import structure.SongMetadata;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by imi on 13.09.2014..
 */
@Security.Authenticated(Secured.class)
public class ContentApi extends BaseController {

    public String getFileSrc(Long userId, Integer cloudId, String fileId) {
        String file = null;
        User user = User.getUserById(userId);
        try {
            if (SystemProperty.DROPBOX_CLOUD_ID.equals(cloudId)) {
                String accessTokenKey = user.dropboxAccessKey;
                Dropbox drop = new Dropbox(accessTokenKey);

                Logger.info(fileId);
                file = drop.getFileLink(fileId);
            } else if (SystemProperty.DRIVE_CLOUD_ID.equals(cloudId)) {
                GDrive gDrive = new GDrive(user.driveAccessToken, user.driveRefreshToken);
                file = gDrive.getFileLink(fileId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return file;
    }

    public PlayList getPlayList(Long userId) {
        List<structure.Song> data = getFiles("/", userId);
        PlayList playList = SongMetadataPopulation.populate(data, userId);

        return playList;
    }

    public boolean saveSongMetadata(structure.Song song, Long userId) {
        boolean res = false;
        User user = User.getUserById(userId);

        // get song by id
        Logger.info("pass1" + song + " " + song.getCloudId() + " " + song.getFileName());
        Song songEntity = Song.getSongByHash(user, song.getCloudId(), song.getFileName());

        if (songEntity == null) {
            // songEntity is empty, create new with metadata
            songEntity = new Song();
            songEntity.cloudId = song.getCloudId();
            songEntity.fileName = song.getFileName();
            songEntity.user = user;
            songEntity = setMetadata(songEntity, song);
            songEntity.save();
        } else {
            // update metadata
            songEntity = setMetadata(songEntity, song);
            songEntity.update();
        }

        return res;
    }

    private Song setMetadata(Song songEntity, structure.Song song) {
        SongMetadata metadata = song.getMetadata();
        if (metadata != null) {
            songEntity.metadataTitle = metadata.getTitle();
            songEntity.metadataAlbum = metadata.getAlbum();
            songEntity.metadataArtist = metadata.getArtist();
            songEntity.metadataGenre = metadata.getGenre();
            songEntity.metadataYear = metadata.getYear();
        }

        return songEntity;
    }

    private List<structure.Song> getFiles(String folderPath, Long userId) {
        FileFetcher dropboxFetcher = new DropboxFileFetcher(folderPath, userId);
        FileFetcher driveFetcher = new DriveFileFetcher(folderPath, userId);
        Thread dropboxThread = new Thread(dropboxFetcher);
        Thread driveThread = new Thread(driveFetcher);
        dropboxThread.start();
        driveThread.start();
        try {
            dropboxThread.join();
            driveThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<structure.Song> files = new ArrayList<structure.Song>();
        if (dropboxFetcher.getFiles() != null) {
            files.addAll(dropboxFetcher.getFiles());
        }
        if (driveFetcher.getFiles() != null) {
            files.addAll(driveFetcher.getFiles());
        }

        return files;
    }

    public static Result removeDrive(){
        Long userId = Long.parseLong(session("userId"));
        User user = User.getUserById(userId);
        user.driveAccessToken = null;
        user.driveRefreshToken = null;
        user.googleEmail = null;
        user.driveTokenExpires = null;
        user.update();
        return ok();
    }

    public static Result removeDropbox(){
        Long userId = Long.parseLong(session("userId"));
        User user = User.getUserById(userId);
        user.dropboxAccessKey = null;
        user.dropboxUid = null;
        user.update();
        return ok();
    }

}
