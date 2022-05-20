# 棱镜 (折射) - 中文版本

**Prism (Refracted)** 是一个可以追踪 Minecraft 世界变化的 Bukkit 插件. 这些变化可以被查询, 回滚, 还原, 或被执行其它操作.
因为追踪的效果太好, NSA 偷走了我们的名字.
中文版可下载于 [Mcbbs 资源站][mcbbsv4]
英文版可下载于 [Spigot][spigot]

# v3 vs v4

v4 是现在进行中的一次彻底的重写. 我没有预计的发布时间，但请随时对这个项目贡献.
请查看 v4 分支. v3 是提供改进/维护工作的旧版 Prism.

# 简述

我, viveleroi, 在 10 年前制作了 Prism, 并对社区的反应感到惊讶. 因为 2014 年 bukkit/mojang 项目的戏剧性, 
我逐渐丧失了对 Minecraft 的兴趣, 转向了 Sponge 项目, 但最终停止了全部的开发.

在 2016 年, 我把这个项目转让给了另一个团队, 但长话短说, 因为我看到游戏和社区已经成熟,
并了解到有这么多 Prism 用户长期使用它, 我决定回到 Prism 的开发之中.

"Prism Refracted" 是 Prism 的我的官方延续版本. 我还有许多东西要做.

## 支持

请耐心等待, 我还需要做很多事情来达到我的目标, 创建文档, 构建服务器, 等.

- [**QQ群**][qq] - 如果您有任何问题需要帮助, 请加入中文版的插件讨论群.
- [**Discord**(英文版)][discord] - 如果使用英文版, 在创建议题前, 请在 Discord 中寻求帮助.

# 粗略的路线图

插件中的很多代码已经有接近 10 年的历史了. 有些东西真的可以做得更好:

- 转换命令处理为注释命令框架
- 替换所有数据库增删改查操作为我们在 Sponge 版 Prism 中做的那样. 允许使用 mongoDB.
- 修复非标准 SQL 的问题, 重新评估效率.
- 改进 Hikari 配置的使用方式.
- 正确检查样式并根据我的喜好修复格式.
- 带回 Prism WebUI.
- 考虑将矿石警告, 以及其他的东西移至其他的插件.

## 许可证

Prism 根据 Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported 提供知识产权许可.
请查阅 `LICENSE` 或者 [这个网站][license] 来了解完整的许可证.

### 鸣谢

- 最初的作者, 以及重制作者, 为了 `play.darkhelmet.network` 服务器.
- 最近 [addstarmc][addstarmc] 团队的管理.
- LegendarySoldier 的美术.


[license]: http://creativecommons.org/licenses/by-nc-sa/3.0/us/
[addstarmc]: https://github.com/AddstarMC
[discord]: https://discord.gg/7FxZScH4EJ
[qq]: https://qm.qq.com/cgi-bin/qm/qr?k=mDtcrvBGzqbA05mPLzBnPAYXm5lskYxg&jump_from=webapi
[spigot]: https://www.spigotmc.org/resources/prism-refracted.99397/
[mcbbsv4]: https://beta.mcbbs.net/resource/qcnedi83
