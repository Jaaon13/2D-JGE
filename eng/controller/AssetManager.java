package controller;

import java.awt.Point;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;

import assets.Asset;
import assets.Atlas;
import assets.Shader;
import assets.Texture;
import fileManager.ReadFile;
import logger.Logger.LoggerInfo;
import utilities.ShaderUtils;

public class AssetManager {
	
	// All of the available types for when asking for a specific asset
	private final int 
		TEXTURE_TID = 0,
		SHADER_TID = 1, 
		ATLAS_TID = 2,
		ENTITY_TID = 3,
		TEMPORARY_TID = 4; // Will overflow not meant to be a permanent id, expect it to be lost after current frame; Used primarily for text
	
	private Set<Asset> assets = new HashSet<>();
	
	private final int BYTE3LIMIT = 16777216;
	
	protected int entityInc = -1;
	
	// This will replace sprite ids
	public int genEntityID() {
		entityInc++;
		
		if(entityInc >= BYTE3LIMIT) {
			Controller.logger.log("Entity count exceeded 3 byte limit!!", LoggerInfo.ERROR);
			entityInc = 0;
		}
		
		return getNewID(entityInc, ENTITY_TID);
	}
	
	protected int tempInc = -1;
	
	public int genTempID() {
		tempInc++;
		return getNewID(tempInc, TEMPORARY_TID);
	}
	
	// Generate the new ID format
		// Fully unsigned
		// First byte is the type of the ID and the last 3 bytes is the ID
	private int getNewID(int id, int type) {
		
		String t;
		
		switch(type) {
		
		case 0:
			t = "TEXTURE";
			break;
			
		case 1:
			t = "SHADER";
			break;
			
		case 2:
			t = "ATLAS";
			break;
			
		case 3:
			t = "ENTITY";
			break;
			
		case 4:
			t = "TEXT";
			break;
			
		default:
			t = "UNKNOWN";
			break;
		
		}
		
		if(!t.equals("ENTITY") && !t.equals("TEXT")) {
			Controller.logger.log("ID #" + id + " generated for type: " + t, LoggerInfo.INFO);
		}
		
		return ((type & 0xFF) << 24 | (id << 8) >> 8);
		
	}
	
	// Translates the ID into a readable format
		// int[0] is the type and int[1] is the id
	public int[] translateID(int id) {
		return new int[] {(id >> 24), (id << 8) >> 8};
	}
	
	// Get asset based on id and file type
	@SuppressWarnings("unchecked")
	public <T extends Asset> T get(int inID) {
		
		for(Asset a : assets) {
			
			if(a.id == inID) {
				return (T) a;
			}
			
		}
		
		return null;
	}
	
	// Get Asset using just the file location !cannot use for programs!
	@SuppressWarnings("unchecked")
	public <T extends Asset> T get(String filePath) {
		
		for(Asset i : assets) {
			
			if(i.filePath.equals(filePath)) {
				return (T) i;
			}
			
		}
		
		return null;
		
	}
	
	// Takes file path w/o the directory
	@SuppressWarnings("unchecked")
	public <T extends Asset> T load(String filePath) {
		
		Asset a;
		
		if(filePath.contains(":")) {
			a = get(filePath);
		} else {
			a = get(Controller.globals.dir + filePath);
		}
		
		if(Objects.nonNull(a)) {
			return (T) a;
		}
		
		Controller.logger.log("Asset Loading: " + filePath, LoggerInfo.INFO);
		
		try {
			a = findFunction(new String[] {filePath});
		} catch (Exception e) {
			
			Controller.logger.log(List.of("Failed to load asset: " + filePath, "Stack Trace: " + e.getMessage()), LoggerInfo.ERROR);
			
		}
		
		if(a != null) {
			assets.add(a);
		}
		
		return (T) a;
		
	}
	
	// Used to load a shader specifically
	@SuppressWarnings("unchecked")
	public <T extends Asset> T load(String fp1, String fp2) {
		
		Asset a = null;
		
		Controller.logger.log("Asset(s) Loading: " + fp1 + " | " + 
		((fp2 != null) ? ((!fp2.isBlank() && !fp2.isEmpty()) ? fp2 : "null") : "null"), LoggerInfo.INFO);
		
		try {
			a = findFunction(new String[] {fp1, fp2});
		} catch (Exception e) {
			
			Controller.logger.log(List.of("Failed to load asset: " + fp1, "Stack Trace: " + e.getMessage()), LoggerInfo.ERROR);
			
		}
		
		if(a != null) {
			assets.add(a);
		}
		
		return (T) a;
		
	}
	
	private Asset findFunction(String[] filePath) throws Exception {
		
		String p;
		
		if(filePath[0].contains(":")) {
			p = filePath[0];
		} else {
			p = Controller.globals.dir + filePath[0];
		}
		
		switch(parseFileType(filePath[0])) {
		
			case "png", "jpg", "jpeg": // Texture File
				return loadTexture(p);
			
			case "vert", "frag": // Shader File // TODO: Refactor to allow custom shaders to be built
				return loadShader(Controller.globals.dir + filePath[0], parseFileType(filePath[0]).toCharArray()[0],
						Controller.globals.dir + filePath[1]);
			
			case "atlas": // Atlas definition file // TODO: Finish making the atlas creation tool
				return loadAtlas(Controller.globals.dir + filePath[0]);
				
			default:
				
				String error = (filePath.length == 1) ? filePath[0] : (filePath[0] + " | " + filePath[1]);
				
				throw new Exception("File: \"" + error + "\" is not an accepted format!");
	
		}
	}
	
	private int atlasIDInc = 0;

	private Atlas loadAtlas(String path) {
		
		ReadFile file = new ReadFile(path);
		
		Atlas a = new Atlas();
		a.filePath = path;
		a.id = getNewID(atlasIDInc, ATLAS_TID);
		atlasIDInc++;
		
		while(true) {
			
			String line = file.nextLine();
			
			if(Objects.isNull(line)) {break;}
			
			char[] data = line.toCharArray();
			
			/* 
			 * 0: top left x
			 * 1: top left y
			 * 2: bottom right x
			 * 3: bottom right y
			 * 4: name
			 */
			String[] types = {"", "", "", "", ""};
			int inc = 0;
			
			for(char c : data) {
				
				if((c == ',' || c == ':' || c == ';') && inc != 4) {
					inc++;
					continue;
				}
				
				types[inc] += c;
				
			}
			
			Point tl = new Point(Integer.parseInt(types[0]), Integer.parseInt(types[1]));
			Point br = new Point(Integer.parseInt(types[2]), Integer.parseInt(types[3]));
			
			if(br.x == -1) {
				
				a.size = tl;
				
			} else {
				
				a.section.put(types[4], tl);
				
			}
			
		}
		
		return a;
	}

	private String parseFileType(String filePath) {
		
		char[] c = filePath.toCharArray();
		
		String extension = "";
		
		for(int x = filePath.indexOf('.') + 1; x < c.length; x++) {
			
			extension += c[x];
			
		}
		
		return extension;
	}
	
	private Shader loadShader(String p1, char p1Type, String p2) {
		
		String vert, frag;
		
		if(p1Type == 'v') {
			vert = p1; frag = p2;
		} else {
			vert = p2; frag = p1;
		}
		
		int id = ShaderUtils.createShader(vert, frag);
		
		return new Shader(getNewID(id, SHADER_TID));
	}

	private Texture loadTexture(String filePath) throws Exception {
		
		IntBuffer width = BufferUtils.createIntBuffer(1);
		IntBuffer height = BufferUtils.createIntBuffer(1);
		IntBuffer channels = BufferUtils.createIntBuffer(1);
		
		ByteBuffer imageBuffer = null;
		
		try(InputStream is = new FileInputStream(filePath)) {
			
			byte[] bytes = is.readAllBytes();
			ByteBuffer directBuffer = BufferUtils.createByteBuffer(bytes.length);
			directBuffer.put(bytes).flip();
			
			imageBuffer = STBImage.stbi_load_from_memory(directBuffer, width, height, channels, 4);
			
		}
		
		if(Objects.isNull(imageBuffer)) {
			
			throw new RuntimeException("File with name: " + filePath + " could not be loaded!");
			
		}
		
		int textureID = GL11.glGenTextures();
		GL11.glBindTexture(GL30.GL_TEXTURE_2D, textureID);
		
		GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
		
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
	    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
	    
	    GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width.get(0), height.get(0), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, imageBuffer);
	    GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
		
	    STBImage.stbi_image_free(imageBuffer);
	    
	    Point size = new Point(width.get(), height.get());
	    
		return new Texture(size, getNewID(textureID, TEXTURE_TID), filePath);
		
	}

}
