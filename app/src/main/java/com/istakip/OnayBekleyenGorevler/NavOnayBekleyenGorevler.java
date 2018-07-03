package com.istakip.OnayBekleyenGorevler;

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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
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
import android.widget.Toast;

import com.istakip.Navigation_Drawer;
import com.istakip.NetworkReceiver;
import com.istakip.R;
import com.istakip.TamamlananGorevler.NavTamamlananGorevler;
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

import static com.istakip.LoginScreen.loginStatus;

public class NavOnayBekleyenGorevler extends Fragment {

    public String kapat_gorev_deneme;
    View myView;
    String text_onay_gorev_id;
    String onay_gorev_id, onay_master_id, onay_slave_id, onay_gorevi_veren, onay_gorevi_alan,
            onay_gorev_adi, onay_oncelik_durumu, onay_gorev_detay, onay_tarih, onay_proje_id,
            onay_proje_adi, onay_gorev_referans, onay_profil_url, onay_bitis_tarihi;


    HashMap<String, String> hashOnay;
    ArrayList<HashMap<String, String>> contactListOnay;
    ListView listOnayGorev;
    RatingBar rating_Onay;
    LinearLayout nav_onay_linear_layout;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.activity_nav_onay_bekleyen_gorevler, container, false);

        contactListOnay = new ArrayList<>();
        listOnayGorev = (ListView) myView.findViewById(R.id.onay_list_id);
        rating_Onay = (RatingBar) myView.findViewById(R.id.ratingbar_onay);

        nav_onay_linear_layout = (LinearLayout) myView.findViewById(R.id.nav_onay_linear_layout);

        checkNetwork();

        swipeRefreshLayout = (SwipeRefreshLayout) myView.findViewById(R.id.swipe_onay);
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
                        contactListOnay.clear();
                        checkNetwork();
                    }
                }, 1000);
            }
        });


        return myView;
    }

    private void checkNetwork() {
        if (NetworkReceiver.getInstance(getContext()).isOnline()) {

            Log.v("Network Connection", "You are not online!!!!");

            new TamamlananWebService().execute();

        } else {
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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

    private class TamamlananWebService extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            String NAMESPACE = "http://tempuri.org/";
            String URL = "http://istakip.goktekinenerji.com/SERVISLER/WebServiceIsTakip.asmx";//Make sure you changed IP address
            String SOAP_ACTION = "http://tempuri.org/ListeleOnayBekleyenler";
            String METHOD_NAME = "ListeleOnayBekleyenler";

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            PropertyInfo masterIdPI = new PropertyInfo();
            PropertyInfo passPI = new PropertyInfo();

            masterIdPI.setName("Master_Id");
            masterIdPI.setValue(loginStatus);
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

                    onay_gorev_id = jsonArray.getJSONObject(i).getString("Gorev_Id");
                    onay_master_id = jsonArray.getJSONObject(i).getString("Master_Id");
                    onay_slave_id = jsonArray.getJSONObject(i).getString("Slave_Id");
                    onay_gorevi_veren = jsonArray.getJSONObject(i).getString("Gorevi_Veren");
                    onay_gorevi_alan = jsonArray.getJSONObject(i).getString("Gorevi_Alan");
                    onay_gorev_adi = jsonArray.getJSONObject(i).getString("Gorev_Adi");
                    onay_oncelik_durumu = jsonArray.getJSONObject(i).getString("Oncelik_Durumu");
                    onay_gorev_detay = jsonArray.getJSONObject(i).getString("Gorev_Detay");
                    onay_tarih = jsonArray.getJSONObject(i).getString("Tarih");
                    onay_proje_id = jsonArray.getJSONObject(i).getString("Proje_Id");
                    onay_proje_adi = jsonArray.getJSONObject(i).getString("Proje_Adi");
                    onay_gorev_referans = jsonArray.getJSONObject(i).getString("Gorev_Referans");
                    onay_profil_url = jsonArray.getJSONObject(i).getString("Profil_Url");
                    onay_bitis_tarihi = jsonArray.getJSONObject(i).getString("Bitis_Tarihi");

                    hashOnay = new HashMap<>();

                    hashOnay.put("Gorev_Id", onay_gorev_id);
                    hashOnay.put("Master_Id", onay_master_id);
                    hashOnay.put("Slave_Id", onay_slave_id);
                    hashOnay.put("Gorevi_Veren", onay_gorevi_veren);
                    hashOnay.put("Gorevi_Alan", onay_gorevi_alan);
                    hashOnay.put("Gorev_Adi", onay_gorev_adi);
                    hashOnay.put("Oncelik_Durumu", onay_oncelik_durumu);
                    hashOnay.put("Gorev_Detay", onay_gorev_detay);
                    hashOnay.put("Tarih", onay_tarih);
                    hashOnay.put("Proje_Id", onay_proje_id);
                    hashOnay.put("Proje_Adi", onay_proje_adi);
                    hashOnay.put("Gorev_Referans", onay_gorev_referans);
                    hashOnay.put("Profil_Url", onay_profil_url);
                    hashOnay.put("Bitis_Tarihi", onay_bitis_tarihi);

                    contactListOnay.add(hashOnay);
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
            nav_onay_linear_layout.setVisibility(View.VISIBLE);

            listOnayGorev.setOnScrollListener(new AbsListView.OnScrollListener() { //PAGE SCROLL NOT REFRESH
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    if (listOnayGorev.getChildAt(0) != null) {
                        swipeRefreshLayout.setEnabled(listOnayGorev.getFirstVisiblePosition() == 0 && listOnayGorev.getChildAt(0).getTop() == 0);
                    }
                }
            });

            listOnayGorev.setOnItemClickListener(new AdapterView.OnItemClickListener() { // LISTVIEW JUST CLICK
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {


                    final android.app.AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new android.app.AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new android.app.AlertDialog.Builder(getContext());
                    }

                    final HashMap<String, Object> obj = (HashMap<String, Object>) listOnayGorev.getItemAtPosition(position);


                    builder.setTitle("Onayla")
                            .setMessage("Emin Misiniz?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // String positionView = contactListOnay.get(position).toString();
                                    text_onay_gorev_id = (String) obj.get("Gorev_Id");
                                    new Gorev_KapatmaWebService().execute();
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .setIcon(R.drawable.nuclear_alert)
                            .setCancelable(false)
                            .show();
                }
            });
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            if (contactListOnay.isEmpty()) {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(getContext());
                }

                builder.setTitle("Oops")
                        .setMessage("Henüz onay bekleyen görev bulunmamaktadır!")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setIcon(R.drawable.nuclear_alert)
                        .setCancelable(false)
                        .show();
            }
            //Toast.makeText(getActivity(),contactListVerilen.toString(),Toast.LENGTH_LONG).show();
            ListAdapter listAdapter = new MyAdapterOnay(getActivity(), contactListOnay, R.layout.activity_list_onay_bekleyen_gorevler,
                    new String[]{"Gorevi_Alan", "Gorev_Adi", "Tarih", "Oncelik_Durumu"},
                    new int[]{R.id.onay_list_gorev_alan, R.id.onay_list_gorev_adi, R.id.onay_list_tarih, R.id.ratingbar_onay});
            ((MyAdapterOnay) listAdapter).setViewBinder(new MyBinder());
            listOnayGorev.setAdapter(listAdapter);

            nav_onay_linear_layout.setVisibility(View.GONE);
        }
    }

    private class Gorev_KapatmaWebService extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            String NAMESPACE = "http://tempuri.org/";
            String URL = "http://istakip.goktekinenerji.com/SERVISLER/WebServiceIsTakip.asmx";//Make sure you changed IP address
            String SOAP_ACTION = "http://tempuri.org/Gorev_Kapatma";
            String METHOD_NAME = "Gorev_Kapatma";

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            PropertyInfo uGorev_Id = new PropertyInfo();
            PropertyInfo passPI = new PropertyInfo();

            uGorev_Id.setName("Gorev_Id");
            uGorev_Id.setValue(text_onay_gorev_id);
            uGorev_Id.setType(String.class);

            request.addProperty(uGorev_Id);

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
                kapat_gorev_deneme = (response.toString());

            } catch (Exception e) {
                //LoginScreen.errored = true;
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (kapat_gorev_deneme.equals("0")) {

                Toast.makeText(getContext(), "Görev Onaylandı!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getContext(),Navigation_Drawer.class));

            } else {
                Toast.makeText(getContext(), "Lütfen Daha Sonra Tekrar Deneyiniz!", Toast.LENGTH_LONG).show();
            }
        }
    }

    public class MyAdapterOnay extends SimpleAdapter {

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
        public MyAdapterOnay(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            View v = super.getView(position, convertView, parent);

            ImageView img = (ImageView) v.getTag();
            if (img == null) {
                img = (ImageView) v.findViewById(R.id.onay_list_profil_url);
                v.setTag(img); // <<< THIS LINE !!!!
            }

            for (Object key : hashOnay.keySet()) {
                if (key.equals("Profil_Url")) { //Profil Url ye eşit oldugunda
                    Object url = ((Map) getItem(position)).get(key); // Keydeki degeri pozisyona gore ata
                    Picasso.with(v.getContext()).load("http://" + url).into(img); //Ekranda göster
                }
            }

            return v;
        }

    }

    private class MyBinder implements SimpleAdapter.ViewBinder { //Rating Bar update value from webservice
        @Override
        public boolean setViewValue(View view, Object data, String textRepresentation) {

            if (view.getId() == R.id.ratingbar_onay) {
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

