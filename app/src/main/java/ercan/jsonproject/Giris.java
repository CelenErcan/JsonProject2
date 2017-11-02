package ercan.jsonproject;

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
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.IOException;

public class Giris extends AppCompatActivity {
    EditText mail, sifre;
    String maill,sifree;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_giris);


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String mail1 = preferences.getString("kmail", "bos");
        String sifre1 = preferences.getString("ksifre", "bos");

        if(mail1.equals("bos") || sifre1.equals("bos"))
        {
            //Toast.makeText(this, mail+" "+sifre, Toast.LENGTH_SHORT).show();
        }
        else
        {
            String url = "http://jsonbulut.com/json/userLogin.php?ref=cb226ff2a31fdd460087fedbb34a6023&" +
                    "userEmail="+mail1+"&" +
                    "userPass="+sifre1+"&face=no";
            new jsonData(url,this).execute();
            Toast.makeText(this, "Başka Bir Kayıt İçin Lütfen Çıkış Yapın", Toast.LENGTH_SHORT).show();
            finish();

        }

        mail = (EditText) findViewById(R.id.girisMail);
        sifre = (EditText) findViewById(R.id.girisSifre);


    }

    void giris(String mail, String sifre)
    {
    this.maill= mail;
    this.sifree=sifre;

    Log.e("asdsaascsac",maill+" "+sifree);
        if(maill.isEmpty() || sifree.isEmpty())
        {
            if (maill.isEmpty())
            {
                Toast.makeText(this, "Mail Adresinizi Girin", Toast.LENGTH_SHORT).show();
                this.mail.requestFocus();
            }
            if (sifree.isEmpty())
            {
                Toast.makeText(this, "Şifrenizi Girin", Toast.LENGTH_SHORT).show();
                this.sifre.requestFocus();
            }
        }
        else
        {
            String url = "http://jsonbulut.com/json/userLogin.php?ref=cb226ff2a31fdd460087fedbb34a6023&" +
                    "userEmail="+maill+"&" +
                    "userPass="+sifree+"&face=no";

            new jsonData(url, Giris.this).execute();
        }

    }
    public void Giris(View view)
    {
        maill = mail.getText().toString();
        sifree = sifre.getText().toString();
        giris(maill,sifree);
    }

    class jsonData extends AsyncTask<Void, Void, Void>
    {
        String url = "";
        String data = "";
        Context cnx;

        ProgressDialog pro;

        public jsonData(String url, Context cnx) {
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

                if (durum) {
                    String kid = obj.getJSONArray("user").getJSONObject(0).getJSONObject("bilgiler").getString("userId");
                    Toast.makeText(cnx, "Kullanıcı ID si= "+kid, Toast.LENGTH_SHORT).show();
                    Toast.makeText(cnx, mesaj, Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(Giris.this,Giris_Ekrani.class);
                    startActivity(i);

                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = preferences.edit();

                    editor.putString("kmail", mail.getText().toString());
                    editor.putString("ksifre", sifre.getText().toString());

                    editor.commit();

                    finish();
                } else {
                    Toast.makeText(cnx, mesaj, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            pro.dismiss();
        }
    }


}
