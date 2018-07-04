package com.istakip.AlinanGorevler;

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

public class NavAlinanGorevlerim extends Fragment {

    static String aldigim_gorevId, aldigim_masterId, aldigim_slaveId, aldigim_gorevVeren, aldigim_gorevAlan, aldigim_gorevAdi, aldigim_oncelikDurumu,
            aldigim_gorevDetay, aldigim_tarih, aldigim_projeAdi, aldigim_projeId, aldigim_gorevReferans, aldigim_profil_URL;
    static String text_gorevVeren, text_gorevGorevId, text_gorevIsGorevId, textSlaveId, text_gorevAdi, text_gorevDetay, text_oncelikDurumu, text_tarih, text_gorevReferans;

    static ArrayList<HashMap<String, String>> contactList, urlList;

    HashMap<String, String> contact;
    HashMap<String, String> contact_url;

    View myView;

    ListView listView;

    LinearLayout nav_alinan_linear_layout;
    SwipeRefreshLayout swipeRefreshLayout;
    RatingBar ratingbar_alinan;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.activity_nav_alinan_gorevlerim, container, false);

        contactList = new ArrayList<>();
        urlList = new ArrayList<>();

        ratingbar_alinan = (RatingBar) myView.findViewById(R.id.ratingbar_alinan);

        listView = (ListView) myView.findViewById(R.id.gorevlerim_list_id);

        nav_alinan_linear_layout = (LinearLayout) myView.findViewById(R.id.nav_alinan_linear_layout);

        checkNetwork();

        swipeRefreshLayout = (SwipeRefreshLayout) myView.findViewById(R.id.swipe_alinan);
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
                        contactList.clear();
                        checkNetwork();
                    }
                }, 1000);
            }
        });

        return myView;
    }

    private void checkNetwork() {

        if (NetworkReceiver.getInstance(getContext()).isOnline()) {

            Log.v("Network Connection", "You not online!!!!");
            new WebService().execute();

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

    private class WebService extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            String NAMESPACE = "http://tempuri.org/";
            String URL = "http://istakip.goktekinenerji.com/SERVISLER/WebServiceIsTakip.asmx";//Make sure you changed IP address
            String SOAP_ACTION = "http://tempuri.org/JListeleAlinanGorevlerim";
            String METHOD_NAME = "JListeleAlinanGorevlerim";

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            PropertyInfo masterIdPI = new PropertyInfo();
            PropertyInfo passPI = new PropertyInfo();

            masterIdPI.setName("Slave_Id");
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

                    aldigim_gorevId = jsonArray.getJSONObject(i).getString("Gorev_Id");
                    aldigim_masterId = jsonArray.getJSONObject(i).getString("Master_Id");
                    aldigim_slaveId = jsonArray.getJSONObject(i).getString("Slave_Id");
                    aldigim_gorevVeren = jsonArray.getJSONObject(i).getString("Gorevi_Veren");
                    aldigim_gorevAlan = jsonArray.getJSONObject(i).getString("Gorevi_Alan");
                    aldigim_gorevAdi = jsonArray.getJSONObject(i).getString("Gorev_Adi");
                    aldigim_oncelikDurumu = jsonArray.getJSONObject(i).getString("Oncelik_Durumu");
                    aldigim_gorevDetay = jsonArray.getJSONObject(i).getString("Gorev_Detay");
                    aldigim_tarih = jsonArray.getJSONObject(i).getString("Tarih");
                    aldigim_projeId = jsonArray.getJSONObject(i).getString("Proje_Id");
                    aldigim_projeAdi = jsonArray.getJSONObject(i).getString("Proje_Adi");
                    aldigim_gorevReferans = jsonArray.getJSONObject(i).getString("Gorev_Referans");
                    aldigim_profil_URL = jsonArray.getJSONObject(i).getString("Profil_Url");

                    contact = new HashMap<>();
                    contact_url = new HashMap<>();

                    contact.put("Gorev_Id", aldigim_gorevId);
                    contact.put("Master_Id", aldigim_masterId);
                    contact.put("Slave_Id", aldigim_slaveId);
                    contact.put("Gorevi_Veren", aldigim_gorevVeren);
                    contact.put("Gorevi_Alan", aldigim_gorevAlan);
                    contact.put("Gorev_Adi", aldigim_gorevAdi);
                    contact.put("Oncelik_Durumu", aldigim_oncelikDurumu);
                    contact.put("Gorev_Detay", aldigim_gorevDetay);
                    contact.put("Tarih", aldigim_tarih);
                    contact.put("Proje_Id", aldigim_projeId);
                    contact.put("Proje_Adi", aldigim_projeAdi);
                    contact.put("Gorev_Referans", aldigim_gorevReferans);
                    contact.put("Profil_Url", aldigim_profil_URL);

                    contactList.add(contact);
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

            if (contactList.isEmpty()) {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(getContext());
                }

                builder.setTitle("Oops")
                        .setMessage("Henüz hiçbir görev almadınız!")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setIcon(R.drawable.nuclear_alert)
                        .setCancelable(false)
                        .show();
            }

            ListAdapter adapter = new MyAdapter(getActivity(), contactList,
                    R.layout.activity_list_alinan_gorevler, new String[]{"Gorevi_Veren", "Gorev_Adi", "Tarih", "Oncelik_Durumu"},
                    new int[]{R.id.alinan_list_gorev_veren, R.id.alinan_list_gorev_adi, R.id.alinan_list_tarih, R.id.ratingbar_alinan});
            ((MyAdapter) adapter).setViewBinder(new MyBinder());
            listView.setAdapter(adapter);

            nav_alinan_linear_layout.setVisibility(View.GONE);

        }

        @Override
        protected void onPreExecute() {
            nav_alinan_linear_layout.setVisibility(View.VISIBLE);

            listView.setOnScrollListener(new AbsListView.OnScrollListener() { //PAGE SCROLL NOT REFRESH
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    if (listView.getChildAt(0) != null) {
                        swipeRefreshLayout.setEnabled(listView.getFirstVisiblePosition() == 0 && listView.getChildAt(0).getTop() == 0);
                    }
                }
            });

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    HashMap<String, Object> obj = (HashMap<String, Object>) listView.getItemAtPosition(position);
                    text_gorevReferans = (String) obj.get("Gorev_Referans");
                    text_oncelikDurumu = (String) obj.get("Oncelik_Durumu");
                    text_gorevGorevId = (String) obj.get("Gorev_Id");
                    startActivity(new Intent(getActivity(), AlinanIsAkislari.class));

                }
            });
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    HashMap<String, Object> objLong = (HashMap<String, Object>) listView.getItemAtPosition(position);

                    text_gorevVeren = (String) objLong.get("Gorevi_Veren");
                    text_gorevAdi = (String) objLong.get("Gorev_Adi");
                    text_gorevDetay = (String) objLong.get("Gorev_Detay");
                    text_oncelikDurumu = (String) objLong.get("Oncelik_Durumu");
                    text_tarih = (String) objLong.get("Tarih");
                    text_gorevGorevId = (String) objLong.get("Gorev_Id");
                    textSlaveId = (String) objLong.get("Slave_Id");
                    text_gorevReferans = (String) objLong.get("Gorev_Referans");

                    startActivity(new Intent(getActivity(), NavAlinanGorevlerimDetail.class));
                    return true;
                }
            });
        }
    }

    public class MyAdapter extends SimpleAdapter {

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
        public MyAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            View v = super.getView(position, convertView, parent);

            ImageView img = (ImageView) v.getTag();
            if (img == null) {
                img = (ImageView) v.findViewById(R.id.alinan_list_profil_url);
                v.setTag(img); // <<< THIS LINE !!!!
            }

            for (Object key : contact.keySet()) {
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

            if (view.getId() == R.id.ratingbar_alinan) {
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
