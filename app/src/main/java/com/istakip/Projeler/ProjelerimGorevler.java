package com.istakip.Projeler;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SimpleAdapter;

import com.istakip.NetworkReceiver;
import com.istakip.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.istakip.Projeler.NavProjeler.text_projeler_proje_id;

public class ProjelerimGorevler extends AppCompatActivity {


    static String text_projeGorevReferans, text_projeOncelikDurum;
    String pojelerim_gorevler_Gorev_Id, pojelerim_gorevler_Master_Id, pojelerim_gorevler_Slave_Id, pojelerim_gorevler_Gorevi_Veren, pojelerim_gorevler_Gorevi_Alan,
            pojelerim_gorevler_Is_Admin, pojelerim_gorevler_Gorev_Adi, pojelerim_gorevler_Oncelik_Durumu, pojelerim_gorevler_Gorev_Detay, pojelerim_gorevler_Tarih,
            pojelerim_gorevler_Bitis_Tarihi, pojelerim_gorevler_Proje_Id, pojelerim_gorevler_Proje_Adi, pojelerim_gorevler_Gorev_Referans, pojelerim_gorevler_Okundu,
            pojelerim_gorevler_Profil_Url, pojelerim_gorevler_Beklemede, pojelerim_gorevler_Kapandi, pojelerim_gorevler_Kapatma_Bekliyor;

    ArrayList<HashMap<String, String>> contactListProjelerGorev;
    HashMap<String, String> hashProjelerGorev;
    ListView listProjelerGorev;

    RatingBar rating_ProjelerGorev;
    LinearLayout nav_projelerGorev_linear_layout;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projelerim_gorevler);

        contactListProjelerGorev = new ArrayList<>();
        listProjelerGorev = (ListView) findViewById(R.id.projelerim_gorevler_list_id);

        rating_ProjelerGorev = (RatingBar) findViewById(R.id.ratingbar_projelerimGorev);
        nav_projelerGorev_linear_layout = (LinearLayout) findViewById(R.id.nav_projelerim_gorevler_linear_layout);

        checkNetwork();

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_projelerim_gorevler);
        swipeRefreshLayout.setColorSchemeResources(R.color.darkred, R.color.colorBlue, R.color.colorpurple);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                Log.d("Swipe", "Refresh Number");
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        contactListProjelerGorev.clear();
                        checkNetwork();
                    }
                }, 1000);
            }
        });
    }

    private void checkNetwork() {
        if (NetworkReceiver.getInstance(ProjelerimGorevler.this).isOnline()) {

            Log.v("Network Connection", "You are not online!!!!");
            new JListeleGorevlerimProjeyeGore().execute();

        } else {
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                builder = new AlertDialog.Builder(ProjelerimGorevler.this, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(ProjelerimGorevler.this);
            }

            builder.setTitle("Hata")
                    .setMessage("İnternet bağlantısı yok!")
                    .setPositiveButton("Tekrar Dene", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            checkNetwork();
                            dialog.cancel();
                        }
                    }).setNegativeButton("Kapat", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ProjelerimGorevler.this.getFragmentManager().popBackStack();
                    System.exit(0);
                }
            }).setIcon(R.drawable.nuclear_alert)
                    .setCancelable(false)
                    .show();

            Log.e("Network Connection", "############################You are not online!!!!");
        }
    }

    public class JListeleGorevlerimProjeyeGore extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {

            String NAMESPACE = "http://tempuri.org/";
            String URL = "http://istakip.goktekinenerji.com/SERVISLER/WebServiceIsTakip.asmx";//Make sure you changed IP address
            String SOAP_ACTION = "http://tempuri.org/JListeleGorevlerimProjeyeGore";
            String METHOD_NAME = "JListeleGorevlerimProjeyeGore";

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            PropertyInfo masterIdPI = new PropertyInfo();
            PropertyInfo passPI = new PropertyInfo();

            masterIdPI.setName("Proje_Id");
            masterIdPI.setValue(text_projeler_proje_id);
            masterIdPI.setType(Integer.class);

            request.addProperty(masterIdPI);

            passPI.setName("Pass");
            passPI.setValue("haloX34fcidj");
            passPI.setType(String.class);

            request.addProperty(passPI);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE httpTransportSE = new HttpTransportSE(URL);
            httpTransportSE.debug = true;

            try {
                httpTransportSE.call(SOAP_ACTION, envelope);

                SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
                String responseJson = response.toString();
                JSONArray jsonArray = new JSONArray(responseJson);

                for (int i = 0; i < responseJson.length(); i++) {

                    pojelerim_gorevler_Gorev_Id = jsonArray.getJSONObject(i).getString("Gorev_Id");
                    pojelerim_gorevler_Master_Id = jsonArray.getJSONObject(i).getString("Master_Id");
                    pojelerim_gorevler_Slave_Id = jsonArray.getJSONObject(i).getString("Slave_Id");
                    pojelerim_gorevler_Gorevi_Veren = jsonArray.getJSONObject(i).getString("Gorevi_Veren");
                    pojelerim_gorevler_Gorevi_Alan = jsonArray.getJSONObject(i).getString("Gorevi_Alan");
                    pojelerim_gorevler_Is_Admin = jsonArray.getJSONObject(i).getString("Is_Admin");
                    pojelerim_gorevler_Gorev_Adi = jsonArray.getJSONObject(i).getString("Gorev_Adi");
                    pojelerim_gorevler_Oncelik_Durumu = jsonArray.getJSONObject(i).getString("Oncelik_Durumu");
                    pojelerim_gorevler_Gorev_Detay = jsonArray.getJSONObject(i).getString("Gorev_Detay");
                    pojelerim_gorevler_Tarih = jsonArray.getJSONObject(i).getString("Tarih");
                    pojelerim_gorevler_Bitis_Tarihi = jsonArray.getJSONObject(i).getString("Bitis_Tarihi");
                    pojelerim_gorevler_Proje_Id = jsonArray.getJSONObject(i).getString("Proje_Id");
                    pojelerim_gorevler_Proje_Adi = jsonArray.getJSONObject(i).getString("Proje_Adi");
                    pojelerim_gorevler_Gorev_Referans = jsonArray.getJSONObject(i).getString("Gorev_Referans");
                    pojelerim_gorevler_Okundu = jsonArray.getJSONObject(i).getString("Okundu");
                    pojelerim_gorevler_Profil_Url = jsonArray.getJSONObject(i).getString("Profil_Url");
                    pojelerim_gorevler_Beklemede = jsonArray.getJSONObject(i).getString("Beklemede");
                    pojelerim_gorevler_Kapandi = jsonArray.getJSONObject(i).getString("Kapandi");
                    pojelerim_gorevler_Kapatma_Bekliyor = jsonArray.getJSONObject(i).getString("Kapatma_Bekliyor");


                    hashProjelerGorev = new HashMap<>();

                    hashProjelerGorev.put("Gorev_Id", pojelerim_gorevler_Gorev_Id);
                    hashProjelerGorev.put("Master_Id", pojelerim_gorevler_Master_Id);
                    hashProjelerGorev.put("Slave_Id", pojelerim_gorevler_Slave_Id);
                    hashProjelerGorev.put("Gorevi_Veren", pojelerim_gorevler_Gorevi_Veren);
                    hashProjelerGorev.put("Gorevi_Alan", pojelerim_gorevler_Gorevi_Alan);
                    hashProjelerGorev.put("Is_Admin", pojelerim_gorevler_Is_Admin);
                    hashProjelerGorev.put("Gorev_Adi", pojelerim_gorevler_Gorev_Adi);
                    hashProjelerGorev.put("Oncelik_Durumu", pojelerim_gorevler_Oncelik_Durumu);
                    hashProjelerGorev.put("Gorev_Detay", pojelerim_gorevler_Gorev_Detay);
                    hashProjelerGorev.put("Tarih", pojelerim_gorevler_Tarih);
                    hashProjelerGorev.put("Bitis_Tarihi", pojelerim_gorevler_Bitis_Tarihi);
                    hashProjelerGorev.put("Proje_Id", pojelerim_gorevler_Proje_Id);
                    hashProjelerGorev.put("Proje_Adi", pojelerim_gorevler_Proje_Adi);
                    hashProjelerGorev.put("Gorev_Referans", pojelerim_gorevler_Gorev_Referans);
                    hashProjelerGorev.put("Okundu", pojelerim_gorevler_Okundu);
                    hashProjelerGorev.put("Profil_Url", pojelerim_gorevler_Profil_Url);
                    hashProjelerGorev.put("Beklemede", pojelerim_gorevler_Beklemede);
                    hashProjelerGorev.put("Kapandi", pojelerim_gorevler_Kapandi);
                    hashProjelerGorev.put("Kapatma_Bekliyor", pojelerim_gorevler_Kapatma_Bekliyor);

                    contactListProjelerGorev.add(hashProjelerGorev);
                }

            } catch (IOException e) {
                Log.e("1", "IOException");
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                Log.e("2", "XmlPullParserException");
                e.printStackTrace();
            } catch (JSONException e) {
                Log.e("3", "JSONException");
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (contactListProjelerGorev.isEmpty()) {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                    builder = new AlertDialog.Builder(ProjelerimGorevler.this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(ProjelerimGorevler.this);
                }

                builder.setTitle("Oops")
                        .setMessage("Henüz hiçbir görev bulunmamaktadır!")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setIcon(R.drawable.nuclear_alert)
                        .setCancelable(false)
                        .show();
            }
            ListAdapter listAdapter = new MyAdapterProjeGorev(ProjelerimGorevler.this, contactListProjelerGorev, R.layout.activity_list_projelerim_gorevler,
                    new String[]{"Gorevi_Alan", "Gorev_Adi", "Tarih", "Oncelik_Durumu"},
                    new int[]{R.id.projelerimGorev_list_gorev_veren, R.id.projelerimGorev_list_gorev_adi, R.id.projelerimGorev_list_tarih, R.id.ratingbar_projelerimGorev});
            ((MyAdapterProjeGorev) listAdapter).setViewBinder(new MyBinder());
            listProjelerGorev.setAdapter(listAdapter);

            nav_projelerGorev_linear_layout.setVisibility(View.GONE);
        }

        @Override
        protected void onPreExecute() {

            nav_projelerGorev_linear_layout.setVisibility(View.GONE);

            listProjelerGorev.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    if (listProjelerGorev.getChildAt(0) != null) {
                        swipeRefreshLayout.setEnabled(listProjelerGorev.getFirstVisiblePosition() == 0 && listProjelerGorev.getChildAt(0).getTop() == 0);
                    }
                }
            });

            listProjelerGorev.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    HashMap<String, Object> obj = (HashMap<String, Object>) listProjelerGorev.getItemAtPosition(position);

                    text_projeGorevReferans = (String) obj.get("Gorev_Referans");
                    text_projeOncelikDurum = (String) obj.get("Oncelik_Durumu");

                    startActivity(new Intent(ProjelerimGorevler.this, ProjeIsAkisi.class));

                }
            });
        }
    }

    public class MyAdapterProjeGorev extends SimpleAdapter {
        /**
         * Constructor
         *
         * @param context  The context where the View associated with this SimpleAdapter is running
         * @param data     A List of Maps. Each entry in the List corresponds to one row in the list. The
         *                 Maps contain the data for each row, and should include all the entries specified in
         *                 "from"
         * @param resource Resource identifier of a view layout that defines the views for this list
         *                 item. The layout file should include at least those named views defined in "to"
         * @param from     A list of column names that will be added to the Map associated with each
         *                 item.
         * @param to       The views that should display column in the "from" parameter. These should all be
         *                 TextViews. The first N views in this list are given the values of the first N columns
         */
        public MyAdapterProjeGorev(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);

            ImageView img = (ImageView) v.getTag();
            if (img == null) {
                img = (ImageView) v.findViewById(R.id.projelerimGorev_list_profil_url);
                v.setTag(img);
            }

            for (Object key : hashProjelerGorev.keySet()) {
                if (key.equals("Profil_Url")) {
                    Object url = ((Map) getItem(position)).get(key);
                    Picasso.with(v.getContext()).load("http://" + url).into(img);
                }
            }
            return v;
        }
    }

    private class MyBinder implements SimpleAdapter.ViewBinder {
        @Override
        public boolean setViewValue(View view, Object data, String textRepresentation) {

            if (view.getId() == R.id.ratingbar_projelerimGorev) {
                String stringVal = (String) data;
                float ratingValue = Float.parseFloat(stringVal);
                RatingBar ratingBar = (RatingBar) view;
                ratingBar.setRating(ratingValue);
                LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
                stars.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
                return true;
            }
            return false;
        }
    }
}
