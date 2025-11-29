# NoteBlockAPI
[![](https://jitpack.io/v/koca2000/NoteBlockAPI.svg)](https://jitpack.io/#koca2000/NoteBlockAPI) [![Build Status](http://ci.haprosgames.com/buildStatus/icon?job=NoteBlockAPI)](http://ci.haprosgames.com/job/NoteBlockAPI)

For information about this Spigot/Bukkit API, go to https://www.spigotmc.org/resources/noteblockapi.19287/

Dev builds are available at [Jenkins](http://ci.haprosgames.com/job/NoteBlockAPI/ "Jenkins")

## Custom instrument assets

To keep the repository free of large binary files, the bundled `Instruments.zip` and
`InstrumentsBE.mcpack` archives are not tracked. On startup the plugin will now
download the latest versions of these packs from the upstream
[NoteBlockAPI releases](https://github.com/koca2000/NoteBlockAPI/releases) into the
plugin data folder if they are missing, preserving the original automatic instrument
selection behavior without committing binaries.
