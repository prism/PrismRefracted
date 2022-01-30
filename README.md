**Prism (Refracted)** is a change-tracking plugin for Bukkit-based servers. Supports rollbacks, restores, previews, 
wands, and so much more. Tracking so good, the NSA stole our name.

## DEV NOTICE

v4 is currently in development and not suitable for production use. Stuff WILL change.

## Support

Please be patient as I work to get this project where I want, establish documentation, build servers, etc. 

- [**Discord**][discord] - Please visit discord for help before opening issues.

## Requirements

- Spigot, although we strongly advise using Paper or Purpur.
- MySQL 8+ or MariaDB 10.2+

## Updating from v2/v3

**Database**

Prism can automatically convert your database schema to the new format. For large
databases this may take quite some time. This should definitely be done when no players are online.

However, there are several factors we can't account for during conversions, so I highly advise
starting fresh:

- v4's block state/item/entity serialization process is far better than v3, however this means
it's also incompatible. Lookups will generally work for blocks/items but rollbacks will generally not. (Invalid 
data will be safely skipped.) Entity data is pretty much in accessible.
- v3 tracked non-player "causes" as fake players. v4 can't separate those when migrating data.

**Configs**

Prism 4 uses a different configuration format/structure. Options may not be 1:1 with new
configurations so please ask in discord if you need help.

### Credits

- Originally made, and made again, for the `play.darkhelmet.network` server.
- Artwork by LegendarySoldier.

[discord]: https://discord.gg/7FxZScH4EJ
