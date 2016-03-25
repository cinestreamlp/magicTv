package org.magictvapi.channel.tf1.loader;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Calendar;

/**
 * Created by thomas on 14/03/2016.
 */
public class Tf1Database extends SQLiteOpenHelper {

    private final String DATABASE_URL = "http://api.mytf1.tf1.fr/mobile/init/sql?device=ios-tablet";

    private final static String DB_NAME = "init";

    private Context context;

    private static Calendar refreshTime;


    /**
     * mydatabase
     */
    private SQLiteDatabase myDataBase;

    public Tf1Database(Context context) {
        super(context, DB_NAME, null, 1);
        this.context = context;
        try {
            this.createDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public synchronized void createDataBase() throws IOException {
        File databaseFile = context.getDatabasePath(DB_NAME);
        boolean needRefresh = false;
        if (databaseFile.lastModified() != 0) {
            Calendar modificationDate = Calendar.getInstance();
            modificationDate.setTimeInMillis(databaseFile.lastModified());
            int modificationDay = modificationDate.get(Calendar.DAY_OF_YEAR);
            int today = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
            needRefresh = modificationDay != today; // refresh if two days are different (00:00 is passed)
        }

        if (!databaseFile.exists() || needRefresh) {

            try {
                copyDataBase();
                refreshTime = Calendar.getInstance();
            } catch (IOException e) {

                Log.e("Tf1Database", e.getMessage(), e);
                throw new Error("Error copying database", e);

            }
        }

    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copyDataBase() throws IOException{
        File databaseFile = context.getDatabasePath(DB_NAME);

        databaseFile.delete();
        boolean success = databaseFile.getParentFile().mkdir();
        if (!success) {
            Log.e("Tf1Database ", "Erreur lors de la création du repertoire database");
        }
        success = databaseFile.createNewFile();
        if (!success) {
            Log.e("Tf1Database ", "Erreur lors de la création du fichier database");
        }

        Log.e("Tf1Database ", "databaseFile : " + databaseFile);
        //Open your local db as the input stream
        InputStream myInput = new URL(DATABASE_URL).openStream();

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(databaseFile);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    @Override
    public synchronized void close() {

        if(myDataBase != null)
            myDataBase.close();

        super.close();

    }


    public void refresh() {
        try {
            this.createDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
