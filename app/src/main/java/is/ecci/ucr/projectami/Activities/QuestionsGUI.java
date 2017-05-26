package is.ecci.ucr.projectami.Activities;

import is.ecci.ucr.projectami.DecisionTree.Matrix;
import is.ecci.ucr.projectami.DecisionTree.TreeController;
import is.ecci.ucr.projectami.DecisionTree.AnswerException;
import is.ecci.ucr.projectami.R;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import android.widget.EditText;

import java.util.LinkedHashSet;


public class QuestionsGUI extends AppCompatActivity {
    TreeController treeControl;
    LinkedHashSet<String> currentInfo;
    String currentQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions_gui);
        Intent parameters = getIntent();
        treeControl = parameters.getParcelableExtra("QuestionsGUI");
//        Matrix matrix = new Matrix();
//        try {
//            matrix.loadArff(getResources().openRawResource(R.raw.dataset));
//        } catch (Exception e) {
//            //File not found
//        }
//        treeControl = new TreeController(matrix);
        currentQuestion = "";
        //this.initialize();
    }

    protected void initialize() {
        this.setCurrentQuestion();
    }

    protected void setCurrentQuestion() {
        if (!treeControl.isLeaf()) {
            displayOnScreen(hashLinkedToArray(treeControl.getQuestionAndOptions()));
        } else {


            //Se termina la clasificación y le envía al administrador de clasificacion el insecto encontrado actualmente
            //y un array de string con los posibles alores obtenidos de retroalimentación
            String found = hashLinkedToArray(treeControl.getQuestionAndOptions())[0];
            //this.endClass(found,currentInfo);
        }

    }

    /*
    *   Convert a LikedHashSet to an array of strings
    *   @param: LinkedHashSet<String> array
     */
    protected String[] hashLinkedToArray(LinkedHashSet<String> array) {
        String[] strArr = new String[array.size()];
        array.toArray(strArr);
        return strArr;
    }

    /*
    *   This method receive an array with the current questions and answers
    *   to choice to display on the screen.
    *   @param: String[] questionsAndOptions
    */
    protected void displayOnScreen(String[] questionsAndOptions) {
        ((LinearLayout)findViewById(R.id.dynamicAnswers)).removeAllViews();
        int arraySize = questionsAndOptions.length;
        if (arraySize > 0) {
            TextView question = (TextView) findViewById(R.id.questionID);
            currentQuestion = questionsAndOptions[0];
            question.setText(currentQuestion);
            LinearLayout answerContainer = (LinearLayout) findViewById(R.id.dynamicAnswers);

            for (int i = 1; i < arraySize; i++) {
                Button button = new Button(this);
                button.setText(questionsAndOptions[i]);
                button.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Button pressed = (Button) v;
                        try {
                            catchAction(pressed);
                        } catch (Exception e) {
                            //Capturar la exceptión
                        }
                    }
                });
                answerContainer.addView(button, 0);
            }
        }
    }

    /*
    *   This method reacts to the button action
     */
    public void catchAction(Button button) throws AnswerException {
        String textB = button.getText().toString();
        if (textB.equals("NA")) {
            ((LinearLayout) findViewById(R.id.userAnswerLayout)).setVisibility(View.VISIBLE);
        }else if(textB.equals("Continuar")){
            ((LinearLayout)findViewById(R.id.dynamicAnswers)).removeAllViews();
            EditText answerBox = (EditText) findViewById(R.id.userAnswer);
            String userAnswer = answerBox.getText().toString();
            if (userAnswer.equals("")) {
                treeControl.reply("NA", userAnswer);
            } else {
                treeControl.reply("NA");
            }
            ((LinearLayout) findViewById(R.id.userAnswerLayout)).setVisibility(View.INVISIBLE);
        } else {
            if (textB.equals("Retroceder")) {
                treeControl.goBack();
            } else {
                treeControl.reply(textB);
            }
        }
        displayOnScreen(hashLinkedToArray(treeControl.getQuestionAndOptions()));
    }

}
