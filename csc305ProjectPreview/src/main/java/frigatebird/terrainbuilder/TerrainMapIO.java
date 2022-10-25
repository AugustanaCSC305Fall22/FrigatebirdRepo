package frigatebird.terrainbuilder;

import com.google.gson.*;

public class TerrainMapIO {
	
	public static String terrainMapToJSON(TerrainMap map) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		return gson.toJson(map);		
	}
	
	public static TerrainMap jsonToTerrainMap(String jsonText) {
		Gson gson = new Gson();
		TerrainMap map = gson.fromJson(jsonText, TerrainMap.class);
		return map;
	}
	
}
