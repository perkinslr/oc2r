# 声音卡
![少一点寂静的声音](item:oc2:sound_card)

声音卡允许从其广泛的逼真样本库中播放各种声音效果。由于内部工程限制，播放连续效果时需要短暂的暂停。在此时间窗口内尝试播放其他效果将没有效果。

这是一个高级设备。必须使用高级设备 API 进行控制。默认的 Linux 发行版提供了该 API 的 Lua 库。例如：  
`local d = require("devices")`  
`local s = d:find("sound")`  
`s:playSound("entity.creeper.primed")`

## API
设备名称：`sound`

### 方法
`playSound(name:string[,volume:float,pitch:float])` 播放指定名称的声音效果。
- `name` 是要播放的效果名称。
- `volume` 是播放效果的音量，范围从 `0` 到 `1`，`1` 为正常音量。可选，默认为 `1`。
- `pitch` 是播放效果的音高，范围从 `0.5` 到 `2`，`1` 为正常音高。可选，默认为 `1`。
- 如果指定的名称无效，则会抛出异常。

`findSound(name:string):table` 返回与给定名称匹配的可用声音效果列表。请注意，结果数量有限，因此过于通用的查询会导致结果被截断。
- `name` 是要搜索的名称查询。
- 返回与查询字符串匹配的声音效果名称列表。
