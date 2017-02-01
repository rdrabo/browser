package com.example.admin.mybrowser;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MyAppSettings {
    private static String PKG = "com.example.admin.mybrowser";
    private static final String P_STARS = PKG + ".prefs.stars";

    Context ctx;
    Gson gson;
    private ArrayList<String> _starSites = new ArrayList<>();

    public MyAppSettings(Context pCtx) {
        ctx = pCtx;
        gson = new Gson();
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        String momString = "";

        momString = prefs.getString(P_STARS, "");
        if(!momString.equals("")){
            _starSites = gson.fromJson(momString, (new ArrayList<String>()).getClass());
        }

    }

    public void save() {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = prefs.edit();
        String appoggio = "";
        appoggio = gson.toJson(_starSites);
        editor.putString(P_STARS, appoggio);
        editor.commit();
    }

    public ArrayList<String> get_starSites() {
        return _starSites;
    }

    public void set_starSites(ArrayList<String> pAddress) {
        this._starSites = pAddress;
    }

    public void addSiteToStar(String pAddress){
        this._starSites.add(pAddress);
        Collections.sort(this._starSites, new Comparator<String>() {
            @Override
            public int compare(String s, String t1) {
                return s.compareToIgnoreCase(t1);
            }
        });
        save();
    }

    public void removeSiteFromStar(String pAddress){
        this._starSites.remove(pAddress);
        save();
    }
}
