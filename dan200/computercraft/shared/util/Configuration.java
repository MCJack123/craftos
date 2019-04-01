package dan200.computercraft.shared.util;

import dan200.computercraft.ComputerCraft;

import java.io.*;

public class Configuration implements Serializable {
    public boolean http_enable;
    public String[] http_whitelist;
    public String[] http_blacklist;
    public boolean disable_lua51_features;
    public String default_computer_settings;
    public boolean logPeripheralErrors;

    public int computerSpaceLimit;
    public int maximumFilesOpen;
    public int maxNotesPerTick;
    public int clockSpeed;

    public Object get(String name) {
        switch (name) {
            case "http_enable":
                return http_enable;
            case "disable_lua51_features":
                return disable_lua51_features;
            case "default_computer_settings":
                return default_computer_settings;
            case "logPeripheralErrors":
                return logPeripheralErrors;
            case "computerSpaceLimit":
                return computerSpaceLimit;
            case "maximumFilesOpen":
                return maximumFilesOpen;
            case "maxNotesPerTick":
                return maxNotesPerTick;
            case "clockSpeed":
                return clockSpeed;
            case "http_whitelist":
                return http_whitelist;
            case "http_blacklist":
                return http_blacklist;
            default:
                return null;
        }
    }

    public String[] list() {
        return new String[] {"http_enable", "disable_lua51_features", "default_computer_settings", "logPeripheralErrors", "computerSpaceLimit", "maximumFilesOpen", "maxNotesPerTick", "clockSpeed", "http_whitelist", "http_blacklist"};
    }

    public int getType(String name) {
        switch (name) {
            case "http_enable":
                return 0;
            case "disable_lua51_features":
                return 0;
            case "default_computer_settings":
                return 1;
            case "logPeripheralErrors":
                return 0;
            case "computerSpaceLimit":
                return 2;
            case "maximumFilesOpen":
                return 2;
            case "maxNotesPerTick":
                return 2;
            case "clockSpeed":
                return 2;
            case "http_whitelist":
                return 3;
            case "http_blacklist":
                return 3;
            default:
                return -1;
        }
    }

    public void set(String name, Object value) {
        switch (name) {
            case "http_enable":
                http_enable = (boolean) value;
            case "disable_lua51_features":
                disable_lua51_features = (boolean) value;
            case "default_computer_settings":
                default_computer_settings = (String) value;
            case "logPeripheralErrors":
                logPeripheralErrors = (boolean) value;
            case "computerSpaceLimit":
                computerSpaceLimit = ((Double) value).intValue();
            case "maximumFilesOpen":
                maximumFilesOpen = ((Double) value).intValue();
            case "maxNotesPerTick":
                maxNotesPerTick = ((Double) value).intValue();
            case "clockSpeed":
                clockSpeed = ((Double) value).intValue();
            case "http_whitelist":
                http_whitelist = (String[]) value;
            case "http_blacklist":
                http_blacklist = (String[]) value;
        }
    }

    public void serialize(String file) {
        ObjectOutputStream oos = null;
        FileOutputStream fout = null;
        try{
            fout = new FileOutputStream(file, true);
            oos = new ObjectOutputStream(fout);
            oos.writeObject(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if(oos != null){
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Configuration(String file) {
        ObjectInputStream objectinputstream = null;
        try {
            FileInputStream streamIn = new FileInputStream(file);
            objectinputstream = new ObjectInputStream(streamIn);
            Configuration obj = (Configuration) objectinputstream.readObject();
            this.http_enable = obj.http_enable;
            this.disable_lua51_features = obj.disable_lua51_features;
            this.default_computer_settings = obj.default_computer_settings;
            this.logPeripheralErrors = obj.logPeripheralErrors;
            this.computerSpaceLimit = obj.computerSpaceLimit;
            this.maximumFilesOpen = obj.maximumFilesOpen;
            this.maxNotesPerTick = obj.maxNotesPerTick;
            this.clockSpeed = obj.clockSpeed;
            this.http_whitelist = obj.http_whitelist;
            this.http_blacklist = obj.http_blacklist;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(objectinputstream != null){
                try {
                    objectinputstream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Configuration() {
        http_enable = ComputerCraft.http_enable;
        disable_lua51_features = ComputerCraft.disable_lua51_features;
        default_computer_settings = ComputerCraft.default_computer_settings;
        logPeripheralErrors = ComputerCraft.logPeripheralErrors;
        computerSpaceLimit = ComputerCraft.computerSpaceLimit;
        maximumFilesOpen = ComputerCraft.maximumFilesOpen;
        maxNotesPerTick = ComputerCraft.maxNotesPerTick;
        clockSpeed = ComputerCraft.clockSpeed;
        http_whitelist = ComputerCraft.DEFAULT_HTTP_WHITELIST;
        http_blacklist = ComputerCraft.DEFAULT_HTTP_BLACKLIST;
    }
}