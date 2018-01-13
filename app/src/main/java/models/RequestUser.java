package models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import java.util.HashMap;
import java.util.Map;

/*
 * Model Request of User:
 * It contains User Id(uid), Partner User Id (partner_uid), Indicator (indicator)(Not used in cuurent usecase)
 * Relation Id (relation_id)
 */
@IgnoreExtraProperties
public class RequestUser {

    public String uid;
    public String partner_uid;
    public int indicator;
    public String relation_id;
    /*
    indicator = 0, not to show location
    indicator = 1, show original location
    indicator = 2, show fake location
    */

    public RequestUser() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    /*
     * Constructure to Intialize Model
     */
    public RequestUser(String relationId,String uid, String partner_uid, int indicator) {
        this.relation_id= relationId;
        this.uid = uid;
        this.partner_uid = partner_uid;
        this.indicator = indicator;
    }

    /*
     * Method to Intialize model
     */
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("relation_id", relation_id);
        result.put("uid", uid);
        result.put("partner_uid", partner_uid);
        result.put("indicator", indicator);
        return result;
    }

}