package project.doublepark.doublepark;

/**
 * Created by jabez on 19/4/2017.
 */

import android.graphics.Bitmap;
import android.nfc.Tag;
import android.support.v4.util.LruCache;

public class Cache {

    private static Cache instance;
    private LruCache<Object, Object> lru;

    private Cache() {

        lru = new LruCache<Object, Object>(1024);

    }

    public static Cache getInstance() {

        if (instance == null) {

            instance = new Cache();
        }

        return instance;

    }
    public void saveProfileImage(Bitmap bitmap){
        lru.put(Tags.PERSONAL_PROFILE,bitmap);
    }

    public void clearAll(){
        lru.evictAll();
    }
    public LruCache<Object, Object> getLru() {
        return lru;
    }
}
