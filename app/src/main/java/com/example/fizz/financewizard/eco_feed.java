package com.example.fizz.financewizard;

import android.content.Context;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.widget.Toast;
public class eco_feed
{

    Context mContext;
    public eco_feed(Context context)
    {
        mContext =context;
    }
private final String ns = null;

        public List<rss_item> parse(InputStream inputStream) throws XmlPullParserException, IOException {
            try {
                XmlPullParser parser = Xml.newPullParser();
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(inputStream, null);
                parser.nextTag();
                return readFeed(parser);
            } finally {
                //inputStream.close();
            }
        }

        private List<rss_item> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {

            parser.require(XmlPullParser.START_TAG, null, "rss");
            String title = null;
            String link = null;
            String image = null;
            //      List<List<RssItem>> items = new ArrayList<List<RssItem>>();
            List<rss_item> items = new ArrayList<>();
            //List<List<String>> stuff = new ArrayList<List<String>>();
            //parser.next();
            while (parser.next() != XmlPullParser.END_DOCUMENT) {
                //if (flag==1){break;}
                  //  Toast.makeText(mContext,"count"+count,Toast.LENGTH_SHORT).show();continue;}
                if (parser.getEventType() != XmlPullParser.START_TAG ) {
                   // Toast.makeText(mContext, "Count=" + count,  Toast.LENGTH_SHORT).show();
                    continue;
                }
                    String name = parser.getName();
                    if (name.equals("title")) {
                        title = readTitle(parser);
                    } else if (name.equals("link")) {
                        link = readLink(parser);
                    }

                 /*else if (name.equals("image")){
                image = readLink(parser);
            `}*/
                if (title != null && link != null) {
                    rss_item item = new rss_item(title, link);
                    items.add(item);
                    title = null;
                    link = null;
                }
            }
            return items;
        }

        private String readLink(XmlPullParser parser) throws XmlPullParserException, IOException {
            parser.require(XmlPullParser.START_TAG, ns, "link");
            String link = readText(parser);
            parser.require(XmlPullParser.END_TAG, ns, "link");
            return link;
        }

        private String readTitle(XmlPullParser parser) throws XmlPullParserException, IOException {
            parser.require(XmlPullParser.START_TAG, ns, "title");
            String title = readText(parser);
            parser.require(XmlPullParser.END_TAG, ns, "title");
            return title;
        }

        // For the tags title and link, extract their text values.
        private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
            String result = "";
                if (parser.next() == XmlPullParser.TEXT) {
                    result = parser.getText();
                    parser.nextTag();
                }
            return result;
        }
}
