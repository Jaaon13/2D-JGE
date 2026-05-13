package graphical.rendering;

import java.awt.Point;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import assets.Shader;
import controller.Controller;
import ecs.EngineComponets.Pos;
import ecs.EngineComponets.Size;
import ecs.EngineComponets.TextureC;
import ecs.Entity;
import graphical.componets.ESprite;
import graphical.componets.Tags;
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
		GL30.glVertexAttribPointer(0, 2, GL30.GL_HALF_FLOAT, false, vertexSize, 0);
						
		// Texture Position
		GL30.glVertexAttribPointer(1, 2, GL30.GL_HALF_FLOAT, false, vertexSize, 4);
						
		// Enable the Vertex Attrib
		GL30.glEnableVertexAttribArray(0);
		GL30.glEnableVertexAttribArray(1);
		
		// Make the shader programs
		textureShader = ((Shader) Controller.assets.load(path + "textVert.vert", path + "textFrag.frag")).getRawID();
		colorShader = ((Shader) Controller.assets.load(path + "colorVert.vert", path + "colorFrag.frag")).getRawID();
				
		GL30.glUniform1i(GL30.glGetUniformLocation(textureShader, "text"), 0);
			
		GL30.glActiveTexture(GL30.GL_TEXTURE0);
				
		GL30.glEnable(GL30.GL_BLEND);
				
		GL30.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
		
	}

	private class Meshes {
		
		public short[] vertexes = new short[entities.size() * 16];
		public int[] indexes = new int[entities.size() * 6];
		
		public Point camOff;
		
		final int width = Controller.globals.screenSize.x;
		final int height = Controller.globals.screenSize.y;
		
		public Meshes() {
			Arrays.fill(vertexes, (short) -1);
			Arrays.fill(indexes, -1);
			camOff = (camera) ? Controller.globals.camera.pos : new Point(0, 0);
		}
		
		private int vertPointer = 0, indPointer = 0;
		
		// Used as the index value
		private int indinc = 0;
		
		private void addEntity(Entity e) {
			
			if(!e.containsComponets(List.of("Pos", "Size"))) {
				Controller.logger.log(
						List.of("BASIC RENDERER: Tried to render an invalid entity.", "ID: " + e.id), LoggerInfo.ERROR);
				return;
			} else if(!e.containsComponet("TextureC") && !e.containsComponet("PlainShape") ) {
				Controller.logger.log(
						List.of("BASIC RENDERER: Tried to render an entity without a rendering option.", "ID: " + e.id), LoggerInfo.ERROR);
				return;
			}
			
			Pos posComp = (Pos) e.componets.get("Pos");
			Size sizeComp = (Size) e.componets.get("Size");
			
			boolean usesAtlas = (e.containsComponet("TextureC"))
					? ((((TextureC) e.componets.get("TextureC")).atlas != null) ? true : false) : false;
			
			TextureC t = (usesAtlas) ? (TextureC) e.componets.get("TextureC") : null;
			
			Point newPos = (Controller.globals.camera != null) ? 
					new Point(posComp.x - Controller.globals.camera.pos.x, height - (height - posComp.y - Controller.globals.camera.pos.y)):
					new Point(posComp.x, height - posComp.y);
			
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
			vertexes[vertPointer] = toHalfFloat(normal(newPos.x, width));
			vertexes[vertPointer + 1] = toHalfFloat(normal(newPos.y, height));
				// Texture Cords
			vertexes[vertPointer + 2] = toHalfFloat((float) tl.x / atlasSizeX);
			vertexes[vertPointer + 3] = toHalfFloat((float) tl.y / atlasSizeY);
			
			// Top Right
				// Position
			vertexes[vertPointer + 4] = toHalfFloat(normal(newPos.x + sizeComp.x, width));
			vertexes[vertPointer + 5] = toHalfFloat(normal(newPos.y, height));
				// Texture Cords
			vertexes[vertPointer + 6] = toHalfFloat((float) tr.x / atlasSizeX);
			vertexes[vertPointer + 7] = toHalfFloat((float) tr.y / atlasSizeY);
			
			// Bottom Right
				// Position
			vertexes[vertPointer + 8] = toHalfFloat(normal(newPos.x + sizeComp.x, width));
			vertexes[vertPointer + 9] = toHalfFloat(normal(newPos.y + sizeComp.y, height));
				// Texture Cords
			vertexes[vertPointer + 10] = toHalfFloat((float) br.x / atlasSizeX);
			vertexes[vertPointer + 11] = toHalfFloat((float) br.y / atlasSizeY);
			
			// Bottom left
				// Position
			vertexes[vertPointer + 12] = toHalfFloat(normal(newPos.x, width));
			vertexes[vertPointer + 13] = toHalfFloat(normal(newPos.y + sizeComp.y, height));
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
	
	public void textureRender(Meshes m, int key, ByteBuffer vertex, ByteBuffer index) {
		
		GL30.glBindTexture(GL30.GL_TEXTURE_2D, key);
		
		// Populate the byte buffers
		
		short[] vertexes = m.getVertexes();
		
		vertex.clear();
		vertex.limit((vertexes.length * 2));
		
		for(short s : vertexes) {
			try {
				vertex.putShort(s);
			} catch(Exception e) {
				
				System.err.println("BYTEBUFFER OVERFLOW : VERTEX");
				System.out.println("Limit: " + vertex.limit() + " Cap: " + vertex.capacity());
				
			}
		}
		
		int[] indexes = m.getIndexes();
		
		index.clear();
		index.limit((indexes.length * 4));
		
		for(int i : indexes) {
			try {
				index.putInt(i);
			} catch(Exception e) {
				
				System.err.println("BYTEBUFFER OVERFLOW : INDEX");
				System.out.println("Limit: " + index.limit() + " Cap: " + index.capacity());
				
			}
		}
		
		vertex.flip();
		index.flip();
		
		GL30.glUseProgram(textureShader);
		
		GL30.glBufferData(GL30.GL_ARRAY_BUFFER, vertex, GL30.GL_DYNAMIC_DRAW);
		GL30.glBufferData(GL30.GL_ELEMENT_ARRAY_BUFFER, index, GL30.GL_DYNAMIC_DRAW);
		
		GL30.glDrawElements(GL30.GL_TRIANGLES, index.remaining(), GL30.GL_UNSIGNED_INT, 0);
		
		drawCalls++;
		
	}
	
	private void primativeRender(Meshes m) {
		
		// #TODO
		
		
		
	}
	
	@Override
	public void render() {
		
		drawCalls = 0;
		trianglesDrawn = 0;
		
		if(!GLFW.glfwWindowShouldClose(Controller.globals.window)) {
			
			// Clear the frame buffer
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
						
			// Start Rendering //
			
			ByteBuffer vertex =  ByteBuffer.allocateDirect((entities.size() * vertexSize) * 4).order(ByteOrder.nativeOrder());
			ByteBuffer index = ByteBuffer.allocateDirect((entities.size() * 4) * 6).order(ByteOrder.nativeOrder());
			
			camera = (Objects.nonNull(Controller.globals.camera));
			
			// Key is texture ID
			Map<Integer, Meshes> meshes = new HashMap<>();
			
			// Meshify each sprite
			for(Entity e : entities) {
				
				TextureC t = (e.containsComponet("TextureC")) ? (TextureC) e.componets.get("TextureC") : null;
				
				int rawTextID = (Objects.nonNull(t)) ? t.texture.getRawID() : -1;
				
				if(meshes.containsKey(rawTextID)) {
					
					meshes.get(rawTextID).addEntity(e);
					
				} else {
					
					meshes.put(rawTextID, new Meshes());
					meshes.get(rawTextID).addEntity(e);
					
				}
				
			}
			
			// Draw everything
			
			for(int key : meshes.keySet()) {
				
				Meshes m = meshes.get(key);
				
				if(key != -1) {
					
					textureRender(m, key, vertex, index);
					
				} else {
					
					primativeRender(m);
					
				}
				
			}
			
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
	
	private float normal(int x, int div) {
		
		return ((float) x) / ((float) div / 2) -1f;
	}

}
