package homeat.backend.domain.mypage.service;

import homeat.backend.domain.address.entity.Address;
import homeat.backend.domain.analyze.entity.FinanceData;
import homeat.backend.domain.homeatreport.entity.Week_Analyze;
import homeat.backend.domain.homeatreport.entity.Week_Check;
import homeat.backend.domain.mypage.dto.MyPageRequest;
import homeat.backend.domain.user.entity.Member;
import homeat.backend.domain.user.entity.MemberInfo;

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

    public static Week_Check toWeekCheck(FinanceData financeData, Long goalPrice) {
        return Week_Check.builder()
                .financeData(financeData)
                .goal_price(goalPrice)
                .next_goal_price(goalPrice)
                .build();
    }

    public static Week_Analyze toWeekAnalyze(FinanceData financeData) {
        return Week_Analyze.builder()
                .financeData(financeData)
                .build();
    }
}
