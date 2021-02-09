package com.example.go4lunch;

import android.app.Application;

import com.example.go4lunch.POJO.Prediction.Prediction;
import com.example.go4lunch.POJO.Prediction.QueryAutocomplete;
import com.example.go4lunch.POJO.Prediction.StructuredFormatting;


import org.junit.Before;
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
    TestJsonDocumentLoader testJsonDocumentLoader = new TestJsonDocumentLoader();
    Prediction prediction = new Prediction();
    QueryAutocomplete mQueryAutocomplete;

    @Before
    public void setup() {
        StructuredFormatting structuredFormatting = new StructuredFormatting();
        prediction.setDescription("Pizza Hut, Avenue Marie Talet, Angers, France");
        prediction.setPlaceId("ChIJVQNQwuh4CEgRWXjEftLVvz4");
        structuredFormatting.setMainText("Pizza Hut");
        structuredFormatting.setSecondaryText("Avenue Marie Talet, Angers, France");
        structuredFormatting.setMainTextMatchedSubstrings(null);
        prediction.setStructuredFormatting(structuredFormatting);
        prediction.setTypes(Collections.singletonList("establishment"));
        mQueryAutocomplete = testJsonDocumentLoader.getListPredictionFromJson("QueryAutocomplete.json");
    }

    @Test
    public void QueryAutoComplete() {
        QueryAutocomplete queryAutocomplete = new QueryAutocomplete();
        queryAutocomplete.setPredictions(Collections.singletonList(prediction));
        assertEquals(queryAutocomplete.getPredictions().get(0), prediction);
        queryAutocomplete.setStatus("OK");
        assertEquals(queryAutocomplete.getStatus(), mQueryAutocomplete.getStatus());
    }

    @Test
    public void PredictionEqualsJson() {
        predictionList = mQueryAutocomplete.getPredictions();
        assertEquals(prediction.getDescription(), predictionList.get(0).getDescription());
        assertEquals(prediction.getPlaceId(), predictionList.get(0).getPlaceId());
        assertEquals(prediction.getStructuredFormatting().getMainText(), predictionList.get(0).getStructuredFormatting().getMainText());
        assertEquals(prediction.getStructuredFormatting().getSecondaryText(), predictionList.get(0).getStructuredFormatting().getSecondaryText());
        assertEquals(prediction.getPlaceTypes(), predictionList.get(0).getPlaceTypes());
    }


}