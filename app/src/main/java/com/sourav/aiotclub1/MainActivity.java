package com.sourav.aiotclub1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.appupdate.AppUpdateOptions;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nafis.bottomnavigation.NafisBottomNavigation;
import com.sourav.aiotclub1.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    FloatingActionButton fabUpload;
    private DrawerLayout drawerLayout;
    private AppUpdateManager appUpdateManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);


        WindowInsetsControllerCompat insetsController =
                new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        insetsController.setAppearanceLightStatusBars(false);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.POST_NOTIFICATIONS
            }, 101);
        }

        checkForInAppUpdate();

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        fabUpload = findViewById(R.id.fab_upload);
        fabUpload.setOnClickListener(v -> openMediaPicker());
        fabUpload.setVisibility(View.GONE);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            DatabaseReference adminRef = FirebaseDatabase.getInstance().getReference("Admins").child(uid);

            adminRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists() && Boolean.TRUE.equals(snapshot.getValue(Boolean.class))) {

                        fabUpload.setTag("isAdmin");
                        fabUpload.setOnClickListener(v -> openMediaPicker());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(MainActivity.this, "Failed to check admin status", Toast.LENGTH_SHORT).show();
                }
            });
        }


        drawerLayout = findViewById(R.id.drawerLayout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.bringToFront();

        setSupportActionBar(toolbar);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        TextView profile_name = headerView.findViewById(R.id.Profile_name);
        TextView profile_email = headerView.findViewById(R.id.Profile_email);

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            profile_name.setText(user.getDisplayName() != null ? user.getDisplayName() : "Unknown Name");
            profile_email.setText(user.getEmail() != null ? user.getEmail() : "No Email Available");
        } else {
            Toast.makeText(this, "No user signed in!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, Login.class));
            finish();
        }
        NafisBottomNavigation bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.add(new NafisBottomNavigation.Model(1, R.drawable.home_selector));
        bottomNavigation.add(new NafisBottomNavigation.Model(2, R.drawable.event_selector));
        bottomNavigation.add(new NafisBottomNavigation.Model(3, R.drawable.add_box));
        bottomNavigation.add(new NafisBottomNavigation.Model(4, R.drawable.message_selector));
        bottomNavigation.add(new NafisBottomNavigation.Model(5, R.drawable.profile_selector));

        loadFragment(new HomeFragment());
        bottomNavigation.show(1, true);

        bottomNavigation.setOnShowListener(model -> {
            int selectedTab = model.getId();

            Fragment fragment = switch (selectedTab) {
                case 1 -> new HomeFragment();
                case 2 -> new EventFragment();
                case 3 -> {
                    Fragment current = getSupportFragmentManager().findFragmentById(R.id.frame_layout);
                    if (!(current instanceof PostFragment)) {
                        yield new PostFragment();
                    } else {
                        yield null;
                    }
                }

                case 4 ->{
                    Fragment current = getSupportFragmentManager().findFragmentById(R.id.frame_layout);
                    if (!(current instanceof WorkshopFragment)) {
                        yield new WorkshopFragment();
                    } else {
                        yield null;
                    }
                }
                case 5 -> new ProfileFragment();
                default -> null;
            };

            if (fragment != null) {
                loadFragment(fragment);
                updateFabVisibility(selectedTab);
            }



            return null;
        });

    }

    private void loadFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, fragment);
            fragmentTransaction.commit();
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.Faculity1 -> startActivity(new Intent(this, Faculity.class));
            case R.id.Team -> startActivity(new Intent(this, Members.class));
            case R.id.Devteam -> startActivity(new Intent(this, DevTeam.class));
            case R.id.AboutClub -> startActivity(new Intent(this, AboutClub.class));
            case R.id.Feedback -> startActivity(new Intent(this, Report.class));
            case R.id.share -> {
                String url = "https://play.google.com/store/apps/details?id=com.sourav.aiotclub1";
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Check out this app!");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Hey! Try this app:\n" + url);
                startActivity(Intent.createChooser(shareIntent, "Share via"));
            }

            case R.id.contactus -> startActivity(new Intent(this, Contact.class));
//            case R.id.Gallery -> startActivity(new Intent(this, Gallery.class));
            case R.id.terms -> startActivity(new Intent(this, TermCondition.class));
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isConnected()) {
            startActivity(new Intent(this, NoInternet.class));
        }
    }

    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        }
        return false;
    }

    private void openMediaPicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*", "video/*"});
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select Media"), 1001);
    }

    private void checkForInAppUpdate() {
        appUpdateManager = AppUpdateManagerFactory.create(this);
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                    appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                try {
                    appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build().appUpdateType(),
                            this,
                            100);
                } catch (IntentSender.SendIntentException e) {
                    throw new RuntimeException(e);
                }
            }
        }).addOnFailureListener(e ->
                Toast.makeText(this, "Update check failed", Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode != RESULT_OK) {
            Toast.makeText(this, "Update canceled", Toast.LENGTH_SHORT).show();
        }

        if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
            Uri mediaUri = data.getData();
            if (mediaUri != null) {
                Intent intent = new Intent(this, UploadActivity.class);
                intent.putExtra("mediaUri", mediaUri.toString());
                startActivity(intent);
            }
        }
    }
    private void updateFabVisibility(int selectedTab) {
        boolean isAdmin = "isAdmin".equals(fabUpload.getTag());
        if (isAdmin && selectedTab == 3) {
            fabUpload.setVisibility(View.VISIBLE);
        } else {
            fabUpload.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


}
