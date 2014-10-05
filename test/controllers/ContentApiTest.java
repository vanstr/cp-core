package controllers;

import app.BaseModelTest;
import com.avaje.ebean.Ebean;
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
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.route;
import static play.test.Helpers.status;

/**
 * Created by alex on 10/1/14.
 */
public class ContentApiTest extends BaseModelTest {

    private static PlayListEntity testPlayListEntity = null;
    @Test
    public void testGetFileSrc(){
        FakeRequest request = new FakeRequest("GET", "/api/getLink?cloudId=1&fileId=/JUnit/music.mp3")
                .withSession("user", "1");
        Result result = route(request);
        assertNotNull(contentAsString(result));
        Logger.info("Get file src test done");
    }

    @Test
    public void testGetPlayList(){
        FakeRequest request = new FakeRequest("GET", "/api/getPlayList")
                .withSession("user", "1");
        Result result = route(request);
        assertNotNull(contentAsString(result));
        Logger.info("Get playlist test done");
    }

    @Test
    public void testAddPlayList(){
        SongEntity song1 = new SongEntity(originUserEntity, 1L, "/songs/song1.mp3", "song1.mp3", false);
        song1.save();

        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("id", (byte[]) null);
        node.put("name", "playlist1");
        ObjectNode child1 = JsonNodeFactory.instance.objectNode();
        child1.put("fileId", "/songs/song1.mp3");
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
        child3.put("hasMetadata", false);

        array.add(child1);
        array.add(child2);
        array.add(child3);

        node.put("songs", array);

        FakeRequest request = new FakeRequest("POST", "/api/addPlayList")
                .withSession("user", originUserEntity.getId().toString());
        request.withJsonBody(node);

        Result result = route(request);
        assertNotNull(contentAsString(result));
        testPlayListEntity = PlayListEntity.getPlayListById(Long.parseLong(contentAsString(result)));
        assertThat(status(result)).isEqualTo(OK);
        assertNotNull(PlayListEntity.find.all());
        assertTrue(PlayListEntity.find.all().get(0).getSongs().size() == 3);

        Logger.info("Add playlist test done");
    }

    @Test
    public void testGetPlayListById(){
        FakeRequest request = new FakeRequest("GET", "/api/getPlayListById?playListId=" + testPlayListEntity.getId())
                .withSession("user", "1");
        Result result = route(request);
        assertNotNull(contentAsString(result));
        Logger.info("Get playlist by id test done");
    }

    @Test
    public void testGetPlayLists(){
        FakeRequest request = new FakeRequest("GET", "/api/getPlayLists")
                .withSession("user", "1");
        Result result = route(request);
        assertNotNull(contentAsString(result));
        Logger.info("Get playlists test done");
    }

    @Test
    public void testSaveSongMetadata(){
        FakeRequest request = new FakeRequest("POST", "/api/saveSongMetadata")
                .withSession("user", "1");
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
    public void testDeletePlayList(){
        testPlayListEntity = new PlayListEntity();
        testPlayListEntity.setName("name");
        testPlayListEntity.setUserEntity(originUserEntity);
        testPlayListEntity.save();
        Long playListId = testPlayListEntity.getId();
        FakeRequest request = new FakeRequest("DELETE", "/api/deletePlayList?playListId=" + playListId)
                .withSession("user", originUserEntity.getId().toString());

        Result result = route(request);
        assertThat(status(result)).isEqualTo(OK);
        assertNull(PlayListEntity.getPlayListById(playListId));
        Logger.info("Delete playlist test done");
    }

    @Test
    public void testRemoveDropbox(){
        UserEntity dropboxUser = UserEntity.getUserByField("login", "dropbox");
        FakeRequest request = new FakeRequest("GET", "/removeDropbox")
                .withSession("user", dropboxUser.getId().toString());

        Result result = route(request);
        assertThat(status(result)).isEqualTo(OK);
        Ebean.refresh(dropboxUser);
        assertNull(dropboxUser.getDropboxAccessKey());
        assertNull(dropboxUser.getDropboxUid());
        Logger.info("Remove Dropbox test done");
    }

    @Test
    public void testRemoveGDrive(){
        UserEntity gDriveuser = UserEntity.getUserByField("login", "gdrive");
        FakeRequest request = new FakeRequest("GET", "/removeDrive")
                .withSession("user", gDriveuser.getId().toString());

        Result result = route(request);
        assertThat(status(result)).isEqualTo(OK);
        Ebean.refresh(gDriveuser);
        assertNull(gDriveuser.getDriveAccessToken());
        assertNull(gDriveuser.getDriveRefreshToken());
        assertNull(gDriveuser.getGoogleEmail());
        Logger.info("Remove GDrive test done");
    }
}
