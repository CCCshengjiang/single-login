# 单设备登陆限制

1. **没有限制**：如果没有限制单设备登录，就会造成数据信息泄露、资源访问冲突、权限管理不便等问题
2. **加上限制**：经过单设备登录限制之后，那么第二个使用相同账号登陆的人，会把第一个登录人的账号在**其它设备**的登录态删除（顶掉）



# 实现思路

> 相同的设备，在不同的浏览器内登录，sessionId 也是不相同的，所有还需要加上 IP 判断



在用户登陆时，先判断 `sessionId` 是否相同：

- 相同：说明是同一个客户端，那么就是同一个设备，正常登录
- 不相同，判断 IP 是否相同
  - 相同，说明是同一个设备，允许登录
  - 不相同，说明已经在其它设备登录，删除其他设备的登录态

继续执行原有的登录逻辑。



# 实现流程

![单设备登录流程图](https://gitee.com/CCCshengjiang/blog-img/raw/master/image/202403072019303.png)