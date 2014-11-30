package controllers;

import app.BaseModelTest;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.PlayListEntity;
import models.SongEntity;
import models.UserEntity;
import org.junit.Test;
import play.Logger;
import play.mvc.Result;
import play.test.FakeRequest;

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

    private static PlayListEntity testPlayListEntity = null;
    @Test
    public void testGetFileSrc(){
        FakeRequest request = new FakeRequest("GET", "/api/link?cloudId=1&fileId=/JUnit/music.mp3")
                .withSession("userId", "1");
        Result result = route(request);
        assertNotNull(contentAsString(result));
        Logger.debug(contentAsString(result));
        Logger.info("Get file src test done");
    }

    @Test
    public void testGetPlayList(){
        FakeRequest request = new FakeRequest("GET", "/api/playList")
                .withSession("userId", "1");
        Result result = route(request);
        assertNotNull(contentAsString(result));
        Logger.debug(contentAsString(result));
        Logger.info("Get playlist test done");
    }

    @Test
    public void testAddPlayList(){
        SongEntity song1 = new SongEntity(originUserEntity, 1L, "/songs/song2.mp3", "song2.mp3");
        song1.save();

        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("id", (byte[]) null);
        node.put("name", "playlist1");
        ObjectNode child1 = JsonNodeFactory.instance.objectNode();
        child1.put("fileId", "/songs/song2.mp3");
        child1.put("cloudId", 1);
        ArrayNode array = JsonNodeFactory.instance.arrayNode();

        ObjectNode child2 = JsonNodeFactory.instance.objectNode();
        child2.put("fileId", "Shots.mp3");
        child2.put("cloudId", 1);

        ObjectNode child3 = JsonNodeFactory.instance.objectNode();
        child3.put("fileId", "QWERTY123");
        child3.put("fileName", "QWERTY123");
        child3.put("cloudId", 2);
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
                .withSession("userId", originUserEntity.getId().toString());
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
                .withSession("userId", originUserEntity.getId().toString());
        Result result = route(request);

        assertThat(status(result)).isEqualTo(OK);
        assertNotNull(contentAsString(result));
        Logger.debug(contentAsString(result));
        Logger.info("Get playlists test done");
    }

    @Test
    public void testSaveSongMetadata(){
        FakeRequest request = new FakeRequest("POST", "/api/saveSongMetadata")
                .withSession("userId", "1");
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("fileId", "/songs/song1.mp3");
        node.put("fileName", "song1.mp3");
        node.put("cloudId", 1L);
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
        SongEntity songEntity = SongEntity.getSongByHash(UserEntity.getUserById(1L), 1L, "/songs/song1.mp3");

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
        testPlayListEntity = new PlayListEntity();
        testPlayListEntity.setName("Test PLaylist");
        testPlayListEntity.setUserEntity(originUserEntity);
        testPlayListEntity.save();
        FakeRequest request = new FakeRequest("POST", "/api/playListSong")
                .withSession("userId", "1");
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("playListId", testPlayListEntity.getId());
        ArrayNode songArray = JsonNodeFactory.instance.arrayNode();
        ObjectNode songNode = JsonNodeFactory.instance.objectNode();
        songNode.put("fileId", "/songs/my_song1.mp3");
        songNode.put("cloudId", 1L);
        songArray.add(songNode);
        node.put("songs", songArray);
        request.withJsonBody(node);
        Result result = route(request);
        assertThat(status(result)).isEqualTo(OK);
        assertThat(testPlayListEntity.getSongs().size() > 0);
        Logger.info("Add song to playlist test done");
    }

    @Test
    public void testRemoveSongFromPlayList(){
        FakeRequest request = new FakeRequest("DELETE", "/api/playListSong")
                .withSession("userId", "1");
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("playListId", testPlayListEntity.getId());
        node.put("fileId", "/songs/my_song1.mp3");
        node.put("cloudId", 1L);
        request.withJsonBody(node);
        Result result = route(request);
        assertThat(status(result)).isEqualTo(OK);
        assertThat(testPlayListEntity.getSongs().size() == 0);
        Logger.info("Remove song from playlist test done");
    }

    @Test
    public void testDeletePlayList(){
        testPlayListEntity = new PlayListEntity();
        testPlayListEntity.setName("name");
        testPlayListEntity.setUserEntity(originUserEntity);
        testPlayListEntity.save();
        Long playListId = testPlayListEntity.getId();
        FakeRequest request = new FakeRequest("DELETE", "/api/playList/" + playListId)
                .withSession("userId", originUserEntity.getId().toString());

        Result result = route(request);
        assertThat(status(result)).isEqualTo(OK);
        assertNull(PlayListEntity.getPlayListById(playListId));
        Logger.info("Delete playlist test done");
    }

}
