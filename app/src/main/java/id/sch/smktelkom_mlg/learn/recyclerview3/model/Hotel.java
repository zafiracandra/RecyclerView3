package id.sch.smktelkom_mlg.learn.recyclerview3.model;

import android.graphics.drawable.Drawable;

/**
 * Created by Zafira Candra on 21/02/2017.
 */

public class Hotel {
    public String judul;
    public String deskripsi;
    public Drawable foto;

    public Hotel(String judul, String deskripsi, Drawable foto) {
        this.judul = judul;
        this.deskripsi = deskripsi;
        this.foto = foto;
    }

}