package com.example.go4lunch.Utils;

import android.content.Context;
import android.util.Log;

import com.example.go4lunch.POJO.Prediction.Prediction;
import com.example.go4lunch.POJO.Prediction.QueryAutocomplete;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class UtilPredictionMock {

    public List<String> parseJsonGetId(Context context) {
        List<String> predictionListId = new ArrayList<>();
        String jsonString;
        jsonString = UtilJson.getJsonFromAssets(context, "QueryAutocomplete.json");
        Log.i("dataJsonNearby", jsonString);
        Gson gson = new Gson();

        Type listPrediction = new TypeToken<List<QueryAutocomplete>>() {
        }.getType();

        QueryAutocomplete resultList = gson.fromJson(jsonString, QueryAutocomplete.class);

        for (int i = 0; i < resultList.getPredictions().size(); i++) {
            Log.i("DATA", " > Item" + i + "\n" + resultList.getPredictions().get(i).getPlaceId());
            predictionListId.add(resultList.getPredictions().get(i).getPlaceId());
        }


        return predictionListId;
        //mMainActivityViewModel.setRestaurant(resultList);
    }


    public List<Prediction> parseJsonGetPrediction(Context context) {
        List<Prediction> predictionList = new ArrayList<>();
        String jsonString;
        jsonString = UtilJson.getJsonFromAssets(context, "QueryAutocomplete.json");
        Log.i("dataJsonNearby", jsonString);
        Gson gson = new Gson();

        QueryAutocomplete resultList = gson.fromJson(jsonString, QueryAutocomplete.class);

        for (int i = 1; i < resultList.getPredictions().size(); i++) {
            Log.i("DATA", " > Item" + i + "\n" + resultList.getPredictions().get(i).getPlaceId());
            predictionList.add(resultList.getPredictions().get(i));
        }
        return predictionList;
    }
}
