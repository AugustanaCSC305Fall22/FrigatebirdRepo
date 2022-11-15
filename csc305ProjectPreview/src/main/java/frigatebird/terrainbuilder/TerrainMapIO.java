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
    public static void createObjFile(TerrainMap map, File objFile) throws IOException {
    	FileWriter writer = new FileWriter(objFile);
    	for (int r = 0; r < map.getNumRows(); r++) {
    		for (int c = 0; c < map.getNumColumns(); c++) {
    			int height = map.getTileAt(r, c).getHeight();

    			writer.write("v" + " " + r + " " + c + " " + height + "\n");
    			writer.write("v" + " " + r + " " + c + " " + 0 + "\n");
    			writer.write("v" + " " + r + " " + (c + 1) + " " + 0 + "\n");
    			writer.write("v" + " " + r + " " + (c + 1) + " " + height + "\n");
    			writer.write("v" + " " + (r + 1) + " " + c + " " + height + "\n");
    			writer.write("v" + " " + (r + 1) + " " + c + " " + 0 + "\n");
    			writer.write("v" + " " + (r + 1) + " " + (c + 1) + " " + 0 + "\n");
    			writer.write("v" + " " + (r + 1) + " " + (c + 1) + " " + height + "\n");
    		}
    	}
    	int previousBlockIndex = 0;
    	for (int i = 0; i < map.getNumRows(); i++) {
    		for (int j = 0; j < map.getNumColumns(); j++) {
    			int v1Index = previousBlockIndex * 8 + 1;
    			int v2Index = v1Index + 1;
    			int v3Index = v2Index + 1;
    			int v4Index = v3Index + 1;
    			int v5Index = v4Index + 1;
    			int v6Index = v5Index + 1;
    			int v7Index = v6Index + 1;
    			int v8Index = v7Index + 1;
    			
    			writer.write("f" + " " + v4Index + " " + v3Index + " " + v2Index + " " + v1Index + "\n");
    			writer.write("f" + " " + v2Index + " " + v6Index + " " + v5Index + " " + v1Index + "\n");
    			writer.write("f" + " " + v3Index + " " + v7Index + " " + v6Index + " " + v2Index + "\n");
    			writer.write("f" + " " + v8Index + " " + v7Index + " " + v3Index + " " + v4Index + "\n");
    			writer.write("f" + " " + v5Index + " " + v8Index + " " + v4Index + " " + v1Index + "\n");
    			writer.write("f" + " " + v6Index + " " + v7Index + " " + v8Index + " " + v5Index + "\n");
    			
    			previousBlockIndex++;
    		}

    	}
    	writer.close();

    }
}
