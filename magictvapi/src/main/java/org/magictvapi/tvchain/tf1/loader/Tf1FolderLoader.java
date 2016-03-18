package org.magictvapi.tvchain.tf1.loader;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.magictvapi.Config;
import org.magictvapi.loader.Loader;
import org.magictvapi.model.Folder;
import org.magictvapi.tvchain.tf1.model.Tf1Folder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomas on 14/03/2016.
 */
public class Tf1FolderLoader extends Loader<List<Folder>> {

    private String chainName;

    public Tf1FolderLoader(String chainName) {
        this.chainName = chainName;
    }

    @Override
    protected List<Folder> doInBackground(Void... params) {
        Tf1Database database = new Tf1Database(Config.context);

        SQLiteDatabase sqllitedatabase = database.getReadableDatabase();
        Cursor c = sqllitedatabase.rawQuery("select distinct category from MYTProgram where channelIdsJSON like ? order by category", new String[]{"%"+chainName+"%"});

        List<Folder> folders = new ArrayList<>();
        while (c.moveToNext()) {
            String name = c.getString(c.getColumnIndex("category"));
            Folder folder = new Tf1Folder(this.chainName);
            folder.setTitle(name);
            folders.add(folder);
        }
        sqllitedatabase.close();

        return folders;
    }
}
