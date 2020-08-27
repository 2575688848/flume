# Flume 项目运行相关

# Flume 目录

    bin          Flume 各配置文件对应的控制脚本
    checkpoint   Flume file channel checkpointDir统一配置路径根目录
    conf         Flume 的配置文件
    data         Flume file channel dataDirs统一配置路径根目录
    log          Flume 日志存放目录

# Flume 项目启动命令

### back_bimservice
    nohup flume-ng agent --conf /home/flume/flume/conf --conf-file /home/flume/flume/conf/flume-back-dev.conf --name back_dev > /home/flume/flume/log/back_dev.log 2>&1 & 