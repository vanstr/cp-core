package ejb;

import cloud.DriveFileFetcher;
import cloud.Dropbox;
import cloud.DropboxFileFetcher;
import cloud.GDrive;
import commons.FileFetcher;
import commons.SongMetadataPopulation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.SongEntity;
import persistence.UserEntity;
import persistence.utility.SongManager;
import persistence.utility.UserManager;
import structure.PlayList;
import structure.Song;
import structure.SongMetadata;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * UserEntity: vanstr
 * Date: 13.6.7
 * Time: 21:45
 * To change this template use File | Settings | File Templates.
 */

@Stateless
@Remote(ContentBeanRemote.class)
public class ContentBean implements ContentBeanRemote {

    final static Logger logger = LoggerFactory.getLogger(ContentBean.class);

    public String getFileSrc(Long userId, Integer cloudId, String fileId) {
        String file = null;
        UserManager manager = new UserManager();
        UserEntity user = manager.getUserById(userId);
        try {
            if (DROPBOX_CLOUD_ID.equals(cloudId)) {
                String accessTokenKey = user.getDropboxAccessKey();
                String accessTokenSecret = user.getDropboxAccessSecret();

                Dropbox drop = new Dropbox(accessTokenKey, accessTokenSecret);
                logger.info(fileId);
                file = drop.getFileLink(fileId);
            } else if (DRIVE_CLOUD_ID.equals(cloudId)) {
                GDrive gDrive = new GDrive(user.getDriveAccessToken(), user.getDriveRefreshToken());
                file = gDrive.getFileLink(fileId);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        manager.finalize();

        return file;

    }

    @Override
    public PlayList getPlayList(Long userId) {

        List<Song> data = getFiles("/", userId);
        PlayList playList = SongMetadataPopulation.populate(data, userId);

        return playList;
    }

    @Override
    public boolean saveSongMetadata(Song song, Long userId) {

        boolean res = false;

        UserEntity user = new UserEntity();
        user.setId(userId);

        // get song by id
        SongManager songManager = new SongManager();
        logger.info("pass1" + song + " " + song.getCloudId() + " " + song.getFileName());
        SongEntity songEntity = songManager.getSongByHash(user, song.getCloudId(), song.getFileName());

        if (songEntity == null) {
            // songEntity is empty, create new with metadata
            songEntity = new SongEntity();
            songEntity.setCloudId(song.getCloudId());
            songEntity.setFileName(song.getFileName());
            songEntity.setUser(user);
            songEntity = setMetadata(songEntity, song);
            res = songManager.addSong(songEntity);
        } else {
            // update metadata
            songEntity = setMetadata(songEntity, song);
            res = songManager.updateSong(songEntity);
        }

        songManager.finalize();

        return res;
    }

    private SongEntity setMetadata(SongEntity songEntity, Song song) {
        SongMetadata metadata = song.getMetadata();
        if (metadata != null) {
            songEntity.setMetadataTitle(metadata.getTitle());
            songEntity.setMetadataAlbum(metadata.getAlbum());
            songEntity.setMetadataArtist(metadata.getArtist());
            songEntity.setMetadataGenre(metadata.getGenre());
            songEntity.setMetadataYear(metadata.getYear());
        }
        return songEntity;
    }

    private List<Song> getFiles(String folderPath, Long userId) {

        FileFetcher dropboxFetcher = new DropboxFileFetcher(folderPath, userId);
        FileFetcher driveFetcher = new DriveFileFetcher(folderPath, userId);
        Thread dropboxThread = new Thread(dropboxFetcher);
        Thread driveThread = new Thread(driveFetcher);
        dropboxThread.start();
        driveThread.start();
        try {
            dropboxThread.join();
            driveThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<Song> files = new ArrayList<Song>();
        if (dropboxFetcher.getFiles() != null) {
            files.addAll(dropboxFetcher.getFiles());
        }
        if (driveFetcher.getFiles() != null) {
            files.addAll(driveFetcher.getFiles());
        }

        return files;
    }
}
