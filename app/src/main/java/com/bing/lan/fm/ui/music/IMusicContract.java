package com.bing.lan.fm.ui.music;

import com.bing.lan.comm.base.mvp.activity.IBaseActivityContract;
import com.bing.lan.comm.utils.musicplay.Music;
import com.bing.lan.fm.ui.music.bean.TrackInfoBean;

import java.util.List;

public interface IMusicContract {

    interface IMusicView
            extends IBaseActivityContract.IBaseActivityView<IMusicPresenter> {

        void updateTrackInfo(TrackInfoBean trackInfoBean);

        // void updatePlaylist(List<Music> musicList);

        // void updatePlayPosition(int firstPlayPos);

          void playOrPause() ;

          void gotoNext() ;

          void gotoPrev() ;

          void setMusicPlaylist(List<Music> playlist) ;

          void gotoPosition(int pos) ;
























    }

    interface IMusicPresenter
            extends IBaseActivityContract.IBaseActivityPresenter<IMusicView, IMusicModule> {

    }

    interface IMusicModule
            extends IBaseActivityContract.IBaseActivityModule {

    }
}
