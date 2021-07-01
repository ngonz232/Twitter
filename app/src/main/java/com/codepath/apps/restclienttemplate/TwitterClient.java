package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.text.TextUtils;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.oauth.OAuthBaseClient;
import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.api.BaseApi;
import java.util.List;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/scribejava/scribejava/tree/master/scribejava-apis/src/main/java/com/github/scribejava/apis
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final BaseApi REST_API_INSTANCE = TwitterApi.instance(); // Change this
	public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
	public static final String REST_CONSUMER_KEY = BuildConfig.CONSUMER_KEY;       // Change this inside apikey.properties
	public static final String REST_CONSUMER_SECRET = BuildConfig.CONSUMER_SECRET; // Change this inside apikey.properties

	// Landing page to indicate the OAuth flow worked in case Chrome for Android 25+ blocks navigation back to the app.
	public static final String FALLBACK_URL = "https://codepath.github.io/android-rest-client-template/success.html";

	// See https://developer.chrome.com/multidevice/android/intents
	public static final String REST_CALLBACK_URL_TEMPLATE = "intent://%s#Intent;action=android.intent.action.VIEW;scheme=%s;package=%s;S.browser_fallback_url=%s;end";

	public TwitterClient(Context context) {
		super(context, REST_API_INSTANCE,
				REST_URL,
				REST_CONSUMER_KEY,
				REST_CONSUMER_SECRET,
				null,  // OAuth2 scope, null for OAuth1
				String.format(REST_CALLBACK_URL_TEMPLATE, context.getString(R.string.intent_host),
						context.getString(R.string.intent_scheme), context.getPackageName(), FALLBACK_URL));
	}
	// CHANGE THIS
	// DEFINE METHODS for different API endpoints here
	public void getHomeTimeline(String id, JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("tweet_mode", "extended");
		params.put("count", "25");
		params.put("since_id", "1");
		client.get(apiUrl, params, handler);
	}

	// Post request to publish Tweet
	public void publishTweet(String tweetContent, String replyIdStr, JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/update.json");
		RequestParams params = new RequestParams();
		params.put("status", tweetContent);
		if (!replyIdStr.isEmpty()) {
			params.put("in_reply_to_status_id", replyIdStr);
			params.put("auto_populate_reply_metadata", true);
		}
		client.post(apiUrl, params, "", handler);
	}

	// Post request to favorite or unfavorite tweet
	public void favorite(String tweetIdStr, Boolean tweetFavorited, JsonHttpResponseHandler handler) {
		String path = tweetFavorited? "favorites/destroy.json": "favorites/create.json";
		String apiUrl = getApiUrl(path);
		RequestParams params = new RequestParams();
		params.put("id", tweetIdStr);
		client.post(apiUrl, params, "", handler);
	}

	// Post request to retweet or un-retweet
	public void retweet(String tweetIdStr, Boolean tweetRetweeted, JsonHttpResponseHandler handler) {
		String path = tweetRetweeted? "statuses/unretweet.json": "statuses/retweet.json";
		String apiUrl = getApiUrl(path);
		RequestParams params = new RequestParams();
		params.put("id", tweetIdStr);
		client.post(apiUrl, params, "", handler);
	}

	// GET request to retrieve followers and/or friends of a user
	public void getFollowers(String mode, String userIdStr, JsonHttpResponseHandler handler) {
		String path = mode.equals("follower") ? "followers/ids.json": "friends/ids.json";
		String apiUrl = getApiUrl(path);
		RequestParams params = new RequestParams();
		params.put("user_id", userIdStr);
		client.get(apiUrl, params, handler);
	}

	// GET request to lookup users
	public void lookupUsers(List<String> userIdStrs, JsonHttpResponseHandler handler) {
		String path = "users/lookup.json";
		String apiUrl = getApiUrl(path);
		RequestParams params = new RequestParams();

		// a comma separated list of of screen names up to 100
		params.put("user_id", TextUtils.join(",", userIdStrs));
		client.get(apiUrl, params, handler);
	}
}
