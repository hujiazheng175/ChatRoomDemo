const {createApp, ref, computed} = Vue;

createApp({
    setup() {
        const username = ref('');
        const password = ref('');
        const confirmPassword = ref('');
        const avatarPreview = ref('./assets/defaultImg.jpg');
        const fileInput = ref(null);
        const isLoading = ref(false);

        // 计算属性：是否可以注册
        const canRegister = computed(() => {
            return username.value.length >= 3 &&
                username.value.length <= 20 &&
                password.value.length >= 6 &&
                password.value === confirmPassword.value &&
                avatarPreview.value !== './assets/defaultImg.jpg';
        });

        // 处理头像上传
        const handleFileChange = (event) => {
            const file = event.target.files[0];
            if (!file) return;

            // 验证文件类型和大小
            if (!['image/jpeg', 'image/png'].includes(file.type)) {
                alert('只支持 JPG 和 PNG 格式的图片');
                return;
            }

            if (file.size > 5 * 1024 * 1024) {
                alert('图片大小不能超过5MB');
                return;
            }

            // 预览头像
            const reader = new FileReader();
            reader.onload = (e) => {
                avatarPreview.value = e.target.result;
            };
            reader.readAsDataURL(file);
        };

        const triggerFileInput = () => {
            fileInput.value.click();
        };

        const handleRegister = async () => {
            if (!username.value || !password.value) {
                alert('请填写用户名和密码');
                return;
            }

            if (!fileInput.value.files[0]) {
                alert('请上传头像');
                return;
            }

            isLoading.value = true;
            try {
                const formData = new FormData();
                formData.append('username', username.value);
                formData.append('password', password.value);
                formData.append('avatar', fileInput.value.files[0]);

                const response = await axios.post('/api/auth/register', formData, {
                    headers: {
                        'Content-Type': 'multipart/form-data'
                    }
                });

                if (response.data.success) {
                    alert('注册成功！');
                    window.location.href = 'login.html';
                } else {
                    alert(response.data.message || '注册失败');
                }
            } catch (error) {
                console.error('注册失败:', error);
                alert(error.response?.data?.message || '注册失败，请重试');
            } finally {
                isLoading.value = false;
            }
        };

        return {
            username,
            password,
            confirmPassword,
            avatarPreview,
            fileInput,
            isLoading,
            canRegister,
            handleFileChange,
            triggerFileInput,
            handleRegister
        };
    }
}).mount('#app'); 