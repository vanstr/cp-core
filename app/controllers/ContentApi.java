package controllers;

import clouds.DriveFileFetcher;
import clouds.Dropbox;
import clouds.DropboxFileFetcher;
import clouds.GDrive;
import commons.FileFetcher;
import commons.SongMetadataPopulation;
import commons.SystemProperty;
import models.SongEntity;
import models.UserEntity;
import play.Logger;
import structure.PlayList;
import structure.Song;
import structure.SongMetadata;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by imi on 13.09.2014..
 */
public class ContentApi {


  public String getFileSrc(Long userId, Integer cloudId, String fileId) {
    String file = null;    
    UserEntity user = UserEntity.getUserById(userId);
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

    List<Song> data = getFiles("/", userId);
    PlayList playList = SongMetadataPopulation.populate(data, userId);

    return playList;
  }

  
  public boolean saveSongMetadata(Song song, Long userId) {

    boolean res = false;

    UserEntity user = UserEntity.getUserById(userId);

    // get song by id    
    Logger.info("pass1" + song + " " + song.getCloudId() + " " + song.getFileName());
    SongEntity songEntity = SongEntity.getSongByHash(user, song.getCloudId(), song.getFileName());

    if (songEntity == null) {
      // songEntity is empty, create new with metadata
      songEntity = new SongEntity();
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

  private SongEntity setMetadata(SongEntity songEntity, Song song) {
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

  private List<Song> getFiles(String folderPath, Long userId) {

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

    List<Song> files = new ArrayList<Song>();
    if (dropboxFetcher.getFiles() != null) {
      files.addAll(dropboxFetcher.getFiles());
    }
    if (driveFetcher.getFiles() != null) {
      files.addAll(driveFetcher.getFiles());
    }

    return files;
  }


}
