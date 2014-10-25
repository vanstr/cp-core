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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by imi on 13.09.2014..
 */
@Security.Authenticated(Secured.class)
public class ContentApi extends BaseController {

    public static Result getFileSrc(Long cloudId, String fileId) {
        String file = null;
        try {
            UserEntity userEntity = UserEntity.getUserById(Long.parseLong(session("userId")));
            if (SystemProperty.DROPBOX_CLOUD_ID.equals(cloudId)) {
                String accessTokenKey = userEntity.getDropboxAccessKey();
                Dropbox drop = new Dropbox(accessTokenKey);

                Logger.info(fileId);
                file = drop.getFileLink(fileId);
            } else if (SystemProperty.DRIVE_CLOUD_ID.equals(cloudId)) {
                GDrive gDrive = new GDrive(userEntity.getDriveAccessToken(), userEntity.getDriveRefreshToken());
                file = gDrive.getFileLink(fileId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return returnInJsonOk(file);
    }

    public static Result getPlayList() {
        Long userId = Long.parseLong(session("userId"));
        List<structure.Song> data = getFiles("/", userId);
        PlayList playList = SongMetadataPopulation.populate(data, userId);

        return returnInJsonOk(playList);
    }

    public static Result getPlayListById(Long playListId) {
        Map<String, Object> fields = new HashMap<String, Object>();
        fields.put("id", playListId);
        fields.put("user_id", Long.parseLong(session("userId")));
        List<PlayListEntity> list = PlayListEntity.getPlayListsByFields(fields);

        PlayList playList = null;
        if(list != null && !list.isEmpty()) {
            PlayListEntity playListEntity = list.get(0);
            playList = new PlayList(playListId, playListEntity.getName());
            for (SongEntity songEntity : playListEntity.getSongs()) {
                playList.add(new Song(songEntity));
            }
        }

        return returnInJsonOk(playList);
    }

    public static Result saveSongMetadata() {
        JsonNode songNode = request().body().asJson();
        Song song = Json.fromJson(songNode, Song.class);
        Long userId = Long.parseLong(session("userId"));
        UserEntity userEntity = UserEntity.getUserById(userId);

        // get song by id
        Logger.debug("name: " + song.getFileName() + "id: " + song.getFileId() + " cId: " + song.getCloudId() + "userId" + userEntity.getId());
        SongEntity songEntity = SongEntity.getSongByHash(userEntity, song.getCloudId(), song.getFileId());

        if (songEntity == null) {
            Logger.debug("Create new SongEntry with metadata");
            songEntity = new SongEntity(song);
            songEntity.setUser(userEntity);
            songEntity.save();
        } else {
            Logger.debug("Update metadata of existing new SongEntry");
            songEntity.setMetadata(song);
            songEntity.update();
        }

        return returnInJsonOk(songEntity.getId());
    }

    private static List<Song> getFiles(String folderPath, Long userId) {
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
        UserEntity userEntity = UserEntity.getUserById(userId);
        userEntity.setDriveAccessToken(null);
        userEntity.setDriveRefreshToken(null);
        userEntity.setGoogleEmail(null);
        userEntity.setDriveTokenExpires(null);
        userEntity.update();
        return ok();
    }

    public static Result removeDropbox(){
        Long userId = Long.parseLong(session("userId"));
        UserEntity userEntity = UserEntity.getUserById(userId);
        userEntity.setDropboxAccessKey(null);
        userEntity.setDropboxUid(null);
        userEntity.update();
        return ok();
    }

    public static Result addPlayList() {
        JsonNode songNode = request().body().asJson();
        PlayList playList = Json.fromJson(songNode, PlayList.class);
        Long userId = Long.parseLong(session("userId"));
        UserEntity user = UserEntity.getUserById(userId);

        Set<SongEntity> songs = new HashSet<SongEntity>();
        if(playList.getSongs() != null){
            List<Song> songsToAdd = new ArrayList<Song>(playList.getSongs());

            songs.addAll(addExistingSongs(songsToAdd, user));
            songs.addAll(addNewSongs(songsToAdd, user));
        }

        PlayListEntity playListEntity = new PlayListEntity();
        playListEntity.setName(playList.getName());
        playListEntity.setUserEntity(user);
        playListEntity.setCreated(new Timestamp(System.currentTimeMillis()));
        playListEntity.setUpdated(new Timestamp(System.currentTimeMillis()));
        for(SongEntity songEntity : songs) {
            playListEntity.addSongEntity(songEntity);
        }

        playListEntity.save();

        return returnInJsonOk(playListEntity.getId());
    }

    public static Result getPlayLists() {
        Long userId = Long.parseLong(session("userId"));
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
        Map<String, Object> fields = new HashMap<String, Object>();
        fields.put("id", playListId);
        fields.put("user_id", Long.parseLong(session("userId")));
        List<PlayListEntity> list = PlayListEntity.getPlayListsByFields(fields);
        if(fields != null && !fields.isEmpty() && !list.isEmpty()) {
            PlayListEntity playListEntity = list.get(0);
            Ebean.delete(playListEntity);
        } else {
            return badRequest();
        }

        return ok();
    }

    private static Set<SongEntity> addExistingSongs(List<Song> songList, UserEntity user){
        Set<SongEntity> songs = new HashSet<SongEntity>();
        Iterator<Song> iterator = songList.iterator();
        while(iterator.hasNext()){
            Song currentSong = iterator.next();
            SongEntity songEntity = SongEntity.getSongByHash(user, currentSong.getCloudId(),
                    currentSong.getFileId());
            if(songEntity != null){
                songs.add(songEntity);
                iterator.remove();
            }
        }

        return songs;
    }

    private static Set<SongEntity> addNewSongs(List<Song> songList, UserEntity user){
        Set<SongEntity> songs = new HashSet<SongEntity>();

        for(int i = 0; i < songList.size(); i++){
            SongEntity songEntity = new SongEntity(songList.get(i));
            songEntity.setUser(user);
            songEntity.save();
            songs.add(songEntity);
        }

        return songs;
    }

}
