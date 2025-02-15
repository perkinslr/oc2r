# 总线接口
![即插即用](item:oc2r:bus_interface)

总线接口将外部设备连接到 [计算机](computer.md)。这包括显式设备方块，例如 [红石接口](redstone_interface.md)。还提供了一些通用功能方块，例如能量存储信息。

可以使用 [螺丝刀扳手](../item/wrench.md) 为总线接口配置一个显式名称。当将多个相同类型的设备连接到计算机时，这很有用：在按类型名称（`devices:find(typeName)`）搜索设备时，这些自定义名称也会生效。

请注意，[计算机](computer.md) 还必须通过总线连接器显式连接到 [总线](bus_cable.md)。
