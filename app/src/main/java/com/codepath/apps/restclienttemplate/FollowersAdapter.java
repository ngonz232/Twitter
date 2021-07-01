package com.codepath.apps.restclienttemplate;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.databinding.ItemFollowerBinding;
import com.codepath.apps.restclienttemplate.models.User;
import org.parceler.Parcels;
import java.util.List;

public class FollowersAdapter extends RecyclerView.Adapter<FollowersAdapter.ViewHolder> {

    private final int RADIUS = 70;
    private final int MARGIN = 15;
    public final int USER_DETAIL_REQUEST_CODE = 4;
    Context context;
    List<User> followers;

    /* Constructor takes in the context and list of image urls. */
    public FollowersAdapter(Context context, List<User> followers) {
        this.context = context;
        this.followers = followers;
    }

    /* For each image item, inflate the layout. */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFollowerBinding binding = ItemFollowerBinding.inflate(LayoutInflater.from(context));
        return new ViewHolder(binding);
    }

    /* Fill in the ImageView's image based on the position of the image. */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User follower = followers.get(position);
        holder.bind(follower);
    }

    /* Returns how many items are in the list of images. */
    @Override
    public int getItemCount() { return followers.size(); }

    /* Defines a view holder for the image in the recycler view. */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView ivProfileImage;
        TextView tvName;
        ImageView ivVerified;
        TextView tvScreenName;
        TextView tvDescription;

        public ViewHolder(ItemFollowerBinding binding) {
            super(binding.getRoot());
            ivProfileImage = binding.ivProfileImage;
            tvName = binding.tvName;
            ivVerified = binding.ivVerified;
            tvScreenName = binding.tvScreenName;
            tvDescription = binding.tvDescription;
            binding.getRoot().setOnClickListener(this);
        }

        public void bind(User follower) {
            Glide.with(context).load(follower.profileImageUrl).circleCrop().into(ivProfileImage);
            tvName.setText(follower.name);
            if (follower.verifiedAccount)
                ivVerified.setImageDrawable(context.getResources().getDrawable(R.drawable.twitter));
            tvScreenName.setText(follower.screenName);
            tvDescription.setText(follower.accountDescription);
        }

        @Override
        public void onClick(View view) {
            Intent i = new Intent(context, UserDetailsActivity.class);
            i.putExtra("user", Parcels.wrap(followers.get(getAdapterPosition())));
            ((Activity) context).startActivityForResult(i, USER_DETAIL_REQUEST_CODE);
        }
    }
}