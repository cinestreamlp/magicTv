package com.allreplay.magicTv;


import android.util.Log;

import org.magictvapi.cookies.CookieManager;

import java.net.CookieHandler;
import java.net.CookiePolicy;

/**
 * Created by thomas on 07/09/16.
 */
public class InitCookies {
    static CookieManager defaultCookieManager;
    static {
        defaultCookieManager = new CookieManager();
        defaultCookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
    }

    private static boolean initOk = false;

    public static void initialize() {
        if (initOk) {
            return;
        }
        Log.d("InitCookies", "Initialize cookieHandler");
        initOk = true;
        CookieHandler currentHandler = CookieHandler.getDefault();
        if (currentHandler != defaultCookieManager) {
            CookieHandler.setDefault(defaultCookieManager);
        }
    }
}
