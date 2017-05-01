package project.doublepark.doublepark;

import java.util.ArrayList;

/**
 * Created by Shiangyoung on 3/28/2017.
 */

public class UserInformation {

    public String carPlate;
    public String name;
    public ArrayList<String> contactNo;
    public String email;
    public String token;
    public String photo;
    public UserInformation(){

    }
    public UserInformation(String carPlate, String name, ArrayList<String> contactNo,String email) {
        this.carPlate = carPlate;
        this.name = name;
        this.contactNo = contactNo;
        this.email = email;
        this.token = null;
    }
    public UserInformation(String carPlate, String name, ArrayList<String> contactNo,String email,String token) {
        this.carPlate = carPlate;
        this.name = name;
        this.contactNo = contactNo;
        this.email = email;
        this.token = token;
    }





}
