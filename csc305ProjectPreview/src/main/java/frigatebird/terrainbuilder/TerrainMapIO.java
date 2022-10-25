package frigatebird.terrainbuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.*;

public class TerrainMapIO {
	
	public static void terrainMapToJSON(TerrainMap map, File outputFile) throws IOException {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		FileWriter writer = new FileWriter(outputFile);
		gson.toJson(map, writer);
		writer.close();		
	}
	
	public static TerrainMap jsonToTerrainMap(File inputFile) throws JsonSyntaxException, JsonIOException, IOException {
		Gson gson = new Gson();
		FileReader reader = new FileReader(inputFile);
		TerrainMap map = gson.fromJson(reader, TerrainMap.class);
		reader.close();
		return map;
	}
	
}
