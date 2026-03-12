<template>
  <div class="order-list">
    <el-empty v-if="!orders.length" description="暂无订单" />
    <el-card v-for="order in orders" :key="order.id" class="order-card">
      <div class="order-header">
        <span class="order-no">订单号: {{ order.orderNo }}</span>
        <el-tag :type="getStatusType(order.status)">
          {{ getStatusText(order.status) }}
        </el-tag>
      </div>
      <el-divider />
      <div class="order-content">
        <div class="order-info">
          <p><strong>商家:</strong> {{ order.shopName }}</p>
          <p><strong>收货人:</strong> {{ order.receiverName }}</p>
          <p><strong>电话:</strong> {{ order.receiverPhone }}</p>
          <p><strong>地址:</strong> {{ order.deliveryAddress }}</p>
          <p v-if="order.remark"><strong>备注:</strong> {{ order.remark }}</p>
        </div>
        <div class="order-amount">
          <p class="total-amount">¥{{ order.totalAmount }}</p>
          <p class="pay-amount">实付: ¥{{ order.payAmount }}</p>
        </div>
      </div>
      <el-divider />
      <div class="order-actions">
        <span class="order-time">{{ order.createTime }}</span>
        <div class="action-buttons">
          <el-button v-if="order.status === 0" type="primary" size="small" @click="payOrder(order)">
            支付
          </el-button>
          <el-button v-if="order.status === 1 && isMerchant" type="success" size="small" @click="acceptOrder(order)">
            接单
          </el-button>
          <el-button v-if="order.status === 2 && isMerchant" type="primary" size="small" @click="deliverOrder(order)">
            配送
          </el-button>
          <el-button v-if="order.status === 2 && isMerchant" type="success" size="small" @click="completeOrder(order)">
            完成
          </el-button>
          <el-button v-if="order.status === 0 || order.status === 1" type="danger" size="small" @click="cancelOrder(order)">
            取消
          </el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
import { payOrder as payOrderApi, cancelOrder as cancelOrderApi, acceptOrder as acceptOrderApi, deliverOrder as deliverOrderApi, completeOrder as completeOrderApi } from '@/api/order'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserInfo } from '@/utils/auth'

export default {
  props: {
    orders: {
      type: Array,
      default: () => []
    }
  },
  emits: ['refresh'],
  setup(props, { emit }) {
    const userInfo = getUserInfo()
    const isMerchant = userInfo?.role === 1

    const getStatusText = (status) => {
      const statusMap = {
        0: '待支付',
        1: '待接单',
        2: '配送中',
        3: '已完成',
        4: '已取消'
      }
      return statusMap[status] || '未知'
    }

    const getStatusType = (status) => {
      const typeMap = {
        0: 'warning',
        1: 'info',
        2: 'primary',
        3: 'success',
        4: 'danger'
      }
      return typeMap[status] || 'info'
    }

    const payOrder = async (order) => {
      try {
        await payOrderApi(order.id)
        ElMessage.success('支付成功')
        emit('refresh')
      } catch (error) {
        console.error(error)
      }
    }

    const cancelOrder = async (order) => {
      try {
        const { value: reason } = await ElMessageBox.prompt('请输入取消原因', '取消订单', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          inputPattern: /\S+/,
          inputErrorMessage: '请输入取消原因'
        })
        await cancelOrderApi(order.id, reason)
        ElMessage.success('取消成功')
        emit('refresh')
      } catch (error) {
        console.error(error)
      }
    }

    const acceptOrder = async (order) => {
      try {
        await acceptOrderApi(order.id)
        ElMessage.success('接单成功')
        emit('refresh')
      } catch (error) {
        console.error(error)
      }
    }

    const deliverOrder = async (order) => {
      try {
        await deliverOrderApi(order.id)
        ElMessage.success('开始配送')
        emit('refresh')
      } catch (error) {
        console.error(error)
      }
    }

    const completeOrder = async (order) => {
      try {
        await completeOrderApi(order.id)
        ElMessage.success('订单完成')
        emit('refresh')
      } catch (error) {
        console.error(error)
      }
    }

    return {
      isMerchant,
      getStatusText,
      getStatusType,
      payOrder,
      cancelOrder,
      acceptOrder,
      deliverOrder,
      completeOrder
    }
  }
}
</script>

<style scoped>
.order-list {
  min-height: 200px;
}

.order-card {
  margin-bottom: 20px;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.order-no {
  font-weight: bold;
  color: #333;
}

.order-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.order-info p {
  margin: 5px 0;
  color: #666;
}

.order-amount {
  text-align: right;
}

.total-amount {
  font-size: 24px;
  font-weight: bold;
  color: #f56c6c;
  margin: 0 0 5px 0;
}

.pay-amount {
  margin: 0;
  color: #999;
}

.order-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.order-time {
  color: #999;
  font-size: 14px;
}

.action-buttons {
  display: flex;
  gap: 10px;
}
</style>
