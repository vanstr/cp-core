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

public class AuthorizationGDriveApiTest extends BaseModelTest {



    @Test
    public void testRemoveGDrive(){
        UserEntity gDriveuser = UserEntity.getUserByField("login", "gdrive");
        FakeRequest request = new FakeRequest("DELETE", "/drive")
                .withSession("userId", gDriveuser.getId().toString());

        Result result = route(request);
        assertThat(status(result)).isEqualTo(OK);
        Ebean.refresh(gDriveuser);
        assertNull(gDriveuser.getDriveAccessToken());
        assertNull(gDriveuser.getDriveRefreshToken());
        assertNull(gDriveuser.getGoogleEmail());
        Logger.info("Remove GDrive test done");
    }

}
