package com.sinergiass.asistencia.facerecog;


import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.sinergiass.asistencia.model.Operador;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.List;

import ch.zhaw.facerecognitionlibrary.Helpers.FileHelper;
import ch.zhaw.facerecognitionlibrary.Helpers.MatName;
import ch.zhaw.facerecognitionlibrary.Helpers.PreferencesHelper;
import ch.zhaw.facerecognitionlibrary.PreProcessor.PreProcessorFactory;
import ch.zhaw.facerecognitionlibrary.Recognition.Recognition;
import ch.zhaw.facerecognitionlibrary.Recognition.RecognitionFactory;

public class Training {
    private static final String TAG = "Training";

    public static class TrainTask extends AsyncTask<Void, Void, Boolean>{

        private Context context;
        private final Callback callback;

        public TrainTask(Context context, Callback callback){
                this.context = context;
                this.callback = callback;
            }

            public interface Callback {
                void onTrainTaskComplete(boolean result);
            }

            @Override
            protected Boolean doInBackground(Void... voids) {

            List<Operador> operadores = Operador.listAll(Operador.class);

            PreProcessorFactory ppF = new PreProcessorFactory(context);
            PreferencesHelper preferencesHelper = new PreferencesHelper(context);
            preferencesHelper.getTensorFlowModelFile();
//            String algorithm = preferencesHelper.getClassificationMethod();
            String algorithm = context.getResources().getString(ch.zhaw.facerecognitionlibrary.R.string.tensorflow);

            FileHelper fileHelper = new FileHelper();
            fileHelper.createDataFolderIfNotExsiting();
//            final File[] persons = fileHelper.getTrainingList();
//            if (persons.length > 0) {
            if (operadores.size() > 0) {
                Recognition rec = RecognitionFactory.getRecognitionAlgorithm(context, Recognition.TRAINING, algorithm);
                for (Operador op : operadores) {
//                    if (person.isDirectory()){
                    if (op.fotos().size() > 0){
//                        File[] files = person.listFiles();
//                        int counter = 1;
//                        for (File file : files) {
                        for (Mat imgRgb : op.fotos()) {
//                            if (FileHelper.isFileAnImage(file)){
//                                Mat imgRgb = Imgcodecs.imread(file.getAbsolutePath());
//                                Imgproc.cvtColor(imgRgb, imgRgb, Imgproc.COLOR_BGRA2RGBA);
                                Mat processedImage = new Mat(160, 160, CvType.CV_8UC4);
                                imgRgb.copyTo(processedImage);
                                List<Mat> images = ppF.getProcessedImage(processedImage, PreProcessorFactory.PreprocessingMode.RECOGNITION);
                                if (images == null || images.size() > 1) {
                                    // More than 1 face detected --> cannot use this file for training
                                    continue;
                                } else {
                                    processedImage = images.get(0);
                                }
                                if (processedImage.empty()) {
                                    continue;
                                }
                                // The last token is the name --> Folder name = Person name
//                                String[] tokens = file.getParent().split("/");
//                                final String name = tokens[tokens.length - 1];

//                                MatName m = new MatName("processedImage", processedImage);
//                                fileHelper.saveMatToImage(m, FileHelper.DATA_PATH);

//                                rec.addImage(processedImage, op.getNombre(), false);
                                rec.addImage(imgRgb, Integer.toString(op.getIdOperador()), false);

//                                      fileHelper.saveCroppedImage(imgRgb, ppF, file, name, counter);

                                // Update screen to show the progress
//                                final int counterPost = counter;
//                                final int filesLength = files.length;
//                                progress.post(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        progress.append("Image " + counterPost + " of " + filesLength + " from " + name + " imported.\n");
//                                    }
//                                });

//                                counter++;
//                            }
                        }
                    }
                }
//                final Intent intent = new Intent(context, MainActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                if (rec.train()) {
//                    intent.putExtra("training", "Training successful");
                    Log.d(TAG, "Entranamiento Exitoso");
                    return true;
                } else {
                    Log.d(TAG, "Entranamiento Fallido");
                    return false;
                }
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        startActivity(intent);
//                    }
//                });
            }
//            else {
//                Thread.currentThread().interrupt();
//            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            callback.onTrainTaskComplete(result);
            if (result){
                Toast.makeText(context, "Modulo de Reconocimiento entrenado con Exito!", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(context, "Error al entrenar el Modulo de Reconocimiento", Toast.LENGTH_SHORT).show();
            }

        }
    }
}
