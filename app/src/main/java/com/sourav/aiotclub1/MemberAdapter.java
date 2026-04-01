package com.sourav.aiotclub1;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MemberViewHolder> {

    private final Context context;
    private final List<Member> memberList;


    public MemberAdapter(Context context, List<Member> memberList) {
        this.context = context;
        this.memberList = memberList;
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.members_item, parent, false);
        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        Member member = memberList.get(position);

        holder.name.setText(member.getName() != null ? member.getName() : "Unnamed");
        holder.role.setText(member.getRole() != null ? member.getRole() : "Role not specified");


        if (member.getImageUrl() != null && !member.getImageUrl().isEmpty()) {
            Glide.with(context).load(member.getImageUrl()).into(holder.profileImage);
        } else {
            holder.profileImage.setImageResource(R.drawable.icons8testaccount96);
        }

        holder.linkedin.setOnClickListener(v -> openUrl(member.getLinkedin()));
        holder.instagram.setOnClickListener(v -> openUrl(member.getInstagram()));
        holder.github.setOnClickListener(v -> openUrl(member.getGithub()));
        holder.email.setOnClickListener(v -> openEmail(member.getEmail()));

        holder.itemView.setOnClickListener(v -> {
            try {
                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.member_details_dialog);
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                ImageView profileImage = dialog.findViewById(R.id.dialog_profile_image);
                TextView name = dialog.findViewById(R.id.dialog_name);
                TextView role = dialog.findViewById(R.id.dialog_role);
                TextView desc = dialog.findViewById(R.id.dialog_description);

                ImageView linkedin = dialog.findViewById(R.id.linkedin_icon);
                ImageView instagram = dialog.findViewById(R.id.instagram_icon);
                ImageView gmail = dialog.findViewById(R.id.gmail_icon);
                ImageView github = dialog.findViewById(R.id.github_icon);

                name.setText(member.getName());
                role.setText(member.getRole());

                desc.setText(member.getDescription() != null ? member.getDescription().replace("\\n", "\n") : "No Description");

                if (member.getImageUrl() != null && !member.getImageUrl().isEmpty()) {
                    Glide.with(context).load(member.getImageUrl()).into(profileImage);
                } else {
                    profileImage.setImageResource(R.drawable.icons8testaccount96);
                }

                linkedin.setOnClickListener(view -> openUrl(member.getLinkedin()));
                instagram.setOnClickListener(view -> openUrl(member.getInstagram()));
                gmail.setOnClickListener(view -> openEmail(member.getEmail()));
                github.setOnClickListener(view -> openUrl(member.getGithub()));

                dialog.show();

                Window window = dialog.getWindow();
                if (window != null) {
                    window.setLayout(
                            (int)(context.getResources().getDisplayMetrics().widthPixels * 0.9),
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );
                }
            } catch (Exception e) {
                Toast.makeText(context, "Error loading member details", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }

    static class MemberViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView profileImage;
        TextView name, role;
        ImageView linkedin, instagram, github, email;

        public MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.shapeableImageView);
            name = itemView.findViewById(R.id.textView7);
            role = itemView.findViewById(R.id.textView9);
            linkedin = itemView.findViewById(R.id.souravlink);
            instagram = itemView.findViewById(R.id.souravinsta);
            github = itemView.findViewById(R.id.souravgit);
            email = itemView.findViewById(R.id.gmail_icon);
        }
    }

    private void openUrl(String url) {
        if (url != null && !url.trim().isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            context.startActivity(intent);
        } else {
            Toast.makeText(context, "Link not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void openEmail(String email) {
        if (email != null && !email.trim().isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + email));
            context.startActivity(Intent.createChooser(intent, "Send Email"));
        } else {
            Toast.makeText(context, "Email not available", Toast.LENGTH_SHORT).show();
        }
    }
}
