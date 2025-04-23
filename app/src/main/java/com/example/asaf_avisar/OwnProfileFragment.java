package com.example.asaf_avisar;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.asaf_avisar.activitys.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class OwnProfileFragment extends Fragment implements FirebaseCallback, FirebaseCallbackPosts {

    private String userId;

    // header views
    private ImageView profileImage_1;
    private TextView username_1, textView2_1, user_info_1_1;
    private TextView followers_count_1, following_count_1, lessons_count_1;

    // additional detail views
    private TextView detail_green_form_1, detail_theory_1, detail_license_date_1;

    // posts RecyclerView
    private RecyclerView recyclerViewPosts_1;
    private List<Post> posts = new ArrayList<>();
    private PostsAdapter adapter;

    private FireBaseManager fbm;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_own_proflie, container, false);

        // initialize Firebase manager and userId
        fbm = new FireBaseManager(requireContext());
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // bind header views
        profileImage_1 = view.findViewById(R.id.profile_image_1);
        username_1 = view.findViewById(R.id.username_1);
        textView2_1 = view.findViewById(R.id.textView2_1);
        user_info_1_1 = view.findViewById(R.id.user_info_1_1);
        followers_count_1 = view.findViewById(R.id.followers_count_1);
        following_count_1 = view.findViewById(R.id.following_count_1);
        lessons_count_1 = view.findViewById(R.id.lessons_count_1);

        detail_green_form_1 = view.findViewById(R.id.detail_green_form_1);
        detail_theory_1 = view.findViewById(R.id.detail_theory_1);
        detail_license_date_1 = view.findViewById(R.id.detail_license_date_1);

        // setup RecyclerView
        recyclerViewPosts_1 = view.findViewById(R.id.recyclerViewPosts_1);
        recyclerViewPosts_1.setLayoutManager(new GridLayoutManager(getContext(), 3));
        adapter = new PostsAdapter(posts);
        recyclerViewPosts_1.setAdapter(adapter);

        // load profile data and posts after views are bound
        fbm.readData(this, "Student", userId);
        fbm.readPostsForUser(this, userId);

        return view;
    }

    // FirebaseCallback for StudentUser
    @Override
    public void oncallbackStudent(StudentUser u) {
        if (u == null) return;

        // profile image
        String b64 = u.getProfilePhotoBase64();
        if (b64 != null) {
            Bitmap bmp = ImageUtils.convert64base(b64);
            if (bmp != null) profileImage_1.setImageBitmap(bmp);
        }

        username_1.setText(u.getName());
        textView2_1.setText(u.getBio() != null ? u.getBio() : "");
        user_info_1_1.setText("Driver Type: " + (u.isDriverType() ? "manual" : "automatic"));

        followers_count_1.setText(String.valueOf(u.getFollowerCount()));
        following_count_1.setText(String.valueOf(u.getFollowingCount()));
        lessons_count_1.setText(String.valueOf(u.getLessonCount()));

        detail_green_form_1.setText("Green Form: " + (u.isHasGreenForm() ? "Submitted" : "Not Submitted"));
        detail_theory_1.setText("Theory: " + (u.isPassedTheory() ? "Completed" : "Not Completed"));
        detail_license_date_1.setText("License Release Date: " +
                (u.getLicenseDate() != null
                        ? new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(u.getLicenseDate())
                        : "On their way!"));
    }

    @Override public void oncallbackArryStudent(ArrayList<StudentUser> s) {}
    @Override public void onCallbackTeacher(ArrayList<TeacherUser> t) {}

    // FirebaseCallbackPosts
    @Override
    public void onCallbackPosts(ArrayList<Post> list) {
        posts.clear();
        posts.addAll(list);
        adapter.notifyDataSetChanged();
    }

    // RecyclerView adapter
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

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
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
