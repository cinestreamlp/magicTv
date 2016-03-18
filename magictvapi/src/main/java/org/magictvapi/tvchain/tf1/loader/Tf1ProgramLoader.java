package org.magictvapi.tvchain.tf1.loader;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.magictvapi.Config;
import org.magictvapi.loader.Loader;
import org.magictvapi.model.Program;
import org.magictvapi.tvchain.tf1.model.Tf1Program;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomas on 14/03/2016.
 */
public class Tf1ProgramLoader extends Loader<List<Program>> {

    private final static String IMAGE_URL = "http://api.mytf1.tf1.fr/image";
    private final static Integer IMG_WIDTH = 640;
    private final static Integer IMG_HEIGHT = 360;

    private final String folder;
    private String channel;

    public Tf1ProgramLoader(String folder, String channel) {
        this.channel = channel;
        this.folder = folder;
    }

    @Override
    protected List<Program> doInBackground(Void... params) {
        Tf1Database database = new Tf1Database(Config.context);

        SQLiteDatabase sqllitedatabase = database.getReadableDatabase();
        Cursor c = sqllitedatabase.rawQuery(
            "select id, name, thumbnailUrl from MYTProgram where channelIdsJSON like ? and category = ? order by category",
            new String[]{"%" + channel + "%", folder}
        );

        List<Program> programs = new ArrayList<>();
        while (c.moveToNext()) {
            String name = c.getString(c.getColumnIndex("name"));
            String image = c.getString(c.getColumnIndex("thumbnailUrl"));
            String programId = c.getString(c.getColumnIndex("id"));

            Tf1Program program = new Tf1Program();
            program.setTitle(name);
            program.setImageUrl(createImage(image));
            program.setProgramId(programId);
            programs.add(program);
        }
        sqllitedatabase.close();

        return programs;
    }

    public static String createImage(String image) {
        String url = IMAGE_URL + "/" +
                IMG_WIDTH + "/" +
                IMG_HEIGHT + "/" +
                image + "/" +
                md5(""+IMG_WIDTH+IMG_HEIGHT+image+"elk45sz6ers68").substring(0, 6);
        Log.d("ProgramLoader", image + "=>" + url);
        return url;
    }

    public static String md5(String str) {
        try {
            MessageDigest instance = MessageDigest.getInstance("MD5");
            instance.update(str.getBytes());
            byte[] digest = instance.digest();
            StringBuilder stringBuilder = new StringBuilder();
            for (byte b : digest) {
                String toHexString = Integer.toHexString(b & 255);
                while (toHexString.length() < 2) {
                    toHexString = "0" + toHexString;
                }
                stringBuilder.append(toHexString);
            }
            return stringBuilder.toString();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return "";
    }
}
