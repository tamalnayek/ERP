package com.istakip.AlinanGorevler;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.istakip.Navigation_Drawer;
import com.istakip.NetworkReceiver;
import com.istakip.R;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.istakip.AlinanGorevler.NavAlinanGorevlerim.text_gorevGorevId;
import static com.istakip.LoginScreen.loginStatus;


public class ImageUpload extends Activity {

    String encodedImage;
    byte b[];
    String status, filestatus;
    File destination;
    FileOutputStream fo;
    Button btnUpload;
    EditText img_name, img_explanation;
    String upload_name, upload_explanation;
    String filePath;
    String encodedBase64 = null;
    String extension = null;
    Boolean isCheckedImage = false;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private Button btnSelect;
    private ImageView ivImage;
    private String userChoosenTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_upload);

        img_name = (EditText) findViewById(R.id.img_name);
        img_explanation = (EditText) findViewById(R.id.img_explanation);

        btnSelect = (Button) findViewById(R.id.btnSelectPhoto);
        btnSelect.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        ivImage = (ImageView) findViewById(R.id.ivImage);

        btnUpload = (Button) findViewById(R.id.btnUploadPhoto);
        btnUpload.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (img_name.getText().length() != 0 && img_name.getText().toString() != "") {
                    if (img_explanation.getText().length() != 0 && img_explanation.getText().toString() != "") {

                        upload_name = img_name.getText().toString();
                        upload_explanation = img_explanation.getText().toString();

                        checkNetwork();

                    } else {
                        Toast.makeText(ImageUpload.this, "Lütfen açıklama giriniz!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ImageUpload.this, "Lütfen dosya adı giriniz!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void checkNetwork() {
        if (NetworkReceiver.getInstance(this).isOnline()) {

            Log.v("Network Connection", "You are online!!!!");
            if (isCheckedImage) {
                new Base64WS().execute();
            } else {
                new Base64File().execute();
            }

        } else {
            android.support.v7.app.AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new android.support.v7.app.AlertDialog.Builder(ImageUpload.this, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new android.support.v7.app.AlertDialog.Builder(ImageUpload.this);
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case 10001: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(ImageUpload.this, "Permisson granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ImageUpload.this, "Permisson not granted", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if (userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }

    private void selectImage() {
        final CharSequence[] items = {"Kamera", "Galeri", "Dosyalarım",
                "Geri"};

        AlertDialog.Builder builder = new AlertDialog.Builder(ImageUpload.this);
        builder.setTitle("Fotoğraf Seç");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(ImageUpload.this);

                if (items[item].equals("Kamera")) {
                    userChoosenTask = "Kamera";
                    if (result)
                        cameraIntent();

                } else if (items[item].equals("Galeri")) {
                    userChoosenTask = "Galeri";
                    if (result)
                        galleryIntent();

                } else if (items[item].equals("Dosyalarım")) {
                    userChoosenTask = "Dosyalarım";
                    if (result)
                        documentIntent();

                } else if (items[item].equals("Geri")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void documentIntent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 10001);
                return;
            }
        }
        new MaterialFilePicker()
                .withActivity(ImageUpload.this)
                .withRequestCode(1000)
                .withHiddenFiles(true) // Show hidden files and folders
                .start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000 && resultCode == RESULT_OK) {
            filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            Toast.makeText(ImageUpload.this, "Dosya Seçildi", Toast.LENGTH_LONG).show();
            File originalFile = new File(filePath);
            try {
                FileInputStream fileInputStreamReader = new FileInputStream(originalFile);
                byte[] bytes = new byte[(int) originalFile.length()];
                fileInputStreamReader.read(bytes);
                // encodedBase64 = new String(Base64.encodeToString(bytes,Base64.NO_WRAP));

                encodedBase64 = Base64.encodeToString(bytes, Base64.NO_WRAP);
                extension = filePath.substring(filePath.lastIndexOf("."));


                Log.e("encodedBase64", encodedBase64);
                Log.e("extension", extension);
                isCheckedImage = false;
                ivImage.setImageDrawable(getResources().getDrawable(R.mipmap.ic_file));
                // new Base64File().execute(); // Run File Upload Service

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }


    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void onCaptureImageResult(Intent data) {
        encodedImage = "";
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, baos);
        b = baos.toByteArray();

        destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        // FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(baos.toByteArray());
            fo.close();

            encodedImage = Base64.encodeToString(b, Base64.NO_WRAP);
            Log.e("Encoded Base64 Camera", encodedImage);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        isCheckedImage = true;
        ivImage.setImageBitmap(thumbnail);

    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        encodedImage = "";
        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 90, baos);
                b = baos.toByteArray();

                encodedImage = Base64.encodeToString(b, Base64.NO_WRAP);
                Log.e("Encoded Base64 Gallery", encodedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        isCheckedImage = true;
        ivImage.setImageBitmap(bm);

    }

    private class Base64WS extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            String NAMESPACE = "http://tempuri.org/";
            String URL = "http://istakip.goktekinenerji.com/SERVISLER/WebServiceIsTakip.asmx";//Make sure you changed IP address
            String SOAP_ACTION = "http://tempuri.org/Dokuman_Paylas_AlinanGorev";
            String METHOD_NAME = "Dokuman_Paylas_AlinanGorev";

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            PropertyInfo uNamePI = new PropertyInfo();
            PropertyInfo passPI = new PropertyInfo();
            PropertyInfo base64 = new PropertyInfo();
            PropertyInfo f = new PropertyInfo();
            PropertyInfo aciklama = new PropertyInfo();
            PropertyInfo uzanti = new PropertyInfo();
            PropertyInfo gorevId = new PropertyInfo();

            base64.setName("dosya_adi");
            base64.setValue(upload_name);
            base64.setType(String.class);

            request.addProperty(base64);

            aciklama.setName("dosya_aciklama");
            aciklama.setValue(upload_explanation);
            aciklama.setType(String.class);

            request.addProperty(aciklama);

            uzanti.setName("dosya_uzanti");
            uzanti.setValue(".jpg");
            uzanti.setType(String.class);

            request.addProperty(uzanti);

            f.setName("f");
            f.setValue(encodedImage);
            f.setType(Base64.class);

            request.addProperty(f);

            uNamePI.setName("Pass");
            uNamePI.setValue("haloX34fcidj");
            uNamePI.setType(String.class);

            request.addProperty(uNamePI);

            passPI.setName("Personel_Id");
            passPI.setValue(loginStatus);
            passPI.setType(String.class);

            request.addProperty(passPI);

            gorevId.setName("Gorev_Id");
            gorevId.setValue(text_gorevGorevId);
            gorevId.setType(String.class);

            request.addProperty(gorevId);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE httpTransportSE = new HttpTransportSE(URL);
            httpTransportSE.debug = true;

            try {

                httpTransportSE.call(SOAP_ACTION, envelope);

                SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
                status = (response.toString());

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
            if (status.equals("Dosya Yüklendi!")) {
                Toast.makeText(ImageUpload.this, status, Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(ImageUpload.this, "Lütfen Tekrar Deneyiniz!", Toast.LENGTH_LONG).show();
                startActivity(new Intent(ImageUpload.this, Navigation_Drawer.class));
            }
        }
    }


    private class Base64File extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            String NAMESPACE = "http://tempuri.org/";
            String URL = "http://istakip.goktekinenerji.com/SERVISLER/WebServiceIsTakip.asmx";//Make sure you changed IP address
            String SOAP_ACTION = "http://tempuri.org/Dokuman_Paylas_AlinanGorev";
            String METHOD_NAME = "Dokuman_Paylas_AlinanGorev";

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            PropertyInfo uNamePI = new PropertyInfo();
            PropertyInfo passPI = new PropertyInfo();
            PropertyInfo base64 = new PropertyInfo();
            PropertyInfo f = new PropertyInfo();
            PropertyInfo aciklama = new PropertyInfo();
            PropertyInfo uzanti = new PropertyInfo();
            PropertyInfo gorevId = new PropertyInfo();

            f.setName("f");
            f.setValue(encodedBase64);
            f.setType(Base64.class);

            request.addProperty(f);

            base64.setName("dosya_adi");
            base64.setValue(upload_name);
            base64.setType(String.class);

            request.addProperty(base64);

            aciklama.setName("dosya_aciklama");
            aciklama.setValue(upload_explanation);
            aciklama.setType(String.class);

            request.addProperty(aciklama);

            uzanti.setName("dosya_uzanti");
            uzanti.setValue(extension);
            uzanti.setType(String.class);

            request.addProperty(uzanti);

            uNamePI.setName("Pass");
            uNamePI.setValue("haloX34fcidj");
            uNamePI.setType(String.class);

            request.addProperty(uNamePI);

            passPI.setName("Personel_Id");
            passPI.setValue(loginStatus);
            passPI.setType(String.class);

            request.addProperty(passPI);

            gorevId.setName("Gorev_Id");
            gorevId.setValue(text_gorevGorevId);
            gorevId.setType(String.class);

            request.addProperty(gorevId);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE httpTransportSE = new HttpTransportSE(URL);
            httpTransportSE.debug = true;

            try {

                httpTransportSE.call(SOAP_ACTION, envelope);

                SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
                filestatus = (response.toString());

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
            if (filestatus.equals("Dosya Yüklendi!")) {
                Toast.makeText(ImageUpload.this, filestatus, Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(ImageUpload.this, "Lütfen Tekrar Deneyiniz!", Toast.LENGTH_LONG).show();
                startActivity(new Intent(ImageUpload.this, Navigation_Drawer.class));
            }
        }
    }
}
