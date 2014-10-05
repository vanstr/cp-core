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

import static junit.framework.TestCase.assertNotNull;
import static org.fest.assertions.Assertions.assertThat;
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

        SongEntity song2 = new SongEntity(originUserEntity, 2L, "QWERTY123", "song2.mp3", false);
        song2.save();

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
        child3.put("cloudId", 2);

        array.add(child1);
        array.add(child2);
        array.add(child3);

        node.put("songs", array);

        FakeRequest request = new FakeRequest("POST", "/api/addPlayList")
                .withSession("user", "1");
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

    }
}
