package org.magictvapi.channel.d8.loader;

import android.util.JsonReader;

import org.magictvapi.channel.d8.model.D8Folder;
import org.magictvapi.channel.d8.model.PreloadedD8Program;
import org.magictvapi.loader.Loader;
import org.magictvapi.model.Folder;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomas on 25/03/2016.
 */
public class D8FolderLoader extends Loader<List<Folder>> {

    private final static String INFO_URL = "http://static2.canalplus.fr/config_applications/%s/prodv1/%sstart_androidphone.json";
    private final static String VIDEO_LIST_URL = "http://service.canal-plus.com/video/rest/listeVideos/%s/";

    private final String channelLower;
    private final String channelUpper;

    public D8FolderLoader(String channel) {
        this.channelUpper = channel.toUpperCase();
        this.channelLower = channel.toLowerCase();
    }

    @Override
    protected List<Folder> doInBackground(Void... params) {
        try {
            String url = String.format(INFO_URL, channelUpper, channelLower);
            JsonReader jsonReader = new JsonReader(new InputStreamReader(new URL(url).openStream()));
            jsonReader.beginArray();
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                String name = jsonReader.nextName();
                if ("nomenclature".equals(name)) {
                    return readFolders(jsonReader);
                } else {
                    jsonReader.skipValue();
                }
            }
            jsonReader.endObject();
            jsonReader.endArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private List<Folder> readFolders(JsonReader jsonReader) {
        List<Folder> folders = new ArrayList<>();

        try {
            jsonReader.beginArray();
            while (jsonReader.hasNext()) {
                folders.add(readFolder(jsonReader));
            }
            jsonReader.endArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return folders;
    }

    private Folder readFolder(JsonReader jsonReader) {
        D8Folder folder = new D8Folder();
        try {
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                String name = jsonReader.nextName();
                if ("title".equals(name)) {
                    folder.setTitle(jsonReader.nextString());
                } else if ("content".equals(name)) {
                    setProgramTargetUrls(jsonReader, folder);
                } else {
                    jsonReader.skipValue();
                }
            }
            jsonReader.endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return folder;
    }

    private void setProgramTargetUrls(JsonReader jsonReader, D8Folder folder) {
        try {
            jsonReader.beginArray();
            while (jsonReader.hasNext()) {
                folder.addTargetUrl(readTargetUrl(jsonReader));
            }
            jsonReader.endArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private PreloadedD8Program readTargetUrl(JsonReader jsonReader) {
        PreloadedD8Program prog = new PreloadedD8Program();

        String targetUrl = "";
        try {
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                String name = jsonReader.nextName();
                if ("targetURL".equals(name)) {
                    prog.setTargetUrl(String.format(VIDEO_LIST_URL, channelLower) + jsonReader.nextString());
                } else if ("title".equals(name)) {
                    prog.setTitle(jsonReader.nextString());
                } else {
                    jsonReader.skipValue();
                }
            }
            jsonReader.endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return prog;
    }
}
