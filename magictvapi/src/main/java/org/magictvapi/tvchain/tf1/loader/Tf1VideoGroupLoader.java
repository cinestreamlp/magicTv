package org.magictvapi.tvchain.tf1.loader;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.magictvapi.Config;
import org.magictvapi.loader.Loader;
import org.magictvapi.model.VideoGroup;
import org.magictvapi.tvchain.tf1.model.Tf1Video;
import org.magictvapi.tvchain.tf1.model.Tf1VideoGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by thomas on 14/03/2016.
 */
public class Tf1VideoGroupLoader extends Loader<List<VideoGroup>> {
    private String programId;

    private static Map<String, Integer> ORDER = new HashMap<>();
    static {
        ORDER.put("Replay", 1);
        ORDER.put("Bonus", 2);
        ORDER.put("Extrait", 3);
    }

    public Tf1VideoGroupLoader(String programId) {
        this.programId = programId;
    }

    @Override
    protected List<VideoGroup> doInBackground(Void... params) {
        Tf1Database database = new Tf1Database(Config.context);

        SQLiteDatabase sqllitedatabase = database.getReadableDatabase();
        Cursor c = sqllitedatabase.rawQuery(
                "select substr(substr(videoTypeDescriptorJSON, 11),0, instr(substr(videoTypeDescriptorJSON, 11), '\"')) cat, title, summary, thumbnailUrl, videoLength, creationDate, streamId " +
                        " from MYTVideo " +
                        " where programId = ?" +
                        " order by cat",
                new String[]{programId});

        List<VideoGroup> folders = new ArrayList<>();
        Tf1VideoGroup current = null;
        while (c.moveToNext()) {
            String name = c.getString(c.getColumnIndex("title"));
            String summary = c.getString(c.getColumnIndex("summary"));
            String cat = c.getString(c.getColumnIndex("cat"));
            String image = c.getString(c.getColumnIndex("thumbnailUrl"));
            Long duration = c.getLong(c.getColumnIndex("videoLength"));
            String streamId = c.getString(c.getColumnIndex("streamId"));
            if (!"Playlist".equals(cat)) {
                Calendar publicationDate = Calendar.getInstance();
                publicationDate.setTimeInMillis(c.getLong(c.getColumnIndex("creationDate")) * 1000);

                // new VideoGroup
                if (current == null || !current.getTitle().equals(cat)) {
                    current = new Tf1VideoGroup();
                    current.setTitle(cat);
                    folders.add(current);
                }

                Tf1Video video = new Tf1Video();
                video.setTitle(name);
                video.setDescription(summary);
                video.setImageUrl(Tf1ProgramLoader.createImage(image));
                video.setDuration(duration * 1000);
                video.setPublicationDate(publicationDate);
                video.setStreamId(streamId);
                current.addVideo(video);
            }
        }
        sqllitedatabase.close();

        Collections.sort(folders, new Comparator<VideoGroup>() {
            @Override
            public int compare(VideoGroup lhs, VideoGroup rhs) {
                Integer order1 = ORDER.get(lhs.getTitle());
                Integer order2 = ORDER.get(rhs.getTitle());
                return order1 == null ? -1 : order2 == null ? 1 :order1 - order2 ;
            }
        });
        return folders;
    }
}
