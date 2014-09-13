package controllers;


import models.UserEntity;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;


public class Application extends Controller {

    public static Result index() {
        UserEntity song = UserEntity.find.byId(1l);
        Logger.debug("my user: " + song);
        return ok(index.render("Your new application is ready."));
    }

}
