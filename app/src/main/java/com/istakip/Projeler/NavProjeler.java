package com.istakip.Projeler;

import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.istakip.NetworkReceiver;
import com.istakip.R;

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

import static com.istakip.LoginScreen.loginStatus;

public class NavProjeler extends Fragment {

    String proje_kullanici_id, proje_durumu, proje_kayit_tarihi;
    static String proje_adi;
    static String proje_id;
    View myView;
    public static ArrayList<HashMap<String, String>> projelerList;
    ListView listView;
    LinearLayout nav_peojeler_linear_layout;
    SwipeRefreshLayout swipeRefreshLayout;
    static String text_projeler_proje_id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        myView = inflater.inflate(R.layout.activity_nav_projeler, container, false);

        projelerList = new ArrayList<>();

        listView = (ListView) myView.findViewById(R.id.projelerim_list_id);
        nav_peojeler_linear_layout = (LinearLayout) myView.findViewById(R.id.nav_peojeler_linear_layout);

        checkNetwork();


        swipeRefreshLayout = (SwipeRefreshLayout) myView.findViewById(R.id.swipe_projeler);
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
                        projelerList.clear();
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
            String SOAP_ACTION = "http://tempuri.org/JListeleProjelerim";
            String METHOD_NAME = "JListeleProjelerim";

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            PropertyInfo masterIdPI = new PropertyInfo();

            masterIdPI.setName("Personel_Id");
            masterIdPI.setValue(loginStatus);
            masterIdPI.setType(String.class);

            request.addProperty(masterIdPI);
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

                    proje_id = jsonArray.getJSONObject(i).getString("Proje_Id");
                    proje_kullanici_id = jsonArray.getJSONObject(i).getString("Kullanici_Id");
                    proje_adi = jsonArray.getJSONObject(i).getString("Proje_Adi");
                    proje_durumu = jsonArray.getJSONObject(i).getString("Durumu");
                    proje_kayit_tarihi = jsonArray.getJSONObject(i).getString("Kayit_Tarihi");

                    HashMap<String, String> contact = new HashMap<>();

                    contact.put("Proje_Id", proje_id);
                    contact.put("Kullanici_Id", proje_kullanici_id);
                    contact.put("Proje_Adi", proje_adi);
                    contact.put("Durumu", proje_durumu);
                    contact.put("Kayit_Tarihi", proje_kayit_tarihi);

                    projelerList.add(contact);

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

            if (projelerList.isEmpty()) {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(getContext());
                }

                builder.setTitle("Oops")
                        .setMessage("Henüz projeniz bulunmamaktadır!")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setIcon(R.drawable.nuclear_alert)
                        .setCancelable(false)
                        .show();
            }

            ListAdapter adapter = new SimpleAdapter(getActivity(), projelerList,
                    R.layout.activity_list_projeler, new String[]{"Proje_Adi", "Durumu", "Kayit_Tarihi"},
                    new int[]{R.id.projeler_list_proje_adi, R.id.projeler_list_durumu, R.id.projeler_list_kayit_tarihi});

            listView.setAdapter(adapter);
            nav_peojeler_linear_layout.setVisibility(View.GONE);

        }

        @Override
        protected void onPreExecute() {
            nav_peojeler_linear_layout.setVisibility(View.VISIBLE);

            listView.setOnScrollListener(new AbsListView.OnScrollListener() { //PAGE SCROLL NOT REFRESH
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    if (listView.getChildAt(0)!= null){
                        swipeRefreshLayout.setEnabled(listView.getFirstVisiblePosition() == 0 && listView.getChildAt(0).getTop() == 0);
                    }
                }
            });
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // LISTVIEW JUST CLICK
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    HashMap<String, Object> obj = (HashMap<String, Object>) listView.getItemAtPosition(position);

                    text_projeler_proje_id =(String) obj.get("Proje_Id");

                    startActivity(new Intent(getActivity(), ProjelerimGorevler.class));
                }
            });
        }
    }
}
