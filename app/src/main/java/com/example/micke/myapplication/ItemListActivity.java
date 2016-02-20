package com.example.micke.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.micke.myapplication.dummy.DummyContent;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ItemListActivity extends AppCompatActivity implements SortingDialogFragment.NoticeDialogListener {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private ActionMode mActionMode;
    public Datasource DS;
    private ArrayList<Item> mArrayList;

    private boolean ascending = true;
    private SimpleItemRecyclerViewAdapter mAdapter;
    View recyclerView;

    //Used to keep track in which post is pressed in mtwopane view
    private long mActivatedPosition;

    private ItemDetailFragment fragment;

    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        //Shared preferences
        prefs = this.getSharedPreferences("com.example.micke.myapplication", Context.MODE_PRIVATE);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Snackbar.make(view, "Far åt helvete", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });



        //sortingDialogFragment = new SortingDialogFragment();
        recyclerView = findViewById(R.id.item_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        mAdapter = new SimpleItemRecyclerViewAdapter();
        recyclerView.setAdapter(mAdapter);
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        public SimpleItemRecyclerViewAdapter() {
            openDB();
            mArrayList = DS.fetchAll(1, ascending);
            Log.d("TAG", String.valueOf(mArrayList.size()));
            closeDB();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mArrayList.get(position);
            holder.mIdView.setText(String.valueOf(mArrayList.get(position).getRating()));
            holder.mContentView.setText(mArrayList.get(position).getTitle());

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putLong(ItemDetailFragment.ARG_ITEM_ID, holder.mItem.getId());
                        arguments.putString(ItemDetailFragment.ARG_ITEM_TITLE, String.valueOf(holder.mItem.getTitle()));
                        arguments.putString(ItemDetailFragment.ARG_ITEM_DESCRIPTION, String.valueOf(holder.mItem.getDescription()));
                        arguments.putInt(ItemDetailFragment.ARG_ITEM_RATING, holder.mItem.getRating());
                        fragment = new ItemDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.item_detail_container, fragment)
                                .commit();
                        //Hur sätta backgroundcolor så att den ändras för varje klick.
                        //holder.mView.setBackgroundColor(Color.BLACK);


                        mActivatedPosition = holder.mItem.getId();

                        /*UPPGIFT:Skapa en contextualactionbar med alternativen delete och edit,
                          se ﬁgur 7. Denna ska endast visas på plattor, när man använder mobil så
                          visas dessa alternativen i en option menu istället, ﬁgur 6.*/

                        // Start the CAB using the ActionMode.Callback defined above
                        mActionMode = startActionMode(mActionModeCallback);
                        v.setSelected(true);

                    } else {
                        Log.d("kalle", String.valueOf(holder.mItem.getId()));

                        Context context = v.getContext();
                        Intent intent = new Intent(context, ItemDetailActivity.class);
                        intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, holder.mItem.getId());
                        intent.putExtra(ItemDetailFragment.ARG_ITEM_TITLE, String.valueOf(holder.mItem.getTitle()));
                        intent.putExtra(ItemDetailFragment.ARG_ITEM_DESCRIPTION, String.valueOf(holder.mItem.getDescription()));
                        intent.putExtra(ItemDetailFragment.ARG_ITEM_RATING, holder.mItem.getRating());
                        //context.startActivity(intent);
                        startActivityForResult(intent, 0);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mArrayList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final View oldView;
            public final TextView mIdView;
            public final TextView mContentView;
            public Item mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                oldView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.optionsmenu_fragment, menu);
        return true;
    }

    //Used when an optionsmenu item is used
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.add:
                openDB();
                DS.insertItem("kung", 3, "gustav");
                DS.insertItem("kalle", 5, "anka");
                DS.insertItem("da", 1, "man");
                DS.insertItem("skapligt", 2, "trög");
                DS.insertItem("james", 4,"bond");
                mArrayList = DS.fetchAll(0, ascending);
                closeDB();
                mAdapter.notifyDataSetChanged();
                return true;
            case R.id.sorting:
                showNoticeDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.optionsmenu_activity_b, menu);
            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.edit:
                    //shareCurrentItem();
                    mode.finish(); // Action picked, so close the CAB
                    return true;
                case R.id.delete:
                    if(mArrayList.size() != 0) {
                        deletePost(mActivatedPosition);
                        if(fragment != null)
                            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                            mode.finish();
                    }
                    return true;
                default:
                    return false;
            }
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        if(requestCode == 0){
            if(resultCode == Activity.RESULT_OK){
                long itemId = data.getLongExtra(ItemDetailFragment.ARG_ITEM_ID, 0);
                deletePost(itemId);
            }
        }

    }

    public void showNoticeDialog() {
        // Create an instance of the dialog fragment and show it
        //DialogFragment dialog = new NoticeDialogFragment();
        SortingDialogFragment sortingDialogFragment = new SortingDialogFragment();
        sortingDialogFragment.show(getFragmentManager(), "SortingDialogFragment");
    }

    @Override
    public void onDialogPositiveClick(android.app.DialogFragment dialogFragment) {
        //retrieves the chosen sort from SortingDialogFragment
        int chosenSort = prefs.getInt(SortingDialogFragment.ARG_SORT, 0);
        openDB();
        mArrayList = DS.fetchAll(chosenSort, ascending);
        closeDB();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDialogNegativeClick(android.app.DialogFragment dialogFragment) {

    }

    private void deletePost(long itemId){
        openDB();
        ListIterator<Item> iter = mArrayList.listIterator();
        while(iter.hasNext()){
            if(iter.next().getId() == itemId)
                iter.remove();
        }
        mAdapter.notifyDataSetChanged();
        DS.deleteItem(itemId);
        closeDB();
    }

    private void openDB(){
        DS = new Datasource(this);
        DS.open();
    }

    private void closeDB(){
        DS.close();
    }
}
