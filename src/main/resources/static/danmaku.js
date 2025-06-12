let stompClient = null;
const videoPlayer = document.getElementById('video-player');
const danmakuContainer = document.getElementById('danmaku-container');
const input = document.getElementById('danmaku-input');
const colorPicker = document.getElementById('color-picker');
const sendBtn = document.getElementById('send-btn');

// 初始化WebSocket连接
function connect() {
    const socket = new SockJS('/ws-danmaku');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        // 订阅目标视频的弹幕频道（示例视频ID为"test_video_001"）
        stompClient.subscribe('/topic/video/test_video_001', function (response) {
            const danmaku = JSON.parse(response.body);
            renderDanmaku(danmaku);
        });
        // 加载历史弹幕（前60秒）
        loadHistoryDanmaku('test_video_001', 0, 60);
    });
}

// 渲染弹幕
function renderDanmaku(danmaku) {
    const danmakuDiv = document.createElement('div');
    danmakuDiv.className = 'danmaku-item';
    danmakuDiv.textContent = danmaku.content;
    danmakuDiv.style.color = danmaku.color;
    danmakuDiv.style.fontSize = `${danmaku.fontSize}px`;
    
    // 随机垂直位置（避免重叠）
    const trackHeight = danmaku.fontSize + 10;
    const maxTrack = Math.floor(danmakuContainer.clientHeight / trackHeight);
    const trackIndex = Math.floor(Math.random() * maxTrack);
    danmakuDiv.style.top = `${trackIndex * trackHeight}px`;

    danmakuContainer.appendChild(danmakuDiv);
    // 动画结束后移除元素
    setTimeout(() => danmakuDiv.remove(), 8000);
}

// 加载历史弹幕
function loadHistoryDanmaku(videoId, startTime, endTime) {
    fetch(`/api/danmaku/video/${videoId}?startTime=${startTime}&endTime=${endTime}`)
        .then(res => res.json())
        .then(danmakus => danmakus.forEach(renderDanmaku));
}

// 发送弹幕事件
sendBtn.addEventListener('click', () => {
    const content = input.value.trim();
    if (!content) return;
    
    const danmaku = {
        content: content,
        color: colorPicker.value,
        fontSize: 24,
        time: videoPlayer.currentTime,
        videoId: 'test_video_001',
        userId: 'current_user_id',  // 实际应从登录状态获取
        username: '当前用户'
    };
    
    stompClient.send('/app/danmaku/send', {}, JSON.stringify(danmaku));
    input.value = '';
});

// 视频时间更新事件（用于触发历史弹幕）
videoPlayer.addEventListener('timeupdate', () => {
    const currentTime = videoPlayer.currentTime;
    // 每5秒加载新5秒的历史弹幕（示例逻辑）
    if (Math.floor(currentTime) % 5 === 0) {
        loadHistoryDanmaku('test_video_001', currentTime, currentTime + 5);
    }
});

// 页面加载后连接WebSocket
window.onload = connect;
    