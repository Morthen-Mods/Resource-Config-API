#### 5.0.0
> Changelog generated automatically

- **breaking:** the config API was rewritten and moved to new packages
  - the annotations `@Config`, `@ConfigEntry` and `@RangedEntry` moved from `net.morthen.resourceconfigapi.annotations` to `net.morthen.resourceconfigapi.api.annotation`
  - `ConfigRegistry.register(...)` now returns a `ConfigFile` and throws if the id is already registered or the class has no `@Config`
  - `ModConfig` and `ConfigHolder` were removed, use `ConfigFile` and `ConfigOption` instead (`ConfigRegistry.get(...)`, `getAll()`, `getConfigs(modId)`)
  - `ConfigRegistry.getConfig(...)`, `contains(...)`, `getConfigs()` and `getConfigEntries()` were removed, use `ConfigRegistry.get(...)` and `getAll()` instead
- **breaking:** `ConfigType.SERVER` was removed, use `ConfigType.COMMON` instead
  - config files of the former `SERVER` type are now stored in the `common` folder
- **breaking:** `fileName` of `@Config` and `category` / `translation` of `@ConfigEntry` may now only contain lowercase letters, digits, `_`, `-` and `.`
  - translation keys follow `config.{modId}.{fileName}.{category}.{field}.label` / `.tooltip`, category labels use `config.{modId}.{fileName}.{category}.label`
- **breaking:** `@ConfigEntry` fields must be `public static` and not final, otherwise they are skipped
  - collections, maps and arrays are not supported
- **breaking:** the old network payloads `ConfigUpdatePayload` and the string based `ConfigSyncPayload` were replaced by a single `ConfigSyncPayload`
  - `ConfigConstants.LOG` was renamed to `ConfigConstants.LOGGER`
- adding `@ColorEntry` for `int` options
  - the config screen shows a color picker, the file stores a hex string (`#RRGGBB`, or `#AARRGGBB` with `alpha = true`)
- adding `ConfigOption` accessors for values, defaults, `range()`, `restartRequirement()` and `colorMode()`
  - `ConfigOption.set(...)` converts numbers to the field type and clamps them to the `@RangedEntry` range
  - `ConfigFile.save()` and `ConfigFile.reload()` write and re-read the config file
- adding a rewritten config screen with a sidebar of config files, search, undo / reset and a discard confirmation
  - ranged numbers are shown as sliders, booleans as toggles, enums as dropdowns
  - options needing a game or world restart are marked with an icon and tooltip
- adding selectable UI themes (`Vanilla` and `Modern`) that can be added or changed by resource packs via `assets/resourceconfigapi/themes/<id>.json`
  - the selected theme is remembered between launches
- adding syncing of `COMMON` configs from the server to clients on join
  - operators can edit `COMMON` configs in-game, the server applies, saves and broadcasts the changes; other players see them read-only
  - the server values are only kept in memory on the client and reset on disconnect
- changing config files to be written atomically and to drop unknown keys, invalid or missing values fall back to the defaults and out-of-range numbers are clamped
- changing the Mod Menu integration on Fabric to show a config screen for every mod registered via `ConfigRegistry`

#### 4.1.2
- port to 26.3

#### 4.1.1
- port to 26.2

#### 4.0.0
- Port to 26.1.2
- remove forge for now
- remove config screens

#### 3.9.1
- fix broken slider

#### 3.9.0
- updating to 1.21.11
- updating internals

#### 3.8.0
- updating to 1.21.9
- config file list has a rendering issue
    - will not be fixed -> new UI is in the making

#### 3.7.3
- Fix Networking issues

#### 3.7.2
- update to 1.21.7
  - works also on 1.21.6

#### 3.7.1
- make networking stuff optional, allows to join vanilla servers

#### 3.7.0
- port to 1.21.6
- There are known issues:
  - Tooltip clips behind next Config Entry Label when the tooltip is too tall.

#### 3.6.3
- don't rely on latest neo/forge loader version

#### 3.6.2
- fixing check for RangedEntries

#### 3.6.1
  - fix crash when local language was Turkish

### 3.6.0
  - update to 1.21.5

#### 3.5.2
- fix network issue for all loader

#### 3.5.1
- fix client crash due to wrongly set mixin file

#### 3.5.0
- adding the option for config saving at runtime
  - useful when values get edited with commands
- complete Network overhaul
- fix incorrect Config Screen display when player was set to op
  - previously it was needed to rejoin the server
- general internal cleanup

#### 3.4.2
- fixing an issue where undo button was falsely active
- fixing logging for missing translation keys
  - outputs the usable translation keys for your config

#### v3.4.1
- adding Indicator for Config Option if a world/game restart is necessary
- adding more translations

#### v3.4.0
- complete overhaul of the config api
- using json files instead of toml files
  - this will reset all configurations you made with the old versions
- adding Config Menu to edit the configs without the need of editing files
  - in Singleplayer worlds you can edit Client and Common Configs
  - in Multiplayer, Common and Server Configs are editable, but only if you are an Operator
    when not only the Client Config is editable
- Configs get synced when you enter a World/Server and when a Server Operator
  edits the config it also gets synced with all clients again

#### v2.1.3
- fixing an issue with the dependencies for neoforge

#### v2.1.2
- fixing the accepted Minecraft versions for Fabric version

#### v2.1.1
- Config Values can be tagged to be Synced between Server and Client
  - Lists aren't currently supported

#### v2.0.0
- update release for Minecraft 1.21

#### v1.2.0
- updating NightConfig to 3.7.2
  - this only applies to Fabric, Neo-/Forge ship Night Config on its own

#### v1.1.0
- backport update
- Fabric 1.20 up to 1.20.4
- Forge 1.20 up to 1.20.4
- NeoForge 1.20.4
- All versions are bundled under 1.20 since it isn't using any Loader specific code

#### v1.0.1
- fixing neoforge mods.toml

#### v1.0.0 - excluding Forge for now
 - initial release
 - basically Simple-Config-API but rebranded to fit my other Mods
 - some more Datatypes like Lists