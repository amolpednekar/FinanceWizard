package com.example.fizz.financewizard;

/**
 * Created by Simeon on 23/09/2015.
 */
public class rss_item {
    private final String title;
    private final String link;

    public rss_item(String title, String link) {
        this.title = title;
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }
}
