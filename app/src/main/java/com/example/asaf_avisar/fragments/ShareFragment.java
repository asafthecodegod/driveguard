package com.example.asaf_avisar.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.asaf_avisar.R;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * Fragment for sharing content to various social media platforms and other apps.
 * Supports sharing text, images, and links with customizable privacy settings.
 */
public class ShareFragment extends Fragment {

    //==========================================================================================
    // DISPLAY LAYER - UI Components and Display Methods
    //==========================================================================================

    // UI Components
    private TextView shareHeader;
    private ImageView contentImagePreview;
    private TextView contentTextPreview;
    private TextInputEditText shareMessageInput;
    private LinearLayout shareWhatsapp, shareFacebook, shareInstagram, shareMore;
    private RadioGroup privacyRadioGroup;
    private RadioButton privacyPublic, privacyFriends, privacyPrivate;
    private Button btnCancel, btnShare;

    // Content type constants for UI handling
    private static final int CONTENT_TYPE_TEXT = 0;
    private static final int CONTENT_TYPE_IMAGE = 1;
    private static final int CONTENT_TYPE_LINK = 2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_share, container, false);

        // Initialize UI components
        initializeUIComponents(view);

        // Set up event listeners
        setupEventListeners();

        // Initialize data components
        initializeLogicComponents();

        // Update UI with content to share
        updateContentPreview();

        return view;
    }

    /**
     * Initialize all UI components
     */
    private void initializeUIComponents(View view) {
        shareHeader = view.findViewById(R.id.share_header);
        contentImagePreview = view.findViewById(R.id.content_image_preview);
        contentTextPreview = view.findViewById(R.id.content_text_preview);
        shareMessageInput = view.findViewById(R.id.share_message_input);

        // Share option buttons
        shareWhatsapp = view.findViewById(R.id.share_whatsapp);
        shareFacebook = view.findViewById(R.id.share_facebook);
        shareInstagram = view.findViewById(R.id.share_instagram);
        shareMore = view.findViewById(R.id.share_more);

        // Privacy radio buttons
        privacyRadioGroup = view.findViewById(R.id.privacy_radio_group);
        privacyPublic = view.findViewById(R.id.privacy_public);
        privacyFriends = view.findViewById(R.id.privacy_friends);
        privacyPrivate = view.findViewById(R.id.privacy_private);

        // Action buttons
        btnCancel = view.findViewById(R.id.btn_cancel);
        btnShare = view.findViewById(R.id.btn_share);
    }

    /**
     * Set up event listeners for interactive UI elements
     */
    private void setupEventListeners() {
        shareWhatsapp.setOnClickListener(v -> handleWhatsAppShare());
        shareFacebook.setOnClickListener(v -> handleFacebookShare());
        shareInstagram.setOnClickListener(v -> handleInstagramShare());
        shareMore.setOnClickListener(v -> handleGenericShare());

        btnCancel.setOnClickListener(v -> handleCancelClick());
        btnShare.setOnClickListener(v -> handleShareClick());

        privacyRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            updatePrivacySetting(checkedId);
        });
    }

    /**
     * Update the content preview based on the content type
     */
    private void updateContentPreview() {
        if (contentToShare == null) {
            showEmptyContentMessage();
            return;
        }

        switch (contentType) {
            case CONTENT_TYPE_IMAGE:
                displayImagePreview();
                break;
            case CONTENT_TYPE_LINK:
                displayLinkPreview();
                break;
            case CONTENT_TYPE_TEXT:
            default:
                displayTextPreview();
                break;
        }
    }

    /**
     * Display an image preview in the content area
     */
    private void displayImagePreview() {
        contentImagePreview.setVisibility(View.VISIBLE);
        if (contentImageBitmap != null) {
            contentImagePreview.setImageBitmap(contentImageBitmap);
            contentTextPreview.setText(shareTitle != null ? shareTitle : "Image to share");
        } else {
            contentImagePreview.setImageResource(R.drawable.ic_default_profile);
            contentTextPreview.setText("Image not available");
        }
    }

    /**
     * Display a link preview in the content area
     */
    private void displayLinkPreview() {
        contentImagePreview.setVisibility(View.VISIBLE);
        contentImagePreview.setImageResource(R.drawable.ic_default_profile); // Use a link icon here
        contentTextPreview.setText(contentToShare);
    }

    /**
     * Display text content in the preview area
     */
    private void displayTextPreview() {
        contentImagePreview.setVisibility(View.GONE);
        contentTextPreview.setText(contentToShare);
    }

    /**
     * Show a message when no content is available to share
     */
    private void showEmptyContentMessage() {
        contentImagePreview.setVisibility(View.GONE);
        contentTextPreview.setText("No content to share");
        disableShareButtons();
    }

    /**
     * Disable all share buttons when no content is available
     */
    private void disableShareButtons() {
        btnShare.setEnabled(false);
        shareWhatsapp.setEnabled(false);
        shareFacebook.setEnabled(false);
        shareInstagram.setEnabled(false);
        shareMore.setEnabled(false);
    }

    /**
     * Display a toast message to the user
     */
    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    //==========================================================================================
    // LOGIC LAYER - Business Logic and Data Management
    //==========================================================================================

    // Content data
    private String contentToShare;
    private String shareTitle;
    private Bitmap contentImageBitmap;
    private int contentType = CONTENT_TYPE_TEXT;
    private int privacySetting = 0; // 0: Public, 1: Friends, 2: Private

    // Bundle keys for sharing content
    private static final String ARG_CONTENT = "arg_content";
    private static final String ARG_TITLE = "arg_title";
    private static final String ARG_TYPE = "arg_type";
    private static final String ARG_IMAGE_URI = "arg_image_uri";

    /**
     * Create a new instance of the share fragment for text content
     */
    public static ShareFragment newInstanceForText(String content, String title) {
        ShareFragment fragment = new ShareFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CONTENT, content);
        args.putString(ARG_TITLE, title);
        args.putInt(ARG_TYPE, CONTENT_TYPE_TEXT);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Create a new instance of the share fragment for image content
     */
    public static ShareFragment newInstanceForImage(String imageUri, String title) {
        ShareFragment fragment = new ShareFragment();
        Bundle args = new Bundle();
        args.putString(ARG_IMAGE_URI, imageUri);
        args.putString(ARG_TITLE, title);
        args.putInt(ARG_TYPE, CONTENT_TYPE_IMAGE);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Create a new instance of the share fragment for link content
     */
    public static ShareFragment newInstanceForLink(String link, String title) {
        ShareFragment fragment = new ShareFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CONTENT, link);
        args.putString(ARG_TITLE, title);
        args.putInt(ARG_TYPE, CONTENT_TYPE_LINK);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Initialize logic components and load data from arguments
     */
    private void initializeLogicComponents() {
        if (getArguments() != null) {
            contentType = getArguments().getInt(ARG_TYPE, CONTENT_TYPE_TEXT);
            shareTitle = getArguments().getString(ARG_TITLE);

            if (contentType == CONTENT_TYPE_IMAGE) {
                String imageUri = getArguments().getString(ARG_IMAGE_URI);
                loadImageFromUri(imageUri);
                contentToShare = shareTitle; // Use title as content description
            } else {
                contentToShare = getArguments().getString(ARG_CONTENT);
            }
        }
    }

    /**
     * Load image from URI string
     */
    private void loadImageFromUri(String uriString) {
        if (uriString != null) {
            try {
                Uri uri = Uri.parse(uriString);
                contentImageBitmap = MediaStore.Images.Media.getBitmap(
                        requireActivity().getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
                showToast("Failed to load image");
            }
        }
    }

    /**
     * Update the privacy setting based on selected radio button
     */
    private void updatePrivacySetting(int checkedId) {
        if (checkedId == R.id.privacy_public) {
            privacySetting = 0;
        } else if (checkedId == R.id.privacy_friends) {
            privacySetting = 1;
        } else if (checkedId == R.id.privacy_private) {
            privacySetting = 2;
        }
    }

    /**
     * Handle WhatsApp share button click
     */
    private void handleWhatsAppShare() {
        if (contentType == CONTENT_TYPE_IMAGE) {
            shareImageToWhatsApp();
        } else {
            shareTextToWhatsApp();
        }
    }

    /**
     * Handle Facebook share button click
     */
    private void handleFacebookShare() {
        if (contentType == CONTENT_TYPE_IMAGE) {
            shareImageToFacebook();
        } else {
            shareTextToFacebook();
        }
    }

    /**
     * Handle Instagram share button click
     */
    private void handleInstagramShare() {
        if (contentType == CONTENT_TYPE_IMAGE) {
            shareImageToInstagram();
        } else {
            showToast("Instagram only supports image sharing");
        }
    }

    /**
     * Handle generic share button click (Android system share dialog)
     */
    private void handleGenericShare() {
        String message = getShareMessage();

        if (contentType == CONTENT_TYPE_IMAGE) {
            shareImageGeneric(message);
        } else {
            shareTextGeneric(message);
        }
    }

    /**
     * Handle cancel button click
     */
    private void handleCancelClick() {
        // Close the fragment
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }

    /**
     * Handle main share button click
     */
    private void handleShareClick() {
        // Use generic share as default action for main share button
        handleGenericShare();
    }

    /**
     * Get the combined share message including user input
     */
    private String getShareMessage() {
        String userMessage = shareMessageInput.getText().toString().trim();
        if (userMessage.isEmpty()) {
            return contentToShare;
        } else {
            return userMessage + "\n\n" + contentToShare;
        }
    }

    /**
     * Share text content to WhatsApp
     */
    private void shareTextToWhatsApp() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.setPackage("com.whatsapp");
        intent.putExtra(Intent.EXTRA_TEXT, getShareMessage());

        try {
            startActivity(intent);
        } catch (Exception e) {
            showToast("WhatsApp not installed");
        }
    }

    /**
     * Share image content to WhatsApp
     */
    private void shareImageToWhatsApp() {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/*");
            intent.setPackage("com.whatsapp");

            Uri imageUri = getImageUriFromBitmap(contentImageBitmap);
            intent.putExtra(Intent.EXTRA_STREAM, imageUri);
            intent.putExtra(Intent.EXTRA_TEXT, getShareMessage());
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(intent);
        } catch (Exception e) {
            showToast("Failed to share to WhatsApp");
        }
    }

    /**
     * Share text content to Facebook
     */
    private void shareTextToFacebook() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.setPackage("com.facebook.katana");
        intent.putExtra(Intent.EXTRA_TEXT, getShareMessage());

        try {
            startActivity(intent);
        } catch (Exception e) {
            // Try Facebook Lite
            try {
                intent.setPackage("com.facebook.lite");
                startActivity(intent);
            } catch (Exception e2) {
                showToast("Facebook not installed");
            }
        }
    }

    /**
     * Share image content to Facebook
     */
    private void shareImageToFacebook() {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/*");
            intent.setPackage("com.facebook.katana");

            Uri imageUri = getImageUriFromBitmap(contentImageBitmap);
            intent.putExtra(Intent.EXTRA_STREAM, imageUri);
            intent.putExtra(Intent.EXTRA_TEXT, getShareMessage());
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(intent);
        } catch (Exception e) {
            // Try Facebook Lite
            try {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/*");
                intent.setPackage("com.facebook.lite");

                Uri imageUri = getImageUriFromBitmap(contentImageBitmap);
                intent.putExtra(Intent.EXTRA_STREAM, imageUri);
                intent.putExtra(Intent.EXTRA_TEXT, getShareMessage());
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                startActivity(intent);
            } catch (Exception e2) {
                showToast("Facebook not installed");
            }
        }
    }

    /**
     * Share image content to Instagram
     */
    private void shareImageToInstagram() {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/*");
            intent.setPackage("com.instagram.android");

            Uri imageUri = getImageUriFromBitmap(contentImageBitmap);
            intent.putExtra(Intent.EXTRA_STREAM, imageUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(intent);
        } catch (Exception e) {
            showToast("Instagram not installed");
        }
    }

    /**
     * Share text content using Android's system share dialog
     */
    private void shareTextGeneric(String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        if (shareTitle != null) {
            intent.putExtra(Intent.EXTRA_SUBJECT, shareTitle);
        }

        startActivity(Intent.createChooser(intent, "Share via"));
    }

    /**
     * Share image content using Android's system share dialog
     */
    private void shareImageGeneric(String text) {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/*");

            Uri imageUri = getImageUriFromBitmap(contentImageBitmap);
            intent.putExtra(Intent.EXTRA_STREAM, imageUri);
            intent.putExtra(Intent.EXTRA_TEXT, text);
            if (shareTitle != null) {
                intent.putExtra(Intent.EXTRA_SUBJECT, shareTitle);
            }
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(Intent.createChooser(intent, "Share image via"));
        } catch (Exception e) {
            showToast("Failed to share image");
        }
    }

    /**
     * Convert a bitmap to a Uri that can be shared
     */
    private Uri getImageUriFromBitmap(Bitmap bitmap) throws IOException {
        if (bitmap == null || getContext() == null) {
            throw new IOException("Invalid bitmap or context");
        }

        File imagesFolder = new File(requireContext().getCacheDir(), "images");
        if (!imagesFolder.exists()) {
            imagesFolder.mkdirs();
        }

        File file = new File(imagesFolder, UUID.randomUUID().toString() + ".png");
        FileOutputStream stream = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
        stream.flush();
        stream.close();

        return FileProvider.getUriForFile(requireContext(),
                requireContext().getPackageName() + ".fileprovider", file);
    }
}