package controllers;

import app.BaseModelTest;
import com.avaje.ebean.Ebean;
import models.UserEntity;
import org.junit.Test;
import play.Logger;
import play.mvc.Result;
import play.test.FakeRequest;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertNull;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.route;
import static play.test.Helpers.status;

public class AuthorizationDropboxApiTest extends BaseModelTest {


    @Test
    public void testRemoveDropbox(){
        UserEntity dropboxUser = UserEntity.getUserByField("login", "dropbox");
        FakeRequest request = new FakeRequest("DELETE", "/dropbox")
                .withSession("userId", dropboxUser.getId().toString());

        Result result = route(request);
        assertThat(status(result)).isEqualTo(OK);
        Ebean.refresh(dropboxUser);
        assertNull(dropboxUser.getDropboxAccessKey());
        assertNull(dropboxUser.getDropboxUid());
        Logger.info("Remove Dropbox test done");
    }

}
