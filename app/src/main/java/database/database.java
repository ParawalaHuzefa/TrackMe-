package database;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import fragment.HomeFragment;
import models.RelationUser;
import models.RequestUser;
import models.User;

public class database {

    private static HashSet<User> allUsers = new HashSet<User>();
    private static HashSet<User> nonPartnerUsers;
    private static HashSet<String> partnerUserIds = new HashSet<String>();
    private static HashSet<String> requestUserIds = new HashSet<String>();
    private static HashSet<String> APrequestUserIds = new HashSet<String>();
    private static HashSet<RelationUser> allAPRequests = new HashSet<RelationUser>();
    private static HashSet<RelationUser> allRelations = new HashSet<RelationUser>();
    private static HashMap<String, Marker> markerHashMap = new HashMap<>();
    private static ArrayList<User> dataModelsNonPartnerUsers = new ArrayList<>();
    private static ArrayList<User> dataModelPartnerUsers = new ArrayList<>();
    private static ArrayList<User> dataModelRequestUsers = new ArrayList<>();
    private static ArrayList<User> dataModelAPRequestUsers = new ArrayList<>();
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private static FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private static User myUser = new User();
    private static HashSet<RequestUser> allRequests = new HashSet<RequestUser>();
    private static HashSet<User> requestsToMe = new HashSet<User>();
    private static final String TAG = "````````Database`````";

    public static ArrayList<User> getDataModelRequestUsers() {
        return dataModelRequestUsers;
    }

    public static ArrayList<User> getDataModelAPRequestUsers() {
        return dataModelAPRequestUsers;
    }

    public static void setDataModelRequestUsers(ArrayList<User> dataModelRequestUsers) {
        database.dataModelRequestUsers = dataModelRequestUsers;
    }

    public static HashSet<User> getRequestsToMe() {
        return requestsToMe;
    }

    public static void setRequestsToMe(HashSet<User> requestsToMe) {
        database.requestsToMe = requestsToMe;
    }

    public FirebaseUser getUser() {
        return user;
    }


    /*
     * Method to bind 'users' node from the firebase.
     * Store all user to different arrays according to the status of their relation with the logged in user.
     * Call to update marker method using HomeFragment instance
     */
    public static void getListUsers() {

        mDatabase.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                requestsToMe.clear();
                allUsers.clear();
                dataModelPartnerUsers.clear();
                dataModelsNonPartnerUsers.clear();
                dataModelRequestUsers.clear();
                dataModelAPRequestUsers.clear();

                for (DataSnapshot userDataSnapshot : dataSnapshot.getChildren()) {

                    User user = userDataSnapshot.getValue(User.class);

                    if(requestUserIds.contains(user.userid)){
                        requestsToMe.add(user);
                        dataModelRequestUsers.add(user);
                    }

                    if(APrequestUserIds.contains(user.userid)){
                        requestsToMe.add(user);
                        dataModelAPRequestUsers.add(user);
                    }

                    if (partnerUserIds.contains(user.userid)) {
                        allUsers.add(user);
                        dataModelPartnerUsers.add(user);

                    } else {
                        if (!user.userid.equals(mAuth.getCurrentUser().getUid())) {
                            dataModelsNonPartnerUsers.add(user);
                        } else {
                            myUser = user;
                        }
                    }
                }
                HomeFragment hm = new HomeFragment();
                hm.updateMarkersMap();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "Database error " + databaseError);
            }

        });

    }

    /*
     * Method to bind 'relations' node from the firebase.
     * Store all relations data to different arrays according to the logged in user.
     * Call to showRequestToMe and getListUser on succesfull bind
     */
    public static void showRelationToMe() {

        mDatabase.child("relations").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                allRelations.clear();
                partnerUserIds.clear();
                APrequestUserIds.clear();

                for (DataSnapshot userDataSnapshot : dataSnapshot.getChildren()) {
                    RelationUser relation = userDataSnapshot.getValue(RelationUser.class);

                    if(relation.partner_uid.equals(getUid())){
                        allAPRequests.add(relation);
                        APrequestUserIds.add(relation.uid);
                    }
                    if(relation.uid.equals(getUid())){
                        partnerUserIds.add(relation.partner_uid);
                        allRelations.add(relation);
                    }

                }
                showRequestToMe();
                getListUsers();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "Database error " + databaseError);

            }
        });
    }

    /*
     * Method to bind 'requests' node from the firebase.
     * Store all requests data to different arrays according to the logged in user.
     * Call to getListUsers on succesfull bind
     */
    public static void showRequestToMe() {

        mDatabase.child("requests").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                allRequests.clear();
                requestUserIds.clear();

                for (DataSnapshot userDataSnapshot : dataSnapshot.getChildren()) {
                    RequestUser request = userDataSnapshot.getValue(RequestUser.class);
                    if(request.partner_uid.equals(user.getUid())){
                        allRequests.add(request);
                        requestUserIds.add(request.uid);
                    }
                }
                getListUsers();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "Database error " + databaseError);

            }
        });
    }



    /*
     * Method to get current logged in user object
     */
    public static User getMyUser() {
        return myUser;
    }


    /*
     * Method to insert new child in the user-relations and relations nodes
     */
    public void writeNewRelation(String userId,String partner_userid) {

        int indication = 1;
        String key = mDatabase.child("relations").push().getKey();

        RelationUser relationUser = new RelationUser(key, userId, partner_userid, indication);
        Map<String, Object> relationValues = relationUser.toMap();
        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put("/relations/" + key, relationValues);
        childUpdates.put("/user-relations/" + userId + "/" + key, relationValues);
        mDatabase.updateChildren(childUpdates);
        showRelationToMe();
    }

    /*
     * Method to insert new child in the requests node
     */
    public void writeNewRequest(String partner_userid) {
        int indication = 1;
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String key = mDatabase.child("requests").push().getKey();
        RelationUser relationUser = new RelationUser(key, userId, partner_userid, indication);
        Map<String, Object> relationValues = relationUser.toMap();
        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put("/requests/" + key, relationValues);
        mDatabase.updateChildren(childUpdates);
    }


    /*
     * Method to remove the child from the request and call writeNewRelation method
     */
    public void approveRequest(User partnerUser) {
        RequestUser relation = null;

        Iterator<RequestUser> itr = allRequests.iterator();
        while (itr.hasNext()) {
            RequestUser temp = itr.next();
            if (temp.uid.equals(partnerUser.userid)) {
                relation = temp;
                break;
            }
        }

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/requests/" + relation.relation_id, null);
        mDatabase.updateChildren(childUpdates);
        writeNewRelation(partnerUser.userid,getUid());
    }

    /*
     * Method to remove the child from the user-relations and relations node
     * Process the relation if the partner user id matches with the user object given in the argument
     */
    public void removeAPRequest(User partnerUser) {
        RelationUser relation = null;

        Iterator<RelationUser> itr = allAPRequests.iterator();
        while (itr.hasNext()) {
            RelationUser temp = itr.next();

            if (temp.uid.equals(partnerUser.userid) && temp.partner_uid.equals(getUid())) {
                relation = temp;
                break;
            }
        }

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/relations/" + relation.relation_id, null);
        childUpdates.put("/user-relations/" + partnerUser.userid + "/" + relation.relation_id, null);
        mDatabase.updateChildren(childUpdates);
    }

    /*
     * Method to remove the child from the user-relations and relations node
     * Process the relation if the user id matches with the user object given in the argument
     */
    public void removePartner(User partnerUser) {
        RelationUser relation = null;
        Iterator<RelationUser> itr = allRelations.iterator();
        while (itr.hasNext()) {
            RelationUser temp = itr.next();

            if (temp.partner_uid.equals(partnerUser.userid)) {
                relation = temp;
                break;
            }
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/relations/" + relation.relation_id, null);
        childUpdates.put("/user-relations/" + userId + "/" + relation.relation_id, null);
        mDatabase.updateChildren(childUpdates);
    }

    /*
     * Update the latitude and longitude of the given user to the firebase by calling writeNewUser method
     */
    public void onAuthSuccess(double lat, double lon) {

        FirebaseUser user = mAuth.getCurrentUser();
        String username = usernameFromEmail(user.getEmail());
        writeNewUser(user.getUid(), username, user.getEmail(), lat, lon);
    }

    /*
     *  Methd to generate the username from the email
     */
    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }

    /*
     * Method to update the specific user's childe instance with updated object
     */
    private void writeNewUser(String userId, String name, String email, double lat, double lon) {
        User user = new User(userId, name, email, lat, lon);
        mDatabase.child("users").child(userId).setValue(user);
    }


    /*
     * Method to destroy firebase's instance by calling signOut method
     */
    public static void signOut() {
        mAuth.signOut();
    }


    /*
     * Method to call update password method of firebase.
     * In order to do so, re authroise the user and then call the method
     */
    public void updatePassword(String password, final String newPassword, final Context c) {
        if (user != null) {
            Log.d(TAG, "email  " + user.getEmail() + " pwd " + newPassword);
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail().toString(), password);
            user.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                user.updatePassword(newPassword.trim())
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(c, "Password is updated, sign in with new password!", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(c, "Failed to update password!", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            } else {
                                Log.d(TAG, "Error auth failed");
                            }
                        }
                    });
        } else {
            Log.d(TAG, "Firebase user is null");
        }

    }

    /*
     * Get logged in user's user id
     */
    public static String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public database() {

    }

    public static HashMap<String, Marker> getMarkerHashMap() {
        return markerHashMap;
    }

    public static ArrayList<User> getDataModelsNonPartnerUsers() {
        return dataModelsNonPartnerUsers;
    }

    public static ArrayList<User> getDataModelPartnerUsers() {
        return dataModelPartnerUsers;
    }

    public static FirebaseAuth getmAuth() {
        return mAuth;
    }

}
