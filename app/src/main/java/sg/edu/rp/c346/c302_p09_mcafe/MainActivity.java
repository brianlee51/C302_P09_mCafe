package sg.edu.rp.c346.c302_p09_mcafe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    private ListView lvCat;
    ArrayList<MenuCategory> alCat = new ArrayList<MenuCategory>();
    ArrayAdapter<MenuCategory> aaCat;

    String user = "";
    String apikey = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        user = prefs.getString("id", "");
        apikey = prefs.getString("apikey","");

        lvCat = findViewById(R.id.lvCat);
        aaCat = new ArrayAdapter<MenuCategory>(this, android.R.layout.simple_list_item_1, alCat);
        lvCat.setAdapter(aaCat);

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.add("loginId", user);
        params.add("apikey", apikey);
        client.post("http://10.0.2.2/C302_P09_mCafe/getMenuCategories.php", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                try {
                    for (int i=0 ; i<response.length(); i++) {
                        JSONObject category = (JSONObject) response.get(i);
                        MenuCategory mc = new MenuCategory(category.getString("menu_item_category_id"), category.getString("menu_item_category_description"));
                        alCat.add(mc);
                    }
                    aaCat.notifyDataSetChanged();
                }
                catch (JSONException e) {

                }
            }
        });

        lvCat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MenuCategory mc = alCat.get(i);
                Intent intent = new Intent(getBaseContext(), DisplayMenuItemsActivity.class );
                intent.putExtra("category", mc);
                startActivity(intent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem item = menu.findItem(R.id.menu_add);
        item.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_add) {
            // Do Nothing
        } else if (id == R.id.menu_logout) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("id", "");
            editor.putString("apikey", "");
            editor.commit();
            finish();

        }
        return super.onOptionsItemSelected(item);
    }
}
