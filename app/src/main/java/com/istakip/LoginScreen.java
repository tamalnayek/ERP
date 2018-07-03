package com.istakip;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

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

public class LoginScreen extends AppCompatActivity {

    private static final int REQUEST_CODE = 1;
    private static final String TAG = "PERMISSONS";
    public static String loginStatus;
    public static String kb_profil_adi, kb_mail, kb_profil_url;
    static SharedPreferences pref;
    static SharedPreferences.Editor editor;
    static String users_Kullanici_ID;
    static String users_Profil_Adi;
    static String usersProjectId;
    static String usersProjectName;
    static ArrayList<String> userList, userId, menuUserList, menuMailList, menuUrlList;
    static ArrayList<String> projectId, projectName;
    static String profil_adi, mail, profil_url;
    CheckBox ch;
    String userName, userPassword;
    EditText etUsername, etPassword;
    Button bLogin;
    LinearLayout linlaHeaderProgress;
    IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
    private boolean saveLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        verifyPermissons();

        ch = (CheckBox) findViewById(R.id.ch_rememberme);

        //    pref = getApplicationContext().getSharedPreferences(loginStatus, MODE_PRIVATE);
        //    blNagSetting = pref.getBoolean("boolean", false);
        //
        //    if (blNagSetting == true) {
        //
        //        Intent startMainPage = new Intent(LoginScreen.this, Navigation_Drawer.class);
        //        startMainPage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //
        //        startActivity(startMainPage);
        //        finish();
        //    }


        userList = new ArrayList<>();
        userId = new ArrayList<>();

        projectId = new ArrayList<>();
        projectName = new ArrayList<>();

        menuUserList = new ArrayList<>();
        menuMailList = new ArrayList<>();
        menuUrlList = new ArrayList<>();

        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);

        linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress);

        pref = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        editor = pref.edit();

        saveLogin = pref.getBoolean("saveLogin", false);
        if (saveLogin == true) {
            etUsername.setText(pref.getString("username", ""));
            etPassword.setText(pref.getString("password", ""));
            ch.setChecked(true);
        }

        bLogin = (Button) findViewById(R.id.bLogin);
        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (etUsername.getText().length() != 0 && etUsername.getText().toString() != "") {
                    if (etPassword.getText().length() != 0 && etPassword.getText().toString() != "") {


                        userName = etUsername.getText().toString();
                        userPassword = etPassword.getText().toString();

                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(etUsername.getWindowToken(), 0);
                        if (ch.isChecked()) {
                            editor.putBoolean("saveLogin", true);
                            editor.putString("username", userName);
                            editor.putString("password", userPassword);
                            editor.commit();
                        } else {
                            editor.clear();
                            editor.commit();
                        }

                        // receiver = new NetworkReceiver(); // Network eski kodlar
                        //  registerReceiver(receiver, filter);
                        checkNetwork();

                    } else {
                        Toast.makeText(LoginScreen.this, "Lütfen şifrenizi giriniz!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(LoginScreen.this, "Lütfen kullanıcı adını giriniz!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void verifyPermissons() {
        Log.d(TAG, "VerifyPermissions: asking user for Permission");
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), permissions[0]) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(), permissions[1]) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(), permissions[2]) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(LoginScreen.this, permissions, REQUEST_CODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        verifyPermissons();
    }

    private void checkNetwork() {

        if (NetworkReceiver.getInstance(this).isOnline()) {

            Log.v("Network Connection", "You are online!!!!");

            new AsynCallWSLogin().execute();

            new AsynProjeler().execute();

        } else {
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(LoginScreen.this, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(LoginScreen.this);
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
   /* @Override
    protected void onDestroy() {  //Network baglantısı yok ise destroy işlemi
        super.onDestroy();
        unregisterReceiver(receiver);
    }
*/

    private class AsynCallWSLogin extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {

            String NAMESPACE = "http://tempuri.org/";
            String URL = "http://istakip.goktekinenerji.com/SERVISLER/WebServiceIsTakip.asmx";//Make sure you changed IP address
            String SOAP_ACTION = "http://tempuri.org/KullaniciGirisi";
            String METHOD_NAME = "KullaniciGirisi";

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            PropertyInfo uNamePI = new PropertyInfo();
            PropertyInfo passPI = new PropertyInfo();

            uNamePI.setName("Kullanici_Adi");
            uNamePI.setValue(userName);
            uNamePI.setType(String.class);

            request.addProperty(uNamePI);

            passPI.setName("Sifre");
            passPI.setValue(userPassword);
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
                loginStatus = (response.toString());

            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                //LoginScreen.errored = true;
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            linlaHeaderProgress.setVisibility(View.GONE);
            // Toast.makeText(LoginScreen.this, loginStatus, Toast.LENGTH_LONG).show();
            if (!loginStatus.equals("Hatalı Kullanıcı Adı veya Şifre!")) {

                // editor = pref.edit();
                // editor.putString("loginStatus", loginStatus);
                // loginStatus = pref.getString("loginStatus", null);

                // if (ch.isChecked()) {
                //     Toast.makeText(LoginScreen.this, "Checkhed :" + loginStatus, Toast.LENGTH_SHORT).show();
                //     editor.putBoolean("boolean", true);
                // } else {
                //     Toast.makeText(LoginScreen.this, "Not Checked :" + loginStatus, Toast.LENGTH_SHORT).show();
                //     editor.putBoolean("boolean", false);
                // }

                // editor.commit();

                new WebServiceLoginUsers().execute();

            } else {
                Toast.makeText(LoginScreen.this, "Hatalı Kullanıcı Adı veya Şifre!", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onPreExecute() {

            linlaHeaderProgress.setVisibility(View.VISIBLE);

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
            passPI.setValue(loginStatus);
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
                //LoginScreen.errored = true;
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            linlaHeaderProgress.setVisibility(View.GONE);

            kb_profil_adi = profil_adi.toString();
            kb_mail = mail.toString();
            kb_profil_url = profil_url.toString();

            // Toast.makeText(LoginScreen.this, menuUrlList.get(0), Toast.LENGTH_LONG).show();

            new AsynKullanicilar().execute();

            startActivity(new Intent(LoginScreen.this, Navigation_Drawer.class));

        }

        @Override
        protected void onPreExecute() {
            linlaHeaderProgress.setVisibility(View.VISIBLE);
        }
    }

}
