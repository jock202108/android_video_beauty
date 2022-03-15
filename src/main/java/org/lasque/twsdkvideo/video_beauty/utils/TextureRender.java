package org.lasque.twsdkvideo.video_beauty.utils;

import android.opengl.GLES10Ext;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLES31Ext;
import android.opengl.Matrix;

import androidx.annotation.NonNull;

import com.tusdk.pulse.utils.gl.GLUtil;

import org.lasque.tusdkpulse.core.utils.TLog;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * TuSDK
 * com.qiniu.pili.droid.shortvideo.demo.tusdk
 * PLDroidShortVideoDemo
 *
 * @author H.ys
 * @Date 2021/3/30  15:04
 * @Copyright (c) 2020 tusdk.com. All rights reserved.
 */
public class TextureRender {

    private static final String TAG = "TextureRender";
    private static final int FLOAT_SIZE_BYTES = 4;
    private static final int TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 5 * FLOAT_SIZE_BYTES;
    private static final int TRIANGLE_VERTICES_DATA_POS_OFFSET = 0;
    private static final int TRIANGLE_VERTICES_DATA_UV_OFFSET = 3;

    private final float[] mTriangleVerticesData = {
            // X, Y, Z, U, V
            -1.0f, -1.0f, 0, 0.f, 0.f,
            1.0f, -1.0f, 0, 1.f, 0.f,
            -1.0f,  1.0f, 0, 0.f, 1.f,
            1.0f,  1.0f, 0, 1.f, 1.f,
    };

    private FloatBuffer mTriangleVertices;

    private static final String VERTEX_SHADER =
            "uniform mat4 uMVPMatrix;\n" +
            "attribute vec3 aPosition;\n" +
                    "attribute vec2 aTextureCoord;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "uniform mat4 uSTMatrix;\n" +
                    "void main() {\n" +
                    "  gl_Position = uMVPMatrix * vec4(aPosition, 1);\n" +
                    "  vTextureCoord = (uSTMatrix * vec4(aTextureCoord, 0, 1)).xy;\n" +
                    "}\n";

    private static final String FRAGMENT_SHADER =
                    "precision mediump float;\n" +      // highp here doesn't seem to matter
                    "varying vec2 vTextureCoord;\n" +
                    "uniform sampler2D sTexture;\n" +
                    "void main() {\n" +
                    "  gl_FragColor = texture2D(sTexture, vTextureCoord);\n" +
                    "}\n";

    private static final String OES_FRAGMENT_SHADER =
            "#extension GL_OES_EGL_image_external : require\n" +
                    "precision mediump float;\n" +      // highp here doesn't seem to matter
                    "varying vec2 vTextureCoord;\n" +
                    "uniform samplerExternalOES sTexture;\n" +
                    "void main() {\n" +
                    "  gl_FragColor = texture2D(sTexture, vTextureCoord);\n" +
                    "}\n";

    private int mProgram;
    private int mTextureID = -12345;
    private int mFBO = -1;

    private int maPositionHandle;
    private int maTextureHandle;
    private int uSTMatrixHandle;
    private int muMVPMatrixHandle;

    private int sTexturePos;

    public int currentWidth;
    public int currentHeight;

    private boolean isOES = false;

    private boolean isOutputOES =false;

    private float[] mSTMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];


    public TextureRender(boolean isOES){
        mTriangleVertices = ByteBuffer.allocateDirect(
                mTriangleVerticesData.length * FLOAT_SIZE_BYTES)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mTriangleVertices.put(mTriangleVerticesData).position(0);
        Matrix.setIdentityM(mSTMatrix, 0);
        Matrix.setIdentityM(mMVPMatrix, 0);
        this.isOES = isOES;
    }

    public void setSTMatrix(@NonNull float[] matrix){
        mSTMatrix = matrix;
    }

    public void setMVPMatrix(@NonNull float[] matrix){
        mMVPMatrix = matrix;
    }

    public int getTextureID(){
        return mTextureID;
    }

    public void create(int width,int height,boolean isOutputOes){
        currentWidth = width;
        currentHeight = height;
        mProgram = GLUtil.buildProgram(VERTEX_SHADER,isOES ? OES_FRAGMENT_SHADER : FRAGMENT_SHADER);
        if (mProgram == 0){
            throw new RuntimeException("failed creating program");
        }

        maPositionHandle = GLES20.glGetAttribLocation(mProgram,"aPosition");
        GLUtil.checkEglError("glGetAttribLocation aPosition");
        if (maPositionHandle == -1) {
            throw new RuntimeException("Could not get attrib location for aPosition");
        }

        maTextureHandle = GLES20.glGetAttribLocation(mProgram, "aTextureCoord");
        GLUtil.checkEglError("glGetAttribLocation aTextureCoord");
        if (maTextureHandle == -1) {
            throw new RuntimeException("Could not get attrib location for aTextureCoord");
        }

        sTexturePos = GLES20.glGetUniformLocation(mProgram,"sTexture");
        GLUtil.checkEglError("glGetUniformLocation sTexture");
        if (sTexturePos == -1) {
            throw new RuntimeException("Could not get attrib location for aTextureCoord");
        }

        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        GLUtil.checkEglError("glGetUniformLocation uMVPMatrix");
        if (muMVPMatrixHandle == -1) {
            throw new RuntimeException("Could not get attrib location for uMVPMatrix");
        }

        uSTMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uSTMatrix");
        GLUtil.checkEglError("glGetUniformLocation sTexture");
        if (uSTMatrixHandle == -1) {
            throw new RuntimeException("Could not get attrib location for uSTMatrix");
        }


        //mFBOTexID = textures[1];

        this.isOutputOES = isOutputOes;

        if (isOutputOes){
            int[] textures = new int[1];
            GLES20.glGenTextures(1, textures, 0);
            mTextureID = textures[0];

            int[] fbos = new int[1];
            GLES20.glGenFramebuffers(1, fbos, 0);
            mFBO = fbos[0];

            boolean res = GLES11Ext.glIsFramebufferOES(mFBO);

            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,mTextureID);
            GLUtil.checkEglError("glBindTexture OES mTextureID");
            GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLUtil.checkEglError("glTexParameter OES");

            GLES11Ext.glBindFramebufferOES(GLES11Ext.GL_FRAMEBUFFER_OES,mFBO);
            GLES11Ext.glFramebufferTexture2DOES(GLES11Ext.GL_FRAMEBUFFER_OES,GLES11Ext.GL_COLOR_ATTACHMENT0_OES,GLES11Ext.GL_TEXTURE_EXTERNAL_OES,mTextureID,0);

            String extensions = " " + GLES20.glGetString(GLES20.GL_EXTENSIONS) + " ";
            TLog.e("GLES20 extensions %s",extensions);

            int status = GLES20.glCheckFramebufferStatus(GLES11Ext.GL_FRAMEBUFFER_OES);
            assert (status == GLES11Ext.GL_FRAMEBUFFER_COMPLETE_OES);


        } else {
            int[] textures = new int[1];
            GLES20.glGenTextures(1, textures, 0);
            mTextureID = textures[0];

            int[] fbos = new int[1];
            GLES20.glGenFramebuffers(1, fbos, 0);
            mFBO = fbos[0];

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureID);
            GLUtil.checkEglError("glBindTexture mTextureID");
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
                    GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
                    GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
                    GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
                    GLES20.GL_CLAMP_TO_EDGE);
            GLUtil.checkEglError("glTexParameter");
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);


            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFBO);
            GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                    GLES20.GL_TEXTURE_2D, mTextureID, 0);

            int status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
            assert (status == GLES20.GL_FRAMEBUFFER_COMPLETE);
        }


    }

    public void release(){
        int[] fbos = new int[1];
        fbos[0] = mFBO;
        GLES20.glDeleteBuffers(1, fbos, 0);
        mFBO = -1;

        int[] textures = new int[1];
        textures[0] = mTextureID;
        GLES20.glDeleteTextures(1, textures, 0);
        mTextureID = -1;

        GLES20.glDeleteProgram(mProgram);
        mProgram = -1;
    }

    public int drawFrame(int tex,int w,int h){
        GLUtil.checkEglError("onDrawFrame start");

        int glerror = GLES20.glGetError();
        if (glerror != GLES20.GL_NO_ERROR)
            return glerror;


        if (isOutputOES){
            GLES11Ext.glBindFramebufferOES(GLES11Ext.GL_FRAMEBUFFER_OES,mFBO);

            int status = GLES20.glCheckFramebufferStatus(GLES11Ext.GL_FRAMEBUFFER_OES);

            if (status != GLES11Ext.GL_FRAMEBUFFER_COMPLETE_OES){
                return -1;
            }

        } else {
            //prepare fbo
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFBO);

//        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, tex, 0);
            //checkGlError("glFramebufferTexture2D");

            if (GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER) != GLES20.GL_FRAMEBUFFER_COMPLETE) {
                //std::terminate();
                return -1;
            }
        }



        GLES20.glViewport(0, 0, w, h);

        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
//        checkGlError("glClearColor");
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
//        checkGlError("glClear");
        GLES20.glUseProgram(mProgram);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
        if (isOES){
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, tex);
        } else {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, tex);
        }

        GLES20.glUniform1i(sTexturePos,2);

        mTriangleVertices.position(TRIANGLE_VERTICES_DATA_POS_OFFSET);

        GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false,
                TRIANGLE_VERTICES_DATA_STRIDE_BYTES, mTriangleVertices);
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        mTriangleVertices.position(TRIANGLE_VERTICES_DATA_UV_OFFSET);
        GLES20.glVertexAttribPointer(maTextureHandle, 2, GLES20.GL_FLOAT, false,
                TRIANGLE_VERTICES_DATA_STRIDE_BYTES, mTriangleVertices);

        GLES20.glEnableVertexAttribArray(maTextureHandle);

        GLES20.glUniformMatrix4fv(uSTMatrixHandle, 1, false, mSTMatrix, 0);
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glFinish();
        GLUtil.checkEglError("glDrawArrays");
        glerror = GLES20.glGetError();
        if (glerror != GLES20.GL_NO_ERROR)
            return glerror;

//        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,0);
//
//        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER,0);

        return GLES20.glGetError();
    }
}
