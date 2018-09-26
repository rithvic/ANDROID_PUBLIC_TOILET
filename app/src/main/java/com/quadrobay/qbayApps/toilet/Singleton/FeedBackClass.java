package com.quadrobay.qbayApps.toilet.Singleton;

import android.graphics.Bitmap;

public class FeedBackClass {

    private String screen;
    private String summary;
    private String suggestion;
    private Bitmap bitmap;

    public void setScreen(String screen) {
        this.screen = screen;
    }

    public String getScreen() {
        return screen;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getSummary() {
        return summary;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

}
