package com.google.sample.cloudvision;

import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Eyeline implements SimpleRenderer.Obj {
    private final static String TAG = "Eye";
    private FloatBuffer vbuf, fVbuf;
    private float x, y, z;

    public Eyeline(float[][][] face_parts_coordinate, int type, float x, float y, float z) {
        float[] vertices = {
                // bottom
                face_parts_coordinate[type][3][0], face_parts_coordinate[type][3][1], face_parts_coordinate[type][3][2], //front
                face_parts_coordinate[type][1][0], face_parts_coordinate[type][1][1], face_parts_coordinate[type][1][2], //left
                face_parts_coordinate[type][2][0], face_parts_coordinate[type][2][1], face_parts_coordinate[type][2][2], //right
                face_parts_coordinate[type][4][0], face_parts_coordinate[type][4][1], face_parts_coordinate[type][4][2], //back
        };
        float [][] v = new float[3][]; //頂点
        v[0] = face_parts_coordinate[type][3]; //left
        v[1] = face_parts_coordinate[type][1]; //right
        v[2] = face_parts_coordinate[type][2]; //front

        float[] faceVertical= Cross(
                new float[]{ v[1][0] - v[0][0], v[1][1] - v[0][1], v[1][2] - v[0][2] }, //「v[1] - v[0]」
                new float[]{ v[2][0] - v[0][0], v[2][1] - v[0][1], v[2][2] - v[0][2] }  //「v[2] - v[0]」
        );
        Log.d(TAG, "faceVertical" + faceVertical[0] + faceVertical[1] + faceVertical[2]) ;

        fVbuf = ByteBuffer.allocateDirect(faceVertical.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        fVbuf.put(faceVertical);
        fVbuf.position(0);
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

        // bottom
        gl.glNormal3f(fVbuf.get(0), fVbuf.get(1), fVbuf.get(2));
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);

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
