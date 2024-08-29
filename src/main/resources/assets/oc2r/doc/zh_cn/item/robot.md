# 机器人
![我欢迎我们的新机器人统治者](item:oc2r:robot)

机器人本质上是移动的[计算机](../block/computer.md)。由于其非固定性质，其行为与常规计算机有所不同。它们不能连接到[总线接口](../block/bus_interface.md)。与卡片设备不同，机器人支持模块设备。这些模块设备专门考虑了机器人的移动性。

机器人具有固定大小的库存和先进的能量储存。只有机器人的常规库存可以通过像漏斗这样的设备自动填充和清空。机器人的组件库存必须手动配置。

在默认配置下，机器人无法与自己的库存进行交互。使用[库存操作模块](inventory_operations_module.md)可以使机器人在自己的库存中移动物品，以及从其他库存中插入和提取物品。

要为机器人充电，建议使用[充电器](../block/charger.md)。机器人可以通过简单地移动到充电器上来自我充电。或者，它们可以被放置到充电器上的一个库存中。

默认的 Linux 发行版提供了一个实用的 Lua 库 `robot`，简化了对机器人的控制。底层 API 提供了异步移动方法。该库实现了同步替代方法，使顺序编程更加方便。

## API
设备名称：`robot`

这是一个高级 API 设备。可以在默认的 Linux 发行版中使用 Lua 控制。例如：  
`local d = require("devices")`  
`local r = d:find("robot")`  
`r:move("forward")`

### 方向
以下方法中的方向参数表示相对于机器人的方向。有效值为：`forward`、`backward`、`upward`、`downward`（用于移动操作），`left` 和 `right`（用于旋转操作）。这些方向始终是从机器人执行操作时的视角来看待的。

为了方便，可以使用这些值的简短别名：`back`、`up`、`down`。为了极致简洁，也可以使用每个方向的首字母。

### 方法
这些方法在底层机器人设备上可用。注意，库为所有这些方法提供了有用的封装。建议使用库而不是直接与设备交互。

`getEnergyStored():number` 返回机器人内部能量储存的当前量。
- 返回储存的能量量。

`getEnergyCapacity():number` 返回机器人内部能量储存的最大量。
- 返回最大储存的能量量。

`getSelectedSlot():number` 返回当前选择的机器人库存槽。这被许多模块作为隐式输入使用。
- 返回所选库存槽的索引。

`setSelectedSlot(slot:number):number` 设置当前选择的机器人库存槽。这被许多模块作为隐式输入使用。
- `slot` 是要选择的库存槽的索引。
- 返回新选择的槽的索引。如果指定的值无效，则可能与 `slot` 不同。

`getStackInSlot(slot:number):table` 获取指定槽中的物品描述。
- `slot` 是要获取物品描述的槽的索引。

`move(direction):boolean` 尝试将移动操作排入指定方向。
- `direction` 是要移动的方向。
- 返回操作是否成功排队。

`turn(direction):boolean` 尝试将转动操作排入指定方向。
- `direction` 是要转动的方向。
- 返回操作是否成功排队。

`getLastActionId():number` 返回最后排队操作的透明 ID。成功调用 `move()` 或 `turn()` 后调用此方法，以获取与排队操作关联的 ID。
- 返回最后排队操作的 ID。

`getQueuedActionCount():number` 返回当前等待处理的操作数量。使用此方法可以在排队失败时等待操作完成。
- 返回当前待处理的操作数量。

`getActionResult(actionId:number):string` 返回具有指定 ID 的操作的结果。操作 ID 可以从 `getLastActionId()` 获取。只有有限数量的过去操作结果可用。
- 返回指定操作 ID 的结果，如果不可用则返回空。当可用时，可能的值包括：`INCOMPLETE`、`SUCCESS` 和 `FAILURE`。

### 库 API
- 库名称：`robot`

这是一个 Lua 库。可以在默认的 Linux 发行版中使用。例如：  
`local r = require("robot")`  
`r.move("forward")`  
`r.turn("left")`

### 方法
`energy():number` 返回机器人内部能量储存的当前量。
- 返回储存的能量量。

`capacity():number` 返回机器人内部能量储存的最大量。
- 返回最大储存的能量量。

`slot():number` 返回当前选择的机器人库存槽。这被许多模块作为隐式输入使用。
- 返回所选库存槽的索引。

`slot(slot:number):number` 设置当前选择的机器人库存槽。这被许多模块作为隐式输入使用。
- `slot` 是要选择的库存槽的索引。
- 返回新选择的槽的索引。如果指定的值无效，则可能与 `slot` 不同。

`stack([slot:number]):table` 获取指定槽中的物品描述。
- `slot` 是要获取物品描述的槽的索引。可选，默认为 `slot()`。

`move(direction):boolean` 尝试移动到指定方向。直到移动操作完成时才会阻塞。
- `direction` 是要移动的方向。
- 返回操作是否成功。

`moveAsync(direction)` 尝试异步移动到指定方向。直到操作成功排队时才会阻塞。
- `direction` 是要移动的方向。

`turn(direction):boolean` 尝试朝指定方向转动。直到旋转操作完成时才会阻塞。
- `direction` 是要转动的方向。
- 返回操作是否成功。

`turnAsync(direction)` 尝试异步转动到指定方向。直到操作成功排队时才会阻塞。
- `direction` 是要转动的方向。
