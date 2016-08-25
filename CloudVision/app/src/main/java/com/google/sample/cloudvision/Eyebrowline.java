package com.google.sample.cloudvision;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Eyebrowline implements SimpleRenderer.Obj {

    private FloatBuffer vbuf;
    private float x, y, z;

    public Eyebrowline(float[][][] parts1, float[][][] parts2, int type, float x, float y, float z) {
        float[] vertices = {
                // left
                parts1[type][1][0], parts1[type][1][1], parts1[type][1][2],
                parts1[type][0][0], parts1[type][0][1], parts1[type][0][2],
                // right
                parts1[type][2][0], parts1[type][2][1], parts1[type][2][2],
                parts1[type][0][0], parts1[type][0][1], parts1[type][0][2],
                // left
                parts2[type][1][0], parts2[type][1][1], parts2[type][1][2],
                parts2[type][0][0], parts2[type][0][1], parts2[type][0][2],
                // right
                parts2[type][2][0], parts2[type][2][1], parts2[type][2][2],
                parts2[type][0][0], parts2[type][0][1], parts2[type][0][2],
                //center
                parts1[type][0][0], parts1[type][0][1], parts1[type][0][2],
                parts2[type][0][0], parts2[type][0][1], parts2[type][0][2],
                //center
                parts1[type][1][0], parts1[type][1][1], parts1[type][1][2],
                parts2[type][1][0], parts2[type][1][1], parts2[type][1][2],
                //center
                parts1[type][2][0], parts1[type][2][1], parts1[type][2][2],
                parts2[type][2][0], parts2[type][2][1], parts2[type][2][2],
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

            // left
            for (int i = 0; i < 14; i = i + 2) {
                gl.glLineWidth(4);    //　描画サイズを決める
//                gl.glColor4f(0.0f, 0.0f, 1.0f, 1.0f);
//        gl.glColorPointer(4,GL10.GL_FIXED, 0, c);
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
