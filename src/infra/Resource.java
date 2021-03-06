package infra;

import entity.Fygar;
import entity.Pooka;
import entity.Rock;
import static infra.Settings.*;
import java.awt.Font;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import scene.Stage;

/**
 * Resource class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class Resource {
    
    private static final Map<String, BufferedImage> IMAGES = new HashMap<>();
    private static final Map<String, byte[]> SOUNDS = new HashMap<>();
    private static final Map<String, Font> FONTS = new HashMap<>();

    private Resource() {
    }

    public static BufferedImage getImage(String name) {
        BufferedImage image = null;
        try {
            InputStream is = Resource.class.getResourceAsStream(
                RES_IMAGE_PATH + name + RES_IMAGE_FILE_EXT);
            
            image = IMAGES.get(name);
            if (image == null) {
                image = ImageIO.read(is);
                IMAGES.put(name, image);
            }
        } catch (Exception ex) {
            Logger.getLogger(
                    Resource.class.getName()).log(Level.SEVERE, null, ex);

            System.exit(-1);
        }
        return image;
    }
    
    public static byte[] getSound(String name) {
        byte[] sound;
        sound = SOUNDS.get(name);
        if (sound == null) {
            String soundResource = RES_SOUND_PATH + name + RES_SOUND_FILE_EXT;
            try (
                InputStream is = 
                    Resource.class.getResourceAsStream(soundResource);
                    
                InputStream bis = new BufferedInputStream(is);            
                AudioInputStream ais = AudioSystem.getAudioInputStream(bis)) {
                if (!ais.getFormat().matches(SOUND_AUDIO_FORMAT)) {
                    throw new Exception("Sound '" + soundResource + 
                                        "' format not compatible !");
                }
                long soundSize = 
                    ais.getFrameLength() * ais.getFormat().getFrameSize();
                
                sound = new byte[(int) soundSize];
                ais.read(sound);
                SOUNDS.put(name, sound);
            } 
            catch (Exception ex) {
                Logger.getLogger(
                        Resource.class.getName()).log(Level.SEVERE, null, ex);
                
                System.exit(-1);
            }
        }
        return sound;
    }
    
    public static Font getFont(String name) {
        Font font = FONTS.get(name);
        if (font == null) {
            String fontResource 
                    = RES_FONT_PATH + name + RES_FONT_FILE_EXT;

            try {
                font = Font.createFont(Font.TRUETYPE_FONT
                    , Resource.class.getResourceAsStream(fontResource));

                FONTS.put(name, font);
            } catch (Exception ex) {
                Logger.getLogger(
                        Resource.class.getName()).log(Level.SEVERE, null, ex);

                System.exit(-1);
            }
        }
        return font;
    }

    public static void loadLevel(int stage, Stage scene) {
        String levelResource 
                = RES_LEVEL_PATH + "level_" + stage + RES_LEVEL_FILE_EXT;

        try {
            InputStream is = Resource.class.getResourceAsStream(levelResource);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            int id = 1;
            double enemyChasingSpeed = 1.0;
            double enemyGhostSpeed = 0.25;
            String line = null;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                String[] data = line.split("\\ ");
                if (data[0].equals("enemy_chasing_speed")) {
                    enemyChasingSpeed = Double.parseDouble(data[1]);
                }
                else if (data[0].equals("enemy_ghost_speed")) {
                    enemyGhostSpeed = Double.parseDouble(data[1]);
                }
                else if (data[0].equals("tunnel")) {
                    int x = Integer.parseInt(data[1]);
                    int y = Integer.parseInt(data[2]);
                    int halfSize = (Integer.parseInt(data[3]) - 10) / 2;
                    String orientation = data[4];
                    if (orientation.equals("h")) {
                        for (int cx = -halfSize; cx < halfSize; cx++) {
                            Underground.dig(x + cx, y, false);
                        }
                    }
                    else if (orientation.equals("v")) {
                        for (int cy = -halfSize; cy < halfSize; cy++) {
                            Underground.dig(x, y + cy, false);
                        }
                    }
                    else {
                        throw new RuntimeException("error loading level " 
                                + stage + ", tunnel invalid '" 
                                + orientation + "'orientation ");
                    }
                }
                else if (data[0].equals("pooka")) {
                    int x = Integer.parseInt(data[1]);
                    int y = Integer.parseInt(data[2]);
                    Pooka pooka = new Pooka(scene, x, y);
                    pooka.setId("" + id++);
                    pooka.setChasingSpeed(enemyChasingSpeed);
                    pooka.setGhostSpeed(enemyGhostSpeed);
                    scene.addEntity(pooka);
                    GameInfo.addEnemyId(pooka.getId());
                }
                else if (data[0].equals("fygar")) {
                    int x = Integer.parseInt(data[1]);
                    int y = Integer.parseInt(data[2]);
                    Fygar fygar = new Fygar(scene, x, y);
                    fygar.setId("" + id++);
                    fygar.setChasingSpeed(enemyChasingSpeed);
                    fygar.setGhostSpeed(enemyGhostSpeed);
                    scene.addEntity(fygar);
                    scene.addEntity(fygar.getFire());
                    GameInfo.addEnemyId(fygar.getId());
                }
                else if (data[0].equals("rock")) {
                    int x = Integer.parseInt(data[1]);
                    int y = Integer.parseInt(data[2]);
                    Rock rock = new Rock(scene, x, y);
                    rock.setId("" + id++);
                    scene.addEntity(rock);
                }
            }
            br.close();
        } catch (Exception ex) {
            Logger.getLogger(
                    Resource.class.getName()).log(Level.SEVERE, null, ex);

            System.exit(-1);
        }
    }
    
}
