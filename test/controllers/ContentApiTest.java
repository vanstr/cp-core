package controllers;

import app.BaseModelTest;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.PlayListEntity;
import models.SongEntity;
import org.junit.Test;
import play.Logger;
import play.mvc.Result;
import play.test.FakeRequest;

import java.util.Arrays;

import static commons.SystemProperty.DRIVE_CLOUD_ID;
import static commons.SystemProperty.DROPBOX_CLOUD_ID;
import static junit.framework.TestCase.assertNotNull;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.*;


/**
 * Created by alex on 10/1/14.
 */
public class ContentApiTest extends BaseModelTest {

    private static final String USER_ID = originUserEntity.getId().toString();
    private static PlayListEntity testPlayListEntity = null;
    private static PlayListEntity playListWithOneSong = null;
    @Test
    public void testGetFileSrc(){
        FakeRequest request = new FakeRequest("GET", "/api/link?cloudId="+DROPBOX_CLOUD_ID+"&fileId=/JUnit/music.mp3")
                .withSession("userId", USER_ID);
        Result result = route(request);
        assertNotNull(contentAsString(result));
        Logger.debug(contentAsString(result));
        Logger.info("Get file src test done");
    }

    @Test
    public void testGetPlayList(){
        FakeRequest request = new FakeRequest("GET", "/api/playList")
                .withSession("userId", USER_ID);
        Result result = route(request);
        assertNotNull(contentAsString(result));
        Logger.debug(contentAsString(result));
        Logger.info("Get playlist test done");
    }

    @Test
    public void testAddPlayList(){
        SongEntity song1 = new SongEntity(originUserEntity, DROPBOX_CLOUD_ID, "/songs/song2.mp3", "song2.mp3");
        song1.save();

        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("id", (byte[]) null);
        node.put("name", "playlist1");
        ObjectNode child1 = JsonNodeFactory.instance.objectNode();
        child1.put("fileId", "/songs/song2.mp3");
        child1.put("cloudId", DROPBOX_CLOUD_ID);
        ArrayNode array = JsonNodeFactory.instance.arrayNode();

        ObjectNode child2 = JsonNodeFactory.instance.objectNode();
        child2.put("fileId", "Shots.mp3");
        child2.put("cloudId", DROPBOX_CLOUD_ID);

        ObjectNode child3 = JsonNodeFactory.instance.objectNode();
        child3.put("fileId", "QWERTY123");
        child3.put("fileName", "QWERTY123");
        child3.put("cloudId", DRIVE_CLOUD_ID);
        child3.put("url", "QWERTY123");

        array.add(child1);
        array.add(child2);
        array.add(child3);

        node.put("songs", array);

        FakeRequest request = new FakeRequest("POST", "/api/playList")
                .withSession("userId", originUserEntity.getId().toString());
        request.withJsonBody(node);

        Result result = route(request);
        assertNotNull(contentAsString(result));
        Logger.debug(contentAsString(result));
        testPlayListEntity = PlayListEntity.getPlayListById(Long.parseLong(contentAsString(result)));
        assertThat(status(result)).isEqualTo(OK);
        assertNotNull(PlayListEntity.find.all());
        assertTrue(testPlayListEntity.getSongs().size() == 3);

        Logger.info("Add playlist test done");
    }

    @Test
    public void testGetPlayListById(){
        FakeRequest request = new FakeRequest("GET", "/api/playList/" + testPlayListEntity.getId())
                .withSession("userId", USER_ID);
        Result result = route(request);

        assertThat(status(result)).isEqualTo(OK);
        assertNotNull(contentAsString(result));
        Logger.debug(contentAsString(result));
        Logger.info("Get playlist by id test done");
    }

    @Test
    public void testGetPlayLists(){
        PlayListEntity playListEntity = new PlayListEntity();
        playListEntity.setUserEntity(originUserEntity);
        playListEntity.setName("playlist2");
        playListEntity.addSongEntities(SongEntity.find.all());
        playListEntity.save();
        FakeRequest request = new FakeRequest("GET", "/api/playLists")
                .withSession("userId", USER_ID);
        Result result = route(request);

        assertThat(status(result)).isEqualTo(OK);
        assertNotNull(contentAsString(result));
        Logger.debug(contentAsString(result));
        Logger.info("Get playlists test done");
    }

    @Test
    public void testSaveSongMetadata(){
        FakeRequest request = new FakeRequest("POST", "/api/saveSongMetadata")
                .withSession("userId", USER_ID);
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("fileId", "/songs/song1.mp3");
        node.put("fileName", "song1.mp3");
        node.put("cloudId", DROPBOX_CLOUD_ID);
        ObjectNode metadataNode = JsonNodeFactory.instance.objectNode();
        metadataNode.put("title", "Song 1");
        metadataNode.put("artist", "Artist 1");
        metadataNode.put("album", "Album 1");
        metadataNode.put("lengthSeconds", 100);
        metadataNode.put("year", "1");
        metadataNode.put("genre", "Genre 1");
        node.put("metadata", metadataNode);
        node.put("urlExpiresTime", 0L);
        node.put("url", "mockUrl");

        request.withJsonBody(node);
        Result result = route(request);
        assertThat(status(result)).isEqualTo(OK);
        SongEntity songEntity = SongEntity.getSongByHash(originUserEntity, DROPBOX_CLOUD_ID, "/songs/song1.mp3");

        assertThat(songEntity.getMetadataTitle()).isEqualTo("Song 1");
        assertThat(songEntity.getMetadataArtist()).isEqualTo("Artist 1");
        assertThat(songEntity.getMetadataAlbum()).isEqualTo("Album 1");
        assertThat(songEntity.getMetadataLengthSeconds()).isEqualTo(100);
        assertThat(songEntity.getMetadataYear()).isEqualTo("1");
        assertThat(songEntity.getMetadataGenre()).isEqualTo("Genre 1");
        Logger.info("Save metadata test done");
    }

    @Test
    public void testAddSongToPlayList(){
        playListWithOneSong = new PlayListEntity();
        playListWithOneSong.setName("Test PLaylist");
        playListWithOneSong.setUserEntity(originUserEntity);
        playListWithOneSong.save();
        FakeRequest request = new FakeRequest("POST", "/api/playListSong")
                .withSession("userId", USER_ID);
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("playListId", playListWithOneSong.getId());
        ArrayNode songArray = JsonNodeFactory.instance.arrayNode();
        ObjectNode songNode = JsonNodeFactory.instance.objectNode();
        songNode.put("fileId", "/songs/my_song1.mp3");
        songNode.put("cloudId", DROPBOX_CLOUD_ID);
        songArray.add(songNode);
        node.put("songs", songArray);
        request.withJsonBody(node);
        Result result = route(request);
        playListWithOneSong.refresh();
        assertThat(status(result)).isEqualTo(OK);
        assertThat(playListWithOneSong.getSongs().size() > 0).isTrue();
        Logger.info("Add song to playlist test done");
    }

    @Test
    public void testRemoveSongFromPlayList(){
        PlayListEntity playListEntity = new PlayListEntity();
        SongEntity song1 = new SongEntity();
        song1.setUser(originUserEntity);
        song1.setCloudId(DROPBOX_CLOUD_ID);
        song1.setFileId("songToRemoveFromPlayList.mp3");
        song1.setFileName("songToRemoveFromPlayList.mp3");
        song1.save();
        playListEntity.setName("Test PLaylist");
        playListEntity.setUserEntity(originUserEntity);
        playListEntity.addSongEntities(Arrays.asList(song1));
        playListEntity.save();

        FakeRequest request = new FakeRequest("DELETE", "/api/playListSong")
                .withSession("userId", USER_ID);
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("playListId", playListEntity.getId());
        node.put("fileId", "songToRemoveFromPlayList.mp3");
        node.put("cloudId", DROPBOX_CLOUD_ID);
        request.withJsonBody(node);
        Result result = route(request);
        playListEntity.refresh();
        assertThat(status(result)).isEqualTo(OK);
        assertThat(playListEntity.getSongs().size() == 0).isTrue();
        Logger.info("Remove song from playlist test done");
    }

    @Test
    public void testDeletePlayList(){
        PlayListEntity playListEntity = new PlayListEntity();
        playListEntity.setName("name");
        playListEntity.setUserEntity(originUserEntity);
        playListEntity.save();
        Long playListId = playListEntity.getId();
        FakeRequest request = new FakeRequest("DELETE", "/api/playList/" + playListId)
                .withSession("userId", originUserEntity.getId().toString());

        Result result = route(request);
        assertThat(status(result)).isEqualTo(OK);
        assertNull(PlayListEntity.getPlayListById(playListId));
        Logger.info("Delete playlist test done");
    }

}
