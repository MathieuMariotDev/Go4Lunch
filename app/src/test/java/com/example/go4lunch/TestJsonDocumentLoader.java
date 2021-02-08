package com.example.go4lunch;

import android.util.Log;

import com.example.go4lunch.POJO.Prediction;
import com.example.go4lunch.POJO.QueryAutocomplete;
import com.google.api.client.util.IOUtils;
import com.google.common.io.Resources;
import com.google.gson.Gson;

import org.mockito.internal.util.io.IOUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class TestJsonDocumentLoader {

    private List<Prediction> predictionList = new ArrayList<>();


    public List<Prediction> listentPrediction(String fileName) {
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

        QueryAutocomplete resultList = gson.fromJson(jsonString, QueryAutocomplete.class);

        for (int i = 0; i < resultList.getPredictions().size(); i++) {
            predictionList.add(resultList.getPredictions().get(i));
        }
        return predictionList;
    }

}
