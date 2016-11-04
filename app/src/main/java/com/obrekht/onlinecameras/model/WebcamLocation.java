
package com.obrekht.onlinecameras.model;

import java.io.Serializable;

public class WebcamLocation implements Serializable {

    private String city;
    private String region;
    private String regionCode;
    private String country;
    private String countryCode;
    private String continent;
    private String continentCode;
    private Double latitude;
    private Double longitude;
    private String timezone;

    public String getCity() {
        return city;
    }

    public String getRegion() {
        return region;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public String getCountry() {
        return country;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getContinent() {
        return continent;
    }

    public String getContinentCode() {
        return continentCode;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getTimezone() {
        return timezone;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || !(obj instanceof WebcamLocation)) {
            return false;
        }

        WebcamLocation other = (WebcamLocation) obj;

        return regionCode.equals(other.regionCode) && country.equals(other.country) &&
                city.equals(other.city) && region.equals(other.region) &&
                countryCode.equals(other.countryCode) && continent.equals(other.continent) &&
                continentCode.equals(other.continentCode) && latitude.equals(other.latitude) &&
                longitude.equals(other.longitude);
    }
}
