package net.appitiza.android.lifedrop.cache;

import android.content.Context;


import net.appitiza.android.lifedrop.app.AppLog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class CacheUtility {
    public static void writeCache(Context context, String cache, Object data)
    {

        try {
            writeObject(context,cache,data);
        } catch (IOException e) {
            AppLog.logErrorString(e.getMessage());
        }

    }
    public static Object readCache(Context context, String cache)
    {
        try {
            return  readObject(context,cache);
        } catch (IOException e) {
            return null;
        } catch (ClassNotFoundException e) {
        } return null;
    }
    public static void clearCache(Context context, String cache)
    {
        try {
            clearObject(context,cache);
        } catch (IOException e) {
            AppLog.logErrorString(e.getMessage());
        }
    }
    private static void writeObject(Context context, String key, Object object) throws IOException {
        FileOutputStream fos = context.openFileOutput(key, Context.MODE_PRIVATE);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(object);
        oos.close();
        fos.close();
    }

    private static Object readObject(Context context, String key) throws IOException,
            ClassNotFoundException {
        FileInputStream fis = context.openFileInput(key);
        ObjectInputStream ois = new ObjectInputStream(fis);
        return ois.readObject();
    }
    private static void clearObject(Context context, String key)  throws IOException {
        FileOutputStream fos = context.openFileOutput(key, Context.MODE_PRIVATE);
        fos.close();
        String path = context.getFilesDir().getAbsolutePath() + "/" + key;
        File file = new File(path);
        file.delete();
    }
}
