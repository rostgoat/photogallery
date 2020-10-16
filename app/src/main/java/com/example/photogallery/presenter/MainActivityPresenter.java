package com.example.photogallery.presenter;

import java.io.File;
import java.util.ArrayList;

public class MainActivityPresenter {

private final View view;
    private ArrayList<String> photos = null;
    private int index = 0;

    public MainActivityPresenter(View view) {
        this.view = view;
    }

    public void updatePhoto(String path, String caption) {
        String[] attr = path.split("_");
        if (attr.length >= 3) {
            File to = new File(attr[0] + "_" + caption + "_" + attr[2] + "_" + attr[3]);
            File from = new File(path);
            from.renameTo(to);
            photos.set(index, to.toString());
        }
    }

    public interface View {
        void updatePhoto(String path, String caption);
    }

}


