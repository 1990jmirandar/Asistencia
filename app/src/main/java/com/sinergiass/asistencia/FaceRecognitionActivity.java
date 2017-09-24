package com.sinergiass.asistencia;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.orm.SugarRecord;
import com.sinergiass.asistencia.facerecognition.CameraBridgeViewBase;
import com.sinergiass.asistencia.NativeMethods;
import com.sinergiass.asistencia.model.Asistencia;
import com.sinergiass.asistencia.model.Operador;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class FaceRecognitionActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2  {
    private static final String TAG = FaceRecognitionActivity.class.getSimpleName();
    private static final int PERMISSIONS_REQUEST_CODE = 0;
    private ArrayList<Mat> images;
    private ArrayList<String> imagesLabels;
    private String[] uniqueLabels;
    private CameraBridgeViewBase mOpenCvCameraView;
    private Mat mRgba, mGray;
    private Toast mToast;
    private boolean useEigenfaces;
    private float faceThreshold, distanceThreshold;
    private int maximumImages;
    private SharedPreferences prefs;
    private NativeMethods.TrainFacesTask mTrainFacesTask;

    private List<Operador> mOperadores;

    private static String mPhotoName = "";
    private String mEncoding;

    /* Metodo usado para sacar la base local del celular afuera del directorio protegido
       para de esta manera traerla al pc y poder examinarla o modificarla */
    protected void exportDbExtStorage(){
        try {
            File sd = Environment.getExternalStorageDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "/data/data/" + getPackageName() + "/databases/asistencia.db";
                String backupDBPath = "asistencia_db.sqlite";
                File currentDB = new File(currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (Exception e) {

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_face_recognition);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        useEigenfaces = true;

        /* Estos valores vienen de la configuración por defecto de la librería. Allí pueden ser
           cambiados por el usuario, pero en este caso los quemaremos para simplificar. */
        faceThreshold = 0.15f;
        distanceThreshold = 0.15f;
        maximumImages = 50;
        /* ------------- */

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.camera_java_surface_view);
        mOpenCvCameraView.setCameraIndex(prefs.getInt("mCameraIndex", CameraBridgeViewBase.CAMERA_ID_FRONT));
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);

        findViewById(R.id.take_picture_button).setOnClickListener(new View.OnClickListener() {
            NativeMethods.MeasureDistTask mMeasureDistTask;

            @Override
            public void onClick(View v) {

                if (mMeasureDistTask != null && mMeasureDistTask.getStatus() != AsyncTask.Status.FINISHED) {
                    Log.i(TAG, "mMeasureDistTask is still running");
                    showToast("Still processing old image...", Toast.LENGTH_SHORT);
                    return;
                }
                if (mTrainFacesTask != null && mTrainFacesTask.getStatus() != AsyncTask.Status.FINISHED) {
                    Log.i(TAG, "mTrainFacesTask is still running");
                    showToast("Espere un momento... ", Toast.LENGTH_SHORT);
                    return;
                }

                /* --- Procesar la imagen tomada desde la camara --- */
                Log.i(TAG, "Gray height: " + mGray.height() + " Width: " + mGray.width() + " total: " + mGray.total());
                if (mGray.total() == 0)
                    return;
                Size imageSize = new Size(200, 200.0f / ((float) mGray.width() / (float) mGray.height())); // Scale image in order to decrease computation time
                Imgproc.resize(mGray, mGray, imageSize);
                Log.i(TAG, "Small gray height: " + mGray.height() + " Width: " + mGray.width() + " total: " + mGray.total());
                //SaveImage(mGray);

                Mat image = mGray.reshape(0, (int) mGray.total()); // Create column vector
                Log.i(TAG, "Vector height: " + image.height() + " Width: " + image.width() + " total: " + image.total());
                /* --- END ---*/

                /* Pruebas de encoding... */
//                Log.i(TAG, "IMAGENORIGINAL");
//                Log.i(TAG, image.toString());
//                Log.i(TAG, image.dump());

//                byte[] data = new byte[(int)(image.total()*image.channels())];
//                image.get(0, 0, data);
//                mEncoding = Base64.encodeToString(data, Base64.DEFAULT);
//
//                Log.i(TAG, "Byte Array encoded to String: ");
//                Log.i(TAG, mEncoding);

                /* Obtener fotos para construir la base */
                // showEnterLabelDialog();
                /* END */

                // Calculate normalized Euclidean distance
                mMeasureDistTask = new NativeMethods.MeasureDistTask(useEigenfaces, measureDistTaskCallback);
                mMeasureDistTask.execute(image);

                //El operador no deberia poder ingresar nuevas caras.
//                 showLabelsDialog();
            }
        });
    }

    private NativeMethods.MeasureDistTask.Callback measureDistTaskCallback = new NativeMethods.MeasureDistTask.Callback() {
        @Override
        public void onMeasureDistComplete(Bundle bundle) {
            if (bundle == null) {
                showToast("No se puedo hacer el reconocimiento", Toast.LENGTH_LONG);
                return;
            }

            float minDist = bundle.getFloat(NativeMethods.MeasureDistTask.MIN_DIST_FLOAT);
            if (minDist != -1) {
                int minIndex = bundle.getInt(NativeMethods.MeasureDistTask.MIN_DIST_INDEX_INT);
                float faceDist = bundle.getFloat(NativeMethods.MeasureDistTask.DIST_FACE_FLOAT);
                Log.i(TAG, "dist[" + minIndex + "]: " + minDist + ", face dist: " + faceDist );

                String minDistString = String.format(Locale.US, "%.4f", minDist);
                String faceDistString = String.format(Locale.US, "%.4f", faceDist);

                // Rostro reconocido
                if (faceDist < faceThreshold && minDist < distanceThreshold){ // 1. Near face space and near a face class
                    showToast("Operador Reconocido: " + imagesLabels.get(minIndex), Toast.LENGTH_SHORT);
//                    showToast("Operador Reconocido: " + mOperadores.get(minIndex).getNombre(), Toast.LENGTH_SHORT);

                    Log.i(TAG, "Detectado en imagesLabels: " + imagesLabels.get(minIndex));
                    Log.i(TAG, "Detectado en mOperadores: " + mOperadores.get(minIndex).getNombre());

                    Intent intent = new Intent(FaceRecognitionActivity.this, AsistenciaActivity.class);
                    intent.putExtra("idOperador", (long) (minIndex + 1)); // Index es 0-based. Sumar 1 para el id en la DB
                    startActivity(intent);

                }
                else if (faceDist < faceThreshold) // 2. Near face space but not near a known face class
                    showToast("Unknown face. Face distance: " + faceDistString + ". Closest Distance: " + minDistString, Toast.LENGTH_LONG);
                else if (minDist < distanceThreshold) // 3. Distant from face space and near a face class
                    showToast("No se reconoce", Toast.LENGTH_LONG);
                    //showToast("False recognition. Face distance: " + faceDistString + ". Closest Distance: " + minDistString, Toast.LENGTH_LONG);
                else // 4. Distant from face space and not near a known face class.
                    showToast("No se reconoce", Toast.LENGTH_LONG);
                    //showToast("Image is not a face. Face distance: " + faceDistString + ". Closest Distance: " + minDistString, Toast.LENGTH_LONG);
            }
            // TODO - Decidir si eliminar esto.
            else {
                /* TEMPORAL : PARA PERMITIR EL PASO A LA SIGUIENTE VISTA */
//                Intent intent = new Intent(FaceRecognitionActivity.this, AsistenciaActivity.class);
//                startActivity(intent);

                Log.w(TAG, "Array is null");
                if (useEigenfaces || uniqueLabels == null || uniqueLabels.length > 1)
                    showToast("Keep training...", Toast.LENGTH_SHORT);
                else
                    showToast("Fisherfaces needs two different faces", Toast.LENGTH_SHORT);
            }
        }
    };


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadOpenCV();
                } else {
                    showToast("Permission required!", Toast.LENGTH_LONG);
                    finish();
                }
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Read threshold values
//        float progress = prefs.getFloat("faceThreshold", -1);
//        if (progress != -1)
//            mThresholdFace.setProgress(progress);
//        progress = prefs.getFloat("distanceThreshold", -1);
//        if (progress != -1)
//            mThresholdDistance.setProgress(progress);
//        mMaximumImages.setProgress(prefs.getInt("maximumImages", 25)); // Use 25 images by default
    }

    @Override
    public void onStop() {
        super.onStop();
        // Store threshold values
        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat("faceThreshold", faceThreshold);
        editor.putFloat("distanceThreshold", distanceThreshold);
        editor.putInt("maximumImages", maximumImages);
        editor.putBoolean("useEigenfaces", useEigenfaces);
        editor.putInt("mCameraIndex", mOpenCvCameraView.mCameraIndex);
        editor.apply();

        // Store ArrayLists containing the images and labels
//        if (images != null && imagesLabels != null) {
//            tinydb.putListMat("images", images);
//            tinydb.putListString("imagesLabels", imagesLabels);
//        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Request permission if needed
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED/* || ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED*/)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA/*, Manifest.permission.WRITE_EXTERNAL_STORAGE*/}, PERMISSIONS_REQUEST_CODE);
        else
            loadOpenCV();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    /** Implementación de los métodos abstractos de CvCameraViewListener2*/

    @Override
    public void onCameraViewStarted(int width, int height) {
        mGray = new Mat();
        mRgba = new Mat();
    }

    @Override
    public void onCameraViewStopped() {
        mGray.release();
        mRgba.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Mat mGrayTmp = inputFrame.gray();
        Mat mRgbaTmp = inputFrame.rgba();

        // Flip image to get mirror effect
        int orientation = mOpenCvCameraView.getScreenOrientation();
        if (mOpenCvCameraView.isEmulator()) // Treat emulators as a special case
            Core.flip(mRgbaTmp, mRgbaTmp, 1); // Flip along y-axis
        else {
            switch (orientation) { // RGB image
                case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
                case ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT:
                    if (mOpenCvCameraView.mCameraIndex == CameraBridgeViewBase.CAMERA_ID_FRONT)
                        Core.flip(mRgbaTmp, mRgbaTmp, 0); // Flip along x-axis
                    else
                        Core.flip(mRgbaTmp, mRgbaTmp, -1); // Flip along both axis
                    break;
                case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
                case ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE:
                    if (mOpenCvCameraView.mCameraIndex == CameraBridgeViewBase.CAMERA_ID_FRONT)
                        Core.flip(mRgbaTmp, mRgbaTmp, 1); // Flip along y-axis
                    break;
            }
            switch (orientation) { // Grayscale image
                case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
                    Core.transpose(mGrayTmp, mGrayTmp); // Rotate image
                    if (mOpenCvCameraView.mCameraIndex == CameraBridgeViewBase.CAMERA_ID_FRONT)
                        Core.flip(mGrayTmp, mGrayTmp, -1); // Flip along both axis
                    else
                        Core.flip(mGrayTmp, mGrayTmp, 1); // Flip along y-axis
                    break;
                case ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT:
                    Core.transpose(mGrayTmp, mGrayTmp); // Rotate image
                    if (mOpenCvCameraView.mCameraIndex == CameraBridgeViewBase.CAMERA_ID_BACK)
                        Core.flip(mGrayTmp, mGrayTmp, 0); // Flip along x-axis
                    break;
                case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
                    if (mOpenCvCameraView.mCameraIndex == CameraBridgeViewBase.CAMERA_ID_FRONT)
                        Core.flip(mGrayTmp, mGrayTmp, 1); // Flip along y-axis
                    break;
                case ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE:
                    Core.flip(mGrayTmp, mGrayTmp, 0); // Flip along x-axis
                    if (mOpenCvCameraView.mCameraIndex == CameraBridgeViewBase.CAMERA_ID_BACK)
                        Core.flip(mGrayTmp, mGrayTmp, 1); // Flip along y-axis
                    break;
            }
        }

        mGray = mGrayTmp;
        mRgba = mRgbaTmp;

        return mRgba;
    }

    /* HELPER METHODS */

    private void showToast(String message, int duration) {
        if (duration != Toast.LENGTH_SHORT && duration != Toast.LENGTH_LONG)
            throw new IllegalArgumentException();
        if (mToast != null && mToast.getView().isShown())
            mToast.cancel(); // Close the toast if it is already open
        mToast = Toast.makeText(this, message, duration);
        mToast.show();
    }

    private void addLabel(String string) {
        String label = string.substring(0, 1).toUpperCase(Locale.US) + string.substring(1).trim().toLowerCase(Locale.US); // Make sure that the name is always uppercase and rest is lowercase
        imagesLabels.add(label); // Add label to list of labels
        Log.i(TAG, "Label: " + label);

        trainFaces(); // When we have finished setting the label, then retrain faces
    }

    private void showEnterLabelDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(FaceRecognitionActivity.this);
        builder.setTitle("Please enter your name:");

        final EditText input = new EditText(FaceRecognitionActivity.this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Submit", null); // Set up positive button, but do not provide a listener, so we can check the string before dismissing the dialog
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
//                images.remove(images.size() - 1); // Remove last image
            }
        });
        builder.setCancelable(false); // User has to input a name
        AlertDialog dialog = builder.create();

        // Source: http://stackoverflow.com/a/7636468/2175837
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {

                Button mButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                mButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String string = input.getText().toString().trim();
                        if (!string.isEmpty()) { // Make sure the input is valid
                            // If input is valid, dismiss the dialog and add the label to the array
                            dialog.dismiss();
//                            addLabel(string);
                            mPhotoName = string;
                            showToast("Capturada Foto de " + mPhotoName, Toast.LENGTH_LONG);
                        }
                    }
                });
            }
        });

        // Show keyboard, so the user can start typing straight away
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        dialog.show();
    }

    /**
     * Train faces using stored images.
     * @return  Returns false if the task is already running.
     */
    private boolean trainFaces() {
        if (images.isEmpty())
            return true; // The array might be empty if the method is changed in the OnClickListener

        if (mTrainFacesTask != null && mTrainFacesTask.getStatus() != AsyncTask.Status.FINISHED) {
            Log.i(TAG, "mTrainFacesTask is still running");
            return false;
        }

        Mat imagesMatrix = new Mat((int) images.get(0).total(), images.size(), images.get(0).type());
        for (int i = 0; i < images.size(); i++)
            images.get(i).copyTo(imagesMatrix.col(i)); // Create matrix where each image is represented as a column vector

        Log.i(TAG, "Images height: " + imagesMatrix.height() + " Width: " + imagesMatrix.width() + " total: " + imagesMatrix.total());

        // Train the face recognition algorithms in an asynchronous task, so we do not skip any frames
         if (useEigenfaces) {
            Log.i(TAG, "Training Eigenfaces");
            showToast("Espere un momento..." , Toast.LENGTH_SHORT);

            mTrainFacesTask = new NativeMethods.TrainFacesTask(imagesMatrix, trainFacesTaskCallback);
        }
        else {
            Log.i(TAG, "Training Fisherfaces");

            Set<String> uniqueLabelsSet = new HashSet<>(imagesLabels); // Get all unique labels
            uniqueLabels = uniqueLabelsSet.toArray(new String[uniqueLabelsSet.size()]); // Convert to String array, so we can read the values from the indices

            int[] classesNumbers = new int[uniqueLabels.length];
            for (int i = 0; i < classesNumbers.length; i++)
                classesNumbers[i] = i + 1; // Create incrementing list for each unique label starting at 1

            int[] classes = new int[imagesLabels.size()];
            for (int i = 0; i < imagesLabels.size(); i++) {
                String label = imagesLabels.get(i);
                for (int j = 0; j < uniqueLabels.length; j++) {
                    if (label.equals(uniqueLabels[j])) {
                        classes[i] = classesNumbers[j]; // Insert corresponding number
                        break;
                    }
                }
            }

            for (int i = 0; i < imagesLabels.size(); i++)
                Log.i(TAG, "Classes: " + imagesLabels.get(i) + " = " + classes[i]);

            Mat vectorClasses = new Mat(classes.length, 1, CvType.CV_32S); // CV_32S == int
            vectorClasses.put(0, 0, classes); // Copy int array into a vector

            mTrainFacesTask = new NativeMethods.TrainFacesTask(imagesMatrix, vectorClasses, trainFacesTaskCallback);
        }

        mTrainFacesTask.execute();

        return true;
    }

    private NativeMethods.TrainFacesTask.Callback trainFacesTaskCallback = new NativeMethods.TrainFacesTask.Callback() {
        @Override
        public void onTrainFacesComplete(boolean result) {
            if (result)
                showToast("Listo", Toast.LENGTH_SHORT);
            else
//                showToast("Training failed", Toast.LENGTH_LONG);
                showToast("Ocurrio un problema con el reconocimiento", Toast.LENGTH_LONG);
        }
    };

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                    NativeMethods.loadNativeLibraries(); // Load native libraries after(!) OpenCV initialization
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();

                    images = new ArrayList<>();
                    imagesLabels = new ArrayList<>();

                    /* Cargar los operadores de la base local
                       TODO - No funcionara hasta que se modifique bien la estructura de la base */
                    mOperadores = Operador.listAll(Operador.class);

                    Log.d(TAG, "" + mOperadores.size());

                    // Hasta eso

                    /* --- Obtener las fotos de cada operador, para hacer el reconocimiento de la foto que toma la actividad --- */
                    if (mOperadores != null && mOperadores.size() != 0){    // Si existen registros
                        for (Operador operador : mOperadores){
                            Log.i(TAG, "Cargado: " + operador.getNombre());
//                            Log.i(TAG, operador.getEncodedFaceData());

                            images.add(operador.getFaceMat());        // Agregar esta cara a images. El indice debe corresponder al id del operador -1
                            // Tambien corresponde al indice de la Lista operadores
                            imagesLabels.add(operador.getNombre());
                        }
                    }
                    /* --- END --- */


                    Log.i(TAG, "Number of images: " + images.size());
                    if (!images.isEmpty()) {
                        trainFaces(); // Train images after they are loaded
                        Log.i(TAG, "Images height: " + images.get(0).height() + " Width: " + images.get(0).width() + " total: " + images.get(0).total());
                    }

                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    private void loadOpenCV() {
        if (!OpenCVLoader.initDebug(true)) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }
}
