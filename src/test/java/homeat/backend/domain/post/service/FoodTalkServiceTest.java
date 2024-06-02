package homeat.backend.domain.post.service;

import static org.junit.jupiter.api.Assertions.*;

import homeat.backend.domain.post.entity.FoodTalk;
import homeat.backend.domain.post.entity.Status;
import homeat.backend.domain.post.entity.Tag;
import homeat.backend.domain.post.repository.FoodTalkRepository;
import homeat.backend.domain.user.entity.Member;
import homeat.backend.domain.user.repository.MemberRepository;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("10명 이상의 회원이 신고 시 신고로 변경되는지에 대한 테스트")
class FoodTalkServiceTest {

    @Mock
    private MemberRepository memberRepository;



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
}
