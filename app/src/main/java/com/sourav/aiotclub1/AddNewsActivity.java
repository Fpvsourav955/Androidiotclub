package com.sourav.aiotclub1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class AddNewsActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int STORAGE_PERMISSION_CODE = 100;
    private  LoadingDialog loadingDialog;

    private EditText newsTitle, newsDescription;
    private TextView imageNameTextView;

    private Uri imageUri;

    private FirebaseUser currentUser;
    private DatabaseReference newsRef;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_news);
        loadingDialog = new LoadingDialog(AddNewsActivity.this);


        newsTitle = findViewById(R.id.newstitle);
        newsDescription = findViewById(R.id.newsdescription);
        ImageButton imageUploadButton = findViewById(R.id.imageUploadButton);
        imageNameTextView = findViewById(R.id.imageNameTextView);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        newsRef = FirebaseDatabase.getInstance().getReference("news");
        storageRef = FirebaseStorage.getInstance().getReference("news_images");

        checkAndRequestStoragePermission();

        imageUploadButton.setOnClickListener(v -> openFileChooser());

        findViewById(R.id.Upload).setOnClickListener(v -> uploadNews());

    }

    private void checkAndRequestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(intent);
                Toast.makeText(this, "Please grant all files access permission", Toast.LENGTH_LONG).show();
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        STORAGE_PERMISSION_CODE);
            }
        }
    }

    private void openFileChooser() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED || Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        } else {
            Toast.makeText(this, "Storage permission is required to select an image", Toast.LENGTH_SHORT).show();
            checkAndRequestStoragePermission();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Storage permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission denied. Image selection won't work.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @SuppressLint("Range")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            String fileName = "Selected Image";
            if (Objects.equals(imageUri.getScheme(), "content")) {
                try (Cursor cursor = getContentResolver().query(imageUri, null, null, null, null)) {
                    if (cursor != null && cursor.moveToFirst())
                        fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } else {
                String path = imageUri.getPath();
                assert path != null;
                fileName = path.substring(path.lastIndexOf('/') + 1);
            }

            imageNameTextView.setText(fileName);
        }
    }

    private void uploadNews() {
        String title = newsTitle.getText().toString().trim();
        String description = newsDescription.getText().toString().trim();

        if (title.isEmpty()) {
            newsTitle.setError("Title required");
            newsTitle.requestFocus();
            return;
        }

        if (description.isEmpty()) {
            newsDescription.setError("Description required");
            newsDescription.requestFocus();
            return;
        }

        if (imageUri == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }
        loadingDialog.startLoadingDiloag();

        String imageId = UUID.randomUUID().toString();
        StorageReference fileRef = storageRef.child(imageId + ".jpg");

        fileRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    String imagePath = "news_images/" + imageId + ".jpg";


                    Map<String, Object> newsData = new HashMap<>();
                    newsData.put("title", title);
                    newsData.put("description", description);
                    newsData.put("imageUrl", imageUrl);
                    newsData.put("imagePath", imagePath); // << add this line
                    newsData.put("timestamp", System.currentTimeMillis());
                    newsData.put("memberName", currentUser.getDisplayName());
                    newsData.put("memberProfileImageUrl", currentUser.getPhotoUrl() != null ? currentUser.getPhotoUrl().toString() : "");
                    newsData.put("memberUid", currentUser.getUid());

                    newsRef.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            long maxId = 0;
                            for (DataSnapshot child : snapshot.getChildren()) {
                                String key = child.getKey();
                                if (key != null && key.startsWith("news")) {
                                    try {
                                        long id = Long.parseLong(key.substring(4));
                                        if (id > maxId) maxId = id;
                                    } catch (NumberFormatException ignored) {

                                    }
                                }
                            }
                            String newKey = "news" + (maxId + 1);

                            newsRef.child(newKey).setValue(newsData)
                                    .addOnSuccessListener(aVoid -> {
                                        loadingDialog.dismissDialog();
                                        Toast.makeText(AddNewsActivity.this, "News uploaded as " + newKey, Toast.LENGTH_SHORT).show();
                                        clearFields();
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        loadingDialog.dismissDialog();
                                        Toast.makeText(AddNewsActivity.this, "Failed to upload news: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    });
                        }


                        @Override
                        public void onCancelled(@NonNull com.google.firebase.database.DatabaseError error) {
                            loadingDialog.dismissDialog();
                            Toast.makeText(AddNewsActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_LONG).show();

                        }
                    });

                }))
                .addOnFailureListener(e -> Toast.makeText(this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    @SuppressLint("SetTextI18n")
    private void clearFields() {
        newsTitle.setText("");
        newsDescription.setText("");
        imageNameTextView.setText("No file selected");
        imageUri = null;
    }
}
