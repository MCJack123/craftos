/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2017. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.computercraft;

import dan200.computercraft.api.filesystem.IMount;
import dan200.computercraft.api.filesystem.IWritableMount;
import dan200.computercraft.core.apis.AddressPredicate;
import dan200.computercraft.core.filesystem.ComboMount;
import dan200.computercraft.core.filesystem.FileMount;
import dan200.computercraft.core.filesystem.JarMount;
import dan200.computercraft.shared.computer.core.ClientComputerRegistry;
import dan200.computercraft.shared.computer.core.ServerComputerRegistry;
import dan200.computercraft.shared.network.ComputerCraftPacket;
import dan200.computercraft.shared.network.NetworkManager;
import dan200.computercraft.shared.util.IDAssigner;
import dan200.computercraft.shared.util.Configuration;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

//import org.apache.logging.log4j.Logger;

///////////////
// UNIVERSAL //
///////////////

public class ComputerCraft
{
    public static final String MOD_ID = "computercraft";
    public static final String LOWER_ID = "computercraft";

    // GUI IDs
    public static final int diskDriveGUIID = 100;
    public static final int computerGUIID = 101;
    public static final int printerGUIID = 102;
    public static final int turtleGUIID = 103;
    // ComputerCraftEdu uses ID 104
    public static final int printoutGUIID = 105;
    public static final int pocketComputerGUIID = 106;

    // Configuration options
    public static final String[] DEFAULT_HTTP_WHITELIST = new String[] { "*" };
    public static final String[] DEFAULT_HTTP_BLACKLIST = new String[] {
        "127.0.0.0/8",
        "10.0.0.0/8",
        "172.16.0.0/12",
        "192.168.0.0/16",
        "fd00::/8",
    };
    
    public static boolean http_enable = true;
    public static AddressPredicate http_whitelist = new AddressPredicate( DEFAULT_HTTP_WHITELIST );
    public static AddressPredicate http_blacklist = new AddressPredicate( DEFAULT_HTTP_BLACKLIST );
    public static boolean disable_lua51_features = false;
    public static String default_computer_settings = "";
    public static boolean logPeripheralErrors = false;

    public static boolean enableCommandBlock = false;
    public static boolean turtlesNeedFuel = true;
    public static int turtleFuelLimit = 20000;
    public static int advancedTurtleFuelLimit = 100000;
    public static boolean turtlesObeyBlockProtection = true;
    public static boolean turtlesCanPush = true;

    public static final int terminalWidth_computer = 51;
    public static final int terminalHeight_computer = 19;

    public static final int terminalWidth_turtle = 39;
    public static final int terminalHeight_turtle = 13;

    public static final int terminalWidth_pocketComputer = 26;
    public static final int terminalHeight_pocketComputer = 20;

    public static int modem_range = 64;
    public static int modem_highAltitudeRange = 384;
    public static int modem_rangeDuringStorm = 64;
    public static int modem_highAltitudeRangeDuringStorm = 384;

    public static int computerSpaceLimit = 1000 * 1000;
    public static int floppySpaceLimit = 125 * 1000;
    public static int maximumFilesOpen = 128;

    public static int maxNotesPerTick = 8;
    public static int clockSpeed = 20;

    public static NetworkManager networkEventChannel;

    // Blocks and Items

    public static Configuration config;

    // Registries
    public static ClientComputerRegistry clientComputerRegistry = new ClientComputerRegistry();
    public static ServerComputerRegistry serverComputerRegistry = new ServerComputerRegistry();

    // API users

    // Implementation
    public static ComputerCraft instance;

    public ComputerCraft()
    {
    }

    //@Mod.EventHandler
    public void preInit( /*FMLPreInitializationEvent event*/ )
    {

        // Load config
        File f = new File(getWorldDir().toString() + "/config.ser");
        if (f.exists()) config = new Configuration(f.toString());
        else config = new Configuration();
        /*Config.config = new Configuration( event.getSuggestedConfigurationFile() );
        Config.config.load();

        Config.http_enable = Config.config.get( Configuration.CATEGORY_GENERAL, "http_enable", http_enable );
        Config.http_enable.setComment( "Enable the \"http\" API on Computers (see \"http_whitelist\" and \"http_blacklist\" for more fine grained control than this)" );

        {
            ConfigCategory category = Config.config.getCategory( Configuration.CATEGORY_GENERAL );
            Property currentProperty = category.get( "http_whitelist" );
            if( currentProperty != null && !currentProperty.isList() ) category.remove( "http_whitelist" );

            Config.http_whitelist = Config.config.get( Configuration.CATEGORY_GENERAL, "http_whitelist", DEFAULT_HTTP_WHITELIST );

            if( currentProperty != null && !currentProperty.isList() )
            {
                Config.http_whitelist.setValues( currentProperty.getString().split( ";" ) );
            }
        }
        Config.http_whitelist.setComment( "A list of wildcards for domains or IP ranges that can be accessed through the \"http\" API on Computers.\n" +
            "Set this to \"*\" to access to the entire internet. Example: \"*.pastebin.com\" will restrict access to just subdomains of pastebin.com.\n" +
            "You can use domain names (\"pastebin.com\"), wilcards (\"*.pastebin.com\") or CIDR notation (\"127.0.0.0/8\")." );

        Config.http_blacklist = Config.config.get( Configuration.CATEGORY_GENERAL, "http_blacklist", DEFAULT_HTTP_BLACKLIST );
        Config.http_blacklist.setComment( "A list of wildcards for domains or IP ranges that cannot be accessed through the \"http\" API on Computers.\n" +
            "If this is empty then all whitelisted domains will be accessible. Example: \"*.github.com\" will block access to all subdomains of github.com.\n" +
            "You can use domain names (\"pastebin.com\"), wilcards (\"*.pastebin.com\") or CIDR notation (\"127.0.0.0/8\")." );

        Config.disable_lua51_features = Config.config.get( Configuration.CATEGORY_GENERAL, "disable_lua51_features", disable_lua51_features );
        Config.disable_lua51_features.setComment( "Set this to true to disable Lua 5.1 functions that will be removed in a future update. Useful for ensuring forward compatibility of your programs now." );

        Config.default_computer_settings = Config.config.get( Configuration.CATEGORY_GENERAL, "default_computer_settings", default_computer_settings );
        Config.default_computer_settings.setComment( "A comma seperated list of default system settings to set on new computers. Example: \"shell.autocomplete=false,lua.autocomplete=false,edit.autocomplete=false\" will disable all autocompletion" );

        Config.logPeripheralErrors = Config.config.get( Configuration.CATEGORY_GENERAL, "logPeripheralErrors", logPeripheralErrors );
        Config.logPeripheralErrors.setComment( "Log exceptions thrown by peripherals and other Lua objects.\n" +
            "This makes it easier for mod authors to debug problems, but may result in log spam should people use buggy methods." );
        
        Config.enableCommandBlock = Config.config.get( Configuration.CATEGORY_GENERAL, "enableCommandBlock", enableCommandBlock );
        Config.enableCommandBlock.setComment( "Enable Command Block peripheral support" );

        Config.modem_range = Config.config.get( Configuration.CATEGORY_GENERAL, "modem_range", modem_range );
        Config.modem_range.setComment( "The range of Wireless Modems at low altitude in clear weather, in meters" );

        Config.modem_highAltitudeRange = Config.config.get( Configuration.CATEGORY_GENERAL, "modem_highAltitudeRange", modem_highAltitudeRange );
        Config.modem_highAltitudeRange.setComment( "The range of Wireless Modems at maximum altitude in clear weather, in meters" );

        Config.modem_rangeDuringStorm = Config.config.get( Configuration.CATEGORY_GENERAL, "modem_rangeDuringStorm", modem_rangeDuringStorm );
        Config.modem_rangeDuringStorm.setComment( "The range of Wireless Modems at low altitude in stormy weather, in meters" );

        Config.modem_highAltitudeRangeDuringStorm = Config.config.get( Configuration.CATEGORY_GENERAL, "modem_highAltitudeRangeDuringStorm", modem_highAltitudeRangeDuringStorm );
        Config.modem_highAltitudeRangeDuringStorm.setComment( "The range of Wireless Modems at maximum altitude in stormy weather, in meters" );

        Config.computerSpaceLimit = Config.config.get( Configuration.CATEGORY_GENERAL, "computerSpaceLimit", computerSpaceLimit );
        Config.computerSpaceLimit.setComment( "The disk space limit for computers and turtles, in bytes" );

        Config.floppySpaceLimit = Config.config.get( Configuration.CATEGORY_GENERAL, "floppySpaceLimit", floppySpaceLimit );
        Config.floppySpaceLimit.setComment( "The disk space limit for floppy disks, in bytes" );

        Config.turtlesNeedFuel = Config.config.get( Configuration.CATEGORY_GENERAL, "turtlesNeedFuel", turtlesNeedFuel );
        Config.turtlesNeedFuel.setComment( "Set whether Turtles require fuel to move" );

        Config.maximumFilesOpen = Config.config.get(Configuration.CATEGORY_GENERAL, "maximumFilesOpen", maximumFilesOpen);
        Config.maximumFilesOpen.setComment( "Set how many files a computer can have open at the same time. Set to 0 for unlimited." );

        Config.turtleFuelLimit = Config.config.get( Configuration.CATEGORY_GENERAL, "turtleFuelLimit", turtleFuelLimit );
        Config.turtleFuelLimit.setComment( "The fuel limit for Turtles" );

        Config.advancedTurtleFuelLimit = Config.config.get( Configuration.CATEGORY_GENERAL, "advancedTurtleFuelLimit", advancedTurtleFuelLimit );
        Config.advancedTurtleFuelLimit.setComment( "The fuel limit for Advanced Turtles" );

        Config.turtlesObeyBlockProtection = Config.config.get( Configuration.CATEGORY_GENERAL, "turtlesObeyBlockProtection", turtlesObeyBlockProtection );
        Config.turtlesObeyBlockProtection.setComment( "If set to true, Turtles will be unable to build, dig, or enter protected areas (such as near the server spawn point)" );

        Config.turtlesCanPush = Config.config.get( Configuration.CATEGORY_GENERAL, "turtlesCanPush", turtlesCanPush );
        Config.turtlesCanPush.setComment( "If set to true, Turtles will push entities out of the way instead of stopping if there is space to do so" );

        Config.maxNotesPerTick = Config.config.get( Configuration.CATEGORY_GENERAL, "maxNotesPerTick", maxNotesPerTick );
        Config.maxNotesPerTick.setComment( "Maximum amount of notes a speaker can play at once" );

        for (Property property : Config.config.getCategory( Configuration.CATEGORY_GENERAL ).getOrderedValues())
        {
            property.setLanguageKey( "gui.computercraft:config." + CaseFormat.LOWER_CAMEL.to( CaseFormat.LOWER_UNDERSCORE, property.getName() ) );
        }

        syncConfig();
*/
        // Setup network
        networkEventChannel = new NetworkManager();
        //proxy.preInit();
        //turtleProxy.preInit();*/
    }

    public static void syncConfig() {
        http_enable = config.http_enable;
        http_whitelist = new AddressPredicate(config.http_whitelist);
        http_blacklist = new AddressPredicate(config.http_blacklist);
        disable_lua51_features = config.disable_lua51_features;
        default_computer_settings = config.default_computer_settings;
        logPeripheralErrors = config.logPeripheralErrors;

        computerSpaceLimit = config.computerSpaceLimit;
        maximumFilesOpen = config.maximumFilesOpen;
        config.serialize(getWorldDir().toString() + "/config.ser");
    }

    //@Mod.EventHandler
    public void init( /*FMLInitializationEvent event*/ )
    {
        //proxy.init();
        //turtleProxy.init();
    }

    //@Mod.EventHandler
    public void onServerStarting( /*FMLServerStartingEvent event*/ )
    {
    }

    //@Mod.EventHandler
    public void onServerStart( /*FMLServerStartedEvent event*/ )
    {
        //if( FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER )
        {
           // ComputerCraft.serverComputerRegistry.reset();
            
        }
    }

    //@Mod.EventHandler
    public void onServerStopped( /*FMLServerStoppedEvent event*/ )
    {
        //if( FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER )
        {
            //ComputerCraft.serverComputerRegistry.reset();
            
        }
    }

    public static String getVersion()
    {
        return "${version}";
    }
/*
    public static boolean isClient()
    {
        return proxy.isClient();
    }

    public static boolean getGlobalCursorBlink()
    {
        return proxy.getGlobalCursorBlink();
    }

    public static long getRenderFrame()
    {
        return proxy.getRenderFrame();
    }

    public static void deleteDisplayLists( int list, int range )
    {
        proxy.deleteDisplayLists( list, range );
    }

    public static Object getFixedWidthFontRenderer()
    {
        return proxy.getFixedWidthFontRenderer();
    }

    public static void playRecord( SoundEvent record, String recordInfo, World world, BlockPos pos )
    {
        proxy.playRecord( record, recordInfo, world, pos );
    }

    public static String getRecordInfo( @Nonnull ItemStack recordStack )
    {
        return proxy.getRecordInfo( recordStack );
    }

    public static void openDiskDriveGUI( EntityPlayer player, TileDiskDrive drive )
    {
        BlockPos pos = drive.getPos();
        player.openGui( ComputerCraft.instance, ComputerCraft.diskDriveGUIID, player.getEntityWorld(), pos.getX(), pos.getY(), pos.getZ() );
    }

    public static void openComputerGUI( EntityPlayer player, TileComputer computer )
    {
        BlockPos pos = computer.getPos();
        player.openGui( ComputerCraft.instance, ComputerCraft.computerGUIID, player.getEntityWorld(), pos.getX(), pos.getY(), pos.getZ() );
    }

    public static void openPrinterGUI( EntityPlayer player, TilePrinter printer )
    {
        BlockPos pos = printer.getPos();
        player.openGui( ComputerCraft.instance, ComputerCraft.printerGUIID, player.getEntityWorld(), pos.getX(), pos.getY(), pos.getZ() );
    }

    public static void openTurtleGUI( EntityPlayer player, TileTurtle turtle )
    {
        BlockPos pos = turtle.getPos();
        player.openGui( instance, ComputerCraft.turtleGUIID, player.getEntityWorld(), pos.getX(), pos.getY(), pos.getZ() );
    }

    public static void openPrintoutGUI( EntityPlayer player, EnumHand hand )
    {
        player.openGui( ComputerCraft.instance, ComputerCraft.printoutGUIID, player.getEntityWorld(), hand.ordinal(), 0, 0 );
    }

    public static void openPocketComputerGUI( EntityPlayer player, EnumHand hand )
    {
        player.openGui( ComputerCraft.instance, ComputerCraft.pocketComputerGUIID, player.getEntityWorld(), hand.ordinal(), 0, 0 );
    }
*/
    public static File getBaseDir()
    {
        return new File(System.getProperty("user.home").concat("/.craftos"));
    }

    public static File getResourcePackDir()
    {
        return new File( getBaseDir(), "resourcepacks" );
    }

    public static File getWorldDir()
    {
        return new File(System.getProperty("user.home").concat("/.craftos"));
    }

    public static void sendToPlayer( int player, ComputerCraftPacket packet )
    {
        networkEventChannel.sendTo( packet, player );
    }

    public static void sendToAllPlayers( ComputerCraftPacket packet )
    {
        networkEventChannel.sendToAll( packet );
    }

    public static void sendToServer( ComputerCraftPacket packet )
    {
        networkEventChannel.sendToServer( packet );
    }

    /*public static void handlePacket( ComputerCraftPacket packet, EntityPlayer player )
    {
        proxy.handlePacket( packet, player );
    }

/*
    public static void registerPocketUpgrade( IPocketUpgrade upgrade )
    {
        String id = upgrade.getUpgradeID().toString();
        IPocketUpgrade existing = pocketUpgrades.get( id );
        if( existing != null )
        {
            throw new RuntimeException( "Error registering '" + upgrade.getUnlocalisedAdjective() + " pocket computer'. UpgradeID '" + id + "' is already registered by '" + existing.getUnlocalisedAdjective() + " pocket computer'" );
        }

        pocketUpgrades.put( id, upgrade );
    }

    public static void registerPeripheralProvider( IPeripheralProvider provider )
    {
        if( provider != null && !peripheralProviders.contains( provider ) )
        {
            peripheralProviders.add( provider );
        }
    }

    public static void registerBundledRedstoneProvider( IBundledRedstoneProvider provider )
    {
        if( provider != null && !bundledRedstoneProviders.contains( provider ) )
        {
            bundledRedstoneProviders.add( provider );
        }
    }

    public static void registerMediaProvider( IMediaProvider provider )
    {
        if( provider != null && !mediaProviders.contains( provider ) )
        {
            mediaProviders.add( provider );
        }
    }*/
    /*
    public static IPeripheral getPeripheralAt( World world, BlockPos pos, EnumFacing side )
    {
        // Try the handlers in order:
        for( IPeripheralProvider peripheralProvider : peripheralProviders )
        {
            try
            {
                IPeripheral peripheral = peripheralProvider.getPeripheral( world, pos, side );
                if( peripheral != null )
                {
                    return peripheral;
                }
            }
            catch( Exception e )
            {
                ComputerCraft.log.error( "Peripheral provider " + peripheralProvider + " errored.", e );
            }
        }
        return null;
    }

    public static int getDefaultBundledRedstoneOutput( World world, BlockPos pos, EnumFacing side )
    {
        if( WorldUtil.isBlockInWorld( world, pos ) )
        {
            return DefaultBundledRedstoneProvider.getDefaultBundledRedstoneOutput( world, pos, side );
        }
        return -1;
    }

    public static int getBundledRedstoneOutput( World world, BlockPos pos, EnumFacing side )
    {
        int y = pos.getY();
        if( y < 0 || y >= world.getHeight() )
        {
            return -1;
        }

        // Try the handlers in order:
        int combinedSignal = -1;
        for( IBundledRedstoneProvider bundledRedstoneProvider : bundledRedstoneProviders )
        {
            try
            {
                int signal = bundledRedstoneProvider.getBundledRedstoneOutput( world, pos, side );
                if( signal >= 0 )
                {
                    if( combinedSignal < 0 )
                    {
                        combinedSignal = (signal & 0xffff);
                    }
                    else
                    {
                        combinedSignal = combinedSignal | (signal & 0xffff);
                    }
                }
            }
            catch( Exception e )
            {
                ComputerCraft.log.error( "Bundled redstone provider " + bundledRedstoneProvider + " errored.", e );
            }
        }
        return combinedSignal;
    }

    public static IMedia getMedia( @Nonnull ItemStack stack )
    {
        if( !stack.isEmpty() )
        {
            // Try the handlers in order:
            for( IMediaProvider mediaProvider : mediaProviders )
            {
                try
                {
                    IMedia media = mediaProvider.getMedia( stack );
                    if( media != null )
                    {
                        return media;
                    }
                }
                catch( Exception e )
                {
                    // mod misbehaved, ignore it
                    ComputerCraft.log.error( "Media provider " + mediaProvider + " errored.", e );
                }
            }
            return null;
        }
        return null;
    }

    public static IPocketUpgrade getPocketUpgrade(String id) {
        return pocketUpgrades.get( id );
    }

    public static IPocketUpgrade getPocketUpgrade( @Nonnull ItemStack stack )
    {
        if( stack.isEmpty() ) return null;

        for (IPocketUpgrade upgrade : pocketUpgrades.values())
        {
            ItemStack craftingStack = upgrade.getCraftingItem();
            if( !craftingStack.isEmpty() && InventoryUtil.areItemsStackable( stack, craftingStack ) )
            {
                return upgrade;
            }
        }

        return null;
    }

    public static Iterable<IPocketUpgrade> getVanillaPocketUpgrades() {
        List<IPocketUpgrade> upgrades = new ArrayList<>();
        for(IPocketUpgrade upgrade : pocketUpgrades.values()) {
            if(upgrade instanceof PocketModem || upgrade instanceof PocketSpeaker) {
                upgrades.add( upgrade );
            }
        }

        return upgrades;
    }*/

    public static int createUniqueNumberedSaveDir( String parentSubPath )
    {
        return IDAssigner.getNextIDFromDirectory(new File(getWorldDir(), parentSubPath));
    }

    public static IWritableMount createSaveDirMount( String subPath, long capacity )
    {
        try
        {
            return new FileMount( new File( getWorldDir(), subPath ), capacity );
        }
        catch( Exception e )
        {
            return null;
        }
    }

    public static IMount createResourceMount( Class<?> modClass, String domain, String subPath )
    {
        // Start building list of mounts
        List<IMount> mounts = new ArrayList<>();
        subPath = "assets/" + domain + "/" + subPath;

        // Mount from debug dir
        File codeDir = getDebugCodeDir( modClass );
        if( codeDir != null )
        {
            File subResource = new File( codeDir, subPath );
            if( subResource.exists() )
            {
                IMount resourcePackMount = new FileMount( subResource, 0 );
                mounts.add( resourcePackMount );
            }
        }

        // Mount from mod jar
        File modJar = getContainingJar( modClass );
        if( modJar != null )
        {
            try
            {
                IMount jarMount = new JarMount( modJar, subPath );
                mounts.add( jarMount );
            }
            catch( IOException e )
            {
                // Ignore
            }
        }

        // Mount from resource packs
        File resourcePackDir = getResourcePackDir();
        if( resourcePackDir.exists() && resourcePackDir.isDirectory() )
        {
            String[] resourcePacks = resourcePackDir.list();
            for( String resourcePack1 : resourcePacks )
            {
                try
                {
                    File resourcePack = new File( resourcePackDir, resourcePack1 );
                    if( !resourcePack.isDirectory() )
                    {
                        // Mount a resource pack from a jar
                        IMount resourcePackMount = new JarMount( resourcePack, subPath );
                        mounts.add( resourcePackMount );
                    }
                    else
                    {
                        // Mount a resource pack from a folder
                        File subResource = new File( resourcePack, subPath );
                        if( subResource.exists() )
                        {
                            IMount resourcePackMount = new FileMount( subResource, 0 );
                            mounts.add( resourcePackMount );
                        }
                    }
                }
                catch( IOException e )
                {
                    // Ignore
                }
            }
        }

        // Return the combination of all the mounts found
        if( mounts.size() >= 2 )
        {
            IMount[] mountArray = new IMount[ mounts.size() ];
            mounts.toArray( mountArray );
            return new ComboMount( mountArray );
        }
        else if( mounts.size() == 1 )
        {
            return mounts.get( 0 );
        }
        else
        {
            return null;
        }
    }

    public static InputStream getResourceFile( Class<?> modClass, String domain, String subPath )
    {
        // Start searching in possible locations
        subPath = "assets/" + domain + "/" + subPath;

        // Look in resource packs
        File resourcePackDir = getResourcePackDir();
        if( resourcePackDir.exists() && resourcePackDir.isDirectory() )
        {
            String[] resourcePacks = resourcePackDir.list();
            for( String resourcePackPath : resourcePacks )
            {
                File resourcePack = new File( resourcePackDir, resourcePackPath );
                if( resourcePack.isDirectory() )
                {
                    // Mount a resource pack from a folder
                    File subResource = new File( resourcePack, subPath );
                    if( subResource.exists() && subResource.isFile() )
                    {
                        try
                        {
                            return new FileInputStream( subResource );
                        }
                        catch( FileNotFoundException ignored )
                        {
                        }
                    }
                }
                else
                {
                    ZipFile zipFile = null;
                    try
                    {
                        final ZipFile zip = zipFile = new ZipFile( resourcePack );
                        ZipEntry entry = zipFile.getEntry( subPath );
                        if( entry != null )
                        {
                            // Return a custom InputStream which will close the original zip when finished.
                            return new FilterInputStream( zipFile.getInputStream( entry ) )
                            {
                                @Override
                                public void close() throws IOException
                                {
                                    super.close();
                                    zip.close();
                                }
                            };
                        }
                        else
                        {
                            //IOUtils.closeQuietly( zipFile );
                            zipFile.close();
                        }
                    }
                    catch( IOException e )
                    {
                        if( zipFile != null ) {
                            try {
                                zipFile.close(); //IOUtils.closeQuietly( zipFile );
                            } catch (IOException ex) {
                                // oh well
                            }
                        }
                    }
                }
            }
        }

        // Look in debug dir
        File codeDir = getDebugCodeDir( modClass );
        if( codeDir != null )
        {
            File subResource = new File( codeDir, subPath );
            if( subResource.exists() && subResource.isFile() )
            {
                try
                {
                    return new FileInputStream( subResource );
                }
                catch( FileNotFoundException ignored )
                {
                }
            }
        }

        // Look in class loader
        return modClass.getClassLoader().getResourceAsStream( subPath );
    }

    private static File getContainingJar( Class<?> modClass )
    {
        String path = modClass.getProtectionDomain().getCodeSource().getLocation().getPath();
        int bangIndex = path.indexOf( "!" );
        if( bangIndex >= 0 )
        {
            path = path.substring( 0, bangIndex );
        }

        URL url;
        try {
            url = new URL( path );
        } catch (MalformedURLException e1) {
            return null;
        }

        File file;
        try {
            file = new File( url.toURI() );
        } catch(URISyntaxException e) {
            file = new File( url.getPath() );
        }
        return file;
    }

    private static File getDebugCodeDir( Class<?> modClass )
    {
        String path = modClass.getProtectionDomain().getCodeSource().getLocation().getPath();
        int bangIndex = path.indexOf("!");
        if( bangIndex >= 0 )
        {
            return null;
        }
        return new File( new File( path ).getParentFile(), "../.." );
    }
/*
    public static void registerTurtleUpgrade( ITurtleUpgrade upgrade )
    {
        turtleProxy.registerTurtleUpgrade( upgrade );
    }

    public static ITurtleUpgrade getTurtleUpgrade( String id )
    {
        return turtleProxy.getTurtleUpgrade( id );
    }

    public static ITurtleUpgrade getTurtleUpgrade( int legacyID )
    {
        return turtleProxy.getTurtleUpgrade( legacyID );
    }

    public static ITurtleUpgrade getTurtleUpgrade( @Nonnull ItemStack item )
    {
        return turtleProxy.getTurtleUpgrade( item );
    }

    public static void addAllUpgradedTurtles( NonNullList<ItemStack> list )
    {
        turtleProxy.addAllUpgradedTurtles( list );
    }

    public static void setEntityDropConsumer( Entity entity, IEntityDropConsumer consumer )
    {
        turtleProxy.setEntityDropConsumer( entity, consumer );
    }

    public static void clearEntityDropConsumer( Entity entity )
    {
        turtleProxy.clearEntityDropConsumer( entity );
    }
    */
}
