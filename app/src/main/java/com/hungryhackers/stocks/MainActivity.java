package com.hungryhackers.stocks;

import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.hungryhackers.stocks.fragments.SearchFragment;
import com.hungryhackers.stocks.fragments.StockListFragment;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements SearchFragment.OnSearchItemClickListener {

    private static final String TAG = "MainActivity";
    public static final String STOCK_DELIMITER_FOR_SP = ",";
    private static final long SEARCH_ANIMATION_DURATION = 100;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.search_view_layout)
    View searchView;
    @BindView(R.id.search_view_edit_text)
    EditText searchEditText;
    @BindView(R.id.search_clear_button)
    ImageView searchClearButton;

    @BindView(R.id.search_view_constraint_layout)
    ConstraintLayout constraintLayout;

    ConstraintSet initialSet, finalSet;

    private Boolean alreadyRevealed = false;
    private Boolean firstCharacterType = true;

    StockListFragment stockFragment;
    SearchFragment searchFragment;

    private FragmentManager manager;

    private TextWatcher searchTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (!charSequence.toString().isEmpty()) {
                if (firstCharacterType) {
                    showClearButton(true);
                    firstCharacterType = false;
                }
            } else {
                showClearButton(false);
                firstCharacterType = true;
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (!editable.toString().isEmpty())
                searchFragment.search(editable.toString());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        stockFragment = new StockListFragment();
        searchFragment = new SearchFragment();

        searchEditText.clearFocus();

        initialSet = new ConstraintSet();
        initialSet.clone(constraintLayout);
        finalSet = new ConstraintSet();
        finalSet.clone(this, R.layout.search_view_revealed);

        searchView.setOnClickListener(view -> revealSearchView(true));
        searchEditText.setOnClickListener(view -> revealSearchView(true));


        manager = getSupportFragmentManager();
        manager.beginTransaction().add(R.id.fragment_frame_layout, stockFragment)
                .add(R.id.fragment_frame_layout, searchFragment)
                .hide(searchFragment)
                .commit();
    }

    public void onSearchClick(View view) {
        revealSearchView(true);
    }

    @Override
    public void onBackPressed() {
        if (alreadyRevealed) revealSearchView(false);
        else super.onBackPressed();
    }

    void revealSearchView(boolean reveal) {

        if (reveal == alreadyRevealed) {
            searchEditText.requestFocusFromTouch();
            return;
        }

        AutoTransition transition = new AutoTransition();
        transition.setDuration(SEARCH_ANIMATION_DURATION);
        TransitionManager.beginDelayedTransition(constraintLayout, transition);

        searchEditText.setFocusable(reveal);

        ConstraintSet animationSet;
        Fragment showFrag, hideFrag;

        if (reveal) {
            animationSet = finalSet;
            showFrag = searchFragment;
            hideFrag = stockFragment;

            searchEditText.requestFocusFromTouch();
            searchEditText.addTextChangedListener(searchTextWatcher);

            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT);
        } else {
            animationSet = initialSet;
            showFrag = stockFragment;
            hideFrag = searchFragment;

            if (!searchEditText.getText().toString().isEmpty())
                searchEditText.setText("");
            searchEditText.removeTextChangedListener(searchTextWatcher);

            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
        }
        alreadyRevealed = !alreadyRevealed;

        animationSet.applyTo(constraintLayout);

        manager.beginTransaction().show(showFrag)
                .hide(hideFrag)
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .commit();
    }

    private void showClearButton(boolean show) {
        Animation anim;
        if (show) {
            anim = AnimationUtils.loadAnimation(this, R.anim.fab_open);
            searchClearButton.setOnClickListener(view -> {
                searchEditText.setText("");
                searchEditText.requestFocusFromTouch();
            });
        } else {
            anim = AnimationUtils.loadAnimation(this, R.anim.fab_close);
            searchClearButton.setOnClickListener(null);
        }
        searchClearButton.setVisibility(View.VISIBLE);
        searchClearButton.startAnimation(anim);
    }

    public void cancelSearch(View view) {
        revealSearchView(false);
    }

    @Override
    public void onItemClicked() {
        revealSearchView(false);
    }
}
