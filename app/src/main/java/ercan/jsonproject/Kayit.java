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

public class Kayit extends AppCompatActivity {
    EditText ad,soyad,tel,mail,sifre;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_kayit);


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


        ad = (EditText) findViewById(R.id.ad);
        soyad = (EditText) findViewById(R.id.soyad);
        tel = (EditText) findViewById(R.id.telefon);
        mail = (EditText) findViewById(R.id.mail);
        sifre = (EditText) findViewById(R.id.sifre);
    }

    public void Kaydet(View view)
    {
        String ad = this.ad.getText().toString();
        String soyad = this.soyad.getText().toString();
        String tel = this.tel.getText().toString();
        String mail = this.mail.getText().toString();
        String sifre = this.sifre.getText().toString();

        if (ad.isEmpty() || soyad.isEmpty() || tel.isEmpty() || mail.isEmpty() || sifre.isEmpty()) {
            if (ad.isEmpty()) {
                Toast.makeText(Kayit.this, "Lütfen Adınızı Yazın.", Toast.LENGTH_SHORT).show();
                this.ad.requestFocus();
            }
            if (soyad.isEmpty()) {
                Toast.makeText(Kayit.this, "Lütfen Soyadınızı Yazın.", Toast.LENGTH_SHORT).show();
                this.soyad.requestFocus();
            }
            if (mail.isEmpty()) {
                Toast.makeText(Kayit.this, "Lütfen Mail Adresinizi Yazın.", Toast.LENGTH_SHORT).show();
                this.mail.requestFocus();
            }
            if (tel.isEmpty()) {
                Toast.makeText(Kayit.this, "Lütfen Telefon Numaranızı Yazın.", Toast.LENGTH_SHORT).show();
                this.tel.requestFocus();
            }
            if (sifre.isEmpty()) {
                Toast.makeText(Kayit.this, "Lütfen Şifrenizi Yazın.", Toast.LENGTH_SHORT).show();
                this.sifre.requestFocus();
            }

        } else {
            String url = "http://jsonbulut.com/json/userRegister.php?ref=cb226ff2a31fdd460087fedbb34a6023&" +
                    "userName=" + ad + "&" +
                    "userSurname=" + soyad + "&" +
                    "userPhone=" + tel + "&" +
                    "userMail=" + mail + "&" +
                    "userPass=" + sifre + "";

            this.ad.setText("");
            this.soyad.setText("");
            this.tel.setText("");
            this.mail.setText("");
            this.sifre.setText("");

            new jsonData(url, Kayit.this).execute();
        }

    }

    class jsonData extends AsyncTask<Void, Void, Void> {
        String url = "";
        String data = "";
        Context cnx;

        ProgressDialog pro;

        public jsonData(String url, Context cnx) {
            this.url = url;
            this.cnx = cnx;

            pro = new ProgressDialog(cnx);
            pro.setMessage("İşlem yaplıyor. Lütfen Bekleyiniz.");
            pro.show();

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

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
                    Toast.makeText(cnx, mesaj, Toast.LENGTH_SHORT).show();
                    String kid = obj.getJSONArray("user").getJSONObject(0).getString("kullaniciId");
                    Log.e("kid", kid);

                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = preferences.edit();

                    editor.putString("kmail", mail.getText().toString());
                    editor.putString("ksifre", sifre.getText().toString());

                    editor.commit();

                    Intent i = new Intent(Kayit.this,Giris_Ekrani.class);
                    startActivity(i);
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