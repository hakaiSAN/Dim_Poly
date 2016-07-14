package com.google.sample.cloudvision;

import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Nose implements SimpleRenderer.Obj {
    private final static String TAG = "Nose";

    private FloatBuffer vbuf, fVbobuf, fVrbuf, fVlbuf, fVbabuf;
    private float x, y, z;

    public Nose(float[][][] parts1, float x, float y, float z) {
        float[] vertices = {
                // parts1
                parts1[1][0][0], parts1[1][0][1], parts1[1][0][2], //top
                parts1[1][1][0], parts1[1][1][1], parts1[1][1][2], //left

                parts1[1][0][0], parts1[1][0][1], parts1[1][0][2], //top
                parts1[1][2][0], parts1[1][2][1], parts1[1][2][2], //right

                parts1[1][0][0], parts1[1][0][1], parts1[1][0][2], //top
                parts1[1][3][0], parts1[1][3][1], parts1[1][3][2], //front

                parts1[1][1][0], parts1[1][1][1], parts1[1][1][2], //left
                parts1[1][2][0], parts1[1][2][1], parts1[1][2][2], //right

                parts1[1][1][0], parts1[1][1][1], parts1[1][1][2], //left
                parts1[1][3][0], parts1[1][3][1], parts1[1][3][2], //front

                parts1[1][2][0], parts1[1][2][1], parts1[1][2][2], //right
                parts1[1][3][0], parts1[1][3][1], parts1[1][3][2], //front
        };

        vbuf = ByteBuffer.allocateDirect(vertices.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        vbuf.put(vertices);
        vbuf.position(0);
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void draw(GL10 gl) {
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vbuf);

        for (int i = 0; i < 12; i = i + 2) {
            gl.glLineWidth(4);    //　描画サイズを決める
//        gl.glColorPointer(4,GL10.GL_FIXED, 0, c);
            gl.glColor4f(1.0f, 0.f, 0.f, 0.f);
            gl.glDrawArrays(GL10.GL_LINES, i, 2);
        }
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public float getZ() {
        return z;
    }
}
