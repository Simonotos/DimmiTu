package it.unimib.dimmitu.CercaInteressi;

import java.util.ArrayList;

public class ItemID {
    ArrayList<String> item_id;

    public ItemID(){
        item_id = new ArrayList<String>();
    }

    public void setItem_id(String word){
        item_id.add(word);
    }

    public ArrayList<String> getItem_id(){
        return item_id;
    }

    public String stampaLista(){
        String id="";

        for(int i=0; i<item_id.size(); i++)
            id = id + " " + item_id.get(i);

        return id;
    }

}
