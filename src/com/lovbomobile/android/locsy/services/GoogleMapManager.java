/**
 Copyright (c) 2013 Sven Schindler

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.
 */

package com.lovbomobile.android.locsy.services;

import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.*;
import com.lovbomobile.android.locsy.R;
import com.lovbomobile.android.locsy.entities.ParceableLocation;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class GoogleMapManager implements MapManager {

    private static final int MIN_ACCURACY_CIRCLE_RADIUS = 1; // in meters
    private final Resources resources;
    GoogleMap map;
    private Marker myPositionMarker;
    private Circle myAccuracyCircle;
    private Map<String, Marker> friendPositionMarkers;
    private Map<String, Circle> friendAccuracyCircles;

    public GoogleMapManager(GoogleMap map, Resources resources
    ) {
        initializeCircleAndMarkerCaches();
        this.resources = resources;
        this.map = map;
    }

    private void initializeCircleAndMarkerCaches() {
        friendPositionMarkers = new HashMap<String, Marker>();
        friendAccuracyCircles = new HashMap<String, Circle>();

    }

    @Override
    public void updateSingleFriendsLocationOnMap(String friendsName, ParceableLocation friendsLocation) {
        Calendar calendar = getCalendarWithDateSetTo(friendsLocation.getClientTime());

                if (map != null && friendsLocation.getLongitude() != 0 && friendsLocation.getLatitude() != 0) {
            removeMarkerAndCircleOfFriend(friendsName);
            addMarkerAndAccuracyCircleForFriend(friendsName, friendsLocation, calendar);

        }
    }

    private Calendar getCalendarWithDateSetTo(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(timestamp));
        return calendar;
    }

    private void removeMarkerAndCircleOfFriend(String friendsName) {
        removeMarkerOfFriend(friendsName);
        removeCircleOfFriend(friendsName);
    }

    private void addMarkerAndAccuracyCircleForFriend(String friendsName, ParceableLocation friendsLocation, Calendar timeOfLocation) {
        LatLng latLng = new LatLng(friendsLocation.getLatitude(), friendsLocation.getLongitude());
        addMarkerForFriend(friendsName, timeOfLocation, latLng);
        addAccuracyCircleForFriendIfNecessary(friendsName, friendsLocation, latLng);
    }

    private void addMarkerForFriend(String friendsName, Calendar timeOfLocation, LatLng latLng) {
        String markerTitle = createMarkerLabelForFriend(friendsName, timeOfLocation);
        Marker marker = addMarkerToMap(markerTitle, latLng);
        friendPositionMarkers.put(friendsName, marker);
    }

    private Marker addMarkerToMap(String title, LatLng latLng) {
        Marker marker = map.addMarker(new MarkerOptions().position(latLng)
                .title(title)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin)));
        return marker;
    }

    private String createMarkerLabelForFriend(String friendsName, Calendar timeOfLocation) {
        Format format = new SimpleDateFormat(resources.getString(R.string.time_formatter));
        String timeString =  format.format(timeOfLocation.getTime());
        return String.format(resources.getString(R.string.was_here_at),friendsName,timeString);

    }

    private void addAccuracyCircleForFriendIfNecessary(String friendsName, ParceableLocation friendsLocation, LatLng latLng) {
        if (isAccuracyCircleNeeded(friendsLocation.getAccuracy())) {
            friendAccuracyCircles.put(friendsName, addAccuracyCircle(latLng, friendsLocation.getAccuracy()));
        }
    }

    private Circle addAccuracyCircle(LatLng latLng, float accuracy) {
        // add accuracy circle
        CircleOptions circleOptions = new CircleOptions().fillColor(Color.parseColor("#150000FF")).center(latLng)
                .radius(accuracy).strokeColor(Color.parseColor("#400000FF")).strokeWidth(2.0f);

        return map.addCircle(circleOptions);
    }

    private void removeCircleOfFriend(String friendsName) {
        Circle accuracyCircle;
        if ((accuracyCircle = friendAccuracyCircles.get(friendsName)) != null) {
            accuracyCircle.remove();
        }
    }

    private void removeMarkerOfFriend(String friendsName) {
        Marker marker;
        if ((marker = friendPositionMarkers.get(friendsName)) != null) {
            marker.remove();
        }
    }

    private boolean isAccuracyCircleNeeded(float accuracy) {
        return accuracy > MIN_ACCURACY_CIRCLE_RADIUS;
    }

    @Override
    public void updateMyCurrentPositionOnMap(Location location) {
        if (map != null) {
            LatLng myPositionLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            updateMyMarker(myPositionLatLng);
            updateMyAccuracyCircle(location.getAccuracy(), myPositionLatLng);

        }
    }

    private void updateMyMarker(LatLng myPositionLatLng) {
        if (myPositionMarker != null) {
            myPositionMarker.remove();
        }
        myPositionMarker = map.addMarker(new MarkerOptions().position(myPositionLatLng).title(resources.getString(R.string.your_location))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin)));
    }

    private void updateMyAccuracyCircle(float accuracy, LatLng myPositionLatLng) {
        // add accuracy circle
        if (myAccuracyCircle != null) {
            myAccuracyCircle.remove();
        }

        if (isAccuracyCircleNeeded(accuracy)) {
            myAccuracyCircle = addAccuracyCircle(myPositionLatLng, accuracy);
        }
    }

    @Override
    public void removeLocationOfFriend(String friendsName) {
        removeMarkerOfFriend(friendsName);
        removeCircleOfFriend(friendsName);
    }

    @Override
    public void animateMapToFriendsLocation(String friendsName) {
        if (map != null) {
            Marker marker = friendPositionMarkers.get(friendsName);

            if (marker != null) {
                animateMapToMarkerAndShowInfoWindow(marker);
            }
        }
    }

    private void animateMapToMarkerAndShowInfoWindow(Marker marker) {
        if (marker != null) {
            map.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
            marker.showInfoWindow();
        }
    }

    private void animateMapToMarkerAndShowInfoWindow(Marker marker, float zoomLevel) {
        if (marker != null) {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), zoomLevel));
            marker.showInfoWindow();
        }


    }

    @Override
    public void animateMapToMyLocation() {
        Marker marker = myPositionMarker;
        animateMapToMarkerAndShowInfoWindow(marker);
    }

    @Override
    public void animateMapToMyLocationAndZoomIn() {
        Marker marker = myPositionMarker;
        animateMapToMarkerAndShowInfoWindow(marker, 12);
    }


}
