package com.codepath.apps.restclienttemplate;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import org.json.JSONException;
import okhttp3.Headers;

public class ComposeActivityFragment extends DialogFragment {

    private static int requestCode;
    public final String TAG = "ComposeDialogFragment";
    private static Tweet replyTweet;
    public final int MAX_TWEET_LENGTH = 280;
    private EditText etCompose;
    private Button btnTweet;

    public interface ComposeDialogListener {
        void onFinishComposeDialog(Tweet tweet, int requestCode);
    }

    public ComposeActivityFragment() {
        // empty constructor required by DialogFragment. Use newInstance below instead

    }

    public static ComposeActivityFragment newInstance(String title, Tweet tweet, int code) {
        ComposeActivityFragment frag = new ComposeActivityFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        replyTweet = tweet;
        requestCode = code;
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_compose, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // gets the field from view
        etCompose = (EditText) view.findViewById(R.id.etCompose);
        btnTweet = (Button) view.findViewById(R.id.btnTweet);
        // fetch arguments and get title
        String title = getArguments().getString("title", "Enter Name");
        getDialog().setTitle(title);
        // user that wrote original tweet is automatically "@" in the reply
        if (replyTweet != null) {
            etCompose.setText("@" + replyTweet.user.screenName + " ");
        }
        // automatically shows soft keyboard and requests focus to field
        etCompose.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        // set ups a call back when the "done" button is pressed on the keyboard
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tweetContent = etCompose.getText().toString();

                // Tweet content validation
                if (tweetContent.isEmpty()) {
                    makeToast("Sorry, your tweet can't be empty");
                    return;
                } else if (tweetContent.length() > MAX_TWEET_LENGTH) {
                    makeToast("Sorry, your tweet is too long");
                    return;
                }

                // Twitter API call for publishing the tweet
                String replyId = replyTweet == null? "": replyTweet.id;
                TwitterClient client = TwitterApp.getRestClient(getContext());
                client.publishTweet(tweetContent, replyId, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        try {
                            // passing the new tweet and closing the activity
                            Tweet tweet = Tweet.fromJson(json.jsonObject);
                            ComposeDialogListener listener = (ComposeDialogListener) getActivity();
                            listener.onFinishComposeDialog(tweet, requestCode);
                            // closes the dialogue and returns to the parent activity
                            dismiss();
                        } catch (JSONException e) {
                            // console error log
                            Log.e(TAG, "jsonObject error");
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        // Console error log
                        Log.e(TAG, "onFailure to publish tweet " + response, throwable);
                    }
                });
            }
        });
    }

    // helper method for a short toast
    private void makeToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}