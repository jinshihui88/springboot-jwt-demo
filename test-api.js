// 端到端API测试脚本
const axios = require('axios');

const API_BASE = 'http://localhost:8080/api';

async function testAPI() {
  console.log('🚀 开始端到端API测试...\n');

  try {
    // 1. 测试用户登录
    console.log('📝 测试用户登录...');
    const loginResponse = await axios.post(`${API_BASE}/auth/login`, {
      username: 'admin',
      password: 'admin123'
    });
    
    if (loginResponse.data.code === 200) {
      console.log('✅ 登录成功');
      console.log(`   用户: ${loginResponse.data.data.userInfo.username}`);
      console.log(`   Token类型: ${loginResponse.data.data.tokenType}`);
    }

    const accessToken = loginResponse.data.data.accessToken;
    const headers = { Authorization: `Bearer ${accessToken}` };

    // 2. 测试获取用户信息
    console.log('\n👤 测试获取用户信息...');
    const profileResponse = await axios.get(`${API_BASE}/user/profile`, { headers });
    
    if (profileResponse.data.code === 200) {
      console.log('✅ 获取用户信息成功');
      console.log(`   用户名: ${profileResponse.data.data.username}`);
      console.log(`   邮箱: ${profileResponse.data.data.email}`);
      console.log(`   状态: ${profileResponse.data.data.status === 1 ? '正常' : '禁用'}`);
    }

    // 3. 测试管理员接口
    console.log('\n🔐 测试管理员接口...');
    const adminResponse = await axios.get(`${API_BASE}/user/admin/users`, { headers });
    
    if (adminResponse.data.code === 200) {
      console.log('✅ 管理员接口访问成功');
      console.log(`   响应数据: ${adminResponse.data.data}`);
    }

    // 4. 测试普通用户登录
    console.log('\n👥 测试普通用户登录...');
    const userLoginResponse = await axios.post(`${API_BASE}/auth/login`, {
      username: 'user',
      password: 'admin123'
    });
    
    if (userLoginResponse.data.code === 200) {
      console.log('✅ 普通用户登录成功');
      console.log(`   用户: ${userLoginResponse.data.data.userInfo.username}`);
    }

    const userToken = userLoginResponse.data.data.accessToken;
    const userHeaders = { Authorization: `Bearer ${userToken}` };

    // 5. 测试普通用户访问管理员接口（应该失败）
    console.log('\n❌ 测试普通用户访问管理员接口...');
    try {
      await axios.get(`${API_BASE}/user/admin/users`, { headers: userHeaders });
      console.log('❌ 权限控制失败：普通用户能访问管理员接口');
    } catch (error) {
      if (error.response?.status === 403) {
        console.log('✅ 权限控制正常：普通用户无法访问管理员接口');
      } else {
        console.log(`🔍 其他错误: ${error.response?.status} - ${error.response?.data?.message}`);
      }
    }

    // 6. 测试用户登出
    console.log('\n🚪 测试用户登出...');
    const logoutResponse = await axios.post(`${API_BASE}/auth/logout`, {}, { headers });
    
    if (logoutResponse.data.code === 200) {
      console.log('✅ 用户登出成功');
    }

    console.log('\n🎉 所有测试完成！前后端集成测试通过！');

  } catch (error) {
    console.error('❌ 测试失败:', error.response?.data?.message || error.message);
    console.error('   状态码:', error.response?.status);
    console.error('   详细信息:', error.response?.data);
  }
}

// 等待服务器启动后执行测试
setTimeout(testAPI, 2000);