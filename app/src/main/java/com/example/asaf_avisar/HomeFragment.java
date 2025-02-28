package com.example.asaf_avisar;

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
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.asaf_avisar.activitys.Post;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private BroadcastReceiver networkChangeReceiver;
    private RecyclerView recyclerView;
    private PostsAdapter postsAdapter;
    private FireBaseManager firebaseManager;

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

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        postsAdapter = new PostsAdapter(null); // Set initial empty list of posts
        recyclerView.setAdapter(postsAdapter);

        // Fetch posts from Firebase
        firebaseManager.readPosts(new FirebaseCallbackPosts() {
            @Override
            public void onCallbackPosts(ArrayList<Post> posts) {
                postsAdapter = new PostsAdapter(posts); // Initialize PostsAdapter with fetched posts
                recyclerView.setAdapter(postsAdapter);
            }
        });

        // Initialize the BroadcastReceiver for network connectivity changes
        networkChangeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                if (connectivityManager != null) {
                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                    if (networkInfo != null && networkInfo.isConnected()) {
                        // Internet is connected
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Unregister the receiver when fragment view is destroyed
        if (networkChangeReceiver != null) {
            requireContext().unregisterReceiver(networkChangeReceiver);
        }
    }
}
