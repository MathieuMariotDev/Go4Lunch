
package com.example.go4lunch.POJO.Prediction;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import com.google.android.libraries.places.api.model.Place;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.maps.model.AutocompletePrediction;

public class Prediction {

    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("matched_substrings")
    @Expose
    private List<AutocompletePrediction.MatchedSubstring> matchedSubstrings = null;
    @SerializedName("structured_formatting")
    @Expose
    private StructuredFormatting structuredFormatting;
    @SerializedName("terms")
    @Expose
    private List<AutocompletePrediction.Term> terms = null;
    @SerializedName("place_id")
    @Expose
    private String placeId;
    @SerializedName("reference")
    @Expose
    private String reference;
    @SerializedName("types")
    @Expose
    private List<String> types = null;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<AutocompletePrediction.MatchedSubstring> getMatchedSubstrings() {
        return matchedSubstrings;
    }

    public void setMatchedSubstrings(List<AutocompletePrediction.MatchedSubstring> matchedSubstrings) {
        this.matchedSubstrings = matchedSubstrings;
    }

    public StructuredFormatting getStructuredFormatting() {
        return structuredFormatting;
    }

    public void setStructuredFormatting(StructuredFormatting structuredFormatting) {
        this.structuredFormatting = structuredFormatting;
    }

    public List<AutocompletePrediction.Term> getTerms() {
        return terms;
    }

    public void setTerms(List<AutocompletePrediction.Term> terms) {
        this.terms = terms;
    }

    public String getPlaceId() {
        return placeId;
    }

    @Nullable
    public Integer getDistanceMeters() {
        return null;
    }

    @NonNull
    public List<Place.Type> getPlaceTypes() {
        return null;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

}
