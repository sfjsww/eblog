package eblog.demo.vo;

import eblog.demo.entity.Comment;
import lombok.Data;

@Data
public class CommentVo extends Comment {
    private Long authorId;
    private String authorName;
    private String authorAvatar;
;
}
