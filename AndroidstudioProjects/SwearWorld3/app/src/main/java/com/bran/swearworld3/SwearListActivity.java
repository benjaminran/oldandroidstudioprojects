package com.bran.swearworld3;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ArrayAdapter;
import android.widget.SearchView;

import com.mongodb.DBObject;


/**
 * An activity representing a list of Swears. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link SwearDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link SwearListFragment} and the item details
 * (if present) is a {@link SwearDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link SwearListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class SwearListActivity extends Activity
        implements SwearListFragment.Callbacks {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    private SwearListFragment swearListFragment;
    private MyMongoClient mongoClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swear_list);

        mongoClient = MyMongoClient.getInstance();
        swearListFragment = (SwearListFragment) getFragmentManager().findFragmentById(R.id.swear_list);
        if (findViewById(R.id.swear_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            swearListFragment.setActivateOnItemClick(true);
        }

        handleIntent(getIntent());
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.i("SwearWorld", query);
            ArrayAdapter<DBObject> adapter = mongoClient.getSwearsFromMongo(query);
            swearListFragment.setListAdapter(adapter);
        }
    }

    /**
     * Callback method from {@link SwearListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(SwearDetailFragment.ARG_ITEM_ID, id);
            SwearDetailFragment fragment = new SwearDetailFragment();
            fragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .replace(R.id.swear_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, SwearDetailActivity.class);
            detailIntent.putExtra(SwearDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }
}
