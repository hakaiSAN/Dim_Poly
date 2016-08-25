package com.google.sample.cloudvision;

import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Noseline implements SimpleRenderer.Obj {
    private final static String TAG = "Nose";

    private FloatBuffer vbuf, fVbobuf, fVrbuf, fVlbuf, fVbabuf;
    private float x, y, z;

    public Noseline(float[][][] parts1, float[][][] parts2, float x, float y, float z) {
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

                // parts2
                parts2[1][0][0], parts2[1][0][1], parts2[1][0][2], //top
                parts2[1][1][0], parts2[1][1][1], parts2[1][1][2], //left

                parts2[1][0][0], parts2[1][0][1], parts2[1][0][2], //top
                parts2[1][2][0], parts2[1][2][1], parts2[1][2][2], //right

                parts2[1][0][0], parts2[1][0][1], parts2[1][0][2], //top
                parts2[1][3][0], parts2[1][3][1], parts2[1][3][2], //front

                parts2[1][1][0], parts2[1][1][1], parts2[1][1][2], //left
                parts2[1][2][0], parts2[1][2][1], parts2[1][2][2], //right

                parts2[1][1][0], parts2[1][1][1], parts2[1][1][2], //left
                parts2[1][3][0], parts2[1][3][1], parts2[1][3][2], //front

                parts2[1][2][0], parts2[1][2][1], parts2[1][2][2], //right
                parts2[1][3][0], parts2[1][3][1], parts2[1][3][2], //front

                // parts1-2
                parts1[1][0][0], parts1[1][0][1], parts1[1][0][2], //top
                parts2[1][0][0], parts2[1][0][1], parts2[1][0][2], //top

                parts1[1][1][0], parts1[1][1][1], parts1[1][1][2], //left
                parts2[1][1][0], parts2[1][1][1], parts2[1][1][2], //left

                parts1[1][2][0], parts1[1][2][1], parts1[1][2][2], //right
                parts2[1][2][0], parts2[1][2][1], parts2[1][2][2], //right

                parts1[1][3][0], parts1[1][3][1], parts1[1][3][2], //front
                parts2[1][3][0], parts2[1][3][1], parts2[1][3][2], //front

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

        for (int i = 0; i < 32; i = i + 2) {
            gl.glLineWidth(10);    //　描画サイズを決める
//        gl.glColorPointer(4,GL10.GL_FIXED, 0, c);
//            gl.glColor4f(1.0f,1.0f,1.0f,1.0f);
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
    public static float[] Cross( float[] vector1, float[] vector2 ) {
        return new float[]{
                vector1[1] * vector2[2] - vector1[2] * vector2[1],
                vector1[2] * vector2[0] - vector1[0] * vector2[2],
                vector1[0] * vector2[1] - vector1[1] * vector2[0]
        };
    }
}
