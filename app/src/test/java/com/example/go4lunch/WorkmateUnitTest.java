package com.example.go4lunch;

import com.example.go4lunch.model.Workmate;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class WorkmateUnitTest {

    public Workmate mWorkmate;
    public ArrayList<String> listIdLike = new ArrayList<String>();

    @Before
    public void WorkmateModel() {
        listIdLike.add("123");
        listIdLike.add("456");
        mWorkmate = new Workmate("01234",
                "Toto",
                "7894",
                listIdLike,
                "https://lh3.googleusercontent.com/a-/AOh14GgciNrRyKYtNP3MR0giw-CH4hhJyrY8b-Ebtj5d=s96");
    }

    @Test
    public void getterWorkmateModel() {
        assertEquals(mWorkmate.getUid(), "01234");
        assertEquals(mWorkmate.getUsername(), "Toto");
        assertEquals(mWorkmate.getIdSelectedRestaurant(), "7894");
        assertEquals(mWorkmate.getIdLikeRestaurant(), listIdLike);
        assertEquals(mWorkmate.getUrlPicture(), "https://lh3.googleusercontent.com/a-/AOh14GgciNrRyKYtNP3MR0giw-CH4hhJyrY8b-Ebtj5d=s96");
    }

    @Test
    public void setterWorkmateModel() {
        mWorkmate.setUid("98765");
        assertEquals(mWorkmate.getUid(), "98765");
        mWorkmate.setUsername("Paul");
        assertEquals(mWorkmate.getUsername(), "Paul");
        mWorkmate.setUrlPicture("https://lh3.googleusercontent.com/a-");
        assertEquals(mWorkmate.getUrlPicture(), "https://lh3.googleusercontent.com/a-");
        mWorkmate.setIdSelectedRestaurant("0123");
        assertEquals(mWorkmate.getIdSelectedRestaurant(), "0123");
        listIdLike.add("Set");
        mWorkmate.setIdLikeRestaurant(listIdLike);
        assertEquals(mWorkmate.getIdLikeRestaurant(), listIdLike);
    }
}
