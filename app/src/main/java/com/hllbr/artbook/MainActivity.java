package com.hllbr.artbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView listView ;
    ArrayList<String> nameArray;
    ArrayList<Integer> idArray;
    ArrayAdapter arrayAdapter;
/*
işleme ikinci activiteden başlamak gerekiyor.
Bunun sebebi ise kullanıcının görsel seçmesi galeriye gitmesi kullanıcıdan izin isteme gibi işlemler gerçekleştirilecek.


 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listView);
        //şimdi yapmamız gereken işlem sqlite üzerinden veri çekmek ve bu verileri listView üzerinden göstermemiz gerekiyor.
        nameArray =new ArrayList<String>();
        idArray = new ArrayList<Integer>();
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,nameArray);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this,DetailActivity.class);
                intent.putExtra("ArtId",idArray.get(position));
                intent.putExtra("info","old");
                startActivity(intent);
            }
        });
        getData();

    }
    public void getData(){
        try{
            SQLiteDatabase database = this.openOrCreateDatabase("Arts",MODE_PRIVATE,null);

            Cursor cursor = database.rawQuery("SELECT * FROM  arts",null);
            int nameIx = cursor.getColumnIndex("artname");
            int idIx = cursor.getColumnIndex("id");
            while(cursor.moveToNext()){
                nameArray.add(cursor.getString(nameIx));
                idArray.add(cursor.getInt(idIx));

            }
            arrayAdapter.notifyDataSetChanged();//veri üzerinde yaptığım değişiklikleri göster
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {//hangi menuyu göstereceğiz bu aktivitede
        //bir xml yapıldığında activite içersinide gösterebilmek için inflater denilen bir yapıyı kullanıyoruz.
        MenuInflater menuInflater = getMenuInflater();//burada oluşturulan obje ile menuyü aktiviteye bağlayabiliyoruz.
        menuInflater.inflate(R.menu.add_art,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {//herhangi bir item seçildiğinde ne yapılacağını seçiyoruz.
        //kullanıcının neye tıkladığını bilmem gerekiyor ona göre işlem yapmam lazım
        if(item.getItemId() == R.id.add_art_item){
            //add_art_item tıklandıysa napıcaz
            Intent intent = new Intent(MainActivity.this,DetailActivity.class);
            intent.putExtra("info","new");
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}