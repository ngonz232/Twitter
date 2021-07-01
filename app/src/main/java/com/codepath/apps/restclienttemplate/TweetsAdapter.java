package com.codepath.apps.restclienttemplate;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.databinding.ActivityTweetsDetailBinding;
import com.codepath.apps.restclienttemplate.databinding.ItemTweetBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;
import java.util.List;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import okhttp3.Headers;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {

    public final int RADIUS = 70;
    public final int MARGIN = 15;
    public final int REPLY_REQUEST_CODE = 2;
    public final int DETAILS_REQUEST_CODE = 3;
    public final String TAG = "TweetsAdapter";
    Context context;
    ItemTweetBinding binding;
    List<Tweet> tweets;

    /* Constructor takes in the context and list of tweets. */
    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    /* For each tweet item, inflate the layout. */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTweetBinding binding = ItemTweetBinding.inflate(LayoutInflater.from(context));
        return new ViewHolder(binding);
    }

    /* Bind values of the tweet view based on the tweet's position in the list. */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tweet tweet = tweets.get(position);
        holder.bind(tweet);
    }

    /* Returns the number of tweets in the recycler view. */
    @Override
    public int getItemCount() { return tweets.size(); }

    /* Clears the adapter and notifies that data has changed. */
    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    /* Adds a list of items to the adapter and notifies that data has change. */
    public void addAll(List<Tweet> list) {
        tweets.addAll(list);
        notifyDataSetChanged();
    }

    /* Defines a view holder. */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final int REPLY_REQUEST_CODE = 2;
        private ActivityTweetsDetailBinding activityTweetsDetailBinding;
        ImageView ivProfileImage;
        TextView tvBody;
        TextView tvScreenName;
        TextView tvRelativeTimestamp;
        RecyclerView rvImages;
        ImageView ivImage;
        LinearLayout btnRow;
        TextView tvName;
        ImageButton imgBtnReply;
        ImageButton imgBtnFavorite;
        ImageButton imgBtnRetweet;
        TextView tvFavoriteCount;
        TextView tvRetweetCount;

        /* Constructor gets the components of the tweet view. */
        public ViewHolder(ItemTweetBinding binding) {
            super(binding.getRoot());
            ivProfileImage = binding.ivProfileImage;
            tvBody = binding.tvBody;
            tvScreenName = binding.tvScreenName;
            tvRelativeTimestamp = binding.tvTimeStamp;
            rvImages = binding.rvImages;
            ivImage = binding.ivImage;
            btnRow = binding.btnRow;
            tvName = binding.tvName;
            imgBtnReply = binding.imgBtnReply;
            imgBtnFavorite = binding.imgBtnFavorite;
            imgBtnRetweet = binding.imgBtnRetweet;
            tvFavoriteCount = binding.tvFavoriteCount;
            tvRetweetCount = binding.tvRetweetCount;
            itemView.setOnClickListener(this);

            // When the reply button is clicked
            imgBtnReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Compose a tweet and pass in the tweet being replied to
                    Tweet tweet = tweets.get(getAdapterPosition());
                    FragmentManager fm = ((AppCompatActivity) context).getSupportFragmentManager();
                    ComposeActivityFragment composeActivityFragment = ComposeActivityFragment.newInstance("Some Title", tweet, REPLY_REQUEST_CODE);
                    composeActivityFragment.show(fm, "fragment_compose");
                }
            });

            // When the favorite button is clicked
            imgBtnFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Favorite or un-favorite the tweet
                    final Tweet tweet = tweets.get(getAdapterPosition());
                    TwitterClient client = TwitterApp.getRestClient(imgBtnFavorite.getContext());
                    client.favorite(tweet.id, tweet.favorited, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            if (tweet.favorited) {
                                tweet.favoriteCount--;
                                tweet.favorited = false;
                            } else {
                                tweet.favoriteCount++;
                                tweet.favorited = true;
                            }
                            notifyItemChanged(getAdapterPosition());
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.e(TAG, "onFailure to favorite or un-favorite tweet " + response, throwable);
                        }
                    });
                }
            });

            // When the retweet button is clicked
            imgBtnRetweet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // retweet or un-retweet the tweet
                    final Tweet tweet = tweets.get(getAdapterPosition());
                    TwitterClient client = TwitterApp.getRestClient(imgBtnFavorite.getContext());
                    client.retweet(tweet.id, tweet.retweeted, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            if (tweet.retweeted) {
                                tweet.retweetCount--;
                                tweet.retweeted = false;
                            } else {
                                tweet.retweetCount++;
                                tweet.retweeted = true;
                            }
                            notifyItemChanged(getAdapterPosition());
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.e(TAG, "onFailure to favorite or un-favorite tweet " + response, throwable);
                        }
                    });
                }
            });
        }


        /* Binds the values of the tweet to the view holder's components. */
        public void bind(final Tweet tweet) {
            tvBody.setText(tweet.body);
            tvName.setText(tweet.user.name);
            tvScreenName.setText("@" + tweet.user.screenName);
            tvFavoriteCount.setText(tweet.favoriteCount.toString());
            tvRetweetCount.setText(tweet.retweetCount.toString());
            tvRelativeTimestamp.setText(tweet.getRelativeTimeAgo());
            Glide.with(context).load(tweet.user.profileImageUrl).circleCrop().into(ivProfileImage);
            int favoriteIcon = tweet.favorited? R.drawable.ic_baseline_favorite_24: R.drawable.ic_baseline_favorite_border_24;
            imgBtnFavorite.setImageResource(favoriteIcon);
            int retweetIcon = tweet.retweeted? R.drawable.ic_vector_retweet: R.drawable.ic_vector_retweet_stroke;
            imgBtnRetweet.setImageResource(retweetIcon);

            // Populate the images view based on the number of images embedded in the tweet
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) btnRow.getLayoutParams();
            if (tweet.imageUrls.isEmpty()) {
                rvImages.setVisibility(View.GONE);
                ivImage.setVisibility(View.GONE);
                params.addRule(RelativeLayout.BELOW, R.id.tvTimeStamp);
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
            rvImages.setVisibility(View.GONE);
            ivImage.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(tweet.imageUrls.get(0))
                    .transform(new RoundedCornersTransformation(RADIUS, MARGIN))
                    .into(ivImage);
        }

        /* Handles binding when the tweet has more than one image. */
        private void bindMultiImageView(Tweet tweet) {
            rvImages.setLayoutManager(new LinearLayoutManager(context,
                    LinearLayoutManager.HORIZONTAL, false));
            com.codepath.apps.restclienttemplate.ImagesAdapter adapter = new com.codepath.apps.restclienttemplate.ImagesAdapter(context, tweet.imageUrls);
            rvImages.setAdapter(adapter);
            ivImage.setVisibility(View.GONE);
            rvImages.setVisibility(View.VISIBLE);
        }

        /* Displays a new activity via an Intent when the user clicks on a tweet. */
        @Override
        public void onClick(View v) {
            int position = getAdapterPosition(); // get the tweet's position
            if (position != RecyclerView.NO_POSITION) { // ensure that the position is valid
                Tweet tweet = tweets.get(position); // get the tweet at the position

                // Navigate to the details activity and pass in the tweet
                Intent intent = new Intent(context, TweetsDetailActivity.class);
                intent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                intent.putExtra("position", position);
                ((Activity) context).startActivityForResult(intent, DETAILS_REQUEST_CODE);
            }
        }
    }
}