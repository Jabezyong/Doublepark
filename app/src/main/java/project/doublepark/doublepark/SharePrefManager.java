package project.doublepark.doublepark;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by jabez on 18/3/2017.
 */

public class SharePrefManager {
    private static final String SHARED_PREF_NAME = "FCMSharedPref";
    private static final String TAG_TOKEN = "tagtoken";
    private static final String CAR_PLATE = "carplate";
    private static final String CONTACT_LIST = "contactlist";
    private static final String NAME = "name";
    private static final String EMAIL = "email";
    private static final String PROFILE_PICTURE = "profilepicture";
    private static SharePrefManager mInstance;
    private static Context mCtx;

    private SharePrefManager(Context context){
        mCtx = context;
    }

    public static synchronized SharePrefManager getInstance(Context context){
        if(mInstance == null){
            mInstance = new SharePrefManager(context);
        }
        return mInstance;
    }
    //this method will save the device token to shared preferences
    public boolean saveDeviceToken(String token){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TAG_TOKEN,token);
        editor.apply();
        return true;
    }
    public boolean saveCarPlate(String carplate){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CAR_PLATE,carplate);
        editor.apply();
        return true;
    }

    public boolean saveName(String name) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(NAME, name);
        editor.apply();
        return true;
    }

    public boolean saveEmail(String email) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(EMAIL, email);
        editor.apply();
        return true;
    }
    public boolean saveProfilePicUrl(String url) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PROFILE_PICTURE, url);
        editor.apply();
        return true;
    }
    public String getName() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        return sharedPreferences.getString(NAME,null);
    }

    public String getEmail() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        return sharedPreferences.getString(EMAIL,null);
    }

    public String getCarPlate(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        return sharedPreferences.getString(CAR_PLATE,null);
    }
    public String getDeviceToken(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        return sharedPreferences.getString(TAG_TOKEN,null);
    }
    public String getProfilePicture() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        return sharedPreferences.getString(PROFILE_PICTURE,null);
    }

    public boolean saveContactList(ArrayList<String> contactList) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //Save the contact list in a json string
        // allow it to pass around the app cause there will be multiple contact number
        Gson gson = new Gson();
        String json = gson.toJson(contactList);
        editor.putString(CONTACT_LIST,json);
        editor.apply();
        return true;
    }

    public ArrayList<String> getContactList() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(CONTACT_LIST,"");

        //Convert the json into a array list type
        Type type = new TypeToken<ArrayList<String>>(){}.getType();

        ArrayList<String> contactList = gson.fromJson(json,type);

        return contactList;


    }

    public void clearAll(){
        mInstance.clearAll();
    }


}
