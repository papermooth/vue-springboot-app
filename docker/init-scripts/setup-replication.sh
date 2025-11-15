#!/bin/bash

# MySQL主从复制配置脚本

echo "开始配置MySQL主从复制..."

# 定义变量
MASTER_HOST=mysql-master
MASTER_PORT=3306
SLAVE1_HOST=mysql-slave-1
SLAVE2_HOST=mysql-slave-2
MYSQL_ROOT_PASSWORD=${MYSQL_ROOT_PASSWORD:-rootpassword}
REPL_USER=repl_user
REPL_PASSWORD=repl_password

# 在主节点创建复制用户
echo "在主节点创建复制用户..."
docker exec mysql-master mysql -uroot -p${MYSQL_ROOT_PASSWORD} -e "\
CREATE USER IF NOT EXISTS '${REPL_USER}'@'%' IDENTIFIED BY '${REPL_PASSWORD}'; \
GRANT REPLICATION SLAVE ON *.* TO '${REPL_USER}'@'%'; \
FLUSH PRIVILEGES;"

# 获取主节点的二进制日志文件和位置
echo "获取主节点二进制日志信息..."
MASTER_STATUS=$(docker exec mysql-master mysql -uroot -p${MYSQL_ROOT_PASSWORD} -e "SHOW MASTER STATUS\G" | grep -E 'File|Position')
MASTER_LOG_FILE=$(echo "$MASTER_STATUS" | grep File | awk '{print $2}')
MASTER_LOG_POS=$(echo "$MASTER_STATUS" | grep Position | awk '{print $2}')

echo "主节点二进制日志文件: $MASTER_LOG_FILE"
echo "主节点二进制日志位置: $MASTER_LOG_POS"

# 配置从节点1
echo "配置从节点1..."
docker exec $SLAVE1_HOST mysql -uroot -p${MYSQL_ROOT_PASSWORD} -e "\
CHANGE MASTER TO \
MASTER_HOST='$MASTER_HOST', \
MASTER_USER='$REPL_USER', \
MASTER_PASSWORD='$REPL_PASSWORD', \
MASTER_LOG_FILE='$MASTER_LOG_FILE', \
MASTER_LOG_POS=$MASTER_LOG_POS, \
MASTER_CONNECT_RETRY=30; \
START SLAVE;"

# 配置从节点2
echo "配置从节点2..."
docker exec $SLAVE2_HOST mysql -uroot -p${MYSQL_ROOT_PASSWORD} -e "\
CHANGE MASTER TO \
MASTER_HOST='$MASTER_HOST', \
MASTER_USER='$REPL_USER', \
MASTER_PASSWORD='$REPL_PASSWORD', \
MASTER_LOG_FILE='$MASTER_LOG_FILE', \
MASTER_LOG_POS=$MASTER_LOG_POS, \
MASTER_CONNECT_RETRY=30; \
START SLAVE;"

# 验证从节点状态
echo "验证从节点1状态..."
docker exec $SLAVE1_HOST mysql -uroot -p${MYSQL_ROOT_PASSWORD} -e "SHOW SLAVE STATUS\G"

echo "验证从节点2状态..."
docker exec $SLAVE2_HOST mysql -uroot -p${MYSQL_ROOT_PASSWORD} -e "SHOW SLAVE STATUS\G"

echo "MySQL主从复制配置完成！"