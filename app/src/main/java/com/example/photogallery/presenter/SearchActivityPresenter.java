package com.example.photogallery.presenter;

public class SearchActivityPresenter {
    private final View view;

    public SearchActivityPresenter(View view) {
        this.view = view;
    }

    public interface View {
        void cancel();
        void go();
    }
}


