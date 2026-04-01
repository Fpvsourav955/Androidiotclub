package com.sourav.aiotclub1;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.yalantis.ucrop.UCrop;

import java.io.File;

public class UploadActivity extends AppCompatActivity {

    private Uri mediaUri;
    private ImageView previewImage;
    private EditText descriptionEditText, tagsEditText;
    private Button uploadButton;
    private FirebaseStorage storage;
    private DatabaseReference databaseRef;
    private FirebaseUser currentUser;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Full screen with light status bar
        Window window = getWindow();
        WindowCompat.setDecorFitsSystemWindows(window, false);
        WindowInsetsControllerCompat controller = new WindowInsetsControllerCompat(window, window.getDecorView());
        controller.setAppearanceLightStatusBars(true);

        setContentView(R.layout.activity_upload);

        // Initialize UI
        previewImage = findViewById(R.id.uploadPreviewImage);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        tagsEditText = findViewById(R.id.uploadTags);
        uploadButton = findViewById(R.id.uploadButton);
        loadingDialog = new LoadingDialog(this);

        // Get URI from intent
        String uriString = getIntent().getStringExtra("mediaUri");
        if (uriString != null) {
            mediaUri = Uri.parse(uriString);
            previewImage.setImageURI(mediaUri);
        } else {
            Toast.makeText(this, "No media selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        previewImage.setOnClickListener(v -> {
            Intent intent = new Intent(UploadActivity.this, CropActivity.class);
            intent.putExtra("mediaUri", mediaUri.toString());
            startActivityForResult(intent, 2001); // Custom request code
        });


        // Firebase setup
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        storage = FirebaseStorage.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference("posts");

        // Upload button click
        uploadButton.setOnClickListener(v -> {
            loadingDialog.startLoadingDiloag();
            uploadPost();
        });
    }

    private void uploadPost() {
        String description = descriptionEditText.getText().toString().trim();
        String tags = tagsEditText.getText().toString().trim();

        if (mediaUri != null && currentUser != null) {
            String fileName = "post_" + System.currentTimeMillis();
            StorageReference storageRef = storage.getReference().child("posts/" + fileName);

            storageRef.putFile(mediaUri)
                    .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String downloadUrl = uri.toString();
                        String postId = databaseRef.push().getKey();

                        Post post = new Post(
                                postId,
                                currentUser.getUid(),
                                currentUser.getDisplayName(),
                                currentUser.getPhotoUrl() != null ? currentUser.getPhotoUrl().toString() : "",
                                downloadUrl,
                                description,
                                tags,
                                System.currentTimeMillis()
                        );

                        if (postId != null) {
                            databaseRef.child(postId).setValue(post).addOnCompleteListener(task -> {
                                loadingDialog.dismissDialog();
                                if (task.isSuccessful()) {
                                    Toast.makeText(this, "Post uploaded!", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(this, "Upload failed", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }))
                    .addOnFailureListener(e -> {
                        loadingDialog.dismissDialog();
                        Toast.makeText(this, "Upload error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            loadingDialog.dismissDialog();
            Toast.makeText(this, "No media or user not logged in", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2001 && resultCode == RESULT_OK && data != null) {
            String croppedUriString = data.getStringExtra("croppedUri");
            if (croppedUriString != null) {
                mediaUri = Uri.parse(croppedUriString);
                previewImage.setImageURI(mediaUri);
            }
        }
    }

        }

