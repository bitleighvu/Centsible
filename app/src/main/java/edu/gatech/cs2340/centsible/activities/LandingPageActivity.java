package edu.gatech.cs2340.centsible.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import edu.gatech.cs2340.centsible.R;
import edu.gatech.cs2340.centsible.model.User;
import edu.gatech.cs2340.centsible.model.UserFacade;

import android.view.View;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;

import edu.gatech.cs2340.centsible.model.LocationManager;
import java.util.Arrays;
import java.util.Objects;

/**
 * landing pge activity
 */
public class LandingPageActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;
    private View rootView;

    /**
     * create intent of context of landing page activity
     *
     * @param context of nonnull context of landing page
     * @return intent of landing page
     */
    public static Intent createIntent(@NonNull Context context) {
        return new Intent(context, LandingPageActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        rootView = findViewById(R.id.root);
        LocationManager lm = LocationManager.getInstance();
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(Arrays.asList(
                                new AuthUI.IdpConfig.GoogleBuilder().build(),
                                new AuthUI.IdpConfig.EmailBuilder().build(),
                                new AuthUI.IdpConfig.PhoneBuilder().build(),
                                new AuthUI.IdpConfig.AnonymousBuilder().build()))
			            .setLogo(R.mipmap.login_logo)
                        .setTheme(R.style.LoginTheme)
                        .build(),
                RC_SIGN_IN);
    }

    /**
     * create intent of landing page
     *
     * @param requestCode code to see if want to sign-in or not
     * @param resultCode code to see if credentials are good to sign-in
     * @param data to check to go to resultcode
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == RESULT_OK) {
                UserFacade tempInstance = UserFacade.getInstance();
                FirebaseAuth firebaseInstance = FirebaseAuth.getInstance();

                tempInstance.setUser(firebaseInstance.getCurrentUser());
                startActivity(StorageActivity.createIntent(this, response));
                finish();
            } else {

                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    Snackbar.make(rootView, R.string.sign_in_cancelled,
                            Snackbar.LENGTH_LONG).show();
                    return;
                }

                if (Objects.requireNonNull(response.getError()).getErrorCode()
                        == ErrorCodes.NO_NETWORK) {
                    Snackbar.make(rootView, R.string.no_internet_connection,
                            Snackbar.LENGTH_LONG).show();
                    return;
                }
                Snackbar.make(rootView, R.string.unknown_error,
                        Snackbar.LENGTH_LONG).show();
            }
        }
    }


}
