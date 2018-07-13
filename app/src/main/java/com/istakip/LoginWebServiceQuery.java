package com.istakip;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

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

public class LoginWebServiceQuery extends AppCompatActivity {

    public static String Astatus;
    static String users_Kullanici_ID;
    static String users_Profil_Adi;
    static String usersProjectId;
    static String usersProjectName;
    static String profil_adi, mail, profil_url;
    static String kb_profil_adi, kb_mail, kb_profil_url;
    static ArrayList<String> userList, userId, menuUserList, menuMailList, menuUrlList;
    static ArrayList<String> projectId, projectName;

    LinearLayout linlaHeaderProgressWS;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_web_service_query);

        userList = new ArrayList<>();
        userId = new ArrayList<>();

        projectId = new ArrayList<>();
        projectName = new ArrayList<>();

        menuUserList = new ArrayList<>();
        menuMailList = new ArrayList<>();
        menuUrlList = new ArrayList<>();

        linlaHeaderProgressWS = (LinearLayout) findViewById(R.id.linlaHeaderProgressWS);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Astatus = preferences.getString("key", "Default");

        checkNetwork();

    }

    private void checkNetwork() {
        if (NetworkReceiver.getInstance(this).isOnline()) {

            Log.v("Network Connection", "You are online!!!!");
            new WebServiceLoginUsers().execute();

        } else {
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                builder = new AlertDialog.Builder(LoginWebServiceQuery.this, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(LoginWebServiceQuery.this);
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

    private class AsynKullanicilar extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {

            String NAMESPACE = "http://tempuri.org/";
            String URL = "http://istakip.goktekinenerji.com/SERVISLER/WebServiceIsTakip.asmx";//Make sure you changed IP address
            String SOAP_ACTION = "http://tempuri.org/JListeleKullanicilar";
            String METHOD_NAME = "JListeleKullanicilar";

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            PropertyInfo uNamePI = new PropertyInfo();

            uNamePI.setName("Pass");
            uNamePI.setValue("haloX34fcidj");
            uNamePI.setType(String.class);

            request.addProperty(uNamePI);

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

                    users_Kullanici_ID = jsonArray.getJSONObject(i).getString("Kullanici_Id");
                    users_Profil_Adi = jsonArray.getJSONObject(i).getString("Profil_Adi");

                    userList.add(users_Profil_Adi);
                    userId.add(users_Kullanici_ID);

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
        protected void onPreExecute() {
            new AsynProjeler().execute();
        }
    }

    private class AsynProjeler extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {

            String NAMESPACE = "http://tempuri.org/";
            String URL = "http://istakip.goktekinenerji.com/SERVISLER/WebServiceIsTakip.asmx";//Make sure you changed IP address
            String SOAP_ACTION = "http://tempuri.org/JListeleProjeler";
            String METHOD_NAME = "JListeleProjeler";

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            PropertyInfo masterIdPI = new PropertyInfo();

            masterIdPI.setName("Pass");
            masterIdPI.setValue("haloX34fcidj");
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

                    usersProjectId = jsonArray.getJSONObject(i).getString("Proje_Id");
                    usersProjectName = jsonArray.getJSONObject(i).getString("Proje_Adi");

                    projectId.add(usersProjectId);
                    projectName.add(usersProjectName);

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
    }

    private class WebServiceLoginUsers extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {

            String NAMESPACE = "http://tempuri.org/";
            String URL = "http://istakip.goktekinenerji.com/SERVISLER/WebServiceIsTakip.asmx";//Make sure you changed IP address
            String SOAP_ACTION = "http://tempuri.org/JListeleKullaniciBilgileri";
            String METHOD_NAME = "JListeleKullaniciBilgileri";

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            PropertyInfo uNamePI = new PropertyInfo();
            PropertyInfo passPI = new PropertyInfo();

            uNamePI.setName("Pass");
            uNamePI.setValue("haloX34fcidj");
            uNamePI.setType(String.class);

            request.addProperty(uNamePI);

            passPI.setName("KullaniciId");
            passPI.setValue(Astatus);
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

                    profil_adi = jsonArray.getJSONObject(i).getString("Profil_Adi");
                    mail = jsonArray.getJSONObject(i).getString("Mail");
                    profil_url = jsonArray.getJSONObject(i).getString("Profil_Url");

                    menuUserList.add(profil_adi);
                    menuMailList.add(mail);
                    menuUrlList.add(profil_url);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            linlaHeaderProgressWS.setVisibility(View.GONE);

            kb_profil_adi = profil_adi.toString();
            kb_mail = mail.toString();
            kb_profil_url = profil_url.toString();

            new AsynKullanicilar().execute();
            startActivity(new Intent(LoginWebServiceQuery.this, Navigation_Drawer.class));

        }

        @Override
        protected void onPreExecute() {
            linlaHeaderProgressWS.setVisibility(View.VISIBLE);
        }
    }
}
