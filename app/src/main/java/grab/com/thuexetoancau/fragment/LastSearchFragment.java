package grab.com.thuexetoancau.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

import grab.com.thuexetoancau.R;
import grab.com.thuexetoancau.adapter.LastSearchAdapter;
import grab.com.thuexetoancau.model.Location;
import grab.com.thuexetoancau.utilities.DividerItemDecoration;

public class LastSearchFragment extends Fragment {
    private RecyclerView listLastSearch;
    private ArrayList<Location> arrayLastSearch;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        dummyData();
        View view = inflater.inflate(R.layout.fragment_last_search, container, false);
        initComponents(view);

        return view;
    }

    private void initComponents(View view) {
        listLastSearch = (RecyclerView) view.findViewById(R.id.list_last_search);
        // set cardview
        listLastSearch.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        listLastSearch.setLayoutManager(llm);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(getActivity(), LinearLayout.VERTICAL);
        listLastSearch.addItemDecoration(mDividerItemDecoration);
        LastSearchAdapter adapter = new LastSearchAdapter(getActivity(), arrayLastSearch);
        listLastSearch.setAdapter(adapter);
    }

    private void dummyData() {
        arrayLastSearch = new ArrayList<>();
        arrayLastSearch.add(new Location("Phạm Ngọc Thạch", "Đống Đa, Hà Nội"));
        arrayLastSearch.add(new Location("Tôn Thất Thuyết", "Cầu Giấy, Hà Nội"));
        arrayLastSearch.add(new Location("Bạch Mai", "Hai Bà Trưng, Hà Nội"));
    }

}
