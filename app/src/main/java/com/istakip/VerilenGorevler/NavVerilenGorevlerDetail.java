package com.istakip.VerilenGorevler;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.istakip.Navigation_Drawer;
import com.istakip.R;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static com.istakip.VerilenGorevler.NavVerilenGorevler.text_verilen_bitisTarih;
import static com.istakip.VerilenGorevler.NavVerilenGorevler.text_verilen_gorev_adi;
import static com.istakip.VerilenGorevler.NavVerilenGorevler.text_verilen_gorev_alan;
import static com.istakip.VerilenGorevler.NavVerilenGorevler.text_verilen_gorev_detay;
import static com.istakip.VerilenGorevler.NavVerilenGorevler.text_verilen_gorev_id;
import static com.istakip.VerilenGorevler.NavVerilenGorevler.text_verilen_oncelik_durumu;
import static com.istakip.VerilenGorevler.NavVerilenGorevler.text_verilen_proje_id;
import static com.istakip.VerilenGorevler.NavVerilenGorevler.text_verilen_slave_id;
import static com.istakip.VerilenGorevler.NavVerilenGorevler.text_verilen_tarih;

public class NavVerilenGorevlerDetail extends AppCompatActivity {

    public String kapat_gorev_deneme;
    String strGorevAdi, strGorevOnceligi, strGorevAciklamasi, strBitisTarih;
    String gorevDurumu;

    Calendar takvim;
    String myFormat = "dd.MM.yyyy";
    DatePickerDialog.OnDateSetListener date;

    Button detail_verilen_gorev_kapatma;
    Button detail_verilen_gorev_degistir;
    EditText detail_verilen_gorev_alan, detail_verilen_gorev_adi, detail_verilen_oncelik_durumu,
            detail_verilen_gorev_detay, detail_verilen_tarih, detail_verilen_gorev_bitisTarih;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_verilen_gorevler_detail);

        takvim = Calendar.getInstance();

        detail_verilen_gorev_alan = (EditText) findViewById(R.id.detail_verilen_gorev_alan);
        detail_verilen_gorev_alan.setKeyListener(null);
        detail_verilen_gorev_alan.setText(text_verilen_gorev_alan);

        detail_verilen_gorev_adi = (EditText) findViewById(R.id.detail_verilen_gorev_adi);
        detail_verilen_gorev_adi.setText(text_verilen_gorev_adi);

        detail_verilen_oncelik_durumu = (EditText) findViewById(R.id.detail_verilen_oncelik_durumu);
        detail_verilen_oncelik_durumu.setText(text_verilen_oncelik_durumu);

        detail_verilen_gorev_detay = (EditText) findViewById(R.id.detail_verilen_gorev_detayi);
        detail_verilen_gorev_detay.setText(text_verilen_gorev_detay);

        detail_verilen_tarih = (EditText) findViewById(R.id.detail_verilen_gorev_tarih);
        detail_verilen_tarih.setKeyListener(null);
        detail_verilen_tarih.setText(text_verilen_tarih);

        detail_verilen_gorev_bitisTarih = (EditText) findViewById(R.id.detail_verilen_gorev_bitisTarih);
        detail_verilen_gorev_bitisTarih.setText(text_verilen_bitisTarih);

        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                takvim.set(Calendar.YEAR, year);
                takvim.set(Calendar.MONTH, monthOfYear);
                takvim.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };
        detail_verilen_gorev_bitisTarih.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(NavVerilenGorevlerDetail.this, date, takvim
                        .get(Calendar.YEAR), takvim.get(Calendar.MONTH),
                        takvim.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        detail_verilen_gorev_kapatma = (Button) findViewById(R.id.detail_verilen_gorev_sil);
        detail_verilen_gorev_kapatma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Gorev_KapatmaWebService().execute();
            }
        });

        detail_verilen_gorev_degistir = (Button) findViewById(R.id.detail_verilen_gorev_degistir);
        detail_verilen_gorev_degistir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                strGorevAdi = detail_verilen_gorev_adi.getText().toString();
                strGorevOnceligi = detail_verilen_oncelik_durumu.getText().toString();
                strGorevAciklamasi = detail_verilen_gorev_detay.getText().toString();
                strBitisTarih = detail_verilen_gorev_bitisTarih.getText().toString();

                new AsynVerilenGorevDegistir().execute();

                if (detail_verilen_oncelik_durumu.getText().toString().equals("1") || detail_verilen_oncelik_durumu.getText().toString().equals("2") ||
                        detail_verilen_oncelik_durumu.getText().toString().equals("3") || detail_verilen_oncelik_durumu.getText().toString().equals("4") ||
                        detail_verilen_oncelik_durumu.getText().toString().equals("5")) {

                } else {
                    Toast.makeText(NavVerilenGorevlerDetail.this, "Gorev Onceligi Değerini 1-5 arasında giriniz!", Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    private void updateLabel() {
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        detail_verilen_gorev_bitisTarih.setText(sdf.format(takvim.getTime()));
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

    private class AsynVerilenGorevDegistir extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {

            String NAMESPACE = "http://tempuri.org/";
            String URL = "http://istakip.goktekinenerji.com/SERVISLER/WebServiceIsTakip.asmx";//Make sure you changed IP address
            String SOAP_ACTION = "http://tempuri.org/DegistirGorev";
            String METHOD_NAME = "DegistirGorev";

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            PropertyInfo uVerilenSlaveIdPI = new PropertyInfo();
            PropertyInfo uVerilenGorevAdiPI = new PropertyInfo();
            PropertyInfo uVerilenGorevDetayPI = new PropertyInfo();
            PropertyInfo uVerilenOncelikPI = new PropertyInfo();
            PropertyInfo uVerilenProjeIdPI = new PropertyInfo();
            PropertyInfo uVerilenGorevId = new PropertyInfo();
            PropertyInfo uBitisTarihi = new PropertyInfo();

            uVerilenSlaveIdPI.setName("Slave_Id");
            uVerilenSlaveIdPI.setValue(text_verilen_slave_id);
            uVerilenSlaveIdPI.setType(Integer.class);

            uVerilenGorevAdiPI.setName("Gorev_Adi");
            uVerilenGorevAdiPI.setValue(strGorevAdi);
            uVerilenGorevAdiPI.setType(String.class);

            uVerilenGorevDetayPI.setName("Gorev_Detay");
            uVerilenGorevDetayPI.setValue(strGorevAciklamasi);
            uVerilenGorevDetayPI.setType(String.class);

            uVerilenOncelikPI.setName("Oncelik_Durumu");
            uVerilenOncelikPI.setValue(strGorevOnceligi);
            uVerilenOncelikPI.setType(String.class);

            uVerilenProjeIdPI.setName("Proje_Id");
            uVerilenProjeIdPI.setValue(text_verilen_proje_id);
            uVerilenProjeIdPI.setType(String.class);

            uVerilenGorevId.setName("Gorev_Id");
            uVerilenGorevId.setValue(text_verilen_gorev_id);
            uVerilenGorevId.setType(String.class);

            uBitisTarihi.setName("Bitis_Tarihi");
            uBitisTarihi.setValue(strBitisTarih);
            uBitisTarihi.setType(String.class);

            request.addProperty(uVerilenSlaveIdPI);
            request.addProperty(uVerilenGorevAdiPI);
            request.addProperty(uVerilenGorevDetayPI);
            request.addProperty(uVerilenOncelikPI);
            request.addProperty(uVerilenProjeIdPI);
            request.addProperty(uVerilenGorevId);
            request.addProperty(uBitisTarihi);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE httpTransportSE = new HttpTransportSE(URL);
            httpTransportSE.debug = true;

            try {

                httpTransportSE.call(SOAP_ACTION, envelope);

                SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
                gorevDurumu = response.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            if (gorevDurumu.equals("0")) {
                Toast.makeText(getBaseContext(), "Yeni Görev Kaydedildi!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(getBaseContext(), "Lütfen Daha Sonra Tekrar Deneyiniz!", Toast.LENGTH_LONG).show();
            }
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
            uGorev_Id.setValue(text_verilen_gorev_id);
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
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (kapat_gorev_deneme.equals("0")) {
                Toast.makeText(getBaseContext(), "Görev Değiştirildi!", Toast.LENGTH_SHORT).show();
                detail_verilen_gorev_bitisTarih.setText("");
                finish();
                startActivity(new Intent(NavVerilenGorevlerDetail.this, Navigation_Drawer.class));
            } else {
                Toast.makeText(getBaseContext(), "Lütfen Daha Sonra Tekrar Deneyiniz!", Toast.LENGTH_LONG).show();
            }
        }
    }
}
