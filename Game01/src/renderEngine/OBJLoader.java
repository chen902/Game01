/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package renderEngine;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import models.RawModel;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

/**
 *
 * @author Chen
 */
public class OBJLoader {
    public static RawModel loadObjModel(String fileName, Loader loader){
        FileReader reader = null; 
        try{
        reader = new  FileReader("res/" + fileName + ".obj");
        }
        catch(FileNotFoundException e){
            System.err.print("File was not found!");
        }
        
        List<Vector3f> vertices = new ArrayList<>(); 
        List<Vector2f> textures = new ArrayList<>(); 
        List<Vector3f> normals = new ArrayList<>(); 
        List<Integer> indices = new ArrayList<>();
        float[] vertices_arr;
        float[] texture_arr = null;
        float[] normals_arr = null;
        int[] indices_arr;
        
        BufferedReader br = new BufferedReader(reader);
         
        try{
            String line = br.readLine();
            while(!line.startsWith("f ")){
                    
                    String[] split = line.split(" ");
                if (line.startsWith("v ")){
                    vertices.add(new Vector3f(Float.parseFloat(split[1]), Float.parseFloat(split[2]), Float.parseFloat(split[3])));
                }else if(line.startsWith("vt ")){
                    textures.add(new Vector2f(Float.parseFloat(split[1]), Float.parseFloat(split[2])));
                }else if(line.startsWith("vn ")){
                    normals.add(new Vector3f(Float.parseFloat(split[1]), Float.parseFloat(split[2]), Float.parseFloat(split[3])));  
                }
                line = br.readLine();
            }
            
            texture_arr = new float[vertices.size()*2];
            normals_arr = new float[vertices.size()*3];
            
            //f v/vt/vn
            while(line != null){
                if(!line.startsWith("f ")){
                    line = br.readLine();
                    continue; 
                }
                
                String[] currentLine = line.split(" ");
                String[] vertex1 = currentLine[1].split("/");
                String[] vertex2 = currentLine[2].split("/");
                String[] vertex3 = currentLine[3].split("/");
                
                processVertex(vertex1, indices, textures, normals, texture_arr, normals_arr);
                processVertex(vertex2, indices, textures, normals, texture_arr, normals_arr);
                processVertex(vertex3, indices, textures, normals, texture_arr, normals_arr);
                
                line = br.readLine();
            }
            br.close();
        }
        catch(IOException e){
            System.err.println("Something got fucked");
        }
        
        vertices_arr = new float[vertices.size()*3];
        for(int vertexPointer=0; vertexPointer<vertices.size();vertexPointer++){
            Vector3f currentVertex = vertices.get(vertexPointer);
            vertices_arr[vertexPointer*3] = currentVertex.x;
            vertices_arr[vertexPointer*3+1] = currentVertex.y;
            vertices_arr[vertexPointer*3+2] = currentVertex.z;
        }
        
        indices_arr = new int[indices.size()];
        for(int index=0; index<indices.size();index++){
            indices_arr[index] = indices.get(index);
        }
        
        return loader.loadToVAO(vertices_arr, texture_arr, indices_arr, normals_arr);
    }
    
    private static void processVertex(String[] currentVertex, List<Integer> indices, List<Vector2f> textures, 
            List<Vector3f> normals, float[] texture_arr, float[] normals_arr){
        int currentVertexIndex = Integer.parseInt(currentVertex[0]) - 1;
        indices.add(currentVertexIndex);
        Vector2f currentTexture = textures.get(Integer.parseInt(currentVertex[1]) - 1);
        texture_arr[currentVertexIndex*2] = currentTexture.x;
        texture_arr[currentVertexIndex*2+1] = 1-currentTexture.y;
        Vector3f currentNormal = normals.get(Integer.parseInt(currentVertex[2]) - 1);
        normals_arr[currentVertexIndex*3] = currentNormal.x;
        normals_arr[currentVertexIndex*3+1] = currentNormal.y;
        normals_arr[currentVertexIndex*3+2] = currentNormal.z;
    }
}
