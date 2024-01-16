package com.example.assignment3;

/**
 * Validator Class
 *
 * This class is used to test single methods for the MapsActivity Functionality
 */
public class Validator {
    /**
     * getWeather URL Method
     * @param lati - Latitude of location
     * @param longi - Longitude of location
     * @return url - Full url
     *
     * This method is used to get the full weather api URL
     */
    public String getWeatherURL(double lati, double longi) {
        String url = "https://api.openweathermap.org/data/2.5/weather?lat="+lati+"&lon="+longi+"&appid=e70db8795aa5707f469928c01a59dc01";
        return url;
    }

    /**
     * getCamURL Method
     * @param lati - Latitude of location
     * @param longi - Longitude of location
     * @return url - Full url
     *
     * This method is used to get the cam api URL
     */
    public String getCamURL(double lati, double longi) {
        String url = "https://api.windy.com/api/webcams/v2/list/nearby="+lati+","+longi+",500/limit=5?show=webcams:location,image&key=PrcUozvG54yPWuuX6UyaOi0vJQeUHb7e";
        return url;
    }

}
