package shoplistcompanion.caillej.jcinformatics.ch;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class DataHolder {
    //tag for debug purpose
    private static final String TAG = DataHolder.class.getSimpleName();
    //config file name
    private static final String FILENAME="prefs";
    //static vars
    private static MyPrefs prefs;
    private static Context c;
    public MyPrefs getPrefs() {return prefs;}
    //public void setPrefs(MyPrefs prefs) {this.prefs = prefs;}

    //dont init here but in getInstance
    private static DataHolder holder = null;
    public static DataHolder getInstance(Context context) {
        c = context;
        if(holder==null)
            holder = new DataHolder(); //so the context exists
        return holder;
    }
    private DataHolder(){
        loadPrefs();
    }
    public void loadPrefs(){
        try {
            FileInputStream fos = c.openFileInput(FILENAME);
            ObjectInputStream reader = new ObjectInputStream(fos);
            this.prefs = (MyPrefs)reader.readObject();
            fos.close();
            Log.d(TAG,"loadPrefs() : Sucessfully");
        } catch (FileNotFoundException e) {
            Log.d(TAG,"loadPrefs() : FileNotFoundException");
            this.prefs = new MyPrefs();
        } catch (IOException e) {
            Log.d(TAG,"loadPrefs() : IOException");
            this.prefs = new MyPrefs();
        } catch (ClassNotFoundException e) {
            Log.d(TAG,"loadPrefs() : ClassNotFoundException");
            this.prefs = new MyPrefs();
        }
    }
    public void savePrefs(){
        try{
            FileOutputStream fos = c.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            ObjectOutputStream writer = new ObjectOutputStream(fos);
            writer.writeObject(this.prefs);
            fos.close();
            Log.d(TAG,"savePrefs(): Successfully");
        } catch (FileNotFoundException e) {
            Log.d(TAG,"savePrefs(): FileNotFoundException");
        } catch (IOException e) {
            Log.d(TAG,"savePrefs(): IOException");
        }
    }

}
