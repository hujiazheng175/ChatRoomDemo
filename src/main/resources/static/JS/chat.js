const {createApp, ref, computed, onMounted, nextTick} = Vue;

createApp({
    setup() {
        // 基础数据
        const messages = ref([]);
        const newMessage = ref('');
        const messageList = ref(null);
        const onlineUsers = ref([]);
        const currentUser = ref(null);
        const showUserList = ref(false);
        const showEmoji = ref(false);
        const showProfilePanel = ref(false);
        let ws = null;

        // API 接口封装
        const API = {
            // 获取历史消息
            getHistory: async () => {
                try {
                    const response = await axios.get('/api/chat/history');
                    return response.data;
                } catch (error) {
                    console.error('获取历史消息失败:', error);
                    throw error;
                }
            },

            // 获取在线用户
            getOnlineUsers: async () => {
                try {
                    const response = await axios.get('/api/users/online');
                    return response.data;
                } catch (error) {
                    console.error('获取在线用户失败:', error);
                    throw error;
                }
            }
        };

        // 格式化时间
        const formatTime = (timestamp) => {
            if (!timestamp) return '';

            // 如果是字符串类型的时间戳，需要先转换
            let date = new Date(timestamp);
            if (isNaN(date.getTime())) {
                // 如果转换失败，尝试解析其他格式
                const parsedDate = new Date(timestamp.replace('T', ' ').replace(/-/g, '/'));
                if (isNaN(parsedDate.getTime())) {
                    console.error('Invalid date:', timestamp);
                    return '';
                }
                date = parsedDate;
            }

            const now = new Date();

            // 今天的消息只显示时间
            if (date.toDateString() === now.toDateString()) {
                return date.toLocaleTimeString('zh-CN', {
                    hour: '2-digit',
                    minute: '2-digit'
                });
            }

            // 昨天的消息显示"昨天 HH:mm"
            const yesterday = new Date(now);
            yesterday.setDate(yesterday.getDate() - 1);
            if (date.toDateString() === yesterday.toDateString()) {
                return `昨天 ${date.toLocaleTimeString('zh-CN', {
                    hour: '2-digit',
                    minute: '2-digit'
                })}`;
            }

            // 其他日期显示完整日期和时间
            return date.toLocaleString('zh-CN', {
                month: '2-digit',
                day: '2-digit',
                hour: '2-digit',
                minute: '2-digit'
            });
        };

        // WebSocket连接
        const initWebSocket = () => {
            const token = localStorage.getItem('token');
            if (!token) {
                console.error('未找到token');
                window.location.href = 'login.html';
                return null;
            }

            const host = window.location.hostname;
            const wsUrl = `ws://${host}:8080/ws?token=${token}`;

            ws = new WebSocket(wsUrl);

            ws.onopen = () => {
                console.log('WebSocket连接已建立');
            };

            ws.onmessage = (event) => {
                console.log('收到消息:', event.data);
                try {
                    const data = JSON.parse(event.data);
                    if (data.type === 'message') {
                        // 确保使用 createdAt 字段
                        const message = {
                            ...data.data,
                            timestamp: data.data.createdAt // 使用 createdAt 作为时间戳
                        };
                        messages.value.push(message);
                        nextTick(() => {
                            scrollToBottom();
                        });
                    } else if (data.type === 'user_status') {
                        updateOnlineUsers(data.data);
                    }
                } catch (error) {
                    console.error('处理消息失败:', error);
                }
            };

            ws.onerror = (error) => {
                console.error('WebSocket错误:', error);
            };

            ws.onclose = () => {
                console.log('WebSocket连接已关闭');
                setTimeout(initWebSocket, 3000);
            };

            return ws;
        };

        // 发送消息
        const sendMessage = () => {
            if (!newMessage.value.trim()) return;

            try {
                if (ws && ws.readyState === WebSocket.OPEN) {
                    ws.send(JSON.stringify({
                        content: newMessage.value.trim()
                    }));
                    newMessage.value = '';
                } else {
                    console.error('WebSocket未连接');
                    ws = initWebSocket();
                }
            } catch (error) {
                console.error('发送消息失败:', error);
                alert('发送失败，请重试');
            }
        };

        // 滚动到底部
        const scrollToBottom = () => {
            if (messageList.value) {
                messageList.value.scrollTop = messageList.value.scrollHeight;
            }
        };

        // 更新在线用户列表
        const updateOnlineUsers = (userData) => {
            if (userData.status === 'online') {
                const existingUser = onlineUsers.value.find(u => u.id === userData.userId);
                if (!existingUser) {
                    onlineUsers.value.push({
                        id: userData.userId,
                        username: userData.username,
                        avatarUrl: userData.avatarUrl
                    });
                }
            } else if (userData.status === 'offline') {
                const index = onlineUsers.value.findIndex(u => u.id === userData.userId);
                if (index !== -1) {
                    onlineUsers.value.splice(index, 1);
                }
            }
        };

        // 初始化
        onMounted(async () => {
            try {
                const token = localStorage.getItem('token');
                const userStr = localStorage.getItem('user');
                if (!token || !userStr) {
                    window.location.href = 'login.html';
                    return;
                }

                currentUser.value = JSON.parse(userStr);

                // 获取历史消息
                const historyResponse = await API.getHistory();
                if (historyResponse.success) {
                    // 转换时间戳
                    messages.value = historyResponse.data.map(msg => ({
                        ...msg,
                        timestamp: msg.createdAt // 使用 createdAt 作为时间戳
                    }));
                    nextTick(() => {
                        scrollToBottom();
                    });
                }

                // 获取在线用户
                const usersResponse = await API.getOnlineUsers();
                if (usersResponse.success) {
                    onlineUsers.value = usersResponse.data;
                }

                // 建立WebSocket连接
                ws = initWebSocket();
            } catch (error) {
                console.error('初始化失败:', error);
            }
        });

        return {
            messages,
            newMessage,
            messageList,
            onlineUsers,
            currentUser,
            showUserList,
            showEmoji,
            showProfilePanel,
            sendMessage,
            formatTime,
            handleLogout: () => {
                if (ws) ws.close();
                localStorage.removeItem('token');
                localStorage.removeItem('user');
                window.location.href = 'login.html';
            },
            toggleUserList: () => showUserList.value = !showUserList.value,
            toggleEmoji: () => showEmoji.value = !showEmoji.value,
            toggleProfilePanel: () => showProfilePanel.value = !showProfilePanel.value
        };
    }
}).mount('#app');