# 红石接口
![一切都是红色的](block:oc2r:redstone_interface)

红石接口提供了一个全方向的总线，用于接收和发射红石信号。

使用此设备可以与原始设备互动，如门和灯，或其他提供红石协议的机械设备。

这是一个高级设备。必须使用高级设备 API 进行控制。默认的 Linux 发行版提供了用于此 API 的 Lua 库。例如：  
`local d = require("devices")`  
`local r = d:find("redstone")`  
`r:setRedstoneOutput("up", 15)`

## API
设备名称：`redstone`

### 面
以下方法中的侧面参数表示设备块的本地面。有效值包括：`up`、`down`、`left`、`right`、`front`、`back`、`north`、`south`、`west` 和 `east`。

每个块面都有一个指示器以方便识别。面名称代表从主面（用单个标记指示）看去的名称。当看向主面时：
- `front` 和 `south` 是我们正看向的面。
- `back` 和 `north` 是块的背面。
- `left` 和 `west` 是我们左边的面。
- `right` 和 `east` 是我们右边的面。
- `up` 和 `down` 是顶部和底部面。

### 方法
`getRedstoneInput(side):number` 获取指定面接收到的红石信号。
- `side` 是表示要获取输入的面的字符串。参见“面”部分。
- 返回一个表示当前输入信号强度的数字。

`setRedstoneOutput(side, value:number)` 设置指定面发射的红石信号。
- `side` 是表示要设置信号的面的字符串。参见“面”部分。
- `value` 是一个表示信号强度的数字，范围为 [0, 15]。

`getRedstoneOutput(side):number` 获取指定面发射的红石信号。
- `side` 是表示要获取输出的面的字符串。参见“面”部分。
- 返回一个表示当前输出信号强度的数字。
