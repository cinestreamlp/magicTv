package org.magictvapi;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.magictvapi.model.Channel;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by thomas on 16/03/2016.
 */
public class ChannelManager {

    /**
     * list of enabled chains
     */
    private static Map<Integer, Channel> chains = new HashMap<>();
    private static List<Channel> listChains = new ArrayList<>();

    private static Properties properties;

    private synchronized static void initProperties() {
        if (properties != null && !chains.isEmpty()) {
            Log.d("ChannelManager", "Le fichier de propriété a déjà été chargé");
            Log.d("ChannelManager", chains.size() + " chargées.");
            return;
        }
        properties = new Properties();

        try {
            InputStream inputStream = ChannelManager.class.getClassLoader().getResourceAsStream("assets/tvchains.properties");
            if (inputStream == null) {
                Log.e("ChannelManager", "Erreur lors du chargement du fichier de propriété.");
            }
            properties.load(inputStream);
            Log.d("ChannelManager", "Le chargement des chaines c'est bien déroulé");
        } catch (Exception e) {
            Log.e("ChannelManager", e.getMessage(), e);
        }
    }

    public static void load() {
        if (!chains.isEmpty()) {
            return;
        }
        try {
            initProperties();

            String[] schains = properties.getProperty("chains").split(",");
            for (String tvchain: schains) {
                String title = properties.getProperty("chains."+tvchain+".title");
                String clazz = properties.getProperty("chains."+tvchain+".class");
                String id = properties.getProperty("chains."+tvchain+".id");
                String image = properties.getProperty("chains."+tvchain+".image");

                Class chainClazz = Class.forName(clazz);

                Channel chain = (Channel) chainClazz.newInstance();
                chain.setTitle(title);
                chain.setImageUrl(image);
                chain.setId(Integer.parseInt(id));
                chains.put(chain.getId(), chain);
                listChains.add(chain);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * initialize chains
     */
    public static void initialize(Context context) {
        initProperties();
        String value = properties.getProperty("initializer");
        if (value != null && !value.trim().isEmpty()) {
            String[] clazzs = value.split(",");
            for (String clazz: clazzs) {
                Class initClazz = null;
                try {
                    initClazz = Class.forName(clazz);
                    AsyncTask<Context, Void, Void> chain = (AsyncTask<Context, Void, Void>) initClazz.newInstance();
                    chain.execute(context);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static List<Channel> getChains() {
        load();
        return listChains;
    }

    public static Channel getChannel(int channelId) {
        load();
        return chains.get(channelId);
    }
}
