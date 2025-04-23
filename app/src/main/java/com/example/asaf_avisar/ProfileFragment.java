package com.example.asaf_avisar;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.asaf_avisar.activitys.Post;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ProfileFragment extends Fragment implements FirebaseCallback, FirebaseCallbackPosts {

    private String userId;

    private ImageView profileImage;
    private TextView username, tvBio, tvDriverType, textFollowersCount, textFollowingCount;
    private TextView tvGreenForm, tvTheory, tvLicenseDateDetail;
    private Button btnUnfollow, btnNudge;
    private ProgressBar nightProgressBar, dayProgressBar;
    private TextView nightCounter, dayCounter;
    private RecyclerView rvPosts;
    private List<Post> posts = new ArrayList<>();
    private PostsAdapter adapter;
    private FireBaseManager fbm;
    private long diffInDays;
    private int progress;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_proflie, container, false);

        fbm = new FireBaseManager(requireContext());

        // get userId passed in arguments
        if (getArguments() != null) {
            userId = getArguments().getString("userId");
        }
        if (userId == null) {
            // fallback to own
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        // bind views
        profileImage = view.findViewById(R.id.profile_image);
        username = view.findViewById(R.id.username);
        tvBio = view.findViewById(R.id.textView2);
        tvDriverType = view.findViewById(R.id.user_info_1);
        textFollowersCount = view.findViewById(R.id.text_followers_count);
        textFollowingCount = view.findViewById(R.id.text_following_count);
        tvGreenForm = view.findViewById(R.id.detail_green_form);
        tvTheory = view.findViewById(R.id.detail_theory);
        tvLicenseDateDetail = view.findViewById(R.id.detail_license_date);
        btnUnfollow = view.findViewById(R.id.btn_unfollow);
        btnNudge = view.findViewById(R.id.btn_nudge);
        nightProgressBar = view.findViewById(R.id.night_progress_bar);
        dayProgressBar = view.findViewById(R.id.day_progress_bar);
        nightCounter = view.findViewById(R.id.night_progress_text);
        dayCounter = view.findViewById(R.id.day_progress_text);
        rvPosts = view.findViewById(R.id.recyclerViewPosts);

        // setup posts grid
        rvPosts.setLayoutManager(new GridLayoutManager(getContext(), 3));
        adapter = new PostsAdapter(posts);
        rvPosts.setAdapter(adapter);

        // load profile data & posts for this user
        fbm.readData(this, "Student", userId);
        fbm.readPostsForUser(this, userId);

        btnUnfollow.setOnClickListener(v -> Toast.makeText(getContext(), "Follow/Unfollow clicked", Toast.LENGTH_SHORT).show());
        btnNudge.setOnClickListener(v -> Toast.makeText(getContext(), "Nudge clicked", Toast.LENGTH_SHORT).show());

        return view;
    }

    @Override
    public void oncallbackStudent(StudentUser u) {
        if (u == null) return;
        String b64 = u.getProfilePhotoBase64();
        if (b64 != null) {
            Bitmap bmp = ImageUtils.convert64base(b64);
            if (bmp != null) profileImage.setImageBitmap(bmp);
        }
        username.setText(u.getName());
        tvBio.setText(u.getBio() == null ? "" : u.getBio());
        tvDriverType.setText("Driver Type: " + (u.isDriverType() ? "manual" : "automatic"));
        textFollowersCount.setText(String.valueOf(u.getFollowerCount()));
        textFollowingCount.setText(String.valueOf(u.getFollowingCount()));
        tvGreenForm.setText("Green Form: " + (u.isHasGreenForm() ? "Submitted" : "Not Submitted"));
        tvTheory.setText("Theory: " + (u.isPassedTheory() ? "Completed" : "Not Completed"));
        tvLicenseDateDetail.setText("License Release Date: " +
                (u.getLicenseDate() != null ? new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(u.getLicenseDate()) : "On their way!"));
        updateProgress(u.getLicenseDate());
    }

    private void updateProgress(Date licenseDate) {
        if (licenseDate == null) return;
        long diff = Calendar.getInstance().getTimeInMillis() - licenseDate.getTime();
        diffInDays = TimeUnit.MILLISECONDS.toDays(diff);
        progress = (int) diffInDays;
        int dayLeft = (int) Math.max(0, 90 - diffInDays);
        int nightLeft = (int) Math.max(0, 180 - diffInDays);
        dayCounter.setText(String.valueOf(dayLeft));
        nightCounter.setText(String.valueOf(nightLeft));
        dayProgressBar.setProgress((dayLeft * 100) / 90);
        nightProgressBar.setProgress((nightLeft * 100) / 180);
    }

    @Override public void oncallbackArryStudent(ArrayList<StudentUser> s) {}
    @Override public void onCallbackTeacher(ArrayList<TeacherUser> t) {}

    @Override
    public void onCallbackPosts(ArrayList<Post> list) {
        posts.clear();
        posts.addAll(list);
        adapter.notifyDataSetChanged();
    }

    private static class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.VH> {
        private final List<Post> items;
        PostsAdapter(List<Post> items) { this.items = items; }
        @NonNull @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ImageView iv = new ImageView(parent.getContext());
            int w = parent.getResources().getDisplayMetrics().widthPixels / 3;
            iv.setLayoutParams(new RecyclerView.LayoutParams(w, w));
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            return new VH(iv);
        }
        @Override public void onBindViewHolder(@NonNull VH holder, int position) {
            String b64 = items.get(position).getContent();
            if (b64 != null) {
                Bitmap bmp = ImageUtils.convert64base(b64);
                if (bmp != null) holder.iv.setImageBitmap(bmp);
            }
        }
        @Override public int getItemCount() { return items.size(); }
        static class VH extends RecyclerView.ViewHolder {
            ImageView iv;
            VH(ImageView iv) { super(iv); this.iv = iv; }
        }
    }
}
