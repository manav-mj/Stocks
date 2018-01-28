package com.hungryhackers.stocks;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.hungryhackers.stocks.fragments.SearchFragment;
import com.hungryhackers.stocks.fragments.StockListFragment;
import com.hungryhackers.stocks.models.StockSymbol;

import java.util.ArrayList;

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

    @BindView(R.id.search_view_constraint_layout)
    ConstraintLayout constraintLayout;

    ConstraintSet initialSet, finalSet;

    private Boolean alreadyRevealed = false;

    StockListFragment stockFragment;
    SearchFragment searchFragment;

    private FragmentManager manager;

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
        if (alreadyRevealed)revealSearchView(false);
        else super.onBackPressed();
    }

    void revealSearchView(boolean reveal) {

        if (reveal == alreadyRevealed) {
            if (reveal)
                startSearching();
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
            startSearching();
        } else {
            searchEditText.setText("");
            animationSet = initialSet;
            showFrag = stockFragment;
            hideFrag = searchFragment;
        }
        alreadyRevealed = !alreadyRevealed;

        animationSet.applyTo(constraintLayout);

        manager.beginTransaction().show(showFrag)
                .hide(hideFrag)
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .commit();
    }

    private void startSearching() {
        searchEditText.bringToFront();
        searchEditText.requestFocus();
        searchEditText.requestFocusFromTouch();
        searchEditText.setShowSoftInputOnFocus(true);
        toolbar.setFocusable(false);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().isEmpty())
                    searchFragment.search(editable.toString());
            }
        });
    }

    public void cancelSearch(View view) {
        revealSearchView(false);
    }

    @Override
    public void onItemClicked() {
        revealSearchView(false);
    }
}
