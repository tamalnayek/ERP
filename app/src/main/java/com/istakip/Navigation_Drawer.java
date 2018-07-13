package com.istakip;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.internal.NavigationMenu;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.istakip.AlinanGorevler.ImageUpload;
import com.istakip.AlinanGorevler.NavAlinanGorevlerim;
import com.istakip.Dokumanlarim.NavDocumentationUpload;
import com.istakip.OnayBekleyenGorevler.NavOnayBekleyenGorevler;
import com.istakip.Projeler.NavProjeler;
import com.istakip.TamamlananGorevler.NavTamamlananGorevler;
import com.istakip.VerilenGorevler.NavVerilenGorevler;
import com.squareup.picasso.Picasso;

import io.github.yavski.fabspeeddial.FabSpeedDial;

import static com.istakip.LoginScreen.etPassword;
import static com.istakip.LoginScreen.etUsername;
import static com.istakip.LoginWebServiceQuery.kb_mail;
import static com.istakip.LoginWebServiceQuery.kb_profil_adi;
import static com.istakip.LoginWebServiceQuery.kb_profil_url;

public class Navigation_Drawer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    FragmentTransaction fragmentTransaction;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation__drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FabSpeedDial fab = (FabSpeedDial) findViewById(R.id.fabSpeed);
        fab.setMenuListener(new FabSpeedDial.MenuListener() {
            @Override
            public boolean onPrepareMenu(NavigationMenu navigationMenu) {
                return true;
            }

            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {

                String menuFab = menuItem.getTitle().toString(); //Menuden secilen itemin adını alır
                switch (menuFab) {
                    case "Görev":
                        startActivity(new Intent(getApplicationContext(), NavYeniGorevEkle.class));
                        break;
                    case "Döküman":
                        startActivity(new Intent(getApplicationContext(), ImageUpload.class));
                        break;
                    default:
                        Toast.makeText(getApplicationContext(), "Hata lütfen tekrar deneyimiz", Toast.LENGTH_LONG).show();
                }
                return false;
            }

            @Override
            public void onMenuClosed() {

            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.content_frame, new NavAlinanGorevlerim()).commit();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View hView = navigationView.getHeaderView(0);
        TextView user_name = (TextView) hView.findViewById(R.id.nav_menu_user_name);
        // user_name.setText(menuUserList.toString());
        user_name.setText(kb_profil_adi);

        View h1View = navigationView.getHeaderView(0);
        TextView mail = (TextView) h1View.findViewById(R.id.nav_menu_mail);
        mail.setText(kb_mail);

        View h2View = navigationView.getHeaderView(0);
        ImageView imgview = (ImageView) h2View.findViewById(R.id.nav_menu_image);
        Picasso.with(getApplicationContext()).load("http://" + kb_profil_url).into(imgview);

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setLogo(R.drawable.logo);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Çıkış")
                    .setMessage("Emin Misiniz?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            moveTaskToBack(true);

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation__drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.log_out) {

         //   preferences = PreferenceManager.getDefaultSharedPreferences(this);
         //   editor = preferences.edit();
         //   editor.clear().apply();
            moveTaskToBack(true);
            //finish();
            // getFragmentManager().popBackStack();
            // System.exit(0);
            //startActivity(new Intent(getApplicationContext(), LoginScreen.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        // getSupportActionBar().setTitle("Görevlerim");

        int id = item.getItemId();
        Fragment fragment;
        if (id == R.id.nav_gorevlerim) {
            fragment = new NavAlinanGorevlerim();
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.content_frame, fragment)
                    .commit();
            // getSupportActionBar().setTitle("Görevlerim");
        } else if (id == R.id.nav_verilen_gorevler) {
            fragment = new NavVerilenGorevler();
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.content_frame, fragment)
                    .commit();
            // getSupportActionBar().setTitle("Verilen Görevler");
        } else if (id == R.id.nav_projeler) {
            fragment = new NavProjeler();
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.content_frame, fragment)
                    .commit();
            // getSupportActionBar().setTitle("Tüm Projeler");
        } else if (id == R.id.nav_documentation) {
            fragment = new NavDocumentationUpload();
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.content_frame, fragment)
                    .commit();
            //getSupportActionBar().setTitle("Döküman Ekle");
        } else if (id == R.id.nav_tamamlanan_gorevler) {
            fragment = new NavTamamlananGorevler();
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.content_frame, fragment)
                    .commit();
            //getSupportActionBar().setTitle("Döküman Ekle");
        } else if (id == R.id.nav_onay_bekleyen_gorevler) {
            fragment = new NavOnayBekleyenGorevler();
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.content_frame, fragment)
                    .commit();
            //getSupportActionBar().setTitle("Döküman Ekle");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
