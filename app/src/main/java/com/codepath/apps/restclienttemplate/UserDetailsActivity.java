package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.databinding.ActivityUserDetailsBinding;
import com.codepath.apps.restclienttemplate.models.User;

import org.parceler.Parcels;

public class UserDetailsActivity extends AppCompatActivity {

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityUserDetailsBinding binding = ActivityUserDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        user = Parcels.unwrap(getIntent().getParcelableExtra("user"));

        if (!user.profileBannerUrl.isEmpty())
            Glide.with(this).load(user.profileBannerUrl).into(binding.ivProfileBanner);
        Glide.with(this).load(user.profileImageUrl).circleCrop().into(binding.ivProfileImage);
        binding.tvName.setText(user.name);
        if (user.verifiedAccount) binding.ivVerified.setImageDrawable(getResources().getDrawable(R.drawable.twitter));
        binding.tvScreenName.setText("@" + user.screenName);
        binding.tvDescription.setText(user.accountDescription);
        binding.tvLocation.setText(user.accountLocation);
        binding.tvUrl.setText(user.profileUrl);
        binding.tvCreatedAt.setText(user.createdDate);
        binding.tvFollowingCount.setText(user.friendsCount.toString());
        binding.tvFollowerCount.setText(user.followerCount.toString());

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(UserDetailsActivity.this, FollowersActivity.class);
                i.putExtra("user", Parcels.wrap(user));
                if (view.getId() == R.id.tvFollowerCount) {
                    i.putExtra("mode", "follower");
                } else if (view.getId() == R.id.tvFollowingCount) {
                    i.putExtra("mode", "following");
                }
                startActivity(i);
            }
        };
        binding.tvFollowerCount.setOnClickListener(listener);
        binding.tvFollowingCount.setOnClickListener(listener);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("user", Parcels.wrap(user));
        setResult(RESULT_OK, intent);
        finish();
    }
}