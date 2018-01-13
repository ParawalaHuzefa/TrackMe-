package activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import models.User;


public class BaseActivity extends AppCompatActivity {

    private ProgressDialog mProgressDialog;

    /*
     * To show progress Dialog before starting some process which might take some time
     */
    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Loading...");
        }

        mProgressDialog.show();
    }

    /*
     * To hide progress Dialog after the process which might take some time is completed
     */
    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    /*
     * when the username and password is authenticated go to main Activity
     */
    protected void onAuthSuccess(FirebaseUser user, DatabaseReference mDatabase,double lat, double lon) {
        String username = usernameFromEmail(user.getEmail());

        // Write new user
        writeNewUser(user.getUid(), username, user.getEmail(),mDatabase,lat,lon);
        startMainActivity();

    }
    /*
     * To start the intent which is the main activity
     */
    protected void startMainActivity(){
        // Go to MainActivity
        Intent intent = new Intent(BaseActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /*
     * To parse username from your email
     */
    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }


    /*
     * To register a new user from the register form
     */
    private void writeNewUser(String userId, String name, String email, DatabaseReference mDatabase,double lat, double lon) {

        User user = new User(userId,name, email,lat,lon);
        mDatabase.child("users").child(userId).setValue(user);
    }

    /*
     * To reset the password
     */
    protected void resetPassword() {
        startActivity(new Intent(BaseActivity.this, ResetPasswordActivity.class));

    }

    /*
     * When sign in is touched on the main screen
     */
    protected void signInAcitivity() {
        startActivity(new Intent(BaseActivity.this, SignInActivity.class));
        finish();
    }
    /*
     * When sign up is touched on the main screen
     */
    protected void signUpActivity() {
        startActivity(new Intent(BaseActivity.this, SignupActivity.class));
        finish();
    }

}