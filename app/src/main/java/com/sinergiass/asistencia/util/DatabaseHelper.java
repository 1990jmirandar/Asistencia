package com.example.myfacerecognitiontest;


import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * Created by avera on 20/09/17.
 * Esta Clase y la libraria SQLiteAssetHelper permiten inicializar la base local a partir de
 * una base de datos ubicada en assets/databases/
 */

public class DatabaseHelper extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "asistencia.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
}