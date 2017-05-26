package is.ecci.ucr.projectami.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;

import is.ecci.ucr.projectami.DBConnectors.CollectionName;
import is.ecci.ucr.projectami.DBConnectors.JsonParserLF;
import is.ecci.ucr.projectami.DBConnectors.MongoAdmin;
import is.ecci.ucr.projectami.MainActivity;
import is.ecci.ucr.projectami.R;
import is.ecci.ucr.projectami.SamplingPoints.SamplingPoint;
import is.ecci.ucr.projectami.SamplingPoints.Site;

/**
 * Created by Daniel on 5/20/2017.
 */

public class SubScreenMap extends Activity {
    SamplingPoint samplingPoint;
    Site site;
    String siteId;

    Button buttonInfo;
    Button buttonRegister;

    TextView siteName;
    TextView siteScore;
    TextView textHghQualBugs;
    TextView textMedQualBugs;
    TextView textLowQualBugs;

    RelativeLayout container_all;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sub_screen_map);
        site = MainActivity._actualSite;
        Intent intent = getIntent();
        siteId = intent.getStringExtra("siteid");
        setSamplingPoint();

        siteName = (TextView) findViewById(R.id.siteName);
        siteName.setText(samplingPoint.getSite().getSiteName());
        siteScore = (TextView) findViewById(R.id.siteScore);
        siteScore.setText(String.valueOf(samplingPoint.getScore()));
        textHghQualBugs = (TextView) findViewById(R.id.textHghQualBugs);
        textHghQualBugs.setText(samplingPoint.getHghQualBug());
        textMedQualBugs = (TextView) findViewById(R.id.textMedQualBugs);
        textMedQualBugs.setText(samplingPoint.getMedQualBug());
        textLowQualBugs = (TextView) findViewById(R.id.textLowQualBugs);
        textLowQualBugs.setText(samplingPoint.getLowQualBug());

        buttonInfo = (Button) findViewById(R.id.buttonInfo);
        buttonRegister = (Button) findViewById(R.id.buttonRegister);

        buttonInfo.setOnClickListener(btnInfoHandler);
        buttonRegister.setOnClickListener(btnRegstrHandler);

    }

    private void setSamplingPoint(){
        final MongoAdmin mongoAdmin = new MongoAdmin(this.getApplicationContext());

        mongoAdmin.getSamplesBySiteID(new MongoAdmin.ServerCallback() {
            @Override
            public JSONObject onSuccess(JSONObject result) {
                ArrayList<String> bugs = JsonParserLF.parseSampleBugList(result);
                mongoAdmin.getBugsByIdRange(new MongoAdmin.ServerCallback() {
                    @Override
                    public JSONObject onSuccess(JSONObject result) {
                        samplingPoint.setBugList(JsonParserLF.parseBugs(result));
                        samplingPoint.updateScoreAndQualBug();
                        return null;
                    }

                    @Override
                    public JSONObject onFailure(JSONObject result) {
                        return null;
                    }
                },bugs);
                return null;
            }

            @Override
            public JSONObject onFailure(JSONObject result) {
                return null;
            }
        },site.getObjID()
        );
    }

    private void findSite(){
        final MongoAdmin mongoAdmin = new MongoAdmin(this.getApplicationContext());
        mongoAdmin.getColl(new MongoAdmin.ServerCallback() {
            @Override
            public JSONObject onSuccess(JSONObject result) {
                ArrayList<Site> sites= JsonParserLF.parseSites(result);
                int i = 0;
                boolean found = false;
                while(!found) {
                    if(sites.get(i).getObjID() == siteId){
                        found = true;
                    }
                    else{
                        i++;
                    }
                }
                site = sites.get(i);
                return null;
            }

            @Override
            public JSONObject onFailure(JSONObject result) {
                return null;
            }
        }, CollectionName.SITE);
    }

    View.OnClickListener btnInfoHandler = new View.OnClickListener() {
        public void onClick(View v){
            Intent intent = new Intent(SubScreenMap.this, SamplePointInfoActivity.class);
            intent.putExtra("siteid", siteId);
            startActivity(intent);
        }
    };

    View.OnClickListener btnRegstrHandler = new View.OnClickListener() {
        public void onClick(View v){
            Intent intent = new Intent(SubScreenMap.this, BugsSampleToRegisterActivity.class);
            intent.putExtra("samplingPoint", (Parcelable) samplingPoint);
            intent.putExtra("siteid", siteId);
            startActivity(intent);
        }
    };

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        container_all = (RelativeLayout) findViewById(R.id.container_all);
        getWindow().setLayout(container_all.getWidth(),container_all.getHeight());
    }
}
