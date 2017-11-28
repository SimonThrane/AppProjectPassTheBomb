package com.thrane.simon.passthebomb;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.NumberPicker;

public class CreateLobbyActivity extends AppCompatActivity {
    NumberPicker nbCategory;
    enum categoryEnum {
        GENERAL_KNOWLEDGE, BOOKS, FILM, MUSIC, MUSICALS_AND_THEATRES, TELEVISION, VIDEO_GAMES, BOARD_GAMES, SCIENCE_AND_NATURE, COMPUTERS, MATHEMATICS, MYTHOLOGY, SPORTS, GEOGRAPHY, HISTORY, POLITICS, ART, CELEBRITIES, ANIMALS, VEHICLES, COMICS, GADGETS, ANIME_AND_MANGA, CARTOON_AND_ANIMATIONS
    }
    final String categories[] = {"General knowledge", "Books", "Film", "Music", "Musicals and theatres", "Television", "Video games", "Science and nature", "Computers", "Mathematics", "Mythology", "Sports", "Geography", "History", "Politics", "Art", "Celebrities", "Animals", "Vehicles", "Comics", "Gadgets", "Anime and manga", "Cartoon and animations"}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_lobby);

        // get widget references
        nbCategory = findViewById(R.id.nbCategory);
    }

    private void configureCategoryPicker() {
        nbCategory.setMinValue(0);
        nbCategory.setMaxValue(categories.length - 1);
        nbCategory.setDisplayedValues(categories);
        nbCategory.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        nbCategory.setValue(0);
//        NumberPicker.OnValueChangeListener catChangedListener = new NumberPicker.OnValueChangeListener() {
//            @Override
//            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
//
//            }
//        }
    }
}

// dont delete pls, need dis
// https://stackoverflow.com/questions/33442035/how-to-make-custom-android-string-picker-that-fits-inside-another-view