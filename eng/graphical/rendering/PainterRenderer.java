package graphical.rendering;

import java.awt.Point;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL30;

import assets.Shader;
import controller.Controller;
import ecs.EngineComponets.Depth;
import ecs.EngineComponets.PlainShape;
import ecs.EngineComponets.Pos;
import ecs.EngineComponets.Size;
import ecs.EngineComponets.TextureC;
import ecs.Entity;
import ecs.EntityManager;
import gui.factorys.Text;
import gui.factorys.TextFactory;
import logger.Logger.LoggerInfo;

public class PainterRenderer extends Renderer {

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

	private Matrix4f projection;
	
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
	
	private abstract class Mesh {
		
		@SuppressWarnings("unused")
		protected short[] vertexes;
		@SuppressWarnings("unused")
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
			
			Pos pos = (Pos) em.get(e, Pos.class);
			Size size = (Size) em.get(e, Size.class);
			TextureC texture = (TextureC) em.get(e, TextureC.class);
			
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
	
	private class RenderWrapper {
		
		public int key;
		public Mesh m;
		
		public RenderWrapper(int key, Mesh m) {
			this.key = key;
			this.m = m;
		}
		
	}
	
	private final int textID = 
			Controller.assets.get(Controller.globals.dir + "\\eng\\graphical\\rendering\\fonts\\minogram_6x10.png").getRawID();
	
	private final int textLayer = Integer.MAX_VALUE;
	
	@Override
	public void render() {
		
		if(GLFW.glfwWindowShouldClose(Controller.globals.window)) {
			Controller.close();
			return;
		}
		
		drawCalls = 0;
		trianglesDrawn = 0;
		
		GL30.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);
		
		EntityManager ecs = Controller.scenes.ecs, textEcs = new EntityManager();
		
		Map<Integer, List<RenderWrapper>> layers = new HashMap<>();
		
		generateText(textEcs);
		
		int[] keylist = getKeyList(sortEntities(layers, ecs));
		
		layers.put(textLayer, List.of(new RenderWrapper(textID, new TextureMesh(textEcs.getVisible(), textEcs))));
		
		for(int i = 0; i < keylist.length; i++) {
			
			String s;
			
			int key = keylist[i];
			
			switch(key) {
			
			case Integer.MIN_VALUE:
				s = "Background | " + key;
				break;
				
			case Integer.MAX_VALUE:
				s = "GUI | " + key;
				break;
				
			case 0:
				s = "Center | " + key;
				break;
				
			default:
				s = "" + key;
				break;
			
			}
			
			for(RenderWrapper r : layers.get(key)) {
				
				if(r.m instanceof TextureMesh) {
					
					GL30.glUseProgram(textureShader);
					
					GL30.glBindTexture(GL30.GL_TEXTURE_2D, r.key);
					
					GL30.glBufferData(GL30.GL_ARRAY_BUFFER, r.m.getVertexes(), GL30.GL_DYNAMIC_DRAW);
					GL30.glBufferData(GL30.GL_ELEMENT_ARRAY_BUFFER, r.m.getIndexes(), GL30.GL_DYNAMIC_DRAW);
					
					GL30.glDrawElements(GL30.GL_TRIANGLES, r.m.getIndexes().length, GL30.GL_UNSIGNED_INT, 0);
					
					drawCalls++;
					
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

	private int[] sortEntities(Map<Integer, List<RenderWrapper>> layers, EntityManager ecs) {
		
		Map<Integer, List<Entity>> sortedLayer = new HashMap<>();
		
		for(Entity e : entities) {
			
			Depth d = (Depth) ecs.get(e, Depth.class);
			
			int layer = (d != null) ? d.depth : 0;
			
			if(!sortedLayer.containsKey(layer)) {
				sortedLayer.put(layer, new ArrayList<>());
			}
			
			sortedLayer.get(layer).add(e);
			
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
				
				TextureC texture = (ecs.contains(e, TextureC.class)) ? (TextureC) ecs.get(e, TextureC.class) : null;
				
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
						? new TextureMesh(batchedEntities.get(textureid), ecs) 
						: null)));
				
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

	private void generateText(EntityManager textEcs) {
		
		for(Text t : text) {
			
			if(t.data.isEmpty()) {continue;}
			
			TextFactory.generateText(t.data, t.pos, t.alignment, textEcs, true);
			
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
