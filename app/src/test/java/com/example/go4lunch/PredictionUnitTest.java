package com.example.go4lunch;

import android.app.Application;

import com.example.go4lunch.POJO.Prediction.Prediction;
import com.example.go4lunch.POJO.Prediction.StructuredFormatting;


import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class PredictionUnitTest {
    Application application;
    List<Prediction> predictionList;

    @Test
    public void PredictionEqualsJson() {
        StructuredFormatting structuredFormatting = new StructuredFormatting();
        Prediction prediction = new Prediction();
        prediction.setDescription("Pizza Hut, Avenue Marie Talet, Angers, France");
        prediction.setPlaceId("ChIJVQNQwuh4CEgRWXjEftLVvz4");
        structuredFormatting.setMainText("Pizza Hut");
        structuredFormatting.setSecondaryText("Avenue Marie Talet, Angers, France");
        structuredFormatting.setMainTextMatchedSubstrings(null);
        prediction.setStructuredFormatting(structuredFormatting);
        prediction.setTypes(Collections.singletonList("establishment"));
        TestJsonDocumentLoader testJsonDocumentLoader = new TestJsonDocumentLoader();
        predictionList = testJsonDocumentLoader.getListPredictionFromJson("QueryAutocomplete.json");
        assertEquals(prediction.getDescription(), predictionList.get(0).getDescription());
        assertEquals(prediction.getPlaceId(), predictionList.get(0).getPlaceId());
        assertEquals(prediction.getStructuredFormatting().getMainText(), predictionList.get(0).getStructuredFormatting().getMainText());
        assertEquals(prediction.getStructuredFormatting().getSecondaryText(), predictionList.get(0).getStructuredFormatting().getSecondaryText());
        assertEquals(prediction.getPlaceTypes(), predictionList.get(0).getPlaceTypes());
    }


}