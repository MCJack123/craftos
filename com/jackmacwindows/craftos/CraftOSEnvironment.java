import dan200.computercraft.api.filesystem.IMount;
import dan200.computercraft.api.filesystem.IWritableMount;
import dan200.computercraft.core.computer.IComputerEnvironment;
import dan200.computercraft.core.filesystem.FileMount;

import java.io.File;
import java.io.InputStream;
import java.util.Calendar;

class CraftOSEnvironment implements IComputerEnvironment {
    public int getDay() {
        return Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
    }
    public double getTimeOfDay() {
        Calendar rightNow = Calendar.getInstance();
        int hour = rightNow.get(Calendar.HOUR_OF_DAY);
        int minute = rightNow.get(Calendar.MINUTE);
        return (double)hour + ((double)minute/60.0);
    }
    public boolean isColour() {
        return true;
    }
    public long getComputerSpaceLimit() {
        return getWorldDir().getTotalSpace();
    }
    public String getHostString() {
        return System.getProperty("os.name").concat(" ").concat(System.getProperty("os.arch")).concat(" ").concat(System.getProperty("os.version"));
    }

    public int assignNewID() {
        return 0;
    }
    public IWritableMount createSaveDirMount( String subPath, long capacity ) {
        return new FileMount(new File(getWorldDir().getAbsolutePath() + "/" + subPath), capacity);
    }
    public IMount createResourceMount( String domain, String subPath ) {
        return new FileMount(new File(getClass().getResource("assets/" + domain + "/" + subPath).toString().replaceAll("file:\\\\", "").replaceAll("file:/", "")), 0);/*
        try {
            return new JarMount(new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath()), "assets/" + domain + "/" + subPath);
        } catch (IOException e) {
            System.err.println("Couldn't mount " + domain + "/" + subPath);
            return null;
        }
        //*/
    }
    public InputStream createResourceFile( String domain, String subPath ) {
        return getClass().getResourceAsStream("assets/" + domain + "/" + subPath);
    }

    public static File getWorldDir()
    {
        return new File(System.getProperty("user.home").concat("/.craftos"));
    }
}