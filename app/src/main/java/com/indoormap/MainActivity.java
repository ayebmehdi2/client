package com.indoormap;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.indoormap.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    ImageView map;
    Intent intent;
    HashMap<Integer,int[]> locationMap=new HashMap<>();
    int[] current;
    List<String> locations=new ArrayList<>();
    List<String> destinations=new ArrayList<>();
    //service binder and connection
    NaviagationService.pathfindBinder mbinder;
    private ProgressDialog progressDialog;
    private ServiceConnection connection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mbinder= (NaviagationService.pathfindBinder) service;
            mbinder.imagetoArray(map);
            //mbinder.imagetoPath(map, locationMap);
            Log.d("NavigationService","onServiceConnected");
            //if(current==null) {mbinder.findpath(map,locationMap.get(9),locationMap.get(mIndexQuestion));}
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("NavigationService","onServiceDisconnected");
        }
    };

    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("MainActivity","onCreate");
        initlocation();

        intent= new Intent(this,NaviagationService.class);
        map=findViewById(R.id.map);
        findViewById(R.id.back_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        initspinner();
        startService(intent);
        bindService(intent,connection,BIND_AUTO_CREATE);
    }

    private void initspinner() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Processing");
        progressDialog.setMessage("Please wait..");
        progressDialog.setCancelable(false);
        Spinner spinner = (Spinner) findViewById(R.id.naviagtion_currentlocationSpinner);
        Spinner spinner_des=findViewById(R.id.naviagtion_destinationSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, locations);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);

        ArrayAdapter<String> adapter_des = new ArrayAdapter<>(this, R.layout.spinner_item, destinations);
        adapter_des.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner_des.setAdapter(adapter_des);
        spinner_des.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.e("DES",position+"");
                if(current!=null) {
                    map.setVisibility(View.GONE);
                    map.setImageBitmap(null);
//                    map.setImageBitmap(TaskParameters.getStore_map());
                    int[] destination=locationMap.get(position-1);
                    if(destination!=null) mbinder.findpath(map, progressDialog, current, destination);
                    else Toast.makeText(MainActivity.this,"Please choose your destination",Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(MainActivity.this,"Please choose your current location",Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                current=locationMap.get(position-1);
                Log.e("SRC",position+"");
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                current=locationMap.get(9);
            }
        });
    }

    private void initlocation() {
        LinkedHashSet<String> set=new LinkedHashSet<>();
        set.add("Conference room(m401)");set.add("BOT HQ(m402)");set.add("Admin Office(m403)");set.add("Registrar(m404)");set.add("Accounting office(m405)");
        set.add("EVP office(m406)");set.add("Disbursing office(m407)");set.add("President’s office(m408)");set.add("Legal office(m500)");set.add("Finance office(m501)");
        set.add("HRD office(m502)");set.add("Discussion area");set.add("IT Library");set.add("Accountancy Library");set.add("Eng. Arch. Library");set.add("THD & Graduate Library");
        set.add("Periodicals Library");set.add("Main Library (m509)");set.add("Admin Extn Office");set.add("Balcony");set.add("Ladies Washroom");set.add("Man washroom");set.add("Security Office(m400)");

        locations.add("What is your current location?");
        locations.addAll(set);
        destinations.add("What is your destination?");
        destinations.addAll(set);

        locationMap.put(0,new int[]{907, 618});
        locationMap.put(1,new int[]{895, 826});
        locationMap.put(2,new int[]{626, 643});
        locationMap.put(3,new int[]{691, 855});
        locationMap.put(4,new int[]{555, 596});
        locationMap.put(5,new int[]{432, 814});
        locationMap.put(6,new int[]{377, 612});
        locationMap.put(7,new int[]{225, 831});

        locationMap.put(8,new int[]{242, 450});
        locationMap.put(9,new int[]{542, 319});
        locationMap.put(10,new int[]{301, 166});
        locationMap.put(11,new int[]{456, 149});
        locationMap.put(12,new int[]{530, 149});
        locationMap.put(13,new int[]{624, 149});
        locationMap.put(14,new int[]{743, 149});
        locationMap.put(15,new int[]{881, 149});
        locationMap.put(16,new int[]{971, 149});
        locationMap.put(17,new int[]{833, 329});
        locationMap.put(18,new int[]{381, 435});
        locationMap.put(19,new int[]{154, 165});
        locationMap.put(20,new int[]{268, 610});
        locationMap.put(21,new int[]{198, 610});
        locationMap.put(22,new int[]{1070, 564});

        /*locationMap.put(0,new int[]{1341,762,246,273});
        locationMap.put(1,new int[]{1296,1176,222,294});
        locationMap.put(2,new int[]{957,765,171,276});
        locationMap.put(3,new int[]{795,1179,489,291});
        locationMap.put(4,new int[]{633,768,324,273});
        locationMap.put(5,new int[]{441,1176,345,288});
        locationMap.put(6,new int[]{453,765,174,279});
        locationMap.put(7,new int[]{255,1176,183,288});

        locationMap.put(8,new int[]{255,654,201,87});
        locationMap.put(9,new int[]{426,438,147,300});
        locationMap.put(10,new int[]{360,42,219,276});
        locationMap.put(11,new int[]{588,42,165,276});
        locationMap.put(12,new int[]{756,42,159,276});
        locationMap.put(13,new int[]{921,42,156,276});
        locationMap.put(14,new int[]{1080,42,153,276});
        locationMap.put(15,new int[]{1236,42,156,276});
        locationMap.put(16,new int[]{1395,45,159,276});
        locationMap.put(17,new int[]{732,318,813,426});
        locationMap.put(18,new int[]{225,39,93,279});
        locationMap.put(19,new int[]{303,768,159,270});*/
    }
    @Override
    protected void onDestroy() {
        //unbind&stop，together with activity
        unbindService(connection);
        stopService(intent);
        super.onDestroy();
        Log.d("MainActivity","Destroy");
    }
}