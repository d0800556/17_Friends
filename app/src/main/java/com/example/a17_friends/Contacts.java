package com.example.a17_friends;

public class Contacts
{
    public String name, status , image;
    public  long point;
    public  int pointt;

    public Contacts()
    {

    }

    public Contacts(String name, String status, String image) {
        this.name = name;
        this.status = status;
        this.image = image;
        this.point= point;
        this.pointt= pointt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getPoint() {
        return point;
    }

    public void setPoint(long point) { this.point = point; }

    public int getPointt() {
        return pointt;
    }

    public void setPointt(int pointt) { this.pointt = pointt; }
}
