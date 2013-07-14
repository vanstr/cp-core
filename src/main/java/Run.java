import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * UserEntity: vanstr
 * Date: 13.6.7
 * Time: 23:06
 * To change this template use File | Settings | File Templates.
 */
public class Run {

    // Dummy account
    // e-mail: cloud_player@inbox.lv
    // pass: cloudPlayer123


    public static void main(String[] args){

        // update user
         /*
        UserManager manager = new UserManager();
        UserEntity user = manager.getUserById(1);
        System.out.println(user.getLogin());



        user.setLogin("Alex");

        manager.updateUser(user);


        UserEntity user3 = manager.getUserById(1);
        System.out.println(user3.toString());

        manager.finalize();
        //*/


        // link account
        /*
        UserManager manager = new UserManager();
        UserEntity user = manager.getUserById(1);
        System.out.println(user.toString());
        manager.finalize();

        AuthorizationBean a = new AuthorizationBean();
        String link = a.getDropboxAuthLink(1L);

        System.out.println(link);

        UserManager manager2 = new UserManager();
        UserEntity user2 = manager2.getUserById(1);
        System.out.println(user2.toString());
        manager2.finalize();

        try {
            Thread.sleep(15000);
        } catch(InterruptedException e) {
        }

        AuthorizationBean b = new AuthorizationBean();
        b.retrieveDropboxAccessToken(1L);

        UserManager manager1 = new UserManager();
        UserEntity user1 = manager1.getUserById(1);
        System.out.println(user1.toString());
        manager1.finalize();
        //*/


        // get content of folger
        ContentBean contentBean = new ContentBean();
        List musicList = contentBean.getFiles("/", true, 1L);
        System.out.println(Arrays.toString(musicList.toArray()));

    }

}
