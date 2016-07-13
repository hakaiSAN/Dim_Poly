package com.google.sample.cloudvision;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Mouth implements SimpleRenderer.Obj {

    private FloatBuffer vbuf;
    private float x, y, z;

//    public Mouth(float s, float x, float y, float z) {
    public Mouth(float[][][] face_parts_coordinate, float x, float y, float z) {
        float[] vertices = {
                // left
                face_parts_coordinate[0][1][0], face_parts_coordinate[0][1][1], face_parts_coordinate[0][1][2],
                face_parts_coordinate[0][0][0], face_parts_coordinate[0][0][1], face_parts_coordinate[0][0][2],
                // right
                face_parts_coordinate[0][2][0], face_parts_coordinate[0][2][1], face_parts_coordinate[0][2][2],
                face_parts_coordinate[0][0][0], face_parts_coordinate[0][0][1], face_parts_coordinate[0][0][2],
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
        gl.glColor4f(0x10000, 0x10000, 0x100000,0x100000);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vbuf);

        // left
        gl.glLineWidth( 4 );	//　描画サイズを決める
//        gl.glColorPointer(4,GL10.GL_FIXED, 0, c);
        gl.glColor4f(1.0f, 0.f, 0.f, 0.f);
        gl.glDrawArrays(GL10.GL_LINES, 0, 2);

        // right
        gl.glLineWidth( 4 );	//　描画サイズを決める
        gl.glNormal3f(1, 1, 0);
        gl.glDrawArrays(GL10.GL_LINES, 2, 2);

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
