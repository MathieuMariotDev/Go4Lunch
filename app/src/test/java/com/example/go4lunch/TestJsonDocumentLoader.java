package com.example.go4lunch;

import com.example.go4lunch.POJO.Detail.PlaceDetail;
import com.example.go4lunch.POJO.Detail.PlaceDetailResponse;
import com.example.go4lunch.POJO.Prediction.Prediction;
import com.example.go4lunch.POJO.Prediction.QueryAutocomplete;
import com.google.gson.Gson;
import com.google.maps.model.PlaceDetails;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

class TestJsonDocumentLoader {

    private PlaceDetail mPlaceDetails;

    public QueryAutocomplete getListPredictionFromJson(String fileName) {
        String jsonString;
        try {
            InputStream is = getClass().getResourceAsStream(fileName);
            assert (is != null);
            final int bufferSize = 1024;
            final char[] buffer = new char[bufferSize];
            final StringBuilder out = new StringBuilder();
            Reader in = new InputStreamReader(is, StandardCharsets.UTF_8);
            int charsRead;
            while ((charsRead = in.read(buffer, 0, buffer.length)) > 0) {
                out.append(buffer, 0, charsRead);

            }
            jsonString = out.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        Gson gson = new Gson();

        QueryAutocomplete autocompleteResponse = gson.fromJson(jsonString, QueryAutocomplete.class);

        return autocompleteResponse;
    }

    public PlaceDetail getPlaceDetailFromJson(String fileName) {
        String jsonString;
        try {
            InputStream is = getClass().getResourceAsStream(fileName);
            assert (is != null);
            final int bufferSize = 1024;
            final char[] buffer = new char[bufferSize];
            final StringBuilder out = new StringBuilder();
            Reader in = new InputStreamReader(is, StandardCharsets.UTF_8);
            int charsRead;
            while ((charsRead = in.read(buffer, 0, buffer.length)) > 0) {
                out.append(buffer, 0, charsRead);

            }
            jsonString = out.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        Gson gson = new Gson();

        PlaceDetailResponse resultList = gson.fromJson(jsonString, PlaceDetailResponse.class);

        mPlaceDetails = resultList.getResult();

        return mPlaceDetails;
    }
}
