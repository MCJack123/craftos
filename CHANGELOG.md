# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [ 1.2.2 ] - 2019-05-24
### Added
- Holding Ctrl-T terminates the running program
- Unused resized terminal space will now be the same color as the background
### Changed
- Left/right modifier keys are now discriminated in key events
- Shift key presses now send key events
- Pressing alt no longer loses focus
### Fixed
- `key_up` events now send the correct key when pressing modifier keys

## [ 1.2.1 ] - 2019-04-20
### Added
- There is now a `showFPS` config option to enable an FPS display in the top-left corner
### Changed
- Config changes are now saved immediately instead of on shutdown, which was unreliable
- Reduced the complexity of the rendering backend, hopefully making things a bit smoother
- Old config files will now be replaced on the next save
### Fixed
- `os.reboot` no longer shuts down the computer entirely

## [ 1.2.0 ] - 2019-04-04
### Added
- `attach`/`detach` programs for creating and removing peripherals
- New icons for Windows (and maybe Linux?)
- Scaling on monitors now works properly
### Fixed
- Rewrote font renderer to not use images (fixes #2, #3)
- Cursor no longer disappears when resizing terminals (#4)
- `term` no longer fails to redirect to a monitor (#5)

## [ 1.2.0-rc1 ] - 2019-04-01
### Added
- Terminal and monitors are now resizeable
- HTTP server listener
### Changed
- 127.0.0.0/8 IP range is no longer blacklisted
### Fixed
- Configuration now does stuff

## [ 1.2.0-b1 ] - 2019-02-25
### Added
- New graphics mode allowing individual pixel addressing
- New `font` API
- `gfxpaint` program: Paint demo for graphics mode
- `raycast` program: Raycasting demo
- `bmpview` program: Views `ccbmp` images (see `/rom/programs/fun/advanced/images`)

## [ 1.1.0 ] - 2018-11-21
### Fixed
- Mouse actions now respect new window borders
- `term.setPaletteColor` works properly
- Fixed HTTP requests

## [ 1.1.0-b1 ] - 2018-10-16
### Added
- Peripheral emulation (`periphemu` API)
- `config` API & tool
- `mounter` API
- `mount` and `unmount` commands
### Changed
- Margins are now present at the edges
### Fixed
- Mouse drag events now send the proper mouse buttons (#1)
- General performance and stability improvements

## [ 1.0.0 ] - 2018-10-14
### Added
- First release
- Compatible with CraftOS 1.8