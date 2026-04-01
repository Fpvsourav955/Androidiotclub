package com.sourav.aiotclub1;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Archive2 extends AppCompatActivity {

    private VideoPagerAdapter adapter;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        WindowInsetsControllerCompat insetsController =
                new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        insetsController.setAppearanceLightStatusBars(false);
        setContentView(R.layout.activity_archive2);

        TextView eventDescription = findViewById(R.id.event_description);
        eventDescription.setText(
                """
                        🎉 Orientation Program for 2024 B.Tech Students
                        
                        On 31st September 2024, the Android and IoT Club, in collaboration with ACIC GIET Foundation, YHills, and GIET University, organized a grand orientation program for the freshers of the 2024 B.Tech batch. The event took place at GIET University, Gunupur, and was graced by an energetic audience and enthusiastic participation.
                        
                        💫 The program began with a warm welcome to the new students, followed by an introduction to the club’s objectives, upcoming workshops, and opportunities in Android and IoT development.
                        
                        🎭 Cultural Highlights:
                        - Energetic dance performances
                        - Soulful songs and music sessions
                        - Engaging shayaris that kept the audience entertained
                        
                        🏆 Prize Distribution:
                        - Recognition for winners of recent Hackathons
                        - Awards for top performers in Ideathons
                        
                        🙏 Special thanks to the coordinators:
                        Priyanshu Sekhar Nanda, Lallu Prasad Panda, Bikash Ranjan Barik, Ayushman Panigrahy, Debasish Mishra, Priyanka Panda, Namrata Bhanja, Abhishek Kumar, Rohit Kumar Naik, Ralesan Pradhan, Lingaraj Rath, Kushal Chand, Bushra Bano, Priyadarshani Patro, Monosmita Behera, Sourav Kumar Pati, @Vinay Kumar, Chitranshu Sanket, Gyana Ranjan Mohanty, Anshuman Mahanta
                        
                        📍 Venue: GIET University, Gunupur
                        🏛️ Organized by: Android and IoT Club, ACIC GIET Foundation, YHills
                        
                        ✨ We look forward to an amazing journey ahead with the new batch!"""
        );

        ViewPager2 videoViewPager = findViewById(R.id.videoViewPager);
        List<String> videoUrls = Arrays.asList(
                "https://firebasestorage.googleapis.com/v0/b/android-iot-club.firebasestorage.app/o/videos%2Forientationvideo.mp4?alt=media&token=510042ed-6468-4106-bdee-7b7275e1b352",
                "https://firebasestorage.googleapis.com/v0/b/android-iot-club.firebasestorage.app/o/WhatsApp%20Video%202025-08-11%20at%2017.16.01_ce9dc8ca.mp4?alt=media&token=4bb3f820-397a-448e-8e00-97caed9eeac3",
                "https://firebasestorage.googleapis.com/v0/b/android-iot-club.firebasestorage.app/o/WhatsApp%20Video%202025-08-11%20at%2017.16.16_5a552d54.mp4?alt=media&token=e9c9baa6-3f24-4ca1-a95a-4b0cd13d9322"
        );

        adapter = new VideoPagerAdapter(this, videoUrls);
        videoViewPager.setAdapter(adapter);

        videoViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                adapter.playVideoAt(position);
            }
        });


        RecyclerView recyclerView = findViewById(R.id.recyclerViewArchive);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<Archive1Model> archiveList = new ArrayList<>();
        archiveList.add(new Archive1Model("https://firebasestorage.googleapis.com/v0/b/android-iot-club.firebasestorage.app/o/event_post_images%2FIMG-20250811-WA0010.jpg?alt=media&token=b4ba7455-51e2-449f-ba22-356b942fafb1", " "));
        archiveList.add(new Archive1Model("https://firebasestorage.googleapis.com/v0/b/android-iot-club.firebasestorage.app/o/event_post_images%2FIMG-20250811-WA0012.jpg?alt=media&token=a56bdaae-283f-4873-9652-90349089858a", " "));
        archiveList.add(new Archive1Model("https://firebasestorage.googleapis.com/v0/b/android-iot-club.firebasestorage.app/o/event_post_images%2FIMG-20250811-WA0016.jpg?alt=media&token=47ee82bf-0194-42a7-a941-b9f809e89cde", " "));
        archiveList.add(new Archive1Model("https://firebasestorage.googleapis.com/v0/b/android-iot-club.firebasestorage.app/o/event_post_images%2Fimg22.jpg?alt=media&token=bcb6156c-3ca2-4fb8-82ca-2cb35908fed1", " "));
        archiveList.add(new Archive1Model("https://firebasestorage.googleapis.com/v0/b/android-iot-club.firebasestorage.app/o/event_post_images%2FIMG-20250811-WA0013.jpg?alt=media&token=aa6a4731-9804-45e4-bef5-447c88142fab", ""));
        archiveList.add(new Archive1Model("https://firebasestorage.googleapis.com/v0/b/android-iot-club.firebasestorage.app/o/event_post_images%2FWhatsApp%20Image%202025-08-11%20at%2016.45.26_f0927f99.jpg?alt=media&token=5b6a3051-73f5-4678-a50c-04bd0ec754b8", " "));
        archiveList.add(new Archive1Model("https://firebasestorage.googleapis.com/v0/b/android-iot-club.firebasestorage.app/o/event_post_images%2FWhatsApp%20Image%202025-08-11%20at%2016.45.26_55d7140c.jpg?alt=media&token=2bae76bd-b911-4a95-9920-275e6f81348e", " "));
        archiveList.add(new Archive1Model("https://firebasestorage.googleapis.com/v0/b/android-iot-club.firebasestorage.app/o/event_post_images%2FWhatsApp%20Image%202025-08-11%20at%2016.45.27_e468f15d.jpg?alt=media&token=f80f5abf-4ee9-4488-9ab6-1bf3e01931e6", ""));

        Archive1Adapter imgAdapter = new Archive1Adapter(this, archiveList);
        recyclerView.setAdapter(imgAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adapter != null) {
            adapter.releasePlayers();
        }
    }
}
