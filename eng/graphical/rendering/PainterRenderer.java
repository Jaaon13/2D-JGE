package graphical.rendering;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_LINE;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SHORT;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glPolygonMode;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.GL_INFO_LOG_LENGTH;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.GL_HALF_FLOAT;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.awt.Point;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;

import controller.Controller;
import ecs.EngineComponets.Collision;
import ecs.EngineComponets.Depth;
import ecs.EngineComponets.PlainShape;
import ecs.EngineComponets.PlainShape.Shape;
import ecs.EngineComponets.Pos;
import ecs.EngineComponets.Size;
import ecs.EngineComponets.TextureC;
import ecs.Entity;
import ecs.EntityManager;
import gui.factorys.Text;
import gui.factorys.TextFactory;
import logger.Logger.LoggerInfo;
import sceneManagment.World;

public class PainterRenderer extends Renderer {
		
	private int textureShader, colorShader;
		
	private int textureVAO, simpleVAO, tVBO, tEBO, sVBO, sEBO;
		
	// Size of a short( 2 bytes ) * number of elements in a vertex
	private final int textVertexSize = 2 * 4, simpVertexSize = 2 * 5;
		
	@Override
	public void initalize() {

		createVAOs();
			
		// Make the shader programs
		textureShader = Controller.assets.load("shaders\\textVert.vert", "shaders\\textFrag.frag").getRawID();
		verifyShader("Texture Shader", textureShader);
		colorShader = Controller.assets.load("shaders\\colorVert.vert", "shaders\\colorFrag.frag").getRawID();
		verifyShader("Simple Shader", colorShader);
					
		glEnable(GL_BLEND);
					
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
			
	}
	
	private void verifyShader(String shaderName, int id) {
		int code = glGetProgrami(id, GL_LINK_STATUS);
		if(code == GL_TRUE) {
			Controller.logger.log(shaderName + ": has successfully compiled", LoggerInfo.INFO);
		} else {
			String infoLog = glGetProgramInfoLog(id, glGetProgrami(id, GL_INFO_LOG_LENGTH));
			Controller.logger.log(List.of(shaderName + ": has failed to be compiled", "Info Log: " + infoLog), LoggerInfo.ERROR);
		}
	}

	private void createVAOs() {
		textureVAO = glGenVertexArrays();
		
		glBindVertexArray(textureVAO);
		
		// Create the Texture VBO
		tVBO = glGenBuffers();
		tEBO = glGenBuffers();
			
		// Bind the buffers
		glBindBuffer(GL_ARRAY_BUFFER, tVBO);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, tEBO);
			
		// Index to start at, number of parameters per vertex, the type, whether or not it needs to be normalized,
		// the byte length of the vertex, and the offset in bytes
							
		// Position
		glVertexAttribPointer(0, 2, GL_SHORT, false, textVertexSize, 0);
							
		// Texture Position
		glVertexAttribPointer(1, 2, GL_HALF_FLOAT, false, textVertexSize, 4);
							
		// Enable the Vertex Attrib
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		
		// Unbind Texture VBO
		glBindVertexArray(0);
		
		simpleVAO = glGenVertexArrays();
		
		glBindVertexArray(simpleVAO);
		
		// Create the Texture VBO
		sVBO = glGenBuffers();
		sEBO = glGenBuffers();
					
		// Bind the buffers
		glBindBuffer(GL_ARRAY_BUFFER, sVBO);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, sEBO);
		
		// Position
		glVertexAttribPointer(0, 2, GL_SHORT, false, simpVertexSize, 0);
		// Color
		glVertexAttribPointer(1, 3, GL_SHORT, false, simpVertexSize, 4);
		
		// Enable the Vertex Attrib
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		
		// Unbind Simple VBO
		glBindVertexArray(0);
	}

	private Matrix4f projection;
	
	@Override
	public void windowResized() {
		
		projection = new Matrix4f().ortho(
				0f, (float) Controller.globals.screenSize.x,
				(float) Controller.globals.screenSize.y, 0f,
				-1f, 1f);
		
	}
	
	private void setProjectionUniform() {
		FloatBuffer projectionBuffer = BufferUtils.createFloatBuffer(16);
		projection.get(projectionBuffer);
		glUniformMatrix4fv(glGetUniformLocation(textureShader, "projection"), false, projectionBuffer);
	}
	
	private abstract class Mesh {
		
		protected short[] vertexes;
		protected int[] indexes;
		
		public abstract short[] getVertexes();
		
		public abstract int[] getIndexes();
		
	}
	
	private class TextureMesh extends Mesh {

		public TextureMesh(List<Entity> entities, EntityManager em) {
			
			vertexes = new short[entities.size() * 16];
			indexes = new int[entities.size() * 6];
			
			for(Entity e : entities) {
				
				process(e, em);
				
			}
			
		}
		
		private int pVert = 0, pInd = 0;
		
		private void process(Entity e, EntityManager em) {
			
			if(!validate(e, em)) {return;}
			
			Pos pos = em.get(e, Pos.class);
			Size size = em.get(e, Size.class);
			TextureC texture = em.get(e, TextureC.class);
			
			boolean atlasUsed = (texture != null) ? (texture.atlas != null) ? true : false : false;
			
			Point newPos = (Controller.globals.camera != null) ? 
					new Point(pos.x - Controller.globals.camera.pos.x, (pos.y - Controller.globals.camera.pos.y)):
					new Point(pos.x, pos.y);
			
			generateValues(newPos, size, texture, atlasUsed);
			
		}
		
		private int incInd = 0;
		
		private void generateValues(Point pos, Size size, TextureC texture, boolean usesAtlas) {
			// Check if the atlas exists if not set it to one so when we divide w/ it, it cannot alter sizing
			float atlasSizeX = (usesAtlas) ? (float) texture.texture.atlas.size.x : 1f;
			float atlasSizeY = (usesAtlas) ? (float) texture.texture.atlas.size.y : 1f;
			
			// All of the texture positions
			Point bl = (usesAtlas) ? new Point(texture.atlas) : new Point(0, 0);
			Point br = (usesAtlas) ? new Point(texture.atlas.x + size.x, texture.atlas.y) : new Point(1, 0);
			Point tr = (usesAtlas) ? new Point(texture.atlas.x + size.x, texture.atlas.y + size.y) : new Point(1, 1);
			Point tl = (usesAtlas) ? new Point(texture.atlas.x, texture.atlas.y + size.y) : new Point(0, 1);
			
			// Top left
				// Position
			vertexes[pVert] = (short) (pos.x);
			vertexes[pVert + 1] = (short) (pos.y + size.y);
				// Texture Cords
			vertexes[pVert + 2] = toHalfFloat((float) tl.x / atlasSizeX);
			vertexes[pVert + 3] = toHalfFloat((float) tl.y / atlasSizeY);
			
			// Top Right
				// Position
			vertexes[pVert + 4] = (short) (pos.x + size.x);
			vertexes[pVert + 5] = (short) (pos.y + size.y);
				// Texture Cords
			vertexes[pVert + 6] = toHalfFloat((float) tr.x / atlasSizeX);
			vertexes[pVert + 7] = toHalfFloat((float) tr.y / atlasSizeY);
			
			// Bottom Right
				// Position
			vertexes[pVert + 8] = (short) (pos.x + size.x);
			vertexes[pVert + 9] = (short) (pos.y);
				// Texture Cords
			vertexes[pVert + 10] = toHalfFloat((float) br.x / atlasSizeX);
			vertexes[pVert + 11] = toHalfFloat((float) br.y / atlasSizeY);
			
			// Bottom left
				// Position
			vertexes[pVert + 12] = (short) (pos.x);
			vertexes[pVert + 13] = (short) (pos.y);
				// Texture Cords
			vertexes[pVert + 14] = toHalfFloat((float) bl.x / atlasSizeX);
			vertexes[pVert + 15] = toHalfFloat((float) bl.y / atlasSizeY);
			
			pVert += 16;
			
			// Index shit
			indexes[pInd] = incInd;
			indexes[pInd + 1] = incInd + 1;
			indexes[pInd + 2] = incInd + 2;
			indexes[pInd + 3] = incInd + 2;
			indexes[pInd + 4] = incInd + 3;
			indexes[pInd + 5] = incInd;
			
			incInd += 4;
			pInd += 6;
			
			trianglesDrawn += 2;
			
		}

		private boolean validate(Entity e, EntityManager em) {
			
			if(!em.contains(e, List.of(Pos.class, Size.class))) {
				Controller.logger.log(
						List.of("BASIC RENDERER: Tried to render an invalid entity.", "ID: " + e.id), LoggerInfo.ERROR);
				return false;
			} else if(!em.contains(e, TextureC.class) && !em.contains(e, PlainShape.class) ) {
				Controller.logger.log(
						List.of("BASIC RENDERER: Tried to render an entity without a rendering option.", "ID: " + e.id), LoggerInfo.ERROR);
				return false;
			}
			
			return true;
			
		}

		
		@Override
		public short[] getVertexes() {
			short[] tosend = new short[pVert];
			
			for(int x = 0; x < pVert; x++) {
				
				tosend[x] = vertexes[x];
				
			}
			
			return tosend;
		}

		@Override
		public int[] getIndexes() {
			int[] tosend = new int[pInd];
			
			for(int x = 0; x < pInd; x++) {
				
				tosend[x] = indexes[x];
				
			}
			
			return tosend;
		}
		
		
		
	}
	
	private class SimpleMesh extends Mesh {
		
		private short[] vertexes;
		public PlainShape.Shape[] shapes;
		
		private int pVert = 0, pOther = 0;
		private int pInd = 0, incInd = 0;
		
		public SimpleMesh(List<Entity> entities, EntityManager em) {
			
			vertexes = new short[entities.size() * 20];
			indexes = new int[entities.size() * 6];
			
			shapes = new PlainShape.Shape[entities.size()];
			
			for(Entity e : entities) {
				
				process(e, em);
				
			}
			
		}
		
		private void process(Entity e, EntityManager em) {
			
			PlainShape plain = em.get(e, PlainShape.class);
			Pos pos = em.get(e, Pos.class);
			Size size = em.get(e, Size.class);
			
			// Top left
			vertexes[pVert] = (short) (pos.x);
			vertexes[pVert + 1] = (short) (pos.y + size.y);
			
			vertexes[pVert + 2] = (short) (plain.color[0]);
			vertexes[pVert + 3] = (short) (plain.color[1]);
			vertexes[pVert + 4] = (short) (plain.color[2]);
			
			// Top Right
			vertexes[pVert + 5] = (short) (pos.x + size.x);
			vertexes[pVert + 6] = (short) (pos.y + size.y);
			
			vertexes[pVert + 7] = (short) (plain.color[0]);
			vertexes[pVert + 8] = (short) (plain.color[1]);
			vertexes[pVert + 9] = (short) (plain.color[2]);
			
			// Bottom Right
			vertexes[pVert + 10] = (short) (pos.x + size.x);
			vertexes[pVert + 11] = (short) (pos.y);
			
			vertexes[pVert + 12] = (short) (plain.color[0]);
			vertexes[pVert + 13] = (short) (plain.color[1]);
			vertexes[pVert + 14] = (short) (plain.color[2]);
			
			// Bottom left
			vertexes[pVert + 15] = (short) (pos.x);
			vertexes[pVert + 16] = (short) (pos.y);
			
			vertexes[pVert + 17] = (short) (plain.color[0]);
			vertexes[pVert + 18] = (short) (plain.color[1]);
			vertexes[pVert + 19] = (short) (plain.color[2]);
			
			pVert += 20;
			shapes[pOther] = plain.shape;
			
			// Index shit
			indexes[pInd] = incInd;
			indexes[pInd + 1] = incInd + 1;
			indexes[pInd + 2] = incInd + 2;
			indexes[pInd + 3] = incInd + 2;
			indexes[pInd + 4] = incInd + 3;
			indexes[pInd + 5] = incInd;
						
			incInd += 4;
			pInd += 6;
			
			pOther++;
			
			trianglesDrawn += 2;
			
		}

		@Override
		public short[] getVertexes() {
			return vertexes;
		}

		@Override
		public int[] getIndexes() {
			return indexes;
		}
		
		
		
	}
	
	private class RenderWrapper {
		
		public int key;
		public Mesh m;
		
		public RenderWrapper(int key, Mesh m) {
			this.key = key;
			this.m = m;
		}
		
	}
	
	private final int textID = 
			Controller.assets.get(Controller.globals.dir + "fonts\\minogram_6x10.png").getRawID();
	
	private final int textLayer = Integer.MAX_VALUE;
	
	@Override
	public void render() {
		
		if(GLFW.glfwWindowShouldClose(Controller.globals.window)) {
			Controller.close();
			return;
		}
		
		drawCalls = 0;
		trianglesDrawn = 0;

		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		World textW = new World();
		
		EntityManager ecs = Controller.currentScene.world.ecs, textEcs = textW.ecs;
		
		Map<Integer, List<RenderWrapper>> layers = new HashMap<>();
		
		generateText(textW);
		
		int[] keylist = getKeyList(sortEntities(layers, Controller.currentScene.world, textW));
		
		List<Entity> text = new ArrayList<>(), tsimple = new ArrayList<>();
		
		for(Entity e : textW.container.getAllVisible()) {
			if(textW.ecs.contains(e, TextureC.class)) {
				text.add(e);
			} else {
				tsimple.add(e);
			}
		}
		
		if(!layers.containsKey(textLayer)) {
			layers.put(textLayer, new ArrayList<>());
		}
		
		layers.get(textLayer).add(new RenderWrapper(textID, new TextureMesh(text, textEcs)));
		
		if(tsimple.size() != 0) {
			layers.get(textLayer).add(new RenderWrapper(-1, new SimpleMesh(tsimple, textEcs)));
		}
		
		for(int i = 0; i < keylist.length; i++) {
			
			int key = keylist[i];
			
			for(RenderWrapper r : layers.get(key)) {
				
				if(r.m instanceof TextureMesh) {
					
					glUseProgram(textureShader);
					
					setProjectionUniform();
					
					glBindVertexArray(textureVAO);
					glBindBuffer(GL_ARRAY_BUFFER, tVBO);
					glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, tEBO);
					
					glBindTexture(GL_TEXTURE_2D, r.key);
					
					glBufferData(GL_ARRAY_BUFFER, r.m.getVertexes(), GL_DYNAMIC_DRAW);
					glBufferData(GL_ELEMENT_ARRAY_BUFFER, r.m.getIndexes(), GL_DYNAMIC_DRAW);
					
					glDrawElements(GL_TRIANGLES, r.m.getIndexes().length, GL_UNSIGNED_INT, 0);
					
					drawCalls++;
					
				} else if(r.m instanceof SimpleMesh) {
					
					glUseProgram(colorShader);
					
					setProjectionUniform();
					
					glBindVertexArray(simpleVAO);
					glBindBuffer(GL_ARRAY_BUFFER, sVBO);
					glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, sEBO);
					
					glBufferData(GL_ARRAY_BUFFER, r.m.getVertexes(), GL_DYNAMIC_DRAW);
					glBufferData(GL_ELEMENT_ARRAY_BUFFER, r.m.getIndexes(), GL_DYNAMIC_DRAW);
					
					glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
					
					glDrawElements(GL_TRIANGLES, r.m.getIndexes().length, GL_UNSIGNED_INT, 0);
					
					glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
					
					drawCalls++;
					
				} else {
					
					Controller.logger.log(List.of("INVALID MESH GIVEN: " + r.m.getClass()), LoggerInfo.ERROR);
					
				}
				
			}
			
		}
		
		// End Rendering //
		
		// swap the color buffers
		GLFW.glfwSwapBuffers(Controller.globals.window);
								
		// Poll events (User input)
		GLFW.glfwPollEvents();
					
		// Delete sprite list & reset old variables
		entities.clear();
		
		layers.clear();
	}

	private int[] getKeyList(int[] temp) {
		
		int[] keylist = new int[temp.length + 1];
		
		for(int x = 0; x < temp.length; x++) {
			keylist[x] = temp[x];
		}
		
		keylist[temp.length] = textLayer;
		
		return keylist;
	}

	private int[] sortEntities(Map<Integer, List<RenderWrapper>> layers, World w, World textW) {
		
		Map<Integer, List<Entity>> sortedLayer = new HashMap<>();
		
		for(Entity e : entities) {
			
			if(Controller.debug.hitBoxes) {
				if(w.ecs.contains(e, Collision.class)) {
					
					Collision c = w.ecs.get(e, Collision.class);
					
					Pos ep = w.ecs.get(e, Pos.class);
					
					if(!sortedLayer.containsKey(textLayer)) {
						sortedLayer.put(textLayer, new ArrayList<>());
					}
					new Entity(List.of(
							new Pos(ep.x + c.offset.x, ep.y + c.offset.y),
							new Size(c.size, c.size),
							new PlainShape(255, 0, 0, Shape.RECTANGLE)
							), textW, true);
				}
			}
			
			Depth d = w.ecs.get(e, Depth.class);
			
			int layer = (d != null) ? d.depth : 0;
			
			if(!sortedLayer.containsKey(layer)) {
				sortedLayer.put(layer, new ArrayList<>());
			}
			
			sortedLayer.get(layer).add(e);
			
		}
		
		if(Controller.debug.hitBoxes) {
			
			int cell = Controller.currentScene.world.container.cellSize;
			
			int x2 = Controller.globals.screenSize.x / cell;
			int y2 = Controller.globals.screenSize.y / cell;
			
			for(int x = 0; x < x2; x++) {
				for(int y = 0; y < y2; y++) {
					new Entity(List.of(
							new Pos(x*cell, y*cell),
							new Size(cell, cell),
							new PlainShape(30, 30, 30, Shape.RECTANGLE)
							), textW, true);
				}
			}
			
		}
		
		int[] sKeys = new int[sortedLayer.keySet().size()];
		
		int i = 0;
		for(int key : sortedLayer.keySet()) {			
			sKeys[i] = key;
			i++;
		}
		
		insertionSort(sKeys);
		
		for(int x = 0; x < sKeys.length; x++) {
			
			int layer = sKeys[x];
			
			Map<Integer, List<Entity>> batchedEntities = new HashMap<>();
			
			for(Entity e : sortedLayer.get(layer)) {
				
				TextureC texture = (w.ecs.contains(e, TextureC.class)) ? w.ecs.get(e, TextureC.class) : null;
				
				if(!w.ecs.contains(e, Pos.class)) {w.container.remove(e); continue;}
				
				int id = (texture != null) ? texture.TextureID : -1;
				
				if(!batchedEntities.containsKey(id)) {
					batchedEntities.put(id, new ArrayList<>());
				}
				
				batchedEntities.get(id).add(e);
				
			}
			
			for(int textureid : batchedEntities.keySet()) {
				
				if(!layers.containsKey(layer)) {
					layers.put(layer, new ArrayList<>());
				}
				
				layers.get(layer).add(new RenderWrapper(textureid, ((textureid != -1)
						? new TextureMesh(batchedEntities.get(textureid), w.ecs) 
						: new SimpleMesh(batchedEntities.get(textureid), w.ecs))));
				
			}
			
		}
		
		return sKeys;
		
	}

	private void insertionSort(int[] sKeys) {
		
		for(int x = 1; x < sKeys.length; ++x) {
			
			int key = sKeys[x];
			int j = x - 1;
			
			while(j >= 0 && sKeys[j] > key) {
				
				sKeys[j+1] = sKeys[j];
				j = j -1;
				
			}
			
			sKeys[j+1] = key;
			
		}
		
	}

	private void generateText(World w) {
		
		for(Text t : text) {
			
			if(t.data.isEmpty()) {continue;}
			
			TextFactory.generateText(t.data, t.pos, t.alignment, w, true);
			
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
	
}
