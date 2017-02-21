package id.sch.smktelkom_mlg.learn.recyclerview3;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

import id.sch.smktelkom_mlg.learn.recyclerview3.adapter.HotelAdapter;
import id.sch.smktelkom_mlg.learn.recyclerview3.model.Hotel;

public class MainActivity extends AppCompatActivity implements HotelAdapter.IHotelAdapter {
    public static final String HOTEL = "hotel";
    public static final int REQUEST_CODE_ADD = 88;
    public static final int REQUEST_CODE_EDIT = 99;
    ArrayList<Hotel> mlist = new ArrayList<>();
    HotelAdapter mAdapter;
    int itemPos;
    ArrayList<Hotel> mListAll = new ArrayList<>();
    boolean isFiltered;
    ArrayList<Integer> mListMapFilter = new ArrayList<>();
    String mQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new HotelAdapter(this, mlist);
        recyclerView.setAdapter(mAdapter);

        fillData();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goAdd();
            }
        });
    }

    private void goAdd() {
        startActivityForResult(new Intent(this, InputActivity.class), REQUEST_CODE_ADD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD && resultCode == RESULT_OK) {
            Hotel hotel = (Hotel) data.getSerializableExtra(HOTEL);
            mlist.add(hotel);
            if (isFiltered) mListAll.add(hotel);
            doFilter(mQuery);
            //mAdapter.notifyDataSetChanged();
        } else if (requestCode == REQUEST_CODE_EDIT && resultCode == RESULT_OK) {
            Hotel hotel = (Hotel) data.getSerializableExtra(HOTEL);
            mlist.remove(itemPos);
            if (isFiltered) mListAll.remove(mListMapFilter.get(itemPos).intValue());
            mlist.add(itemPos, hotel);
            if (isFiltered) mListAll.add(itemPos, hotel);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void fillData() {
        Resources resources = getResources();
        String[] arJudul = resources.getStringArray(R.array.places);
        String[] arDeskripsi = resources.getStringArray(R.array.place_desc);
        String[] arDetail = resources.getStringArray(R.array.place_details);
        String[] arLokasi = resources.getStringArray(R.array.place_locations);
        TypedArray a = resources.obtainTypedArray(R.array.places_picture);
        String[] arFoto = new String[a.length()];
        for (int i = 0; i < arFoto.length; i++) {
            int id = a.getResourceId(i, 0);
            arFoto[i] = ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                    + resources.getResourcePackageName(id) + '/'
                    + resources.getResourceTypeName(id) + '/'
                    + resources.getResourceEntryName(id);
        }
        a.recycle();

        for (int i = 0; i < arJudul.length; i++) {
            mlist.add(new Hotel(arJudul[i], arDeskripsi[i], arDetail[i], arLokasi[i], arFoto[i]));
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mQuery = newText.toLowerCase();
                doFilter(mQuery);
                return true;
            }
        });

        return true;
    }

    private void doFilter(String query) {
        if (!isFiltered) {
            mListAll.clear();
            mListAll.addAll(mlist);
            isFiltered = true;
        }

        mlist.clear();
        if (query == null || query.isEmpty()) {
            mlist.addAll(mListAll);
            isFiltered = false;
        } else {
            mListMapFilter.clear();
            for (int i = 0; i < mListAll.size(); i++) {
                Hotel hotel = mListAll.get(i);
                if (hotel.judul.toLowerCase().contains(query) ||
                        hotel.deskripsi.toLowerCase().contains(query) ||
                        hotel.lokasi.toLowerCase().contains(query)) {
                    mlist.add(hotel);
                    mListMapFilter.add(i);
                }
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void doClick(int pos) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(HOTEL, mlist.get(pos));
        startActivity(intent);
    }

    @Override
    public void doEdit(int Pos) {
        itemPos = Pos;
        Intent intent = new Intent(this, InputActivity.class);
        intent.putExtra(HOTEL, mlist.get(Pos));
        startActivityForResult(intent, REQUEST_CODE_EDIT);
    }

    @Override
    public void doDelete(int Pos) {
        itemPos = Pos;
        final Hotel hotel = mlist.get(Pos);
        mlist.remove(itemPos);
        if (isFiltered) mListAll.remove(mListMapFilter.get(itemPos).intValue());
        mAdapter.notifyDataSetChanged();
        Snackbar.make(findViewById(R.id.fab), hotel.judul + "Terhapus", Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mlist.add(itemPos, hotel);
                        if (isFiltered) mListAll.add(mListMapFilter.get(itemPos), hotel);
                        mAdapter.notifyDataSetChanged();
                    }
                })
                .show();
    }

    @Override
    public void doFav(int Pos) {

    }

    @Override
    public void doShare(int Pos) {

    }
}