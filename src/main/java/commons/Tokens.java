package commons;

/**
 * Created with IntelliJ IDEA.
 * User: vanstr
 * Date: 13.14.7
 * Time: 10:16
 * Just two strings -- a "key" and a "secret". Used by OAuth in several
 * places (app key/secret, request token/secret, access token/secret).
 */
public class Tokens {

    /**
     * The "key" portion of the pair.  For example, the "consumer key",
     * "request token", or "access token".  Will never contain the "|"
     * character.
     */
    public final String key;

    /**
     * The "secret" portion of the pair.  For example, the "consumer secret",
     * "request token secret", or "access token secret".
     */
    public final String secret;

    /**
     * @param key
     * @param secret
     * @throws IllegalArgumentException if key or secret is null or invalid.
     */
    public Tokens(String key, String secret) {
        if (key == null)
            throw new IllegalArgumentException("'key' must be non-null");
        if (key.contains("|"))
            throw new IllegalArgumentException("'key' must not contain a \"|\" character: \"" + key + "\"");
        if (secret == null)
            throw new IllegalArgumentException("'secret' must be non-null");

        this.key = key;
        this.secret = secret;
    }



    @Override
    public String toString() {
        return "{key=\"" + key + "\", secret=\"" + secret + "\"}";
    }
}