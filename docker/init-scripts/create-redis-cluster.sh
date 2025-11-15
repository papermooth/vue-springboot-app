#!/bin/sh

# 创建Redis集群
redis-cli --cluster create \
  redis-master-1:7001 \
  redis-master-2:7002 \
  redis-master-3:7003 \
  redis-replica-1:7004 \
  redis-replica-2:7005 \
  redis-replica-3:7006 \
  --cluster-replicas 1 \
  --cluster-yes

echo "Redis集群创建完成！"