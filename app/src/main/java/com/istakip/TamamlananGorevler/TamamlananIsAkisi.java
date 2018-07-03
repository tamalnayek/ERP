package com.istakip.TamamlananGorevler;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.istakip.TamamlananGorevler.NavTamamlananGorevler.text_tamamlanan_gorev_referans;
import static com.istakip.TamamlananGorevler.NavTamamlananGorevler.text_tamamlanan_oncelik_durumu;

public class TamamlananIsAkisi extends AppCompatActivity {

    String Tamamlanan_IsAkisi_Gorev_Id, Tamamlanan_IsAkisi_Adim_Id, Tamamlanan_IsAkisi_Master_Id, Tamamlanan_IsAkisi_Slave_Id,
            Tamamlanan_IsAkisi_Gorevi_Veren, Tamamlanan_IsAkisi_Gorevi_Alan, Tamamlanan_IsAkisi_Profil_Adi,
            Tamamlanan_IsAkisi_Gorev_Adi, Tamamlanan_IsAkisi_Gorev_Detay, Tamamlanan_IsAkisi_Oncelik_Durumu, Tamamlanan_IsAkisi_Yapilan_Is,
            Tamamlanan_IsAkisi_Gorev_Adim_Yapilan_Tarih, Tamamlanan_IsAkisi_Gorev_Adim_Yapilan_Saat,
            Tamamlanan_IsAkisi_Proje_Id, Tamamlanan_IsAkisi_Gorevi_Yapan, Tamamlanan_IsAkisi_Gorevi_Yapan_Profil_URL;

    ArrayList<HashMap<String, String>> contactListTamamlananIsAkislari;
    ListView list_tamamlanan_is_akisi;
    HashMap<String, String> tamamlananhashIsAkisi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tamamlanan_is_akisi);

        contactListTamamlananIsAkislari = new ArrayList<>();
        list_tamamlanan_is_akisi = (ListView) findViewById(R.id.list_view_tamamlanan_is_akislari);

        checkNetwork();


    }

    private void checkNetwork() {
        if (NetworkReceiver.getInstance(TamamlananIsAkisi.this).isOnline()) {

            Log.v("Network Connection", "You are not online!!!!");

            new JListeleIsAkislari().execute();

        } else {
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(TamamlananIsAkisi.this, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(TamamlananIsAkisi.this);
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
                    TamamlananIsAkisi.this.getFragmentManager().popBackStack();
                    System.exit(0);
                }
            }).setIcon(R.drawable.nuclear_alert)
                    .setCancelable(false)
                    .show();

            Log.e("Network Connection", "############################You are not online!!!!");
        }
    }

    private class JListeleIsAkislari extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            String NAMESPACE = "http://tempuri.org/";
            String URL = "http://istakip.goktekinenerji.com/SERVISLER/WebServiceIsTakip.asmx";//Make sure you changed IP address
            String SOAP_ACTION = "http://tempuri.org/JListeleIsAkislari";
            String METHOD_NAME = "JListeleIsAkislari";

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            PropertyInfo masterIdPI = new PropertyInfo();
            PropertyInfo passPI = new PropertyInfo();
            PropertyInfo OncelikPI = new PropertyInfo();

            passPI.setName("Pass");
            passPI.setValue("haloX34fcidj");
            passPI.setType(String.class);

            request.addProperty(passPI);

            masterIdPI.setName("Gorev_Referans");
            masterIdPI.setValue(text_tamamlanan_gorev_referans);
            masterIdPI.setType(String.class);

            request.addProperty(masterIdPI);

            OncelikPI.setName("Oncelik_Durumu");
            OncelikPI.setValue(text_tamamlanan_oncelik_durumu);
            OncelikPI.setType(String.class);

            request.addProperty(OncelikPI);

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

                    Tamamlanan_IsAkisi_Gorev_Id = jsonArray.getJSONObject(i).getString("Gorev_Id");
                    Tamamlanan_IsAkisi_Adim_Id = jsonArray.getJSONObject(i).getString("Adim_Id");
                    Tamamlanan_IsAkisi_Master_Id = jsonArray.getJSONObject(i).getString("Master_Id");
                    Tamamlanan_IsAkisi_Slave_Id = jsonArray.getJSONObject(i).getString("Slave_Id");
                    Tamamlanan_IsAkisi_Gorevi_Veren = jsonArray.getJSONObject(i).getString("Gorevi_Veren");
                    Tamamlanan_IsAkisi_Gorevi_Alan = jsonArray.getJSONObject(i).getString("Gorevi_Alan");
                    Tamamlanan_IsAkisi_Profil_Adi = jsonArray.getJSONObject(i).getString("Profil_Adi");
                    Tamamlanan_IsAkisi_Gorev_Adi = jsonArray.getJSONObject(i).getString("Gorev_Adi");
                    Tamamlanan_IsAkisi_Gorev_Detay = jsonArray.getJSONObject(i).getString("Gorev_Detay");
                    Tamamlanan_IsAkisi_Oncelik_Durumu = jsonArray.getJSONObject(i).getString("Oncelik_Durumu");
                    Tamamlanan_IsAkisi_Yapilan_Is = jsonArray.getJSONObject(i).getString("Yapilan_Is");
                    Tamamlanan_IsAkisi_Gorev_Adim_Yapilan_Tarih = jsonArray.getJSONObject(i).getString("Gorev_Adim_Yapilan_Tarih");
                    Tamamlanan_IsAkisi_Gorev_Adim_Yapilan_Saat = jsonArray.getJSONObject(i).getString("Gorev_Adim_Yapilan_Saat");
                    Tamamlanan_IsAkisi_Proje_Id = jsonArray.getJSONObject(i).getString("Proje_Id");
                    Tamamlanan_IsAkisi_Gorevi_Yapan = jsonArray.getJSONObject(i).getString("Gorevi_Yapan");
                    Tamamlanan_IsAkisi_Gorevi_Yapan_Profil_URL = jsonArray.getJSONObject(i).getString("Gorevi_Yapan_Profil_Url");

                    tamamlananhashIsAkisi = new HashMap<>();

                    tamamlananhashIsAkisi.put("Gorev_Id", Tamamlanan_IsAkisi_Gorev_Id);
                    tamamlananhashIsAkisi.put("Adim_Id", Tamamlanan_IsAkisi_Adim_Id);
                    tamamlananhashIsAkisi.put("Master_Id", Tamamlanan_IsAkisi_Master_Id);
                    tamamlananhashIsAkisi.put("Slave_Id", Tamamlanan_IsAkisi_Slave_Id);
                    tamamlananhashIsAkisi.put("Gorevi_Veren", Tamamlanan_IsAkisi_Gorevi_Veren);
                    tamamlananhashIsAkisi.put("Gorevi_Alan", Tamamlanan_IsAkisi_Gorevi_Alan);
                    tamamlananhashIsAkisi.put("Profil_Adi", Tamamlanan_IsAkisi_Profil_Adi);
                    tamamlananhashIsAkisi.put("Gorev_Adi", Tamamlanan_IsAkisi_Gorev_Adi);
                    tamamlananhashIsAkisi.put("Gorev_Detay", Tamamlanan_IsAkisi_Gorev_Detay);
                    tamamlananhashIsAkisi.put("Oncelik_Durumu", Tamamlanan_IsAkisi_Oncelik_Durumu);
                    tamamlananhashIsAkisi.put("Yapilan_Is", Tamamlanan_IsAkisi_Yapilan_Is);
                    tamamlananhashIsAkisi.put("Gorev_Adim_Yapilan_Tarih", Tamamlanan_IsAkisi_Gorev_Adim_Yapilan_Tarih);
                    tamamlananhashIsAkisi.put("Gorev_Adim_Yapilan_Saat", Tamamlanan_IsAkisi_Gorev_Adim_Yapilan_Saat);
                    tamamlananhashIsAkisi.put("Proje_Id", Tamamlanan_IsAkisi_Proje_Id);
                    tamamlananhashIsAkisi.put("Gorevi_Yapan", Tamamlanan_IsAkisi_Gorevi_Yapan);
                    tamamlananhashIsAkisi.put("Gorevi_Yapan_Profil_Url", Tamamlanan_IsAkisi_Gorevi_Yapan_Profil_URL);

                    contactListTamamlananIsAkislari.add(tamamlananhashIsAkisi);

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

            if (contactListTamamlananIsAkislari.isEmpty()) {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(TamamlananIsAkisi.this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(TamamlananIsAkisi.this);
                }

                builder.setTitle("Oops")
                        .setMessage("Henüz görev bulunmamaktadır!")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                finish();
                            }
                        })
                        .setIcon(R.drawable.nuclear_alert)
                        .setCancelable(false)
                        .show();
            }

            ListAdapter listAdapter = new MyAdapterIsAkislari(TamamlananIsAkisi.this, contactListTamamlananIsAkislari, R.layout.activity_list_tamamlanan_is_akisi,
                    new String[]{"Gorev_Adim_Yapilan_Saat", "Gorevi_Yapan", "Yapilan_Is"},
                    new int[]{R.id.tamamlanan_list_is_akisi_tarih, R.id.tamamlanan_list_is_akisi_yapan, R.id.tamamlanan_list_is_akisi_gorev_detayi});
            list_tamamlanan_is_akisi.setAdapter(listAdapter);
            Collections.reverse(contactListTamamlananIsAkislari);
        }
    }
    public class MyAdapterIsAkislari extends SimpleAdapter {

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
        public MyAdapterIsAkislari(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            View v = super.getView(position, convertView, parent);

            ImageView img = (ImageView) v.getTag();
            if (img == null) {
                img = (ImageView) v.findViewById(R.id.tamamlanan_isakisi_list_profil_url);
                v.setTag(img); // <<< THIS LINE !!!!
            }

            for (Object key : tamamlananhashIsAkisi.keySet()) {
                if (key.equals("Gorevi_Yapan_Profil_Url")) { //Profil Url ye eşit oldugunda
                    Object url = ((Map) getItem(position)).get(key); // Keydeki degeri pozisyona gore ata
                    Picasso.with(v.getContext()).load("http://" + url).into(img); //Ekranda göster
                }
            }
            return v;
        }
    }
}
