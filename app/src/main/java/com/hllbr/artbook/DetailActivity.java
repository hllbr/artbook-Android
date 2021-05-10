package com.hllbr.artbook;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class DetailActivity extends AppCompatActivity {

    Bitmap selectedImage ;
    ImageView imageView ;
    EditText artname,paintername,yeartext;
    Button button;
    SQLiteDatabase database;
    String info;
/*
Bu alanda ekleme işlemlerini gerçekleştirmeye çalışıcam gerekli butonların yerleştirilmesi ve talimatların ifade edildiği bu ekran tasarımı gerekiyor.,
içerikte bulunacaklar eserin ismi yılı ve resim seçmek için bir ImageView

 */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        imageView = findViewById(R.id.imageView1);
        artname = findViewById(R.id.artnameText);
        paintername = findViewById(R.id.painternameText);
        yeartext = findViewById(R.id.yearText);
        button = findViewById(R.id.button);
        database = this.openOrCreateDatabase("Arts",MODE_PRIVATE,null);
        Intent intent = getIntent();
        info = intent.getStringExtra("info");
        if(info.matches("new")){
            artname.setText("");
            paintername.setText("");
            yeartext.setText("");
            button.setVisibility(View.VISIBLE);
            Bitmap selectI = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.sel);
            imageView.setImageBitmap(selectI);
        }else{
            int artId = intent.getIntExtra("ArtId",1);
            button.setVisibility(View.INVISIBLE);
            try{
                Cursor cursor = database.rawQuery("SELECT * FROM arts WHERE id = ?",new String[] {String.valueOf(artId)});
                int artnameIx = cursor.getColumnIndex("artname");
                int painternameIx = cursor.getColumnIndex("paintername");
                int yearIx = cursor.getColumnIndex("year");
                int imageIx = cursor.getColumnIndex("image");
                    while(cursor.moveToNext()){
                        artname.setText(cursor.getString(artnameIx));
                        paintername.setText(cursor.getString(painternameIx));
                        yeartext.setText(cursor.getString(yearIx));


                        byte[] bytes = cursor.getBlob(imageIx);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                        imageView.setImageBitmap(bitmap);

                    }
                    cursor.close();
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }
    public void selectImage(View view){
        //This area is for image selection operations.
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            //not allowed
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
            //requestPermissions == izinleri iste olarak ifade edilebilir.
        }else{
            //allowed
            //this area galery operations
            //Galeriye gitme işlemi activiteler arası geçişte kullandığımız gibi intent ile yapılıyor.
            if(info.matches("new")){
                Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);//normalde ikinci parametre olarak nereye gideceği yazılıyordu şimdi ise ne yapacağımızı yazıyoruz.
                //ACTION_PICK == TOPLA BİYERDEN BİŞEY YOPLAMAK OLARKA İFADE EDEBİLRİZ.
                //2. PARAMETRE GALERİDEN BİR TOPLAMA YAPACAĞIMI BENİM BU ALANA GİTMEME GEREKTİĞİNİ PROGRAMA İFADE EDİYORUM
                startActivityForResult(intentToGallery,2);//burada startactivity demiyorum oınun yerine for result diyorum bunun sebebi ise bir activitye gitmemem bir sonuç için activity nin başlatıldığını ifade etmem gerekiyor.
                //sonuç veren aktivite olarak ifade edebiliriz.
            }else{
                Toast.makeText(getApplicationContext(),"not Allowed",Toast.LENGTH_SHORT).show();
            }


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //izin istenen durumlarda sonuç olarak nelerin gerçekleşeceğini yazıyoruz.
        if(requestCode == 1){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //içerisinde eleman varsa ve izin verildiyse yapacağım işlemler
                Intent intentToGallery = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intentToGallery,2);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    //şuan seçilen verilerin verileceğin bir metod ihtiyacım var


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        //şuan kontrol etmem gerekn result code 2
        //şimdi biz kullanıcının resme tıkladı ,seçilmemiş olabilir.yada saçma birşey seçmeye çalışabilir.
        //data ifademiz ise kullanıcının ne seçtiğini dönen data
        if(requestCode == 2  && resultCode == RESULT_OK && data != null){
            //veri seçilmiş ve seçilen veri boş değil ise ....
            Uri imageData = data.getData();
            //verinin nereye kayıtlı olduğunun yolunu kaydeden yapımız getData bunu bir yol(path) olarak kaydedelim
            //buradaqki verinin bitmapa dönüştürülmesi eski versionlar için kolay fakat versionlar geliştikçe bu işlem zorlaştı.
            try {
                if(Build.VERSION.SDK_INT  >= 28){
                    ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(),imageData);
                    selectedImage = ImageDecoder.decodeBitmap(source);
                    //ImageDecoder daha yeni bir yapı aralarında fark sınıfların farklı olması
                    imageView.setImageBitmap(selectedImage);
                }else{
                    selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(),imageData);
                    imageView.setImageBitmap(selectedImage);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void save(View view){
        //This area is for save opareations.
        String artnm = artname.getText().toString();
        String paintnm = paintername.getText().toString();
        String yearnt = yeartext.getText().toString();

        Bitmap smallImage = makeSmallerImage(selectedImage,300);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        //görseli veriye çevirerek kaydediyoruz.
        smallImage.compress(Bitmap.CompressFormat.PNG,50,byteArrayOutputStream);//hangi formatta çevireyim ve kalitesi ne olsun parametrelerimizin türkçe tabir ve ifadeleri
        byte[] byteArray = byteArrayOutputStream.toByteArray();//görselin veriye çevirme işlemi bu ifade ile son buluyor.

        try {
            database = this.openOrCreateDatabase("Arts",MODE_PRIVATE,null);
            database.execSQL("CREATE TABLE IF NOT EXISTS arts(id INTEGER PRIMARY KEY,artname VARCHAR,paintername VARCHAR,year VARCHAR,image BLOB)");
            String sqlString = "INSERT INTO arts(artname,paintername,year,image) VALUES(?,?,?,?)";
            SQLiteStatement sqLiteStatement = database.compileStatement(sqlString);
            //bu yapı bir string ifadeyi sql içersinde sql komuty gibi çalıştırmaya olanak sağlıyor
            sqLiteStatement.bindString(1,artnm);//bind bağlamak olarak ifade edilebilir.
            sqLiteStatement.bindString(2,paintnm);
            sqLiteStatement.bindString(3,yearnt);
            //buı yapıda ifadelerin indexi 1 den başlıyor.
            sqLiteStatement.bindBlob(4,byteArray);
            sqLiteStatement.execute();//çalıştır.

        } catch (Exception e) {
            e.printStackTrace();
        }
        //finish();//akticiteyi kapatıyoruz işlemimiz bittiğinde
        Intent intent = new Intent(DetailActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }
    //Görselin boyutlandırılmasının gerçekleştirilmesi gerekiyor.Bu işlemi bir metod yardımı ile gerçekleştirmek istiyorum

    public Bitmap makeSmallerImage(Bitmap image,int maximumSize){
        //bu metodun bana bir adet bitmap dönmesinini istediğim için bitmap sınıfından yararlanarak metodu oluşturuyorum
        //orantılı olarak bir küçültme işlemi gerçekleştirmek istiyorum.Bunu sorgularla yapabilirim

        int width = image.getWidth();
        int height = image.getHeight();
        //hassas bir sonuç elde etmek için double/float ile işlemlerime devam ediyorum

        float bitmapRatio = (float)(width/height);
        //bitmapRatio eğer 1 den büyükse widht daha büyük demektir.Genişlik daha büyük resim yatay büyük demek
        if(bitmapRatio > 1){
            width = maximumSize;
            height = (int)(width/bitmapRatio);
        }else{
            height = maximumSize;
            width = (int)(height*bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image,width,height,true);


    }
}