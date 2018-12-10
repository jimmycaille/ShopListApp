package shoplistcompanion.caillej.jcinformatics.ch;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class MyPrefs implements Serializable {
    public ArrayList<String> listsNames;
    public ArrayList<ArrayList<String>> listsContent;
    public ArrayList<ArrayList<Boolean>> listsStates;
    public MyPrefs(){
        this.listsNames   = new ArrayList<String>();
        this.listsContent = new ArrayList<ArrayList<String>>();
        this.listsStates  = new ArrayList<ArrayList<Boolean>>();
    }
}
