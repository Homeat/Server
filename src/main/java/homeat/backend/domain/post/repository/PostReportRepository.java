package homeat.backend.domain.post.repository;

import homeat.backend.domain.post.entity.PostDetailType;
import homeat.backend.domain.post.entity.PostReport;
import homeat.backend.domain.post.entity.PostType;
import homeat.backend.domain.user.entity.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostReportRepository extends JpaRepository<PostReport, Long> {
    Optional<PostReport> findPostReportByPostTypeAndPostDetailTypeAndMappingIdAndMember(PostType postType,
                                                                                        PostDetailType postDetailType,
                                                                                        Long mappingId, Member member);

    List<PostReport> findPostReportByPostTypeAndPostDetailTypeAndMappingId(PostType postType,
                                                                           PostDetailType postDetailType,
                                                                           Long mappingId);
}
