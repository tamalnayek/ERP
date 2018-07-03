package com.istakip.AlinanGorevler;

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
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

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

import static com.istakip.AlinanGorevler.NavAlinanGorevlerim.text_gorevGorevId;
import static com.istakip.AlinanGorevler.NavAlinanGorevlerim.text_gorevReferans;
import static com.istakip.AlinanGorevler.NavAlinanGorevlerim.text_oncelikDurumu;
import static com.istakip.LoginScreen.loginStatus;

public class AlinanIsAkislari extends AppCompatActivity {

    String Gorev_Id, Adim_Id, Master_Id, Slave_Id, Gorevi_Veren, Gorevi_Alan, Profil_Adi,
            Gorev_Adi, Gorev_Detay, Oncelik_Durumu, Yapilan_Is, Gorev_Adim_Yapilan_Tarih, Gorev_Adim_Yapilan_Saat,
            Proje_Id, Gorevi_Yapan, Gorevi_Yapan_Profil_URL;

    ArrayList<HashMap<String, String>> contactListIsAkislari;
    ArrayList<String> sil_gorev_WS;

    String yeni_is_durum, sil_gorev_result;
    String sil_gorev;

    ListView list_is_akisi;

    EditText yeni_gorev_adim;
    Button btn_yeni_gorev_adim;
    String str_yeni_gorev;
    HashMap<String, String> hashIsAkisi;

    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_alinan_is_akislari);

        contactListIsAkislari = new ArrayList<>();
        sil_gorev_WS = new ArrayList<>();

        // image_upload = (Button) findViewById(R.id.image_upload);
        // image_upload.setOnClickListener(new View.OnClickListener() {
        //     @Override
        //     public void onClick(View v) {
        //         startActivity(new Intent(AlinanIsAkislari.this, ImageUpload.class));
        //     }
        // });

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AlinanIsAkislari.this, ImageUpload.class));
            }
        });
    
        Toast.makeText(AlinanIsAkislari.this, text_gorevGorevId, Toast.LENGTH_SHORT).show();
        list_is_akisi = (ListView) findViewById(R.id.list_view_is_akislari);

        yeni_gorev_adim = (EditText) findViewById(R.id.yeni_gorev_adim);

        new WebServiceIsAkislari().execute();

        btn_yeni_gorev_adim = (Button) findViewById(R.id.btn_yeni_gorev_adim);
        btn_yeni_gorev_adim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                str_yeni_gorev = yeni_gorev_adim.getText().toString();

                checkNetwork();
                finish(); //Page reload activity
                startActivity(getIntent());

            }
        });
    }

    private void checkNetwork() {

        if (NetworkReceiver.getInstance(this).isOnline()) {

            Log.v("Network Connection", "You not online!!!!");

            new WebServiceYeniGorevAdimEkle().execute(); // Call Webservice

        } else {
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(AlinanIsAkislari.this, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(AlinanIsAkislari.this);
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            onBackPressed();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private class WebServiceIsAkislari extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {

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
            masterIdPI.setValue(text_gorevReferans);
            masterIdPI.setType(String.class);

            request.addProperty(masterIdPI);

            OncelikPI.setName("Oncelik_Durumu");
            OncelikPI.setValue(text_oncelikDurumu);
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

                    Gorev_Id = jsonArray.getJSONObject(i).getString("Gorev_Id");
                    Adim_Id = jsonArray.getJSONObject(i).getString("Adim_Id");
                    Master_Id = jsonArray.getJSONObject(i).getString("Master_Id");
                    Slave_Id = jsonArray.getJSONObject(i).getString("Slave_Id");
                    Gorevi_Veren = jsonArray.getJSONObject(i).getString("Gorevi_Veren");
                    Gorevi_Alan = jsonArray.getJSONObject(i).getString("Gorevi_Alan");
                    Profil_Adi = jsonArray.getJSONObject(i).getString("Profil_Adi");
                    Gorev_Adi = jsonArray.getJSONObject(i).getString("Gorev_Adi");
                    Gorev_Detay = jsonArray.getJSONObject(i).getString("Gorev_Detay");
                    Oncelik_Durumu = jsonArray.getJSONObject(i).getString("Oncelik_Durumu");
                    Yapilan_Is = jsonArray.getJSONObject(i).getString("Yapilan_Is");
                    Gorev_Adim_Yapilan_Tarih = jsonArray.getJSONObject(i).getString("Gorev_Adim_Yapilan_Tarih");
                    Gorev_Adim_Yapilan_Saat = jsonArray.getJSONObject(i).getString("Gorev_Adim_Yapilan_Saat");
                    Proje_Id = jsonArray.getJSONObject(i).getString("Proje_Id");
                    Gorevi_Yapan = jsonArray.getJSONObject(i).getString("Gorevi_Yapan");
                    Gorevi_Yapan_Profil_URL = jsonArray.getJSONObject(i).getString("Gorevi_Yapan_Profil_Url");

                    hashIsAkisi = new HashMap<>();

                    hashIsAkisi.put("Gorev_Id", Gorev_Id);
                    hashIsAkisi.put("Adim_Id", Adim_Id);
                    hashIsAkisi.put("Master_Id", Master_Id);
                    hashIsAkisi.put("Slave_Id", Slave_Id);
                    hashIsAkisi.put("Gorevi_Veren", Gorevi_Veren);
                    hashIsAkisi.put("Gorevi_Alan", Gorevi_Alan);
                    hashIsAkisi.put("Profil_Adi", Profil_Adi);
                    hashIsAkisi.put("Gorev_Adi", Gorev_Adi);
                    hashIsAkisi.put("Gorev_Detay", Gorev_Detay);
                    hashIsAkisi.put("Oncelik_Durumu", Oncelik_Durumu);
                    hashIsAkisi.put("Yapilan_Is", Yapilan_Is);
                    hashIsAkisi.put("Gorev_Adim_Yapilan_Tarih", Gorev_Adim_Yapilan_Tarih);
                    hashIsAkisi.put("Gorev_Adim_Yapilan_Saat", Gorev_Adim_Yapilan_Saat);
                    hashIsAkisi.put("Proje_Id", Proje_Id);
                    hashIsAkisi.put("Gorevi_Yapan", Gorevi_Yapan);
                    hashIsAkisi.put("Gorevi_Yapan_Profil_Url", Gorevi_Yapan_Profil_URL);

                    contactListIsAkislari.add(hashIsAkisi);
                    sil_gorev_WS.add(Adim_Id);
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

            // Toast.makeText(AlinanIsAkislari.this, contactListIsAkislari.toString(), Toast.LENGTH_LONG).show();
            ListAdapter listAdapter = new MyAdapterIsAkislari(AlinanIsAkislari.this, contactListIsAkislari, R.layout.activity_list_alinan_is_akisi,
                    new String[]{"Gorev_Adim_Yapilan_Saat", "Gorevi_Yapan", "Yapilan_Is"},
                    new int[]{R.id.alinan_list_is_akisi_tarih, R.id.alinan_list_is_akisi_yapan, R.id.alinan_list_is_akisi_gorev_detayi});
            list_is_akisi.setAdapter(listAdapter);
            Collections.reverse(contactListIsAkislari);

        }

        @Override
        protected void onPreExecute() {

            list_is_akisi.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                    //Toast.makeText(AlinanIsAkislari.this, sil_gorev_WS.get(position).toString(), Toast.LENGTH_LONG).show();

                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(AlinanIsAkislari.this, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(AlinanIsAkislari.this);
                    }

                    builder.setTitle("Sil")
                            .setMessage("Emin misiniz?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    sil_gorev = sil_gorev_WS.get(position).toString();
                                    new WebServiceSilGorevAdim().execute();

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


                    return true;
                }
            });
        }
    }

    private class WebServiceYeniGorevAdimEkle extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {

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
            masterIdPI.setValue(text_gorevGorevId);
            masterIdPI.setType(String.class);

            request.addProperty(masterIdPI);

            yapilan_isPI.setName("Yapilan_Is");
            yapilan_isPI.setValue(str_yeni_gorev);
            yapilan_isPI.setType(String.class);

            request.addProperty(yapilan_isPI);

            passPI.setName("Pass");
            passPI.setValue("haloX34fcidj");
            passPI.setType(String.class);

            request.addProperty(passPI);

            gorevi_yapan_kisi_IDPI.setName("Gorevi_Yapan_Kisi_Id");
            gorevi_yapan_kisi_IDPI.setValue(loginStatus);
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
                Log.e("1", "do");
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                Log.e("2", "do");
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (yeni_is_durum.equals("0")) {
                Toast.makeText(getBaseContext(), "Yeni Görev Kaydedildi!", Toast.LENGTH_SHORT).show();

                contactListIsAkislari.clear();
                new WebServiceIsAkislari().execute();
            } else {
                Toast.makeText(getBaseContext(), "Lütfen Daha Sonra Tekrar Deneyiniz!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class WebServiceSilGorevAdim extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {


            String NAMESPACE = "http://tempuri.org/";
            String URL = "http://istakip.goktekinenerji.com/SERVISLER/WebServiceIsTakip.asmx";//Make sure you changed IP address
            String SOAP_ACTION = "http://tempuri.org/Sil_GorevAdim";
            String METHOD_NAME = "Sil_GorevAdim";

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            PropertyInfo masterIdPI = new PropertyInfo();
            PropertyInfo passPI = new PropertyInfo();

            masterIdPI.setName("GorevAdimId");
            masterIdPI.setValue(sil_gorev);
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
                sil_gorev_result = response.toString();

            } catch (IOException e) {
                Log.e("1", "do");
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                Log.e("2", "do");
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (sil_gorev_result.equals("0")) {

                Toast.makeText(getBaseContext(), "Kayıt başarıyla silindi!", Toast.LENGTH_SHORT).show();
                contactListIsAkislari.clear();
                new WebServiceIsAkislari().execute();
            } else {
                Toast.makeText(getBaseContext(), "Lütfen Daha Sonra Tekrar Deneyiniz!", Toast.LENGTH_SHORT).show();
            }
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
                img = (ImageView) v.findViewById(R.id.isakisi_list_profil_url);
                v.setTag(img); // <<< THIS LINE !!!!
            }

            for (Object key : hashIsAkisi.keySet()) {
                if (key.equals("Gorevi_Yapan_Profil_Url")) { //Profil Url ye eşit oldugunda
                    Object url = ((Map) getItem(position)).get(key); // Keydeki degeri pozisyona gore ata
                    Picasso.with(v.getContext()).load("http://" + url).into(img); //Ekranda göster
                }
            }
            return v;
        }
    }
}
