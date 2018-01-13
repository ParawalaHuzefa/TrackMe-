package models;

import com.google.firebase.database.IgnoreExtraProperties;

/*
 * Model  User:
 * It contains User Id(uid), Username (username), Email (email)
 * Relation Id (relation_id)
 */

@IgnoreExtraProperties
public class User {

    public String userid;
    public String username;
    public String email;
    public double lat;
    public double lon;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }


    /*
     * Constructure to Intialize Model
     */
    public User(String userid, String username, String email, double lat, double lon) {
        this.userid=userid;
        this.username = username;
        this.email = email;
        this.lat=lat;
        this.lon=lon;
    }

}