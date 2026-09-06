import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SoundPlayer {
    // Map to store sound files with their names as keys
    private Map<String, Clip> soundMap;

    public SoundPlayer() {
        soundMap = new HashMap<>();
    }

    // Method to add a sound file to the map
    public void addSound(String name, String filePath) {
        try {
            File soundFile = new File(filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            soundMap.put(name, clip); // Store the clip with the associated name
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Error loading sound file: " + filePath);
            e.printStackTrace();
        }
    }

    // Method to play a sound by its name without blocking the main thread
    public void playSound(final String name) {
        // Run the sound playing in a separate thread
        new Thread(() -> {
            Clip clip = soundMap.get(name);
            if (clip != null) {
                clip.setFramePosition(0); // Rewind the clip if it's already been played
                clip.start();
                try {
                    Thread.sleep(clip.getMicrosecondLength() / 1000); // Wait for the sound to finish (in milliseconds)
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Sound with name '" + name + "' not found.");
            }
        }).start();
    }
}
