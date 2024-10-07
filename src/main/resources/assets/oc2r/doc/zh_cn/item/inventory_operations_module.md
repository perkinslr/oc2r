# 物品栏操作模块
![你的就是我的](item:oc2r:inventory_operations_module)

物品栏操作模块为[机器人](robot.md)提供了从世界中的物品栏插入和提取物品的能力。它支持方块和实体的物品栏。

## API
设备名称：`inventory_operations`

这是一个高级API设备。它可以使用默认Linux发行版中的Lua进行控制。例如：  
`local d = require("devices")`  
`local m = d:find("inventory_operations")`  
`m:drop(1, "front")`

### 方向
以下方法中的`side`参数表示从机器人视角的一个方向。有效的值包括：`front`、`up`和`down`。

### 方法
`move(fromSlot:number, intoSlot:number, count:number)` 尝试将指定数量的物品从一个机器人物品栏槽移动到另一个槽。
- `fromSlot` 是提取物品的槽位。
- `intoSlot` 是插入物品的槽位。
- `count` 是要移动的物品数量。

`drop(count:number[,side]):number` 尝试从指定槽位朝指定方向丢弃物品。它会将物品丢入一个物品栏中，如果没有物品栏存在则会丢到世界中。
- `count` 是要丢弃的物品数量。
- `side` 是丢弃物品的相对方向。可选，默认为`front`。参见“方向”部分。
- 返回丢弃的物品数量。

`dropInto(intoSlot:number, count:number[,side]):number` 尝试将物品从指定槽位朝指定方向丢入物品栏的指定槽位。它只会将物品丢入物品栏中。
- `intoSlot` 是要插入物品的槽位。
- `count` 是要丢弃的物品数量。
- `side` 是丢弃物品的相对方向。可选，默认为`front`。参见“方向”部分。
- 返回丢弃的物品数量。

`take(count:number[,side]):number` 尝试从指定方向获取指定数量的物品。它会从物品栏中获取物品，如果没有物品栏存在则会从世界中获取物品。
- `count` 是要获取的物品数量。
- `side` 是获取物品的相对方向。可选，默认为`front`。参见“方向”部分。
- 返回获取的物品数量。

`takeFrom(fromSlot:number, count:number[,side]):number` 尝试从指定方向的物品栏的指定槽位获取指定数量的物品。它只会从物品栏中获取物品。
- `fromSlot` 是获取物品的槽位。
- `count` 是要获取的物品数量。
- `side` 是获取物品的相对方向。可选，默认为`front`。参见“方向”部分。
- 返回获取的物品数量。
