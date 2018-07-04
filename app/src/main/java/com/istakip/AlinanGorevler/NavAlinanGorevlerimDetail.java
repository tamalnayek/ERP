package com.istakip.AlinanGorevler;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.istakip.Navigation_Drawer;
import com.istakip.R;
import com.kosalgeek.genasync12.MainActivity;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import static com.istakip.AlinanGorevler.NavAlinanGorevlerim.contactList;
import static com.istakip.AlinanGorevler.NavAlinanGorevlerim.text_gorevAdi;
import static com.istakip.AlinanGorevler.NavAlinanGorevlerim.text_gorevDetay;
import static com.istakip.AlinanGorevler.NavAlinanGorevlerim.text_gorevGorevId;
import static com.istakip.AlinanGorevler.NavAlinanGorevlerim.text_gorevVeren;
import static com.istakip.AlinanGorevler.NavAlinanGorevlerim.text_oncelikDurumu;
import static com.istakip.AlinanGorevler.NavAlinanGorevlerim.text_tarih;

public class NavAlinanGorevlerimDetail extends AppCompatActivity {

    EditText detail_gorev_veren, detail_gorev_adi,
            detail_verilis_tarihi, detail_gorev_aciklamasi, detail_gorev_onceligi;

    Button detail_alinan_gorev_onay;
    public String kapat_gorev_deneme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_alinan_gorevlerim_detail);

        detail_gorev_veren = (EditText) findViewById(R.id.detail_gorev_veren);
        detail_gorev_veren.setKeyListener(null);
        detail_gorev_veren.setText(text_gorevVeren);

        detail_gorev_adi = (EditText) findViewById(R.id.detail_gorev_adi);
        detail_gorev_adi.setKeyListener(null);
        detail_gorev_adi.setText(text_gorevAdi);

        detail_gorev_aciklamasi = (EditText) findViewById(R.id.detail_gorev_aciklamasi);
        detail_gorev_aciklamasi.setKeyListener(null);
        detail_gorev_aciklamasi.setText(text_gorevDetay);

        detail_verilis_tarihi = (EditText) findViewById(R.id.detail_verilis_tarihi);
        detail_verilis_tarihi.setKeyListener(null);
        detail_verilis_tarihi.setText(text_tarih);

        detail_gorev_onceligi = (EditText) findViewById(R.id.detail_gorev_onceligi);
        detail_gorev_onceligi.setKeyListener(null);
        detail_gorev_onceligi.setText(text_oncelikDurumu);

        switch (text_oncelikDurumu){
            case "1":
                detail_gorev_onceligi.setTextColor(Color.BLACK);
                break;
            case "2":
                detail_gorev_onceligi.setTextColor(Color.BLUE);
                break;
            case "3":
                detail_gorev_onceligi.setTextColor(Color.MAGENTA);
                break;
            case "4":
                detail_gorev_onceligi.setTextColor(Color.GREEN);
                break;
            case "5":
                detail_gorev_onceligi.setTextColor(Color.RED);
                break;
            default:
                detail_gorev_onceligi.setText("Hata");
        }

        detail_alinan_gorev_onay = (Button) findViewById(R.id.detail_alinan_gorev_onay);
        detail_alinan_gorev_onay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               new OnayaGonderWS().execute();
            }
        });
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

    private class OnayaGonderWS extends AsyncTask<String, Void, Void>
    {
        @Override
        protected Void doInBackground(String... strings) {
            String NAMESPACE = "http://tempuri.org/";
            String URL = "http://istakip.goktekinenerji.com/SERVISLER/WebServiceIsTakip.asmx";//Make sure you changed IP address
            String SOAP_ACTION = "http://tempuri.org/Gorev_OnayaGonder";
            String METHOD_NAME = "Gorev_OnayaGonder";

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            PropertyInfo uGorev_Id = new PropertyInfo();

            uGorev_Id.setName("Gorev_Id");
            uGorev_Id.setValue(text_gorevGorevId);
            uGorev_Id.setType(String.class);

            request.addProperty(uGorev_Id);

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
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (kapat_gorev_deneme.equals("0")) {
                Toast.makeText(getBaseContext(), "Görev Onaya Gönderildi!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(NavAlinanGorevlerimDetail.this,Navigation_Drawer.class));
            } else {
                Toast.makeText(getBaseContext(), "Lütfen Daha Sonra Tekrar Deneyiniz!", Toast.LENGTH_LONG).show();
            }
        }
    }
}
