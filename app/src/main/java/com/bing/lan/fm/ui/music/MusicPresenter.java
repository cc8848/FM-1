package com.bing.lan.fm.ui.music;

import android.content.Intent;

import com.bing.lan.comm.base.mvp.activity.BaseActivityPresenter;
import com.bing.lan.comm.utils.musicplay.Music;
import com.bing.lan.comm.utils.musicplay.MusicPlayer;
import com.bing.lan.fm.ui.album.bean.AlbumResultBean;
import com.bing.lan.fm.ui.album.bean.TracksInfoBean;
import com.bing.lan.fm.ui.music.bean.PlayResult;

import java.util.ArrayList;
import java.util.List;

import static com.bing.lan.fm.ui.music.MusicActivity.ALBUM_ID;
import static com.bing.lan.fm.ui.music.MusicActivity.ALBUM_PLAYLIST;
import static com.bing.lan.fm.ui.music.MusicActivity.ALBUM_POSITION;

/**
 * @author 蓝兵
 * @time 2017/2/6  19:11
 */
public class MusicPresenter
        extends BaseActivityPresenter<IMusicContract.IMusicView, IMusicContract.IMusicModule>
        implements IMusicContract.IMusicPresenter {

    public static final int LOAD_TRACK = 0;
    public static final int LOAD_ALBUM = 10;
    private List<Music> mArrayList;
    private List<TracksInfoBean> mTrackInfos;
    private int mFirstPlayPos;

    private long mAlbumId;
    private long mCurrentTrackId;

    @Override
    public void onStart(Object... params) {
        mView.showDialog("正在获取音乐资源..");
        initData((Intent) params[0]);
    }

    private void initData(Intent intent) {
        //album进来
        mTrackInfos = (ArrayList<TracksInfoBean>) intent.getSerializableExtra(ALBUM_PLAYLIST);
        mFirstPlayPos = intent.getIntExtra(ALBUM_POSITION, 0);

        //main进来
        mAlbumId = intent.getLongExtra(ALBUM_ID, 0);

        // updateCurrentTrackId();

        //album进来
        if (mTrackInfos != null) {
            handTrackInfo(mTrackInfos);
            mView.gotoPosition(mFirstPlayPos);
            requestData(LOAD_TRACK, mCurrentTrackId, mAlbumId);//???????
        } else if (mAlbumId != 0) {
            // 不在播放 main,查询数据库(能拿到数据)进来
            requestData(LOAD_ALBUM, mAlbumId);
        } else {
            // 1.正在播放 main进来
            // 2.第一次安装,查询数据库(不能拿到数据) main 进来

            Music music = MusicPlayer.getCurrentPlayMusic();
            if (music != null) {
                mAlbumId = music.albumId;
                mCurrentTrackId = music.trackId;
            } else {
                mAlbumId = 270535;
                mCurrentTrackId = 31353083;
            }

            requestData(LOAD_TRACK, mCurrentTrackId, mAlbumId);//???????
        }

        // requestData(LOAD_TRACK, mCurrentTrackId, mAlbumId);//???????
    }

    public void handTrackInfo(List<TracksInfoBean> tracksInfoBeen) {

        TracksInfoBean tracksInfoBean = tracksInfoBeen.get(mFirstPlayPos);
        mCurrentTrackId = tracksInfoBean.getTrackId();
        mArrayList = new ArrayList<>();
        for (TracksInfoBean infoBean : tracksInfoBeen) {
            Music music = new Music();
            music.url = infoBean.getPlayUrl32();
            music.albumId = infoBean.getAlbumId();
            mAlbumId = infoBean.getAlbumId();
            music.duration = infoBean.getDuration() * 1000;
            music.coverSmall = infoBean.coverSmall;
            music.coverMiddle = infoBean.coverMiddle;
            music.coverLarge = infoBean.coverLarge;
            music.title = infoBean.title;
            music.trackId = infoBean.trackId;
            mArrayList.add(music);
        }

        mView.setMusicPlaylist(mArrayList);
    }

    @Override
    public void requestData(int action, Object... parameter) {
        switch (action) {
            case LOAD_TRACK:
                mModule.requestData(action, this, parameter);
                break;
            case LOAD_ALBUM:
                mModule.requestData(action, this, parameter);
                break;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onSuccess(int action, Object data) {

        switch (action) {

            case LOAD_TRACK:

                PlayResult playResult = (PlayResult) data;
                mView.updateTrackInfo(playResult.getTrackInfo());

                // List<Music> playlist1 = MusicPlayer.getPlaylist();
                //
                // if (playlist1 != null && playlist1.size() < 1) {
                //
                //     Music music = new Music(playResult.getTrackInfo().getPlayUrl32());
                //     if (music != null) {
                //         music.albumId = mAlbumId;
                //         music.trackId = mCurrentTrackId;
                //     }
                //
                //     ArrayList<Music> musics = new ArrayList<>();
                //     musics.add(music);
                //     mView.setMusicPlaylist(musics);
                // }
                //
                // List<Music> playlist = MusicPlayer.getPlaylist();
                // log.d("onSuccess(): playlist.size()" + playlist.size());
                //
                // if (!MusicPlayer.isPlaying()) {
                //     mView.gotoPosition(0);
                // }

                break;
            case LOAD_ALBUM:
                // TODO: 2017/3/8 处理数据
                List<TracksInfoBean> tracksInfoBeen = ((AlbumResultBean.DataBean) data).getTracks().getList();
                handTrackInfo(tracksInfoBeen);
                //播放第一首歌
                mCurrentTrackId = mTrackInfos.get(0).getTrackId();
                //播放音乐
                mView.gotoPosition(mFirstPlayPos);

                requestData(LOAD_TRACK, mCurrentTrackId, mAlbumId);//???????
                break;
        }
    }

    @Override
    public void onError(int action, Throwable e) {
        super.onError(action, e);
        mView.dismissDialog();
    }

    @Override
    public void onCompleted(int action) {
        super.onCompleted(action);

        mView.dismissDialog();
    }
}
