const {createApp, ref, onMounted} = Vue;

createApp({
    setup() {
        const isTyping = ref(false);
        const typingText = ref(null);

        const eraseText = (element, callback) => {
            let text = element.textContent;
            const erasing = () => {
                if (text.length > 0) {
                    text = text.substring(0, text.length - 1);
                    element.textContent = text;
                    setTimeout(erasing, 50);
                } else {
                    callback();
                }
            };
            erasing();
        };

        const startTypingAnimation = () => {
            const text = "欢迎来到XiaoMing的聊天频道";
            const element = typingText.value;
            let index = 0;

            const typeText = () => {
                if (isTyping.value) return;
                isTyping.value = true;

                const typing = () => {
                    if (index < text.length) {
                        element.textContent = text.substring(0, index + 1);
                        index++;
                        setTimeout(typing, 150);
                    } else {
                        setTimeout(() => {
                            eraseText(element, () => {
                                index = 0;
                                isTyping.value = false;
                                typeText();
                            });
                        }, 2000);
                    }
                };

                typing();
            };

            typeText();
        };

        const enterChat = () => {
            const token = localStorage.getItem('token');
            if (token) {
                window.location.href = 'chat.html';
            } else {
                window.location.href = 'login.html';
            }
        };

        onMounted(() => {
            startTypingAnimation();
        });

        return {
            typingText,
            enterChat
        };
    }
}).mount('#app'); 