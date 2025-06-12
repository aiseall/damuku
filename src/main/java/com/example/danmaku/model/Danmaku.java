import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("danmaku")
public class Danmaku {
    @TableId(type = IdType.AUTO)
    private Long id;            // 主键ID
    private String content;     // 弹幕内容
    private String color;       // 颜色（#RRGGBB格式）
    private Integer fontSize;   // 字体大小（px）
    private Double time;        // 视频时间点（秒）
    private String videoId;     // 关联视频ID
    private String userId;      // 发送用户ID
    private String username;    // 发送用户名
    private LocalDateTime createdAt;  // 发送时间

    @TableField(exist = false)  // 非数据库字段
    private String token;       // 前端认证token（用于校验用户）
}
    