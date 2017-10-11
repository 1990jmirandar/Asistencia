/* Copyright 2016 Michael Sladoje and Mike Schälchli. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================*/

package com.sinergiass.asistencia.facerecog;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.sinergiass.asistencia.R;
import com.sinergiass.asistencia.model.Operador;

import ch.zhaw.facerecognitionlibrary.Helpers.CustomCameraView;
import ch.zhaw.facerecognitionlibrary.Helpers.FileHelper;
import ch.zhaw.facerecognitionlibrary.Helpers.MatName;
import ch.zhaw.facerecognitionlibrary.Helpers.MatOperation;
import ch.zhaw.facerecognitionlibrary.PreProcessor.PreProcessorFactory;

public class AddPersonPreviewActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {
    public static final int TIME = 0;
    public static final int MANUALLY = 1;
    private CustomCameraView mAddPersonView;
    // The timerDiff defines after how many milliseconds a picture is taken
    private long timerDiff;
    private long lastTime;
    private PreProcessorFactory ppF;
    private FileHelper fh;
    private String folder;
    private String subfolder;
    private String nombre;
    private int total;
    private int numberOfPictures;
    private int method;
    private Button btn_Capture;
    private boolean capturePressed;
    private boolean front_camera;
    private boolean night_portrait;
    private int exposure_compensation;

    private Operador mOperador;

    private List<byte[]> fotosEnByteArray;

    static {
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_person_preview);

        Bundle extras = getIntent().getExtras();

        fotosEnByteArray = new ArrayList<>();

//        folder = extras.getString("Folder");
//        if(folder.equals("Test")){
//            subfolder = intent.getStringExtra("Subfolder");
//        }

//        method = intent.getIntExtra("Method", 0);
        method = AddPersonPreviewActivity.TIME;
        capturePressed = false;
        if(method == MANUALLY){
            btn_Capture = (Button)findViewById(R.id.btn_Capture);
            btn_Capture.setVisibility(View.VISIBLE);
            btn_Capture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    capturePressed = true;
                }
            });
        }

        fh = new FileHelper();
        total = 0;
        lastTime = new Date().getTime();

//        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//        timerDiff = Integer.valueOf(sharedPrefs.getString("key_timerDiff", "500"));
        timerDiff = 500;

        mAddPersonView = (CustomCameraView) findViewById(R.id.AddPersonPreview);
        // Use camera which is selected in settings
//        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
//        front_camera = sharedPref.getBoolean("key_front_camera", true);
        front_camera = true;

//        numberOfPictures = Integer.valueOf(sharedPref.getString("key_numberOfPictures", "10"));
        numberOfPictures = extras.getInt("numberOfPictures");

//        night_portrait = sharedPref.getBoolean("key_night_portrait", false);
        night_portrait = false;
//        exposure_compensation = Integer.valueOf(sharedPref.getString("key_exposure_compensation", "50"));
        exposure_compensation = 50;

        if (front_camera){
            mAddPersonView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_FRONT);
        } else {
            mAddPersonView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_BACK);
        }
        mAddPersonView.setVisibility(SurfaceView.VISIBLE);
        mAddPersonView.setCvCameraViewListener(this);

//        int maxCameraViewWidth = Integer.parseInt(sharedPref.getString("key_maximum_camera_view_width", "640"));
//        int maxCameraViewHeight = Integer.parseInt(sharedPref.getString("key_maximum_camera_view_height", "480"));
        int maxCameraViewWidth = 640;
        int maxCameraViewHeight = 480;
        mAddPersonView.setMaxFrameSize(maxCameraViewWidth, maxCameraViewHeight);

//        // Operador a ser creado
//        nombre = extras.getString("nombre");
//        mOperador = new Operador(extras.getString("nombre"),
//                extras.getString("apellido"),
//                extras.getString());
//
//        mOperador.setNombre(extras.getString("nombre"));
//        mOperador.setApellido();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

        if (night_portrait) {
            mAddPersonView.setNightPortrait();
        }

        if (exposure_compensation != 50 && 0 <= exposure_compensation && exposure_compensation <= 100)
            mAddPersonView.setExposure(exposure_compensation);
    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Mat imgRgba = inputFrame.rgba();
        Mat imgCopy = new Mat();
        imgRgba.copyTo(imgCopy);
        // Selfie / Mirror mode
        if(front_camera){
            Core.flip(imgRgba,imgRgba,1);
        }

        long time = new Date().getTime();
        if((method == MANUALLY) || (method == TIME) && (lastTime + timerDiff < time)){
            lastTime = time;

            // Check that only 1 face is found. Skip if any or more than 1 are found.
            List<Mat> images = ppF.getCroppedImage(imgCopy);
            if (images != null && images.size() == 1){
                Mat img = images.get(0);
                if(img != null){
                    Rect[] faces = ppF.getFacesForRecognition();
                    //Only proceed if 1 face has been detected, ignore if 0 or more than 1 face have been detected
                    if((faces != null) && (faces.length == 1)){
                        faces = MatOperation.rotateFaces(imgRgba, faces, ppF.getAngleForRecognition());
                        if(((method == MANUALLY) && capturePressed) || (method == TIME)){

//                            MatName m = new MatName(nombre + "_" + total, img);
//                            if (folder.equals("Test")) {
//                                String wholeFolderPath = fh.TEST_PATH + nombre + "/" + subfolder;
//                                new File(wholeFolderPath).mkdirs();
//                                fh.saveMatToImage(m, wholeFolderPath + "/");
//                            } else {
//                                String wholeFolderPath = fh.TRAINING_PATH + nombre;
//                                new File(wholeFolderPath).mkdirs();
//                                fh.saveMatToImage(m, wholeFolderPath + "/");
//                            }

                            byte[] data = new byte[(int)(img.total()*img.channels())];
                            img.get(0, 0, data);

                            fotosEnByteArray.add(data);

                            for(int i = 0; i<faces.length; i++){
                                MatOperation.drawRectangleAndLabelOnPreview(imgRgba, faces[i], String.valueOf(total), front_camera);
                            }

                            total++;

                            // Stop after numberOfPictures (settings option)
                            if(total >= numberOfPictures){

                                Intent returnIntent = new Intent();

                                for (int i = 0; i < fotosEnByteArray.size() /*max 10*/; i++){
                                    returnIntent.putExtra("foto"+i, fotosEnByteArray.get(i));
                                }

                                returnIntent.putExtra("exitoso", true);
                                setResult(Activity.RESULT_OK,returnIntent);
                                finish();
                            }
                            capturePressed = false;
                        } else {
                            for(int i = 0; i<faces.length; i++){
                                MatOperation.drawRectangleOnPreview(imgRgba, faces[i], front_camera);
                            }
                        }
                    }
                }
            }
        }

        return imgRgba;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        ppF = new PreProcessorFactory(this);
        mAddPersonView.enableView();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mAddPersonView != null)
            mAddPersonView.disableView();
    }

    public void onDestroy() {
        super.onDestroy();
        if (mAddPersonView != null)
            mAddPersonView.disableView();
    }
}
