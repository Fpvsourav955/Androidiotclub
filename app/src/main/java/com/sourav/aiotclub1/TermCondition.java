package com.sourav.aiotclub1;

import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowInsetsControllerCompat;

public class TermCondition extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);


        WindowInsetsControllerCompat insetsController =
                new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        insetsController.setAppearanceLightStatusBars(false);
        setContentView(R.layout.activity_term_condition);


        TextView privacyText = findViewById(R.id.privacyText);
        String policy = "Privacy Policy\n" +
                "Effective Date: 05 June 2025\n" +
                "App Name: AIOT Club\n" +
                "Developer: AIOT Club Team\n\n" +
                "1. Information We Collect\n" +
                "a. Google Account Information:\n" +
                "- Name\n" +
                "- Email address\n" +
                "- Profile picture (if available)\n\n" +
                "b. Device and Usage Data:\n" +
                "- Device model, OS version\n" +
                "- IP address\n" +
                "- App version\n" +
                "- Crash logs (via Firebase)\n\n" +
                "c. Media and Storage Access:\n" +
                "- Read/Write to External Storage\n" +
                "- Notifications for events\n\n" +
                "2. Permissions We Request:\n" +
                "INTERNET – For Firebase and communication\n" +
                "ACCESS_NETWORK_STATE – Check internet\n" +
                "POST_NOTIFICATIONS – Send updates\n" +
                "READ_EXTERNAL_STORAGE – Read media files\n" +
                "WRITE_EXTERNAL_STORAGE – Save media\n" +
                "MANAGE_EXTERNAL_STORAGE – For Android 11+\n" +
                "Custom Permission – For internal events only\n\n" +
                "3. How We Use Your Data:\n" +
                "- Authentication and profile creation\n" +
                "- Notifications\n" +
                "- Crash reports and analytics\n" +
                "- Display profile images\n\n" +
                "4. Third-Party Services:\n" +
                "- Firebase\n" +
                "- Glide, Picasso\n" +
                "- Lottie, Shimmer, SpinKit\n\n" +
                "5. Data Sharing:\n" +
                "- Not shared except with Firebase or as required by law\n\n" +
                "6. Data Security:\n" +
                "- HTTPS communication\n" +
                "- Obfuscated code (ProGuard)\n" +
                "- Minimal data access\n\n" +
                "7. Your Rights:\n" +
                "- View/update your data\n" +
                "- Request account deletion\n" +
                "- Revoke permissions\n\n" +
                "8. Children’s Privacy:\n" +
                "- No data knowingly collected from children under 13\n\n" +
                "9. Changes:\n" +
                "- Updates will be shared via app or email\n\n" +
                "10. Contact:\n" +
                "Email: aiotclub.support@email.com\n" +
                "Developer: AIOT Club Team\n";

        privacyText.setText(policy);

    }
}