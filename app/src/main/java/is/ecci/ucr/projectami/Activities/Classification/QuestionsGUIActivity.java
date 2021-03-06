package is.ecci.ucr.projectami.Activities.Classification;

import is.ecci.ucr.projectami.DBConnectors.CollectionName;
import is.ecci.ucr.projectami.DBConnectors.Consultor;
import is.ecci.ucr.projectami.DBConnectors.JsonParserLF;
import is.ecci.ucr.projectami.DBConnectors.ServerCallback;
import is.ecci.ucr.projectami.DecisionTree.Matrix;
import is.ecci.ucr.projectami.DecisionTree.TreeController;
import is.ecci.ucr.projectami.DecisionTree.AnswerException;
import is.ecci.ucr.projectami.LogInfo;
import is.ecci.ucr.projectami.Questions;
import is.ecci.ucr.projectami.R;
import is.ecci.ucr.projectami.ResolvingFeedbackActivity;

import android.app.Activity;
import android.content.Intent;
import android.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;


public class QuestionsGUIActivity extends AppCompatActivity {

    //Declaración de variables

    //Variables estáticas que requieren de muchos recursos, que se busca que se creen pocas veces
    static TreeController treeControl;
    static HashMap<String, String> questions;
    static boolean openedBefore = false;
    static Matrix matrix = new Matrix();

    //Variables estáticas que se llaman desde otras clases, para las cuales existen métodos
    private static String currentBug;
    private static LinkedList<Pair<String, String>> currentQuestionsRealized;

    //Variables de la clase
    String currentQuestion;
    boolean extraQuestion = false;
    int currentExtraQuestions = 3;


    /**
     * This method describe the instance of the class
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions_gui);

        //Clase dedicada para pasar parámetro
        currentBug = "Unknown";
        currentQuestionsRealized = null;
        findViewById(R.id.progressBarQuestion).setVisibility(View.VISIBLE);
        findViewById(R.id.dynamicAnswers).setVisibility(View.GONE);

        TextView btnResolve = (TextView) findViewById(R.id.txtResolve);
        btnResolve.setVisibility(View.INVISIBLE);

        if (LogInfo.getRoles() != null && LogInfo.getRoles().contains("bioadministrador")){
            btnResolve.setVisibility(View.VISIBLE);
            btnResolve.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /*Intent intent = new Intent(QuestionsGUIActivity.this, ResolvingFeedbackActivity.class);
                    startActivity(intent);*/
                    Intent intent = new Intent(QuestionsGUIActivity.this, ResolvingFeedbackActivity.class);
                    LinkedList<String> familiesLinked = treeControl.getPossibleFamilies();
                    String families[] = familiesLinked.toArray(new String[familiesLinked.size()]);
                    intent.putExtra("families", families);
                    intent.putExtra("bioadmin", true);
                    startActivityForResult(intent, 0XF);
                }
            });
        }

        if (!openedBefore) {   //Si el árbol ya había sido inicializado, no se vuelve a inicializar
            questions = new HashMap<String, String>();
            try {
                matrix.loadArff(getResources().openRawResource(R.raw.dataset));
                this.loadQuestions();
            } catch (Exception e) {
                //File not found
            }
            openedBefore = true;
        } else {
            this.initialize();
        }
    }

    /**
     * Inicialización de árbol
     */
    protected void initialize() {

        findViewById(R.id.progressBarQuestion).setVisibility(View.GONE);
        findViewById(R.id.dynamicAnswers).setVisibility(View.VISIBLE);


        treeControl = new TreeController(matrix);
        //Linking between static buttons and actions
        ImageView btnGoHome = (ImageView) findViewById(R.id.btnBack);
        btnGoHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Button backB = (Button) (findViewById(R.id.backButton));
        backB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //Boton de pregunta anterior
                Button pressed = (Button) v;
                try {
                    catchAction(pressed);
                } catch (Exception e) {
                    //Capturar la exceptión
                }
            }
        });

        Button contB = (Button) (findViewById(R.id.naButton));
        contB.setOnClickListener(new View.OnClickListener() {
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
        currentQuestion = "";
        this.setCurrentQuestion();
    }

    /**
     * Set the current question and sends it to other method to publish it on the gui
     */
    protected void setCurrentQuestion() {

        if (extraQuestion) {
            if (currentExtraQuestions > 0) {
                displayOnScreen(hashLinkedToArray(treeControl.getQuestionAndOptions()));
                currentExtraQuestions--;
            } else { //Si se acaban las 5 preguntas extras de retroalimentacion
                currentQuestionsRealized = treeControl.getQuestionsRealized();
                try {
                    System.out.println("Finishing the activity");
                    Intent intent = new Intent(QuestionsGUIActivity.this, ResolvingFeedbackActivity.class);
                    LinkedList<String> familiesLinked = treeControl.getPossibleFamilies();
                    String families[] = familiesLinked.toArray(new String[familiesLinked.size()]);
                    intent.putExtra("families", families);
                    intent.putExtra("bioadmin", false);
                    startActivityForResult(intent, 0XF);
                    //terminarActividad();
                } catch (Exception e) {
                    //
                    System.out.println("Error finishing the frame");
                }
            }
        } else {
            boolean j = treeControl.isLeaf();
            if (!j) {
                displayOnScreen(hashLinkedToArray(treeControl.getQuestionAndOptions()));
            } else {
                extraQuestion = true;
            }
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
    *   It generates dynamically buttons (options) depending on the current questions and answers,
    *   publishing it on the corresponding layout.
    *   @param: String[] questionsAndOptions
    *
    */
    protected void displayOnScreen(String[] questionsAndOptions) {
        ((LinearLayout) findViewById(R.id.dynamicAnswers)).removeAllViews();
        int arraySize = questionsAndOptions.length;
        if (arraySize > 0) {
            TextView question = (TextView) findViewById(R.id.questionID);
            String string = questions.get(questionsAndOptions[0]); //Traduce del id de la pregunta a la pregunta en si.
            currentQuestion = (string == null) ? questionsAndOptions[0] : string;

            question.setText(currentQuestion);
            question.setText(JsonParserLF.convert(currentQuestion));
            LinearLayout answerContainer = (LinearLayout) findViewById(R.id.dynamicAnswers);

            for (int i = 1; i < arraySize; i++) { //Agrega los botones por cada respuesta
                Button button = new Button(this);
                button.setText(translateReply(questionsAndOptions[i], true));
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
                answerContainer.addView(button, answerContainer.getChildCount());
            }

            if (treeControl.isLeaf()) {
                //Search by image id
                int resourceId = getResources().getIdentifier("drawable/" + getImageName(currentQuestion), null, this.getApplicationContext().getPackageName());
                if (resourceId > 0) {
                    //ImageView Setup
                    ImageView icon = (ImageView) new ImageView(this);

                    //setting image resource
                    icon.setImageResource(resourceId);

                    icon.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));

                    //adding view to layout
                    answerContainer.addView(icon, 0);
                }

            }

        } else {
            currentBug = currentQuestion;
            currentQuestionsRealized = treeControl.getQuestionsRealized();
            try {
                terminarActividad();
            } catch (Throwable e) {
                //
            }
        }
    }

    /**
     * This method reacts to the button action
     * It´s the listener for the buttons. It has a different action depending on the input
     *
     * @param button
     * @throws AnswerException
     */
    public void catchAction(Button button) throws AnswerException {
        setCurrentQuestion();
        String textB = translateReply(button.getText().toString(), false);
        if (textB.equals("NA")) {
            ((LinearLayout) findViewById(R.id.dynamicAnswers)).removeAllViews();
            findViewById(R.id.userAnswerLayout).setVisibility(View.VISIBLE);
            findViewById(R.id.scrollQuestionsView).setVisibility(View.INVISIBLE);
        } else if (textB.equals("Continuar")) {
            EditText answerBox = (EditText) findViewById(R.id.userAnswer);
            String userAnswer = answerBox.getText().toString();
            if (userAnswer.trim().equals("")) {
                treeControl.reply("NA");
            } else {
                treeControl.reply("NA", userAnswer);
                ((TextView)findViewById(R.id.userAnswer)).setText("");
            }
            findViewById(R.id.userAnswerLayout).setVisibility(View.INVISIBLE);
            findViewById(R.id.scrollQuestionsView).setVisibility(View.VISIBLE);

        } else {
            if (textB.equals("Volver a pregunta anterior")) {
                treeControl.goBack();
                findViewById(R.id.userAnswerLayout).setVisibility(View.INVISIBLE);
                findViewById(R.id.scrollQuestionsView).setVisibility(View.VISIBLE);
            } else {
                treeControl.reply(textB);
            }
        }
        displayOnScreen(hashLinkedToArray(treeControl.getQuestionAndOptions()));
    }

    private String translateReply(String s, Boolean b) {
        if (b) {
            if (s.equals("TRUE")) {
                return "Si";
            } else if (s.equals("FALSE")) {
                return "No";
            }
            if (s.equals("NA")) {
                return "No aplica";
            }
        } else {
            if (s.equals("Si")) {
                return "TRUE";
            } else if (s.equals("No")) {
                return "FALSE";
            }
            if (s.equals("No aplica")) {
                return "NA";
            }
        }
        return s;
    }

    /**
     * This method load the set of questions from the database
     *
     * @throws Exception
     */
    public void loadQuestions() throws Exception {
        Consultor.getColl(new ServerCallback() {
            @Override
            public JSONObject onSuccess(JSONObject result) {
                ArrayList<Questions> questionsArray = JsonParserLF.parseQuestionsList(result);
                int totalQuestions = questionsArray.size();
                for (int i = 0; i < totalQuestions; i++) {
                    try {
                        questions.put(convert(questionsArray.get(i).getIdentificador().trim()), convert(questionsArray.get(i).getQuestion()));
                        Log.v("Sucessfull::", "Questions correctly downloaded");

                    } catch (java.io.UnsupportedEncodingException e) {
                        Log.v("Error::", "Questions incorrectly downloaded");
                    }
                }
                initialize();
                return null;

            }

            @Override
            public JSONObject onFailure(JSONObject result) {
                Log.d("Failed to request##", "Error descargando de BD");
                initialize();
                return null;
            }
        }, CollectionName.QUESTIONS);
    }

    /**
     * Converts the charset of the string sended by the DB.
     *
     * @param string
     * @return
     * @throws java.io.UnsupportedEncodingException
     */
    public String convert(String string) throws java.io.UnsupportedEncodingException {
        byte[] bytes = string.getBytes("ISO_8859-1");
        return new String(bytes);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0XF) { // Please, use a final int instead of hardcoded int value
            if (resultCode == RESULT_OK) {
                String value = (String) data.getExtras().getString("returning_from_resolving");
                try {
                    treeControl.resolve(value);
                    currentBug = value;
                    currentQuestionsRealized = treeControl.getQuestionsRealized();
                    terminarActividad();
                } catch (Exception e){
                    System.out.println(e.getMessage());

                }
            } else if (resultCode == RESULT_CANCELED) {
                finish();
            }
        }
    }

    public void terminarActividad() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("returning_from_classification", true);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    static String getCurrentBug() {
        return currentBug;
    }

    static LinkedList<Pair<String, String>> getCurrentQuestionsRealized() {
        return currentQuestionsRealized;
    }

    private String getImageName(String bugFamily) {
        String finalString = (bugFamily.replace(":", "_")).toLowerCase();
        return "img_" + finalString;
    }

}

