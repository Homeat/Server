package homeat.backend.domain.mypage.service;

import homeat.backend.domain.address.entity.Address;
import homeat.backend.domain.analyze.entity.FinanceData;
import homeat.backend.domain.homeatreport.controller.HomeatReportErrorStatus;
import homeat.backend.domain.homeatreport.entity.Badge_img;
import homeat.backend.domain.homeatreport.entity.WeekAnalyze;
import homeat.backend.domain.homeatreport.entity.WeekCheck;
import homeat.backend.domain.mypage.dto.MyPageRequest;
import homeat.backend.domain.user.entity.Member;
import homeat.backend.domain.user.entity.MemberInfo;
import homeat.backend.global.exception.GeneralException;

public class MyPageMapper {
    public static MemberInfo toMemberInfo(MyPageRequest.postInfoDto request, Member member, Address address) {
        return MemberInfo.builder()
                .member(member)
                .address(address)
                .gender(request.getGender())
                .birth(request.getBirth())
                .income(request.getIncome())
                .build();
    }

    public static FinanceData toFinanceData(Member member) {
        return FinanceData.builder()
                .member(member)
                .build();
    }

    public static WeekCheck toWeekCheck(FinanceData financeData, Long goalPrice) {
        return WeekCheck.builder()
                .financeData(financeData)
                .goal_price(goalPrice)
                .next_goal_price(goalPrice)
                .badge_img(null)
                .build();
    }

    public static WeekAnalyze toWeekAnalyze(FinanceData financeData, Integer currentWeekIdx) {
        return WeekAnalyze.builder()
                .financeData(financeData)
                .weekIdx(currentWeekIdx)
                .build();
    }
}
