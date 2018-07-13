package com.istakip.TamamlananGorevler;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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

import static com.istakip.LoginWebServiceQuery.Astatus;

public class NavTamamlananGorevler extends Fragment {
    View myView;

    String tamamlanan_gorev_id, tamamlanan_master_id, tamamlanan_slave_id, tamamlanan_gorevi_veren, tamamlanan_gorevi_alan,
            tamamlanan_gorev_adi, tamamlanan_oncelik_durumu, tamamlanan_gorev_detay, tamamlanan_tarih, tamamlanan_referans,
            tamamlanan_proje_id, tamamlanan_proje_adi, tamamlanan_gorev_referans, tamamlanan_profil_url,tamamlanan_bitis_tarihi;

    static String text_tamamlanan_gorev_alan, text_tamamlanan_gorev_adi, text_tamamlanan_oncelik_durumu,
            text_tamamlanan_gorev_detay, text_tamamlanan_tarih, text_tamamlanan_proje_id, text_tamamlanan_gorev_id,
            text_tamamlanan_slave_id, text_tamamlanan_gorev_referans,text_tamamlanan_bitisTarih;

    HashMap<String, String> hashTamamlanan;
    ArrayList<HashMap<String, String>> contactListTamamlanan;

    ListView listTamamlananGorev;
    RatingBar rating_tamamlanan;
    LinearLayout nav_tamamlanan_linear_layout;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        myView = inflater.inflate(R.layout.activity_nav_tamamlanan_gorevler, container, false);

        contactListTamamlanan = new ArrayList<>();
        listTamamlananGorev = (ListView) myView.findViewById(R.id.tamamlanan_gorevlerim_list_id);
        rating_tamamlanan = (RatingBar) myView.findViewById(R.id.ratingbar_tamamlanan);

        nav_tamamlanan_linear_layout = (LinearLayout) myView.findViewById(R.id.nav_tamamlanan_linear_layout);

        checkNetwork();

        swipeRefreshLayout = (SwipeRefreshLayout) myView.findViewById(R.id.swipe_tamamlanan);
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
                        contactListTamamlanan.clear();
                        checkNetwork();
                    }
                }, 1000);
            }
        });
        return myView;
    }

    private void checkNetwork() {

        if (NetworkReceiver.getInstance(getContext()).isOnline()) {

            Log.v("Network Connection", "You are online!!!!");
            new TamamlananWebService().execute();

        } else {
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(getContext());
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
                    getActivity().getFragmentManager().popBackStack();
                    System.exit(0);
                }
            }).setIcon(R.drawable.nuclear_alert)
                    .setCancelable(false)
                    .show();

            Log.e("Network Connection", "############################You are not online!!!!");
        }
    }

    private class TamamlananWebService extends AsyncTask<String, Void, Void>{
        @Override
        protected Void doInBackground(String... strings) {
            String NAMESPACE = "http://tempuri.org/";
            String URL = "http://istakip.goktekinenerji.com/SERVISLER/WebServiceIsTakip.asmx";//Make sure you changed IP address
            String SOAP_ACTION = "http://tempuri.org/ListeleTamamlananGorevler";
            String METHOD_NAME = "ListeleTamamlananGorevler";

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            PropertyInfo masterIdPI = new PropertyInfo();
            PropertyInfo passPI = new PropertyInfo();

            masterIdPI.setName("Master_Id");
            masterIdPI.setValue(Astatus);
            masterIdPI.setType(String.class);

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
                String responseJSON = response.toString();
                JSONArray jsonArray = new JSONArray(responseJSON);

                for (int i = 0; i < responseJSON.length(); i++) {

                    tamamlanan_master_id = jsonArray.getJSONObject(i).getString("Master_Id");
                    tamamlanan_gorev_id = jsonArray.getJSONObject(i).getString("Gorev_Id");
                    tamamlanan_slave_id = jsonArray.getJSONObject(i).getString("Slave_Id");
                    tamamlanan_gorevi_veren = jsonArray.getJSONObject(i).getString("Gorevi_Veren");
                    tamamlanan_gorevi_alan = jsonArray.getJSONObject(i).getString("Gorevi_Alan");
                    tamamlanan_gorev_adi = jsonArray.getJSONObject(i).getString("Gorev_Adi");
                    tamamlanan_oncelik_durumu = jsonArray.getJSONObject(i).getString("Oncelik_Durumu");
                    tamamlanan_gorev_detay = jsonArray.getJSONObject(i).getString("Gorev_Detay");
                    tamamlanan_tarih = jsonArray.getJSONObject(i).getString("Tarih");
                    tamamlanan_proje_id = jsonArray.getJSONObject(i).getString("Proje_Id");
                    tamamlanan_proje_adi = jsonArray.getJSONObject(i).getString("Proje_Adi");
                    tamamlanan_gorev_referans = jsonArray.getJSONObject(i).getString("Gorev_Referans");
                    tamamlanan_profil_url = jsonArray.getJSONObject(i).getString("Profil_Url");
                    tamamlanan_bitis_tarihi = jsonArray.getJSONObject(i).getString("Bitis_Tarihi");

                    hashTamamlanan = new HashMap<>();

                    hashTamamlanan.put("Master_Id", tamamlanan_master_id);
                    hashTamamlanan.put("Gorev_Id", tamamlanan_gorev_id);
                    hashTamamlanan.put("Slave_Id", tamamlanan_slave_id);
                    hashTamamlanan.put("Gorevi_Veren", tamamlanan_gorevi_veren);
                    hashTamamlanan.put("Gorevi_Alan", tamamlanan_gorevi_alan);
                    hashTamamlanan.put("Gorev_Adi", tamamlanan_gorev_adi);
                    hashTamamlanan.put("Oncelik_Durumu", tamamlanan_oncelik_durumu);
                    hashTamamlanan.put("Gorev_Detay", tamamlanan_gorev_detay);
                    hashTamamlanan.put("Tarih", tamamlanan_tarih);
                    hashTamamlanan.put("Proje_Id", tamamlanan_proje_id);
                    hashTamamlanan.put("Proje_Adi", tamamlanan_proje_adi);
                    hashTamamlanan.put("Gorev_Referans", tamamlanan_gorev_referans);
                    hashTamamlanan.put("Profil_Url", tamamlanan_profil_url);
                    hashTamamlanan.put("Bitis_Tarihi", tamamlanan_bitis_tarihi);

                    contactListTamamlanan.add(hashTamamlanan);
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
        protected void onPreExecute() {
            nav_tamamlanan_linear_layout.setVisibility(View.VISIBLE);

            listTamamlananGorev.setOnScrollListener(new AbsListView.OnScrollListener() { //PAGE SCROLL NOT REFRESH
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    if (listTamamlananGorev.getChildAt(0) != null) {
                        swipeRefreshLayout.setEnabled(listTamamlananGorev.getFirstVisiblePosition() == 0 && listTamamlananGorev.getChildAt(0).getTop() == 0);
                    }
                }
            });

            listTamamlananGorev.setOnItemClickListener(new AdapterView.OnItemClickListener() { // LISTVIEW JUST CLICK
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    HashMap<String, Object> obj = (HashMap<String, Object>) listTamamlananGorev.getItemAtPosition(position);

                    text_tamamlanan_gorev_alan = (String) obj.get("Gorevi_Alan");
                    text_tamamlanan_gorev_adi = (String) obj.get("Gorev_Adi");
                    text_tamamlanan_oncelik_durumu = (String) obj.get("Oncelik_Durumu");
                    text_tamamlanan_gorev_detay = (String) obj.get("Gorev_Detay");
                    text_tamamlanan_tarih = (String) obj.get("Tarih");
                    text_tamamlanan_gorev_id = (String) obj.get("Gorev_Id");
                    text_tamamlanan_slave_id = (String) obj.get("Slave_Id");
                    text_tamamlanan_gorev_referans = (String) obj.get("Gorev_Referans");
                    text_tamamlanan_proje_id = (String) obj.get("Proje_Id") ;
                    text_tamamlanan_bitisTarih = (String) obj.get("Bitis_Tarihi");
                    startActivity(new Intent(getActivity(), TamamlananIsAkisi.class));
                }
            });
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            if (contactListTamamlanan.isEmpty()) {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                    builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(getContext());
                }

                builder.setTitle("Oops")
                        .setMessage("Henüz hiçbir görev tamamlanmadı!")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setIcon(R.drawable.nuclear_alert)
                        .setCancelable(false)
                        .show();
            }
            ListAdapter listAdapter = new MyAdapterTamamlanan(getActivity(), contactListTamamlanan, R.layout.activity_list_tamamlanan_gorevler,
                    new String[]{"Gorevi_Alan", "Gorev_Adi", "Tarih", "Oncelik_Durumu"},
                    new int[]{R.id.tamamlanan_list_gorev_alan, R.id.tamamlanan_list_gorev_adi, R.id.tamamlanan_list_tarih, R.id.ratingbar_tamamlanan});
            ((MyAdapterTamamlanan) listAdapter).setViewBinder(new MyBinder());
            listTamamlananGorev.setAdapter(listAdapter);

            nav_tamamlanan_linear_layout.setVisibility(View.GONE);
        }
    }
    public class MyAdapterTamamlanan extends SimpleAdapter {
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
        public MyAdapterTamamlanan(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);

            ImageView img = (ImageView) v.getTag();
            if (img == null) {
                img = (ImageView) v.findViewById(R.id.tamamlanan_list_profil_url);
                v.setTag(img);
            }

            for (Object key : hashTamamlanan.keySet()) {
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

            if (view.getId() == R.id.ratingbar_tamamlanan) {
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
