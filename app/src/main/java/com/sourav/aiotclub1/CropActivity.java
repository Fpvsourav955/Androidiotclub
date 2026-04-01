package com.sourav.aiotclub1;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;

import java.io.File;

public class CropActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);


        String uriString = getIntent().getStringExtra("mediaUri");
        if (uriString == null) {
            finish();
            return;
        }

        Uri sourceUri = Uri.parse(uriString);
        Uri destinationUri = Uri.fromFile(new File(getCacheDir(), "cropped.jpg"));

        UCrop.Options options = new UCrop.Options();
        options.setFreeStyleCropEnabled(true);
        options.setToolbarTitle("Crop Image");
        options.setCompressionQuality(100); // full quality
        options.setHideBottomControls(false);
        options.setShowCropFrame(true);
        options.setShowCropGrid(true);

        options.setAllowedGestures(UCropActivity.ALL, UCropActivity.ALL, UCropActivity.ALL);
        options.setDimmedLayerColor(R.style.UCropToolbarTheme);


        UCrop.of(sourceUri, destinationUri)
                .withAspectRatio(4, 3)
                .withOptions(options)
                .start(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == UCrop.REQUEST_CROP) {
            Intent result = new Intent();
            if (resultCode == RESULT_OK && data != null) {
                Uri resultUri = UCrop.getOutput(data);
                if (resultUri != null) {
                    result.putExtra("croppedUri", resultUri.toString());
                    setResult(RESULT_OK, result);
                }
            } else if (resultCode == UCrop.RESULT_ERROR && data != null) {
                result.putExtra("error", UCrop.getError(data).getMessage());
                setResult(RESULT_CANCELED, result);
            }
            finish();
        }
    }
}
