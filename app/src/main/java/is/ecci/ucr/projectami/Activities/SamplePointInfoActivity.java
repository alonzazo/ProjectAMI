package is.ecci.ucr.projectami.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import is.ecci.ucr.projectami.DBConnectors.JsonParserLF;
import is.ecci.ucr.projectami.DBConnectors.MongoAdmin;
import is.ecci.ucr.projectami.MainActivity;
import is.ecci.ucr.projectami.R;
import is.ecci.ucr.projectami.SamplingPoints.SamplingPoint;
import is.ecci.ucr.projectami.Activities.QuestionsGUI;
import is.ecci.ucr.projectami.SamplingPoints.Site;

/**
 * Created by Daniel on 5/16/2017.
 */

public class SamplePointInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private SamplingPoint samplingPoint ;
    private Site site;

    TextView siteName;
    TextView siteDescription;
    TextView textTotSpecies;
    TextView textTotScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*
        String pathSamplePointImage = "path";
        File imgFile = new File(pathSamplePointImage);
        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            ImageView myImage = (ImageView) findViewById(R.id.siteImage);
            myImage.setImageBitmap(myBitmap);
        }
        */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_sample_point);

        Intent intent = getIntent();
        site = intent.getParcelableExtra("site");
        setSamplingPoint();

        siteName = (TextView) findViewById(R.id.siteName);
        siteName.setText(samplingPoint.getSite().getSiteName());
        siteDescription = (TextView) findViewById(R.id.siteDescription);
        siteDescription.setText(samplingPoint.getSite().getDescription());
        textTotScore = (TextView) findViewById(R.id.textTotScore);
        textTotScore.setText(String.valueOf(samplingPoint.getScore()));
        textTotSpecies = (TextView) findViewById(R.id.textTotSpecies);
        textTotSpecies.setText(String.valueOf(samplingPoint.getBugList().size()));

        String initialDate;
        String finalDate;

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

    @Override
    public void onClick(View v) {

    }
}
