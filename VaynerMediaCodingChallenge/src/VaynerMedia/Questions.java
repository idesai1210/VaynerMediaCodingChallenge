package VaynerMedia;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.lang.Object;

public class Questions {
	
	public static void main(String[] args) throws FileNotFoundException, IOException, JSONException {
		
		List < Map < String, String >> source1List = new ArrayList < > ();
		List < Map < String, String >> source2List = new ArrayList < > ();
		try (InputStream in = new FileInputStream("source1.csv");) {
		    CSV csv = new CSV(true, ',', in );
		    List < String > fieldNames = null;
		    if (csv.hasNext()) fieldNames = new ArrayList < > (csv.next());
		    //List < Map < String, String >> list = new ArrayList < > ();
		    while (csv.hasNext()) {
		        List < String > x = csv.next();
		        Map < String, String > obj = new LinkedHashMap < > ();
		        for (int i = 0; i < fieldNames.size(); i++) {
		            obj.put(fieldNames.get(i), x.get(i));
		        }
		        source1List.add(obj);
		    }
		      
		}
		
		try (InputStream in = new FileInputStream("source2.csv");) {
		    CSV csv = new CSV(true, ',', in );
		    List < String > fieldNames = null;
		    if (csv.hasNext()) fieldNames = new ArrayList < > (csv.next());
		    //List < Map < String, String >> list1 = new ArrayList < > ();
		    while (csv.hasNext()) {
		        List < String > x = csv.next();
		        Map < String, String > obj = new LinkedHashMap < > ();
		        for (int i = 0; i < fieldNames.size(); i++) {
		            obj.put(fieldNames.get(i), x.get(i));
		        }
		        source2List.add(obj);
		    }
		    
		 }
		
		 int uniqueCampaigns = questionOne(source1List);
		 System.out.println("Unique Campaigns in February are: "+uniqueCampaigns);
		 
		 int plantConversions = questionTwo(source1List);
		 System.out.println("Total number of plant Conversions are: "+plantConversions);
		 
		 HashMap<String, Double> leastExpConversion = questionThree(source1List);
		 for( Map.Entry<String,Double> entry : leastExpConversion.entrySet()){
			System.out.println("The combination that has the least expenisve conversion is: "+entry.getKey()+". And it "
					+ "has a value of "+entry.getValue());
		 }
		 
		 float totalCost = questionFour(source1List,source2List);
		 System.out.println("The total cost per video view is: $"+totalCost);
		
		
	}
	
	
	static int questionOne(List<Map<String, String>> list){
		
		HashSet<String> uniqueCampaigns = new HashSet<>();
		
		for (Map<String, String> listValues : list) {
			//System.out.println(map.get("date"));
			String month[] = listValues.get("date").split("-");
			//System.out.println(month[1]);
			if(month[1].equals("02")){
				//System.out.println(month[1]);
				//System.out.println("///"+map.get("campaign"));
				uniqueCampaigns.add(listValues.get("campaign"));
			}
			
		}
		return uniqueCampaigns.size();
	}
	
	static int questionTwo(List<Map<String,String>> list) throws JSONException{
		
		int plantConversions = 0;
		
		for (Map<String, String> listValues : list) {
			//System.out.println(map.get("actions"));
			String actions ="{actions:" + listValues.get("actions") + "}";
			//Parsing the column actions into a JSON Array 
			JSONObject jObject = new JSONObject(actions);
			JSONArray jArray = jObject.getJSONArray("actions");
			
			for(int i = 0; i < jArray.length();i++){
				
				JSONObject jObj = jArray.getJSONObject(i);
				//System.out.println(jObj.has("x"));
				if(jObj.has("x") && jObj.getString("action").equals("conversions")){
					plantConversions = plantConversions + jObj.getInt("x");
				}
				else if(jObj.has("y") && jObj.getString("action").equals("conversions")){
					plantConversions = plantConversions + jObj.getInt("y");
				}
			}
		}
		
		return plantConversions;
	}
	
	static HashMap<String, Double> questionThree(List<Map<String, String>> list){
		
		HashMap<String, Double> currLeastExpensive = new HashMap<>();
		currLeastExpensive.put("default", Double.MAX_VALUE);
		
		String campaign = "default";
		//System.out.println(currLeastExpensive.get(campaign));
		for(Map<String, String> listValues: list){
			double cost = Double.parseDouble(listValues.get("spend")) / Double.parseDouble(listValues.get("impressions")) * 1000;
			
			if(cost < currLeastExpensive.get(campaign)){
				currLeastExpensive.clear();
				campaign = listValues.get("campaign");
				currLeastExpensive.put(campaign, cost);
			}
		}
		return currLeastExpensive;
		
	}
	
	static float questionFour(List<Map<String, String>> list, List<Map<String, String>> list1) throws JSONException{
		
		float totalCost = 0;
		List<String> allVideos = new ArrayList<>();
		
		for(Map<String, String> list1Values: list1){
			if(list1Values.get("object_type").equals("video")){
				allVideos.add(list1Values.get("campaign"));
			}
			
		}
		//System.out.println(allVideos);
		
		for(Map<String, String> listValues: list){
			
			String actions ="{actions:" + listValues.get("actions") + "}";
			//Parsing the column actions into a JSON Array 
			JSONObject jObject = new JSONObject(actions);
			JSONArray jArray = jObject.getJSONArray("actions");
			
			for(int i = 0; i < jArray.length();i++){
				JSONObject jObj = jArray.getJSONObject(i);
				//System.out.println(jObj.has("x"));
				if(allVideos.contains(listValues.get("campaign"))){
					//System.out.println("check");
					if(jObj.has("x") && jObj.getString("action").equals("views")){
						totalCost = totalCost + Float.parseFloat(listValues.get("spend"))/(float) jObj.getInt("x");
					}
					else if(jObj.has("y") && jObj.getString("action").equals("views")){
						totalCost = totalCost + Float.parseFloat(listValues.get("spend"))/ (float)jObj.getInt("y");
					}
					
				}
			}
			
		}
		
		return totalCost;
	}	
}
