package sg.edu.rp.c346.c302_p09_mcafe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class DisplayMenuItemsActivity extends AppCompatActivity {

    String user = "";
    String apikey = "";
    private ListView lvItems;
    ArrayList<MenuCategory> alItems = new ArrayList<MenuCategory>();
    ArrayAdapter<MenuCategory> aaItems;
    String categoryID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_menu_items);
        lvItems = findViewById(R.id.lvItems);
        aaItems = new ArrayAdapter<MenuCategory>(this, android.R.layout.simple_list_item_1, alItems);
        lvItems.setAdapter(aaItems);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(DisplayMenuItemsActivity.this);
        user = prefs.getString("id", "");
        apikey = prefs.getString("apikey","");
        Intent intent = getIntent();
        MenuCategory category = (MenuCategory) intent.getSerializableExtra("category");
        categoryID = category.getCategoryId();

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.add("categoryId", categoryID);
        params.add("loginId", user);
        params.add("apikey", apikey);
        client.post("http://10.0.2.2/C302_P09_mCafe/getMenuItemsByCategory.php", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                try {
                    if (response.length() == 0) {
                        Toast.makeText(getBaseContext(), "No Items to display", Toast.LENGTH_LONG).show();
                    } else {
                        for (int i=0 ; i<response.length(); i++) {
                            JSONObject category = (JSONObject) response.get(i);
                            MenuCategory mc = new MenuCategory(category.getString("menu_item_id"), category.getString("menu_item_description"));
                            alItems.add(mc);
                        }
                        aaItems.notifyDataSetChanged();
                    }
                }
                catch (JSONException e) {

                }
            }
        });

        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MenuCategory mc = alItems.get(i);
                Intent intent = new Intent(getBaseContext(), editItemActivity.class );
                intent.putExtra("item", mc);
                startActivityForResult(intent, 12345);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_add) {
            Intent i = new Intent(getBaseContext(), addMenuItem.class);
            i.putExtra("categoryID", categoryID);
            startActivityForResult(i,12345);
        } else if (id == R.id.menu_logout) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(DisplayMenuItemsActivity.this);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("id", "");
            editor.putString("apikey", "");
            editor.commit();
            Intent i = new Intent(getBaseContext(), LoginActivity.class);
            startActivity(i);

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 12345) {
            alItems.clear();
            aaItems = new ArrayAdapter<MenuCategory>(this, android.R.layout.simple_list_item_1, alItems);
            lvItems.setAdapter(aaItems);

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(DisplayMenuItemsActivity.this);
            user = prefs.getString("id", "");
            apikey = prefs.getString("apikey","");
            Intent intent = getIntent();
            MenuCategory category = (MenuCategory) intent.getSerializableExtra("category");
            categoryID = category.getCategoryId();

            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            params.add("categoryId", categoryID);
            params.add("loginId", user);
            params.add("apikey", apikey);
            client.post("http://10.0.2.2/C302_P09_mCafe/getMenuItemsByCategory.php", params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    try {
                        if (response.length() == 0) {
                            Toast.makeText(getBaseContext(), "No Items to display", Toast.LENGTH_LONG).show();
                        } else {
                            for (int i=0 ; i<response.length(); i++) {
                                JSONObject category = (JSONObject) response.get(i);
                                MenuCategory mc = new MenuCategory(category.getString("menu_item_id"), category.getString("menu_item_description"));
                                alItems.add(mc);
                            }
                            aaItems.notifyDataSetChanged();
                        }
                    }
                    catch (JSONException e) {

                    }
                }
            });
        }
    }
}
