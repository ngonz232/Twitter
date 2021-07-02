package com.codepath.apps.restclienttemplate.models;

import android.text.format.DateUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Tweet {

    public String body;
    public String createdAt;
    public String id;
    public User user;
    public String TAG = "TimelineActivity";
    public Boolean favorited;
    public Boolean retweeted;
    public Integer favoriteCount;
    public Integer retweetCount;
    public List<String> imageUrls;

    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.body = jsonObject.getString("full_text");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
        tweet.id = jsonObject.getString("id_str");
        tweet.favorited = jsonObject.getBoolean("favorited");
        tweet.retweeted = jsonObject.getBoolean("retweeted");
        tweet.favoriteCount = jsonObject.getInt("favorite_count");
        tweet.retweetCount = jsonObject.getInt("retweet_count");


        if (jsonObject.getJSONObject("entities").getJSONArray("urls").length() > 8) {
            tweet.body = tweet.body + "" + jsonObject.getJSONObject("entities").getJSONArray("urls").getJSONObject(0).getString("url");
        }

//        if (jsonObject.has("extended_entities")) {
//            if (jsonObject.getJSONObject("extended_entities").has("media")) {
//                JSONArray mediaObjects = jsonObject.getJSONObject("extended_entities").getJSONArray("media");
//                for (int i = 0; i < mediaObjects.length(); i++) {
//                    tweet.imageUrls.add(mediaObjects.getJSONObject(i).getString("media_url_https"));
//
//            }
////            JSONArray mediaObjects = jsonObject.getJSONObject("extended_entities").getJSONArray("media");
////            for (int i = 0; i < mediaObjects.length(); i++) {
////                tweet.imageUrls.add(mediaObjects.getJSONObject(i).getString("media_url_https"));
//            }
//        }else{
//            tweet.imageUrls = new ArrayList<>();
//        }

        tweet.imageUrls = new ArrayList<>();
        if (jsonObject.has("extended_entities")){
            JSONObject extended_entities = jsonObject.getJSONObject("extended_entities");
            if (extended_entities.has("media")) {
                JSONArray mediaObjects = extended_entities.getJSONArray("media");
                for (int i = 0; i < mediaObjects.length(); i++) {
                    if (mediaObjects.getJSONObject(i).has("media_url_https"))
                        tweet.imageUrls.add(mediaObjects.getJSONObject(i).getString("media_url_https"));
                }
            }
        }
        return tweet;

    }

    // constructor for parcelable
    public Tweet() {}

    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++){
         tweets.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return tweets;
    }

    public String getBody() {
        return body;
    }



    public User getUser() {
        return user;
    }

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    // getRelativeTimeAgo("Mon Jun 28 21:16:23 +0000 2021");
    public String getRelativeTimeAgo() {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(createdAt).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            Log.e("Tweet", "error getting relative time ago");
        }
        return relativeDate;
    }

    public String getCreatedAt() {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        String shownFormat = "hh:mmaa MM/dd/yy";
        SimpleDateFormat sfTwitter = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        SimpleDateFormat sfShown = new SimpleDateFormat(shownFormat, Locale.ENGLISH);
        sfTwitter.setLenient(true);
        sfShown.setLenient(true);
        String output = "";
        try {
            Date date = sfTwitter.parse(createdAt);
            output = sfShown.format(date);
        } catch (ParseException e) {
            Log.e("Tweet", "error parsing created at");
        }
        return output;
    }

}

