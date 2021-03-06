package edu.gatech.cs2340.centsible.model;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;


// POJO

/**
 * gets user entitlements
 */
public class User {
    private static final String TAG = "NEWCENTSIBLE";

    private final String displayName;
    private final String uid;
    // --Commented out by Inspection (11/10/18, 1:41 AM):private boolean isLocked;
    private List<UserEntitlements> entitlements = new ArrayList<>();

    /**
     * constructor of firebase user
     *
     * @param user user of firebase
     */
    public User(UserInfo user) {
        displayName = user.getDisplayName();
        uid = user.getUid();
        String email = user.getEmail();
        boolean emailVerified = user.isEmailVerified();
        retrieveEntitlementsFromFirestore();
    }

    /**
     * getter for display name of user
     *
     * @return display name of user
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * getter for id of user
     *
     * @return user id of user
     */
    public String getUid() {
        return uid;
    }

// --Commented out by Inspection START (11/10/18, 1:41 AM):
//    public String getEmail() {
//        return email;
//    }
// --Commented out by Inspection STOP (11/10/18, 1:41 AM)

    /**
     * getter for entitlements of user
     *
     * @return list of user entitlements
     */
    public Iterable<UserEntitlements> getEntitlements() {
        return Collections.unmodifiableList(entitlements);
    }

    /**
     * setter for user entitlements
     *
     * @param entitlements new user entitlements
     */
    public void setEntitlements(List<UserEntitlements> entitlements) {
        //noinspection AssignmentOrReturnOfFieldWithMutableType
        this.entitlements = entitlements;
    }

// --Commented out by Inspection START (11/10/18, 1:41 AM):
//    public boolean isEmailVerified() {
//        return emailVerified;
//    }
// --Commented out by Inspection STOP (11/10/18, 1:41 AM)


    private void retrieveEntitlementsFromFirestore() {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        final CollectionReference usersRef = database.collection("users");
        Query query = usersRef.whereEqualTo("uid", uid);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot s = task.getResult();
                    if (!(Objects.requireNonNull(task.getResult()).isEmpty())) {
                        if (task.getResult().size() > 1) {
                            Log.d(TAG, "There are multiple documents matching the uid" + uid);
                            return; // error handling
                        }
                        for (QueryDocumentSnapshot document: task.getResult()) {
                            @SuppressWarnings("unchecked")
                            Iterable<String> remoteEntitlements = (List<String>) document.getData()
                                    .get("entitlements");
                            for (String j: remoteEntitlements) {
                                entitlements.add(UserEntitlements.valueOf(j));
                            }
                        }
                    } else {
                        entitlements.add(UserEntitlements.USER);
                        Collection<String> list = new ArrayList<>();
                        list.add("USER");
                        Map<String, Object> user = new HashMap<>();
                        user.put("entitlements", list);
                        user.put("uid", uid);
                        usersRef.add(user)
                                .addOnFailureListener(
                                        new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d(TAG, "Failed to add an entitlement " +
                                                        "to Firestore");
                                            }
                                        }
                                );
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

    }




}