package com.example.asaf_avisar.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.asaf_avisar.FireBaseManager;
import com.example.asaf_avisar.callbacks.FirebaseCallbackPosts;
import com.example.asaf_avisar.adapters.PostsAdapter;
import com.example.asaf_avisar.R;
import com.example.asaf_avisar.objects.Post;

import java.util.ArrayList;

/**
 * The type Home fragment.
 */
public class HomeFragment extends Fragment {

    private BroadcastReceiver networkChangeReceiver;
    private RecyclerView recyclerView;
    private PostsAdapter postsAdapter;
    private FireBaseManager firebaseManager;
    private ProgressBar loadingProgressBar;
    private TextView noPostsTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseManager = new FireBaseManager(getContext()); // Initialize FirebaseManager
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize UI components
        recyclerView = view.findViewById(R.id.recyclerView);
        loadingProgressBar = view.findViewById(R.id.loading_progress_bar);
        noPostsTextView = view.findViewById(R.id.no_posts_text_view);

        // Initially show loading state
        showLoading(true);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        postsAdapter = new PostsAdapter(new ArrayList<>()); // Initialize with empty list
        recyclerView.setAdapter(postsAdapter);

        // Load posts
        loadPosts();

        // Initialize the BroadcastReceiver for network connectivity changes
        networkChangeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                if (connectivityManager != null) {
                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                    if (networkInfo != null && networkInfo.isConnected()) {
                        // Internet is connected - try to load posts if we don't have any
                        if (postsAdapter == null || postsAdapter.getItemCount() == 0) {
                            loadPosts();
                        }
                    } else {
                        Toast.makeText(context, "Wi-Fi Disconnected", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };

        // Register the receiver for connectivity changes
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        requireContext().registerReceiver(networkChangeReceiver, filter);
    }

    /**
     * Load posts from Firebase
     */
    private void loadPosts() {
        // Show loading indicator
        showLoading(true);

        // Fetch posts from Firebase
        firebaseManager.readPosts(new FirebaseCallbackPosts() {
            @Override
            public void onCallbackPosts(ArrayList<Post> posts) {
                // Hide loading indicator
                showLoading(false);

                if (posts != null && !posts.isEmpty()) {
                    // We have posts to display
                    postsAdapter = new PostsAdapter(posts);
                    recyclerView.setAdapter(postsAdapter);
                    recyclerView.setVisibility(View.VISIBLE);
                    noPostsTextView.setVisibility(View.GONE);
                } else {
                    // No posts to display
                    recyclerView.setVisibility(View.GONE);
                    noPostsTextView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /**
     * Show or hide loading indicator
     */
    private void showLoading(boolean isLoading) {
        if (isLoading) {
            loadingProgressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            noPostsTextView.setVisibility(View.GONE);
        } else {
            loadingProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Unregister the receiver when fragment view is destroyed
        if (networkChangeReceiver != null) {
            requireContext().unregisterReceiver(networkChangeReceiver);
        }
    }
}