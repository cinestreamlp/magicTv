package org.magictvapi;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.magictvapi.model.TvChain;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by thomas on 16/03/2016.
 */
public class TvChainManager {

    /**
     * list of enabled chains
     */
    private static List<TvChain> chains = new ArrayList<>();
    private static Properties properties;

    private static void initProperties() {
        if (properties != null) {
            return;
        }

        try {
            InputStream inputStream = Config.context.getAssets().open("tvchains.properties");
            properties = new Properties();
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
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

                TvChain chain = (TvChain) chainClazz.newInstance();
                chain.setTitle(title);
                chain.setImageUrl(image);
                chain.setId(Integer.parseInt(id));
                chains.add(chain);
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
                    Log.d("TvChainManager", "init class " + initClazz);
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

    public static List<TvChain> getChains() {
        return chains;
    }
}
