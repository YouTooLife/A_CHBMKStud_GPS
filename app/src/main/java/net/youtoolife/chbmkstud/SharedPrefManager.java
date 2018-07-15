package net.youtoolife.chbmkstud;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

/**
 * Created by youtoolife on 4/7/18.
 */

public class SharedPrefManager {

    private static final String SHARED_PREF_NAME = "fcmsharedprefdemo";
    private static final String KEY_ACCESS_TOKEN = "token";
    private static final String KEY_ACCESS_LOGIN = "login";
    private static final String KEY_ACCESS_INVITE = "invite";
    private static final String KEY_ACCESS_PARENT= "parent";
    private static final String KEY_LAST_ID_MSG = "0";
    //private static final String KEY_ACCESS_MSGS= "msgs";

    private static Context mCtx;
    private static SharedPrefManager mInstance;

    private SharedPrefManager(Context context) {
        mCtx = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null)
            mInstance = new SharedPrefManager(context);
        return mInstance;
    }

    public boolean storeToken(String token) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_ACCESS_TOKEN, token);
        editor.apply();
        return true;
    }


    public boolean storeLogin(String login, String invite) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_ACCESS_LOGIN, login);
        String a = (invite != null)? XA.a(invite) : null;
        editor.putString(KEY_ACCESS_INVITE, a);
        editor.apply();
        return true;
    }

    public boolean storeLastID(int lastID) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_LAST_ID_MSG, String.valueOf(lastID));
        editor.apply();
        return true;
    }

    public boolean storeParent(String parent) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_ACCESS_PARENT, parent);
        editor.apply();
        return true;
    }

    public int getLastID() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String result = sharedPreferences.getString(KEY_LAST_ID_MSG, null);
        if (result != null)
            return Integer.parseInt(result);
        else
            return 0;
    }

    public String getToken() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null);
    }

    public String getParent() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_ACCESS_PARENT, null);
    }


    public String getLogin() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_ACCESS_LOGIN, null);
    }

    public String getInvite() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        String b = null;
        String aa = sharedPreferences.getString(KEY_ACCESS_INVITE, null);
            if (aa != null) {
                //Log.d("GET_LOGIN", (aa != null) ? aa : "null");
                byte[] a = Base64.decode(aa, Base64.DEFAULT);
                b = XA.b(a);
                //Log.d("GET_LOGIN", (b != null) ? b : "null");
            }
        return b;
    }


    /*
    public boolean storeMsgs(String msgs) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_ACCESS_MSGS, msgs);
        editor.apply();
        return true;
    }

    public String getMsgs() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_ACCESS_MSGS, null);
    }
    */


}
