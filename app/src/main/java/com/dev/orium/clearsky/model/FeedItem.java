package com.dev.orium.clearsky.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by y.drobysh on 18.11.2014.
 */
public class FeedItem {

    String title;

    String description;

    public FeedItem(String title, String desc) {
        this.title = title;
        this.description = desc;
    }

    @Override
    public String toString() {
        return title;
    }

    public static List<FeedItem> getFakeData() {
        return new ArrayList<FeedItem>() {
            {add(new FeedItem("Title", "Description"));}
            {add(new FeedItem("Title1", "Description 1"));}
            {add(new FeedItem("Title2", "Description 2"));}
            {add(new FeedItem("Title3", "Description 2"));}
            {add(new FeedItem("Title4", "Description 33"));}
            {add(new FeedItem("Title5", "Description 4"));}
            {add(new FeedItem("Title6", "Description 5"));}
            {add(new FeedItem("Title7", "Description 6"));}
            {add(new FeedItem("Title8", "Description 7"));}
            {add(new FeedItem("Title9", "Description 8"));}
            {add(new FeedItem("Title10", "Description 9"));}

        };
    }

}
