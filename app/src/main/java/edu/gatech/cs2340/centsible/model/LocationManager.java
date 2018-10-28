package edu.gatech.cs2340.centsible.model;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class LocationManager {

    // singleton
    private static final LocationManager INSTANCE = new LocationManager();

    public LocationManager() {
        retrieveLocationsFromFirebase();
    }

    public static LocationManager getInstance() {
        return INSTANCE;
    }

    public Location getLocation(String key) {
        return locations.get(key);
    }

    public List<Location> getList() {
        List<Location> list = new ArrayList<Location>(locations.values());
        return list;
    }

    HashMap<String, Location> locations;


    public void retrieveLocationsFromFirebase() {
        locations = new HashMap<>();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReferenceFromUrl("gs://centsible-d48e9.appspot.com").child("locations/")
                .child("currentLocations");
        downloadFile(storageReference);

    }


    private File downloadFile(StorageReference storageReference) {
        try {
            final File localFile = File.createTempFile("text", "csv");
            storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    String filename = localFile.getName();
                    parseFile(localFile);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {

                }
            });

            return localFile;
        } catch (IOException e) {
            return null;
        }
    }

    private void parseFile(File inFile) {
        String line = "\n";
        String csvSplitBy = ",";
        BufferedReader br = null;
        String[] locations = new String[]{};
        StringBuilder outP = new StringBuilder();
        ArrayList<Location> locArr = new ArrayList<Location>();
        int counter = 0;
        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        try {
            /*br = new BufferedReader(new FileReader(downloadedFile));
            while ((line = br.readLine()) != null) {
                locations = line.split(csvSplitBy);
                String[] tester = new String[]{"1", "2", "3"};
                //counter++;
            }
            for (String word : locations) {
                outP.append(word);
            }

            fileContent.setText(Arrays.toString(locations));*/

            // Create an object of filereader
            // class with CSV file as a parameter.
            FileReader filereader = new FileReader(inFile);

            // create csvReader object passing
            // file reader as a parameter
            CSVReader csvReader = new CSVReader(filereader);
            String[] nextRecord;

            // we are going to read data line by line
            while ((nextRecord = csvReader.readNext()) != null) {
                Location tempLoc = new Location();
                for (int i = 0; i < 11; i++) {
                    outP.append(nextRecord[i]);
                    if (i == 0) {
                        tempLoc.setKey(nextRecord[i]);
                    }
                    if (i == 1) {
                        tempLoc.setName(nextRecord[i]);
                    }
                    if (i == 2) {
                        tempLoc.setLatitude(nextRecord[i]);
                    }
                    if (i == 3) {
                        tempLoc.setLongitude(nextRecord[i]);
                    }
                    if (i == 4) {
                        tempLoc.setStAddress(nextRecord[i]);
                    }
                    if (i == 5) {
                        tempLoc.setCity(nextRecord[i]);
                    }
                    if (i == 6) {
                        tempLoc.setState(nextRecord[i]);
                    }
                    if (i == 7) {
                        tempLoc.setZip(nextRecord[i]);
                    }
                    if (i == 8) {
                        tempLoc.setType(nextRecord[i]);
                    }
                    if (i == 9) {
                        tempLoc.setPhone(nextRecord[i]);
                    }
                    if (i == 10) {
                        tempLoc.setWebsite(nextRecord[i]);
                    }
                }
                locArr.add(tempLoc);

            }
            int locCount = 0;
            for (Location loc : locArr) {
                String tempStr = loc.getKey() + " | " + loc.getName() + " | " + loc.getLatitude()
                        + " | " + loc.getLongitude() + " | " + loc.getStAddress() + " | "
                        + loc.getCity() + " | " + loc.getState() + " | " + loc.getZip() + " | "
                        + loc.getType() + " | " + loc.getPhone() + " | " + loc.getWebsite() + "\n";

                locCount++;
                Log.d("TEST", "parseFile: " + tempStr);
                this.locations.put(loc.getKey(), loc);

            }

            //fileContent.setText(tempStr);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException l) {
            l.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}