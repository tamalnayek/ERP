package com.istakip;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class LoginScreen extends AppCompatActivity {

    private static final int REQUEST_CODE = 1;
    private static final String TAG = "PERMISSONS";
    static EditText etUsername, etPassword;
    String loginStatus;
    String userName, userPassword;
    SharedPreferences preferences;
    SharedPreferences.Editor editorPref;
    Button bLogin;
    LinearLayout linlaHeaderProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        verifyPermissons();

        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);

        linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editorPref = preferences.edit();

        if (preferences.contains("register")) {
            String getStatus = preferences.getString("register", "false");
            if (getStatus.equals("true")) {
                startActivity(new Intent(LoginScreen.this, LoginWebServiceQuery.class));
                finish();
            } else {
                //first time

                editorPref = preferences.edit();
                editorPref.putString("key", loginStatus);
                editorPref.putString("register", "true");
                editorPref.apply();
                ///  show registration page again
            }
        } else { //first time
            editorPref = preferences.edit();
            editorPref.putString("key", loginStatus);
            editorPref.putString("register", "true");
            editorPref.apply();
            ///  show registration page again
        }

        bLogin = (Button) findViewById(R.id.bLogin);
        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (etUsername.getText().length() != 0 && etUsername.getText().toString() != "") {
                    if (etPassword.getText().length() != 0 && etPassword.getText().toString() != "") {

                        userName = etUsername.getText().toString();
                        userPassword = etPassword.getText().toString();

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

        } else {
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
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
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            linlaHeaderProgress.setVisibility(View.GONE);

            if (!loginStatus.equals("Hatalı Kullanıcı Adı veya Şifre!")) {

                editorPref.putString("key", loginStatus);
                editorPref.putString("register", "true");
                editorPref.apply();

                startActivity(new Intent(LoginScreen.this, LoginWebServiceQuery.class));
            } else {
                Toast.makeText(LoginScreen.this, "Hatalı Kullanıcı Adı veya Şifre!", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onPreExecute() {
            editorPref = preferences.edit();
            editorPref.putString("key", loginStatus);
            editorPref.putString("register", "true");
            editorPref.apply();
            linlaHeaderProgress.setVisibility(View.VISIBLE);
        }
    }
}
