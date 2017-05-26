package is.ecci.ucr.projectami.Activities;

import android.content.Intent;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.android.gms.games.quest.Quest;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import is.ecci.ucr.projectami.Bugs.Bug;

import is.ecci.ucr.projectami.DecisionTree.Matrix;
import is.ecci.ucr.projectami.DecisionTree.TreeController;
import is.ecci.ucr.projectami.R;
import is.ecci.ucr.projectami.SampleBugsAdapter;
import is.ecci.ucr.projectami.SamplingPoints.SamplingPoint;

public class BugsSampleToRegisterActivity extends AppCompatActivity {


    QuestionsGUI questions;
    private ArrayList<Bug> _bugsListToRegister;
    Button floatingActionButton;
    SamplingPoint samplePoint;

    TreeController treeControl;
    LinkedList<Pair<String, String>> currentInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bugs_sample_to_register);

        Intent intent = this.getIntent();
        samplePoint = intent.getParcelableExtra("");                    // Nombre de intent variable

        ListView list = (ListView) findViewById(R.id.lstBugList);

        FloatingActionButton btnAddBug = (FloatingActionButton) findViewById(R.id.floatingActionButton);

        btnAddBug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BugsSampleToRegisterActivity.this, QuestionsGUI.class);
                intent.putExtra("feedbackArray", currentInfo);

               // intent.putExtra("QuestionsGUI", (Parcelable) treeControl);
                startActivity(intent);
            }
        });

        ImageView btnGoHome = (ImageView) findViewById(R.id.btnGoHome);
        btnGoHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Button btnRegister = (Button) findViewById(R.id.btnRegistrar);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        _bugsListToRegister = new ArrayList<Bug>();
        _bugsListToRegister.add(new Bug("Insectivorus", 5, ""));
        _bugsListToRegister.add(new Bug("Cucarachus", 5, ""));
        _bugsListToRegister.add(new Bug("Hormiguis", 5, ""));
        _bugsListToRegister.add(new Bug("Mantis", 5, ""));
        _bugsListToRegister.add(new Bug("Aedes Aegypti", 5, ""));
        _bugsListToRegister.add(new Bug("Mariposarium", 5,""));
        _bugsListToRegister.add(new Bug("Sompo Pa", 5,""));
        _bugsListToRegister.add(new Bug("Scara Bajum", 5, ""));

        SampleBugsAdapter adapter = new SampleBugsAdapter(this, _bugsListToRegister);
        list.setAdapter(adapter);
        //floatingActionButton = (Button) findViewById(R.id.floatingActionButton);
/*
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent Prueba = new Intent(BugsSampleToRegisterActivity.this, QuestionsGUI.class);
                startActivity(Prueba);
            }
        });
*/
    }

    public void addBugToBasket(Bug inBug){
        _bugsListToRegister.add(inBug);
    }

}
