package com.istakip;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.istakip.AlinanGorevler.NavAlinanGorevlerim;
import com.istakip.VerilenGorevler.NavVerilenGorevler;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.istakip.LoginScreen.loginStatus;
import static com.istakip.LoginScreen.projectId;
import static com.istakip.LoginScreen.projectName;
import static com.istakip.LoginScreen.userId;
import static com.istakip.LoginScreen.userList;

public class NavYeniGorevEkle extends Activity {

    Spinner spinner;
    ArrayAdapter<CharSequence> adapter;
    String personelAdi, projeId, projeAdi, oncelik, edittextGorevAdi, edittextGorevDetayi, gorevBitis;
    Integer personelId;
    Button yeniGorevKaydet;
    EditText yeni_gorev_adi, yeni_gorev_detayi, yeni_gorev_tarih_Edittext;
    String gorevDurumu;
    LinearLayout nav_yenı_gorev_linear_layout;
    Calendar takvim;
    String myFormat = "dd.MM.yyyy";
    String baslangic;
    DatePickerDialog.OnDateSetListener date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_new_task);

        yeni_gorev_adi = (EditText) findViewById(R.id.yeni_gorev_adi);
        yeni_gorev_detayi = (EditText) findViewById(R.id.yeni_gorev_detayi);
        yeni_gorev_tarih_Edittext = (EditText) findViewById(R.id.yeni_gorev_tarih);

        nav_yenı_gorev_linear_layout = (LinearLayout) findViewById(R.id.nav_yenı_gorev_linear_layout);
        takvim = Calendar.getInstance();

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

        yeni_gorev_tarih_Edittext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(NavYeniGorevEkle.this, date, takvim
                        .get(Calendar.YEAR), takvim.get(Calendar.MONTH),
                        takvim.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        yeniGorevKaydet = (Button) findViewById(R.id.yeni_gorev_kaydet);
        yeniGorevKaydet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                baslangic = sdf.format(new Date());

                edittextGorevAdi = yeni_gorev_adi.getText().toString();
                edittextGorevDetayi = yeni_gorev_detayi.getText().toString();
                gorevBitis = yeni_gorev_tarih_Edittext.getText().toString();

                if (edittextGorevAdi.equals("") || edittextGorevAdi.equals(null) ||
                        edittextGorevDetayi.equals("") || edittextGorevDetayi.equals(null)
                        || gorevBitis.equals("") || gorevBitis.equals(null)) {
                    Toast.makeText(NavYeniGorevEkle.this, "Boş Alanları Doldurunuz!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("Baslangic", baslangic);
                    Log.e("Bitis", gorevBitis);
                    checkNetwork();
                }

            }
        });

        Spinner mySpinner = (Spinner) findViewById(R.id.yeni_personel_sec_spinner);
        mySpinner.setAdapter(new ArrayAdapter<>(NavYeniGorevEkle.this,
                android.R.layout.simple_spinner_dropdown_item, userList));
        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                personelId = Integer.valueOf(userId.get(position));
                personelAdi = userList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Spinner proje_spinner = (Spinner) findViewById(R.id.proje_spinner);
        proje_spinner.setAdapter(new ArrayAdapter<>(NavYeniGorevEkle.this,
                android.R.layout.simple_spinner_dropdown_item, projectName));
        proje_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                projeAdi =projectName.get(position);
                projeId = projectId.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        spinner = (Spinner) findViewById(R.id.spinner);
        adapter = ArrayAdapter.createFromResource(this, R.array.priority_degree, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                oncelik = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void updateLabel() {
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        yeni_gorev_tarih_Edittext.setText(sdf.format(takvim.getTime()));
    }

    private void checkNetwork() {

        if (NetworkReceiver.getInstance(this).isOnline()) {

            Log.v("Network Connection", "You are online!!!!");

            new AsynCallYeniGorev().execute();

        } else {
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(NavYeniGorevEkle.this, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(NavYeniGorevEkle.this);
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

     private class AsynCallYeniGorev extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {

            String NAMESPACE = "http://tempuri.org/";
            String URL = "http://istakip.goktekinenerji.com/SERVISLER/WebServiceIsTakip.asmx";//Make sure you changed IP address
            String SOAP_ACTION = "http://tempuri.org/YeniGorevEkleWithTarih";
            String METHOD_NAME = "YeniGorevEkleWithTarih";

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            PropertyInfo uMasterIdPI = new PropertyInfo();
            PropertyInfo uSlaveIdPI = new PropertyInfo();
            PropertyInfo uGorevAdiPI = new PropertyInfo();
            PropertyInfo uGorevDetayPI = new PropertyInfo();
            PropertyInfo uOncelikPI = new PropertyInfo();
            PropertyInfo uProjeIdPI = new PropertyInfo();
            PropertyInfo passPI = new PropertyInfo();
            PropertyInfo uBaslangicTarihPI = new PropertyInfo();
            PropertyInfo uBitisTarih = new PropertyInfo();

            uMasterIdPI.setName("Master_Id");
            uMasterIdPI.setValue(loginStatus);
            uMasterIdPI.setType(Integer.class);

            uSlaveIdPI.setName("Slave_Id");
            uSlaveIdPI.setValue(personelId);
            uSlaveIdPI.setType(Integer.class);

            uGorevAdiPI.setName("Gorev_Adi");
            uGorevAdiPI.setValue(edittextGorevAdi);
            uGorevAdiPI.setType(String.class);

            uGorevDetayPI.setName("Gorev_Detay");
            uGorevDetayPI.setValue(edittextGorevDetayi);
            uGorevDetayPI.setType(String.class);

            uOncelikPI.setName("Oncelik_Durumu");
            uOncelikPI.setValue(oncelik);
            uOncelikPI.setType(String.class);

            uProjeIdPI.setName("Proje_Id");
            uProjeIdPI.setValue(projeId);
            uProjeIdPI.setType(String.class);

            passPI.setName("Pass");
            passPI.setValue("haloX34fcidj");
            passPI.setType(String.class);

            uBaslangicTarihPI.setName("Baslama_Tarihi");
            uBaslangicTarihPI.setValue(baslangic);
            uBaslangicTarihPI.setType(String.class);

            uBitisTarih.setName("Bitis_Tarihi");
            uBitisTarih.setValue(gorevBitis);
            uBitisTarih.setType(String.class);

            request.addProperty(uMasterIdPI);
            request.addProperty(uSlaveIdPI);
            request.addProperty(uGorevAdiPI);
            request.addProperty(uGorevDetayPI);
            request.addProperty(uOncelikPI);
            request.addProperty(uProjeIdPI);
            request.addProperty(passPI);
            request.addProperty(uBaslangicTarihPI);
            request.addProperty(uBitisTarih);

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
            nav_yenı_gorev_linear_layout.setVisibility(View.GONE);
            if (gorevDurumu.equals("0")) {
                Toast.makeText(getBaseContext(), "Yeni Görev Kaydedildi!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(NavYeniGorevEkle.this, Navigation_Drawer.class));

            } else {
                Toast.makeText(getBaseContext(), "Lütfen Daha Sonra Tekrar Deneyiniz!", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onPreExecute() {
            nav_yenı_gorev_linear_layout.setVisibility(View.VISIBLE);
        }
    }
}
