package com.sourav.aiotclub1;


public class Member {
    private String name;
    private String role;
    private String position;
    private String description;
    private String imageUrl;
    private String linkedin;
    private String instagram;
    private String github;
    private String email;

    public Member() {}

    public Member(String name, String role, String position, String description, String imageUrl,
                  String linkedin, String instagram, String github, String email) {
        this.name = name;
        this.role = role;
        this.position = position;
        this.description = description;
        this.imageUrl = imageUrl;
        this.linkedin = linkedin;
        this.instagram = instagram;
        this.github = github;
        this.email = email;
    }

    public String getName() { return name; }
    public String getRole() { return role; }
    public String getPosition() { return position; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public String getLinkedin() { return linkedin; }
    public String getInstagram() { return instagram; }
    public String getGithub() { return github; }
    public String getEmail() { return email; }

    public void setName(String name) { this.name = name; }
    public void setRole(String role) { this.role = role; }
    public void setPosition(String position) { this.position = position; }
    public void setDescription(String description) { this.description = description; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setLinkedin(String linkedin) { this.linkedin = linkedin; }
    public void setInstagram(String instagram) { this.instagram = instagram; }
    public void setGithub(String github) { this.github = github; }
    public void setEmail(String email) { this.email = email; }
}
