package com.example.asaf_avisar;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ProfileFragment extends Fragment
        implements FirebaseCallback, FirebaseCallbackPosts {

    // header views
    private ImageView profileImage;
    private TextView username, tvBio,
            tvDriverType,
            textFollowersCount, textFollowingCount;

    // additional detail views
    private TextView tvGreenForm, tvTheory, tvLicenseDateDetail;

    private Button btnUnfollow, btnNudge;

    // progress bars
    private ProgressBar nightProgressBar, dayProgressBar;
    private TextView nightCounter, dayCounter;
    private int progress, userNight, userDay;
    private long diffInDays;

    // recyclerview
    private RecyclerView rvPosts;
    private List<Post> posts = new ArrayList<>();
    private PostsAdapter adapter;

    private FireBaseManager fbm;

    public ProfileFragment() {
        // required empty constructor
    }

    @Nullable @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_proflie, container, false);

        fbm = new FireBaseManager(requireContext());

        // bind header
        profileImage       = view.findViewById(R.id.profile_image);
        username           = view.findViewById(R.id.username);
        tvBio              = view.findViewById(R.id.textView2);
        tvDriverType       = view.findViewById(R.id.user_info_1);
        textFollowersCount = view.findViewById(R.id.text_followers_count);
        textFollowingCount = view.findViewById(R.id.text_following_count);

        // bind additional details
        tvGreenForm         = view.findViewById(R.id.detail_green_form);
        tvTheory            = view.findViewById(R.id.detail_theory);
        tvLicenseDateDetail = view.findViewById(R.id.detail_license_date);

        btnUnfollow    = view.findViewById(R.id.btn_unfollow);
        btnNudge       = view.findViewById(R.id.btn_nudge);

        // bind progress bars and their text
        nightProgressBar = view.findViewById(R.id.night_progress_bar);
        dayProgressBar   = view.findViewById(R.id.day_progress_bar);
        nightCounter     = view.findViewById(R.id.night_progress_text);
        dayCounter       = view.findViewById(R.id.day_progress_text);

        // setup RecyclerView
        rvPosts = view.findViewById(R.id.recyclerViewPosts);
        rvPosts.setLayoutManager(new GridLayoutManager(getContext(), 3));
        adapter = new PostsAdapter(posts);
        rvPosts.setAdapter(adapter);

        // load data
        String uid = fbm.getUserid();
        fbm.readData(this, "Student", uid);
        fbm.readPosts(this);

        btnUnfollow.setOnClickListener(v ->
                Toast.makeText(getContext(), "Follow/Unfollow clicked", Toast.LENGTH_SHORT).show());
        btnNudge.setOnClickListener(v ->
                Toast.makeText(getContext(), "Nudge clicked", Toast.LENGTH_SHORT).show());

        return view;
    }

    // --- FirebaseCallback ---
    @Override
    public void oncallbackStudent(StudentUser u) {
        if (u == null) return;

        // photo
        String b64 = u.getProfilePhotoBase64();
        if (b64 != null) {
            Bitmap bmp = ImageUtils.convert64base(b64);
            if (bmp != null) profileImage.setImageBitmap(bmp);
        }

        // name & bio
        username.setText(u.getName());
        tvBio.setText(u.getBio() == null ? "" : u.getBio());

        // driver / city / license
        tvDriverType.setText("Driver Type: " + (u.isDriverType() ? "manual" : "automatic"));
        // followers & following
        textFollowersCount.setText(String.valueOf(u.getFollowerCount()));
        textFollowingCount.setText(String.valueOf(u.getFollowingCount()));

        // additional details: green form, theory, license release date
        tvGreenForm.setText("Green Form: " + (u.isHasGreenForm() ? "Submitted" : "Not Submitted"));
        tvTheory   .setText("Theory: " + (u.isPassedTheory() ? "Completed" : "Not Completed"));
        tvLicenseDateDetail.setText("License Release Date: " +
                (u.getLicenseDate() != null
                        ? new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(u.getLicenseDate())
                        : "On their way!"));


        // progress bars
        updateProgressBasedOnLicense(u.getLicenseDate());
        updateProgressText(diffInDays);
    }

    private void updateProgressText(long diffInDays) {
        int daysLeft   = (int) Math.max(0, 90  - diffInDays);
        int nightsLeft = (int) Math.max(0, 180 - diffInDays);

        dayCounter.setText(String.valueOf(daysLeft));
        nightCounter.setText(String.valueOf(nightsLeft));
    }

    @Override public void oncallbackArryStudent(ArrayList<StudentUser> s) {}
    @Override public void onCallbackTeacher(ArrayList<TeacherUser> t) {}

    // --- progress logic ---
    private void startProgressBarUpdate() {
        int pn = (progress * 100) / 180;
        Handler h1 = new Handler();
        Runnable r1 = new Runnable() {
            int i=1;
            @Override public void run() {
                if (i<=pn) {
                    nightProgressBar.setProgress(i);
                    userNight=i++;
                    h1.postDelayed(this,10);
                }
            }
        };
        int pd = (progress*100)/90;
        Handler h2 = new Handler();
        Runnable r2 = new Runnable() {
            int j=1;
            @Override public void run() {
                if (j<=pd) {
                    dayProgressBar.setProgress(j);
                    userDay=j++;
                    h2.postDelayed(this,10);
                }
            }
        };
        h1.post(r1);
        h2.post(r2);
    }

    private void updateProgressBasedOnLicense(Date licenseDate) {
        if (licenseDate==null) {
            Toast.makeText(getContext(),"Invalid license date",Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            long diff = Calendar.getInstance().getTimeInMillis() - licenseDate.getTime();
            diffInDays = TimeUnit.MILLISECONDS.toDays(diff);
            progress = (int) diffInDays;
            startProgressBarUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(),"Error calculating days",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCallbackPosts(ArrayList<Post> list) {
        posts.clear();
        posts.addAll(list);
        adapter.notifyDataSetChanged();
    }

    // --- simple adapter for grid of Base64-encoded photos ---
    private static class PostsAdapter
            extends RecyclerView.Adapter<PostsAdapter.VH> {

        private final List<Post> items;
        PostsAdapter(List<Post> items){ this.items=items; }

        @Override public VH onCreateViewHolder(ViewGroup p, int i){
            ImageView iv=new ImageView(p.getContext());
            int w=p.getResources().getDisplayMetrics().widthPixels/3;
            iv.setLayoutParams(
                    new RecyclerView.LayoutParams(w,w)
            );
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            return new VH(iv);
        }
        @Override public void onBindViewHolder(VH h,int i){
            String b64=items.get(i).getContent();
            if(b64!=null){
                Bitmap bmp=ImageUtils.convert64base(b64);
                if(bmp!=null)h.iv.setImageBitmap(bmp);
            }
        }
        @Override public int getItemCount(){return items.size();}
        static class VH extends RecyclerView.ViewHolder{
            ImageView iv;
            VH(ImageView iv){super(iv);this.iv=iv;}
        }
    }
}
