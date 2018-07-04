package com.istakip.Projeler;

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

import static com.istakip.Projeler.ProjelerimGorevler.text_projeGorevReferans;
import static com.istakip.Projeler.ProjelerimGorevler.text_projeOncelikDurum;

public class ProjeIsAkisi extends AppCompatActivity {

    String Projeler_IsAkisi_Gorev_Id, Projeler_IsAkisi_Adim_Id, Projeler_IsAkisi_Master_Id, Projeler_IsAkisi_Slave_Id,
            Projeler_IsAkisi_Gorevi_Veren, Projeler_IsAkisi_Gorevi_Alan, Projeler_IsAkisi_Profil_Adi,
            Projeler_IsAkisi_Gorev_Adi, Projeler_IsAkisi_Gorev_Detay, Projeler_IsAkisi_Oncelik_Durumu, Projeler_IsAkisi_Yapilan_Is,
            Projeler_IsAkisi_Gorev_Adim_Yapilan_Tarih, Projeler_IsAkisi_Gorev_Adim_Yapilan_Saat,
            Projeler_IsAkisi_Proje_Id, Projeler_IsAkisi_Gorevi_Yapan, Projeler_IsAkisi_Gorevi_Yapan_Profil_URL;

    ArrayList<HashMap<String, String>> contactListProjeIsAkislari;
    ListView list_proje_is_akisi;
    HashMap<String, String> projehashIsAkisi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proje_is_akisi);

        contactListProjeIsAkislari = new ArrayList<>();

        list_proje_is_akisi = (ListView) findViewById(R.id.list_view_projeler_is_akislari);
        checkNetwork();

    }

    private void checkNetwork() {

        if (NetworkReceiver.getInstance(ProjeIsAkisi.this).isOnline()) {

            Log.v("Network Connection", "You are online!!!!");

            new JListeleIsAkislari().execute();

        } else {
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(ProjeIsAkisi.this, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(ProjeIsAkisi.this);
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
                    ProjeIsAkisi.this.getFragmentManager().popBackStack();
                    System.exit(0);
                }
            }).setIcon(R.drawable.nuclear_alert)
                    .setCancelable(false)
                    .show();
            Log.e("Network Connection", "############################You are not online!!!!");
        }
    }

    private class JListeleIsAkislari extends AsyncTask<String, Void, Void>{

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
            masterIdPI.setValue(text_projeGorevReferans);
            masterIdPI.setType(String.class);

            request.addProperty(masterIdPI);

            OncelikPI.setName("Oncelik_Durumu");
            OncelikPI.setValue(text_projeOncelikDurum);
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

                    Projeler_IsAkisi_Gorev_Id = jsonArray.getJSONObject(i).getString("Gorev_Id");
                    Projeler_IsAkisi_Adim_Id = jsonArray.getJSONObject(i).getString("Adim_Id");
                    Projeler_IsAkisi_Master_Id = jsonArray.getJSONObject(i).getString("Master_Id");
                    Projeler_IsAkisi_Slave_Id = jsonArray.getJSONObject(i).getString("Slave_Id");
                    Projeler_IsAkisi_Gorevi_Veren = jsonArray.getJSONObject(i).getString("Gorevi_Veren");
                    Projeler_IsAkisi_Gorevi_Alan = jsonArray.getJSONObject(i).getString("Gorevi_Alan");
                    Projeler_IsAkisi_Profil_Adi = jsonArray.getJSONObject(i).getString("Profil_Adi");
                    Projeler_IsAkisi_Gorev_Adi = jsonArray.getJSONObject(i).getString("Gorev_Adi");
                    Projeler_IsAkisi_Gorev_Detay = jsonArray.getJSONObject(i).getString("Gorev_Detay");
                    Projeler_IsAkisi_Oncelik_Durumu = jsonArray.getJSONObject(i).getString("Oncelik_Durumu");
                    Projeler_IsAkisi_Yapilan_Is = jsonArray.getJSONObject(i).getString("Yapilan_Is");
                    Projeler_IsAkisi_Gorev_Adim_Yapilan_Tarih = jsonArray.getJSONObject(i).getString("Gorev_Adim_Yapilan_Tarih");
                    Projeler_IsAkisi_Gorev_Adim_Yapilan_Saat = jsonArray.getJSONObject(i).getString("Gorev_Adim_Yapilan_Saat");
                    Projeler_IsAkisi_Proje_Id = jsonArray.getJSONObject(i).getString("Proje_Id");
                    Projeler_IsAkisi_Gorevi_Yapan = jsonArray.getJSONObject(i).getString("Gorevi_Yapan");
                    Projeler_IsAkisi_Gorevi_Yapan_Profil_URL = jsonArray.getJSONObject(i).getString("Gorevi_Yapan_Profil_Url");

                    projehashIsAkisi = new HashMap<>();

                    projehashIsAkisi.put("Gorev_Id", Projeler_IsAkisi_Gorev_Id);
                    projehashIsAkisi.put("Adim_Id", Projeler_IsAkisi_Adim_Id);
                    projehashIsAkisi.put("Master_Id", Projeler_IsAkisi_Master_Id);
                    projehashIsAkisi.put("Slave_Id", Projeler_IsAkisi_Slave_Id);
                    projehashIsAkisi.put("Gorevi_Veren", Projeler_IsAkisi_Gorevi_Veren);
                    projehashIsAkisi.put("Gorevi_Alan", Projeler_IsAkisi_Gorevi_Alan);
                    projehashIsAkisi.put("Profil_Adi", Projeler_IsAkisi_Profil_Adi);
                    projehashIsAkisi.put("Gorev_Adi", Projeler_IsAkisi_Gorev_Adi);
                    projehashIsAkisi.put("Gorev_Detay", Projeler_IsAkisi_Gorev_Detay);
                    projehashIsAkisi.put("Oncelik_Durumu", Projeler_IsAkisi_Oncelik_Durumu);
                    projehashIsAkisi.put("Yapilan_Is", Projeler_IsAkisi_Yapilan_Is);
                    projehashIsAkisi.put("Gorev_Adim_Yapilan_Tarih", Projeler_IsAkisi_Gorev_Adim_Yapilan_Tarih);
                    projehashIsAkisi.put("Gorev_Adim_Yapilan_Saat", Projeler_IsAkisi_Gorev_Adim_Yapilan_Saat);
                    projehashIsAkisi.put("Proje_Id", Projeler_IsAkisi_Proje_Id);
                    projehashIsAkisi.put("Gorevi_Yapan", Projeler_IsAkisi_Gorevi_Yapan);
                    projehashIsAkisi.put("Gorevi_Yapan_Profil_Url", Projeler_IsAkisi_Gorevi_Yapan_Profil_URL);

                    contactListProjeIsAkislari.add(projehashIsAkisi);
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

            if (contactListProjeIsAkislari.isEmpty()) {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(ProjeIsAkisi.this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(ProjeIsAkisi.this);
                }

                builder.setTitle("Oops")
                        .setMessage("Henüz görev bulunmamaktadır!")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setIcon(R.drawable.nuclear_alert)
                        .setCancelable(false)
                        .show();
            }
            ListAdapter listAdapter = new MyAdapterIsAkislari(ProjeIsAkisi.this, contactListProjeIsAkislari, R.layout.activity_list_projeler_is_akisi,
                    new String[]{"Gorev_Adim_Yapilan_Saat", "Gorevi_Yapan", "Yapilan_Is"},
                    new int[]{R.id.list_is_akisi_proje_tarih, R.id.list_is_proje_akisi_yapan, R.id.list_is_akisi_proje_gorev_detayi});
            list_proje_is_akisi.setAdapter(listAdapter);
            Collections.reverse(contactListProjeIsAkislari);
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
                img = (ImageView) v.findViewById(R.id.isakisi_list_proje_profil_url);
                v.setTag(img); // <<< THIS LINE !!!!
            }

            for (Object key : projehashIsAkisi.keySet()) {
                if (key.equals("Gorevi_Yapan_Profil_Url")) { //Profil Url ye eşit oldugunda
                    Object url = ((Map) getItem(position)).get(key); // Keydeki degeri pozisyona gore ata
                    Picasso.with(v.getContext()).load("http://" + url).into(img); //Ekranda göster
                }
            }
            return v;
        }
    }
}
