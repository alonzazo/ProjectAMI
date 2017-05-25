package is.ecci.ucr.projectami.DBConnectors;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import is.ecci.ucr.projectami.Bugs.Bug;
import is.ecci.ucr.projectami.SamplingPoints.SamplingPoint;
import is.ecci.ucr.projectami.SamplingPoints.Site;

/**
 * Created by alaincruzcasanova on 5/22/17.
 */

public class JsonParserLF {

    //Parsers

    public static ArrayList<Site> parseSites(JSONObject response) {
        ArrayList<Site> sites = new ArrayList<>();
        try {
        if (response.has("_embedded")) {
            JSONArray jsonArray = response.getJSONArray("_embedded");
            for (int i = 0; i<jsonArray.length(); i++) {
                JSONObject docJson = jsonArray.getJSONObject(i);
                sites.add(readSite(docJson));
            }
        } else {
            sites.add(readSite(response));
        }} catch (JSONException e) {
            e.printStackTrace();
        }
        return  sites;
    }

    public static ArrayList<Bug> parseBugs(JSONObject response) {
        ArrayList<Bug> bugs = new ArrayList<>();
        try {
            if (response.has("_embedded")) {
                JSONArray jsonArray = response.getJSONArray("_embedded");
                for (int i = 0; i<jsonArray.length(); i++) {
                    JSONObject docJson = jsonArray.getJSONObject(i);
                    bugs.add(readBug(docJson));
                }
            } else {
                bugs.add(readBug(response));
            }} catch (JSONException e) {
            e.printStackTrace();
        }
        return  bugs;
    }

    //Readers

    private static Site readSite(JSONObject siteDoc) throws JSONException {
        String objID = siteDoc.getJSONObject("_id").getString("$oid");
        String name = siteDoc.getString("name");
        String desc;
        JSONObject coor = siteDoc.getJSONObject("coordinates");
        Double latitude = coor.getDouble("lat");
        Double longitude = coor.getDouble("long");
        if (siteDoc.has("desc")) {
            desc = siteDoc.getString("desc");
        } else {
            desc = "N/A";
        }
        return new Site(objID,name,latitude,longitude,desc);
    }

    private static Bug readBug(JSONObject bugDoc) throws JSONException {
        String objID = bugDoc.getString("_id");
        String desc;
        double score = bugDoc.getDouble("score");
        if (bugDoc.has("desc")) {
            desc = bugDoc.getString("desc");
        } else {
            desc = "N/A";
        }
        return new Bug(objID,score,desc);
    }

}
