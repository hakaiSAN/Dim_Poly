/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.sample.cloudvision;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.AnnotateImageResponse;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.api.services.vision.v1.model.FaceAnnotation;
import com.google.api.services.vision.v1.model.Landmark;
// import com.google.common.io.Resources;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.lang.Object;
import java.util.Objects;
import android.content.res.Resources;

public class Face_detection extends AppCompatActivity {

    // please your cloud vision api key
    private static final String CLOUD_VISION_API_KEY = "";

    public static final String FILE_NAME = "temp.jpg";

    private static final String TAG = Face_detection.class.getSimpleName();
    private static final int GALLERY_IMAGE_REQUEST = 1;
    public static final int CAMERA_PERMISSIONS_REQUEST = 2;
    public static final int CAMERA_IMAGE_REQUEST = 3;

    private TextView mImageDetails;
    private ImageView mMainImage;

    private static List<Landmark> face_parts = null;
//    public static float[] face_parts_coordinate = new float[90];
    public static float[][][] face_parts_coordinate = new float[6][5][3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Face_detection.this);
                builder
                        .setMessage(R.string.dialog_select_prompt)
                        .setPositiveButton(R.string.dialog_select_gallery, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startGalleryChooser();
                            }
                        })
                        .setNegativeButton(R.string.dialog_select_camera, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startCamera();
                            }
                        });
                builder.create().show();
            }
        });

        mImageDetails = (TextView) findViewById(R.id.image_details);
        mMainImage = (ImageView) findViewById(R.id.main_image);
    }

    public void startGalleryChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select a photo"),
                GALLERY_IMAGE_REQUEST);
    }

    public void startCamera() {
        if (PermissionUtils.requestPermission(
                this,
                CAMERA_PERMISSIONS_REQUEST,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getCameraFile()));
            startActivityForResult(intent, CAMERA_IMAGE_REQUEST);
        }
    }

    public File getCameraFile() {
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return new File(dir, FILE_NAME);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            uploadImage(data.getData());

        } else if (requestCode == CAMERA_IMAGE_REQUEST && resultCode == RESULT_OK) {
            uploadImage(Uri.fromFile(getCameraFile()));
        }
    }


    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PermissionUtils.permissionGranted(
                requestCode,
                CAMERA_PERMISSIONS_REQUEST,
                grantResults)) {
            startCamera();
        }
    }

    public void uploadImage(Uri uri) {
        if (uri != null) {
            try {
                // scale the image to save on bandwidth
                Bitmap bitmap =
                        scaleBitmapDown(
                                MediaStore.Images.Media.getBitmap(getContentResolver(), uri),
                                1200);

                callCloudVision(bitmap);
                mMainImage.setImageBitmap(bitmap);

            } catch (IOException e) {
                Log.d(TAG, "Image picking failed because " + e.getMessage());
                Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d(TAG, "Image picker gave us a null image.");
            Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
        }
    }

    private void callCloudVision(final Bitmap bitmap) throws IOException {
        // Switch text to loading
        mImageDetails.setText(R.string.loading_message);

        // Do the real work in an async task, because we need to use the network anyway
        new AsyncTask<Object, Void, BatchAnnotateImagesResponse>() {
            @Override
            protected BatchAnnotateImagesResponse doInBackground(Object... params) {
///*
                try {
                    HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
                    JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

                    Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
                    builder.setVisionRequestInitializer(new
                            VisionRequestInitializer(CLOUD_VISION_API_KEY));
                    Vision vision = builder.build();

                    BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                            new BatchAnnotateImagesRequest();
                    batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {
                        {
                            AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

                            // Add the image
                            Image base64EncodedImage = new Image();
                            // Convert the bitmap to a JPEG
                            // Just in case it's a format that Android understands but Cloud Vision
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                            byte[] imageBytes = byteArrayOutputStream.toByteArray();

                            // Base64 encode the JPEG
                            base64EncodedImage.encodeContent(imageBytes);
                            annotateImageRequest.setImage(base64EncodedImage);

                            // add the features we want
                            annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                                Feature labelDetection = new Feature();
                                labelDetection.setType("FACE_DETECTION");
                                labelDetection.setMaxResults(5);
                                add(labelDetection);
                            }});

                            // Add the list of one thing to the request
                            add(annotateImageRequest);
                        }
                    });

                    Vision.Images.Annotate annotateRequest =
                            vision.images().annotate(batchAnnotateImagesRequest);
                    // Due to a bug: requests to Vision API containing large images fail when GZipped.
                    annotateRequest.setDisableGZipContent(true);
                    Log.d(TAG, "created Cloud Vision request object, sending request");

                    BatchAnnotateImagesResponse response = annotateRequest.execute();
                    return response;
//                    return response.toString(); //json file

//                    return convertResponseToString(response); //default

                } catch (GoogleJsonResponseException e) {
                    Log.d(TAG, "failed to make API request because " + e.getContent());
                } catch (IOException e) {
                    Log.d(TAG, "failed to make API request because of other IOException " +
                            e.getMessage());
                }
//                return "Cloud Vision API request failed. Check logs for details.";
                return null;
            }

            protected void onPostExecute(BatchAnnotateImagesResponse response) {
                extractionface(response);
                Intent intent = new Intent();
                Log.d(TAG, "screen change");
                intent.setClassName("com.google.sample.cloudvision", "com.google.sample.cloudvision.GL_model");
                send_position(face_parts_coordinate,intent);
                startActivity(intent);
            }
        }.execute();
    }

    public Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

    //特徴量検出リスト関数
    private void extractionface(BatchAnnotateImagesResponse response) {
        List<FaceAnnotation> labels = response.getResponses().get(0).getFaceAnnotations();
        face_parts = null; //new image
        if (labels != null) {
            //一人のみ
            for (FaceAnnotation label : labels) {
                face_parts = label.getLandmarks();
                Log.d(TAG, "Landmark detected.");
           }
        }
        for (Landmark face_part : face_parts) {
            Mouthlist(face_part);
            Noselist(face_part);
            Lefteyebrowlist(face_part);
            Righteyebrowlist(face_part);
            Lefteyelist(face_part);
            Righteyelist(face_part);
            Log.d(TAG, "Landmark extracted.");
        }
        Log.d(TAG, "Landmark extracted(null).");
 //        return face_parts;
    }

    private void Mouthlist(Landmark face_part) {
        Resources res = getResources();
//       Log.d(TAG, "" + R.integer.MOUTH + R.integer.LEFT);
        xyz_extraction_null(res.getInteger(R.integer.MOUTH), res.getInteger(R.integer.TOP));
        xyz_extraction(face_part, "MOUTH_LEFT", res.getInteger(R.integer.MOUTH), res.getInteger(R.integer.LEFT));
        xyz_extraction(face_part, "MOUTH_RIGHT", res.getInteger(R.integer.MOUTH), res.getInteger(R.integer.RIGHT));
        xyz_extraction(face_part, "MOUTH_CENTER", res.getInteger(R.integer.MOUTH), res.getInteger(R.integer.FRONT));
        xyz_extraction_null(res.getInteger(R.integer.MOUTH), res.getInteger(R.integer.BACK));
    }
    private void Noselist(Landmark face_part) {
        Resources res = getResources();
        xyz_extraction(face_part, "FORHEAD_GLABELLA", res.getInteger(R.integer.NOSE), res.getInteger(R.integer.TOP));
        xyz_extraction(face_part, "NOSE_BOTTOM_LEFT", res.getInteger(R.integer.NOSE), res.getInteger(R.integer.LEFT));
        xyz_extraction(face_part, "NOSE_BOTTOM_RIGHT", res.getInteger(R.integer.NOSE), res.getInteger(R.integer.RIGHT));
        xyz_extraction(face_part, "NOSE_TIP", res.getInteger(R.integer.NOSE), res.getInteger(R.integer.FRONT));
        xyz_extraction_null(res.getInteger(R.integer.NOSE), res.getInteger(R.integer.BACK));
    }
    private void Lefteyebrowlist(Landmark face_part) {
        Resources res = getResources();
        xyz_extraction_null(res.getInteger(R.integer.LEFT_EYEBROW), res.getInteger(R.integer.TOP));
        xyz_extraction(face_part, "LEFT_OF_LEFT_EYEBROW", res.getInteger(R.integer.LEFT_EYEBROW), res.getInteger(R.integer.LEFT));
        xyz_extraction(face_part, "RIGHT_OF_LEFT_EYEBROW", res.getInteger(R.integer.LEFT_EYEBROW), res.getInteger(R.integer.RIGHT));
        xyz_extraction(face_part, "LEFT_EYEBROW_UPPER_MIDPOINT", res.getInteger(R.integer.LEFT_EYEBROW), res.getInteger(R.integer.FRONT));
        xyz_extraction_null(res.getInteger(R.integer.LEFT_EYEBROW), res.getInteger(R.integer.BACK));
    }
    private void Righteyebrowlist(Landmark face_part) {
        Resources res = getResources();
        xyz_extraction_null(res.getInteger(R.integer.RIGHT_EYEBROW), res.getInteger(R.integer.TOP));
        xyz_extraction(face_part, "LEFT_OF_RIGHT_EYEBROW", res.getInteger(R.integer.RIGHT_EYEBROW), res.getInteger(R.integer.LEFT));
        xyz_extraction(face_part, "RIGHT_OF_RIGHT_EYEBROW", res.getInteger(R.integer.RIGHT_EYEBROW), res.getInteger(R.integer.RIGHT));
        xyz_extraction(face_part, "RIGHT_EYEBROW_UPPER_MIDPOINT", res.getInteger(R.integer.RIGHT_EYEBROW), res.getInteger(R.integer.FRONT));
        xyz_extraction_null(res.getInteger(R.integer.RIGHT_EYEBROW), res.getInteger(R.integer.BACK));
    }
    private void Lefteyelist(Landmark face_part) {
        Resources res = getResources();
        xyz_extraction_null(res.getInteger(R.integer.LEFT_EYE), res.getInteger(R.integer.TOP));
        xyz_extraction(face_part, "LEFT_EYE_LEFT_CORNER", res.getInteger(R.integer.LEFT_EYE), res.getInteger(R.integer.LEFT));
        xyz_extraction(face_part, "LEFT_EYE_RIGHT_CORNER", res.getInteger(R.integer.LEFT_EYE), res.getInteger(R.integer.RIGHT));
        xyz_extraction(face_part, "LEFT_EYE_TOP_BOUNDARY", res.getInteger(R.integer.LEFT_EYE), res.getInteger(R.integer.FRONT));
        xyz_extraction(face_part, "LEFT_EYE_BOTTOM_BOUNDARY", res.getInteger(R.integer.LEFT_EYE), res.getInteger(R.integer.BACK));
    }
    private void Righteyelist(Landmark face_part) {
        Resources res = getResources();
        xyz_extraction_null(res.getInteger(R.integer.RIGHT_EYE), res.getInteger(R.integer.TOP));
        xyz_extraction(face_part, "RIGHT_EYE_LEFT_CORNER", res.getInteger(R.integer.RIGHT_EYE), res.getInteger(R.integer.LEFT));
        xyz_extraction(face_part, "RIGHT_EYE_RIGHT_CORNER", res.getInteger(R.integer.RIGHT_EYE), res.getInteger(R.integer.RIGHT));
        xyz_extraction(face_part, "RIGHT_EYE_TOP_BOUNDARY", res.getInteger(R.integer.RIGHT_EYE), res.getInteger(R.integer.FRONT));
        xyz_extraction(face_part, "RIGHT_EYE_BOTTOM_BOUNDARY", res.getInteger(R.integer.RIGHT_EYE), res.getInteger(R.integer.BACK));
    }

    private void xyz_extraction(Landmark face_part, String type, int parts, int position){
        String tmp = face_part.getType();
//        Log.d(TAG, "xyz_extract." + face_part.getType());
        if (type.equals(tmp)) {
//           int parts_position = parts * 15 + position * 3;
//           Log.d(TAG, "x axis." + parts_position);
            face_parts_coordinate[parts][position][0] = face_part.getPosition().getX(); //parts position x
            face_parts_coordinate[parts][position][1] = face_part.getPosition().getY(); //parts position y
            face_parts_coordinate[parts][position][2] = face_part.getPosition().getZ(); //parts position z
       }
    }
    private void xyz_extraction_null(int parts, int position){
            face_parts_coordinate[parts][position][0] = 0.f; //parts position x
            face_parts_coordinate[parts][position][1] = 0.f; //parts position y
            face_parts_coordinate[parts][position][2] = 0.f; //parts position z
    }

    private void send_position(float[][][] face_parts_coordinate, Intent intent){
        intent.putExtra("MOUTH_TOP", face_parts_coordinate[0][0]);
        intent.putExtra("MOUTH_LEFT", face_parts_coordinate[0][1]);
        intent.putExtra("MOUTH_RIGHT", face_parts_coordinate[0][2]);
        intent.putExtra("MOUTH_FRONT", face_parts_coordinate[0][3]);
        intent.putExtra("MOUTH_BACK", face_parts_coordinate[0][4]);
        intent.putExtra("NOSE_TOP", face_parts_coordinate[1][0]);
        intent.putExtra("NOSE_LEFT", face_parts_coordinate[1][1]);
        intent.putExtra("NOSE_RIGHT", face_parts_coordinate[1][2]);
        intent.putExtra("NOSE_FRONT", face_parts_coordinate[1][3]);
        intent.putExtra("NOSE_BACK", face_parts_coordinate[1][4]);
        intent.putExtra("LEFT_EYEBROW_TOP", face_parts_coordinate[2][0]);
        intent.putExtra("LEFT_EYEBROW_LEFT", face_parts_coordinate[2][1]);
        intent.putExtra("LEFT_EYEBROW_RIGHT", face_parts_coordinate[2][2]);
        intent.putExtra("LEFT_EYEBROW_FRONT", face_parts_coordinate[2][3]);
        intent.putExtra("LEFT_EYEBROW_BACK", face_parts_coordinate[2][4]);
        intent.putExtra("RIGHT_EYEBROW_TOP", face_parts_coordinate[3][0]);
        intent.putExtra("RIGHT_EYEBROW_LEFT", face_parts_coordinate[3][1]);
        intent.putExtra("RIGHT_EYEBROW_RIGHT", face_parts_coordinate[3][2]);
        intent.putExtra("RIGHT_EYEBROW_FRONT", face_parts_coordinate[3][3]);
        intent.putExtra("RIGHT_EYEBROW_BACK", face_parts_coordinate[3][4]);
        intent.putExtra("LEFT_EYE_TOP", face_parts_coordinate[4][0]);
        intent.putExtra("LEFT_EYE_LEFT", face_parts_coordinate[4][1]);
        intent.putExtra("LEFT_EYE_RIGHT", face_parts_coordinate[4][2]);
        intent.putExtra("LEFT_EYE_FRONT", face_parts_coordinate[4][3]);
        intent.putExtra("LEFT_EYE_BACK", face_parts_coordinate[4][4]);
        intent.putExtra("RIGHT_EYE_TOP", face_parts_coordinate[5][0]);
        intent.putExtra("RIGHT_EYE_LEFT", face_parts_coordinate[5][1]);
        intent.putExtra("RIGHT_EYE_RIGHT", face_parts_coordinate[5][2]);
        intent.putExtra("RIGHT_EYE_FRONT", face_parts_coordinate[5][3]);
        intent.putExtra("RIGHT_EYE_BACK", face_parts_coordinate[5][4]);
        Log.d(TAG, "Send face parts coordinate");
    }

}
