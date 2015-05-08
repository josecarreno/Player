package pe.edu.upc.player;

import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Alumnos on 07/05/2015.
 */
public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    //media player
    private MediaPlayer player;
    //lista de canciones
    private ArrayList<Song> songs;
    //posicion actual
    private int songPosn;
    private final IBinder musicBind = new MusicBinder();

    public void onCreate() {
        super.onCreate();
        player = new MediaPlayer();
        songPosn = 0;
        initMusicPlayer();
    }
    public void initMusicPlayer() {
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        // setear la clase para cuando la instancia del reproductor este preparada
        player.setOnPreparedListener(this);
        // setear la clase para cuando se hay completado el playback de la cancion
        player.setOnCompletionListener(this);
        // setea la clase para cuando haya un error
        player.setOnErrorListener(this);

    }

    public void setList(ArrayList<Song> theSongs){
        songs = theSongs;
    }

    public void playSong(){
        player.reset();
        // obtener cancion
        Song playSong = songs.get(songPosn);
        // obtener id
        long currSong = playSong.getId();
        //setear uri
        Uri trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                currSong);
        try {
            player.setDataSource(getApplicationContext(), trackUri);
        }
        catch(Exception e) {
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
        player.prepareAsync();
    }
    public void setSong(int songIndex){
        songPosn=songIndex;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent){
        player.stop();
        player.release();
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //Iniciar playback
        mp.start();
    }

    //clase anidada
    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }
}
