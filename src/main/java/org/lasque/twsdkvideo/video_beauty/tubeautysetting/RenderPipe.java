package org.lasque.twsdkvideo.video_beauty.tubeautysetting;

import com.tusdk.pulse.DispatchQueue;
import com.tusdk.pulse.Engine;
import com.tusdk.pulse.utils.gl.GLContext;


public class RenderPipe{
    /**
     * OpenGL上下文对象
     */
    private GLContext mGLCtx;

    /**
     * 任务队列
     */
    private DispatchQueue mRenderPool;

    private boolean isInit = false;

    public boolean initRenderPipe(){
        if (mRenderPool != null) return false;

        mRenderPool = new DispatchQueue();
        mRenderPool.runSync(new Runnable() {
            @Override
            public void run() {
                mGLCtx = new GLContext();
                mGLCtx.createForRender(Engine.getInstance().getMainGLContext().getEGLContext());
                mGLCtx.makeCurrent();
            }
        });
        return isInit;
    }

    /**
     * @return 获取任务队列
     */
    public DispatchQueue getRenderPool(){
        return mRenderPool;
    }

    /**
     * @return 获取OpenGL 上下文
     */
    public GLContext getContext(){
        return mGLCtx;
    }

    /**
     * 释放
     */
    public void release(){
        mRenderPool.runSync(new Runnable() {
            @Override
            public void run() {
                mGLCtx.unMakeCurrent();
                mGLCtx.destroy();
            }
        });
    }
}
