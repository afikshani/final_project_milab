package com.example.afikshani.milab_final.helpers;

public class WarningDetails {

    private String address;
    private String details;

    public WarningDetails(){
        this.address = "";
        this.details = "";
    }

    public WarningDetails(String address, String details){
        this.address = address;
        this.details = details;
    }

    public String getAddress() {
        return address;
    }

    public String getDetails() {
        return details;
    }

    public String ToString(){
        return ("Location: "+address+" - "+"Warning: "+details);
    }

}
