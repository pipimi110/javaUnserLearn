# yso payload添加

cc10(cc6前半+cc2后半)
cc11(cc6前半+cc3后半)
ccDoubleUnser(cc6前半+RMIConnector)(解决数组问题)
ccURL(cc6前半+URL)(利用file://和报错盲注文件内容)

# 内存马使用

实现两个功能

- 命令执行回显

- 冰蝎shell（密码rebeyond）

> 冰蝎需实现HttpServlet内存马（替换pageContext为HttpServletRequest+HttpServletResponse）
>
> 以及另一种获取org.apache.coyote.Request（todo）

## Tomcat
### TomcatContextMemShell

> 通过获取StandardContext注册Filter、Servlet、Listener
>
> 注册的注册Filter、Servlet、Listener使用controller反射调用mpwd（默认值为popko123）对应的方法

#### demo

```
favicon1.ico?popko123=getandout&servlet_cmd=whoami
favicon1.ico?popko123=memshellbx&servlet_cmd=memshell
```

## Weblogic

todo...
