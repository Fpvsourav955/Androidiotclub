package com.sourav.aiotclub1;

import static androidx.core.content.ContextCompat.getSystemService;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private FirebaseAuth mAuth;
    private LoadingDialog loadingDialog;
    private ProgressBar profileProgressBar;
    private TextView completionPercentage;

    private TextView yearTextView, branchTextView, rollTextView;
    private TextView linkedInTextView, githubTextView;

    public ProfileFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        loadingDialog = new LoadingDialog((Activity) requireContext());

        TextView changePass = view.findViewById(R.id.Changepass);

        profileProgressBar = view.findViewById(R.id.profileProgressBar);
        completionPercentage = view.findViewById(R.id.completionPercentage);
        profileProgressBar.setProgress(0);
        completionPercentage.setText("0%");

        TextView logout = view.findViewById(R.id.logout);

        TextView profileName = view.findViewById(R.id.Profile_name);
        TextView profileEmail = view.findViewById(R.id.Profile_email);
        CircleImageView profileImage = view.findViewById(R.id.Profile_image);

        linkedInTextView = view.findViewById(R.id.linkdinlink);
        githubTextView = view.findViewById(R.id.githublink);

        ImageView linkedInEdit = view.findViewById(R.id.linkdinedit);
        ImageView githubEdit = view.findViewById(R.id.githubedit);

        ImageView yearEdit = view.findViewById(R.id.yearedit);
        ImageView branchEdit = view.findViewById(R.id.branchedit);
        ImageView rollEdit = view.findViewById(R.id.rolledit);

        yearTextView = view.findViewById(R.id.year);
        branchTextView = view.findViewById(R.id.branch);
        rollTextView = view.findViewById(R.id.rollno);
        linkedInTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Copied Link",  linkedInTextView.getText());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(requireContext(), "Link copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });
        profileImage.setScaleType(ImageView.ScaleType.CENTER_CROP);

        changePass.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), ForgetPass.class));
        });

        loadUserData();

        logout.setOnClickListener(v -> {
            mAuth.signOut();
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(requireActivity(), Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                requireActivity().finish();
            }, 500);
        });


        linkedInEdit.setOnClickListener(v -> showEditUrlDialog("Edit LinkedIn URL", "linkedinUrl", linkedInTextView));
        githubEdit.setOnClickListener(v -> showEditUrlDialog("Edit GitHub URL", "githubUrl", githubTextView));

        yearEdit.setOnClickListener(v -> showYearDialog());
        branchEdit.setOnClickListener(v -> showBranchDialog());
        rollEdit.setOnClickListener(v -> showRollDialog());

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            profileName.setText(user.getDisplayName() != null ? user.getDisplayName() : "Unknown Name");
            profileEmail.setText(user.getEmail() != null ? user.getEmail() : "No Email Available");

            Uri photoUri = user.getPhotoUrl();
            if (photoUri != null) {
                Glide.with(this).load(photoUri).into(profileImage);
            } else {
                profileImage.setImageResource(R.drawable.person);
            }
        } else {
            Toast.makeText(getContext(), "No user signed in!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showYearDialog() {
        final String[] years = {"1st", "2nd", "3rd", "4th"};

        new AlertDialog.Builder(requireContext())
                .setTitle("Select Year")
                .setItems(years, (dialog, which) -> {
                    String selectedYear = years[which];
                    yearTextView.setText(selectedYear);
                    saveFieldToFirebase("year", selectedYear);
                    updateProfileCompletion();
                })
                .setCancelable(true)
                .show();
    }

    private void showEditUrlDialog(String title, String firebaseField, TextView targetTextView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(title);

        final EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
        input.setHint("Enter URL here");

        String currentUrl = targetTextView.getText().toString().trim();
        if (!currentUrl.isEmpty() && !currentUrl.equalsIgnoreCase("not specified")) {
            input.setText(currentUrl);
            input.setSelection(currentUrl.length());
        } else {

            input.setText("");
        }


        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String url = input.getText().toString().trim();
            if (!url.isEmpty()) {

                saveFieldToFirebase(firebaseField, url);

                targetTextView.setText(url);
                updateProfileCompletion();
            } else {
                Toast.makeText(requireContext(), "URL cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    @SuppressLint("SetTextI18n")
    private void showBranchDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_branch, null);

        AutoCompleteTextView departmentView = dialogView.findViewById(R.id.departmentEditText);
        AutoCompleteTextView branchView = dialogView.findViewById(R.id.branchEditText);

        String[] departments = {"BTech", "MTech", "MBA","BBA"};
        String[] branches = {"CSE", "ECE", "Mechanical", "Civil","CSE-AIML","CSE-DS","CSE-IOT","EEE","BIo-Tech"};

        departmentView.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, departments));
        branchView.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, branches));

        new AlertDialog.Builder(requireContext())
                .setTitle("Select Department and Branch")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String selectedDepartment = departmentView.getText().toString().trim();
                    String selectedBranch = branchView.getText().toString().trim();

                    if (!selectedDepartment.isEmpty() && !selectedBranch.isEmpty()) {
                        branchTextView.setText(selectedDepartment + " - " + selectedBranch);
                        saveFieldToFirebase("department", selectedDepartment);
                        saveFieldToFirebase("branch", selectedBranch);
                        updateProfileCompletion();
                    } else {
                        Toast.makeText(requireContext(), "Please select both department and branch.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .setCancelable(true)
                .show();
    }

    private void showRollDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_roll, null);
        EditText rollEditText = dialogView.findViewById(R.id.rollNumberEditText);

        new AlertDialog.Builder(requireContext())
                .setTitle("Enter Roll Number")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String enteredRollNumber = rollEditText.getText().toString().trim();
                    if (!enteredRollNumber.isEmpty()) {
                        rollTextView.setText(enteredRollNumber);
                        saveFieldToFirebase("rollNumber", enteredRollNumber);
                        updateProfileCompletion();
                    } else {
                        Toast.makeText(requireContext(), "Roll number cannot be empty.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .setCancelable(true)
                .show();
    }

    private void saveFieldToFirebase(String field, String value) {
        if (value == null || value.trim().isEmpty()) {
            Context context = getContext();
            if (context != null && isAdded()) {
                Toast.makeText(context, "Field cannot be empty!", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(uid);

            Map<String, Object> update = new HashMap<>();
            update.put(field, value);

            ref.updateChildren(update)
                    .addOnSuccessListener(unused -> {
                        Context context = getContext();
                        if (context != null && isAdded()) {
                            Toast.makeText(context, "Updated " + field + " successfully!", Toast.LENGTH_SHORT).show();
                            updateProfileCompletion();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Context context = getContext();
                        if (context != null && isAdded()) {
                            Toast.makeText(context, "Failed to update " + field + ": " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


    private void updateProfileCompletion() {
        int completedFields = 0;

        if (!yearTextView.getText().toString().equalsIgnoreCase("Not Specified")) completedFields++;
        if (!branchTextView.getText().toString().equalsIgnoreCase("Not Specified")) completedFields++;
        if (!rollTextView.getText().toString().equalsIgnoreCase("Not Specified")) completedFields++;
        if (!linkedInTextView.getText().toString().equalsIgnoreCase("Not Specified")) completedFields++;
        if (!githubTextView.getText().toString().equalsIgnoreCase("Not Specified")) completedFields++;

        int targetProgress = completedFields * 20;

        animateProgress(profileProgressBar.getProgress(), targetProgress);
        animatePercentageText(profileProgressBar.getProgress(), targetProgress);
    }




    @SuppressLint("SetTextI18n")
    private void loadUserData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        String uid = user.getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(uid);
        ref.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                String year = snapshot.child("year").getValue(String.class);
                String department = snapshot.child("department").getValue(String.class);
                String branch = snapshot.child("branch").getValue(String.class);
                String rollNumber = snapshot.child("rollNumber").getValue(String.class);
                String linkedinUrl = snapshot.child("linkedinUrl").getValue(String.class);
                String githubUrl = snapshot.child("githubUrl").getValue(String.class);
                if (linkedinUrl != null && !linkedinUrl.isEmpty()) {
                    linkedInTextView.setText(linkedinUrl);
                } else {
                    linkedInTextView.setText("Not Specified");
                }

                if (githubUrl != null && !githubUrl.isEmpty()) {
                    githubTextView.setText(githubUrl);
                } else {
                    githubTextView.setText("Not Specified");
                }

                if (year != null && !year.isEmpty()) {
                    yearTextView.setText(year);
                } else {
                    yearTextView.setText("Not Specified");
                }

                if (department != null && branch != null && !department.isEmpty() && !branch.isEmpty()) {
                    branchTextView.setText(department + " - " + branch);
                } else {
                    branchTextView.setText("Not Specified");
                }

                if (rollNumber != null && !rollNumber.isEmpty()) {
                    rollTextView.setText(rollNumber);
                } else {
                    rollTextView.setText("Not Specified");
                }

                updateProfileCompletion();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(requireContext(), "Failed to load user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });

    }
    private void animateProgress(int from, int to) {
        Handler handler = new Handler();
        int delay = 15;
        final int[] progress = {from};

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (progress[0] < to) {
                    progress[0]++;
                    profileProgressBar.setProgress(progress[0]);
                    handler.postDelayed(this, delay);
                } else if (progress[0] > to) {
                    progress[0]--;
                    profileProgressBar.setProgress(progress[0]);
                    handler.postDelayed(this, delay);
                }
            }
        }, delay);
    }

    private void animatePercentageText(int from, int to) {
        Handler handler = new Handler();
        int delay = 15;
        final int[] progress = {from};

        handler.postDelayed(new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                if (progress[0] < to) {
                    progress[0]++;
                    completionPercentage.setText(progress[0] + "%");
                    handler.postDelayed(this, delay);
                } else if (progress[0] > to) {
                    progress[0]--;
                    completionPercentage.setText(progress[0] + "%");
                    handler.postDelayed(this, delay);
                }
            }
        }, delay);
    }

}
