package com.gbegbe.myapplication;

public class Information {
    String Remarks;
    String macAddress;
    String Latitude;
    String longitude;
    String LocationName;

    public Information(String remarks, String macAddress, String latitude, String longitude, String locationName) {
        Remarks = remarks;
        this.macAddress = macAddress;
        Latitude = latitude;
        this.longitude = longitude;
        LocationName = locationName;
    }

    public Information() {
    }

    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String remarks) {
        Remarks = remarks;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLocationName() {
        return LocationName;
    }

    public void setLocationName(String locationName) {
        LocationName = locationName;
    }
}
