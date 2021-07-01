package com.codepath.apps.restclienttemplate;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.ComposeActivityFragment;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.ImagesAdapter;
import com.codepath.apps.restclienttemplate.databinding.ActivityTimelineBinding;
import com.codepath.apps.restclienttemplate.databinding.ActivityTweetsDetailBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import org.parceler.Parcels;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import okhttp3.Headers;

public class TweetsDetailActivity extends AppCompatActivity implements ComposeActivityFragment.ComposeDialogListener {

    public final int RADIUS = 70;
    public final int MARGIN = 15;
    public final String TAG = "TweetDetailsActivity";
    public final int REPLY_REQUEST_CODE = 2;
    public final int USER_DETAIL_REQUEST_CODE = 4;
    Tweet tweet;
    int position;
    ActivityTweetsDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTweetsDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setTitle("");

        tweet = Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));
        position = getIntent().getIntExtra("position", -1);

        Glide.with(this).load(tweet.user.profileImageUrl).circleCrop().into(binding.ivProfileImage);
        binding.tvName.setText(tweet.user.name);
        binding.tvScreenName.setText("@" + tweet.user.screenName);
        binding.tvBody.setText(tweet.body);
        binding.tvRetweetCount.setText(tweet.retweetCount.toString());
        binding.tvFavoriteCount.setText(tweet.favoriteCount.toString());
        binding.tvCreatedAt.setText(tweet.getCreatedAt());

        int favoriteIcon = tweet.favorited? R.drawable.ic_baseline_favorite_24: R.drawable.ic_baseline_favorite_border_24;
        int retweetIcon = tweet.retweeted? R.drawable.ic_vector_retweet: R.drawable.ic_vector_retweet_stroke;
        binding.imgBtnFavorite.setImageResource(favoriteIcon);
        binding.imgBtnRetweet.setImageResource(retweetIcon);

        // Populate the images view based on the number of images embedded in the tweet
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) binding.tvCreatedAt.getLayoutParams();
        if (tweet.imageUrls.isEmpty()) {
            binding.rvImages.setVisibility(View.GONE);
            binding.ivImage.setVisibility(View.GONE);
            params.addRule(RelativeLayout.BELOW, R.id.tvBody);
        } else if (tweet.imageUrls.size() == 1) {
            bindSingleImageView(tweet);
            params.addRule(RelativeLayout.BELOW, R.id.ivImage);
        } else {
            bindMultiImageView(tweet);
            params.addRule(RelativeLayout.BELOW, R.id.rvImages);
        }
    }

    /* Handles binding when the tweet has exactly one image. */
    private void bindSingleImageView(Tweet tweet) {
        binding.rvImages.setVisibility(View.GONE);
        binding.ivImage.setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(tweet.imageUrls.get(0))
                .transform(new RoundedCornersTransformation(RADIUS, MARGIN))
                .into(binding.ivImage);
    }

    /* Handles binding when the tweet has more than one image. */
    private void bindMultiImageView(Tweet tweet) {
        binding.ivImage.setVisibility(View.GONE);
        binding.rvImages.setVisibility(View.VISIBLE);
        binding.rvImages.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));
        ImagesAdapter adapter = new ImagesAdapter(this, tweet.imageUrls);
        binding.rvImages.setAdapter(adapter);
    }

    // When the reply button is clicked
    public void replyOnClick(View view) {
        // Compose a tweet and pass in the tweet being replied to
        FragmentManager fm = getSupportFragmentManager();
        ComposeActivityFragment composeDialogFragment = ComposeActivityFragment.newInstance("Some Title", tweet, REPLY_REQUEST_CODE);
        composeDialogFragment.show(fm, "fragment_compose");
    }

    // When the retweet button is clicked
    public void retweetOnClick(View view) {
        // retweet or un-retweet the tweet
        TwitterClient client = TwitterApp.getRestClient(binding.imgBtnFavorite.getContext());
        client.retweet(tweet.id, tweet.retweeted, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                if (tweet.retweeted) {
                    tweet.retweetCount--;
                    tweet.retweeted = false;
                    binding.imgBtnRetweet.setImageResource(R.drawable.ic_vector_retweet_stroke);
                } else {
                    tweet.retweetCount++;
                    tweet.retweeted = true;
                    binding.imgBtnRetweet.setImageResource(R.drawable.ic_vector_retweet);
                }
                binding.tvRetweetCount.setText(tweet.retweetCount.toString());
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onFailure to favorite or un-favorite tweet " + response, throwable);
            }
        });
    }

    // When the favorite button is clicked
    public void favoriteOnClick(View view) {
        TwitterClient client = TwitterApp.getRestClient(this);
        client.favorite(tweet.id, tweet.favorited, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                if (tweet.favorited) {
                    tweet.favoriteCount--;
                    tweet.favorited = false;
                    binding.imgBtnFavorite.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                } else {
                    tweet.favoriteCount++;
                    tweet.favorited = true;
                    binding.imgBtnFavorite.setImageResource(R.drawable.ic_baseline_favorite_24);
                }
                binding.tvFavoriteCount.setText(tweet.favoriteCount.toString());
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onFailure to favorite or un-favorite tweet " + response, throwable);
            }
        });
    }

    public void shareOnClick(View view) {

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("position", position);
        intent.putExtra("tweet", Parcels.wrap(tweet));
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onFinishComposeDialog(Tweet tweet, int requestCode) {
        if (requestCode == REPLY_REQUEST_CODE) {
            Toast.makeText(this, "Added tweet to timeline", Toast.LENGTH_SHORT).show();
        }
    }

    public void userOnClick(View view) {
        Intent i = new Intent(TweetsDetailActivity.this, UserDetailsActivity.class);
        i.putExtra("user", Parcels.wrap(tweet.user));
        startActivityForResult(i, USER_DETAIL_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // Get tweet from the intent and update the recycler view with the tweet
        if (requestCode == USER_DETAIL_REQUEST_CODE && resultCode == RESULT_OK) {
            User user = Parcels.unwrap(data.getParcelableExtra("user"));
            tweet.user = user;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}