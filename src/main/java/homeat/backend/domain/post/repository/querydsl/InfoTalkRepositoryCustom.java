package homeat.backend.domain.post.repository.querydsl;

import homeat.backend.domain.post.entity.InfoTalk;

public interface InfoTalkRepositoryCustom {

    InfoTalk findByInfoTalkId(Long id);

}
