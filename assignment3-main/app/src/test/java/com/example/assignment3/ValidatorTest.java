package com.example.assignment3;

import static org.junit.Assert.*;

import android.os.Bundle;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ValidatorTest Class
 *
 * This class is used to unit test the MapsActivity Functionality
 */
public class ValidatorTest {

    //Validator Object
    Validator map;

    //Setting up before a test
    @Before
    public void setUp() throws Exception {
        map = new Validator();
    }

    //Tearing down after a test
    @After
    public void tearDown() throws Exception {
        map = null;
    }

    //Tests whether weather URL is correct
    @Test
    public void weatherURL_isCorrect() {
        double lati = 41.40338;
        double longi = 2.17403;
        String url = map.getWeatherURL(lati, longi);
        String actualURL = "https://api.openweathermap.org/data/2.5/weather?lat="+lati+"&lon="+longi+"&appid=e70db8795aa5707f469928c01a59dc01";

        assertEquals(url, actualURL);
    }

    //Tests whether web cam URL is correct
    @Test
    public void webcamURL_isCorrect() {
        double lati = 41.40338;
        double longi = 2.17403;
        String url = map.getCamURL(lati, longi);
        String actualURL = "https://api.windy.com/api/webcams/v2/list/nearby="+lati+","+longi+",500/limit=5?show=webcams:location,image&key=PrcUozvG54yPWuuX6UyaOi0vJQeUHb7e";

        assertEquals(url, actualURL);
    }

    //Tests whether the arraylist add method works
    @Test
    public void locationlistADD_isCorrect() {
        ArrayList<Double> locationList = new ArrayList<>();
        locationList.add(4.5);
        locationList.add(6.3);
        locationList.add(7.2);

        assertNotNull(locationList);
        assertEquals(3, locationList.size());
    }

    //Testing whether the arraylist get method works
    @Test
    public void locationlistGET_isCorrect() {
        ArrayList<Double> locationList = new ArrayList<>();
        locationList.add(4.5);

        assertNotNull(locationList);
        assertEquals(4.5, locationList.get(0), 0.0f);
    }

    //Testing whether JSON object is correct
    @Test
    public void jsonOBJECT_isCorrect() {
        JSONObject actual = new JSONObject();
        try {
            actual.put("id", 1);
            actual.put("name", "mkyong");
            actual.put("age", 37);

            JSONAssert.assertEquals("{id:1, age:37}", actual, false);
            JSONAssert.assertEquals("{name:\"mkyong\"}", actual, false);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    //Testing whether JSON array is correct
    @Test
    public void jsonARRAY_isCorrect() {
        String result = "[1,2,3,4,5]";
        try {
            JSONAssert.assertEquals("[5,3,2,1,4]", result, false);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    //Testing whether drawable array is correct
    @Test
    public void drawableARRAY_isCorrect() {
        int[] draw = {R.drawable.thunderstorm, R.drawable.drizzle, R.drawable.rain, R.drawable.snow, R.drawable.clear, R.drawable.clouds, R.drawable.atmosphere, R.drawable.atmosphere, R.drawable.atmosphere, R.drawable.atmosphere};
        assertNotNull(draw);
        assertEquals(10, draw.length);
    }

    //Testing whether JSON object getString method works
    @Test
    public void jsonObjecttoSTRING_isCorrect() {
        JSONObject actual = new JSONObject();
        try {
            actual.put("id", 1);
            actual.put("name", "mkyong");
            String actualString = actual.getString("id");
            String actualString2 = actual.getString("name");

            assertEquals("1", actualString);
            assertEquals("mkyong", actualString2);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    //Testing whether bundle contains key inside
    @Test
    public void bundlecontainKey_isCorrect() {
        Bundle mockBundle = Mockito.mock(Bundle.class);
        Mockito.when(mockBundle.containsKey("city")).thenReturn(true);
        assertTrue(mockBundle.containsKey("city"));
    }
}