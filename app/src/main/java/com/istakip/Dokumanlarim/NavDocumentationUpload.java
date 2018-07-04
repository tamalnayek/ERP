package com.istakip.Dokumanlarim;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.istakip.NetworkReceiver;
import com.istakip.R;

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
import java.util.HashMap;

import static android.content.Context.DOWNLOAD_SERVICE;
import static com.istakip.LoginWebServiceQuery.Astatus;

/**
 * Created by Ask on 13.9.2017.
 */
public class NavDocumentationUpload extends Fragment {

    public static ArrayList<HashMap<String, String>> dokumanlarList;
    static String dokuman_adi, dokuman_aciklama, dokuman_tarihi, dokuman_URL, dokuman_uzanti, dokuman_indir;
    static String text_file_URL;
    String indir_file_URL;

    ArrayList<String> indir_file_list;
    HashMap<String, String> contact_doc;

    View myView;
    ListView listView;

    LinearLayout nav_documentation_linear_layout;
    SwipeRefreshLayout swipeRefreshLayout;

    private DownloadManager downloadManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        myView = inflater.inflate(R.layout.activity_nav_documentation, container, false);

        dokumanlarList = new ArrayList<>();
        indir_file_list = new ArrayList<>();

        listView = (ListView) myView.findViewById(R.id.documentation_list_id);
        nav_documentation_linear_layout = (LinearLayout) myView.findViewById(R.id.nav_documentation_linear_layout);

        checkNetwork();

        swipeRefreshLayout = (SwipeRefreshLayout) myView.findViewById(R.id.swipe_documentation);
        swipeRefreshLayout.setColorSchemeResources(R.color.darkred, R.color.colorBlue, R.color.colorpurple);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                Log.d("Swipe_Documentation", "Refresh Number");
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        dokumanlarList.clear();
                        indir_file_list.clear();
                        checkNetwork();
                    }
                }, 1000);
            }
        });
        return myView;
    }

    private void checkNetwork() {

        if (NetworkReceiver.getInstance(getContext()).isOnline()) {
            Log.v("Network Connection", "You are online!!!!");
            new WebServiceDocumentation().execute();

        } else {
            android.support.v7.app.AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new android.support.v7.app.AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new android.support.v7.app.AlertDialog.Builder(getContext());
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
                    getActivity().getFragmentManager().popBackStack();
                    System.exit(0);
                }
            }).setIcon(R.drawable.nuclear_alert)
                    .setCancelable(false)
                    .show();
            Log.e("Network Connection", "############################You are not online!!!!");
        }
    }

    private class WebServiceDocumentation extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {

            String NAMESPACE = "http://tempuri.org/";
            String URL = "http://istakip.goktekinenerji.com/SERVISLER/WebServiceIsTakip.asmx";//Make sure you changed IP address
            String SOAP_ACTION = "http://tempuri.org/JListele_Dokumanlarim";
            String METHOD_NAME = "JListele_Dokumanlarim";

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            PropertyInfo masterIdPI = new PropertyInfo();
            PropertyInfo passPI = new PropertyInfo();

            masterIdPI.setName("Kullanici_Id");
            masterIdPI.setValue(Astatus);
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
                String responseJSON = response.toString();
                JSONArray jsonArray = new JSONArray(responseJSON);
                final int numberOfItemsInPesp = responseJSON.length();

                for (int i = 0; i < numberOfItemsInPesp; i++) {

                    dokuman_adi = jsonArray.getJSONObject(i).getString("Dokuman_Adi");
                    dokuman_aciklama = jsonArray.getJSONObject(i).getString("Dokuman_Aciklama");
                    dokuman_tarihi = jsonArray.getJSONObject(i).getString("Tarih");
                    dokuman_URL = jsonArray.getJSONObject(i).getString("Document_Url");
                    dokuman_uzanti = jsonArray.getJSONObject(i).getString("Uzanti");
                    dokuman_indir = jsonArray.getJSONObject(i).getString("Document_Indir");

                    contact_doc = new HashMap<>();

                    contact_doc.put("Dokuman_Adi", dokuman_adi);
                    contact_doc.put("Dokuman_Aciklama", dokuman_aciklama);
                    contact_doc.put("Tarih", dokuman_tarihi);
                    contact_doc.put("Document_Url", dokuman_URL);
                    contact_doc.put("Uzanti", dokuman_uzanti);

                    dokumanlarList.add(contact_doc);
                    indir_file_list.add(dokuman_indir);
                }
            } catch (IOException e) {
                Log.e("1", "IOException WebServiceDocumentation");
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                Log.e("2", "XmlPullParserException WebServiceDocumentation");
                e.printStackTrace();
            } catch (JSONException e) {
                Log.e("3", "JSONException WebServiceDocumentation");
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            if (dokumanlarList.isEmpty()) {
                android.support.v7.app.AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new android.support.v7.app.AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new android.support.v7.app.AlertDialog.Builder(getContext());
                }
                builder.setTitle("Oops")
                        .setMessage("Henüz dosya bulunmamaktadır!")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setIcon(R.drawable.nuclear_alert)
                        .setCancelable(false)
                        .show();
            }

            ListAdapter adapter = new SimpleAdapter(getActivity(), dokumanlarList,
                    R.layout.activity_list_documentation, new String[]{"Dokuman_Adi", "Dokuman_Aciklama", "Tarih", "Uzanti"},
                    new int[]{R.id.dokuman_adi, R.id.dokuman_aciklama, R.id.dokuman_tarih, R.id.documan_uzanti});

            listView.setAdapter(adapter);
            nav_documentation_linear_layout.setVisibility(View.GONE);
        }

        @Override
        protected void onPreExecute() {
            nav_documentation_linear_layout.setVisibility(View.VISIBLE);

            listView.setOnScrollListener(new AbsListView.OnScrollListener() { //PAGE SCROLL NOT REFRESH
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    if (listView.getChildAt(0) != null) {
                        swipeRefreshLayout.setEnabled(listView.getFirstVisiblePosition() == 0 && listView.getChildAt(0).getTop() == 0);
                    }
                }
            });

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
                    HashMap<String, Object> obj = (HashMap<String, Object>) listView.getItemAtPosition(position);

                    text_file_URL = (String) obj.get("Document_Url");

                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(getContext());
                    }

                    builder.setTitle("İndir")
                            .setMessage("Emin Misiniz?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    indir_file_URL = indir_file_list.get(position).toString();

                                    downloadManager = (DownloadManager) getActivity().getSystemService(DOWNLOAD_SERVICE);
                                    Uri uri = Uri.parse("http://" + indir_file_URL);
                                    DownloadManager.Request request = new DownloadManager.Request(uri);
                                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                    Long reference = downloadManager.enqueue(request);

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
                }
            });
        }
    }
}
  /*
    private long DownloadFile(Uri uri){
        long downloadReference = 0;

        downloadManager = (DownloadManager)getActivity().getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setTitle("Data Download");
        request.setDescription("Android Data download using DownloadManager.");
        request.setDestinationInExternalFilesDir(getContext(),Environment.DIRECTORY_DOWNLOADS,"AndroidTutorialPoint.jpg");

        downloadReference = downloadManager.enqueue(request);

        return downloadReference;
    }

    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            //check if the broadcast message is for our Enqueued download
            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

            if(referenceId == document_downloadId) {

                Toast toast = Toast.makeText(getContext(),
                        "Image Download Complete", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, 25, 400);
                toast.show();
            }
        }
    };*/

