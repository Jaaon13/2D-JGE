package graphical.rendering;

import java.awt.Point;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL30;

import assets.Shader;
import controller.Controller;
import ecs.EngineComponets.PlainShape;
import ecs.EngineComponets.Pos;
import ecs.EngineComponets.Size;
import ecs.EngineComponets.TextureC;
import gui.factorys.Text;
import gui.factorys.TextFactory;
import ecs.Entity;
import ecs.EntityManager;
import logger.Logger.LoggerInfo;

public class BasicRenderer extends Renderer {

	// Path of engine rendering files
	private final static String path = "\\eng\\graphical\\rendering\\precompiledFiles\\";
	
	private int textureShader, colorShader;
	
	private int vertexBuffer, indexBuffer;
	
	// Size of a short( 2 bytes ) * number of elements in a vertex
	private final int vertexSize = 2 * 4;
	
	@Override
	public void initalize() {
		
		// Create the vertex & index buffers
		vertexBuffer = GL30.glGenBuffers();
		indexBuffer = GL30.glGenBuffers();
		
		// Bind the buffers
		GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vertexBuffer);
		GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
		
		// Index to start at, number of parameters per vertex, the type, whether or not it needs to be normalized,
		// the byte length of the vertex, and the offset in bytes
						
		// Position
		GL30.glVertexAttribPointer(0, 2, GL30.GL_SHORT, false, vertexSize, 0);
						
		// Texture Position
		GL30.glVertexAttribPointer(1, 2, GL30.GL_HALF_FLOAT, false, vertexSize, 4);
						
		// Enable the Vertex Attrib
		GL30.glEnableVertexAttribArray(0);
		GL30.glEnableVertexAttribArray(1);
		
		// Make the shader programs
		textureShader = ((Shader) Controller.assets.load(path + "textVert.vert", path + "textFrag.frag")).getRawID();
		colorShader = ((Shader) Controller.assets.load(path + "colorVert.vert", path + "colorFrag.frag")).getRawID();
				
		GL30.glUseProgram(textureShader);
		
		GL30.glUniform1i(GL30.glGetUniformLocation(textureShader, "text"), 0);
			
		GL30.glActiveTexture(GL30.GL_TEXTURE0);
				
		GL30.glEnable(GL30.GL_BLEND);
				
		GL30.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
		
	}

	private class Meshes {
		
		public short[] vertexes;
		public int[] indexes;
		
		public Point camOff;
		
		final int width = Controller.globals.screenSize.x;
		final int height = Controller.globals.screenSize.y;
		
		public Meshes(int totalToDraw) {
			vertexes = new short[totalToDraw * 16];
			indexes = new int[totalToDraw * 6];
			Arrays.fill(vertexes, (short) -1);
			Arrays.fill(indexes, -1);
			camOff = (camera) ? Controller.globals.camera.pos : new Point(0, 0);
		}
		
		private int vertPointer = 0, indPointer = 0;
		
		// Used as the index value
		private int indinc = 0;
		
		private void addEntity(Entity e, EntityManager em) {
			
			if(!em.contains(e, List.of(Pos.class, Size.class))) {
				Controller.logger.log(
						List.of("BASIC RENDERER: Tried to render an invalid entity.", "ID: " + e.id), LoggerInfo.ERROR);
				return;
			} else if(!em.contains(e, TextureC.class) && !em.contains(e, PlainShape.class) ) {
				Controller.logger.log(
						List.of("BASIC RENDERER: Tried to render an entity without a rendering option.", "ID: " + e.id), LoggerInfo.ERROR);
				return;
			}
			
			Pos posComp = (Pos) em.get(e, Pos.class);
			Size sizeComp = (Size) em.get(e, Size.class);
			
			boolean usesAtlas = (em.contains(e, TextureC.class))
					? ((((TextureC) em.get(e, TextureC.class)).atlas != null) ? true : false) : false;
			
			TextureC t = (TextureC) em.get(e, TextureC.class);
			
			Point newPos = (Controller.globals.camera != null) ? 
					new Point(posComp.x - Controller.globals.camera.pos.x, (posComp.y - Controller.globals.camera.pos.y)):
					new Point(posComp.x, posComp.y);
			
			// Check if the atlas exists if not set it to one so when we divide w/ it, it cannot alter sizing
			float atlasSizeX = (usesAtlas) ? (float) t.texture.atlas.size.x : 1f;
			float atlasSizeY = (usesAtlas) ? (float) t.texture.atlas.size.y : 1f;
			
			// All of the texture positions
			Point bl = (usesAtlas) ? new Point(t.atlas) : new Point(0, 0);
			Point br = (usesAtlas) ? new Point(t.atlas.x + sizeComp.x, t.atlas.y) : new Point(1, 0);
			Point tr = (usesAtlas) ? new Point(t.atlas.x + sizeComp.x, t.atlas.y + sizeComp.y) : new Point(1, 1);
			Point tl = (usesAtlas) ? new Point(t.atlas.x, t.atlas.y + sizeComp.y) : new Point(0, 1);
			
			// Top left
				// Position
			vertexes[vertPointer] = (short) (newPos.x);
			vertexes[vertPointer + 1] = (short) (newPos.y + sizeComp.y);
				// Texture Cords
			vertexes[vertPointer + 2] = toHalfFloat((float) tl.x / atlasSizeX);
			vertexes[vertPointer + 3] = toHalfFloat((float) tl.y / atlasSizeY);
			
			// Top Right
				// Position
			vertexes[vertPointer + 4] = (short) (newPos.x + sizeComp.x);
			vertexes[vertPointer + 5] = (short) (newPos.y + sizeComp.y);
				// Texture Cords
			vertexes[vertPointer + 6] = toHalfFloat((float) tr.x / atlasSizeX);
			vertexes[vertPointer + 7] = toHalfFloat((float) tr.y / atlasSizeY);
			
			// Bottom Right
				// Position
			vertexes[vertPointer + 8] = (short) (newPos.x + sizeComp.x);
			vertexes[vertPointer + 9] = (short) (newPos.y);
				// Texture Cords
			vertexes[vertPointer + 10] = toHalfFloat((float) br.x / atlasSizeX);
			vertexes[vertPointer + 11] = toHalfFloat((float) br.y / atlasSizeY);
			
			// Bottom left
				// Position
			vertexes[vertPointer + 12] = (short) (newPos.x);
			vertexes[vertPointer + 13] = (short) (newPos.y);
				// Texture Cords
			vertexes[vertPointer + 14] = toHalfFloat((float) bl.x / atlasSizeX);
			vertexes[vertPointer + 15] = toHalfFloat((float) bl.y / atlasSizeY);
			
			vertPointer += 16;
			
			// Index shit
			indexes[indPointer] = indinc;
			indexes[indPointer + 1] = indinc + 1;
			indexes[indPointer + 2] = indinc + 2;
			indexes[indPointer + 3] = indinc + 2;
			indexes[indPointer + 4] = indinc + 3;
			indexes[indPointer + 5] = indinc;
			
			indinc += 4;
			indPointer += 6;
			
			trianglesDrawn += 2;
		}
		

		public short[] getVertexes() {
			
			short[] tosend = new short[vertPointer];
			
			for(int x = 0; x < vertPointer; x++) {
				
				tosend[x] = vertexes[x];
				
			}
			
			return tosend;
		}
		
		public int[] getIndexes() {
			
			int[] tosend = new int[indPointer];
			
			for(int x = 0; x < indPointer; x++) {
				
				tosend[x] = indexes[x];
				
			}
			
			return tosend;
		}
		
	}
	
	public boolean camera;
	
	public void textureRender(Meshes m, int key) {
	
		GL30.glUseProgram(textureShader);
		
		GL30.glBindTexture(GL30.GL_TEXTURE_2D, key);
		
		GL30.glBufferData(GL30.GL_ARRAY_BUFFER, m.getVertexes(), GL30.GL_DYNAMIC_DRAW);
		GL30.glBufferData(GL30.GL_ELEMENT_ARRAY_BUFFER, m.getIndexes(), GL30.GL_DYNAMIC_DRAW);
		
		GL30.glDrawElements(GL30.GL_TRIANGLES, m.getIndexes().length, GL30.GL_UNSIGNED_INT, 0);
		
		drawCalls++;
		
	}
	
	private void primativeRender(Meshes m) {
		
		// #TODO
		
		
		
	}
	
	EntityManager em = new EntityManager();
	
	private void renderText() {
		
		int id = Controller.assets.get(Controller.globals.dir + "\\eng\\graphical\\rendering\\fonts\\minogram_6x10.png").getRawID();
		
		int total = 0;
		
		for(Text t : text) {
			total += t.data.length();
		}
		
		// Private helper objects
		em.clear();
		Meshes meshes = new Meshes(total);
		
		for(Text t : text) {
			
			if(t.data.isEmpty()) {continue;}
			
			TextFactory.generateText(t.data, t.pos, t.alignment, em, true);
			
		}
		
		for(Entity e : em.getVisible()) {
			
			meshes.addEntity(e, em);
			
		}
		
		textureRender(meshes, id);
		
	}
	
	private Matrix4f projection;
	
	@Override
	public void render() {
		
		drawCalls = 0;
		trianglesDrawn = 0;
		
		if(!GLFW.glfwWindowShouldClose(Controller.globals.window)) {
			
			GL30.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);
						
			// Start Rendering //
			
			camera = (Objects.nonNull(Controller.globals.camera));
			
			// Key is texture ID
			Map<Integer, Meshes> meshes = new HashMap<>();
			
			// Meshify each sprite
			for(Entity e : entities) {
				
				TextureC t = (Controller.scenes.ecs.contains(e, TextureC.class))
						? (TextureC) Controller.scenes.ecs.get(e, TextureC.class) : null;
				
				int rawTextID = (Objects.nonNull(t)) ? t.TextureID : -1;
				
				if(meshes.containsKey(rawTextID)) {
					
					meshes.get(rawTextID).addEntity(e, Controller.scenes.ecs);
					
				} else {
					
					meshes.put(rawTextID, new Meshes(entities.size()));
					meshes.get(rawTextID).addEntity(e, Controller.scenes.ecs);
					
				}
				
			}
			
			// Draw everything
			
			for(int key : meshes.keySet()) {
				
				Meshes m = meshes.get(key);
				
				if(key != -1) {
					
					textureRender(m, key);
					
				} else {
					
					primativeRender(m);
					
				}
				
			}
			
			renderText();
			
			// End Rendering //
			
			// swap the color buffers
			GLFW.glfwSwapBuffers(Controller.globals.window);
						
			// Poll events (User input)
			GLFW.glfwPollEvents();
			
			// Delete sprite list & reset old variables
			entities.clear();
			
		} else {
			
			GL30.glDeleteProgram(textureShader);
			
			Controller.close();
			
		}
		
	}

	private short toHalfFloat(float f) { // This is magic //
		
		if(f > 65504.0f) {return (short)0x7c00;}
		if(f < -65504.0f) {return(short)0xfc00;}
		if(f == 0.0f) return(short)0x0000;
		if(f == -0.0f) return(short)0x8000;
		if(f > 0.0f && f < 5.96046E-8f) return 0x0001;
		if(f < 0.0f && f > -5.96046E-8f) return(short)0x8001;
		
		final int v = Float.floatToIntBits(f);
		
		return(short)((( v >>16 ) & 0x8000 ) | (((( v & 0x7f800000 ) - 0x38000000 )>>13 ) & 0x7c00 ) | (( v >>13 ) & 0x03ff ));
		
	}

	@Override
	public void windowResized() {
		
		projection = new Matrix4f().ortho(
				0f, (float) Controller.globals.screenSize.x,
				(float) Controller.globals.screenSize.y, 0f,
				-1f, 1f);
		
		FloatBuffer projectionBuffer = BufferUtils.createFloatBuffer(16);
		projection.get(projectionBuffer);
		
		GL30.glUniformMatrix4fv(GL30.glGetUniformLocation(textureShader, "projection"), false, projectionBuffer);
		
	}

}
