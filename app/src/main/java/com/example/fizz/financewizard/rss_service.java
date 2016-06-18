package com.example.fizz.financewizard;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.List;
//IntentService is a base class for Services that handle asynchronous requests
public class rss_service extends IntentService {

    public static final String ITEMS = "items";
    public static final String RECEIVER = "receiver";
    public static String RSS_LINK = "http://timesofindia.indiatimes.com/rssfeeds/1898055.cms";
    public rss_service() {
        super("RssService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(Constants.TAG, "Service started");
        List<rss_item> rssItems = null;
        try {
            eco_feed parser = new eco_feed(rss_service.this);
            rssItems = parser.parse(getInputStream(RSS_LINK));
        } catch (XmlPullParserException e) {
            Log.w(e.getMessage(), e);
        } catch (IOException e) {
            Log.w(e.getMessage(), e);
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable(ITEMS, (Serializable) rssItems);
        ResultReceiver receiver = intent.getParcelableExtra(RECEIVER); //Receive data from intent
        receiver.send(0, bundle);
    }

    public InputStream getInputStream(String link) {
        try {
            URL url = new URL(link);
            return url.openConnection().getInputStream();
        } catch (IOException e) {
            Log.w(Constants.TAG, "Exception while retrieving the input stream", e);
            return null;
        }
    }
}
//end