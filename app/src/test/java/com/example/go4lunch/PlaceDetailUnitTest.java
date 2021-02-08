package com.example.go4lunch;

import com.example.go4lunch.POJO.Detail.PlaceDetail;
import com.google.maps.model.PlaceDetails;

import org.junit.Test;

import static org.junit.Assert.*;

public class PlaceDetailUnitTest {
    PlaceDetail mPlaceDetailFromJson;

    @Test
    public void PlaceDetailEqualsJson() {
        PlaceDetail placeDetail = new PlaceDetail();
        placeDetail.setName("Pizzeria Bistro Le San Marco");
        placeDetail.setAdrAddress("\u003cspan class=\"street-address\"\u003e6 Passage Saint-Aubin\u003c/span\u003e, \u003cspan class=\"postal-code\"\u003e49130\u003c/span\u003e \u003cspan class=\"locality\"\u003eLes Ponts-de-Cé\u003c/span\u003e, \u003cspan class=\"country-name\"\u003eFrance\u003c/span\u003e"
        );
        placeDetail.setRating(4.3);
        placeDetail.setFormattedPhoneNumber("02 41 44 98 08");
        placeDetail.setWebsite("http://www.lesanmarco-pizzeria.fr/");

        TestJsonDocumentLoader testJsonDocumentLoader = new TestJsonDocumentLoader();
        mPlaceDetailFromJson = testJsonDocumentLoader.getPlaceDetailFromJson("FetchPlaceDetail.json");

        assertEquals(placeDetail.getName(), mPlaceDetailFromJson.getName());
        assertEquals(placeDetail.getAdrAddress(), mPlaceDetailFromJson.getAdrAddress());
        assertEquals(placeDetail.getWebsite(), mPlaceDetailFromJson.getWebsite());
        assertEquals(placeDetail.getRating(), mPlaceDetailFromJson.getRating());
        assertEquals(placeDetail.getFormattedPhoneNumber(), mPlaceDetailFromJson.getFormattedPhoneNumber());

    }

}
