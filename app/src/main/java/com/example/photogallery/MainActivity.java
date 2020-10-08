package com.example.photogallery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int SEARCH_ACTIVITY_REQUEST_CODE = 2;
    String mCurrentPhotoPath;
    private ArrayList<String> photos = null;
    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        photos = findPhotos(new Date(Long.MIN_VALUE), new Date(), null, 0, "");

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.CAMERA
            }, 1000);
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 1001);
        }

        if (photos.size() == 0) {
            displayPhoto(null);
        } else {
            displayPhoto(photos.get(index));
        }

    }


    public void sharingToSocialMedia(View v) {

        try {
            File file = new File(photos.get(index));
            Uri bmpUri = FileProvider.getUriForFile(this, "com.example.photogallery.fileprovider", file);
            //Todo: Commenting this out for now, uncomment it to show it the Caption as well, works on Twitter
//            EditText mEdit   = (EditText)findViewById(R.id.etCaption);
//            String text = mEdit.getText().toString();

            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
//            shareIntent.putExtra(Intent.EXTRA_TEXT, text);
            shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
            shareIntent.setType("image/*");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(shareIntent, "Share images using"));

        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getApplicationContext(), "Please install the application first", Toast.LENGTH_LONG).show();
        }

    }

    public void searchPhoto(View view) {
        Intent searchIntent = new Intent(this, SearchActivity.class);
        startActivityForResult(searchIntent, SEARCH_ACTIVITY_REQUEST_CODE);
    }

    public void takePhoto(View v) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there is a camera activity to handle the intent
        //Todo: Commenting this out for now to allow pictures to work. Not sure why it is always null
        //if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            // Error occurred while creating the File
        }
        // Continue only if the File was successfully created
        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(this, "com.example.photogallery.fileprovider", photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
        //}
    }

    private ArrayList<String> findPhotos(Date startTimestamp, Date endTimestamp, Double[] coordinates, int radius, String keywords) {
        File file = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath(), "/Android/data/com.example.photogallery/files/Pictures");
        ArrayList<String> photos = new ArrayList<String>();
        File[] fList = file.listFiles();
        if (fList != null) {
            for (File f : fList) {
                if (coordinates != null){
                    try {
                        ExifInterface exif = new ExifInterface(f.getAbsolutePath());
                        String lat = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
                        String latRef = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
                        String lng = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
                        String lngRef = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
                        if (lat == null || lng == null || latRef == null || lngRef == null){
                            continue;
                        }
                        Double[] fileCoordinates = {0.0, 0.0};
                        fileCoordinates[0] = getParsedCoordinates(lat, latRef.equals("S"));
                        fileCoordinates[1] = getParsedCoordinates(lng, lngRef.equals("W"));
                        System.out.println(distance(fileCoordinates, coordinates));
                        if (!(distance(fileCoordinates, coordinates) < radius * 1000)){
                            continue;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (((startTimestamp == null && endTimestamp == null) || (f.lastModified() >= startTimestamp.getTime()
                        && f.lastModified() <= endTimestamp.getTime())
                ) && (keywords.equals("") || f.getPath().contains(keywords)))
                    photos.add(f.getPath());
            }
        }
        return photos;
    }

    public void scrollPhotos(View v) {
        if (photos.size() != 0) {
            updatePhoto(photos.get(index), ((EditText) findViewById(R.id.etCaption)).getText().toString());
            switch (v.getId()) {
                case R.id.btnPrev:
                    if (index > 0) {
                        index--;
                    }
                    break;
                case R.id.btnNext:
                    if (index < (photos.size() - 1)) {
                        index++;
                    }
                    break;
                default:
                    break;
            }
            displayPhoto(photos.get(index));
        }
    }

    private void displayPhoto(String path) {
        ImageView iv = (ImageView) findViewById(R.id.ivGallery);
        TextView tv = (TextView) findViewById(R.id.tvTimestamp);
        EditText et = (EditText) findViewById(R.id.etCaption);
        TextView tvLat = (TextView) findViewById(R.id.tvLatitude);
        TextView tvLong = (TextView) findViewById(R.id.tvLongitude);
        if (path == null || path.equals("")) {
            iv.setImageResource(R.mipmap.ic_launcher);
            et.setText("");
            tv.setText("");
            tvLat.setText("");
            tvLong.setText("");
        } else {
            iv.setImageBitmap(BitmapFactory.decodeFile(path));
            String[] attr = path.split("_");
            et.setText(attr[1]);
            tv.setText(attr[2]);
            addLocationTagging(path);
        }
    }

    public File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SEARCH_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                DateFormat format = new SimpleDateFormat("yyyy‐MM‐dd HH:mm:ss");
                Date startTimestamp, endTimestamp;
                try {
                    String from = (String) data.getStringExtra("STARTTIMESTAMP");
                    String to = (String) data.getStringExtra("ENDTIMESTAMP");
                    startTimestamp = format.parse(from);
                    endTimestamp = format.parse(to);
                } catch (Exception ex) {
                    startTimestamp = null;
                    endTimestamp = null;
                }
                Double[] coordinates = null;
                int radius = 0;
                try {
                    double longitude = data.getDoubleExtra("LONGITUDE", 0.0);
                    double latitude = data.getDoubleExtra("LATITUDE", 0.0);
                    radius = Integer.parseInt(String.valueOf(data.getIntExtra("RADIUS", 0)));
                    if (longitude != 0.0 && latitude != 0.0 && radius != 0){
                        coordinates = new Double[]{latitude, longitude};
                    }
                } catch (Exception e){}
                String keywords = (String) data.getStringExtra("KEYWORDS");
                index = 0;
                photos = findPhotos(startTimestamp, endTimestamp, coordinates, radius, keywords);
                if (photos.size() == 0) {
                    displayPhoto(null);
                } else {
                    displayPhoto(photos.get(index));
                }
            }
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            ImageView mImageView = (ImageView) findViewById(R.id.ivGallery);
            mImageView.setImageBitmap(BitmapFactory.decodeFile(mCurrentPhotoPath));
            photos = findPhotos(new Date(Long.MIN_VALUE), new Date(), null, 0, "");
        }
    }

    private void updatePhoto(String path, String caption) {
        String[] attr = path.split("_");
        if (attr.length >= 3) {
            File to = new File(attr[0] + "_" + caption + "_" + attr[2] + "_" + attr[3]);
            File from = new File(path);
            from.renameTo(to);
            photos.set(index, to.toString());
        }
    }

    private void addLocationTagging(String path) {
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(path);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        String lat = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
        String latRef = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
        String lng = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
        String lngRef = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
        if (lat != null && latRef != null && lng != null && lngRef != null){
            Double latitude = getParsedCoordinates(lat, latRef.equals("S"));
            Double longitude = getParsedCoordinates(lng, lngRef.equals("W"));
            TextView latitudeField = (TextView) findViewById(R.id.tvLatitude);
            TextView longitudeField = (TextView) findViewById(R.id.tvLongitude);
            latitudeField.setText(String.format(Locale.CANADA,"Latitude: %.6f",latitude));
            longitudeField.setText(String.format(Locale.CANADA,"Longitude: %.6f",longitude));
        }
    }

    private static Double getParsedCoordinates(String raw, boolean neg){
        // raw = 49/1,2/1,29112/10000
        String[] fields = raw.split(",");
        // fields = [49/1, 2/1, 29112/10000]
        double accumulator = 0.0;
        String[] operands = fields[0].split("/");
        accumulator += (double) Integer.parseInt(operands[0]) / (double) Integer.parseInt(operands[1]);
        operands = fields[1].split("/");
        accumulator += (double) Integer.parseInt(operands[0]) / (double) Integer.parseInt(operands[1]) / 60;
        operands = fields[2].split("/");
        accumulator += (double) Integer.parseInt(operands[0]) / (double) Integer.parseInt(operands[1]) / 3600;
        System.out.println(neg ? accumulator * -1 : accumulator);
        return neg ? accumulator * -1 : accumulator;
    }

    // Return distance between 2 points in meters
    private static double distance(Double[] p1, Double[] p2){
        final int R = 6371; // Radius of Earth
        double latDistance = Math.toRadians(p2[0] - p1[0]);
        double lonDistance = Math.toRadians(p2[1] - p1[1]);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(Math.toRadians(p1[0])) * Math.cos(Math.toRadians(p2[0])) * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        distance = Math.pow(distance, 2);
        return Math.sqrt(distance);
    }
}