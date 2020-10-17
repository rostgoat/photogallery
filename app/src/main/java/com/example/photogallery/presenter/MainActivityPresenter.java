package com.example.photogallery.presenter;

import android.media.ExifInterface;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class MainActivityPresenter {

private final View view;

    public MainActivityPresenter(View view) {
        this.view = view;
    }

    public void updatePhoto(String path, String caption, ArrayList<String> photos, int index) {
        String[] attr = path.split("_");
        if (attr.length >= 3) {
            File to = new File(attr[0] + "_" + caption + "_" + attr[2] + "_" + attr[3]);
            File from = new File(path);
            from.renameTo(to);
            photos.set(index, to.toString());
        }
    }

    public void extractLocationCoordinates(String path){
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
        if (lat != null && latRef != null && lng != null && lngRef != null) {
            Double latitude = getParsedCoordinates(lat, latRef.equals("S"));
            Double longitude = getParsedCoordinates(lng, lngRef.equals("W"));
            view.showLatitudeAndLongitude(latitude,longitude);
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

    public ArrayList<String> findPhotos(Date startTimestamp, Date endTimestamp, Double[] coordinates, int radius, String keywords) {
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

    public interface View {
        void updatePhoto(String path, String caption);
        void showLatitudeAndLongitude(Double latitude, Double longitude);
    }
}


