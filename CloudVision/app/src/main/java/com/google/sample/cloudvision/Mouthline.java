package com.google.sample.cloudvision;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Mouthline implements SimpleRenderer.Obj {

    private FloatBuffer vbuf;
    private float x, y, z;

//    public Mouth(float s, float x, float y, float z) {
    public Mouthline(float[][][] parts1, float[][][] parts2, float x, float y, float z) {
        float[] vertices = {
                // left
                parts1[0][1][0], parts1[0][1][1], parts1[0][1][2],
                parts1[0][0][0], parts1[0][0][1], parts1[0][0][2],
                // right
                parts1[0][2][0], parts1[0][2][1], parts1[0][2][2],
                parts1[0][0][0], parts1[0][0][1], parts1[0][0][2],
                // left
                parts2[0][1][0], parts2[0][1][1], parts2[0][1][2],
                parts2[0][0][0], parts2[0][0][1], parts2[0][0][2],
                // right
                parts2[0][2][0], parts2[0][2][1], parts2[0][2][2],
                parts2[0][0][0], parts2[0][0][1], parts2[0][0][2],
                //center
                parts1[0][0][0], parts1[0][0][1], parts1[0][0][2],
                parts2[0][0][0], parts2[0][0][1], parts2[0][0][2],
                //center
                parts1[0][1][0], parts1[0][1][1], parts1[0][1][2],
                parts2[0][1][0], parts2[0][1][1], parts2[0][1][2],
                //center
                parts1[0][2][0], parts1[0][2][1], parts1[0][2][2],
                parts2[0][2][0], parts2[0][2][1], parts2[0][2][2],
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
        gl.glLineWidth(4);    //　描画サイズを決める
//        gl.glColorPointer(4,GL10.GL_FIXED, 0, c);
//        gl.glColor4f(1.0f, 0.f, 0.f, 0.f);
        gl.glDrawArrays(GL10.GL_LINES, 0, 2);
        // right
        gl.glLineWidth(4);    //　描画サイズを決める
        gl.glDrawArrays(GL10.GL_LINES, 2, 2);
        // left
        gl.glLineWidth(4);    //　描画サイズを決める
        gl.glDrawArrays(GL10.GL_LINES, 4, 2);
        // right
        gl.glLineWidth(4);    //　描画サイズを決める
        gl.glDrawArrays(GL10.GL_LINES, 6, 2);
        // center
        gl.glLineWidth(4);    //　描画サイズを決める
        gl.glDrawArrays(GL10.GL_LINES, 8, 2);
        // center
        gl.glLineWidth(4);    //　描画サイズを決める
        gl.glDrawArrays(GL10.GL_LINES, 10, 2);
        // center
        gl.glLineWidth(4);    //　描画サイズを決める
        gl.glDrawArrays(GL10.GL_LINES, 12, 2);
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
