package com.typ.typnotes.Session;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
public class SessionCookieManager {
    private static CookieManager cookieManager;

    public static synchronized CookieManager getInstance() {
        if (cookieManager == null) {
            cookieManager = new CookieManager();
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        }
        return cookieManager;
    }

    public static void setCookie(String cookie) {
        if (cookieManager != null) {
            cookieManager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
        }
    }
}
