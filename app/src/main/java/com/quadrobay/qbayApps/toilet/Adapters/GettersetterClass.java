package com.quadrobay.qbayApps.toilet.Adapters;

import org.json.JSONArray;

/**
 * Created by Benchmark on 2/28/2018.
 */

public class GettersetterClass {
    private String toiletID;
    private String latitude;
    private String longtitude;
    private String gender;
    private String hygenic;
    private String place;
    private String rating;
    private String disabledaccess;
    private String cost;
    private String address;
    private String first_Image;
    private String second_Image;
    private String third_Image;
    private JSONArray comment_Array;

    public String getToiletID ()
    {
        return toiletID;
    }

    public void setToiletID (String toiletID)
    {
        this.toiletID = toiletID;
    }
    public String getLatitude ()
    {
        return latitude;
    }

    public void setLatitude (String latitude)
    {
        this.latitude = latitude;
    }
    public String getLongtitude()
    {
        return longtitude;
    }

    public void setLongtitude(String longtitude)
    {
        this.longtitude = longtitude;
    }
    public String getGender ()
    {
        return gender;
    }

    public void setGender (String gender)
    {
        this.gender = gender;
    }
    public String getHygenic ()
    {
        return hygenic;
    }

    public void setHygenic (String hygenic)
    {
        this.hygenic = hygenic;
    }
    public String getPlace ()
    {
        return place;
    }

    public void setPlace (String place)
    {
        this.place = place;
    }
    public String getRating()
    {
        return rating;
    }

    public void setRating (String rating)
    {
        this.rating = rating;
    }
    public String getDisabledaccess()
    {
        return disabledaccess;
    }

    public void setDisabledaccess (String disabledaccess)
    {
        this.disabledaccess = disabledaccess;
    }
    public String getCost()
    {
        return cost;
    }

    public void setCost (String cost)
    {
        this.cost = cost;
    }
    public String getAddress()
    {
        return address;
    }

    public void setAddress (String address)
    {
        this.address = address;
    }
    public String getFirst_Image()
    {
        return first_Image;
    }

    public void setFirst_Image (String first_Image)
    {
        this.first_Image = first_Image;
    }
    public String getSecond_Image()
    {
        return second_Image;
    }

    public void setSecond_Image (String second_Image)
    {
        this.second_Image = second_Image;
    }
    public String getThird_Image()
    {
        return third_Image;
    }

    public void setThird_Image (String third_Image)
    {
        this.third_Image = third_Image;
    }
    public JSONArray getComment_Array()
    {
        return comment_Array;
    }

    public void setComment_Array (JSONArray comment_Array)
    {
        this.comment_Array = comment_Array;
    }



}
