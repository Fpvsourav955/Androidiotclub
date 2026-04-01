package com.sourav.aiotclub1;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Archive1 extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);


        WindowInsetsControllerCompat insetsController =
                new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        insetsController.setAppearanceLightStatusBars(false);

        setContentView(R.layout.activity_archive1);

        TextView eventDescription = findViewById(R.id.event_description);
        eventDescription.setText("🚀 Android App Development Workshop Recap\n\n"
                + "We are excited to share the successful completion of our three-day Android App Development Workshop, held in Smart Lab 1 and 2 at GIET University. With a phenomenal turnout of over 200 participants, the workshop empowered students with hands-on experience in mobile app development using Kotlin and Android Studio.\n\n"
                + "🗓️ Day 1: Getting Started with Kotlin\n"
                + "- Introduction to Kotlin basics and logic building\n"
                + "- Syntax, variables, conditionals, loops, and functions\n\n"
                + "🗓️ Day 2: Android Layout & XML Design\n"
                + "- Writing XML layout files\n"
                + "- Using ConstraintLayout for flexible UI\n"
                + "- UI component exploration\n\n"
                + "🗓️ Day 3: Kotlin Backend + Full Stack App\n"
                + "- Backend logic and integration\n"
                + "- Creating a mini full-stack app\n"
                + "- Connecting frontend with Kotlin\n\n"
                + "🎯 Tools Used: Android Studio, Kotlin, Firebase\n"
                + "👥 Participants: 200+ students\n"
                + "📍 Venue: Smart Lab 1 & 2, GIET University\n\n"
                + "🙏 Special thanks to the coordinators:\n"
                + "Priyanshu Sekhar Nanda, Lallu Prasad Panda, Bikash Ranjan Barik, Ayushman Panigrahy, Debasish Mishra, Priyanka Panda, Namrata Bhanja, Abhishek Kumar, Rohit Kumar Naik, Ralesan Pradhan, Lingaraj Rath, Kushal Chand, Bushra Bano, Priyadarshani Patro, Monosmita Behera, Sourav Kumar Pati, @Vinay Kumar, Chitranshu Sanket, Gyana Ranjan Mohanty, Anshuman Mahanta\n\n"
                + "🏛️ Supported by: Android and IoT Club, ACIC GIET Foundation, YHills, GIET University\n\n"
                + "Stay tuned for more events!");

        RecyclerView recyclerView = findViewById(R.id.recyclerViewArchive);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Archive1Model> archiveList = new ArrayList<>();
        archiveList.add(new Archive1Model("https://firebasestorage.googleapis.com/v0/b/android-iot-club.firebasestorage.app/o/event_post_images%2FWhatsApp%20Image%202025-08-11%20at%2015.50.09_bfb46edc.jpg?alt=media&token=4c30a6b6-e117-43a0-b3e8-351ef396986c", "Hands-on layout design using XML and ConstraintLayout in full swing.\n" +
                "Students explored UI fundamentals to bring their app ideas to life."));
        archiveList.add(new Archive1Model("https://firebasestorage.googleapis.com/v0/b/android-iot-club.firebasestorage.app/o/event_post_images%2FWhatsApp%20Image%202025-08-11%20at%2015.50.10_cb45acfb.jpg?alt=media&token=ec8df234-3c4b-4917-8a53-40761288808e", "Backend session using Kotlin covering Firebase integration and logic building.\n" +
                "Participants developed modules of a full-stack Android app."));
        archiveList.add(new Archive1Model("https://firebasestorage.googleapis.com/v0/b/android-iot-club.firebasestorage.app/o/event_post_images%2FWhatsApp%20Image%202025-08-11%20at%2015.50.09_787d1594.jpg?alt=media&token=821fa6af-1a5a-443e-a628-39dd9677abfe", "Participants diving into Kotlin basics on Day 1 of our Android workshop.\n" +
                "The session focused on building a strong foundation in programming logic."));
        archiveList.add(new Archive1Model("https://firebasestorage.googleapis.com/v0/b/android-iot-club.firebasestorage.app/o/event_post_images%2FWhatsApp%20Image%202025-08-11%20at%2015.50.10_67e585dd.jpg?alt=media&token=be686c62-3ab9-4847-8ee8-83992831bb83", "Group collaboration and live app testing in Smart Lab 2.\n" +
                "Focused teamwork helped bring real-world Android projects to completion.\n" +
                "\n"));
        archiveList.add(new Archive1Model("https://firebasestorage.googleapis.com/v0/b/android-iot-club.firebasestorage.app/o/event_post_images%2FWhatsApp%20Image%202025-08-11%20at%2015.50.11_a9591574.jpg?alt=media&token=a869bdc2-36e7-439b-9f6a-fcaecb34677c", "Group collaboration and live app testing in Smart Lab 2.\n" +
                "Focused teamwork helped bring real-world Android projects to completion.\n" +
                "\n"));

        Archive1Adapter adapter = new Archive1Adapter(this, archiveList);
        recyclerView.setAdapter(adapter);
    }

}
