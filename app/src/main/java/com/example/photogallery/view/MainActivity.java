package com.example.photogallery.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.photogallery.R;
import com.example.photogallery.presenter.MainActivityPresenter;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements MainActivityPresenter.View {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int SEARCH_ACTIVITY_REQUEST_CODE = 2;
    String mCurrentPhotoPath;
    private ArrayList<String> photos = null;
    private int index = 0;

    private MainActivityPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPresenter = new MainActivityPresenter(this);

        photos = mPresenter.findPhotos(new Date(Long.MIN_VALUE), new Date(), null, 0, "");

        checkPermissions();

        if (photos.size() == 0) {
            displayPhoto(null);
        } else {
            displayPhoto(photos.get(index));
        }
    }

    private void checkPermissions(){
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
                photos = mPresenter.findPhotos(startTimestamp, endTimestamp, coordinates, radius, keywords);
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
            photos = mPresenter.findPhotos(new Date(Long.MIN_VALUE), new Date(), null, 0, "");
        }
    }

    private void addLocationTagging(String path) {
        mPresenter.extractLocationCoordinates(path);
    }


    @Override
    public void updatePhoto(String path, String caption) {
        mPresenter.updatePhoto(path,caption,photos,index);
    }

    @Override
    public void showLatitudeAndLongitude(Double latitude, Double longitude) {
        TextView latitudeField = (TextView) findViewById(R.id.tvLatitude);
        TextView longitudeField = (TextView) findViewById(R.id.tvLongitude);
        latitudeField.setText(String.format(Locale.CANADA,"Latitude: %.6f",latitude));
        longitudeField.setText(String.format(Locale.CANADA,"Longitude: %.6f",longitude));
    }
}