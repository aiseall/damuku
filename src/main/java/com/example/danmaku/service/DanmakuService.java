import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DanmakuService extends ServiceImpl<DanmakuMapper, Danmaku> {

    @Resource
    private SimpMessagingTemplate messagingTemplate;  // WebSocket消息模板

    // 保存并推送弹幕
    public Danmaku saveAndPushDanmaku(Danmaku danmaku) {
        // 1. 内容过滤（示例实现）
        danmaku.setContent(filterSensitiveWords(danmaku.getContent()));
        
        // 2. 补充默认值
        danmaku.setCreatedAt(LocalDateTime.now());
        if (danmaku.getFontSize() == null) danmaku.setFontSize(24);
        if (danmaku.getColor() == null) danmaku.setColor("#FFFFFF");

        // 3. 持久化存储
        save(danmaku);

        // 4. 广播消息（/topic/video/{videoId}）
        messagingTemplate.convertAndSend(
            "/topic/video/" + danmaku.getVideoId(), 
            danmaku
        );
        return danmaku;
    }

    // 历史弹幕查询（按视频ID和时间区间）
    public List<Danmaku> queryHistoryDanmaku(String videoId, Double startTime, Double endTime) {
        return list(new LambdaQueryWrapper<Danmaku>()
            .eq(Danmaku::getVideoId, videoId)
            .between(Danmaku::getTime, startTime, endTime)
            .orderByAsc(Danmaku::getTime)
        );
    }

    // 敏感词过滤（示例）
    private String filterSensitiveWords(String content) {
        String[] sensitiveWords = {"违规词", "敏感词", "非法内容"};
        for (String word : sensitiveWords) {
            content = content.replaceAll(word, "***");
        }
        return content;
    }
}
    