package sg.edu.rp.c346.c302_p09_mcafe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class editItemActivity extends AppCompatActivity {

    Button btnEdit, btnDelete;
    EditText etItem, etPrice;

    String user = "";
    String apikey = "";
    String itemID = "";

    private AsyncHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        etItem = findViewById(R.id.etItem);
        etPrice = findViewById(R.id.etPrice);
        btnEdit = findViewById(R.id.buttonAdd);
        btnDelete = findViewById(R.id.buttonDelete);
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(editItemActivity.this);
        user = prefs.getString("id", "");
        apikey = prefs.getString("apikey", "");

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        Intent i = getIntent();
        MenuCategory item = (MenuCategory) i.getSerializableExtra("item");
        itemID = item.getCategoryId();
        params.add("id", itemID);
        params.add("loginId", user);
        params.add("apikey", apikey);
        client.post("http://10.0.2.2/C302_P09_mCafe/getMenuItemById.php", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    etItem.setText(response.getString("menu_item_description"));
                    etPrice.setText(String.valueOf(response.getDouble("menu_item_unit_price")));
                } catch (JSONException e) {

                }
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String item = etItem.getText().toString();
                String price = etPrice.getText().toString();
                if (item.length() == 0 || price.length() == 0) {
                    Toast.makeText(getBaseContext(), "Cannot be empty", Toast.LENGTH_LONG).show();
                } else {
                    AsyncHttpClient client = new AsyncHttpClient();
                    RequestParams params = new RequestParams();
                    params.add("id", itemID);
                    params.add("loginId", user);
                    params.add("apikey", apikey);
                    params.add("description", item);
                    params.add("price", price);
                    client.post("http://10.0.2.2/C302_P09_mCafe/updateMenuItemById.php", params, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            try {
                                String message = response.getString("message");
                                Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
                                Intent i = new Intent();
                                setResult(RESULT_OK, i);
                                finish();
                            } catch (JSONException e) {

                            }
                        }
                    });
                }
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncHttpClient client = new AsyncHttpClient();
                RequestParams params = new RequestParams();
                params.add("id", itemID);
                params.add("loginId", user);
                params.add("apikey", apikey);
                client.post("http://10.0.2.2/C302_P09_mCafe/deleteMenuItemById.php", params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            String message = response.getString("message");
                            Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
                            Intent i = new Intent();
                            setResult(RESULT_OK, i);
                            finish();
                        } catch (JSONException e) {

                        }
                    }
                });

//                Toast.makeText(getBaseContext(), "Clicked", Toast.LENGTH_LONG).show();
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
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(editItemActivity.this);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("id", "");
            editor.putString("apikey", "");
            editor.commit();
            finish();

        }
        return super.onOptionsItemSelected(item);
    }
}
