package com.istakip.VerilenGorevler;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.istakip.AlinanGorevler.ImageUpload;
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

import static com.istakip.LoginWebServiceQuery.Astatus;
import static com.istakip.VerilenGorevler.NavVerilenGorevler.text_verilen_gorev_id;
import static com.istakip.VerilenGorevler.NavVerilenGorevler.text_verilen_gorev_referans;
import static com.istakip.VerilenGorevler.NavVerilenGorevler.text_verilen_oncelik_durumu;

public class VerilenIsAkislari extends AppCompatActivity {

    String Verilen_Gorev_Id, Verilen_Adim_Id, Verilen_Master_Id, Verilen_Slave_Id, Verilen_Gorevi_Veren, Verilen_Gorevi_Alan, Verilen_Profil_Adi,
            Verilen_Gorev_Adi, Verilen_Gorev_Detay, Verilen_Oncelik_Durumu, Verilen_Yapilan_Is, Verilen_Gorev_Adim_Yapilan_Tarih, Verilen_Gorev_Adim_Yapilan_Saat,
            Verilen_Proje_Id, Verilen_Gorevi_Yapan, Verilen_Gorevi_Yapan_Profil_URL;
    String str_yeni_görev;
    String yeni_is_durum;

    ArrayList<HashMap<String, String>> contactVerilenListIsAkislari;
    ListView verilen_list_is_akisi;
    HashMap<String, String> Verilen_hashIsAkisi;

    Button btn_yeni_gorev_adim_verilen;
    EditText yeni_gorev_adim_verilen;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_verilen_is_akislari);

        contactVerilenListIsAkislari = new ArrayList<>();

        verilen_list_is_akisi = (ListView) findViewById(R.id.list_view_verilen_is_akislari);
        yeni_gorev_adim_verilen = (EditText) findViewById(R.id.yeni_gorev_adim_verilen);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(VerilenIsAkislari.this, ImageUpload.class));
            }
        });

        new WebServiceIsAkislariVerilen().execute();

        btn_yeni_gorev_adim_verilen = (Button) findViewById(R.id.btn_yeni_gorev_adim_verilen);
        btn_yeni_gorev_adim_verilen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str_yeni_görev = yeni_gorev_adim_verilen.getText().toString();

                checkNetwork();

                finish();
                startActivity(getIntent());
            }
        });
    }

    private void checkNetwork() {

        if (NetworkReceiver.getInstance(this).isOnline()) {

            Log.v("Network Connection", "You are not online!!!!");
            new WebServiceYeniGörevAdımEkle().execute();

        } else {
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(VerilenIsAkislari.this, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(VerilenIsAkislari.this);
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
                    finish();
                    System.exit(0);
                }
            }).setIcon(R.drawable.nuclear_alert)
                    .setCancelable(false)
                    .show();
            Log.e("Network Connection", "############################You are not online!!!!");
        }
    }

    private class WebServiceIsAkislariVerilen extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {

            String NAMESPACE = "http://tempuri.org/";
            String URL = "http://istakip.goktekinenerji.com/SERVISLER/WebServiceIsTakip.asmx";//Make sure you changed IP address
            String SOAP_ACTION = "http://tempuri.org/JListeleIsAkislari";
            String METHOD_NAME = "JListeleIsAkislari";

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            PropertyInfo masterIdPI = new PropertyInfo();
            PropertyInfo passPI = new PropertyInfo();
            PropertyInfo oncelikPI = new PropertyInfo();

            passPI.setName("Pass");
            passPI.setValue("haloX34fcidj");
            passPI.setType(String.class);

            request.addProperty(passPI);

            masterIdPI.setName("Gorev_Referans");
            masterIdPI.setValue(text_verilen_gorev_referans);
            masterIdPI.setType(String.class);

            request.addProperty(masterIdPI);

            oncelikPI.setName("Oncelik_Durumu");
            oncelikPI.setValue(text_verilen_oncelik_durumu);
            oncelikPI.setType(String.class);

            request.addProperty(oncelikPI);

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

                    Verilen_Gorev_Id = jsonArray.getJSONObject(i).getString("Gorev_Id");
                    Verilen_Adim_Id = jsonArray.getJSONObject(i).getString("Adim_Id");
                    Verilen_Master_Id = jsonArray.getJSONObject(i).getString("Master_Id");
                    Verilen_Slave_Id = jsonArray.getJSONObject(i).getString("Slave_Id");
                    Verilen_Gorevi_Veren = jsonArray.getJSONObject(i).getString("Gorevi_Veren");
                    Verilen_Gorevi_Alan = jsonArray.getJSONObject(i).getString("Gorevi_Alan");
                    Verilen_Profil_Adi = jsonArray.getJSONObject(i).getString("Profil_Adi");
                    Verilen_Gorev_Adi = jsonArray.getJSONObject(i).getString("Gorev_Adi");
                    Verilen_Gorev_Detay = jsonArray.getJSONObject(i).getString("Gorev_Detay");
                    Verilen_Oncelik_Durumu = jsonArray.getJSONObject(i).getString("Oncelik_Durumu");
                    Verilen_Yapilan_Is = jsonArray.getJSONObject(i).getString("Yapilan_Is");
                    Verilen_Gorev_Adim_Yapilan_Tarih = jsonArray.getJSONObject(i).getString("Gorev_Adim_Yapilan_Tarih");
                    Verilen_Gorev_Adim_Yapilan_Saat = jsonArray.getJSONObject(i).getString("Gorev_Adim_Yapilan_Saat");
                    Verilen_Proje_Id = jsonArray.getJSONObject(i).getString("Proje_Id");
                    Verilen_Gorevi_Yapan = jsonArray.getJSONObject(i).getString("Gorevi_Yapan");
                    Verilen_Gorevi_Yapan_Profil_URL = jsonArray.getJSONObject(i).getString("Gorevi_Yapan_Profil_Url");

                    Verilen_hashIsAkisi = new HashMap<>();

                    Verilen_hashIsAkisi.put("Gorev_Id", Verilen_Gorev_Id);
                    Verilen_hashIsAkisi.put("Adim_Id", Verilen_Adim_Id);
                    Verilen_hashIsAkisi.put("Master_Id", Verilen_Master_Id);
                    Verilen_hashIsAkisi.put("Slave_Id", Verilen_Slave_Id);
                    Verilen_hashIsAkisi.put("Gorevi_Veren", Verilen_Gorevi_Veren);
                    Verilen_hashIsAkisi.put("Gorevi_Alan", Verilen_Gorevi_Alan);
                    Verilen_hashIsAkisi.put("Profil_Adi", Verilen_Profil_Adi);
                    Verilen_hashIsAkisi.put("Gorev_Adi", Verilen_Gorev_Adi);
                    Verilen_hashIsAkisi.put("Gorev_Detay", Verilen_Gorev_Detay);
                    Verilen_hashIsAkisi.put("Oncelik_Durumu", Verilen_Oncelik_Durumu);
                    Verilen_hashIsAkisi.put("Yapilan_Is", Verilen_Yapilan_Is);
                    Verilen_hashIsAkisi.put("Gorev_Adim_Yapilan_Tarih", Verilen_Gorev_Adim_Yapilan_Tarih);
                    Verilen_hashIsAkisi.put("Gorev_Adim_Yapilan_Saat", Verilen_Gorev_Adim_Yapilan_Saat);
                    Verilen_hashIsAkisi.put("Proje_Id", Verilen_Proje_Id);
                    Verilen_hashIsAkisi.put("Gorevi_Yapan", Verilen_Gorevi_Yapan);
                    Verilen_hashIsAkisi.put("Gorevi_Yapan_Profil_Url", Verilen_Gorevi_Yapan_Profil_URL);

                    contactVerilenListIsAkislari.add(Verilen_hashIsAkisi);
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
            ListAdapter listAdapter = new MyAdapterVerilenIsAkislari(VerilenIsAkislari.this, contactVerilenListIsAkislari, R.layout.activity_list_verilen_is_akisi,
                    new String[]{"Gorev_Adim_Yapilan_Saat", "Gorevi_Yapan", "Yapilan_Is"},
                    new int[]{R.id.verilen_list_is_akisi_tarih, R.id.verilen_list_is_akisi_yapan, R.id.verilen_list_is_akisi_gorev_detayi});
            verilen_list_is_akisi.setAdapter(listAdapter);
            Collections.reverse(contactVerilenListIsAkislari); //Listeyi Ters çevirir
        }
    }

    private class WebServiceYeniGörevAdımEkle extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            String NAMESPACE = "http://tempuri.org/";
            String URL = "http://istakip.goktekinenerji.com/SERVISLER/WebServiceIsTakip.asmx";//Make sure you changed IP address
            String SOAP_ACTION = "http://tempuri.org/YeniGorevAdimEkle";
            String METHOD_NAME = "YeniGorevAdimEkle";

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            PropertyInfo masterIdPI = new PropertyInfo();
            PropertyInfo passPI = new PropertyInfo();
            PropertyInfo yapilan_isPI = new PropertyInfo();
            PropertyInfo gorevi_yapan_kisi_IDPI = new PropertyInfo();

            masterIdPI.setName("Gorev_Id");
            masterIdPI.setValue(text_verilen_gorev_id);
            masterIdPI.setType(String.class);

            request.addProperty(masterIdPI);

            yapilan_isPI.setName("Yapilan_Is");
            yapilan_isPI.setValue(str_yeni_görev);
            yapilan_isPI.setType(String.class);

            request.addProperty(yapilan_isPI);

            passPI.setName("Pass");
            passPI.setValue("haloX34fcidj");
            passPI.setType(String.class);

            request.addProperty(passPI);

            gorevi_yapan_kisi_IDPI.setName("Gorevi_Yapan_Kisi_Id");
            gorevi_yapan_kisi_IDPI.setValue(Astatus);
            gorevi_yapan_kisi_IDPI.setType(Integer.class);

            request.addProperty(gorevi_yapan_kisi_IDPI);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE httpTransportSE = new HttpTransportSE(URL);
            httpTransportSE.debug = true;

            try {
                httpTransportSE.call(SOAP_ACTION, envelope);

                SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
                yeni_is_durum = response.toString();
            } catch (IOException e) {
                Log.e("1", "IOException");
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                Log.e("2", "XmlPullParserException");
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (yeni_is_durum.equals("0")) {
                Toast.makeText(getBaseContext(), "Yeni Görev Kaydedildi!", Toast.LENGTH_SHORT).show();

                contactVerilenListIsAkislari.clear();
                new WebServiceIsAkislariVerilen().execute();
            } else {
                Toast.makeText(getBaseContext(), "Lütfen Daha Sonra Tekrar Deneyiniz!", Toast.LENGTH_LONG).show();
            }
        }
    }


    public class MyAdapterVerilenIsAkislari extends SimpleAdapter {

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
        public MyAdapterVerilenIsAkislari(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            View v = super.getView(position, convertView, parent);

            ImageView img = (ImageView) v.getTag();
            if (img == null) {
                img = (ImageView) v.findViewById(R.id.verilen_isakisi_list_profil_url);
                v.setTag(img); // <<< THIS LINE !!!!
            }

            for (Object key : Verilen_hashIsAkisi.keySet()) {
                if (key.equals("Gorevi_Yapan_Profil_Url")) { //Profil Url ye eşit oldugunda
                    Object url = ((Map) getItem(position)).get(key); // Keydeki degeri pozisyona gore ata
                    Picasso.with(v.getContext()).load("http://" + url).into(img); //Ekranda göster
                }
            }
            return v;
        }
    }
}
