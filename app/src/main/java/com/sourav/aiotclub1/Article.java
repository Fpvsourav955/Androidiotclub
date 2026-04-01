package com.sourav.aiotclub1;

public class Article {
    public String title;
    public String articleContent;
    public String articleContent1;
    public String articleContent2;
    public String articleContent3;
    public String date;
    public String description;
    public String imageUrl;     // Required even if not used — prevents error
    public String imageUrl1;
    public String authorname;   // ✅ Must match Firebase field exactly

    // Required empty constructor for Firebase
    public Article() {
    }
}
