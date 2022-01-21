**Prism (Refracted)** is a change-tracking plugin for Bukkit-based servers. Supports rollbacks, restores, previews, 
wands, and so much more. Tracking so good, the NSA stole our name.

# TL;DR

I, viveleroi, created Prism ten years ago and was amazed by the community response. With the bukkit/mojang project
drama in 2014 and my waning interest in Minecraft, I moved to the Sponge project but eventually stopped all development.

In 2016 I turned the project over to another team but long story short, as I saw the game and community mature, 
and heard from so many long-time Prism users, I've decided to return to Prism.

"Prism Refracted" is my official continuation of Prism. There's a lot of work I want to do

## Support

Please be patient as I work to get this project where I want, establish documentation, build servers, etc. 

- [**Discord**][discord] - Please visit discord for help before opening issues.

# Rough Roadmap

A lot of this code is nearing a decade old. Some things really can be better:

- Convert command handling to ACF
- Replace all database crud with a Storage approach like we did in Prism for Sponge. Allow mongoDB.
- Fix non-standard SQL issues, re-evaluate efficiency stuff.
- Improve how hikari configs are used.
- Properly checkstyle and fix formatting to my tastes
- Bring back Prism WebUI
- Consider moving ore alerts, etc to a separate plugin

## License

Prism is licensed the under the Creative Commons
Attribution-NonCommercial-ShareAlike 3.0 Unported. Please see `LICENSE` or [this website][license]
for the full license.

### Credits

- Originally made, and made again, for the `play.darkhelmet.network` server.
- Recent stewardship by the [addstarmc][[addstarmc]] team.
- Artwork by LegendarySoldier.


[license]: http://creativecommons.org/licenses/by-nc-sa/3.0/us/
[addstarmc]: https://github.com/AddstarMC
[discord]: https://discord.gg/7FxZScH4EJ
