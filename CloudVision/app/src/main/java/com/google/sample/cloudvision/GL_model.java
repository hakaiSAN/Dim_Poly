package com.google.sample.cloudvision;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.SeekBar;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.content.Intent;

public class GL_model extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {
    private final static String TAG = "GL_model";

    private GLSurfaceView glView;
    private SimpleRenderer renderer;
    private SeekBar rotationBarX, rotationBarY, rotationBarZ;

    private GestureDetector mGestureDetector;
    public static float[][][] face_parts_coordinate = new float[6][5][3];
//    public static float[][][] face_parts_coordinate_4 = new float[6][5][4];
    public static float[][][] face_parts_position = new float[6][5][3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_gl_model);
        glView = (GLSurfaceView) findViewById(R.id.glview);

        rotationBarX = (SeekBar) findViewById(R.id.rotation_bar_x);
        rotationBarY = (SeekBar) findViewById(R.id.rotation_bar_y);
        rotationBarZ = (SeekBar) findViewById(R.id.rotation_bar_z);
        rotationBarX.setOnSeekBarChangeListener(this);
        rotationBarY.setOnSeekBarChangeListener(this);
        rotationBarZ.setOnSeekBarChangeListener(this);

        renderer = new SimpleRenderer();
        glView.setRenderer(renderer);

        Intent intent = getIntent();
        receive_position(intent);

        dimensition_3to4();

//        setdraw(renderer, face_parts_position);
        setline(renderer, face_parts_coordinate, face_parts_position);

        mGestureDetector = new GestureDetector(this, mOnGestureListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        glView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        glView.onPause();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar == rotationBarX)
            renderer.setRotationX(progress);
        else if (seekBar == rotationBarY)
            renderer.setRotationY(progress);
        else if (seekBar == rotationBarZ)
            renderer.setRotationZ(progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }
    // タッチイベント
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    // タッチイベントのリスナー
    private final GestureDetector.SimpleOnGestureListener mOnGestureListener = new GestureDetector.SimpleOnGestureListener() {
        // フリックイベント
        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
            Log.d(TAG, "onFling");
            try {
                // スピードを出力
                float velocity_x = Math.abs(velocityX);
                float velocity_y = Math.abs(velocityY);
                while(velocity_x > 0 || velocity_y>0) { //判定怪しい
                    if (velocity_x > 0)
                        Log.d(TAG, "x_moving");
                    renderer.setRotationY(velocity_x/10);
                    if (velocity_y > 0)
                        Log.d(TAG, "y_moving");
                    renderer.setRotationZ(velocity_y/10);

                    velocity_x = velocity_x - 1; //改善の余地あり
                    velocity_y = velocity_y - 1; //改善の余地あり
                }

            } catch (Exception e) {
                // TODO
            }
            return false;
        }
    };
    private void receive_position(Intent intent){
        face_parts_coordinate[0][0] = intent.getFloatArrayExtra("MOUTH_TOP");
        face_parts_coordinate[0][1] = intent.getFloatArrayExtra("MOUTH_LEFT");
        face_parts_coordinate[0][2] = intent.getFloatArrayExtra("MOUTH_RIGHT");
        face_parts_coordinate[0][3] = intent.getFloatArrayExtra("MOUTH_FRONT");
        face_parts_coordinate[0][4] = intent.getFloatArrayExtra("MOUTH_BACK");
        face_parts_coordinate[1][0] = intent.getFloatArrayExtra("NOSE_TOP");
        face_parts_coordinate[1][1] = intent.getFloatArrayExtra("NOSE_LEFT");
        face_parts_coordinate[1][2] = intent.getFloatArrayExtra("NOSE_RIGHT");
        face_parts_coordinate[1][3] = intent.getFloatArrayExtra("NOSE_FRONT");
        face_parts_coordinate[1][4] = intent.getFloatArrayExtra("NOSE_BACK");
        face_parts_coordinate[2][0] = intent.getFloatArrayExtra("LEFT_EYEBROW_TOP");
        face_parts_coordinate[2][1] = intent.getFloatArrayExtra("LEFT_EYEBROW_LEFT");
        face_parts_coordinate[2][2] = intent.getFloatArrayExtra("LEFT_EYEBROW_RIGHT");
        face_parts_coordinate[2][3] = intent.getFloatArrayExtra("LEFT_EYEBROW_FRONT");
        face_parts_coordinate[2][4] = intent.getFloatArrayExtra("LEFT_EYEBROW_BACK");
        face_parts_coordinate[3][0] = intent.getFloatArrayExtra("RIGHT_EYEBROW_TOP");
        face_parts_coordinate[3][1] = intent.getFloatArrayExtra("RIGHT_EYEBROW_LEFT");
        face_parts_coordinate[3][2] = intent.getFloatArrayExtra("RIGHT_EYEBROW_RIGHT");
        face_parts_coordinate[3][3] = intent.getFloatArrayExtra("RIGHT_EYEBROW_FRONT");
        face_parts_coordinate[3][4] = intent.getFloatArrayExtra("RIGHT_EYEBROW_BACK");
        face_parts_coordinate[4][0] = intent.getFloatArrayExtra("LEFT_EYE_TOP");
        face_parts_coordinate[4][1] = intent.getFloatArrayExtra("LEFT_EYE_LEFT");
        face_parts_coordinate[4][2] = intent.getFloatArrayExtra("LEFT_EYE_RIGHT");
        face_parts_coordinate[4][3] = intent.getFloatArrayExtra("LEFT_EYE_FRONT");
        face_parts_coordinate[4][4] = intent.getFloatArrayExtra("LEFT_EYE_BACK");
        face_parts_coordinate[5][0] = intent.getFloatArrayExtra("RIGHT_EYE_TOP");
        face_parts_coordinate[5][1] = intent.getFloatArrayExtra("RIGHT_EYE_LEFT");
        face_parts_coordinate[5][2] = intent.getFloatArrayExtra("RIGHT_EYE_RIGHT");
        face_parts_coordinate[5][3] = intent.getFloatArrayExtra("RIGHT_EYE_FRONT");
        face_parts_coordinate[5][4] = intent.getFloatArrayExtra("RIGHT_EYE_BACK");
        for(int i=0; i< 6; i++) {
            for (int j = 0; j < 5; j++) {
                for (int k = 0; k < 3; k++) {
                    Log.d(TAG, "" + face_parts_coordinate[i][j][k]);
                    face_parts_coordinate[i][j][k] = face_parts_coordinate[i][j][k] / 400;
                }
            }
        }
    }

    private void dimensition_3to4(){
        float x, y, z, w;
        for(int i=0; i< 6; i++) {
            for (int j = 0; j < 5; j++) {
                x = face_parts_coordinate[i][j][0];
                y = face_parts_coordinate[i][j][1];
                z = face_parts_coordinate[i][j][2];
                w = x + y + z;
                /*
                face_parts_coordinate_4[i][j][0] = x;
                face_parts_coordinate_4[i][j][1] = y;
                face_parts_coordinate_4[i][j][2] = z;
                face_parts_coordinate_4[i][j][3] = w;
                */
                face_parts_position[i][j][0] = x + 0.5f * w;
                face_parts_position[i][j][1] = y + 0.5f * w;
                face_parts_position[i][j][2] = z + 0.5f * w;
            }
        }
    }

    private void setdraw(SimpleRenderer renderer, float[][][] parts){
//        renderer.addObj(new Mouth(parts, 0, -0.3f, 0));
        renderer.addObj(new Mouth(parts, 0, 0, 0));
//        renderer.addObj(new Nose(parts, 0, 0, 0));
//        renderer.addObj(new Eyebrow(parts, getResources().getInteger(R.integer.LEFT_EYEBROW), 0.4f, 0.3f, 0));
        renderer.addObj(new Eyebrow(parts, getResources().getInteger(R.integer.LEFT_EYEBROW), 0, 0, 0));
//        renderer.addObj(new Eyebrow(parts, getResources().getInteger(R.integer.RIGHT_EYEBROW), -0.4f, 0.3f, 0));
        renderer.addObj(new Eyebrow(parts, getResources().getInteger(R.integer.RIGHT_EYEBROW), 0, 0, 0));
//        renderer.addObj(new Eye(parts, getResources().getInteger(R.integer.LEFT_EYE), 0.3f, 0.3f, 0));
        renderer.addObj(new Eye(parts, getResources().getInteger(R.integer.LEFT_EYE), 0, 0, 0));
//        renderer.addObj(new Eye(parts, getResources().getInteger(R.integer.RIGHT_EYE), -0.3f, 0.3f, 0));
        renderer.addObj(new Eye(parts, getResources().getInteger(R.integer.RIGHT_EYE), 0, 0, 0));
    }
    private void setline(SimpleRenderer renderer, float[][][] parts1, float [][][] parts2){
            renderer.addObj(new Mouthline(parts1, parts2, 0, -0.3f, 0));
            renderer.addObj(new Noseline(parts1, parts2, 0, 0, 0));
            renderer.addObj(new Eyebrowline(parts1, parts2, getResources().getInteger(R.integer.LEFT_EYEBROW), 0.4f, 0.3f, 0));
            renderer.addObj(new Eyebrowline(parts1, parts2, getResources().getInteger(R.integer.RIGHT_EYEBROW), -0.4f, 0.3f, 0));
            renderer.addObj(new Eyeline(parts1, parts2, getResources().getInteger(R.integer.LEFT_EYE), 0.3f, 0.3f, 0));
            renderer.addObj(new Eyeline(parts1, parts2, getResources().getInteger(R.integer.RIGHT_EYE), -0.3f, 0.3f, 0));
    }

}
