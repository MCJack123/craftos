import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.core.computer.IComputerEnvironment;
import dan200.computercraft.shared.peripheral.speaker.ISpeakerProvider;

import javax.annotation.Nonnull;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Speaker implements ISpeakerProvider {

    private IComputerEnvironment env;

    public Speaker(IComputerEnvironment e) {
        env = e;
    }

    @Nonnull
    @Override
    public Object[] playNote(Object[] arguments, ILuaContext context) throws LuaException {
        return playSound(arguments, context, true);
    }

    @Nonnull
    @Override
    public Object[] playSound(Object[] arguments, ILuaContext context, boolean isNote) throws LuaException {
        //new Thread(new Runnable() {
            // The wrapper thread is unnecessary, unless it blocks on the
            // Clip finishing; see comments.
            //public void run() {
                try {
                    Clip clip = AudioSystem.getClip();
                    AudioInputStream inputStream = AudioSystem.getAudioInputStream(env.createResourceFile("minecraft", "sound/" + arguments[0] + ".wav"));
                    clip.open(inputStream);
                    clip.start();
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                    return new Object[]{false};
                }
            //}
        //}).start();
        return new Object[]{true};
    }
}
