package com.thedeveloperworldisyours.whatdoyoudoandroid.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.thedeveloperworldisyours.whatdoyoudoandroid.R;
import com.thedeveloperworldisyours.whatdoyoudoandroid.adapters.MySimpleArrayAdapter;
import com.thedeveloperworldisyours.whatdoyoudoandroid.dao.MissionDAO;
import com.thedeveloperworldisyours.whatdoyoudoandroid.dao.NodeDAO;
import com.thedeveloperworldisyours.whatdoyoudoandroid.models.Mission;
import com.thedeveloperworldisyours.whatdoyoudoandroid.models.Node;
import com.thedeveloperworldisyours.whatdoyoudoandroid.utils.Constants;
import com.thedeveloperworldisyours.whatdoyoudoandroid.utils.Utils;
import com.thedeveloperworldisyours.whatdoyoudoandroid.webservice.Client;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends ActionBarActivity implements View.OnClickListener,AdapterView.OnItemClickListener {
    //private Button mMission;
    private MissionDAO mMissionDAO;
    private NodeDAO mNodeDAO;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*mMission = (Button) findViewById(R.id.main_activity_mission_button);
        mMission.setOnClickListener(this);
*/
        mListView = (ListView) findViewById(R.id.main_activity_listView);
        mListView.setOnItemClickListener(this);

        mMissionDAO = new MissionDAO(this);
        mNodeDAO = new NodeDAO(this);

        getInfo();


    }

    public void getInfo(){
        if (Utils.isOnline(this)) {
            clearDB();
            getMissions();
        }else if(exitsDB()){
            clearDB();
            getInfoMissionFromDB();
            buildList(mMissionDAO.readAllAsc());
        }else{
            Toast.makeText(this,R.string.no_connection,Toast.LENGTH_SHORT).show();
        }
    }

    public void clearDB(){
        mMissionDAO.deleteAll();
        mNodeDAO.deleteAll();
    }

    public void getInfoMissionFromDB(){
        List<Mission> listMision= new ArrayList<Mission>();
        listMision=mMissionDAO.readAllAsc();
    }

    public boolean exitsDB(){
        int missions = mMissionDAO.getCount();
        int nodes = mNodeDAO.getCount();

        return missions > 0 && nodes > 0;
    }

    public void buildList(List<Mission> listMision){
        final MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(this, (ArrayList<Mission>) listMision);
        mListView.setAdapter(adapter);
    }

    public void getMissions(){
        Callback<List<Mission>> callback = new Callback<List<Mission>>() {
            @Override
            public void success(List<Mission> missions, Response response) {
                Log.v("Client", "success");
                insertMission(missions);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.v("Client", "failure");
            }

        };
        Client.initRestAdapter().getMissions(callback);
    }

    public void insertMission(List<Mission> listMission){
        for (int i=0; i< listMission.size();i++){
            mMissionDAO.create(listMission.get(i));
            getNodes(listMission.get(i).getId());
        }
        getInfoMissionFromDB();
        buildList(mMissionDAO.readAllAsc());
    }

    public void getNodes(String idMission){
        Callback<List<Node>> callback = new Callback<List<Node>>() {
            @Override
            public void success(List<Node> nodes, Response response) {
                Log.v("Client", "success");
                insertNodes(nodes);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.v("Client", "failure");
            }
        };
        Client.initRestAdapter().getNodes(idMission,callback);
    }

    public void insertNodes(List<Node> listNodes){
        for (int i=0;i<listNodes.size();i++){
            mNodeDAO.create(listNodes.get(i));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            /*case R.id.main_activity_mission_button:
                startMission();
                break;*/
            default:
        }

    }

    public void startMission(String id){
        Intent intent = new Intent(this, IntroActivity.class);
        intent.putExtra(Constants.ID_INTENT_MISSION, id);
        startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String tag = view.getTag().toString();
        startMission(tag);
    }
}
