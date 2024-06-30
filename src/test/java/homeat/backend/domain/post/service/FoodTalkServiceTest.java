package homeat.backend.domain.post.service;

import static org.junit.jupiter.api.Assertions.*;
import homeat.backend.domain.post.entity.FoodTalk;
import homeat.backend.domain.post.entity.PostLove;
import homeat.backend.domain.post.entity.PostType;
import homeat.backend.domain.post.entity.Status;
import homeat.backend.domain.post.entity.Tag;
import homeat.backend.domain.post.repository.FoodTalkRepository;
import homeat.backend.domain.post.repository.PostLoveRepository;
import homeat.backend.domain.user.entity.Member;
import homeat.backend.domain.user.repository.MemberRepository;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class FoodTalkServiceTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    FoodTalkRepository foodTalkRepository;

    @Autowired
    PostLoveRepository postLoveRepository;



    @Test
    void reportFoodTalk() throws InterruptedException {
        int numThreads = 10;
        CountDownLatch doneSignal = new CountDownLatch(numThreads);
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        // Mock 데이터 설정
        Member member1 = Member.builder().id(1L).build();
        FoodTalk foodTalk = FoodTalk.builder()
                .id(142L)
                .member(member1)
                .name("냥")
                .memo("냥")
                .tag(Tag.아침)
                .status(Status.저장)
                .build();


        for (long i = 2L; i <= numThreads + 1; i++) {
            long finalI = i;
            executorService.execute(() -> {
                try {
                    Member member = Member.builder().id(finalI).build();
                    memberRepository.save(member);

                    synchronized (foodTalk) {
                        foodTalk.plusReport(foodTalk.getReportNumber() + 1);

                        if (foodTalk.getReportNumber() >= 10) {
                            foodTalk.reported();
                        }
                    }
                    successCount.getAndIncrement();
                } catch (Exception e) {
                    failCount.getAndIncrement();
                } finally {
                    doneSignal.countDown();
                }
            });
        }

        doneSignal.await();
        executorService.shutdown();

        // then
        assertEquals(10, foodTalk.getReportNumber());
        assertEquals(Status.신고, foodTalk.getStatus());
    }
    @Nested
    class 좋아요_기능_테스트 {
        @Nested
        class 성공 {
            @Test
            void 집밥토크에서_유저가_좋아요를_누르면() throws Exception {
                // Given
                Member member = memberRepository.findById(6L).get();

                FoodTalk foodTalk = foodTalkRepository.findById(180L).get();

                // When
                PostLove postLove = PostLove.builder()
                        .postType(PostType.FoodTalk)
                        .mappingId(foodTalk.getId())
                        .member(member)
                        .build();

                foodTalk.plusLove(foodTalk.getLove() + 1);
                foodTalk.setLove(true);

                PostLove save = postLoveRepository.save(postLove);

                // Then
                assertEquals(1, foodTalk.getLove());
                assertEquals(true, foodTalk.getSetLove());
                assertEquals(foodTalk.getId(), save.getMappingId());
                assertEquals(PostType.FoodTalk, save.getPostType());
                assertEquals(member, save.getMember());
            }
            @Test
            public void 집밥토크에서_유저가_좋아요를_취소하면() throws Exception {
                //given
                Member member = memberRepository.findById(6L).get();

                FoodTalk foodTalk = foodTalkRepository.findById(180L).get();

                // When
                PostLove postLove = PostLove.builder()
                        .postType(PostType.FoodTalk)
                        .mappingId(foodTalk.getId())
                        .member(member)
                        .build();

                foodTalk.plusLove(foodTalk.getLove() + 1);
                foodTalk.setLove(true);

                PostLove save = postLoveRepository.save(postLove);

                //when
                PostLove postLove2 = postLoveRepository.findPostLoveByPostTypeAndMember(PostType.FoodTalk, member).orElseThrow();

                foodTalk.setLove(false);
                foodTalk.plusLove(foodTalk.getLove() - 1);

                postLoveRepository.delete(postLove);

                //then
                assertEquals(0, foodTalk.getLove());
                assertEquals(false, foodTalk.getSetLove());
                assertEquals(0, postLoveRepository.count());

            }
        }
    }
}
