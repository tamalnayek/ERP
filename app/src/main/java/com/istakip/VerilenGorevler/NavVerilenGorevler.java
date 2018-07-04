package com.istakip.VerilenGorevler;

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

public class NavVerilenGorevler extends Fragment {

    static String text_verilen_gorev_alan, text_verilen_gorev_adi, text_verilen_oncelik_durumu,
            text_verilen_gorev_detay, text_verilen_tarih, text_verilen_proje_id, text_verilen_gorev_id,
            text_verilen_slave_id, text_verilen_gorev_referans,text_verilen_bitisTarih;

    String verdigim_gorev_id, verdigim_master_id, verdigim_slave_id, verdigim_gorevi_veren, verdigim_gorevi_alan,
            verdigim_gorev_adi, verdigim_oncelik_durumu, verdigim_gorev_detay, verdigim_tarih,
            verdigim_proje_id, verdigim_proje_adi, verdigim_gorev_referans, verdigim_profil_url,verdigim_bitis_tarihi;

    ArrayList<HashMap<String, String>> contactListVerilen;
    HashMap<String, String> hashverdigim;

    View myView;

    LinearLayout nav_verilen_linear_layout;
    SwipeRefreshLayout swipeRefreshLayout;
    ListView listVerilenGorev;
    RatingBar rating_verilen;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        myView = inflater.inflate(R.layout.activity_nav_verilen_gorevler, container, false);

        contactListVerilen = new ArrayList<>();
        listVerilenGorev = (ListView) myView.findViewById(R.id.verilen_gorevlerim_list_id);

        rating_verilen = (RatingBar) myView.findViewById(R.id.ratingbar_verilen);

        nav_verilen_linear_layout = (LinearLayout) myView.findViewById(R.id.nav_verilen_linear_layout);

        checkNetwork();

        swipeRefreshLayout = (SwipeRefreshLayout) myView.findViewById(R.id.swipe_verilen);
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
                        contactListVerilen.clear();
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
            new WebServiceVerilenGorev().execute();

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

    public class WebServiceVerilenGorev extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            String NAMESPACE = "http://tempuri.org/";
            String URL = "http://istakip.goktekinenerji.com/SERVISLER/WebServiceIsTakip.asmx";//Make sure you changed IP address
            String SOAP_ACTION = "http://tempuri.org/ListeleVerdigimGorevler";
            String METHOD_NAME = "ListeleVerdigimGorevler";

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            PropertyInfo masterIdPI = new PropertyInfo();
            PropertyInfo passPI = new PropertyInfo();

            masterIdPI.setName("Master_Id");
            masterIdPI.setValue(Astatus);
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

                    verdigim_master_id = jsonArray.getJSONObject(i).getString("Master_Id");
                    verdigim_gorev_id = jsonArray.getJSONObject(i).getString("Gorev_Id");
                    verdigim_slave_id = jsonArray.getJSONObject(i).getString("Slave_Id");
                    verdigim_gorevi_veren = jsonArray.getJSONObject(i).getString("Gorevi_Veren");
                    verdigim_gorevi_alan = jsonArray.getJSONObject(i).getString("Gorevi_Alan");
                    verdigim_gorev_adi = jsonArray.getJSONObject(i).getString("Gorev_Adi");
                    verdigim_oncelik_durumu = jsonArray.getJSONObject(i).getString("Oncelik_Durumu");
                    verdigim_gorev_detay = jsonArray.getJSONObject(i).getString("Gorev_Detay");
                    verdigim_tarih = jsonArray.getJSONObject(i).getString("Tarih");
                    verdigim_proje_id = jsonArray.getJSONObject(i).getString("Proje_Id");
                    verdigim_proje_adi = jsonArray.getJSONObject(i).getString("Proje_Adi");
                    verdigim_gorev_referans = jsonArray.getJSONObject(i).getString("Gorev_Referans");
                    verdigim_profil_url = jsonArray.getJSONObject(i).getString("Profil_Url");
                    verdigim_bitis_tarihi = jsonArray.getJSONObject(i).getString("Bitis_Tarihi");

                    hashverdigim = new HashMap<>();

                    hashverdigim.put("Master_Id", verdigim_master_id);
                    hashverdigim.put("Gorev_Id", verdigim_gorev_id);
                    hashverdigim.put("Slave_Id", verdigim_slave_id);
                    hashverdigim.put("Gorevi_Veren", verdigim_gorevi_veren);
                    hashverdigim.put("Gorevi_Alan", verdigim_gorevi_alan);
                    hashverdigim.put("Gorev_Adi", verdigim_gorev_adi);
                    hashverdigim.put("Oncelik_Durumu", verdigim_oncelik_durumu);
                    hashverdigim.put("Gorev_Detay", verdigim_gorev_detay);
                    hashverdigim.put("Tarih", verdigim_tarih);
                    hashverdigim.put("Proje_Id", verdigim_proje_id);
                    hashverdigim.put("Proje_Adi", verdigim_proje_adi);
                    hashverdigim.put("Gorev_Referans", verdigim_gorev_referans);
                    hashverdigim.put("Profil_Url", verdigim_profil_url);
                    hashverdigim.put("Bitis_Tarihi", verdigim_bitis_tarihi);

                    contactListVerilen.add(hashverdigim);
                }

            } catch (IOException e) {
                Log.e("1", "do");
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                Log.e("2", "do");
                e.printStackTrace();
            } catch (JSONException e) {
                Log.e("3", "do");
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            if (contactListVerilen.isEmpty()) {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(getContext());
                }

                builder.setTitle("Oops")
                        .setMessage("Henüz hiçbir görev vermediniz!")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setIcon(R.drawable.nuclear_alert)
                        .setCancelable(false)
                        .show();
            }
            ListAdapter listAdapter = new MyAdapterVerilen(getActivity(), contactListVerilen, R.layout.activity_list_verilen_gorevler,
                    new String[]{"Gorevi_Alan", "Gorev_Adi", "Tarih", "Oncelik_Durumu"},
                    new int[]{R.id.verilen_list_gorev_alan, R.id.verilen_list_gorev_adi, R.id.verilen_list_tarih, R.id.ratingbar_verilen});
            ((MyAdapterVerilen) listAdapter).setViewBinder(new MyBinder());
            listVerilenGorev.setAdapter(listAdapter);

            nav_verilen_linear_layout.setVisibility(View.GONE);
        }

        @Override
        protected void onPreExecute() {
            nav_verilen_linear_layout.setVisibility(View.VISIBLE);

            listVerilenGorev.setOnScrollListener(new AbsListView.OnScrollListener() { //PAGE SCROLL NOT REFRESH
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    if (listVerilenGorev.getChildAt(0) != null) {
                        swipeRefreshLayout.setEnabled(listVerilenGorev.getFirstVisiblePosition() == 0 && listVerilenGorev.getChildAt(0).getTop() == 0);
                    }
                }
            });

            listVerilenGorev.setOnItemClickListener(new AdapterView.OnItemClickListener() { // LISTVIEW JUST CLICK
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    HashMap<String, Object> obj = (HashMap<String, Object>) listVerilenGorev.getItemAtPosition(position);

                    text_verilen_gorev_alan = (String) obj.get("Gorevi_Alan");
                    text_verilen_gorev_adi = (String) obj.get("Gorev_Adi");
                    text_verilen_oncelik_durumu = (String) obj.get("Oncelik_Durumu");
                    text_verilen_gorev_detay = (String) obj.get("Gorev_Detay");
                    text_verilen_tarih = (String) obj.get("Tarih");
                    text_verilen_gorev_id = (String) obj.get("Gorev_Id");
                    text_verilen_slave_id = (String) obj.get("Slave_Id");
                    text_verilen_gorev_referans = (String) obj.get("Gorev_Referans");
                    startActivity(new Intent(getActivity(), VerilenIsAkislari.class));
                }
            });

            listVerilenGorev.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() { //LISTVIEW LONG CLICK
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                    HashMap<String, Object> objLong = (HashMap<String, Object>) listVerilenGorev.getItemAtPosition(position);

                    text_verilen_slave_id = (String) objLong.get("Slave_Id");
                    text_verilen_gorev_adi = (String) objLong.get("Gorev_Adi");
                    text_verilen_gorev_detay = (String) objLong.get("Gorev_Detay");
                    text_verilen_oncelik_durumu = (String) objLong.get("Oncelik_Durumu");
                    text_verilen_proje_id = (String) objLong.get("Proje_Id");
                    text_verilen_gorev_id = (String) objLong.get("Gorev_Id");
                    text_verilen_gorev_alan = (String) objLong.get("Gorevi_Alan");
                    text_verilen_tarih = (String) objLong.get("Tarih");
                    text_verilen_bitisTarih= (String) objLong.get("Bitis_Tarihi");

                    startActivity(new Intent(getActivity(), NavVerilenGorevlerDetail.class));
                    return true;
                }
            });
        }
    }

    public class MyAdapterVerilen extends SimpleAdapter {
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
        public MyAdapterVerilen(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);

            ImageView img = (ImageView) v.getTag();
            if (img == null) {
                img = (ImageView) v.findViewById(R.id.verilen_list_profil_url);
                v.setTag(img);
            }

            for (Object key : hashverdigim.keySet()) {
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

            if (view.getId() == R.id.ratingbar_verilen) {
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
