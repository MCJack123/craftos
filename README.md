# CraftOS-PC Classic
## Disclaimer: CraftOS-PC 2
I am currently working on [CraftOS-PC 2](https://github.com/MCJack123/craftos2), which is a complete rewrite of CraftOS-PC in C/C++ designed for speed. CraftOS-PC Classic is no longer in development, and is archived. If you want to have the latest features (including CC: Tweaked support) with fast speeds, please switch to CraftOS-PC 2. (Switching will not cause data loss as the save files will automatically be migrated.) CraftOS-PC Classic will still be available for users who wish to stay on a stable and fully compliant version of CraftOS.

-----------
CraftOS-PC is a port of the CraftOS operating system from the popular Minecraft mod [ComputerCraft](https://github.com/dan200/ComputerCraft). This is a mostly full port of the mod to desktop, removing the parts that only work in a Minecraft world.  

![Screenshot](image.png)
![Screenshot](image2.png)
![Screenshot](image3.png)

## What works
* Full terminal interaction (mouse/keyboard)
* Color support
* Most programs written for ComputerCraft should work out-of-the-box
* HTTP requests
* Peripherals (printers, speakers, monitors)
* Platform-independent (works on all systems with Java 8)

## New features in CraftOS-PC
* Shared mounts from real PC
* Pixel-based graphics mode
* Resizeable terminals
* HTTP server functionality

## What doesn't work
* Networking/multi-computer support

## Usage
Just download and run the JAR or EXE file available in the releases and the terminal will automatically start.

## Files
ComputerCraft's root directory is located at `<your home directory>/.craftos/computer/0`. You can place files here, and they will appear in the root directory of the computer.  
You can also mount real directories with the `mount` command: `mount /CDrive C:\ `, `mount /Documents /home/user/Documents`.

## License
Since this is based on ComputerCraft it follows the same license as it does. You can read it [here](https://github.com/dan200/ComputerCraft/blob/master/LICENSE) as well as in LICENSE in this repository.
