**Prism (Refracted)** is a change-tracking plugin for Bukkit-based servers. Supports rollbacks, restores, previews, 
wands, and so much more. Tracking so good, the NSA stole our name.

## DEV NOTICE

v4 is currently in development and not suitable for production use. Stuff WILL change.

## Support

Please be patient as I work to get this project where I want, establish documentation, build servers, etc. 

- [**Discord**][discord] - Please visit discord for help before opening issues.

## Updating from v2/v3

**Database**

Prism can automatically convert your database schema to the new format. However, for large
databases this may take time. This should definitely be done when no players are online.

However, because v4 has a drastically improved database than older versions we recommend
starting fresh, because:

- v4's block state/item/entity serialization process is far better than v3, however this means
it's also incompatible. Lookups will generally work but rollbacks will generally not. (Invalid 
data will be safely skipped.)
- v3 tracked non-player "causes" as fake players. v4 can't separate those when migrating data.

**Configs**

Prism 4 uses a different configuration format/structure. Options may not be 1:1 with new
configurations so please ask in discord if you need help.

# Rough Roadmap

Prism 2 is a relic from the ~2014 Bukkit era. There's so much that could be done better, so we're 
starting fresh with Prism for Bukkit v3.

- Convert command handling to better command lib
- Replace all database crud with a Storage approach like we did in Prism for Sponge. Allow mongoDB.
- Fix non-standard SQL issues, re-evaluate databases/efficiency stuff.
- Improve how hikari configs are used.
- Bring back Prism WebUI
- Move ore alerts, etc to a separate plugin

## License

Prism is licensed the under the Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported.
Please see `LICENSE` or [this website][license] for the full license.

### Credits

- Originally made, and made again, for the `play.darkhelmet.network` server.
- Artwork by LegendarySoldier.

[license]: http://creativecommons.org/licenses/by-nc-sa/3.0/us/
[discord]: https://discord.gg/7FxZScH4EJ
