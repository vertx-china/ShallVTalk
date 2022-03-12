# ShallVTalk

耍微淘，树新蜂的客户端，树新蜂是服务器端

# 用法  

fork仓库到本地，执行github action，然后会根据不同平台生成对应的软件，

如果选择JIT方式编译与执行，下载后解压缩后是文件夹：  

Windows    
+ 双击bin\VTalk.exe

macOSX   
+ 需将文件夹赋予可执行权：sudo chmod -R +x VTalk-macOSX
+ 需执行以下命令移除苹果隔离：sudo xattr -r -d com.apple.quarantine VTalk-macOSX

Linux
+ 需将文件夹赋予可执行权：sudo chmod -R +x VTalk-Linux
+ 双击启动可通过设置.desktop实现

若选择AOT编译，下载后解压缩后为文件：  

树莓派    
+ 需将文件赋予可执行权：sudo chmod +x ShallVTalk
+ 双击启动可通过设置.desktop实现

Windows    
+ 提供exe和msi两种格式文件，双击即可       
