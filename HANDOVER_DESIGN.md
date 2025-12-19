# 🤝 有温度的交接会话设计 - 完整实施方案

> **让器材共享不再冷冰冰，用心呵护每一次交接**

---

## 🌟 设计理念

### 核心价值
- 💝 **有温度**: 不仅仅是器材交换，更是爱心传递
- 🔒 **重隐私**: 受控的联系方式交换，敏感信息自动拦截
- 🎯 **简化流程**: 清晰的状态流转，降低操作复杂度
- 🏆 **社区互动**: 感谢贴自动同步NodeBB，增强社区凝聚力

---

## 📋 功能全览

### 1. 交接会话页 (Handover Page)

#### 1.1 状态流转
```
沟通中 → 约见/寄出 → 完成
```

**状态说明**:
- **沟通中**: 双方可以简单留言，了解器材详情和约定细节
- **约见/寄出**: 确定交接方式(快递/当面)，并进入实际交接流程
- **完成**: 器材已成功交接，进入感谢评价环节

#### 1.2 沟通区（简化留言）

**设计要点**:
```vue
<template>
  <div class="message-area">
    <!-- 系统提示 -->
    <el-alert type="warning" :closable="false">
      💡 请不要发送电话、微信等联系方式,如需交换请点击"交换联系方式"按钮
    </el-alert>
    
    <!-- 消息列表 -->
    <div class="message-list">
      <div v-for="msg in messages" :key="msg.id" :class="msgClass(msg)">
        <div class="msg-content">{{ msg.content }}</div>
        <div class="msg-time">{{ msg.createTime }}</div>
        <!-- 如果包含敏感信息 -->
        <el-tag v-if="msg.hasSensitiveInfo" type="danger" size="small">
          ⚠️ 包含敏感信息: {{ msg.sensitiveKeywords }}
        </el-tag>
      </div>
    </div>
    
    <!-- 发送框 -->
    <el-input
      v-model="newMessage"
      type="textarea"
      :rows="3"
      placeholder="简短沟通,了解器材详情..."
      maxlength="200"
      show-word-limit
    />
    <el-button @click="sendMessage">发送</el-button>
  </div>
</template>
```

**敏感信息拦截机制**:
- 关键词检测: 电话、手机、微信、QQ、邮箱、地址等
- 三级处理:
  - **提示级**: 高亮显示,但允许发送
  - **警告级**: 弹窗确认
  - **拦截级**: 禁止发送

#### 1.3 "交换联系方式"按钮（受控流程）

```vue
<template>
  <div class="contact-exchange">
    <!-- 未交换状态 -->
    <el-button 
      v-if="contactStatus === 0" 
      type="primary" 
      @click="requestContactExchange"
    >
      🤝 交换联系方式
    </el-button>
    
    <!-- 等待对方同意 -->
    <div v-if="contactStatus === 1" class="waiting-status">
      <el-icon><Loading /></el-icon>
      <span>等待对方同意...</span>
      <el-button size="small" @click="cancelRequest">取消请求</el-button>
    </div>
    
    <!-- 已交换，显示临时中转号 -->
    <div v-if="contactStatus === 2" class="contact-info">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="对方临时联系号">
          <el-tag type="success">{{ tempPhone }}</el-tag>
          <el-text type="info" size="small">
            （交易完成后自动失效）
          </el-text>
        </el-descriptions-item>
        <el-descriptions-item label="有效期至">
          {{ expireTime }}
        </el-descriptions-item>
      </el-descriptions>
    </div>
  </div>
</template>
```

**隐私保护方案**:
1. **双方确认机制**: 需要双方都同意才展示联系方式
2. **一次性中转号**: 使用临时号码,交易完成后自动失效
3. **有效期限制**: 默认7天,可配置
4. **可选方案**: 集成第三方中转IM(如网易云信、环信)

---

### 2. 快递模式（Express Mode）

#### 2.1 流程设计

```
选择快递模式 → 填写收货信息 → 填写快递信息 → 物流追踪 → 确认签收
```

#### 2.2 页面设计

```vue
<template>
  <div class="express-mode">
    <el-steps :active="currentStep" finish-status="success">
      <el-step title="选择快递" />
      <el-step title="填写单号" />
      <el-step title="物流追踪" />
      <el-step title="确认签收" />
    </el-steps>
    
    <!-- Step 1: 收货信息（仅接收人填写，仅发布者可见） -->
    <el-form v-if="currentStep === 0" :model="receiveForm">
      <el-alert type="info" :closable="false">
        📦 收货信息仅发布者可见，保障您的隐私
      </el-alert>
      <el-form-item label="收货人">
        <el-input v-model="receiveForm.receiverName" />
      </el-form-item>
      <el-form-item label="联系电话">
        <el-input v-model="receiveForm.receiverPhone" />
      </el-form-item>
      <el-form-item label="收货地址">
        <el-cascader 
          v-model="receiveForm.address" 
          :options="areaOptions"
          placeholder="省/市/区"
        />
        <el-input 
          v-model="receiveForm.detailAddress" 
          placeholder="详细地址"
          style="margin-top: 10px"
        />
      </el-form-item>
      <el-button type="primary" @click="submitReceiveInfo">确认</el-button>
    </el-form>
    
    <!-- Step 2: 快递信息（发布者填写） -->
    <el-form v-if="currentStep === 1" :model="expressForm">
      <el-form-item label="快递公司">
        <el-select v-model="expressForm.company" placeholder="选择快递公司">
          <el-option label="顺丰速运" value="SF" />
          <el-option label="圆通速递" value="YTO" />
          <el-option label="中通快递" value="ZTO" />
          <el-option label="申通快递" value="STO" />
          <el-option label="韵达快递" value="YD" />
          <el-option label="邮政EMS" value="EMS" />
        </el-select>
      </el-form-item>
      <el-form-item label="快递单号">
        <el-input 
          v-model="expressForm.trackingNo" 
          placeholder="请输入快递单号"
        />
      </el-form-item>
      <el-button type="primary" @click="submitExpressInfo">提交</el-button>
    </el-form>
    
    <!-- Step 3: 物流追踪 -->
    <div v-if="currentStep === 2" class="tracking-info">
      <el-timeline>
        <el-timeline-item
          v-for="(activity, index) in trackingList"
          :key="index"
          :timestamp="activity.time"
          :type="index === 0 ? 'primary' : 'info'"
        >
          {{ activity.context }}
        </el-timeline-item>
      </el-timeline>
      
      <!-- 自动刷新按钮 -->
      <el-button @click="refreshTracking">
        <el-icon><Refresh /></el-icon>
        刷新物流
      </el-button>
    </div>
    
    <!-- Step 4: 确认签收 -->
    <div v-if="currentStep === 3" class="confirm-delivery">
      <el-result icon="success" title="快递已签收">
        <template #extra>
          <el-button type="primary" @click="confirmDelivery">
            确认收货
          </el-button>
        </template>
      </el-result>
    </div>
  </div>
</template>
```

#### 2.3 物流追踪API集成

**推荐方案**: 快递鸟API

```java
@Service
public class ExpressTrackingService {
    
    /**
     * 查询物流信息
     */
    public List<ExpressTrackingDTO> queryExpress(String expressCompany, String expressNo) {
        // 调用快递鸟API
        String url = "https://api.kdniao.com/Ebusiness/EbusinessOrderHandle.aspx";
        
        // 构建请求参数
        Map<String, Object> params = new HashMap<>();
        params.put("ShipperCode", expressCompany);
        params.put("LogisticCode", expressNo);
        
        // 发送请求并解析结果
        String response = HttpUtil.post(url, params);
        return parseExpressResponse(response);
    }
    
    /**
     * 定时任务：自动更新物流状态
     */
    @Scheduled(cron = "0 */30 * * * ?") // 每30分钟更新一次
    public void autoUpdateExpressStatus() {
        // 查询所有未完成的快递订单
        List<EquipmentHandoverDO> list = handoverMapper.selectListByExpressStatus(
            Arrays.asList(0, 1, 2, 3) // 待发货到派送中
        );
        
        for (EquipmentHandoverDO handover : list) {
            // 查询最新物流信息
            List<ExpressTrackingDTO> tracking = queryExpress(
                handover.getExpressCompany(), 
                handover.getExpressNo()
            );
            
            // 更新状态
            if (isDelivered(tracking)) {
                handover.setExpressStatus(ExpressStatusEnum.DELIVERED.getStatus());
                handoverMapper.updateById(handover);
                
                // 发送通知
                sendDeliveryNotification(handover);
            }
        }
    }
}
```

---

### 3. 当面模式（Face-to-Face Mode）

#### 3.1 流程设计

```
选择当面模式 → 约定时间 → 选择公共地点 → 确认交接
```

#### 3.2 页面设计

```vue
<template>
  <div class="face-to-face-mode">
    <el-steps :active="currentStep" finish-status="success">
      <el-step title="约定时间" />
      <el-step title="选择地点" />
      <el-step title="确认交接" />
    </el-steps>
    
    <!-- Step 1: 约定时间 -->
    <div v-if="currentStep === 0" class="date-selection">
      <el-calendar v-model="selectedDate">
        <template #date-cell="{ data }">
          <div 
            :class="['calendar-day', {
              'available': isAvailable(data.day),
              'selected': isSelected(data.day)
            }]"
            @click="selectDate(data.day)"
          >
            {{ data.day.split('-').slice(2).join('-') }}
          </div>
        </template>
      </el-calendar>
      
      <el-time-select
        v-model="selectedTime"
        :picker-options="{
          start: '09:00',
          step: '00:30',
          end: '21:00'
        }"
        placeholder="选择时间"
      />
      
      <el-button type="primary" @click="confirmDateTime">
        确认时间
      </el-button>
    </div>
    
    <!-- Step 2: 选择公共地点（仅限公共场所） -->
    <div v-if="currentStep === 1" class="location-selection">
      <el-alert type="warning" :closable="false">
        🔒 为了您的安全，交接地点仅限公共场所
      </el-alert>
      
      <el-tabs v-model="locationType">
        <el-tab-pane label="🚇 地铁站" name="metro">
          <el-select v-model="selectedLocation" filterable placeholder="搜索地铁站">
            <el-option 
              v-for="loc in metroStations" 
              :key="loc.id"
              :label="loc.name"
              :value="loc.id"
            >
              <span>{{ loc.name }}</span>
              <span style="float: right; color: #8492a6">{{ loc.line }}</span>
            </el-option>
          </el-select>
        </el-tab-pane>
        
        <el-tab-pane label="🛍️ 商场" name="mall">
          <el-select v-model="selectedLocation" filterable placeholder="搜索商场">
            <el-option 
              v-for="loc in malls" 
              :key="loc.id"
              :label="loc.name"
              :value="loc.id"
            />
          </el-select>
        </el-tab-pane>
        
        <el-tab-pane label="🌳 公园" name="park">
          <el-select v-model="selectedLocation" filterable placeholder="搜索公园">
            <el-option 
              v-for="loc in parks" 
              :key="loc.id"
              :label="loc.name"
              :value="loc.id"
            />
          </el-select>
        </el-tab-pane>
        
        <el-tab-pane label="📚 图书馆" name="library">
          <el-select v-model="selectedLocation" filterable placeholder="搜索图书馆">
            <el-option 
              v-for="loc in libraries" 
              :key="loc.id"
              :label="loc.name"
              :value="loc.id"
            />
          </el-select>
        </el-tab-pane>
      </el-tabs>
      
      <!-- 地图预览 -->
      <div class="map-preview">
        <el-amap 
          :center="locationCenter" 
          :zoom="15"
          :markers="[{ position: locationCenter }]"
        />
      </div>
      
      <el-button type="primary" @click="confirmLocation">
        确认地点
      </el-button>
    </div>
    
    <!-- Step 3: 交接确认 -->
    <div v-if="currentStep === 2" class="handover-confirm">
      <el-result icon="info" title="约定信息">
        <template #sub-title>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="时间">
              {{ selectedDate }} {{ selectedTime }}
            </el-descriptions-item>
            <el-descriptions-item label="地点">
              {{ locationName }}
            </el-descriptions-item>
            <el-descriptions-item label="地址">
              {{ locationAddress }}
            </el-descriptions-item>
          </el-descriptions>
        </template>
        <template #extra>
          <el-button type="primary" @click="confirmHandover">
            确认已交接
          </el-button>
          <el-button @click="modifyAppointment">
            修改约定
          </el-button>
        </template>
      </el-result>
      
      <!-- 倒计时提醒 -->
      <el-alert 
        v-if="shouldRemind" 
        type="warning" 
        :closable="false"
        style="margin-top: 20px"
      >
        ⏰ 距离约定时间还有 {{ remainingTime }}
      </el-alert>
    </div>
  </div>
</template>
```

---

### 4. 交接后：感谢与评价

#### 4.1 流程设计

```
交接完成 → 引导评价 → 填写感谢 → 选择公开/私密 → 同步NodeBB（可选） → 完成
```

#### 4.2 页面设计

```vue
<template>
  <div class="feedback-page">
    <el-dialog 
      v-model="showFeedbackDialog" 
      title="💝 写下您的感谢" 
      width="600px"
      :close-on-click-modal="false"
    >
      <!-- 星级评价 -->
      <el-form :model="feedbackForm">
        <el-form-item label="评价">
          <el-rate 
            v-model="feedbackForm.rating" 
            :texts="['很差', '较差', '一般', '满意', '非常满意']"
            show-text
          />
        </el-form-item>
        
        <!-- 感谢内容 -->
        <el-form-item label="感谢的话">
          <el-input
            v-model="feedbackForm.content"
            type="textarea"
            :rows="6"
            placeholder="分享您的感受，让爱心传递..."
            maxlength="500"
            show-word-limit
          />
          
          <el-text type="info" size="small">
            💡 可以分享器材的使用感受、对方的贴心之处等
          </el-text>
        </el-form-item>
        
        <!-- 隐私设置 -->
        <el-form-item label="隐私设置">
          <el-radio-group v-model="feedbackForm.isPublic">
            <el-radio :label="true">
              <span>🌍 公开</span>
              <el-text type="info" size="small">
                （其他用户可以看到，增强社区信任）
              </el-text>
            </el-radio>
            <el-radio :label="false">
              <span>🔒 私密</span>
              <el-text type="info" size="small">
                （仅自己可见）
              </el-text>
            </el-radio>
          </el-radio-group>
        </el-form-item>
        
        <!-- 同步到NodeBB -->
        <el-form-item label="同步到社区">
          <el-checkbox v-model="feedbackForm.syncToNodebb">
            📢 同步生成"感谢贴"到NodeBB论坛
            <el-text type="info" size="small">
              （感谢贴会自动带上器材链接，方便其他用户参考）
            </el-text>
          </el-checkbox>
        </el-form-item>
        
        <!-- 预览效果 -->
        <el-collapse v-if="feedbackForm.syncToNodebb">
          <el-collapse-item title="预览感谢贴效果">
            <div class="nodebb-preview">
              <h3>{{ generatePostTitle() }}</h3>
              <div class="post-content">
                {{ feedbackForm.content }}
              </div>
              <div class="equipment-card">
                <el-card shadow="hover">
                  <template #header>
                    <span>相关器材</span>
                  </template>
                  <div class="equipment-info">
                    <img :src="equipment.picUrl" alt="" />
                    <div class="info">
                      <h4>{{ equipment.name }}</h4>
                      <p>{{ equipment.introduction }}</p>
                    </div>
                  </div>
                </el-card>
              </div>
            </div>
          </el-collapse-item>
        </el-collapse>
      </el-form>
      
      <template #footer>
        <el-button @click="skipFeedback">暂不评价</el-button>
        <el-button type="primary" @click="submitFeedback">
          提交感谢
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
const generatePostTitle = () => {
  return `【感谢】感谢 @${donorName} 赠送/出借 ${equipment.name}`
}

const submitFeedback = async () => {
  // 1. 保存评价到数据库
  await saveFeedback(feedbackForm)
  
  // 2. 如果勾选同步到NodeBB
  if (feedbackForm.syncToNodebb) {
    await syncToNodeBB({
      title: generatePostTitle(),
      content: feedbackForm.content,
      equipmentLink: getEquipmentLink(),
      equipmentCardData: equipment
    })
  }
  
  // 3. 更新信用分
  await updateCreditScore(userId, 'GOOD_REVIEW', 3)
  
  ElMessage.success('感谢已提交！')
  showFeedbackDialog.value = false
}
</script>
```

#### 4.3 NodeBB同步机制

```java
@Service
public class NodebbSyncService {
    
    @Resource
    private NodebbApiClient nodebbApiClient;
    
    /**
     * 同步感谢贴到NodeBB
     */
    public Long syncFeedbackToNodeBB(EquipmentFeedbackDO feedback) {
        try {
            // 1. 获取器材信息
            ProductSpuDO equipment = productSpuMapper.selectById(feedback.getEquipmentId());
            
            // 2. 构建帖子内容
            String title = String.format("【感谢】感谢 @%s %s %s",
                getUserName(feedback.getEvaluatedUserId()),
                feedback.getHandoverType() == 1 ? "赠送" : "出借",
                equipment.getName()
            );
            
            String content = buildNodeBBContent(feedback, equipment);
            
            // 3. 发布到NodeBB
            NodeBBPostRespDTO response = nodebbApiClient.createTopic(
                NodeBBCreateTopicReqDTO.builder()
                    .title(title)
                    .content(content)
                    .cid(getThanksCategoryId()) // 感谢分类ID
                    .tags(Arrays.asList("感谢", "器材共享"))
                    .build()
            );
            
            // 4. 保存NodeBB信息
            feedback.setNodebbTopicId(response.getTopicId());
            feedback.setNodebbPostId(response.getPostId());
            feedback.setNodebbSyncTime(LocalDateTime.now());
            feedback.setNodebbSyncStatus(2); // 已同步
            feedbackMapper.updateById(feedback);
            
            return response.getTopicId();
            
        } catch (Exception e) {
            log.error("[NodeBB同步] 同步失败", e);
            feedback.setNodebbSyncStatus(3); // 同步失败
            feedbackMapper.updateById(feedback);
            throw new ServiceException("同步到NodeBB失败");
        }
    }
    
    /**
     * 构建NodeBB帖子内容
     */
    private String buildNodeBBContent(EquipmentFeedbackDO feedback, ProductSpuDO equipment) {
        StringBuilder content = new StringBuilder();
        
        // 感谢内容
        content.append(feedback.getFeedbackContent()).append("\n\n");
        
        // 器材卡片（使用NodeBB的卡片插件格式）
        content.append("---\n\n");
        content.append("## 相关器材\n\n");
        content.append(String.format("[![%s](%s)](%s)\n\n",
            equipment.getName(),
            equipment.getPicUrl(),
            feedback.getEquipmentCardLink()
        ));
        content.append(String.format("**器材名称**: %s\n\n", equipment.getName()));
        content.append(String.format("**器材简介**: %s\n\n", equipment.getIntroduction()));
        content.append(String.format("**查看详情**: [点击这里](%s)\n\n", feedback.getEquipmentCardLink()));
        
        return content.toString();
    }
}
```

---

## 📊 数据库表结构

### 核心表

#### 1. equipment_handover (交接会话表)
- 记录完整的交接过程
- 支持快递和当面两种模式
- 包含联系方式交换状态
- 一次性中转号管理

#### 2. equipment_handover_message (消息表)
- 简化的留言功能
- 敏感信息自动检测
- 阅读状态追踪

#### 3. equipment_feedback (感谢评价表)
- 感谢内容和评分
- 公开/私密设置
- NodeBB同步状态
- 器材卡片回链

#### 4. sensitive_keyword (敏感词库表)
- 可配置的敏感词
- 分级处理机制

#### 5. public_location (公共地点库表)
- 预置的公共地点
- 支持地理位置
- 使用统计

---

## 🚀 快速开始

### 1. 执行数据库脚本

```bash
# PostgreSQL
psql -U your_user -d your_db -f sql/postgresql/equipment_handover.sql

# MySQL
mysql -u your_user -p your_db < sql/mysql/equipment_handover.sql
```

### 2. 创建后端Service（示例）

参考 `QUICK_START.md` 中的Service实现模板

### 3. 创建前端页面

使用上面提供的Vue组件代码作为起点

---

## 💡 最佳实践

### 隐私保护
1. ✅ 始终使用临时中转号
2. ✅ 严格限制联系方式交换条件
3. ✅ 交易完成后自动失效中转号
4. ✅ 敏感信息实时检测和提醒

### 用户体验
1. ✅ 清晰的状态提示
2. ✅ 简化的操作流程
3. ✅ 友好的错误提示
4. ✅ 及时的进度通知

### 社区互动
1. ✅ 鼓励公开感谢
2. ✅ 自动同步到论坛
3. ✅ 器材卡片回链
4. ✅ 增加信用分奖励

---

## 🎨 UI/UX设计要点

### 色彩搭配
- **沟通中**: 蓝色(#409EFF) - 代表沟通
- **约见/寄出**: 橙色(#E6A23C) - 代表进行中
- **完成**: 绿色(#67C23A) - 代表成功

### 图标使用
- 🤝 交换联系方式
- 📦 快递模式
- 👥 当面模式
- 💝 感谢评价
- 🔒 隐私保护

### 提示文案
- 温馨友好
- 清晰明确
- 避免专业术语
- 注重情感传递

---

## 📞 需要帮助?

完整的实现代码和更多细节，请参考:
- 数据库脚本: `sql/postgresql/equipment_handover.sql`
- 后端枚举: 已创建在 `yudao-module-trade/enums/handover/`
- 实施指南: `EQUIPMENT_RENOVATION_GUIDE.md`

---

<p align="center">
  <b>用心设计每一个细节，让爱心传递更有温度！</b><br>
  <i>温暖、安全、便捷 ❤️</i>
</p>

---

**文档版本**: v1.0  
**创建日期**: 2025-12-15  
**适用系统**: 康复训练器材共享平台

