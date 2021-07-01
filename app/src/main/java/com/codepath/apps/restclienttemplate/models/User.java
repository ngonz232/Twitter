package com.codepath.apps.restclienttemplate.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class User {
    public String name;
    public String screenName;
    public String profileImageUrl;
    public String id;
    public String profileUrl; // user's profile URL
    public String accountDescription; // user provided account description
    public boolean verifiedAccount; // if the account is a verified Twitter account
    public String createdDate; // When the user's account was created
    public String profileBannerUrl; // User's DP Banner
    public Integer followerCount; // how many followers the user has
    public Integer friendsCount; // how many friends the user has
    public String accountLocation; // user's defined account location

    public static User fromJson(JSONObject jsonObject) throws JSONException {
        User user = new User();
        user.name = jsonObject.getString("name");
        user.screenName = jsonObject.getString("screen_name");
        user.profileImageUrl = jsonObject.getString("profile_image_url_https");
        user.createdDate = jsonObject.getString("created_at");
        user.followerCount = jsonObject.getInt("followers_count");
        user.friendsCount = jsonObject.getInt("friends_count");
        user.verifiedAccount = jsonObject.getBoolean("verified");
        user.profileUrl = jsonObject.getString("url");
        user.accountDescription = jsonObject.getString("description");
        user.accountLocation = jsonObject.getString("location");

        if (jsonObject.has("profile_banner_url")) {
            user.profileBannerUrl = jsonObject.getString("profile_banner_url");
        } else {
            user.profileBannerUrl = "";
        }

        return user;
    }

    public static List<User> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            users.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return users;

    }
}
