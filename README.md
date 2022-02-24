# ShallVTalk

耍微淘，树新蜂的客户端，树新蜂是服务器端

# 用法  

fork仓库到本地，执行github action，然后会根据不同平台生成对应的软件，如果选择JIT方式编译与执行，需打开bin目录，双击VTalk便可启动。    
目前JIT支持的平台有：  
Windows（双击bin\VTalk.exe）    
macOSX（需要执行以下命令移除苹果的隔离：sudo xattr -r -d com.apple.quarantine 随后加上解压缩后文件夹的路径）  
Linux  
目前AOT支持的平台有：  
树莓派（64位）   
