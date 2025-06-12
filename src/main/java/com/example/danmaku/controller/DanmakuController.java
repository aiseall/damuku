import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/danmaku")
public class DanmakuController {

    @Resource
    private DanmakuService danmakuService;

    // WebSocket消息入口（客户端发送到/app/danmaku/send）
    @MessageMapping("/danmaku/send")
    @SendTo("/topic/video/{videoId}")  // 自动替换路径变量
    public Danmaku sendDanmaku(Danmaku danmaku) {
        return danmakuService.saveAndPushDanmaku(danmaku);
    }

    // 历史弹幕查询接口
    @GetMapping("/video/{videoId}")
    public List<Danmaku> getDanmakusByVideo(
            @PathVariable String videoId,
            @RequestParam(defaultValue = "0") Double startTime,
            @RequestParam(defaultValue = "60") Double endTime) {
        return danmakuService.queryHistoryDanmaku(videoId, startTime, endTime);
    }
}
    