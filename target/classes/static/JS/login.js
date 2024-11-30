const {createApp, ref} = Vue;

createApp({
    setup() {
        const username = ref('');
        const password = ref('');
        const isLoading = ref(false);

        const handleLogin = async () => {
            if (!username.value || !password.value) return;

            isLoading.value = true;
            try {
                const response = await axios.post('/api/auth/login', {
                    username: username.value,
                    password: password.value
                });

                if (response.data.success) {
                    // 修正数据存储路径
                    const {token, user} = response.data.data;
                    localStorage.setItem('token', token);
                    localStorage.setItem('user', JSON.stringify(user));

                    // 设置全局请求头
                    axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;

                    window.location.href = 'chat.html';
                } else {
                    alert(response.data.message || '登录失败');
                }
            } catch (error) {
                console.error('登录失败:', error);
                alert(error.response?.data?.message || '登录失败，请重试');
            } finally {
                isLoading.value = false;
            }
        };

        return {
            username,
            password,
            isLoading,
            handleLogin
        };
    }
}).mount('#app');