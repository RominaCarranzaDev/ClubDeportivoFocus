package com.example.clubfocus.helperDB

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ClubFocusDBHelper(context: Context) :
    SQLiteOpenHelper(context, "ClubFocus.db", null, 1) {

        override fun onCreate(db: SQLiteDatabase) {
            DBTablas.crearTodasLasTablas(db)
            DBTablas.cargarAdministradorInicial(db)
            DBTablas.cargarDatosPrueba(db)
        }

        override fun onUpgrade(
            db: SQLiteDatabase,
            oldVersion: Int,
            newVersion: Int
        ) {
            //To Do
        }

        fun inicializarBaseDatos() {
            writableDatabase
        }
}