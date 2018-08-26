package engineTest;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;


import entities.Camera;
import entities.Entity;
import entities.Light;
import java.util.Random;
import models.RawModel;
import models.TexturedModel;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.OBJLoader;
import renderEngine.Renderer;
import shaders.StaticShader;
import textures.ModelTexture;

public class Main {

    public static void main(String[] args) {
        
        Random random = new Random();
        
        DisplayManager.createDisplay();

        Loader loader = new Loader();
        StaticShader shader = new StaticShader();
        Renderer renderer = new Renderer(shader);

        //RawModel model = loader.loadToVAO(vertices, textureCoords, indices);
        RawModel model = OBJLoader.loadObjModel("plane", loader);
        ModelTexture texture = new ModelTexture(loader.loadTexture("plane_texture"));
        
        TexturedModel texturedModel = new TexturedModel(model, texture);
        
        texture.setShineDamper(10);
        texture.setReflectivity(0);

        Entity entity = new Entity(texturedModel, new Vector3f(0, 0, -30), 0, 0, 0, 1);
        Light light = new Light(new Vector3f(0,0,-20), new Vector3f(1,1,1));
        Camera camera = new Camera();

        while (!Display.isCloseRequested()) {
            //entity.increaseRotation(1, 0, 0);
            //entity.increasePosition(0, 0, -0.01f);
            if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
                entity.increaseRotation(-0.5f, 0, 0);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
                entity.increaseRotation(0.5f, 0, 0);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
                entity.increaseRotation(0, 0.5f, 0);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
                entity.increaseRotation(0, -0.5f, 0);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_V)) {
                texture.setShineDamper(texture.getShineDamper()+ 0.2f);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_C)) {
                texture.setShineDamper(texture.getShineDamper()- 0.2f);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_R)) {
                texture.setReflectivity(texture.getReflectivity()+ 0.2f);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_E)) {
                texture.setReflectivity(texture.getReflectivity()- 0.2f);
            }


            camera.move();
            renderer.prepare();
            shader.start();
            shader.loadViewMatrix(camera);
            renderer.render(entity, shader);
            shader.loadLight(light);
            shader.stop();
            DisplayManager.updateDisplay();
        }

        shader.cleanUp();
        loader.cleaUp();

        DisplayManager.destroyDisplay();

    }

}
