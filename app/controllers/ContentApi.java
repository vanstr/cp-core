package controllers;

import clouds.DriveFileFetcher;
import clouds.Dropbox;
import clouds.DropboxFileFetcher;
import clouds.GDrive;
import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.JsonNode;
import commons.FileFetcher;
import commons.SongMetadataPopulation;
import commons.SystemProperty;
import models.PlayListEntity;
import models.SongEntity;
import models.UserEntity;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Security;
import structure.PlayList;
import structure.Song;
import structure.SongMetadata;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by imi on 13.09.2014..
 */
@Security.Authenticated(Secured.class)
public class ContentApi extends BaseController {

    public static Result getFileSrc(Integer cloudId, String fileId) {
        String file = null;
        try {
            UserEntity userEntity = UserEntity.getUserById(Long.parseLong(session("user")));
            if (SystemProperty.DROPBOX_CLOUD_ID.equals(cloudId)) {
                String accessTokenKey = userEntity.dropboxAccessKey;
                Dropbox drop = new Dropbox(accessTokenKey);

                Logger.info(fileId);
                file = drop.getFileLink(fileId);
            } else if (SystemProperty.DRIVE_CLOUD_ID.equals(cloudId)) {
                GDrive gDrive = new GDrive(userEntity.driveAccessToken, userEntity.driveRefreshToken);
                file = gDrive.getFileLink(fileId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return returnInJsonOk(file);
    }

    public static Result getPlayList() {
        Long userId = Long.parseLong(session("user"));
        List<structure.Song> data = getFiles("/", userId);
        PlayList playList = SongMetadataPopulation.populate(data, userId);

        return returnInJsonOk(playList);
    }

    public static Result getPlayList(Long playListId) {
        PlayListEntity playListEntity = PlayListEntity.getUserById(playListId);

        PlayList playList = new PlayList(playListId, playListEntity.getName());
        for(SongEntity songEntity : playListEntity.getSongEntities()){
            playList.add(new Song(songEntity));
        }

        return returnInJsonOk(playList);
    }

    public static Result saveSongMetadata() {
        JsonNode songNode = request().body().asJson();
        Song song = Json.fromJson(songNode, Song.class);
        Long userId = Long.parseLong(session("user"));
        UserEntity userEntity = UserEntity.getUserById(userId);

        // get song by id
        Logger.info("pass1" + song + " " + song.getCloudId() + " " + song.getFileName());
        SongEntity songEntity = SongEntity.getSongByHash(userEntity, song.getCloudId(), song.getFileName());

        if (songEntity == null) {
            // songEntity is empty, create new with metadata
            songEntity = new SongEntity();
            songEntity.cloudId = song.getCloudId();
            songEntity.fileName = song.getFileName();
            songEntity.userEntity = userEntity;
            songEntity = setMetadata(songEntity, song);
            songEntity.save();
        } else {
            // update metadata
            songEntity = setMetadata(songEntity, song);
            songEntity.update();
        }

        return ok();
    }

    private static SongEntity setMetadata(SongEntity songEntity, structure.Song song) {
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

    private static List<structure.Song> getFiles(String folderPath, Long userId) {
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
        Long userId = Long.parseLong(session("user"));
        UserEntity userEntity = UserEntity.getUserById(userId);
        userEntity.driveAccessToken = null;
        userEntity.driveRefreshToken = null;
        userEntity.googleEmail = null;
        userEntity.driveTokenExpires = null;
        userEntity.update();
        return ok();
    }

    public static Result removeDropbox(){
        Long userId = Long.parseLong(session("user"));
        UserEntity userEntity = UserEntity.getUserById(userId);
        userEntity.dropboxAccessKey = null;
        userEntity.dropboxUid = null;
        userEntity.update();
        return ok();
    }

    public static Result addPlayList() {
        JsonNode songNode = request().body().asJson();
        PlayList playList = Json.fromJson(songNode, PlayList.class);
        Long userId = Long.parseLong(session("user"));
        UserEntity user = UserEntity.getUserById(userId);

        Set<SongEntity> songs = new HashSet<SongEntity>();
        if(playList.getSongs() != null){
            List<Object> fileIds = new ArrayList<Object>();
            List<Object> cloudIds = new ArrayList<Object>();

            for(int i = 0; i < playList.getSongs().size(); i++){
                fileIds.add(i, playList.getSongs().get(i).getFileId());
                cloudIds.add(i, playList.getSongs().get(i).getCloudId());
            }

            songs.addAll(addExistingSongs(fileIds, cloudIds));
            songs.addAll(addNewSongs(fileIds, cloudIds, user));
        }

        PlayListEntity playListEntity = new PlayListEntity();
        playListEntity.setName(playList.getName());
        playListEntity.setUserEntity(user);
        playListEntity.setCreated(new Timestamp(System.currentTimeMillis()));
        playListEntity.setUpdated(new Timestamp(System.currentTimeMillis()));
        playListEntity.setSongEntities(songs);

        playListEntity.save();

        return returnInJsonOk(playListEntity.getId());
    }

    public static Result getPlayLists() {
        Long userId = Long.parseLong(session("user"));
        List<PlayListEntity> entities = Ebean.createQuery(PlayListEntity.class)
                .where().eq("user_id", userId).findList();
        List<PlayList> playLists = new ArrayList<PlayList>();
        if(entities != null){
            for(PlayListEntity entity : entities){
                playLists.add(new PlayList(entity));
            }
        }
        return returnInJsonOk(playLists);
    }

    public static Result deletePlayList(Long playListId) {
        PlayListEntity playListEntity = Ebean.find(PlayListEntity.class, playListId);
        Ebean.delete(playListEntity);

        return ok();
    }

    private static Set<SongEntity> addExistingSongs(List<Object> fileIds, List<Object> cloudIds){
        Set<SongEntity> songs = new HashSet<SongEntity>();
        List<Map<String, Object>> fields = new ArrayList<Map<String, Object>>();
        List<SongEntity> songList = SongEntity.getSongsByMultipleFields(fields);

        if(songList != null){
            for(SongEntity songEntity : songList){
                fileIds.remove(songEntity.getFileId());
                cloudIds.remove(songEntity.getCloudId());
                songs.add(songEntity);
            }
        }

        return songs;
    }

    private static Set<SongEntity> addNewSongs(List<Object> fileIds, List<Object> cloudIds, UserEntity user){
        Set<SongEntity> songs = new HashSet<SongEntity>();

        for(int i = 0; i < fileIds.size(); i++){
            SongEntity songEntity = new SongEntity();
            songEntity.setUserEntity(user);
            songEntity.setFileId((String) fileIds.get(i));
            songEntity.setCloudId((Long) cloudIds.get(i));
            songEntity.setHasMetadata(false);
            songEntity.save();
            songs.add(songEntity);
        }

        return songs;
    }

}
