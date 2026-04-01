package com.sourav.aiotclub1;


public class Feedback {
    public String name;
    public String rollNumber;
    public int overallRating;
    public String contentQuality;
    public String instructorRating;
    public String venueFacilities;
    public String mostValuableDay;
    public String recommend;
    public String suggestions;


    public Feedback() {}

    public Feedback(String name, String rollNumber, int overallRating, String contentQuality,
                    String instructorRating, String venueFacilities, String mostValuableDay,
                    String recommend, String suggestions) {
        this.name = name;
        this.rollNumber = rollNumber;
        this.overallRating = overallRating;
        this.contentQuality = contentQuality;
        this.instructorRating = instructorRating;
        this.venueFacilities = venueFacilities;
        this.mostValuableDay = mostValuableDay;
        this.recommend = recommend;
        this.suggestions = suggestions;
    }
}
