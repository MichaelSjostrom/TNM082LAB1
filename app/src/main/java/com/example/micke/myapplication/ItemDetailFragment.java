package com.example.micke.myapplication;

import android.app.Activity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.micke.myapplication.dummy.DummyContent;

import java.util.ArrayList;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link ItemListActivity}
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
 */
public class ItemDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    public static final String ARG_ITEM_TITLE = "item_title";
    public static final String ARG_ITEM_DESCRIPTION = "item_description";
    public static final String ARG_ITEM_RATING = "item_rating";

    /**
     * The dummy content this fragment is presenting.
     */
    private Item mItem;

    private RatingBar ratingBar;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mItem = new Item();

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.

            //This should be replaced
            //mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
            Long argID = getArguments().getLong(ARG_ITEM_ID, 0);
            String argTitle = getArguments().getString(ARG_ITEM_TITLE);
            String argDescription = getArguments().getString(ARG_ITEM_DESCRIPTION);
            int argRating = getArguments().getInt(ARG_ITEM_RATING);

            Log.d("kalle", String.valueOf(argID));
            Log.d("kalle",argTitle);
            Log.d("kalle",argDescription);
            Log.d("kalle","after");
            mItem.setId(argID);
            mItem.setTitle(argTitle);
            mItem.setDescription(argDescription);
            mItem.setRating(argRating);

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);

            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.getTitle());

            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_layout, container, false);
        ratingBar = (RatingBar) rootView.findViewById(R.id.ratingBar);

        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.text_description)).setText(mItem.getDescription());
            ratingBar.setNumStars(mItem.getRating());
        }

        return rootView;
    }


}
