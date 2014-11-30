package controllers;

import app.BaseModelTest;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.UserEntity;
import org.junit.Test;
import play.Logger;
import play.mvc.Result;
import play.test.FakeRequest;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;
import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.CREATED;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.route;
import static play.test.Helpers.status;

/**
 * Created by alex on 10/1/14.
 */
public class AuthorizationApiTest extends BaseModelTest {

    @Test
    public void testLogin(){
        FakeRequest request = new FakeRequest("POST", "/api/login");
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("login", "dropbox");
        node.put("password", "123");
        request.withJsonBody(node);

        // Act
        Result result = route(request);
        assertThat(status(result)).isEqualTo(OK);
        Logger.info("Login test done");
    }

    @Test
    public void testLogout(){
        FakeRequest request = new FakeRequest("GET", "/api/logout");
        Result result = route(request);
        assertThat(status(result)).isEqualTo(OK);
        Logger.info("Logout test done");
    }

    @Test
    public void testRegisterUser(){
        FakeRequest request = new FakeRequest("POST", "/api/user");
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("login", "user_from_test");
        node.put("password", "Qwerty132");
        request.withJsonBody(node);

        Result result = route(request);
        assertThat(status(result)).isEqualTo(CREATED);
        assertNotNull(UserEntity.getUserByField("login", "user_from_test"));
        Logger.info("Register user test done");
    }

    @Test
    public void testDeleteUser(){
        UserEntity user = UserEntity.getUserByField("login", "user_from_test");
        //TODO session data names -> constants
        FakeRequest request = new FakeRequest("DELETE", "/api/user").withSession("userId", user.getId().toString());
        Result result = route(request);
        assertThat(status(result)).isEqualTo(OK);
        assertNull(UserEntity.getUserByField("login", "user_from_test"));
        Logger.info("Remove account test done");
    }

}
