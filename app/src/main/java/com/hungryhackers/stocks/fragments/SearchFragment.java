package com.hungryhackers.stocks.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.hungryhackers.stocks.R;
import com.hungryhackers.stocks.models.SearchViewModel;
import com.hungryhackers.stocks.models.StockSymbol;
import com.hungryhackers.stocks.models.StockViewModel;
import com.hungryhackers.stocks.network.StockRepository;
import com.hungryhackers.stocks.utils.StockUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchFragment extends Fragment {

    private OnSearchFragmentListener mListener;
    private StockRepository repository;
    private SearchViewModel searchViewModel;
    private StockViewModel stockViewModel;
    @BindView(R.id.symbol_list_view)
    ListView listView;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        searchViewModel = new SearchViewModel();
        stockViewModel = new StockViewModel();
        repository = new StockRepository(getContext());

        stockViewModel.init(false, repository);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSearchFragmentListener) {
            mListener = (OnSearchFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSearchFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void search(String companyName) {
        if (companyName.isEmpty()) {
            listView.setAdapter(null);
            return;
        }

        stockViewModel.fetchSymbolForName(companyName);
        stockViewModel.getSymbolSearchResponse().observe(this, this::setSymbolDialogue);
    }

    private void setSymbolDialogue(final ArrayList<StockSymbol> symbols) {

        String[] items = new String[symbols.size()];

        for (int i = 0; i < symbols.size(); i++) {
            items[i] = symbols.get(i).getName();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, items);
        listView.setAdapter(adapter);

        mListener.onSearchCompleted();

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            String symbol = symbols.get(i).getSymbol();
            if (!stockViewModel.getSymbolList().getValue().contains(symbol))
                stockViewModel.addStockWithSymbol(symbol);
            listView.setAdapter(null);
            mListener.onItemClicked();
        });
    }

    public interface OnSearchFragmentListener {
        // TODO: Update argument type and name
        void onItemClicked();

        void onSearchCompleted();
    }
}
