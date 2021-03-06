package infra;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Set;

/**
 * Input class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class Input implements KeyListener {
    
    public static Set<Integer> keyPressed = new HashSet<>();
    public static Set<Integer> keyPressedConsumed = new HashSet<>();
    
    public static boolean isKeyPressed(int keyCode) {
        return keyPressed.contains(keyCode);
    }

    public static boolean isKeyJustPressed(int keyCode) {
        if (!keyPressedConsumed.contains(keyCode) 
                && keyPressed.contains(keyCode)) {
            
            keyPressedConsumed.add(keyCode);
            return true;
        }
        return false;
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        keyPressed.add(e.getKeyCode());
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        keyPressed.remove(e.getKeyCode());
        keyPressedConsumed.remove(e.getKeyCode());
    }
    
}
