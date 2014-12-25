package controllers;

import app.BaseModelTest;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import commons.PasswordService;
import models.UserEntity;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import play.test.FakeRequest;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;
import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.*;
import static play.test.Helpers.route;
import static play.test.Helpers.status;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AuthorizationApiTest extends BaseModelTest {

    private String tmpUserPwd = "Qwerty132";
    private String tmpUserNewPwd = "New_Passw0rd#";

    @Test
    public void test1Login(){
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
    public void test2Logout(){
        FakeRequest request = new FakeRequest("GET", "/api/logout").withSession("userId", USER_ID);;
        Result result = route(request);
        assertThat(status(result)).isEqualTo(OK);
        Logger.info("Logout test done");
    }

    @Test
    public void test3RegisterUser(){
        FakeRequest request = new FakeRequest("POST", "/api/register");
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("login", "user_from_test");

        node.put("password", tmpUserPwd);
        request.withJsonBody(node);

        Result result = route(request);
        assertThat(status(result)).isEqualTo(CREATED);
        assertNotNull(UserEntity.getUserByField("login", "user_from_test"));
        Logger.info("Register user test done");
    }

    @Test
    public void test4changePasswordWrongOldPwd(){
        UserEntity user = UserEntity.getUserByField("login", "user_from_test");

        ObjectNode json = Json.newObject();
        json.put("password", "SomeWr0ngPwd");
        json.put("new_password", tmpUserNewPwd);

        FakeRequest request = new FakeRequest("POST", "/api/user/password")
                .withJsonBody(json)
                .withSession("userId", user.getId().toString());
        Result result = route(request);
        assertThat(status(result)).isEqualTo(BAD_REQUEST);

        Logger.info("changePasswordWrongOldPwd test done");
    }

    @Test
    public void test5changePassword(){
        UserEntity user = UserEntity.getUserByField("login", "user_from_test");

        ObjectNode json = Json.newObject();
        json.put("password", tmpUserPwd);
        json.put("new_password", tmpUserNewPwd);

        FakeRequest request = new FakeRequest("POST", "/api/user/password")
                .withJsonBody(json)
                .withSession("userId", user.getId().toString());
        Result result = route(request);
        assertThat(status(result)).isEqualTo(OK);

        UserEntity refreshedUser = UserEntity.getUserByField("login", "user_from_test");

        assertEquals(refreshedUser.getPassword(), PasswordService.encrypt(tmpUserNewPwd));
        Logger.info("changePassword test done");
    }

    @Test
    public void test6DeleteUser(){
        UserEntity user = UserEntity.getUserByField("login", "user_from_test");

        FakeRequest request = new FakeRequest("DELETE", "/api/user").withSession("userId", user.getId().toString());
        Result result = route(request);
        assertThat(status(result)).isEqualTo(OK);
        assertNull(UserEntity.getUserByField("login", "user_from_test"));
        Logger.info("Remove account test done");
    }


}
