package org.lasque.twsdkvideo.video_beauty.editor.component.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.lasque.tusdk.core.seles.sources.TuSdkEditorEffector;
import org.lasque.tusdk.core.seles.sources.TuSdkEditorPlayer;
import org.lasque.tusdk.core.seles.sources.TuSdkMovieEditor;
import org.lasque.tusdk.core.utils.TLog;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public  class EffectFragment extends Fragment {
        protected TuSdkMovieEditor mMovieEditor;
        protected boolean isAnimationStarting = false;

        public EffectFragment(TuSdkMovieEditor mMovieEditor) {
            this.mMovieEditor = mMovieEditor;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return super.onCreateView(inflater, container, savedInstanceState);
        }



        /**
         * 获取编辑器
         *
         * @return
         */
        protected TuSdkMovieEditor getMovieEditor() {
            if (mMovieEditor == null) {
                TLog.e("EffectFragment is not init");
                return null;
            }
            return mMovieEditor;
        }

        /**
         * 获取编辑播放器
         *
         * @return
         */
        protected TuSdkEditorPlayer getEditorPlayer() {
            return getMovieEditor().getEditorPlayer();
        }

        /**
         * 获取编辑特效器
         *
         * @return
         */
        protected TuSdkEditorEffector getEditorEffector() {
            return getMovieEditor().getEditorEffector();
        }

        public void onAnimationStart() {
            isAnimationStarting = true;
        }

        public void onAnimationEnd() {
            isAnimationStarting = false;
        }


        /**
         * 同步组件的attach方法
         **/
        public void attach() {
        }

        /**
         * 同步组件的detach方法
         */
        public void detach() {
        }

        /**
         * 同步组件的返回事件
         */
        public void back() {
        }


        /**
         * 同步组件的确认事件
         */
        public void next() {
        }

        /**
         * 被选中
         **/
        public void onSelected() {
        }

    }