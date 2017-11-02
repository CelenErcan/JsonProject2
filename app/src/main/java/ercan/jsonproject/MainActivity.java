package ercan.jsonproject;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    boolean var = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        String mail = preferences.getString("kmail", "bos");
        String sifre = preferences.getString("ksifre", "bos");

        if(mail.equals("bos") || sifre.equals("bos"))
        {
           //Toast.makeText(this, mail+" "+sifre, Toast.LENGTH_SHORT).show();
        }
        else
        {
            var = true;
            String url = "http://jsonbulut.com/json/userLogin.php?ref=cb226ff2a31fdd460087fedbb34a6023&" +
                    "userEmail="+mail+"&" +
                    "userPass="+sifre+"&face=no";
            new jsonData(url,MainActivity.this, var).execute();
        }


    }

    public void KayitOl(View view)
    {
        Intent kayit = new Intent(MainActivity.this, Kayit.class);
        startActivity(kayit);
    }

    public void GirisYap(View view)
    {
        Intent i = new Intent(this,Giris.class);
        startActivity(i);
    }


    class jsonData extends AsyncTask<Void, Void, Void>
    {
        String url = "";
        String data = "";
        Context cnx;

        ProgressDialog pro;

        public jsonData(String url, Context cnx, boolean var) {
            this.url = url;
            this.cnx = cnx;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pro = new ProgressDialog(cnx);
            pro.setMessage("İşlem yaplıyor. Lütfen Bekleyiniz.");
            pro.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                data = Jsoup.connect(url).ignoreContentType(true).
                        get().body().text();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Data Json Hatası", "doinBackground: ", e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d("Gelen Data", data);

            try {
                JSONObject obj = new JSONObject(data);
                boolean durum = obj.getJSONArray("user").getJSONObject(0).getBoolean("durum");
                String mesaj = obj.getJSONArray("user").getJSONObject(0).getString("mesaj");

                if (durum)
                {

                        String kid = obj.getJSONArray("user").getJSONObject(0).getJSONObject("bilgiler").getString("userId");
                        Toast.makeText(cnx, "Kullanıcı ID si= " + kid, Toast.LENGTH_SHORT).show();
                        Toast.makeText(cnx, mesaj, Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(MainActivity.this, Giris_Ekrani.class);
                        startActivity(i);

                }
                else {
                    Toast.makeText(cnx, mesaj, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            pro.dismiss();
        }
    }
}
