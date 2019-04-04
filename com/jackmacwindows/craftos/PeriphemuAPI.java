import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.core.apis.ILuaAPI;
import dan200.computercraft.core.computer.Computer;
import dan200.computercraft.core.computer.IComputerEnvironment;
import dan200.computercraft.core.filesystem.FileSystemException;
import dan200.computercraft.shared.peripheral.monitor.IMonitorProvider;
import dan200.computercraft.shared.peripheral.monitor.MonitorPeripheral;
import dan200.computercraft.shared.peripheral.printer.PrinterPeripheral;
import dan200.computercraft.shared.peripheral.printer.TextPrinter;
import dan200.computercraft.shared.peripheral.speaker.SpeakerPeripheral;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class PeriphemuAPI implements ILuaAPI {

    private Map<Integer, IPeripheral> peripherals = new HashMap<>();
    private Computer computer;
    private IComputerEnvironment environment;

    private static final Map<String, Integer> sides;
    static {
        sides = new HashMap<>();
        sides.put("bottom", 0);
        sides.put("top", 1);
        sides.put("back", 2);
        sides.put("front", 3);
        sides.put("right", 4);
        sides.put("left", 5);
    }

    PeriphemuAPI(Computer comp, IComputerEnvironment env) {
        computer = comp;
        environment = env;
    }

    @Override
    public String[] getNames() {
        return new String[]{"periphemu"};
    }

    @Override
    public void startup() {

    }

    @Override
    public void advance(double _dt) {
        //System.out.println("Ticking");
        for (IPeripheral p : peripherals.values()) p.tick();
    }

    @Override
    public void shutdown() {
        for (int p : peripherals.keySet()) {
            if (peripherals.get(p) instanceof IMonitorProvider) ((IMonitorProvider) peripherals.get(p)).destroy();
            computer.setPeripheral(p, null);
        }
        peripherals.clear();
    }

    public boolean remove(String side) {
        try {
            peripherals.get(sides.get(side)).willDetach();
            computer.setPeripheral(sides.get(side), null);
            peripherals.remove(sides.get(side));
        } catch (NullPointerException e) {
            return false;
        }
        return true;
    }

    @Nonnull
    @Override
    public String[] getMethodNames() {
        return new String[]{"create", "remove"};
    }

    @Nullable
    @Override
    public Object[] callMethod(@Nonnull ILuaContext context, int method, @Nonnull Object[] arguments) throws LuaException {
        switch (method) {
            case 0:
                // create(side, type, options...)
                if (arguments.length < 2) {
                    throw new LuaException("expected at least 2 arguments, got " + arguments.length);
                }
                String side = (String)arguments[0];
                IPeripheral peripheral;
                String type = (String)arguments[1];
                switch (type) {
                    case "monitor":
                        if (peripherals.containsKey(sides.get(side)) && peripherals.get(sides.get(side)) instanceof MonitorPeripheral) {
                            return new Object[] {false};
                        }
                        peripheral = new MonitorPeripheral(new Monitor(this, side));
                        break;
                    case "printer":
                        if (peripherals.containsKey(sides.get(side)) && peripherals.get(sides.get(side)) instanceof PrinterPeripheral) {
                            return new Object[] {false};
                        }
                        if (arguments.length < 3) {
                            throw new LuaException("expected 3 arguments, got 2");
                        }
                        try {
                            peripheral = new PrinterPeripheral(new TextPrinter(computer.getFileSystem().openForWrite((String) arguments[2], false)));
                        } catch (FileSystemException e) {
                            e.printStackTrace();
                            throw new LuaException("could not open file to write");
                        }
                        break;
                    case "speaker":
                        if (peripherals.containsKey(sides.get(side)) && peripherals.get(sides.get(side)) instanceof SpeakerPeripheral) {
                            return new Object[] {false};
                        }
                        peripheral = new SpeakerPeripheral(new Speaker(environment));
                        break;
                    default:
                        throw new LuaException("invalid peripheral type " + type);
                }
                computer.setPeripheral(sides.get(side), peripheral);
                peripherals.put(sides.get(side), peripheral);
                return new Object[] {true};
            case 1:
                // remove(side)
                if (arguments.length != 1) throw new LuaException("expected 1 argument, got " + arguments.length);
                //((MonitorPeripheral)sides.get(arguments[0])).
                return new Object[] {remove((String)arguments[0])};
        }
        return null;
    }

    void didResize(String side) {
        computer.queueEvent("monitor_resize", new Object[] {side});
    }
}
