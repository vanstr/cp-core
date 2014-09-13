package controllers;


import models.UserEntity;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;


public class Application extends Controller {

    public static Result index() {
      UserEntity user = new UserEntity();
      user.save();
        UserEntity song = UserEntity.find.byId(1L);
        Logger.debug("my user: " + song);

        return ok(index.render("Your new application is ready."));
        // test commit
    }

}
