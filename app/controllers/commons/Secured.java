package controllers.commons;

import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

/**
 * Created by alex on 9/23/14.
 */
public class Secured extends Security.Authenticator {

    @Override
    public String getUsername(Http.Context context) {
        return context.session().get("userId");
    }

    @Override
    public Result onUnauthorized(Http.Context context) {
        return unauthorized();
    }
}
