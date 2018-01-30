package com.hungryhackers.stocks;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
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
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hungryhackers.stocks.fragments.SearchFragment;
import com.hungryhackers.stocks.fragments.StockListFragment;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements SearchFragment.OnSearchFragmentListener {

    private static final String TAG = "MainActivity";
    public static final String STOCK_DELIMITER_FOR_SP = ",";
    private static final long SEARCH_ANIMATION_DURATION = 100;

    private static long SEARCH_DELAY = 2000;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.search_view_layout)
    View searchView;
    @BindView(R.id.search_view_edit_text)
    EditText searchEditText;
    @BindView(R.id.search_clear_button)
    ImageView searchClearButton;
    @BindView(R.id.search_status_text_view)
    TextView statusTextView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.search_view_constraint_layout)
    ConstraintLayout constraintLayout;

    ConstraintSet initialSet, finalSet;

    private Boolean alreadyRevealed = false;

    StockListFragment stockFragment;
    SearchFragment searchFragment;

    private FragmentManager manager;

    private TextWatcher searchTextWatcher;

    private Handler searchHandler;
    private Runnable searchTimer;
    private String searchString;
    private long lastTextEditTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        initialise();

        searchView.setOnClickListener(view -> revealSearchView(true));
        searchEditText.setOnClickListener(view -> revealSearchView(true));


        manager = getSupportFragmentManager();
        manager.beginTransaction().add(R.id.fragment_frame_layout, stockFragment)
                .add(R.id.fragment_frame_layout, searchFragment)
                .hide(searchFragment)
                .commit();
    }

    private void initialise() {
        setSupportActionBar(toolbar);

        searchHandler = new Handler();

        searchTimer = () -> {
            if (System.currentTimeMillis() > (lastTextEditTime + SEARCH_DELAY)) {
                searchFragment.search(searchString);
            }
        };

        searchTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                if (charSequence.toString().isEmpty()){
                    showProgress(true);
                    showClearButton(true);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

                searchHandler.removeCallbacks(searchTimer);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().isEmpty()) {
                    showProgress(true);

                    searchString = editable.toString();

                    lastTextEditTime = System.currentTimeMillis();
                    searchHandler.postDelayed(searchTimer, SEARCH_DELAY);
                } else {
                    searchFragment.search(editable.toString());
                    showClearButton(false);
                    showProgress(false);
                }
            }
        };

        stockFragment = new StockListFragment();
        searchFragment = new SearchFragment();

        searchEditText.clearFocus();

        initialSet = new ConstraintSet();
        initialSet.clone(constraintLayout);
        finalSet = new ConstraintSet();
        finalSet.clone(this, R.layout.search_view_revealed);
    }

    private void showProgress(boolean show) {
        statusTextView.setText(show
                ? getResources().getString(R.string.search_status_validating)
                : getResources().getString(R.string.search_status_normal)
        );
        progressBar.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
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

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT);
        } else {
            animationSet = initialSet;
            showFrag = stockFragment;
            hideFrag = searchFragment;

            if (!searchEditText.getText().toString().isEmpty())
                searchEditText.setText("");
            searchEditText.removeTextChangedListener(searchTextWatcher);

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
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

    @Override
    public void onSearchCompleted() {
        showProgress(false);
    }
}
