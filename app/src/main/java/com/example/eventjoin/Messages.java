package com.example.eventjoin;

public class Messages {
    private String message;
    private String senderId;
    private String id;
    private String time;



    private String type; //1  text  2 image 3 location
    private String imageUrl;
    private double lat;
    private double lon;
    private String locationInfo;

    public Messages(String message, String senderId,String id,String time,  String type, String imageUrl,
             double lat, double lon, String locationInfo) {
        this.message = message;
        this.senderId = senderId;
        this.id=id;
        this.time=time;
        this.type=type;
        this.imageUrl=imageUrl;
        this.lat=lat;
        this.lon=lon;
        this.locationInfo=locationInfo;
    }

    public Messages() {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getLocationInfo() {
        return locationInfo;
    }

    public void setLocationInfo(String locationInfo) {
        this.locationInfo = locationInfo;
    }
}
