package org.magictvapi.channel.pluzz.loader;

import android.util.JsonReader;

import org.magictvapi.loader.Loader;
import org.magictvapi.model.Folder;
import org.magictvapi.channel.pluzz.model.PluzzFolder;
import org.magictvapi.channel.pluzz.model.PluzzProgram;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by thomas on 19/03/2016.
 */
public class PluzzFolderLoader extends Loader<List<Folder>> {

    private static final String INFO_URL = "http://pluzz.webservices.francetelevisions.fr/mobile/v1.3/liste/tri/nbvue/type/replay/chaine/%s/nb/200/debut/0";
    private static final String IMAGE_URL = "http://refonte.webservices.francetelevisions.fr";

    private static final String TAG = "PluzzFolderLoader";

    private static final Map<String, String> rubriques = new HashMap<>();
    static {
        rubriques.put("info","Info");
        rubriques.put("seriefiction","Série & fiction");
        rubriques.put("culture","Culture");
        rubriques.put("divertissement","Divertissement");
        rubriques.put("jeu","Jeu");
        rubriques.put("documentaire","Documentaire");
        rubriques.put("magazine","Magazine");
        rubriques.put("jeunesse","Jeunesse");
        rubriques.put("sport","Sport");
    }

    public String chain;

    public PluzzFolderLoader(String chain) {
        this.chain = chain;
    }

    @Override
    protected List<Folder> doInBackground(Void... params) {
        Calendar currentDate = Calendar.getInstance();
        String url = String.format(INFO_URL,
                chain,
                currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH) + 1, currentDate.get(Calendar.DAY_OF_MONTH)
        );

        try {
            JsonReader jsonReader = new JsonReader(new InputStreamReader(new URL(url).openStream()));
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                String name = jsonReader.nextName();
                if ("reponse".equals(name)) {
                    return readResponse(jsonReader);
                } else {
                    jsonReader.skipValue();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private List<Folder> readResponse(JsonReader jsonReader) {
        try {
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                String name = jsonReader.nextName();
                if ("emissions".equals(name)) {
                    return readEmissions(jsonReader);
                } else {
                    jsonReader.skipValue();
                }
            }
            jsonReader.endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<Folder> readEmissions(JsonReader jsonReader) {
        Map<String, PluzzFolder> folderMap = new HashMap<>();
        List<String> loadedCollections = new ArrayList<>();
        try {
            jsonReader.beginArray();
            while (jsonReader.hasNext()) {
                readProgram(jsonReader, folderMap, loadedCollections);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ArrayList<Folder>(folderMap.values());
    }

    private void readProgram(JsonReader jsonReader, Map<String, PluzzFolder> folderMap, List<String> loadedCollections) {
        try {
            String rubrique = null;
            String url = null;
            String codeProgram = "";
            PluzzProgram program = new PluzzProgram();

            jsonReader.beginObject();
            while (jsonReader.hasNext()) {

                String name = jsonReader.nextName();
                if ("id_programme".equals(name)) {
                    try {
                        program.setId(Integer.parseInt(jsonReader.nextString()));
                    } catch (NumberFormatException ex) {
                       // Log.e(TAG, ex.getMessage(), ex);
                    }
                } else if ("id_diffusion".equals(name)) {
                    program.setMainVideoId(Integer.parseInt(jsonReader.nextString()));
                } else if ("code_programme".equals(name)) {
                    codeProgram = jsonReader.nextString();
                } else if ("titre_programme".equals(name)) {
                    program.setTitle(jsonReader.nextString());
                } else if ("titre".equals(name)) {
                    if (program.getTitle() == null || program.getTitle().isEmpty()) {
                        program.setTitle(jsonReader.nextString());
                    } else {
                        jsonReader.skipValue();
                    }
                } else if ("rubrique".equals(name)) {
                    rubrique = jsonReader.nextString();
                } else if ("image_large".equals(name)) {
                    program.setBackgroundImageUrl(IMAGE_URL + jsonReader.nextString());
                } else if ("image_medium".equals(name)) {
                    program.setImageUrl(IMAGE_URL + jsonReader.nextString());
                } else if ("accroche_programme".equals(name)) {
                    program.setDescription(jsonReader.nextString());
                } else if ("accroche".equals(name)) {
                    if (program.getDescription() == null || program.getDescription().isEmpty()) {
                        program.setDescription(jsonReader.nextString());
                    } else {
                        jsonReader.skipValue();
                    }
                } else if ("url_video".equals(name)) {
                    url = jsonReader.nextString();
                } else {
                    jsonReader.skipValue();
                }
            }
            jsonReader.endObject();

            // add program on if it have a rubrique and have not already be added
            if (url != null && !url.isEmpty() && rubrique != null && (codeProgram.isEmpty() || !loadedCollections.contains(codeProgram))) {
                if (!folderMap.containsKey(rubrique)) {
                    PluzzFolder folder = new PluzzFolder();
                    folder.setTitle(rubriques.get(rubrique));
                    folderMap.put(rubrique, folder);
                }
                folderMap.get(rubrique).addProgram(program);
                loadedCollections.add(codeProgram);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
