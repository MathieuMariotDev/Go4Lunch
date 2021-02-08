package com.example.go4lunch.Utils;


import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.SphericalUtil;

public class LocationToBoundsUtils {

    /**
     * Define a perimeter for the autocomplete prediction request
     *
     * @param center         : object : LatLng center of the perimeter
     * @param radiusInMeters : double : radius in meters of the perimeter
     * @return : object : LatLngBounds : return the new position
     */
    public static LatLngBounds toBounds(LatLng center, double radiusInMeters) {
        double distanceFromCenterToCorner = radiusInMeters * Math.sqrt(2.0);
        LatLng southwestCorner =
                SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 225.0);
        LatLng northeastCorner =
                SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 45.0);
        return new LatLngBounds(southwestCorner, northeastCorner);
    }


}
